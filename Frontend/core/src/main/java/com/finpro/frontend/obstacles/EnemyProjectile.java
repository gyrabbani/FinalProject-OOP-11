package com.finpro.frontend.obstacles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.finpro.frontend.BaseEntity;
import com.finpro.frontend.services.ResourceManager;

public class EnemyProjectile extends BaseEntity {

    public enum Type { BULLET, MISSILE }

    private Type type;
    private Vector2 velocity;
    private Vector2 targetPos; // Lokasi tujuan misil (titik ledak)
    private Texture texture;
    private boolean isExplosive;
    private boolean reachedTarget; // Status apakah sudah sampai tujuan

    private float worldWidthLimit;
    private float worldHeightLimit;

    public EnemyProjectile() {
        super(0, 0, 10, 20);
        velocity = new Vector2();
        targetPos = new Vector2();
        isActive = false;
    }

    public void init(float x, float y, float targetX, float targetY, Type type, float worldWidth, float worldHeight) {
        this.type = type;
        this.position.set(x, y);
        this.targetPos.set(targetX, targetY);
        this.isActive = true;
        this.reachedTarget = false;

        this.worldWidthLimit = worldWidth;
        this.worldHeightLimit = worldHeight;

        if (type == Type.BULLET) {
            this.texture = ResourceManager.getInstance().getTexture("projectile_enemy.png");
            this.bounds.setSize(10, 20);
            this.isExplosive = false;
            this.velocity.set(0, -400f);
        } else {
            // MISSILE logic
            this.texture = ResourceManager.getInstance().getTexture("missile.png");
            this.bounds.setSize(20, 40);
            this.isExplosive = true;

            Vector2 direction = new Vector2(targetX - x, targetY - y).nor();
            float missileSpeed = 300f;
            this.velocity.set(direction).scl(missileSpeed);
        }
        updateBounds();
    }

    @Override
    public void update(float delta) {
        if (!isActive) return;

        if (type == Type.MISSILE) {

            float distToTarget = position.dst(targetPos);
            float step = velocity.len() * delta;


            if (distToTarget <= step) {
                position.set(targetPos);
                reachedTarget = true;
            } else {
                // Belum sampai, jalan terus
                position.mulAdd(velocity, delta);
            }
        } else {
            // Bullet biasa
            position.mulAdd(velocity, delta);
        }

        updateBounds();

        if (!reachedTarget && (position.y < -50 || position.y > worldHeightLimit + 50 ||
            position.x < -50 || position.x > worldWidthLimit + 50)) {
            isActive = false;
        }
    }

    public boolean isExplosive() { return isExplosive; }
    public boolean hasReachedTarget() { return reachedTarget; }
    public Type getType() { return type; }

    @Override
    public void render(SpriteBatch batch) {
        if (isActive) {
            // Rotasi visual misil biar ujungnya ngadep sesuai arah
            float rotation = 0;
            if (type == Type.MISSILE) {
                rotation = velocity.angleDeg() - 90;
            }

            // Draw pake parameter rotasi
            batch.draw(texture,
                position.x, position.y,
                bounds.width / 2, bounds.height / 2,
                bounds.width, bounds.height,
                1, 1,
                rotation,
                0, 0, texture.getWidth(), texture.getHeight(), false, false);
        }
    }
}
