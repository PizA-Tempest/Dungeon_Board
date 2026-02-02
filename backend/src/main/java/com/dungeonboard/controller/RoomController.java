package com.dungeonboard.controller;

import com.dungeonboard.dto.CreateRoomRequest;
import com.dungeonboard.dto.GameStateDTO;
import com.dungeonboard.model.GameState;
import com.dungeonboard.model.Room;
import com.dungeonboard.service.AuthService;
import com.dungeonboard.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/room")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private AuthService authService;

    @PostMapping("/create")
    public ResponseEntity<?> createRoom(@RequestBody CreateRoomRequest request) {
        try {
            var currentUser = authService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(401).body("User not authenticated");
            }

            Room room = roomService.createRoom(
                    request,
                    currentUser.getId().toString(),
                    currentUser.getUsername()
            );

            return ResponseEntity.ok(room);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{roomId}/join")
    public ResponseEntity<?> joinRoom(@PathVariable String roomId) {
        try {
            var currentUser = authService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(401).body("User not authenticated");
            }

            Room room = roomService.joinRoom(
                    roomId,
                    currentUser.getId().toString(),
                    currentUser.getUsername()
            );

            return ResponseEntity.ok(room);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{roomId}/leave")
    public ResponseEntity<?> leaveRoom(@PathVariable String roomId) {
        try {
            var currentUser = authService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(401).body("User not authenticated");
            }

            Room room = roomService.leaveRoom(
                    roomId,
                    currentUser.getId().toString()
            );

            return ResponseEntity.ok(room);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<?> getRoom(@PathVariable String roomId) {
        try {
            Room room = roomService.getRoom(roomId);
            if (room == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(room);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> getAllRooms() {
        try {
            List<Room> rooms = roomService.getAvailableRooms();
            return ResponseEntity.ok(rooms);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{roomId}/start")
    public ResponseEntity<?> startGame(@PathVariable String roomId) {
        try {
            var currentUser = authService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(401).body("User not authenticated");
            }

            Room room = roomService.getRoom(roomId);
            if (room == null || !room.isHost(currentUser.getId().toString())) {
                return ResponseEntity.status(403).body("Only host can start the game");
            }

            GameState gameState = roomService.startGame(roomId);
            return ResponseEntity.ok(GameStateDTO.fromGameState(gameState));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{roomId}/state")
    public ResponseEntity<?> getGameState(@PathVariable String roomId) {
        try {
            GameState gameState = roomService.getGameState(roomId);
            if (gameState == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(GameStateDTO.fromGameState(gameState));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{roomId}/add-bot")
    public ResponseEntity<?> addBot(@PathVariable String roomId) {
        try {
            roomService.addBotToRoom(roomId);
            Room room = roomService.getRoom(roomId);
            return ResponseEntity.ok(room);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
