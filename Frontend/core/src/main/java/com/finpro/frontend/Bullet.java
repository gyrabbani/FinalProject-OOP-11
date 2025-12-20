package com.finpro.frontend;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.finpro.frontend.services.GameConfig;
import com.finpro.frontend.services.ResourceManager;

public class Bullet extends BaseEntity {

    private Vector2 velocity;
    private int damage;
    private Texture texture;

    public Bullet() {
        super(0, 0, GameConfig.BULLET_WIDTH, GameConfig.BULLET_HEIGHT);
        this.velocity = new Vector2();
        this.texture = ResourceManager.getInstance().getTexture("bullet.png");
        this.isActive = false;
    }

    public void init(float x, float y, float velX, float velY, int damage) {
        this.position.set(x, y);
        this.velocity.set(velX, velY);
        this.damage = damage;
        this.isActive = true;
        updateBounds();
    }

    @Override
    public void update(float delta) {
        if (!isActive) return;

        // Gerak berdasarkan vector velocity
        position.mulAdd(velocity, delta);
        updateBounds();

        // Cek keluar layar
        if (position.y > GameConfig.SCREEN_HEIGHT + 50 || position.y < -50) {
            isActive = false;
        }
    }

    public int getDamage() { return damage; }

    @Override
    public void render(SpriteBatch batch) {
        if (isActive) {
            batch.draw(texture, position.x, position.y, bounds.width, bounds.height);
        }
    }
}
