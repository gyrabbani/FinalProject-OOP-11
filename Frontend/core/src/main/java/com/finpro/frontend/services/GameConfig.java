package com.finpro.frontend.services;

import com.badlogic.gdx.Gdx;

public class GameConfig {
    public static final float SCREEN_WIDTH = Gdx.graphics.getWidth();
    public static final float SCREEN_HEIGHT = Gdx.graphics.getHeight();

    public static final float PLAYER_SPEED = 400f;
    public static final float PLAYER_WIDTH = 64f;
    public static final float PLAYER_HEIGHT = 64f;

    public static final int PLAYER_LIVES = 5;

    public static final float BULLET_SPEED = 500f;
    public static final float BULLET_WIDTH = 15f;
    public static final float BULLET_HEIGHT = 30f;

    public static final float METEOR_WIDTH = 40f;
    public static final float METEOR_HEIGHT = 25f;

}
