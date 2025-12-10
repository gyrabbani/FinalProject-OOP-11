package com.finpro.frontend.pools;

import com.finpro.frontend.obstacles.Meteor;

public class MeteorPool extends ObjectPool<Meteor> {
    @Override
    protected Meteor createObject() {
        return new Meteor();
    }
}

