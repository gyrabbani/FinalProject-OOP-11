package com.finpro.frontend.pools;
import com.finpro.frontend.obstacles.LootStar;

public class LootPool extends ObjectPool<LootStar> {
    @Override
    protected LootStar createObject() {
        return new LootStar();
    }
}
