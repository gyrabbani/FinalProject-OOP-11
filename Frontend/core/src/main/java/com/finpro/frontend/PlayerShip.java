package com.finpro.frontend;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.finpro.frontend.pools.BulletPool;
import com.finpro.frontend.services.GameConfig;
import com.finpro.frontend.services.ResourceManager;
import com.finpro.frontend.strategies.SingleShotStrategy;
import com.finpro.frontend.strategies.WeaponStrategy;

public class PlayerShip extends BaseEntity {

    private Texture texture;
    private int lives;
    private BulletPool bulletPool;
    private WeaponStrategy currentWeapon;


    private float worldWidthLimit;
    private float worldHeightLimit;

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
        this.isActive = true;

        this.worldWidthLimit = GameConfig.SCREEN_WIDTH;
        this.worldHeightLimit = GameConfig.SCREEN_HEIGHT;
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
            currentWeapon.shoot(this.position.x, this.position.y, getWidth(), getHeight(), bulletPool);

        }
    }

    @Override
    public void update(float delta) {

        keepWithinScreen();
        updateBounds();
    }

    private void keepWithinScreen() {
        if (position.x < 0) position.x = 0;
        if (position.x > worldWidthLimit - getWidth()) position.x = worldWidthLimit - getWidth();
        if (position.y < 0) position.y = 0;
        if (position.y > worldHeightLimit - getHeight()) position.y = worldHeightLimit - getHeight();
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y, getWidth(), getHeight());
    }

    public void hit() { lives--; }
    public boolean isDead() { return lives <= 0; }
    public int getLives() { return lives; }

    public void reset() {
        this.lives = GameConfig.PLAYER_LIVES;
        this.isActive = true;
        this.position.set((worldWidthLimit / 2) - (getWidth() / 2), 50);
        updateBounds();
    }
}
