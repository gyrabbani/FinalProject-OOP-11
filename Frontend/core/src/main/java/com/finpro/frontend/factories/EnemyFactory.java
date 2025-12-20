package com.finpro.frontend.factories;

import com.badlogic.gdx.math.MathUtils;
import com.finpro.frontend.PlayerShip;
import com.finpro.frontend.obstacles.Enemy;
import com.finpro.frontend.pools.EnemyPool;
import com.finpro.frontend.pools.EnemyProjectilePool;
import com.finpro.frontend.services.DifficultyManager;

public class EnemyFactory {

    private final EnemyPool enemyPool;

    private float smallTimer;
    private float mediumTimer;
    private float bigTimer;
    private float bossTimer;

    public EnemyFactory(EnemyPool enemyPool) {
        this.enemyPool = enemyPool;
    }

    // Update dipanggil tiap frame
    public void update(float delta, float worldWidth, float worldHeight, PlayerShip player, EnemyProjectilePool projectilePool) {
        DifficultyManager dm = DifficultyManager.getInstance();

        smallTimer += delta;
        mediumTimer += delta;
        bigTimer += delta;
        bossTimer += delta;

        // Cek spawn SMALL
        if (smallTimer >= dm.getInterval(Enemy.Type.SMALL)) {
            spawnEnemy(Enemy.Type.SMALL, worldWidth, worldHeight, player, projectilePool);
            smallTimer = 0;
        }

        // Cek spawn MEDIUM
        if (mediumTimer >= dm.getInterval(Enemy.Type.MEDIUM)) {
            spawnEnemy(Enemy.Type.MEDIUM, worldWidth, worldHeight, player, projectilePool);
            mediumTimer = 0;
        }

        // Cek spawn BIG
        if (bigTimer >= dm.getInterval(Enemy.Type.BIG)) {
            spawnEnemy(Enemy.Type.BIG, worldWidth, worldHeight, player, projectilePool);
            bigTimer = 0;
        }

        // Cek spawn BOSS ( belum tau ada apa ngga )
        if (bossTimer >= dm.getInterval(Enemy.Type.BOSS)) {
            spawnEnemy(Enemy.Type.BOSS, worldWidth, worldHeight, player, projectilePool);
            bossTimer = 0;
        }
    }

    private void spawnEnemy(Enemy.Type type, float worldWidth, float worldHeight, PlayerShip player, EnemyProjectilePool pool) {
        Enemy enemy = enemyPool.obtain();
        float spawnX = MathUtils.random(0, worldWidth - 64);
        float spawnY = worldHeight + 50;
        enemy.init(spawnX, spawnY, type, player, pool, worldWidth, worldHeight);
    }
}
