package com.finpro.frontend.strategies;

import com.finpro.frontend.PlayerShip;
import com.finpro.frontend.Bullet;
import com.finpro.frontend.pools.BulletPool;

public class SingleShotStrategy implements WeaponStrategy {

    @Override
    public void shoot(PlayerShip player) {
        BulletPool pool = player.getBulletPool();

        if (pool != null) {
            Bullet b = pool.obtain();

            float startX = player.getX() + (player.getWidth() / 2) - (b.getWidth() / 2);
            float startY = player.getY() + player.getHeight();

            b.init(startX, startY);
        }
    }
}