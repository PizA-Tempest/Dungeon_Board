package com.dungeonboard.service;

import com.dungeonboard.model.Player;
import com.dungeonboard.model.character.Character;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CombatService {

    @Autowired
    private DiceService diceService;

    public CombatResult resolveCombat(Player player, int monsterLevel) {
        if (player.getCharacter() == null || !player.getCharacter().isAlive()) {
            return new CombatResult(false, 0, 0);
        }

        Character character = player.getCharacter();
        int playerAttack = character.getTotalAttack();
        int playerDefense = character.getTotalDefense();

        // Calculate monster stats based on level
        int monsterHp = monsterLevel * 15;
        int monsterAttack = monsterLevel * 5;

        // Player attacks first
        int damageToMonster = calculateDamage(playerAttack, monsterLevel);

        // Apply class-specific combat bonuses
        if (character.getPlayerClass() == com.dungeonboard.model.character.PlayerClass.WARRIOR) {
            damageToMonster += 1; // Warrior bonus
        } else if (character.getPlayerClass() == com.dungeonboard.model.character.PlayerClass.PALADIN &&
                   character.isAbilityUsedThisMatch()) {
            damageToMonster += 5; // Paladin smite bonus
        }

        // Roll for critical hit (Ranger bonus on 6)
        int attackRoll = diceService.rollDice();
        if (character.getPlayerClass() == com.dungeonboard.model.character.PlayerClass.RANGER &&
            attackRoll == 6) {
            damageToMonster *= 2;
        }

        monsterHp -= damageToMonster;

        if (monsterHp <= 0) {
            // Victory
            return new CombatResult(true, damageToMonster, 0);
        }

        // Monster attacks back
        int damageToPlayer = Math.max(1, monsterAttack - playerDefense / 2);

        // Orc racial bonus (+2 damage) applies to combat
        if (character.getRace() == com.dungeonboard.model.character.Race.ORC) {
            damageToMonster += 2;
        }

        // Apply defensive abilities
        if (character.getPlayerClass() == com.dungeonboard.model.character.PlayerClass.WARRIOR &&
            character.isAbilityUsedThisMatch()) {
            damageToPlayer = 0; // Shield ability
        }

        character.takeDamage(damageToPlayer);

        // Check if player survived
        boolean victory = character.isAlive();

        return new CombatResult(victory, damageToMonster, damageToPlayer);
    }

    private int calculateDamage(int attack, int monsterLevel) {
        // Base damage plus some randomness
        int baseDamage = attack + (diceService.rollDice() / 3);
        return Math.max(1, baseDamage - monsterLevel / 2);
    }

    public record CombatResult(boolean victory, int damageDealt, int damageTaken) {}
}
