package com.dungeonboard.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class DiceService {

    private final Random random = new Random();

    public int rollDice() {
        return random.nextInt(6) + 1; // 1-6
    }

    public int rollMultipleDice(int count) {
        int total = 0;
        for (int i = 0; i < count; i++) {
            total += rollDice();
        }
        return total;
    }

    public boolean rollChance(double chance) {
        return random.nextDouble() < chance;
    }
}
