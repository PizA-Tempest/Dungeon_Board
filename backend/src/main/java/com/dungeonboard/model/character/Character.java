package com.dungeonboard.model.character;

import lombok.Data;

@Data
public class Character {
    private PlayerClass playerClass;
    private Race race;

    // Derived stats
    private int maxHp;
    private int currentHp;
    private int attack;
    private int defense;

    // Special ability usage tracking
    private boolean abilityUsedThisMatch; // For one-per-match abilities
    private boolean abilityUsedThisTurn; // For once-per-turn abilities
    private int rerollsUsedThisTurn; // For Halfling reroll

    public Character(PlayerClass playerClass, Race race) {
        this.playerClass = playerClass;
        this.race = race;
        this.maxHp = playerClass.getBaseHp();
        this.currentHp = maxHp;
        this.attack = playerClass.getBaseAttack() + race.getAttackBonus();
        this.defense = playerClass.getBaseDefense() + (race.getRollBonus() > 0 ? race.getRollBonus() : 0);
        this.abilityUsedThisMatch = false;
        this.abilityUsedThisTurn = false;
        this.rerollsUsedThisTurn = 0;
    }

    public int getTotalAttack() {
        return attack;
    }

    public int getTotalDefense() {
        return defense;
    }

    public void takeDamage(int damage) {
        this.currentHp = Math.max(0, this.currentHp - damage);
    }

    public void heal(int amount) {
        this.currentHp = Math.min(maxHp, this.currentHp + amount);
    }

    public boolean isAlive() {
        return currentHp > 0;
    }

    public void resetForNewTurn() {
        this.abilityUsedThisTurn = false;
        this.rerollsUsedThisTurn = 0;
    }

    public void resetForNewMatch() {
        this.currentHp = this.maxHp;
        this.abilityUsedThisMatch = false;
        this.abilityUsedThisTurn = false;
        this.rerollsUsedThisTurn = 0;
    }

    public boolean canUseReroll() {
        return race.hasRerollAbility() && rerollsUsedThisTurn == 0;
    }

    public void useReroll() {
        if (canUseReroll()) {
            rerollsUsedThisTurn++;
        }
    }
}
