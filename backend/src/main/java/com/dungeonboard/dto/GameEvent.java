package com.dungeonboard.dto;

import com.dungeonboard.model.GameState;
import com.dungeonboard.model.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameEvent {
    private String type;
    private Object data;
    private String message;
    private GameState gameState;

    public static GameEvent gameStateUpdate(GameState gameState) {
        return new GameEvent("GAME_STATE", null, null, gameState);
    }

    public static GameEvent playerJoined(Player player) {
        return new GameEvent("PLAYER_JOINED", player, player.getUsername() + " joined the game", null);
    }

    public static GameEvent playerLeft(Player player) {
        return new GameEvent("PLAYER_LEFT", player, player.getUsername() + " left the game", null);
    }

    public static GameEvent gameStarted() {
        return new GameEvent("GAME_STARTED", null, "Game started!", null);
    }

    public static GameEvent diceRolled(int roll) {
        return new GameEvent("DICE_ROLLED", roll, "Dice rolled: " + roll, null);
    }

    public static GameEvent playerMoved(String username, int newPosition) {
        return new GameEvent("PLAYER_MOVED", null, username + " moved to position " + newPosition, null);
    }

    public static GameEvent combatResult(String message, boolean victory) {
        return new GameEvent("COMBAT_RESULT", victory, message, null);
    }

    public static GameEvent treasureFound(int amount) {
        return new GameEvent("TREASURE_FOUND", amount, "Found " + amount + " gold!", null);
    }

    public static GameEvent trapTriggered(int damage) {
        return new GameEvent("TRAP_TRIGGERED", damage, "Trap! Took " + damage + " damage", null);
    }

    public static GameEvent eventCard(String message) {
        return new GameEvent("EVENT_CARD", null, message, null);
    }

    public static GameEvent gameOver(String winner) {
        return new GameEvent("GAME_OVER", winner, "Game Over! Winner: " + winner, null);
    }

    public static GameEvent error(String message) {
        return new GameEvent("ERROR", null, message, null);
    }
}
