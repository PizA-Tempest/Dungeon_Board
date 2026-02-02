package com.dungeonboard.model;

import com.dungeonboard.model.character.Character;
import com.dungeonboard.model.character.PlayerClass;
import com.dungeonboard.model.character.Race;
import lombok.Data;

@Data
public class Player {
    private Long id;
    private String username;
    private boolean isBot;
    private Character character;

    // Position and movement
    private int position;
    private int skipTurns;

    // Stats
    private int gold;
    private int score;
    private int monstersDefeated;
    private int treasuresCollected;

    public Player() {
        this.position = 0; // Start position
        this.skipTurns = 0;
        this.gold = 0;
        this.score = 0;
        this.monstersDefeated = 0;
        this.treasuresCollected = 0;
    }

    public Player(Long id, String username, boolean isBot) {
        this();
        this.id = id;
        this.username = username;
        this.isBot = isBot;
    }

    public void setCharacter(PlayerClass playerClass, Race race) {
        this.character = new Character(playerClass, race);
    }

    public void move(int tiles) {
        this.position += tiles;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void addGold(int amount) {
        this.gold += amount;
        if (amount > 0) {
            this.treasuresCollected++;
        }
    }

    public void removeGold(int amount) {
        this.gold = Math.max(0, this.gold - amount);
    }

    public void skipTurn() {
        this.skipTurns++;
    }

    public boolean shouldSkipTurn() {
        return skipTurns > 0;
    }

    public void decrementSkipTurns() {
        if (skipTurns > 0) {
            skipTurns--;
        }
    }

    public void incrementMonstersDefeated() {
        this.monstersDefeated++;
    }

    public boolean isAlive() {
        return character != null && character.isAlive();
    }

    public void calculateScore() {
        // Score = gold + bonus points
        // Bonus: 10 points per monster defeated, 5 points per treasure collected
        this.score = gold + (monstersDefeated * 10) + (treasuresCollected * 5);
    }
}
