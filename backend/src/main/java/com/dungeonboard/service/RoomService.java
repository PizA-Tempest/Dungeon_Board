package com.dungeonboard.service;

import com.dungeonboard.dto.CreateRoomRequest;
import com.dungeonboard.model.GameState;
import com.dungeonboard.model.Player;
import com.dungeonboard.model.Room;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomService {

    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final Map<String, GameState> gameStates = new ConcurrentHashMap<>();

    public RoomService() {
    }

    public Room createRoom(CreateRoomRequest request, String hostId, String hostUsername) {
        String roomId = generateRoomId();

        Room room = new Room(roomId, request.getName(), hostId, request.getMaxPlayers(), request.isPrivate());
        room.setStatus(Room.RoomStatus.WAITING);

        // Add host as first player
        Player host = new Player(Long.parseLong(hostId), hostUsername, false);
        room.addPlayer(host);

        // Add bots if requested
        for (int i = 0; i < request.getBotCount(); i++) {
            Player bot = new Player(null, "Bot " + (i + 1), true);
            room.addPlayer(bot);
        }

        rooms.put(roomId, room);

        // Initialize game state
        GameState gameState = new GameState(roomId, 24);
        gameState.setPlayers(room.getPlayers());
        gameStates.put(roomId, gameState);

        return room;
    }

    public Room joinRoom(String roomId, String playerId, String username) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new RuntimeException("Room not found");
        }

        if (room.isFull()) {
            throw new RuntimeException("Room is full");
        }

        if (room.getStatus() != Room.RoomStatus.WAITING) {
            throw new RuntimeException("Game already in progress");
        }

        Player player = new Player(Long.parseLong(playerId), username, false);
        room.addPlayer(player);

        // Update game state
        GameState gameState = gameStates.get(roomId);
        gameState.setPlayers(room.getPlayers());

        return room;
    }

    public Room leaveRoom(String roomId, String playerId) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new RuntimeException("Room not found");
        }

        room.removePlayer(playerId);

        // If host left and there are other players, transfer host
        if (room.getHostId().equals(playerId) && !room.getPlayers().isEmpty()) {
            room.setHostId(room.getPlayers().get(0).getId().toString());
        }

        // If room is empty, remove it
        if (room.isEmpty()) {
            rooms.remove(roomId);
            gameStates.remove(roomId);
        }

        return room;
    }

    public Room getRoom(String roomId) {
        return rooms.get(roomId);
    }

    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }

    public List<Room> getAvailableRooms() {
        return rooms.values().stream()
                .filter(room -> !room.isPrivate() &&
                        room.getStatus() == Room.RoomStatus.WAITING &&
                        !room.isFull())
                .toList();
    }

    public GameState getGameState(String roomId) {
        return gameStates.get(roomId);
    }

    public GameState startGame(String roomId) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new RuntimeException("Room not found");
        }

        if (room.getStatus() != Room.RoomStatus.WAITING) {
            throw new RuntimeException("Game already started");
        }

        // Check if all players have selected characters
        for (Player player : room.getPlayers()) {
            if (player.getCharacter() == null && !player.isBot()) {
                throw new RuntimeException("Not all players have selected characters");
            }
        }

        // Assign random characters to bots
        for (Player player : room.getPlayers()) {
            if (player.isBot() && player.getCharacter() == null) {
                assignRandomCharacter(player);
            }
        }

        room.setStatus(Room.RoomStatus.IN_PROGRESS);
        GameState gameState = gameStates.get(roomId);
        gameState.setStatus(GameState.GameStatus.IN_PROGRESS);
        gameState.setStartTime(java.time.LocalDateTime.now());

        return gameState;
    }

    public void endGame(String roomId) {
        Room room = rooms.get(roomId);
        if (room != null) {
            room.setStatus(Room.RoomStatus.FINISHED);

            GameState gameState = gameStates.get(roomId);
            gameState.setStatus(GameState.GameStatus.FINISHED);
            gameState.setEndTime(java.time.LocalDateTime.now());
            gameState.calculateFinalScores();
        }
    }

    private void assignRandomCharacter(Player player) {
        Random random = new Random();
        com.dungeonboard.model.character.PlayerClass[] classes =
                com.dungeonboard.model.character.PlayerClass.values();
        com.dungeonboard.model.character.Race[] races =
                com.dungeonboard.model.character.Race.values();

        com.dungeonboard.model.character.PlayerClass randomClass =
                classes[random.nextInt(classes.length)];
        com.dungeonboard.model.character.Race randomRace =
                races[random.nextInt(races.length)];

        player.setCharacter(randomClass, randomRace);
    }

    private String generateRoomId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public void addBotToRoom(String roomId) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new RuntimeException("Room not found");
        }

        if (room.isFull()) {
            throw new RuntimeException("Room is full");
        }

        int botNumber = (int) room.getPlayers().stream()
                .filter(Player::isBot)
                .count() + 1;

        Player bot = new Player(null, "Bot " + botNumber, true);
        room.addPlayer(bot);

        GameState gameState = gameStates.get(roomId);
        gameState.setPlayers(room.getPlayers());
    }
}
