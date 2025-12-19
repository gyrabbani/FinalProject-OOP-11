package com.finpro.frontend;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.finpro.frontend.services.ResourceManager;

public class Background {

    private Texture texture;
    private float yOffset;
    private float speed = 100f;

    public Background() {
        this.texture = ResourceManager.getInstance().getTexture("background");

        this.texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        this.yOffset = 0;
    }

    public void update(float delta) {
        float currentHeight = Gdx.graphics.getHeight();

        yOffset -= speed * delta;

        if (yOffset <= -currentHeight) {
            yOffset += currentHeight;
        }
    }

    public void render(SpriteBatch batch) {

        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        batch.draw(texture, 0, yOffset, width, height);

        batch.draw(texture, 0, yOffset + height, width, height);
    }
}
