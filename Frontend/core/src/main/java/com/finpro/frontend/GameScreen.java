package com.finpro.frontend;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.finpro.frontend.pools.BulletPool;
import com.finpro.frontend.pools.MeteorPool;
import com.finpro.frontend.obstacles.Meteor;
import com.finpro.frontend.commands.InputHandler;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {

    public enum GameState { PLAYING, PAUSED, GAME_OVER }
    private GameState currentState;
    private SpriteBatch batch;

    private PlayerShip player;
    private Background background;
    private InputHandler inputHandler;

    private BulletPool bulletPool;
    private MeteorPool meteorPool;
    private float meteorSpawnTimer;

    private BitmapFont font;
    private Texture heartTexture;

    public GameScreen() {
        this.batch = new SpriteBatch();
        this.currentState = GameState.PLAYING;
        this.font = new BitmapFont();
        this.font.getData().setScale(2);

        this.bulletPool = new BulletPool();
        this.meteorPool = new MeteorPool();
        this.inputHandler = new InputHandler();

        this.background = new Background();
        this.player = new PlayerShip();
        this.player.setBulletPool(bulletPool);

        this.heartTexture = new Texture(Gdx.files.internal("heart.png"));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (currentState == GameState.PLAYING) {
            updatePlaying(delta);
        } else if (currentState == GameState.GAME_OVER) {
            updateGameOver();
        }

        batch.begin();

        if (background != null) background.render(batch);

        for (Bullet b : bulletPool.getActiveObjects()) b.render(batch);
        for (Meteor m : meteorPool.getActiveObjects()) m.render(batch);

        if (player != null) player.render(batch);

        if (player != null) {
            for (int i = 0; i < player.getLives(); i++) {
                batch.draw(heartTexture, 20 + (i * 30), GameConfig.SCREEN_HEIGHT - 50, 20, 20);
            }
        }

        if (currentState == GameState.GAME_OVER) {
            font.setColor(Color.RED);
            font.draw(batch, "GAME OVER", GameConfig.SCREEN_WIDTH/2 - 80, GameConfig.SCREEN_HEIGHT/2 + 20);
            font.getData().setScale(1.5f);
            font.setColor(Color.WHITE);
            font.draw(batch, "Press SPACE to Restart", GameConfig.SCREEN_WIDTH/2 - 120, GameConfig.SCREEN_HEIGHT/2 - 30);
            font.getData().setScale(2f);
        }

        batch.end();
    }

    private void updatePlaying(float delta) {
        inputHandler.handleInput(player, delta);
        if (background != null) background.update(delta);
        if (player != null) player.update(delta);

        meteorSpawnTimer += delta;
        if (meteorSpawnTimer > GameConfig.METEOR_SPAWN_TIME) {
            meteorSpawnTimer = 0;
            spawnMeteor();
        }

        updateBullets(delta);
        updateMeteors(delta);
        checkCollisions();

        if (player.isDead()) {
            currentState = GameState.GAME_OVER;
        }
    }

    private void updateGameOver() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            restartGame();
        }
    }

    private void restartGame() {
        player.reset();

        List<Meteor> meteorsToRemove = new ArrayList<>(meteorPool.getActiveObjects());
        for (Meteor m : meteorsToRemove) meteorPool.free(m);

        List<Bullet> bulletsToRemove = new ArrayList<>(bulletPool.getActiveObjects());
        for (Bullet b : bulletsToRemove) bulletPool.free(b);

        meteorSpawnTimer = 0;
        currentState = GameState.PLAYING;
    }

    private void spawnMeteor() {
        Meteor m = meteorPool.obtain();
        float startX = MathUtils.random(0, GameConfig.SCREEN_WIDTH - m.getWidth());
        float startY = GameConfig.SCREEN_HEIGHT;
        float targetX = player.getX();
        float targetY = player.getY();
        m.init(startX, startY, targetX, targetY);
    }

    private void updateBullets(float delta) {
        List<Bullet> activeBullets = bulletPool.getActiveObjects();
        for (int i = 0; i < activeBullets.size(); i++) {
            Bullet b = activeBullets.get(i);
            b.update(delta);
            if (!b.isActive()) {
                bulletPool.free(b);
                i--;
            }
        }
    }

    private void updateMeteors(float delta) {
        List<Meteor> activeMeteors = meteorPool.getActiveObjects();
        for (int i = 0; i < activeMeteors.size(); i++) {
            Meteor m = activeMeteors.get(i);
            m.update(delta);
            if (!m.isActive()) {
                meteorPool.free(m);
                i--;
            }
        }
    }

    private void checkCollisions() {
        List<Bullet> bullets = bulletPool.getActiveObjects();
        List<Meteor> meteors = meteorPool.getActiveObjects();

        for (int i = 0; i < meteors.size(); i++) {
            Meteor m = meteors.get(i);

            if (m.getBounds().overlaps(player.getBounds())) {
                player.hit();
                meteorPool.free(m);
                i--;
                continue;
            }

            for (int j = 0; j < bullets.size(); j++) {
                Bullet b = bullets.get(j);
                if (m.getBounds().overlaps(b.getBounds())) {
                    m.takeDamage();
                    bulletPool.free(b);
                    if (m.isDestroyed()) {
                        meteorPool.free(m);
                        i--;
                    }
                    break;
                }
            }
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void show() {}
    @Override public void pause() { if(currentState == GameState.PLAYING) currentState = GameState.PAUSED; }
    @Override public void resume() { if(currentState == GameState.PAUSED) currentState = GameState.PLAYING; }
    @Override public void hide() {}
    @Override public void dispose() {
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
        if (heartTexture != null) heartTexture.dispose();
    }
}
