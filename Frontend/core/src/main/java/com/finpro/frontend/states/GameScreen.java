package com.finpro.frontend.states;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import com.finpro.frontend.Background;
import com.finpro.frontend.PlayerShip;
import com.finpro.frontend.ScoreManager;
import com.finpro.frontend.commands.InputHandler;
import com.finpro.frontend.factories.MeteorFactory;
import com.finpro.frontend.Bullet;
import com.finpro.frontend.obstacles.Meteor;
import com.finpro.frontend.observer.Observer;
import com.finpro.frontend.observer.Subject;
import com.finpro.frontend.pools.BulletPool;
import com.finpro.frontend.pools.MeteorPool;
import com.finpro.frontend.services.GameConfig;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen, Subject {

    public enum GameState { PLAYING, PAUSED, GAME_OVER }
    private GameState currentState;
    private SpriteBatch batch;

    private OrthographicCamera camera;
    private Viewport viewport;
    private GlyphLayout layout;

    private PlayerShip player;
    private Background background;
    private InputHandler inputHandler;

    private BulletPool bulletPool;
    private MeteorPool meteorPool;
    private MeteorFactory meteorFactory;
    private float meteorSpawnTimer;

    private BitmapFont font;
    private Texture heartTexture;

    private ScoreManager scoreManager;
    private List<Observer> observers;

    public GameScreen() {
        this.batch = new SpriteBatch();
        this.currentState = GameState.PLAYING;

        this.camera = new OrthographicCamera();
        this.viewport = new ExtendViewport(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT, camera);        this.viewport.apply();

        this.layout = new GlyphLayout();

        this.font = new BitmapFont();
        this.font.getData().setScale(1.5f);

        this.bulletPool = new BulletPool();
        this.meteorPool = new MeteorPool();
        this.meteorFactory = new MeteorFactory(meteorPool);

        this.inputHandler = new InputHandler();
        this.background = new Background();
        this.player = new PlayerShip();
        this.player.setBulletPool(bulletPool);

        this.heartTexture = new Texture(Gdx.files.internal("heart.png"));

        this.observers = new ArrayList<>();
        this.scoreManager = new ScoreManager();
        addObserver(scoreManager);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleGlobalInput();
        if (currentState == GameState.PLAYING) {
            updatePlaying(delta);
        }

        camera.update();
        batch.setProjectionMatrix(camera.combined);

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

        Color oldColor = font.getColor();
        font.setColor(Color.WHITE);
        font.draw(batch, "Score: " + scoreManager.getScore(), 20, 40);
        font.setColor(oldColor);
        drawOverlayText();
        batch.end();
    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    private void handleGlobalInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (currentState == GameState.PLAYING) currentState = GameState.PAUSED;
            else if (currentState == GameState.PAUSED) currentState = GameState.PLAYING;
        }
    }

    private void updatePlaying(float delta) {
        inputHandler.handleInput(player, delta);
        if (player != null) {
            player.updateLimits(viewport.getWorldWidth(), viewport.getWorldHeight());
            player.update(delta);
        }

        if (background != null) background.update(delta);

        meteorSpawnTimer += delta;
        if (meteorSpawnTimer > GameConfig.METEOR_SPAWN_TIME) {
            meteorSpawnTimer = 0;
            spawnMeteor();
        }

        updateBullets(delta);
        updateMeteors(delta);
        checkCollisions();

        if (player.isDead()) currentState = GameState.GAME_OVER;
    }

    private void drawOverlayText() {
        if (currentState == GameState.GAME_OVER) {
            font.setColor(Color.RED);
            font.getData().setScale(2f);

            String text1 = "GAME OVER";
            layout.setText(font, text1);
            float x1 = camera.position.x - (layout.width / 2);
            float y1 = camera.position.y + 50;
            font.draw(batch, text1, x1, y1);

            font.setColor(Color.WHITE);
            font.getData().setScale(1.5f);

            String text2 = "Press SPACE to Restart";
            layout.setText(font, text2);
            float x2 = camera.position.x - (layout.width / 2);
            float y2 = camera.position.y - 10;
            font.draw(batch, text2, x2, y2);

            font.setColor(Color.YELLOW);

            String text3 = "Press M for Menu";
            layout.setText(font, text3);
            float x3 = camera.position.x - (layout.width / 2);
            float y3 = camera.position.y - 50;
            font.draw(batch, text3, x3, y3);

            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) restartGame();
            if (Gdx.input.isKeyJustPressed(Input.Keys.M)) ((Game) Gdx.app.getApplicationListener()).setScreen(new MenuScreen());

            font.getData().setScale(1.5f);

        } else if (currentState == GameState.PAUSED) {
            font.setColor(Color.YELLOW);
            font.getData().setScale(2f);

            String textPause = "PAUSED";
            layout.setText(font, textPause);
            float xP = camera.position.x - (layout.width / 2);
            float yP = camera.position.y;
            font.draw(batch, textPause, xP, yP);

            font.getData().setScale(1.5f);
            font.setColor(Color.WHITE);
        }
    }

    private void spawnMeteor() { meteorFactory.createMeteor(player); }

    private void restartGame() {
        player.reset();
        scoreManager.reset();
        List<Meteor> meteorsToRemove = new ArrayList<>(meteorPool.getActiveObjects());
        for (Meteor m : meteorsToRemove) meteorPool.free(m);
        List<Bullet> bulletsToRemove = new ArrayList<>(bulletPool.getActiveObjects());
        for (Bullet b : bulletsToRemove) bulletPool.free(b);
        meteorSpawnTimer = 0;
        currentState = GameState.PLAYING;
    }

    private void updateBullets(float delta) {
        List<Bullet> activeBullets = bulletPool.getActiveObjects();
        for (int i = 0; i < activeBullets.size(); i++) {
            Bullet b = activeBullets.get(i);
            b.update(delta);
            if (!b.isActive()) { bulletPool.free(b); i--; }
        }
    }

    private void updateMeteors(float delta) {
        List<Meteor> activeMeteors = meteorPool.getActiveObjects();
        for (int i = 0; i < activeMeteors.size(); i++) {
            Meteor m = activeMeteors.get(i);
            m.update(delta);
            if (!m.isActive()) { meteorPool.free(m); i--; }
        }
    }

    private void checkCollisions() {
        List<Bullet> bullets = bulletPool.getActiveObjects();
        List<Meteor> meteors = meteorPool.getActiveObjects();

        for (int i = 0; i < meteors.size(); i++) {
            Meteor m = meteors.get(i);
            if (m.getBounds().overlaps(player.getBounds())) {
                player.hit(); meteorPool.free(m); i--; continue;
            }
            for (int j = 0; j < bullets.size(); j++) {
                Bullet b = bullets.get(j);
                if (m.getBounds().overlaps(b.getBounds())) {
                    m.takeDamage(); bulletPool.free(b);
                    if (m.isDestroyed()) { notifyObservers("METEOR_DESTROYED"); meteorPool.free(m); i--; }
                    break;
                }
            }
        }
    }

    @Override
    public void addObserver(Observer observer) { observers.add(observer); }
    @Override
    public void removeObserver(Observer observer) { observers.remove(observer); }
    @Override
    public void notifyObservers(String event) { for (Observer observer : observers) observer.onNotify(event); }

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
