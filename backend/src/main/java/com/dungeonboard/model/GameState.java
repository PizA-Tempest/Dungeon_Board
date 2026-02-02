package com.dungeonboard.model;

import com.dungeonboard.model.board.Board;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class GameState {
    private String roomId;
    private GameStatus status;
    private Board board;
    private List<Player> players;
    private int currentPlayerIndex;
    private int currentRound;
    private int maxRounds;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String winnerId;
    private List<String> gameLog;
    private String lastEvent;
    private boolean waitingForRoll;
    private boolean waitingForReroll;
    private int lastDiceRoll;

    public enum GameStatus {
        WAITING, IN_PROGRESS, FINISHED
    }

    public GameState(String roomId, int boardSize) {
        this.roomId = roomId;
        this.status = GameStatus.WAITING;
        this.board = new Board(boardSize);
        this.players = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.currentRound = 0;
        this.maxRounds = 10;
        this.gameLog = new ArrayList<>();
        this.waitingForRoll = false;
        this.waitingForReroll = false;
        this.lastDiceRoll = 0;
    }

    public Player getCurrentPlayer() {
        if (players.isEmpty()) return null;
        return players.get(currentPlayerIndex % players.size());
    }

    public void nextPlayer() {
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

            // Check if we've completed a round
            if (currentPlayerIndex == 0) {
                currentRound++;
            }
        } while (getCurrentPlayer().shouldSkipTurn());

        // Reset skip turns for the current player
        getCurrentPlayer().decrementSkipTurns();
    }

    public boolean isGameFinished() {
        return currentRound >= maxRounds || players.stream().filter(Player::isAlive).count() <= 1;
    }

    public void addLog(String message) {
        gameLog.add(message);
        if (gameLog.size() > 100) {
            gameLog.remove(0); // Keep only last 100 messages
        }
    }

    public void calculateFinalScores() {
        players.forEach(Player::calculateScore);

        // Determine winner
        Player winner = players.stream()
                .max((p1, p2) -> Integer.compare(p1.getScore(), p2.getScore()))
                .orElse(null);

        if (winner != null) {
            this.winnerId = winner.getId().toString();
        }
    }

    public boolean isWaitingForRoll() {
        return waitingForRoll;
    }

    public void setWaitingForRoll(boolean waitingForRoll) {
        this.waitingForRoll = waitingForRoll;
        if (waitingForRoll) {
            this.waitingForReroll = false;
        }
    }

    public void setWaitingForReroll(boolean waitingForReroll) {
        this.waitingForReroll = waitingForReroll;
        if (waitingForReroll) {
            this.waitingForRoll = false;
        }
    }
}
