package com.finpro.frontend.factories;

import com.badlogic.gdx.math.MathUtils;
import com.finpro.frontend.PlayerShip;
import com.finpro.frontend.obstacles.Meteor;
import com.finpro.frontend.pools.MeteorPool;

public class MeteorFactory {

    private MeteorPool meteorPool;

    public MeteorFactory(MeteorPool meteorPool) {
        this.meteorPool = meteorPool;
    }

    public void createMeteor(PlayerShip player, float worldWidth, float worldHeight) {
        Meteor meteor = meteorPool.obtain();

        float spawnX = MathUtils.random(0, worldWidth);
        float spawnY = worldHeight + 50;
        float targetX = player.getX();
        float targetY = player.getY();

        meteor.init(spawnX, spawnY, targetX, targetY, worldWidth, worldHeight);
    }
}
