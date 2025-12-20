package com.finpro.frontend.pools;

import com.finpro.frontend.obstacles.EnemyProjectile;

public class EnemyProjectilePool extends ObjectPool<EnemyProjectile> {
    @Override
    protected EnemyProjectile createObject() {
        return new EnemyProjectile();
    }
}
