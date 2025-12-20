package com.finpro.frontend.obstacles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.finpro.frontend.BaseEntity;
import com.finpro.frontend.PlayerShip;
import com.finpro.frontend.pools.EnemyProjectilePool;
import com.finpro.frontend.services.GameConfig;
import com.finpro.frontend.enemies.*;

public class Enemy extends BaseEntity {

    public enum Type { SMALL, MEDIUM, BIG, BOSS }

    private EnemyBehavior behavior;

    private Texture texture;
    private Texture shieldTexture;
    private Texture laserTexture;
    private Type type;
    private int hp;
    private int shieldHp;
    private float speed;

    private Vector2 targetPosition = new Vector2();
    private Vector2 velocity = new Vector2();
    private boolean isEntering;
    private float minPatrolY, maxPatrolY;
    private float worldWidthLimit, worldHeightLimit;

    private PlayerShip targetPlayer;
    private EnemyProjectilePool projectilePool;

    private boolean isFiringLaser;

    public Enemy() {
        super(0, 0, 32, 32);
        this.isActive = false;
    }

    public void init(float startX, float startY, Type type, PlayerShip player, EnemyProjectilePool pool, float worldWidth, float worldHeight) {
        this.type = type;
        this.position.set(startX, startY);
        this.targetPlayer = player;
        this.projectilePool = pool;
        this.worldWidthLimit = worldWidth;
        this.worldHeightLimit = worldHeight;

        this.isActive = true;
        this.isEntering = true;
        this.isFiringLaser = false;

        switch (type) {
            case SMALL -> this.behavior = new SmallEnemyBehavior();
            case MEDIUM -> this.behavior = new MediumEnemyBehavior();
            case BIG -> this.behavior = new BigEnemyBehavior();
        }

        this.behavior.init(this, player, pool, worldWidth, worldHeight);

        this.maxPatrolY = worldHeight - 50;
        this.minPatrolY = worldHeight * 0.60f;

        updateBounds();
    }

    @Override
    public void update(float delta) {
        if (!isActive) return;


        // Logic Gerak Dasar (Entering & Patrol)
        // boss nya lom dibuat
        if (type != Type.BOSS) {
            if (isEntering) {
                position.y -= speed * delta;
                if (position.y <= maxPatrolY - 50) {
                    isEntering = false;
                    setRandomPatrolTarget();
                }
            } else if (!isFiringLaser) {
                moveToTarget(delta);
                if (position.dst(targetPosition) < 5f) setRandomPatrolTarget();
            }
        }

        // logika behavior (Nembak, Gerak Boss, dll)
        if (behavior != null) behavior.update(this, delta);

        updateBounds();
        if (position.y < -200) isActive = false;
    }

    public void moveToTarget(float delta) {
        velocity.set(targetPosition).sub(position).nor().scl(speed);
        position.mulAdd(velocity, delta);
    }

    public void setRandomPatrolTarget() {
        float minX = 0;
        float maxX = worldWidthLimit - bounds.width;
        targetPosition.set(MathUtils.random(minX, maxX), MathUtils.random(minPatrolY, maxPatrolY - bounds.height));
    }

    @Override
    public void render(SpriteBatch batch) {
        if (isActive) {
            if (hp <= 1 && type != Type.SMALL && shieldHp <= 0) batch.setColor(1f, 0.5f, 0.5f, 1f);

            if (texture != null) batch.draw(texture, position.x, position.y, bounds.width, bounds.height);
            batch.setColor(Color.WHITE);

            if (shieldHp > 0 && shieldTexture != null) {
                batch.setColor(1, 1, 1, 0.6f);
                batch.draw(shieldTexture, position.x - 5, position.y - 5, bounds.width + 10, bounds.height + 10);
                batch.setColor(Color.WHITE);
            }

            if (isFiringLaser && laserTexture != null) {
                float originX = position.x + bounds.width / 2 - 5;
                float width = (type == Type.BOSS) ? 20 : 10;
                float offset = (type == Type.BOSS) ? 10 : 0;
                batch.draw(laserTexture, originX - offset, 0, width + offset*2, position.y);
            }
        }
    }

    @Override
    public void takeDamage() {
        if (behavior != null) behavior.takeDamage(this);
    }

    public int getScoreValue() {
        return (behavior != null) ? behavior.getScoreValue() : 0;
    }

    public boolean isDestroyed() { return hp <= 0; }

    // getter setter
    public void setTexture(Texture t) { this.texture = t; }
    public void setShieldTexture(Texture t) { this.shieldTexture = t; }
    public void setLaserTexture(Texture t) { this.laserTexture = t; }
    public void setBoundsSize(float w, float h) { this.bounds.setSize(w, h); }
    public void setHp(int hp) { this.hp = hp; }
    public void reduceHp(int amount) { this.hp -= amount; }
    public void setShieldHp(int hp) { this.shieldHp = hp; }
    public void reduceShieldHp(int amount) { this.shieldHp -= amount; }
    public int getShieldHp() { return shieldHp; }
    public void setSpeed(float s) { this.speed = s; }
    public float getSpeed() { return speed; }
    public PlayerShip getTargetPlayer() { return targetPlayer; }
    public EnemyProjectilePool getProjectilePool() { return projectilePool; }
    public float getWorldWidthLimit() { return worldWidthLimit; }
    public float getWorldHeightLimit() { return worldHeightLimit; }
    public void setFiringLaser(boolean firing) { this.isFiringLaser = firing; }
    public boolean isFiringLaser() { return isFiringLaser; }
    public boolean isEntering() { return isEntering; }
    public void setX(float x) { this.position.x = x; }
}
