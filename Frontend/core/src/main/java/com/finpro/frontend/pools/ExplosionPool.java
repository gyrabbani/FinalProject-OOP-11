package com.finpro.frontend.pools;

import com.finpro.frontend.obstacles.Explosion;

public class ExplosionPool extends ObjectPool<Explosion> {
    @Override
    protected Explosion createObject() {
        return new Explosion();
    }
}
