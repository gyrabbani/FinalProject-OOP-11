package com.finpro.frontend.pools;

import com.finpro.frontend.obstacles.Enemy;

public class EnemyPool extends ObjectPool<Enemy> {
    @Override
    protected Enemy createObject() {
        return new Enemy();
    }
}
