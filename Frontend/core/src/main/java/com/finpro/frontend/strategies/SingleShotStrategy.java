package com.finpro.frontend.strategies;
import com.finpro.frontend.Bullet;
import com.finpro.frontend.pools.BulletPool;
import com.finpro.frontend.services.GameConfig;

public class SingleShotStrategy implements WeaponStrategy {
    @Override
    public void shoot(float x, float y, float width, float height, BulletPool pool, int damage) {
        Bullet b = pool.obtain();
        b.init(x + width/2 - GameConfig.BULLET_WIDTH/2, y + height, 0, GameConfig.BULLET_SPEED, damage);
    }
}
