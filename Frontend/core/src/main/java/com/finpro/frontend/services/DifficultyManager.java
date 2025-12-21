package com.finpro.frontend.services;

import com.finpro.frontend.obstacles.Enemy;

public class DifficultyManager {

    private static final DifficultyManager instance = new DifficultyManager();
    public enum DifficultyLevel {
        EASY,
        MEDIUM,
        HARD
    }
    private DifficultyLevel currentLevel;

    private DifficultyManager() {
        this.currentLevel = DifficultyLevel.EASY;
    }
    public static DifficultyManager getInstance() {
        return instance;
    }

    public void updateDifficulty(int currentScore) {
        if (currentScore < 3000) currentLevel = DifficultyLevel.EASY;
        else if (currentScore < 10000) currentLevel = DifficultyLevel.MEDIUM;
        else currentLevel = DifficultyLevel.HARD;
    }

    public DifficultyLevel getCurrentLevel() { return currentLevel; }
    public float getMinMeteorSpeed() {
        return switch (currentLevel) {
        case MEDIUM -> 200f;
        case HARD -> 300f;
        default -> 150f; };
    }
    public float getMaxMeteorSpeed() {
        return switch (currentLevel) {
            case MEDIUM -> 300f;
            case HARD -> 400f;
            default -> 250f;
        };
    }
    public float getSpawnInterval() {
        return switch (currentLevel) {
            case MEDIUM -> 4f;
            case HARD -> 3f;
            default -> 5.5f;
        };
    }
    public int getHpMultiplier() {
        return switch (currentLevel) {
            case MEDIUM -> 2;
            case HARD -> 3;
            default -> 1;
        };
    }
    public float getEnemyFireRate() {
        return switch (currentLevel) {
            case MEDIUM -> 1.5f;
            case HARD -> 0.8f;
            default -> 2.5f;
        };
    }

    public float getEnemySpeed(Enemy.Type type) {
        float mul = switch (currentLevel) {
            case MEDIUM -> 1.2f;
            case HARD -> 1.5f;
            default -> 1.0f;
        };
        float base = switch (type) {
            case SMALL -> 250f;
            case MEDIUM -> 150f;
            case BIG -> 50f;
            default -> 100f;
        };
        return base * mul;
    }

    public float getInterval(Enemy.Type type) {
        return switch (type) {
            case SMALL -> switch (currentLevel) {
                case MEDIUM -> 4.0f;
                case HARD -> 3.0f;
                default -> 5.0f;
            };
            case MEDIUM -> switch (currentLevel) {
                case MEDIUM -> 7.0f;
                case HARD -> 6.0f;
                default -> 11.0f;
            };
            case BIG -> switch (currentLevel) {
                case MEDIUM -> 17.0f;
                case HARD -> 13.0f;
                default -> 23.0f;
            };
            case BOSS -> 60.0f; // Boss muncul tiap 1 menit
        };
    }
}
