package com.finpro.frontend.enemies;

import com.finpro.frontend.PlayerShip;
import com.finpro.frontend.obstacles.Enemy;
import com.finpro.frontend.pools.EnemyProjectilePool;

public interface EnemyBehavior {
    void init(Enemy enemy, PlayerShip player, EnemyProjectilePool pool, float worldWidth, float worldHeight);
    void update(Enemy enemy, float delta);
    void takeDamage(Enemy enemy);
    int getScoreValue();
}
