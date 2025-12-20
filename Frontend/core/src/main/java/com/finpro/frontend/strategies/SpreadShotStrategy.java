package com.finpro.frontend.strategies;

import com.finpro.frontend.Bullet;
import com.finpro.frontend.pools.BulletPool;
import com.finpro.frontend.services.GameConfig;

public class SpreadShotStrategy implements WeaponStrategy {
    @Override
    public void shoot(float x, float y, float width, float height, BulletPool pool, int damage) {
        float speed = GameConfig.BULLET_SPEED;
        float startX = x + width/2 - GameConfig.BULLET_WIDTH/2;
        float startY = y + height;

        // Tengah
        Bullet b1 = pool.obtain();
        b1.init(startX, startY, 0, speed, damage);

        // Kiri (Miring)
        Bullet b2 = pool.obtain();
        b2.init(startX, startY, -150f, speed * 0.9f, damage);

        // Kanan (Miring)
        Bullet b3 = pool.obtain();
        b3.init(startX, startY, 150f, speed * 0.9f, damage);
    }
}
