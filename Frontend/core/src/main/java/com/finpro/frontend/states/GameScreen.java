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
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.finpro.frontend.Background;
import com.finpro.frontend.PlayerShip;
import com.finpro.frontend.ScoreManager;
import com.finpro.frontend.commands.InputHandler;
import com.finpro.frontend.factories.EnemyFactory;
import com.finpro.frontend.factories.LootFactory;
import com.finpro.frontend.factories.MeteorFactory;
import com.finpro.frontend.Bullet;
import com.finpro.frontend.obstacles.Enemy;
import com.finpro.frontend.obstacles.EnemyProjectile;
import com.finpro.frontend.obstacles.Explosion;
import com.finpro.frontend.obstacles.LootStar;
import com.finpro.frontend.obstacles.Meteor;
import com.finpro.frontend.observer.Observer;
import com.finpro.frontend.observer.Subject;
import com.finpro.frontend.pools.*;
import com.finpro.frontend.services.DifficultyManager;
import com.finpro.frontend.services.GameConfig;
import com.finpro.frontend.services.ResourceManager;

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

    private EnemyPool enemyPool;
    private EnemyFactory enemyFactory;

    private EnemyProjectilePool enemyProjectilePool;
    private ExplosionPool explosionPool;

    private LootPool lootPool;
    private LootFactory lootFactory;
    private float lootSpawnTimer;

    private boolean gameOverSoundPlayed = false;
    private boolean gameOverTriggered = false;

    private int nextBossScore = 10000;
    private boolean isBossActive = false;


    public GameScreen() {
        this.batch = new SpriteBatch();
        this.currentState = GameState.PLAYING;

        this.camera = new OrthographicCamera();
        this.viewport = new ExtendViewport(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT, camera);
        this.viewport.apply();

        this.layout = new GlyphLayout();

        this.font = new BitmapFont();
        this.font.getData().setScale(1.5f);

        // Pools Setup
        this.bulletPool = new BulletPool();
        this.meteorPool = new MeteorPool();
        this.enemyPool = new EnemyPool();
        this.enemyProjectilePool = new EnemyProjectilePool();
        this.explosionPool = new ExplosionPool();

        this.lootPool = new LootPool();

        // Factories
        this.meteorFactory = new MeteorFactory(meteorPool);
        this.enemyFactory = new EnemyFactory(enemyPool);

        this.lootFactory = new LootFactory(lootPool);

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

        // Render Projectiles dan Explosions
        for (EnemyProjectile p : enemyProjectilePool.getActiveObjects()) p.render(batch);
        for (Explosion ex : explosionPool.getActiveObjects()) ex.render(batch);

        // Render LootStar
        for (LootStar star : lootPool.getActiveObjects()) star.render(batch);

        for (Bullet b : bulletPool.getActiveObjects()) b.render(batch);
        for (Meteor m : meteorPool.getActiveObjects()) m.render(batch);
        for (Enemy e : enemyPool.getActiveObjects()) e.render(batch);

        if (player != null) player.render(batch);

        // UI Rendering
        if (player != null) {
            for (int i = 0; i < player.getLives(); i++) {
                batch.draw(heartTexture, 20 + (i * 30), viewport.getWorldHeight() - 50, 20, 20);
            }
        }

        Color oldColor = font.getColor();
        font.setColor(Color.WHITE);
        font.draw(batch, "Score: " + scoreManager.getScore(), 20, 70);

        String difficultyText = "Difficulty: " + DifficultyManager.getInstance().getCurrentLevel();
        if (DifficultyManager.getInstance().getCurrentLevel() == DifficultyManager.DifficultyLevel.HARD) {
            font.setColor(Color.RED);
        } else if (DifficultyManager.getInstance().getCurrentLevel() == DifficultyManager.DifficultyLevel.MEDIUM) {
            font.setColor(Color.YELLOW);
        } else {
            font.setColor(Color.GREEN);
        }
        font.draw(batch, difficultyText, 20, 40);
        font.setColor(oldColor);

        drawOverlayText();
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    private void updatePlaying(float delta) {
        inputHandler.handleInput(player, delta);
        if (player != null) {
            player.updateLimits(viewport.getWorldWidth(), viewport.getWorldHeight());
            player.update(delta);
        }
        if (background != null) background.update(delta);

        DifficultyManager.getInstance().updateDifficulty(scoreManager.getScore());


        if (scoreManager.getScore() >= nextBossScore && !isBossActive) {
            enemyFactory.spawnEnemy(Enemy.Type.BOSS, viewport.getWorldWidth(), viewport.getWorldHeight(), player, enemyProjectilePool);

            nextBossScore += 10000;
            isBossActive = true;

            System.out.println("BOSS SPAWNED! Score Freeze Active.");
        }
        // Spawn Meteors
        meteorSpawnTimer += delta;
        if (meteorSpawnTimer > DifficultyManager.getInstance().getSpawnInterval()) {
            meteorSpawnTimer = 0;
            meteorFactory.createMeteor(player, viewport.getWorldWidth(), viewport.getWorldHeight());
        }

        // Spawn Enemies
        enemyFactory.update(delta, viewport.getWorldWidth(), viewport.getWorldHeight(), player, enemyProjectilePool);

        // Spawn Natural LootStar ( tiap 20 detik spawn random dari atas )
        lootSpawnTimer += delta;
        if (lootSpawnTimer > 20.0f) {
            lootSpawnTimer = 0;
            lootFactory.createRandomLoot(viewport.getWorldWidth(), viewport.getWorldHeight());
        }

        // loot movement
        List<LootStar> activeLoot = lootPool.getActiveObjects();
        for (int i = 0; i < activeLoot.size(); i++) {
            LootStar star = activeLoot.get(i);
            star.update(delta);
            if (!star.isActive()) {
                lootPool.free(star);
                i--;
            }
        }

        updateBullets(delta);
        updateMeteors(delta);
        updateEnemies(delta);
        updateEnemyProjectiles(delta);
        updateExplosions(delta);
        checkCollisions();

        if (player.isDead() && !gameOverTriggered) {
            gameOverTriggered = true;
            currentState = GameState.GAME_OVER;
            ResourceManager.getInstance().stopMusic();
            ResourceManager.getInstance().playSfx("gameover.wav");
        }
    }

    private void updateEnemies(float delta) {
        List<Enemy> activeEnemies = enemyPool.getActiveObjects();
        for (int i = 0; i < activeEnemies.size(); i++) {
            Enemy e = activeEnemies.get(i);
            e.update(delta);
            if (!e.isActive()) { enemyPool.free(e); i--; }
        }
    }
    private void updateEnemyProjectiles(float delta) {
        List<EnemyProjectile> projectiles = enemyProjectilePool.getActiveObjects();
        for (int i = 0; i < projectiles.size(); i++) {
            EnemyProjectile p = projectiles.get(i);
            p.update(delta);
            if (p.getType() == EnemyProjectile.Type.MISSILE && p.hasReachedTarget()) {
                spawnExplosion(p.getX() + p.getWidth()/2, p.getY() + p.getHeight()/2);
                ResourceManager.getInstance().playSfx("explosion.wav");
                p.setActive(false);
            }
            if (!p.isActive()) { enemyProjectilePool.free(p); i--; }
        }
    }
    private void updateExplosions(float delta) {
        List<Explosion> explosions = explosionPool.getActiveObjects();
        for (int i = 0; i < explosions.size(); i++) {
            Explosion ex = explosions.get(i);
            ex.update(delta);
            if (!ex.isActive()) { explosionPool.free(ex); i--; }
        }
    }

    private void checkCollisions() {
        List<Bullet> bullets = bulletPool.getActiveObjects();
        List<Meteor> meteors = meteorPool.getActiveObjects();
        List<Enemy> enemies = enemyPool.getActiveObjects();
        List<EnemyProjectile> projectiles = enemyProjectilePool.getActiveObjects();
        List<LootStar> lootStars = lootPool.getActiveObjects();

        for (LootStar star : lootStars) {
            if (star.getBounds().overlaps(player.getBounds())) {
                player.levelUp(); // Naik level senjata tetap jalan
                star.setActive(false);

                // --- UPDATE: Cek Boss Active Dulu ---
                if (!isBossActive) {
                    scoreManager.addScore(500); // Skor cuma nambah kalau GAK ADA Boss
                }

                ResourceManager.getInstance().playSfx("pickup.mp3");
            }
        }

        for (Meteor m : meteors) {
            if (m.getBounds().overlaps(player.getBounds())) {
                player.hit();
                m.setActive(false);
            }
            for (Bullet b : bullets) {
                if (m.getBounds().overlaps(b.getBounds())) {
                    m.takeDamage();
                    b.setActive(false);
                    if (m.isDestroyed()) {
                        notifyObservers("METEOR_DESTROYED");
                        m.setActive(false);
                        ResourceManager.getInstance().playSfx("explosion.wav");
                    }
                }
            }
        }

        // COLLISION ENEMY
        for (Enemy e : enemies) {
            // Player nabrak Musuh
            if (e.getBounds().overlaps(player.getBounds())) {
                player.hit();
                // Kalau bukan Boss, musuh hancur. Boss tetap hidup.
                if (e.getType() != Enemy.Type.BOSS) {
                    e.setActive(false);
                }
            }

            // Kena Laser Musuh
            if (e.isFiringLaser()) {
                float laserX = e.getX() + e.getWidth() / 2;
                if (player.getX() < laserX + 5 && player.getX() + player.getWidth() > laserX - 5) {
                    if (player.getY() < e.getY()) {
                        player.hit();
                    }
                }
            }

            // Bullet kena Musuh
            for (Bullet b : bullets) {
                if (b.isActive() && e.getBounds().overlaps(b.getBounds())) {
                    e.takeDamage();
                    b.setActive(false);

                    if (e.isDestroyed()) {
                        ResourceManager.getInstance().playSfx("explosion.wav");

                        if (e.getType() == Enemy.Type.BOSS) {
                            notifyObservers("ENEMY_DESTROYED");

                            isBossActive = false;
                            System.out.println("BOSS DEFEATED! No Score Awarded. Resume Normal Game.");

                        } else {
                            if (!isBossActive) {
                                notifyObservers("ENEMY_DESTROYED");
                                scoreManager.addScore(e.getScoreValue());
                            }
                        }

                        if (MathUtils.randomBoolean(0.3f)) {
                            lootFactory.createLoot(e.getX(), e.getY());
                        }

                        e.setActive(false);
                    }
                }
            }
        }

        for (EnemyProjectile ep : projectiles) {
            if (ep.getBounds().overlaps(player.getBounds())) {
                player.hit();
                if (ep.getType() == EnemyProjectile.Type.MISSILE) {
                    spawnExplosion(ep.getX(), ep.getY());
                }
                ep.setActive(false);
            }
        }
    }

    private void spawnExplosion(float x, float y) {
        Explosion ex = explosionPool.obtain();
        ex.init(x, y);
    }

    private void restartGame() {
        player.reset();
        clearPool(meteorPool);
        clearPool(enemyPool);
        clearPool(bulletPool);
        clearPool(enemyProjectilePool);
        clearPool(explosionPool);

        //clear loot pas restart
        clearPool(lootPool);

        meteorSpawnTimer = 0;
        lootSpawnTimer = 0;

        nextBossScore = 10000;
        isBossActive = false;

        currentState = GameState.PLAYING;
        gameOverTriggered = false;
        DifficultyManager.getInstance().updateDifficulty(0);
        scoreManager.reset();

        ResourceManager.getInstance().playMusic("gameplay.mp3", true);
    }

    private <T> void clearPool(ObjectPool<T> pool) {
        List<T> active = new ArrayList<>(pool.getActiveObjects());
        for (T obj : active) pool.free(obj);
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
    private void handleGlobalInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (currentState == GameState.PLAYING) currentState = GameState.PAUSED;
            else if (currentState == GameState.PAUSED) currentState = GameState.PLAYING;
        }
    }
    private void drawOverlayText() {
        if (currentState == GameState.GAME_OVER) {
            font.setColor(Color.RED);
            font.getData().setScale(2f);
            String text1 = "GAME OVER";
            layout.setText(font, text1);
            font.draw(batch, text1, camera.position.x - layout.width / 2, camera.position.y + 50);
            font.setColor(Color.WHITE);
            font.getData().setScale(1.5f);
            String text2 = "Press SPACE to Restart";
            layout.setText(font, text2);
            font.draw(batch, text2, camera.position.x - layout.width / 2, camera.position.y - 10);
            font.setColor(Color.YELLOW);
            String text3 = "Press M for Menu";
            layout.setText(font, text3);
            font.draw(batch, text3, camera.position.x - layout.width / 2, camera.position.y - 50);

            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) restartGame();
            if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
                ResourceManager.getInstance().playSfx("click.wav");
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MenuScreen());
            }
        } else if (currentState == GameState.PAUSED) {
            font.setColor(Color.YELLOW);
            font.getData().setScale(2f);
            layout.setText(font, "PAUSED");
            font.draw(batch, "PAUSED", camera.position.x - layout.width / 2, camera.position.y);
        }
    }
    @Override public void addObserver(Observer o) { observers.add(o); }
    @Override public void removeObserver(Observer o) { observers.remove(o); }
    @Override public void notifyObservers(String e) { for(Observer o: observers) o.onNotify(e); }
    @Override public void show() { ResourceManager.getInstance().playMusic("gameplay.mp3", true);}
    @Override public void pause() { if(currentState == GameState.PLAYING) currentState = GameState.PAUSED; }
    @Override public void resume() { if(currentState == GameState.PAUSED) currentState = GameState.PLAYING; }
    @Override public void hide() { ResourceManager.getInstance().stopMusic();}
    @Override public void dispose() { batch.dispose(); font.dispose(); heartTexture.dispose(); }
}
