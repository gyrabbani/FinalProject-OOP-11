package com.finpro.frontend.enemies;

import com.finpro.frontend.PlayerShip;
import com.finpro.frontend.obstacles.Enemy;
import com.finpro.frontend.obstacles.EnemyProjectile;
import com.finpro.frontend.pools.EnemyProjectilePool;
import com.finpro.frontend.services.DifficultyManager;
import com.finpro.frontend.services.ResourceManager;

public class MediumEnemyBehavior implements EnemyBehavior {
    private float fireTimer;

    @Override
    public void init(Enemy enemy, PlayerShip player, EnemyProjectilePool pool, float worldWidth, float worldHeight) {
        enemy.setTexture(ResourceManager.getInstance().getTexture("enemy_medium.png"));
        enemy.setBoundsSize(48, 48);

        int hpMul = DifficultyManager.getInstance().getHpMultiplier();
        enemy.setHp(3 * hpMul);
        enemy.setShieldHp(0);
        enemy.setSpeed(DifficultyManager.getInstance().getEnemySpeed(Enemy.Type.MEDIUM));
        fireTimer = 0;
    }

    @Override
    public void update(Enemy enemy, float delta) {
        fireTimer += delta;
        if (fireTimer >= 3.0f) {
            fireTimer = 0;
            shootMissile(enemy);
        }
    }

    private void shootMissile(Enemy enemy) {
        EnemyProjectile m = enemy.getProjectilePool().obtain();
        m.init(enemy.getX() + enemy.getWidth()/2 - 10, enemy.getY(),
            enemy.getTargetPlayer().getX(), enemy.getTargetPlayer().getY(),
            EnemyProjectile.Type.MISSILE, enemy.getWorldWidthLimit(), enemy.getWorldHeightLimit());
    }

    @Override public void takeDamage(Enemy enemy) { enemy.reduceHp(1); }
    @Override public int getScoreValue() { return 300; }
}
