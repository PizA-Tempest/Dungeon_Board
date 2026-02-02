package com.dungeonboard.model.board;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tile {
    private int position;
    private TileType type;

    // Event-specific data
    private int monsterLevel;
    private int treasureAmount;
    private int trapDamage;
    private int portalDestination;

    public Tile(int position, TileType type) {
        this.position = position;
        this.type = type;
        // Default values
        this.monsterLevel = 1;
        this.treasureAmount = 10;
        this.trapDamage = 5;
        this.portalDestination = -1; // -1 means random
    }

    public static Tile createNormalTile(int position) {
        return new Tile(position, TileType.NORMAL);
    }

    public static Tile createMonsterTile(int position, int level) {
        Tile tile = new Tile(position, TileType.MONSTER);
        tile.setMonsterLevel(level);
        return tile;
    }

    public static Tile createTreasureTile(int position, int amount) {
        Tile tile = new Tile(position, TileType.TREASURE);
        tile.setTreasureAmount(amount);
        return tile;
    }

    public static Tile createTrapTile(int position, int damage) {
        Tile tile = new Tile(position, TileType.TRAP);
        tile.setTrapDamage(damage);
        return tile;
    }

    public static Tile createPortalTile(int position) {
        return new Tile(position, TileType.PORTAL);
    }

    public static Tile createEventTile(int position) {
        return new Tile(position, TileType.EVENT);
    }

    public static Tile createShopTile(int position) {
        return new Tile(position, TileType.SHOP);
    }

    public static Tile createStartTile(int position) {
        return new Tile(position, TileType.START);
    }
}
