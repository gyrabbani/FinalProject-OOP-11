package com.finpro.frontend.enemies;

import com.finpro.frontend.PlayerShip;
import com.finpro.frontend.obstacles.Enemy;
import com.finpro.frontend.obstacles.EnemyProjectile;
import com.finpro.frontend.pools.EnemyProjectilePool;
import com.finpro.frontend.services.DifficultyManager;
import com.finpro.frontend.services.ResourceManager;

public class SmallEnemyBehavior implements EnemyBehavior {
    private float fireTimer;

    @Override
    public void init(Enemy enemy, PlayerShip player, EnemyProjectilePool pool, float worldWidth, float worldHeight) {
        enemy.setTexture(ResourceManager.getInstance().getTexture("enemy_small.png"));
        enemy.setBoundsSize(32, 32);

        int hpMul = DifficultyManager.getInstance().getHpMultiplier();
        enemy.setHp(1 * hpMul);
        enemy.setShieldHp(0);
        enemy.setSpeed(DifficultyManager.getInstance().getEnemySpeed(Enemy.Type.SMALL));

        fireTimer = 0;
    }

    @Override
    public void update(Enemy enemy, float delta) {
        float fireRate = DifficultyManager.getInstance().getEnemyFireRate();
        fireTimer += delta;
        if (fireTimer >= fireRate) {
            fireTimer = 0;
            shoot(enemy);
        }
    }

    private void shoot(Enemy enemy) {
        EnemyProjectile b = enemy.getProjectilePool().obtain();
        b.init(enemy.getX() + enemy.getWidth()/2 - 5, enemy.getY(),
            enemy.getX() + enemy.getWidth()/2 - 5, enemy.getY() - 100,
            EnemyProjectile.Type.BULLET, enemy.getWorldWidthLimit(), enemy.getWorldHeightLimit());
    }

    @Override
    public void takeDamage(Enemy enemy) { enemy.reduceHp(1); }
    @Override
    public int getScoreValue() { return 100; }
}
