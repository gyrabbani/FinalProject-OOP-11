package com.finpro.frontend.services;

public class DifficultyManager {

    private static final DifficultyManager instance = new DifficultyManager();

    public enum DifficultyLevel {
        EASY, MEDIUM, HARD
    }

    private DifficultyLevel currentLevel;

    private DifficultyManager() {
        this.currentLevel = DifficultyLevel.EASY;
    }

    public static DifficultyManager getInstance() {
        return instance;
    }

    public void updateDifficulty(int currentScore) {
        if (currentScore < 1000) {
            currentLevel = DifficultyLevel.EASY;
        } else if (currentScore < 3000) {
            currentLevel = DifficultyLevel.MEDIUM;
        } else {
            currentLevel = DifficultyLevel.HARD;
        }
    }

    public DifficultyLevel getCurrentLevel() {
        return currentLevel;
    }

    // --- KECEPATAN METEOR  ---
    public float getMinMeteorSpeed() {
        switch (currentLevel) {
            case MEDIUM: return 200f; // Lebih cepat dari Easy
            case HARD:   return 300f; // Sangat cepat
            default:     return 150f; // Easy (Pelan)
        }
    }

    public float getMaxMeteorSpeed() {
        switch (currentLevel) {
            case MEDIUM: return 300f;
            case HARD:   return 400f;
            default:     return 250f; // Easy
        }
    }

    // --- SPAWN RATE  ---
    // Return dalam satuan DETIK
    public float getSpawnInterval() {
        switch (currentLevel) {
            case MEDIUM: return 1f;
            case HARD:   return 0.4f;
            default:     return 1.5f;
        }
    }
}
