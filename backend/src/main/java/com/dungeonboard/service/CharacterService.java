package com.dungeonboard.service;

import com.dungeonboard.dto.CharacterSelectionRequest;
import com.dungeonboard.model.GameState;
import com.dungeonboard.model.Player;
import com.dungeonboard.model.character.Character;
import com.dungeonboard.model.character.PlayerClass;
import com.dungeonboard.model.character.Race;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CharacterService {

    public List<PlayerClass> getAllClasses() {
        return Arrays.asList(PlayerClass.values());
    }

    public List<Race> getAllRaces() {
        return Arrays.asList(Race.values());
    }

    public Character createCharacter(PlayerClass playerClass, Race race) {
        return new Character(playerClass, race);
    }

    public void selectCharacter(GameState gameState, String playerId, int classId, int raceId) {
        Player player = gameState.getPlayers().stream()
                .filter(p -> p.getId().toString().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Player not found"));

        PlayerClass playerClass = Arrays.stream(PlayerClass.values())
                .filter(c -> c.getId() == classId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid class ID"));

        Race race = Arrays.stream(Race.values())
                .filter(r -> r.getId() == raceId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid race ID"));

        player.setCharacter(playerClass, race);
    }

    public void useCharacterAbility(GameState gameState, String playerId) {
        Player player = gameState.getPlayers().stream()
                .filter(p -> p.getId().toString().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Player not found"));

        Character character = player.getCharacter();
        if (character == null) {
            throw new RuntimeException("Character not set");
        }

        // Implement class-specific abilities
        switch (character.getPlayerClass()) {
            case WARRIOR -> {
                if (!character.isAbilityUsedThisMatch()) {
                    // Shield ability - temporary defense boost
                    character.setAbilityUsedThisMatch(true);
                    gameState.addLog(player.getUsername() + " used Warrior's Shield!");
                }
            }
            case MAGE -> {
                // Fireball - deal damage to monsters in next 3 tiles
                gameState.addLog(player.getUsername() + " cast Fireball!");
            }
            case ROGUE -> {
                // Steal gold from player on same tile
                Player target = findPlayerOnSameTile(gameState, player);
                if (target != null && target != player) {
                    int stolenGold = Math.min(10, target.getGold());
                    target.removeGold(stolenGold);
                    player.addGold(stolenGold);
                    gameState.addLog(player.getUsername() + " stole " + stolenGold + " gold from " + target.getUsername() + "!");
                }
            }
            case CLERIC -> {
                // Heal self
                character.heal(20);
                gameState.addLog(player.getUsername() + " healed for 20 HP!");
            }
            case RANGER -> {
                // Passive ability - critical hit on 6 roll
                gameState.addLog(player.getUsername() + " aims carefully!");
            }
            case PALADIN -> {
                if (!character.isAbilityUsedThisMatch()) {
                    // Smite - bonus damage against monsters
                    character.setAbilityUsedThisMatch(true);
                    gameState.addLog(player.getUsername() + " used Paladin's Smite!");
                }
            }
            case BARD -> {
                // Buff all players
                gameState.getPlayers().forEach(p -> {
                    if (p.getCharacter() != null) {
                        p.getCharacter().heal(5);
                    }
                });
                gameState.addLog(player.getUsername() + " played an inspiring song!");
            }
            case NECROMANCER -> {
                // Drain life from monsters
                gameState.addLog(player.getUsername() + " used Life Drain!");
            }
        }
    }

    private Player findPlayerOnSameTile(GameState gameState, Player currentPlayer) {
        return gameState.getPlayers().stream()
                .filter(p -> p.getPosition() == currentPlayer.getPosition() && p != currentPlayer)
                .findFirst()
                .orElse(null);
    }

    public boolean canUseAbility(Player player) {
        if (player.getCharacter() == null) return false;
        return !player.getCharacter().isAbilityUsedThisTurn();
    }
}
