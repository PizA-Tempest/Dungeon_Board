package com.dungeonboard.dto;

import com.dungeonboard.model.GameState;
import com.dungeonboard.model.Player;
import com.dungeonboard.model.board.Board;
import com.dungeonboard.model.board.Tile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameStateDTO {
    private String roomId;
    private String status;
    private List<SimplePlayer> players;
    private List<SimpleTile> tiles;
    private int currentPlayerIndex;
    private int currentRound;
    private int maxRounds;
    private String lastEvent;
    private boolean waitingForRoll;
    private String winnerId;

    public static GameStateDTO fromGameState(GameState gameState) {
        List<SimplePlayer> players = gameState.getPlayers().stream()
                .map(SimplePlayer::fromPlayer)
                .collect(Collectors.toList());

        List<SimpleTile> tiles = gameState.getBoard().getTiles().stream()
                .map(SimpleTile::fromTile)
                .collect(Collectors.toList());

        return new GameStateDTO(
                gameState.getRoomId(),
                gameState.getStatus().name(),
                players,
                tiles,
                gameState.getCurrentPlayerIndex(),
                gameState.getCurrentRound(),
                gameState.getMaxRounds(),
                gameState.getLastEvent(),
                gameState.isWaitingForRoll(),
                gameState.getWinnerId()
        );
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimplePlayer {
        private Long id;
        private String username;
        private boolean isBot;
        private String playerClass;
        private String race;
        private int position;
        private int gold;
        private int score;
        private int currentHp;
        private int maxHp;
        private boolean isAlive;

        public static SimplePlayer fromPlayer(Player player) {
            return new SimplePlayer(
                    player.getId(),
                    player.getUsername(),
                    player.isBot(),
                    player.getCharacter() != null ? player.getCharacter().getPlayerClass().getName() : null,
                    player.getCharacter() != null ? player.getCharacter().getRace().getName() : null,
                    player.getPosition(),
                    player.getGold(),
                    player.getScore(),
                    player.getCharacter() != null ? player.getCharacter().getCurrentHp() : 0,
                    player.getCharacter() != null ? player.getCharacter().getMaxHp() : 0,
                    player.isAlive()
            );
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimpleTile {
        private int position;
        private String type;
        private String description;

        public static SimpleTile fromTile(Tile tile) {
            return new SimpleTile(
                    tile.getPosition(),
                    tile.getType().getName(),
                    tile.getType().getDescription()
            );
        }
    }
}
