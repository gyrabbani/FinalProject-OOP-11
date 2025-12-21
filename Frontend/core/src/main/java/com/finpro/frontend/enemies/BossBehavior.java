package com.finpro.frontend.enemies;

import com.finpro.frontend.PlayerShip;
import com.finpro.frontend.obstacles.Enemy;
import com.finpro.frontend.obstacles.EnemyProjectile;
import com.finpro.frontend.obstacles.EnemyProjectile.Type;
import com.finpro.frontend.pools.EnemyProjectilePool;
import com.finpro.frontend.services.DifficultyManager;
import com.finpro.frontend.services.ResourceManager;

public class BossBehavior implements EnemyBehavior {

    // Timer & Status
    private float rocketTimer, laserDelayTimer, laserActiveTimer;
    private boolean waitingForLaser;

    // Gerakan
    private float moveSpeed = 100f;
    private boolean movingRight = true;
    private boolean isEntering = true;
    private float fightPosY; // Batas Y untuk berhenti turun

    @Override
    public void init(Enemy enemy, PlayerShip player, EnemyProjectilePool pool, float worldWidth, float worldHeight) {
        // 1. SETUP VISUAL
        if (ResourceManager.getInstance().getTexture("boss.png") != null) {
            enemy.setTexture(ResourceManager.getInstance().getTexture("boss.png"));
        } else {
            enemy.setTexture(ResourceManager.getInstance().getTexture("enemy_big.png"));
        }
        enemy.setShieldTexture(ResourceManager.getInstance().getTexture("shield.png"));
        enemy.setLaserTexture(ResourceManager.getInstance().getTexture("laser_tex.png"));
        enemy.setBoundsSize(150, 150);

        // 2. SETUP STATS
        int hpMul = DifficultyManager.getInstance().getHpMultiplier();
        enemy.setHp(40 * hpMul);
        enemy.setShieldHp(30 * hpMul);
        enemy.setFiringLaser(false);

        // Kita tidak setPosition manual di sini supaya sama kyk enemy lain (ikut Factory).
        // Tapi kita tentukan titik henti (Fight Position) di dalam layar.
        this.fightPosY = worldHeight - 180;

        // Reset Variabel
        this.isEntering = true;
        this.rocketTimer = 0;
        this.laserDelayTimer = 0;
        this.laserActiveTimer = 0;
        this.waitingForLaser = false;

        System.out.println("BOSS INIT SUCCESS");
    }

    @Override
    public void update(Enemy enemy, float delta) {
        // Karena Enemy.java tidak menggerakkan BOSS, kita gerakkan manual di sini.
        if (isEntering) {
            enemy.setPosition(enemy.getX(), enemy.getY() - moveSpeed * delta);

            if (enemy.getY() <= fightPosY) {
                enemy.setPosition(enemy.getX(), fightPosY);
                isEntering = false;
            }
            return; // Jangan serang dulu pas lagi turun
        }

        // --- 2. GERAKAN PATROLI (KIRI-KANAN) ---
        float limitRight = enemy.getWorldWidthLimit() - enemy.getWidth();

        if (movingRight) {
            enemy.setPosition(enemy.getX() + moveSpeed * delta, enemy.getY());
            if (enemy.getX() >= limitRight) movingRight = false;
        } else {
            enemy.setPosition(enemy.getX() - moveSpeed * delta, enemy.getY());
            if (enemy.getX() <= 0) movingRight = true;
        }

        rocketTimer += delta;

        if (rocketTimer >= 4.0f && !waitingForLaser && !enemy.isFiringLaser()) {
            rocketTimer = 0;
            fireMissiles(enemy);
            waitingForLaser = true;
            laserDelayTimer = 0;
        }

        if (waitingForLaser) {
            laserDelayTimer += delta;
            if (laserDelayTimer >= 2.0f) {
                waitingForLaser = false;
                enemy.setFiringLaser(true);
                laserActiveTimer = 0;
            }
        }

        if (enemy.isFiringLaser()) {
            laserActiveTimer += delta;
            if (laserActiveTimer >= 2.5f) {
                enemy.setFiringLaser(false);
            }
        }
    }

    private void fireMissiles(Enemy enemy) {
        PlayerShip player = enemy.getTargetPlayer();
        if (player == null) return;

        EnemyProjectilePool pool = enemy.getProjectilePool();
        float startX = enemy.getX() + enemy.getWidth()/2;
        float startY = enemy.getY();

        spawnMissile(pool, startX, startY, player.getX(), player.getY(), enemy);
        spawnMissile(pool, startX, startY, player.getX()-150, player.getY(), enemy);
        spawnMissile(pool, startX, startY, player.getX()+150, player.getY(), enemy);
    }

    private void spawnMissile(EnemyProjectilePool pool, float x, float y, float tx, float ty, Enemy enemy) {
        EnemyProjectile p = pool.obtain();
        p.init(x, y, tx, ty, Type.MISSILE, enemy.getWorldWidthLimit(), enemy.getWorldHeightLimit());
    }

    @Override
    public void takeDamage(Enemy enemy) {
        if (enemy.getShieldHp() > 0) {
            enemy.reduceShieldHp(1);
        } else {
            enemy.reduceHp(1);
        }
    }

    @Override
    public int getScoreValue() { return 10000; }
}
