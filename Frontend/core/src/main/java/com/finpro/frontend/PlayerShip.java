package com.finpro.frontend;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.finpro.frontend.pools.BulletPool;
import com.finpro.frontend.services.GameConfig;
import com.finpro.frontend.services.ResourceManager;
import com.finpro.frontend.strategies.SingleShotStrategy;
import com.finpro.frontend.strategies.SpreadShotStrategy; // Pastikan sudah ada import ini
import com.finpro.frontend.strategies.WeaponStrategy;

public class PlayerShip extends BaseEntity {

    private Texture texture;
    private int lives;
    private BulletPool bulletPool;

    private int currentLevel = 1;
    private WeaponStrategy currentWeapon;
    private int currentDamage = 1;

    private float worldWidthLimit;
    private float worldHeightLimit;

    // kebal
    private float invulnerabilityTimer;
    private static final float INVULNERABILITY_DURATION = 3.0f;
    private boolean isInvulnerable;

    public PlayerShip() {
        super(
            (GameConfig.SCREEN_WIDTH / 2) - (GameConfig.PLAYER_WIDTH / 2),
            50,
            GameConfig.PLAYER_WIDTH,
            GameConfig.PLAYER_HEIGHT
        );

        this.texture = ResourceManager.getInstance().getTexture("playership.png");
        this.lives = GameConfig.PLAYER_LIVES;
        this.isActive = true;

        this.worldWidthLimit = GameConfig.SCREEN_WIDTH;
        this.worldHeightLimit = GameConfig.SCREEN_HEIGHT;

        this.invulnerabilityTimer = 0;
        this.isInvulnerable = false;

        setLevel(1);
    }

    public void levelUp() {
        if (currentLevel < 6) {
            setLevel(currentLevel + 1);

            // Tambah darah jika belum penuh saat naik level
            if (lives < GameConfig.PLAYER_LIVES) {
                lives++;
            }
            System.out.println("LEVEL UP! Current Level: " + currentLevel);
        } else {
            // Max Level Bonus (Heal)
            if (lives < GameConfig.PLAYER_LIVES) lives++;
            System.out.println("MAX LEVEL REACHED!");
        }
    }

    private void setLevel(int level) {
        this.currentLevel = level;

        // Logika Upgrade sesuai request (Texture tetap sama)
        switch (level) {
            case 1: // Default
                this.currentWeapon = new SingleShotStrategy();
                this.currentDamage = 1;
                break;
            case 2: // Spread Attack (3 peluru), Damage 1
                this.currentWeapon = new SpreadShotStrategy();
                this.currentDamage = 1;
                break;
            case 3: // Single Shot, Damage Naik jadi 2
                this.currentWeapon = new SingleShotStrategy();
                this.currentDamage = 2;
                break;
            case 4: // Spread Attack, Damage 2
                this.currentWeapon = new SpreadShotStrategy();
                this.currentDamage = 2;
                break;
            case 5: // Single Shot, Damage Naik jadi 3 (Max Dmg)
                this.currentWeapon = new SingleShotStrategy();
                this.currentDamage = 3;
                break;
            case 6: // MAX: Spread Attack, Damage 3
                this.currentWeapon = new SpreadShotStrategy();
                this.currentDamage = 3;
                break;
        }
    }

    public void updateLimits(float width, float height) {
        this.worldWidthLimit = width;
        this.worldHeightLimit = height;
    }

    public void setBulletPool(BulletPool pool) {
        this.bulletPool = pool;
    }

    public void move(float dirX, float dirY, float delta) {
        float speed = GameConfig.PLAYER_SPEED;
        position.x += dirX * speed * delta;
        position.y += dirY * speed * delta;
    }

    public void performShoot() {
        if (bulletPool != null) {
            currentWeapon.shoot(this.position.x, this.position.y, getWidth(), getHeight(), bulletPool, currentDamage);
            ResourceManager.getInstance().playSfx("shoot.wav");
        }
    }

    @Override
    public void update(float delta) {
        keepWithinScreen();
        updateBounds();

        // Timer Kebal
        if (isInvulnerable) {
            invulnerabilityTimer -= delta;
            if (invulnerabilityTimer <= 0) {
                isInvulnerable = false;
                invulnerabilityTimer = 0;
            }
        }
    }

    private void keepWithinScreen() {
        if (position.x < 0) position.x = 0;
        if (position.x > worldWidthLimit - getWidth()) position.x = worldWidthLimit - getWidth();
        if (position.y < 0) position.y = 0;
        if (position.y > worldHeightLimit - getHeight()) position.y = worldHeightLimit - getHeight();
    }

    @Override
    public void render(SpriteBatch batch) {
        // Efek ngeblink saat Kebal
        if (isInvulnerable) {
            if (invulnerabilityTimer % 0.2f < 0.1f) return;
            batch.setColor(1, 1, 1, 0.5f);
        }

        batch.draw(texture, position.x, position.y, getWidth(), getHeight());

        if (isInvulnerable) {
            batch.setColor(Color.WHITE);
        }
    }

    public void hit() {
        if (!isInvulnerable && lives > 0) {
            lives--;
            isInvulnerable = true;
            invulnerabilityTimer = INVULNERABILITY_DURATION;
            System.out.println("Player Hit! Lives left: " + lives);
            ResourceManager.getInstance().playSfx("explosion.wav");
        }
    }

    public boolean isDead() { return lives <= 0; }
    public int getLives() {
        return lives; }

    public void reset() {
        this.lives = GameConfig.PLAYER_LIVES;
        this.isActive = true;
        this.position.set((worldWidthLimit / 2) - (getWidth() / 2), 50);

        // Reset status kebal
        this.isInvulnerable = false;
        this.invulnerabilityTimer = 0;

        // Reset Level ke 1
        setLevel(1);

        updateBounds();
    }
}
