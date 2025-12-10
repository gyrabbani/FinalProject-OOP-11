package com.finpro.frontend.pools;

import com.finpro.frontend.Bullet;

public class BulletPool extends ObjectPool<Bullet> {
    @Override
    protected Bullet createObject() {
        return new Bullet();
    }
}

