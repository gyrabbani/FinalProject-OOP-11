package com.finpro.frontend;

public class GameManager {

    private static final GameManager instance = new GameManager();

    private int score;
    private boolean isGameOver;

    private GameManager() {
    }

    public static GameManager getInstance() {
        return instance;
    }

    public void reset() {
        score = 0;
        isGameOver = false;
    }

    public void addScore(int value) {
        if (!isGameOver) {
            score += value;
        }
    }

    public int getScore() {
        return score;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
    }
}
