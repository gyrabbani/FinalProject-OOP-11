package com.finpro.frontend.factories;

import com.badlogic.gdx.math.MathUtils;
import com.finpro.frontend.PlayerShip;
import com.finpro.frontend.obstacles.Meteor;
import com.finpro.frontend.pools.MeteorPool;
import com.finpro.frontend.services.GameConfig;

public class MeteorFactory {

    private MeteorPool meteorPool;

    public MeteorFactory(MeteorPool meteorPool) {
        this.meteorPool = meteorPool;
    }

    public Meteor createMeteor(PlayerShip targetPlayer) {
        Meteor m = meteorPool.obtain();


        float startX = MathUtils.random(0, GameConfig.SCREEN_WIDTH - GameConfig.METEOR_WIDTH);
        float startY = GameConfig.SCREEN_HEIGHT;

        float targetX = targetPlayer.getX();
        float targetY = targetPlayer.getY();

        m.init(startX, startY, targetX, targetY);

        return m;
    }
}
