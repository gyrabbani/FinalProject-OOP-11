package com.finpro.frontend;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.finpro.frontend.BaseEntity;
import com.finpro.frontend.GameConfig;

public class Bullet extends BaseEntity {

    private Texture texture;

    public Bullet() {
        super(0, 0, GameConfig.BULLET_WIDTH, GameConfig.BULLET_HEIGHT);
        this.texture = ResourceManager.getInstance().getTexture("bullet.png");
        this.isActive = false;
    }

    public void init(float startX, float startY) {
        this.position.set(startX, startY);
        this.updateBounds();
        this.isActive = true;
    }

    @Override
    public void update(float delta) {
        if (!isActive) return;

        position.y += GameConfig.BULLET_SPEED * delta;
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

    private void createTexture() {
        Pixmap p = new Pixmap((int)bounds.width, (int)bounds.height, Pixmap.Format.RGBA8888);
        p.setColor(Color.YELLOW);
        p.fill();
        this.texture = new Texture(p);
        p.dispose();
    }
}
