package com.finpro.frontend.strategies;
import com.finpro.frontend.pools.BulletPool;

public interface WeaponStrategy {
    void shoot(float x, float y, float width, float height, BulletPool pool, int damage);
}
