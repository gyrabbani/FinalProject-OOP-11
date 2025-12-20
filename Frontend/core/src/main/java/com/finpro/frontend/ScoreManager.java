package com.finpro.frontend;

import com.finpro.frontend.observer.Observer;

public class ScoreManager implements Observer {

    private int score;

    public ScoreManager() {
        this.score = 0;
    }

    @Override
    public void onNotify(String event) {
        if ("METEOR_DESTROYED".equals(event)) {
            addScore(50);
        }
        if ("ENEMY_DESTROYED".equals(event)){
            addScore(150);
        }
    }

    public void addScore(int value) {
        this.score += value;
    }

    public int getScore() {
        return score;
    }

    public void reset() {
        this.score = 0;
    }
}
