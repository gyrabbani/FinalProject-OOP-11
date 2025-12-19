package com.finpro.frontend;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.finpro.frontend.BaseEntity;
import com.finpro.frontend.services.GameConfig;
import com.finpro.frontend.services.ResourceManager;

public class Bullet extends BaseEntity {

    private float speed;
    private Texture texture;

    public Bullet() {
        super(0, 0, GameConfig.BULLET_WIDTH, GameConfig.BULLET_HEIGHT);

        this.texture = ResourceManager.getInstance().getTexture("bullet.png");

        this.speed = GameConfig.BULLET_SPEED;
        this.isActive = false;
    }

    public void init(float startX, float startY) {
        this.position.set(startX, startY);
        this.isActive = true;
        updateBounds();
    }

    @Override
    public void update(float delta) {
        if (!isActive) return;

        position.y += speed * delta;
        updateBounds();

        if (position.y > GameConfig.SCREEN_HEIGHT) {
            isActive = false;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (isActive) {
            batch.draw(texture, position.x, position.y, bounds.width, bounds.height);
        }
    }

}
