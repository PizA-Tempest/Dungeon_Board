package com.dungeonboard.service;

import com.dungeonboard.dto.GameEvent;
import com.dungeonboard.handler.GameWebSocketHandler;
import com.dungeonboard.model.GameState;
import com.dungeonboard.model.Player;
import com.dungeonboard.model.board.Tile;
import com.dungeonboard.model.board.TileType;
import com.dungeonboard.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

@Service
public class BotAIService {

    private static final Logger logger = LoggerFactory.getLogger(BotAIService.class);

    @Autowired
    private RoomService roomService;

    @Autowired
    private GameService gameService;

    @Autowired
    private DiceService diceService;

    @Autowired
    private CharacterService characterService;

    @Autowired
    private GameWebSocketHandler webSocketHandler;

    private final Random random = new Random();

    /**
     * Execute a bot's turn using AI decision-making
     */
    public void executeBotTurn(String roomId, String botId) {
        try {
            GameState gameState = roomService.getGameState(roomId);
            if (gameState == null) {
                logger.warn("Game state not found for room: {}", roomId);
                return;
            }

            Player bot = gameState.getPlayers().stream()
                    .filter(p -> p.getId() != null && p.getId().toString().equals(botId))
                    .findFirst()
                    .orElse(null);

            if (bot == null || !bot.isBot()) {
                logger.warn("Bot not found: {}", botId);
                return;
            }

            // AI Decision Making
            makeStrategicDecisions(gameState, bot);

            // Roll dice (main action)
            GameEvent rollResult = gameService.rollDice(roomId, botId);
            logger.info("Bot {} rolled dice", bot.getUsername());

        } catch (Exception e) {
            logger.error("Error executing bot turn", e);
        }
    }

    /**
     * Make strategic decisions before rolling
     * Evaluates game state and decides whether to use abilities
     */
    private void makeStrategicDecisions(GameState gameState, Player bot) {
        // Decision 1: Should I use an ability?
        if (shouldUseAbility(gameState, bot)) {
            try {
                characterService.useCharacterAbility(gameState, bot.getId().toString());
                gameState.addLog(bot.getUsername() + " (Bot) used their class ability!");
            } catch (Exception e) {
                logger.debug("Bot could not use ability: {}", e.getMessage());
            }
        }

        // Decision 2: Strategic considerations based on board position
        evaluateBoardPosition(gameState, bot);
    }

    /**
     * Decide whether to use class ability based on game state
     */
    private boolean shouldUseAbility(GameState gameState, Player bot) {
        if (bot.getCharacter() == null) return false;

        // Don't use ability if already used this turn
        if (bot.getCharacter().isAbilityUsedThisTurn()) return false;

        // Decision matrix for each class
        return switch (bot.getCharacter().getPlayerClass()) {
            // Offensive abilities: Use when HP is high
            case WARRIOR, PALADIN, NECROMANCER -> {
                double hpPercent = (double) bot.getCharacter().getCurrentHp() / bot.getCharacter().getMaxHp();
                yield hpPercent > 0.5 && random.nextDouble() < 0.6;
            }

            // Healing abilities: Use when HP is low
            case CLERIC -> {
                double hpPercent = (double) bot.getCharacter().getCurrentHp() / bot.getCharacter().getMaxHp();
                yield hpPercent < 0.6;
            }

            // Utility abilities: Use strategically
            case MAGE, RANGER, BARD -> random.nextDouble() < 0.4;

            // Stealing ability: Use when behind on gold
            case ROGUE -> {
                boolean isBehind = gameState.getPlayers().stream()
                        .anyMatch(p -> p.getGold() > bot.getGold() + 15);
                yield isBehind;
            }
        };
    }

    /**
     * Evaluate current board position and make strategic adjustments
     */
    private void evaluateBoardPosition(GameState gameState, Player bot) {
        int currentPosition = bot.getPosition();
        Tile currentTile = gameState.getBoard().getTile(currentPosition);

        // Look ahead at next 6 tiles (max dice roll)
        int bestTileScore = Integer.MIN_VALUE;
        int targetPosition = currentPosition;

        for (int roll = 1; roll <= 6; roll++) {
            int futurePos = (currentPosition + roll) % gameState.getBoard().getSize();
            Tile futureTile = gameState.getBoard().getTile(futurePos);
            int tileScore = evaluateTileScore(gameState, bot, futureTile);

            if (tileScore > bestTileScore) {
                bestTileScore = tileScore;
                targetPosition = futurePos;
            }
        }

        // Log strategic thought process
        if (bestTileScore > 0) {
            gameState.addLog(bot.getUsername() + " (Bot) is aiming for position " + targetPosition);
        }
    }

    /**
     * Evaluate how desirable a tile is for this bot
     * Higher score = more desirable
     */
    private int evaluateTileScore(GameState gameState, Player bot, Tile tile) {
        int score = 0;

        // Base tile type scores
        score += switch (tile.getType()) {
            case TREASURE -> 20; // Love gold!
            case MONSTER -> {
                // Weigh monster difficulty against bot's strength
                int monsterLevel = tile.getMonsterLevel();
                int botStrength = bot.getCharacter() != null ? bot.getCharacter().getTotalAttack() : 10;
                if (botStrength >= monsterLevel * 3) {
                    yield 15; // Can win easily
                } else {
                    yield -10; // Too dangerous
                }
            }
            case SHOP -> 10; // Useful for upgrades
            case EVENT -> 5; // Risk/reward
            case NORMAL -> 0; // Neutral
            case TRAP -> -15; // Avoid traps!
            case PORTAL -> -5; // Unpredictable
            case START -> 0;
        };

        // Adjust based on bot's current state
        if (bot.getCharacter() != null) {
            double hpPercent = (double) bot.getCharacter().getCurrentHp() / bot.getCharacter().getMaxHp();

            // Low HP bots are more cautious
            if (hpPercent < 0.3) {
                if (tile.getType() == TileType.MONSTER) score -= 20;
                if (tile.getType() == TileType.TRAP) score -= 10;
            }

            // High HP bots are more aggressive
            if (hpPercent > 0.7) {
                if (tile.getType() == TileType.MONSTER) score += 10;
            }

            // Low gold bots seek treasure
            if (bot.getGold() < 20) {
                if (tile.getType() == TileType.TREASURE) score += 15;
            }

            // Race-specific preferences
            if (bot.getCharacter().getRace() == com.dungeonboard.model.character.Race.GOBLIN) {
                // Goblins aren't worried about traps
                if (tile.getType() == TileType.TRAP) score += 20;
            }

            if (bot.getCharacter().getRace() == com.dungeonboard.model.character.Race.DWARF) {
                // Dwarves love treasure
                if (tile.getType() == TileType.TREASURE) score += 10;
            }
        }

        // Consider competitive position
        int maxGold = gameState.getPlayers().stream()
                .mapToInt(Player::getGold)
                .max()
                .orElse(0);

        // If behind, take more risks
        if (bot.getGold() < maxGold - 20) {
            if (tile.getType() == TileType.MONSTER || tile.getType() == TileType.EVENT) {
                score += 8;
            }
        }

        // If ahead, play safely
        if (bot.getGold() > maxGold) {
            if (tile.getType() == TileType.MONSTER) score -= 5;
            if (tile.getType() == TileType.TREASURE) score += 5;
        }

        return score;
    }

    /**
     * Create a bot with a strategic class/race combination
     */
    public void assignOptimalCharacter(Player bot) {
        // Assign random but strategic combinations
        com.dungeonboard.model.character.PlayerClass[] classes =
                com.dungeonboard.model.character.PlayerClass.values();
        com.dungeonboard.model.character.Race[] races =
                com.dungeonboard.model.character.Race.values();

        // Bias towards certain combinations based on bot number
        int botIndex = (int) (Math.random() * classes.length);

        com.dungeonboard.model.character.PlayerClass chosenClass;
        com.dungeonboard.model.character.Race chosenRace;

        // Strategic combinations
        if (botIndex % 3 == 0) {
            // Aggressive bot: Warrior/Orc or Paladin/Dragonborn
            chosenClass = random.nextDouble() < 0.5 ?
                    com.dungeonboard.model.character.PlayerClass.WARRIOR :
                    com.dungeonboard.model.character.PlayerClass.PALADIN;
            chosenRace = random.nextDouble() < 0.5 ?
                    com.dungeonboard.model.character.Race.ORC :
                    com.dungeonboard.model.character.Race.DRAGONBORN;
        } else if (botIndex % 3 == 1) {
            // Strategic bot: Mage/Human or Ranger/Elf
            chosenClass = random.nextDouble() < 0.5 ?
                    com.dungeonboard.model.character.PlayerClass.MAGE :
                    com.dungeonboard.model.character.PlayerClass.RANGER;
            chosenRace = random.nextDouble() < 0.5 ?
                    com.dungeonboard.model.character.Race.HUMAN :
                    com.dungeonboard.model.character.Race.ELF;
        } else {
            // Chaos bot: Rogue/Tiefling or Bard/Halfling
            chosenClass = random.nextDouble() < 0.5 ?
                    com.dungeonboard.model.character.PlayerClass.ROGUE :
                    com.dungeonboard.model.character.PlayerClass.BARD;
            chosenRace = random.nextDouble() < 0.5 ?
                    com.dungeonboard.model.character.Race.TIEFLING :
                    com.dungeonboard.model.character.Race.HALFLING;
        }

        bot.setCharacter(chosenClass, chosenRace);
    }

    /**
     * Decide whether to reroll (for Halfling bots)
     */
    public boolean shouldReroll(GameState gameState, Player bot, int currentRoll) {
        if (bot.getCharacter() == null) return false;
        if (!bot.getCharacter().canUseReroll()) return false;

        // Look at what the current roll would lead to
        int currentPosition = bot.getPosition();
        int futurePos = (currentPosition + currentRoll) % gameState.getBoard().getSize();
        Tile futureTile = gameState.getBoard().getTile(futurePos);

        // Reroll if landing on something bad
        if (futureTile.getType() == TileType.TRAP) return true;
        if (futureTile.getType() == TileType.MONSTER &&
            bot.getCharacter().getCurrentHp() < bot.getCharacter().getMaxHp() * 0.4) return true;

        // Don't reroll if landing on something good
        if (futureTile.getType() == TileType.TREASURE) return false;
        if (futureTile.getType() == TileType.SHOP) return false;

        // Otherwise, reroll if current roll is low (1-2)
        return currentRoll <= 2;
    }
}
