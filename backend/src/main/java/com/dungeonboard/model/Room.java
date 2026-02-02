package com.dungeonboard.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Room {
    private String roomId;
    private String name;
    private String hostId;
    private int maxPlayers;
    private boolean isPrivate;
    private RoomStatus status;
    private List<Player> players;
    private int currentRound;
    private int maxRounds;
    private int currentPlayerIndex;
    private long createdAt;

    public enum RoomStatus {
        WAITING, IN_PROGRESS, FINISHED
    }

    public Room(String roomId, String name, String hostId, int maxPlayers, boolean isPrivate) {
        this.roomId = roomId;
        this.name = name;
        this.hostId = hostId;
        this.maxPlayers = maxPlayers;
        this.isPrivate = isPrivate;
        this.status = RoomStatus.WAITING;
        this.players = new ArrayList<>();
        this.currentRound = 0;
        this.maxRounds = 10;
        this.currentPlayerIndex = 0;
        this.createdAt = System.currentTimeMillis();
    }

    public boolean isFull() {
        return players.size() >= maxPlayers;
    }

    public boolean isEmpty() {
        return players.isEmpty();
    }

    public void addPlayer(Player player) {
        if (!isFull()) {
            players.add(player);
        }
    }

    public void removePlayer(String playerId) {
        players.removeIf(p -> p.getId().toString().equals(playerId));
    }

    public Player getCurrentPlayer() {
        if (players.isEmpty()) return null;
        return players.get(currentPlayerIndex % players.size());
    }

    public void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

        // Check if we've completed a round
        if (currentPlayerIndex == 0) {
            currentRound++;
        }
    }

    public Player getPlayerById(String playerId) {
        return players.stream()
                .filter(p -> p.getId().toString().equals(playerId))
                .findFirst()
                .orElse(null);
    }

    public int getPlayerCount() {
        return players.size();
    }

    public boolean isHost(String playerId) {
        return hostId.equals(playerId);
    }
}
