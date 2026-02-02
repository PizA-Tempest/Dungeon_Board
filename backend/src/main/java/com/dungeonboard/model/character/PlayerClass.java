package com.dungeonboard.model.character;

import lombok.Getter;

/**
 * 8 Classes with unique abilities
 */
@Getter
public enum PlayerClass {
    WARRIOR("Warrior", "Deal +1 damage, shield once per match", 1, 100, 15, 10),
    MAGE("Mage", "Cast fireball (3 tiles range), higher mana", 2, 80, 20, 8),
    ROGUE("Rogue", "Steal gold from player on same tile, dodge chance", 3, 70, 18, 12),
    CLERIC("Cleric", "Heal self, remove curses", 4, 90, 15, 10),
    RANGER("Ranger", "Critical hit on 6 roll, move through enemies", 5, 85, 17, 11),
    PALADIN("Paladin", "Smite evil monsters, protect ally once per match", 6, 95, 14, 12),
    BARD("Bard", "Buff all players (chaos), distract enemies", 7, 75, 16, 9),
    NECROMANCER("Necromancer", "Summon minion, drain life from monsters", 8, 70, 22, 7);

    private final String name;
    private final String description;
    private final int id;
    private final int baseHp;
    private final int baseAttack;
    private final int baseDefense;

    PlayerClass(String name, String description, int id, int baseHp, int baseAttack, int baseDefense) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.baseHp = baseHp;
        this.baseAttack = baseAttack;
        this.baseDefense = baseDefense;
    }
}
