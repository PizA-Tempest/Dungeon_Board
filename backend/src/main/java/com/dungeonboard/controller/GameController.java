package com.dungeonboard.controller;

import com.dungeonboard.dto.CharacterSelectionRequest;
import com.dungeonboard.dto.GameEvent;
import com.dungeonboard.dto.GameStateDTO;
import com.dungeonboard.model.GameState;
import com.dungeonboard.service.AuthService;
import com.dungeonboard.service.GameService;
import com.dungeonboard.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*", maxAge = 3600)
public class GameController {

    @Autowired
    private GameService gameService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private AuthService authService;

    @PostMapping("/{roomId}/roll")
    public ResponseEntity<?> rollDice(@PathVariable String roomId) {
        try {
            var currentUser = authService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(401).body("User not authenticated");
            }

            GameEvent result = gameService.rollDice(roomId, currentUser.getId().toString());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{roomId}/reroll")
    public ResponseEntity<?> reroll(@PathVariable String roomId) {
        try {
            var currentUser = authService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(401).body("User not authenticated");
            }

            GameEvent result = gameService.reroll(roomId, currentUser.getId().toString());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{roomId}/character")
    public ResponseEntity<?> selectCharacter(
            @PathVariable String roomId,
            @RequestBody CharacterSelectionRequest request) {
        try {
            var currentUser = authService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(401).body("User not authenticated");
            }

            gameService.selectCharacter(roomId, currentUser.getId().toString(),
                    request.getClassId(), request.getRaceId());

            GameState gameState = roomService.getGameState(roomId);
            return ResponseEntity.ok(GameStateDTO.fromGameState(gameState));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{roomId}/ability")
    public ResponseEntity<?> useAbility(@PathVariable String roomId) {
        try {
            var currentUser = authService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(401).body("User not authenticated");
            }

            gameService.useAbility(roomId, currentUser.getId().toString());
            return ResponseEntity.ok("Ability used");
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

    @GetMapping("/classes")
    public ResponseEntity<?> getClasses() {
        try {
            var classes = com.dungeonboard.model.character.PlayerClass.values();
            return ResponseEntity.ok(classes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/races")
    public ResponseEntity<?> getRaces() {
        try {
            var races = com.dungeonboard.model.character.Race.values();
            return ResponseEntity.ok(races);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
