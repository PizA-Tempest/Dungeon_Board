package com.dungeonboard.model.board;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Game board - 24 tile circular board
 */
@Data
public class Board {
    private final int size;
    private final List<Tile> tiles;

    public Board(int size) {
        this.size = size;
        this.tiles = new ArrayList<>(size);
        initializeBoard();
    }

    private void initializeBoard() {
        // Create a balanced board layout
        tiles.clear();

        // Start tile at position 0
        tiles.add(Tile.createStartTile(0));

        // Distribute tile types across the board
        // For a 24-tile board:
        // - 4 Monster tiles
        // - 4 Treasure tiles
        // - 3 Trap tiles
        // - 2 Portal tiles
        // - 3 Event tiles
        // - 1 Shop tile
        // - 6 Normal tiles
        // (plus Start tile at position 0)

        for (int i = 1; i < size; i++) {
            TileType type = determineTileType(i, size);
            Tile tile = switch (type) {
                case MONSTER -> Tile.createMonsterTile(i, (i / 6) + 1); // Level increases every 6 tiles
                case TREASURE -> Tile.createTreasureTile(i, 10 + (i / 3) * 5); // Gold increases with position
                case TRAP -> Tile.createTrapTile(i, 5 + (i / 8) * 3);
                case PORTAL -> Tile.createPortalTile(i);
                case EVENT -> Tile.createEventTile(i);
                case SHOP -> Tile.createShopTile(i);
                default -> Tile.createNormalTile(i);
            };
            tiles.add(tile);
        }
    }

    private TileType determineTileType(int position, int boardSize) {
        // Use a semi-random pattern to ensure good distribution
        int pattern = position % 8;

        return switch (pattern) {
            case 0 -> TileType.NORMAL;
            case 1 -> TileType.MONSTER;
            case 2 -> TileType.NORMAL;
            case 3 -> TileType.TREASURE;
            case 4 -> TileType.NORMAL;
            case 5 -> TileType.MONSTER;
            case 6 -> TileType.EVENT;
            case 7 -> position == boardSize - 1 ? TileType.SHOP : TileType.NORMAL;
            default -> TileType.NORMAL;
        };
    }

    public Tile getTile(int position) {
        // Handle circular movement
        int actualPosition = ((position % size) + size) % size;
        return tiles.get(actualPosition);
    }

    public int getMonsterLevel(int position) {
        return getTile(position).getMonsterLevel();
    }

    public int getTreasureAmount(int position) {
        return getTile(position).getTreasureAmount();
    }

    public int getTrapDamage(int position) {
        return getTile(position).getTrapDamage();
    }
}
