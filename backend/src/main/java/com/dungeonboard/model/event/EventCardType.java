package com.dungeonboard.model.event;

import lombok.Getter;

/**
 * Event cards that add chaos to the game
 */
@Getter
public enum EventCardType {
    // Bless Cards (positive effects)
    BLESS_GOLD("Gold Rush", "Double your gold from next treasure", 1, EventType.BLESS),
    BLESS_MOVE("Swift Feet", "Move +3 tiles on your next turn", 2, EventType.BLESS),
    BLESS_SHIELD("Divine Shield", "Immune to next trap or monster attack", 3, EventType.BLESS),
    BLESS_HEAL("Healing Light", "Restore 20 HP", 4, EventType.BLESS),
    BLESS_REROLL("Lucky Star", "Reroll your next dice roll", 5, EventType.BLESS),
    BLESS_STEAL("Pickpocket", "Steal 10 gold from random player", 6, EventType.BLESS),

    // Curse Cards (negative effects)
    CURSE_LOSE_TURN("Stunned", "Skip your next turn", 101, EventType.CURSE),
    CURSE_DROP_GOLD("Greedy Ghost", "Drop 15 gold", 102, EventType.CURSE),
    CURSE_SWAP("Confusion", "Swap positions with random player", 103, EventType.CURSE),
    CURSE_DAMAGE("Poison", "Take 10 damage", 104, EventType.CURSE),
    CURSE_TELEPORT("Warp", "Teleport to a random tile", 105, EventType.CURSE),
    CURSE_MOVE_BACK("Stumble", "Move back 3 tiles", 106, EventType.CURSE);

    private final String name;
    private final String description;
    private final int id;
    private final EventType type;

    EventCardType(String name, String description, int id, EventType type) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.type = type;
    }

    public enum EventType {
        BLESS, CURSE
    }
}
