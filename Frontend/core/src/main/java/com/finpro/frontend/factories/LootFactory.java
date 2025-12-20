package com.finpro.frontend.factories;
import com.badlogic.gdx.math.MathUtils;
import com.finpro.frontend.obstacles.LootStar;
import com.finpro.frontend.pools.LootPool;

public class LootFactory {
    private LootPool pool;

    public LootFactory(LootPool pool) { this.pool = pool; }

    // Drop dari musuh ( tinggal di balancing aja )
    public void createLoot(float x, float y) {
        LootStar star = pool.obtain();
        star.init(x, y);
    }

    // Spawn natural
    public void createRandomLoot(float worldWidth, float worldHeight) {
        LootStar star = pool.obtain();
        float x = MathUtils.random(20, worldWidth - 40);
        star.init(x, worldHeight + 20);
    }
}
