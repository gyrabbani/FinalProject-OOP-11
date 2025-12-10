package com.finpro.frontend;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.finpro.frontend.pools.BulletPool;
import com.finpro.frontend.strategies.SingleShotStrategy;
import com.finpro.frontend.strategies.WeaponStrategy;

public class PlayerShip extends BaseEntity {

    private Texture texture;
    private WeaponStrategy currentWeapon;
    private BulletPool bulletPool;
    private int lives;

    public PlayerShip() {
        super(
            (GameConfig.SCREEN_WIDTH / 2) - (GameConfig.PLAYER_WIDTH / 2),
            50,
            GameConfig.PLAYER_WIDTH,
            GameConfig.PLAYER_HEIGHT
        );

        this.texture = ResourceManager.getInstance().getTexture("playership.png");
        this.currentWeapon = new SingleShotStrategy();
        this.lives = GameConfig.PLAYER_LIVES;
    }

    public void reset() {
        this.lives = GameConfig.PLAYER_LIVES;

        setPosition(
            (GameConfig.SCREEN_WIDTH / 2) - (GameConfig.PLAYER_WIDTH / 2),
            50
        );
    }

    public void hit() {
        if (lives > 0) {
            lives--;
        }
    }

    public int getLives() {
        return lives;
    }

    public boolean isDead() {
        return lives <= 0;
    }

    public void setBulletPool(BulletPool pool) {
        this.bulletPool = pool;
    }

    public BulletPool getBulletPool() {
        return this.bulletPool;
    }

    @Override
    public void update(float delta) {
        keepWithinScreen();
        updateBounds();
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!isDead()) {
            batch.draw(texture, position.x, position.y, bounds.width, bounds.height);
        }
    }

    public void move(float dirX, float dirY, float delta) {
        if (isDead()) return;
        position.x += dirX * GameConfig.PLAYER_SPEED * delta;
        position.y += dirY * GameConfig.PLAYER_SPEED * delta;
    }

    public void performShoot() {
        if (!isDead() && currentWeapon != null && bulletPool != null) {
            currentWeapon.shoot(this);
        }
    }

    public void setWeaponStrategy(WeaponStrategy weapon) {
        this.currentWeapon = weapon;
    }

    private void keepWithinScreen() {
        if (position.x < 0) position.x = 0;
        if (position.x > GameConfig.SCREEN_WIDTH - bounds.width)
            position.x = GameConfig.SCREEN_WIDTH - bounds.width;

        if (position.y < 0) position.y = 0;
        if (position.y > GameConfig.SCREEN_HEIGHT - bounds.height)
            position.y = GameConfig.SCREEN_HEIGHT - bounds.height;
    }
}
