package com.finpro.frontend.enemies;

import com.badlogic.gdx.math.MathUtils;
import com.finpro.frontend.PlayerShip;
import com.finpro.frontend.obstacles.Enemy;
import com.finpro.frontend.obstacles.EnemyProjectile;
import com.finpro.frontend.pools.EnemyProjectilePool;
import com.finpro.frontend.services.DifficultyManager;
import com.finpro.frontend.services.ResourceManager;

public class BigEnemyBehavior implements EnemyBehavior {
    private float rocketTimer, laserDelayTimer, laserActiveTimer;
    private boolean waitingForLaser;

    @Override
    public void init(Enemy enemy, PlayerShip player, EnemyProjectilePool pool, float worldWidth, float worldHeight) {
        enemy.setTexture(ResourceManager.getInstance().getTexture("enemy_big.png"));
        enemy.setShieldTexture(ResourceManager.getInstance().getTexture("shield.png"));
        enemy.setLaserTexture(ResourceManager.getInstance().getTexture("laser_tex.png"));
        enemy.setBoundsSize(80, 80);

        int hpMul = DifficultyManager.getInstance().getHpMultiplier();
        enemy.setHp(8 * hpMul);
        enemy.setShieldHp(5 * hpMul);
        enemy.setSpeed(DifficultyManager.getInstance().getEnemySpeed(Enemy.Type.BIG));

        rocketTimer = 0; laserDelayTimer = 0; laserActiveTimer = 0;
        waitingForLaser = false;
        enemy.setFiringLaser(false);
    }

    @Override
    public void update(Enemy enemy, float delta) {
        rocketTimer += delta;
        if (rocketTimer >= 7.0f) {
            rocketTimer = 0;
            fireTwinRockets(enemy);
            waitingForLaser = true;
            laserDelayTimer = 0;
        }
        if (waitingForLaser) {
            laserDelayTimer += delta;
            if (laserDelayTimer >= 3.0f) {
                waitingForLaser = false;
                enemy.setFiringLaser(true);
                laserActiveTimer = 0;
            }
        }
        if (enemy.isFiringLaser()) {
            laserActiveTimer += delta;
            if (laserActiveTimer >= 2.0f) {
                enemy.setFiringLaser(false);
            }
        }
    }

    private void fireTwinRockets(Enemy enemy) {
        for (int i = 0; i < 2; i++) {
            float rngX = enemy.getTargetPlayer().getX() + MathUtils.random(-150, 150);
            float rngY = enemy.getTargetPlayer().getY() + MathUtils.random(-50, 50);
            EnemyProjectile m = enemy.getProjectilePool().obtain();
            m.init(enemy.getX() + enemy.getWidth()/2 - 10, enemy.getY(), rngX, rngY,
                EnemyProjectile.Type.MISSILE, enemy.getWorldWidthLimit(), enemy.getWorldHeightLimit());
        }
    }

    @Override
    public void takeDamage(Enemy enemy) {
        if (enemy.getShieldHp() > 0) enemy.reduceShieldHp(1);
        else enemy.reduceHp(1);
    }
    @Override public int getScoreValue() { return 1000; }
}
