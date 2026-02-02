package com.dungeonboard.model.board;

import lombok.Getter;

/**
 * Tile types on the board
 */
@Getter
public enum TileType {
    NORMAL("Normal", "A safe tile", 0),
    MONSTER("Monster", "A wild monster appears!", 1),
    TREASURE("Treasure", "Found some gold!", 2),
    TRAP("Trap", "It's a trap!", 3),
    PORTAL("Portal", "Teleport to a random location", 4),
    EVENT("Event", "Something unexpected happens", 5),
    SHOP("Shop", "Buy items and upgrades", 6),
    START("Start", "Starting position", 7);

    private final String name;
    private final String description;
    private final int id;

    TileType(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
    }
}
