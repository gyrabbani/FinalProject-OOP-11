package com.finpro.frontend.strategies;

import com.finpro.frontend.Bullet;
import com.finpro.frontend.pools.BulletPool;

public class SingleShotStrategy implements WeaponStrategy {

    @Override
    public void shoot(float x, float y, float width, float height, BulletPool bulletPool) {
        Bullet b = bulletPool.obtain();

        float bulletX = x + (width / 2) - (b.getWidth() / 2);
        float bulletY = y + height;

        b.init(bulletX, bulletY);
    }
}
