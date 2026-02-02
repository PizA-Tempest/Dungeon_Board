package com.dungeonboard.model.character;

import lombok.Getter;

/**
 * 8 Races with passive bonuses
 */
@Getter
public enum Race {
    HUMAN("Human", "Balanced, +1 to all rolls", 1, 0, 0, 0, 1),
    ELF("Elf", "+1 movement on 5-6 roll", 2, 1, 0, 0, 0),
    DWARF("Dwarf", "+2 gold from treasures", 3, 0, 0, 2, 0),
    ORC("Orc", "+2 damage, -1 defense", 4, 0, 2, 0, -1),
    HALFLING("Halfling", "Can reroll once per turn", 5, 0, 0, 0, 0),
    TIEFLING("Tiefling", "+50% curse effectiveness, chaos magic", 6, 0, 0, 0, 0),
    DRAGONBORN("Dragonborn", "Breath weapon (3-tile cone), fire resist", 7, 0, 1, 0, 0),
    GOBLIN("Goblin", "Steal bonus, trap immunity", 8, 0, 0, 1, 0);

    private final String name;
    private final String description;
    private final int id;
    private final int movementBonus;
    private final int attackBonus;
    private final int goldBonus;
    private final int rollBonus;

    Race(String name, String description, int id, int movementBonus, int attackBonus, int goldBonus, int rollBonus) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.movementBonus = movementBonus;
        this.attackBonus = attackBonus;
        this.goldBonus = goldBonus;
        this.rollBonus = rollBonus;
    }

    public boolean hasRerollAbility() {
        return this == HALFLING;
    }

    public boolean isTrapImmune() {
        return this == GOBLIN;
    }
}
