package com.dungeonboard.service;

import com.dungeonboard.dto.GameEvent;
import com.dungeonboard.handler.GameWebSocketHandler;
import com.dungeonboard.model.GameState;
import com.dungeonboard.model.Player;
import com.dungeonboard.model.board.Tile;
import com.dungeonboard.model.board.TileType;
import com.dungeonboard.model.event.EventCardType;
import com.dungeonboard.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class GameService {

    private static final Logger logger = LoggerFactory.getLogger(GameService.class);

    @Autowired
    private RoomService roomService;

    @Autowired
    private DiceService diceService;

    @Autowired
    private CombatService combatService;

    @Autowired
    private CharacterService characterService;

    @Autowired
    private BotAIService botAIService;

    @Autowired
    private GameWebSocketHandler webSocketHandler;

    private final Random random = new Random();

    public GameEvent rollDice(String roomId, String playerId) {
        GameState gameState = roomService.getGameState(roomId);
        if (gameState == null) {
            return GameEvent.error("Game not found");
        }

        Player currentPlayer = gameState.getCurrentPlayer();
        if (currentPlayer == null) {
            return GameEvent.error("No current player");
        }

        // Verify it's the current player's turn
        if (!currentPlayer.getId().toString().equals(playerId)) {
            return GameEvent.error("Not your turn");
        }

        // Roll the dice
        int roll = diceService.rollDice();
        gameState.setLastDiceRoll(roll);
        gameState.setWaitingForRoll(false);

        // Apply racial bonus to roll (e.g., Human +1 to all rolls)
        if (currentPlayer.getCharacter() != null) {
            roll += currentPlayer.getCharacter().getRace().getRollBonus();
        }

        gameState.addLog(currentPlayer.getUsername() + " rolled a " + roll + "!");

        // Apply Elf movement bonus on 5-6
        if (currentPlayer.getCharacter() != null &&
            currentPlayer.getCharacter().getRace() == com.dungeonboard.model.character.Race.ELF &&
            roll >= 5) {
            roll += currentPlayer.getCharacter().getRace().getMovementBonus();
            gameState.addLog(currentPlayer.getUsername() + " (Elf) moves +1 tile!");
        }

        // Move player
        movePlayer(gameState, currentPlayer, roll);

        // Broadcast game state
        webSocketHandler.sendToRoom(roomId, GameEvent.gameStateUpdate(gameState));

        // Handle tile event
        GameEvent tileEvent = handleTileEvent(roomId, gameState, currentPlayer);
        if (tileEvent != null) {
            webSocketHandler.sendToRoom(roomId, tileEvent);
        }

        // Check if game is finished
        if (gameState.isGameFinished()) {
            roomService.endGame(roomId);
            gameState.calculateFinalScores();
            webSocketHandler.sendToRoom(roomId, GameEvent.gameOver(gameState.getWinnerId()));
        } else {
            // Move to next player
            gameState.nextPlayer();
            webSocketHandler.sendToRoom(roomId, GameEvent.gameStateUpdate(gameState));

            // If next player is a bot, trigger bot action
            Player nextPlayer = gameState.getCurrentPlayer();
            if (nextPlayer != null && nextPlayer.isBot()) {
                gameState.setWaitingForRoll(true);
                // Schedule bot action after a delay
                scheduleBotAction(roomId, nextPlayer);
            } else {
                gameState.setWaitingForRoll(true);
            }
        }

        return GameEvent.diceRolled(roll);
    }

    public GameEvent reroll(String roomId, String playerId) {
        GameState gameState = roomService.getGameState(roomId);
        if (gameState == null) {
            return GameEvent.error("Game not found");
        }

        Player currentPlayer = gameState.getCurrentPlayer();
        if (currentPlayer == null || !currentPlayer.getId().toString().equals(playerId)) {
            return GameEvent.error("Not your turn");
        }

        if (currentPlayer.getCharacter() == null || !currentPlayer.getCharacter().canUseReroll()) {
            return GameEvent.error("Cannot reroll");
        }

        currentPlayer.getCharacter().useReroll();

        // Roll again
        int newRoll = diceService.rollDice();
        gameState.setLastDiceRoll(newRoll);
        gameState.addLog(currentPlayer.getUsername() + " rerolled and got " + newRoll + "!");

        // Move with new roll
        movePlayer(gameState, currentPlayer, newRoll);

        webSocketHandler.sendToRoom(roomId, GameEvent.gameStateUpdate(gameState));

        // Handle tile event
        GameEvent tileEvent = handleTileEvent(roomId, gameState, currentPlayer);
        if (tileEvent != null) {
            webSocketHandler.sendToRoom(roomId, tileEvent);
        }

        return GameEvent.diceRolled(newRoll);
    }

    private void movePlayer(GameState gameState, Player player, int tiles) {
        int boardSize = gameState.getBoard().getSize();
        int newPosition = (player.getPosition() + tiles) % boardSize;
        player.setPosition(newPosition);
        gameState.addLog(player.getUsername() + " moved to position " + newPosition);
    }

    private GameEvent handleTileEvent(String roomId, GameState gameState, Player player) {
        Tile tile = gameState.getBoard().getTile(player.getPosition());
        TileType tileType = tile.getType();

        gameState.setLastEvent("Landed on " + tileType.getName());

        return switch (tileType) {
            case MONSTER -> handleMonsterTile(roomId, gameState, player, tile);
            case TREASURE -> handleTreasureTile(gameState, player, tile);
            case TRAP -> handleTrapTile(gameState, player, tile);
            case PORTAL -> handlePortalTile(gameState, player);
            case EVENT -> handleEventTile(gameState, player);
            case SHOP -> GameEvent.eventCard("Welcome to the shop!");
            case START, NORMAL -> null;
        };
    }

    private GameEvent handleMonsterTile(String roomId, GameState gameState, Player player, Tile tile) {
        int monsterLevel = tile.getMonsterLevel();
        boolean victory = combatService.resolveCombat(player, monsterLevel);

        if (victory) {
            player.incrementMonstersDefeated();
            int goldReward = 5 * monsterLevel + diceService.rollDice();
            player.addGold(goldReward);
            gameState.addLog(player.getUsername() + " defeated a level " + monsterLevel + " monster and found " + goldReward + " gold!");
            return GameEvent.combatResult("Victory! Found " + goldReward + " gold", true);
        } else {
            int damage = monsterLevel * 3;
            player.getCharacter().takeDamage(damage);
            gameState.addLog(player.getUsername() + " was defeated by a level " + monsterLevel + " monster and took " + damage + " damage!");
            return GameEvent.combatResult("Defeat! Took " + damage + " damage", false);
        }
    }

    private GameEvent handleTreasureTile(GameState gameState, Player player, Tile tile) {
        int goldAmount = tile.getTreasureAmount();

        // Apply Dwarf racial bonus
        if (player.getCharacter() != null &&
            player.getCharacter().getRace() == com.dungeonboard.model.character.Race.DWARF) {
            goldAmount += player.getCharacter().getRace().getGoldBonus();
        }

        player.addGold(goldAmount);
        gameState.addLog(player.getUsername() + " found a treasure with " + goldAmount + " gold!");
        return GameEvent.treasureFound(goldAmount);
    }

    private GameEvent handleTrapTile(GameState gameState, Player player, Tile tile) {
        // Check for Goblin trap immunity
        if (player.getCharacter() != null &&
            player.getCharacter().getRace().getRace() == com.dungeonboard.model.character.Race.GOBLIN &&
            player.getCharacter().getRace().isTrapImmune()) {
            gameState.addLog(player.getUsername() + " (Goblin) is immune to traps!");
            return GameEvent.eventCard("Trap immunity activated!");
        }

        int damage = tile.getTrapDamage();
        player.getCharacter().takeDamage(damage);
        gameState.addLog(player.getUsername() + " triggered a trap and took " + damage + " damage!");
        return GameEvent.trapTriggered(damage);
    }

    private GameEvent handlePortalTile(GameState gameState, Player player) {
        int newPosition = random.nextInt(gameState.getBoard().getSize());
        player.setPosition(newPosition);
        gameState.addLog(player.getUsername() + " was teleported to position " + newPosition + "!");
        return GameEvent.eventCard("Teleported to position " + newPosition);
    }

    private GameEvent handleEventTile(GameState gameState, Player player) {
        EventCardType eventCard = drawRandomEventCard();
        gameState.addLog("Event card drawn: " + eventCard.getName());

        return switch (eventCard) {
            // Bless cards
            case BLESS_GOLD -> {
                gameState.addLog(player.getUsername() + " received Gold Rush blessing!");
                yield GameEvent.eventCard("Gold Rush: Double gold from next treasure!");
            }
            case BLESS_MOVE -> {
                gameState.addLog(player.getUsername() + " received Swift Feet blessing!");
                yield GameEvent.eventCard("Swift Feet: +3 movement next turn!");
            }
            case BLESS_SHIELD -> {
                gameState.addLog(player.getUsername() + " received Divine Shield blessing!");
                yield GameEvent.eventCard("Divine Shield: Immune to next attack!");
            }
            case BLESS_HEAL -> {
                if (player.getCharacter() != null) {
                    player.getCharacter().heal(20);
                    gameState.addLog(player.getUsername() + " healed for 20 HP!");
                }
                yield GameEvent.eventCard("Healing Light: Restored 20 HP!");
            }
            case BLESS_REROLL -> {
                gameState.addLog(player.getUsername() + " received Lucky Star blessing!");
                yield GameEvent.eventCard("Lucky Star: Reroll next dice roll!");
            }
            case BLESS_STEAL -> {
                Player target = findRandomPlayer(gameState, player);
                if (target != null) {
                    int stolen = Math.min(10, target.getGold());
                    target.removeGold(stolen);
                    player.addGold(stolen);
                    gameState.addLog(player.getUsername() + " stole " + stolen + " gold from " + target.getUsername() + "!");
                }
                yield GameEvent.eventCard("Pickpocket: Stole 10 gold!");
            }

            // Curse cards
            case CURSE_LOSE_TURN -> {
                player.skipTurn();
                gameState.addLog(player.getUsername() + " is stunned and will skip next turn!");
                yield GameEvent.eventCard("Stunned: Skip next turn!");
            }
            case CURSE_DROP_GOLD -> {
                int dropped = Math.min(15, player.getGold());
                player.removeGold(dropped);
                gameState.addLog(player.getUsername() + " dropped " + dropped + " gold!");
                yield GameEvent.eventCard("Greedy Ghost: Dropped " + dropped + " gold!");
            }
            case CURSE_SWAP -> {
                Player target = findRandomPlayer(gameState, player);
                if (target != null) {
                    int tempPos = player.getPosition();
                    player.setPosition(target.getPosition());
                    target.setPosition(tempPos);
                    gameState.addLog(player.getUsername() + " swapped positions with " + target.getUsername() + "!");
                }
                yield GameEvent.eventCard("Confusion: Swapped positions!");
            }
            case CURSE_DAMAGE -> {
                if (player.getCharacter() != null) {
                    player.getCharacter().takeDamage(10);
                    gameState.addLog(player.getUsername() + " took 10 poison damage!");
                }
                yield GameEvent.eventCard("Poison: Took 10 damage!");
            }
            case CURSE_TELEPORT -> {
                int newPosition = random.nextInt(gameState.getBoard().getSize());
                player.setPosition(newPosition);
                gameState.addLog(player.getUsername() + " was warped to position " + newPosition + "!");
                yield GameEvent.eventCard("Warp: Teleported randomly!");
            }
            case CURSE_MOVE_BACK -> {
                int boardSize = gameState.getBoard().getSize();
                int newPosition = ((player.getPosition() - 3) % boardSize + boardSize) % boardSize;
                player.setPosition(newPosition);
                gameState.addLog(player.getUsername() + " stumbled back to position " + newPosition + "!");
                yield GameEvent.eventCard("Stumble: Moved back 3 tiles!");
            }
        };
    }

    private EventCardType drawRandomEventCard() {
        EventCardType[] cards = EventCardType.values();
        return cards[random.nextInt(cards.length)];
    }

    private Player findRandomPlayer(GameState gameState, Player excludePlayer) {
        List<Player> otherPlayers = gameState.getPlayers().stream()
                .filter(p -> p != excludePlayer && p.isAlive())
                .toList();

        if (otherPlayers.isEmpty()) {
            return null;
        }

        return otherPlayers.get(random.nextInt(otherPlayers.size()));
    }

    private void scheduleBotAction(String roomId, Player botPlayer) {
        // Run bot action after a delay (simulated)
        new Thread(() -> {
            try {
                Thread.sleep(1500); // 1.5 second delay for realism
                botAIService.executeBotTurn(roomId, botPlayer.getId().toString());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public void selectCharacter(String roomId, String playerId, int classId, int raceId) {
        GameState gameState = roomService.getGameState(roomId);
        if (gameState == null) {
            throw new RuntimeException("Game not found");
        }

        characterService.selectCharacter(gameState, playerId, classId, raceId);
        webSocketHandler.sendToRoom(roomId, GameEvent.gameStateUpdate(gameState));
    }

    public void useAbility(String roomId, String playerId) {
        GameState gameState = roomService.getGameState(roomId);
        if (gameState == null) {
            throw new RuntimeException("Game not found");
        }

        characterService.useCharacterAbility(gameState, playerId);
        webSocketHandler.sendToRoom(roomId, GameEvent.gameStateUpdate(gameState));
    }
}
