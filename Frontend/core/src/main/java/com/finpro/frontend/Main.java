package com.finpro.frontend;

import com.badlogic.gdx.Game;
import com.finpro.frontend.services.ResourceManager;
import com.finpro.frontend.states.MenuScreen;

public class Main extends Game {

    @Override
    public void create() {
        ResourceManager.getInstance().loadAll();

        this.setScreen(new MenuScreen());
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        ResourceManager.getInstance().dispose();
    }
}
