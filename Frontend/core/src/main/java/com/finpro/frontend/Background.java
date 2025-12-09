package com.finpro.frontend;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.finpro.frontend.GameConfig;
import com.finpro.frontend.ResourceManager;

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
        yOffset -= speed * delta;

        if (yOffset <= -GameConfig.SCREEN_HEIGHT) {
            yOffset += GameConfig.SCREEN_HEIGHT;
        }
    }

    public void render(SpriteBatch batch) {
        float width = GameConfig.SCREEN_WIDTH;
        float height = GameConfig.SCREEN_HEIGHT;

        batch.draw(texture, 0, yOffset, width, height);

        batch.draw(texture, 0, yOffset + height, width, height);
    }
}
