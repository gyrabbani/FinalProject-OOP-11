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
        // Logika Level Baru sesuai permintaan
        if (currentScore < 5000) {
            currentLevel = DifficultyLevel.EASY;
        } else if (currentScore < 10000) {
            currentLevel = DifficultyLevel.MEDIUM;
        } else {
            currentLevel = DifficultyLevel.HARD;
        }
    }

    public DifficultyLevel getCurrentLevel() {
        return currentLevel;
    }

    // --- KECEPATAN METEOR (Semakin sulit, semakin ngebut) ---
    public float getMinMeteorSpeed() {
        switch (currentLevel) {
            case MEDIUM: return 200f; // Lebih cepat dari Easy
            case HARD:   return 350f; // Sangat cepat
            default:     return 100f; // Easy (Pelan)
        }
    }

    public float getMaxMeteorSpeed() {
        switch (currentLevel) {
            case MEDIUM: return 400f;
            case HARD:   return 600f;
            default:     return 250f; // Easy
        }
    }

    // --- SPAWN RATE (Semakin sulit, interval waktu makin KECIL/cepat) ---
    // Return dalam satuan DETIK
    public float getSpawnInterval() {
        switch (currentLevel) {
            case MEDIUM: return 0.8f; // Muncul tiap 0.8 detik
            case HARD:   return 0.4f; // Muncul tiap 0.4 detik (Banjir meteor!)
            default:     return 1.5f; // Easy: Muncul tiap 1.5 detik (Santai)
        }
    }
}
