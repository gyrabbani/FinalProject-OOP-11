package com.finpro.frontend;

import com.badlogic.gdx.Gdx;

public class GameConfig {
    public static final float SCREEN_WIDTH = Gdx.graphics.getWidth();
    public static final float SCREEN_HEIGHT = Gdx.graphics.getHeight();
    public static final String TITLE = "Galaxy Shooter Finpro";

    public static final float PLAYER_SPEED = 400f;
    public static final float PLAYER_WIDTH = 64f;
    public static final float PLAYER_HEIGHT = 64f;

    public static final int PLAYER_LIVES = 3;

    public static final float BULLET_SPEED = 500f;
    public static final float BULLET_WIDTH = 15f;
    public static final float BULLET_HEIGHT = 30f;

    public static final float METEOR_MIN_SPEED = 200f;
    public static final float METEOR_MAX_SPEED = 400f;
    public static final float METEOR_WIDTH = 28f;
    public static final float METEOR_HEIGHT = 28f;
    public static final float METEOR_SPAWN_TIME = 1.0f;

    public static final boolean DEBUG_MODE = true;
}
