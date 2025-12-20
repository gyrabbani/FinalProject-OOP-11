package com.finpro.frontend.obstacles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.finpro.frontend.BaseEntity;
import com.finpro.frontend.services.ResourceManager;

public class LootStar extends BaseEntity {
    private Texture texture;
    private float speed = 150f;

    public LootStar() {
        super(0, 0, 25, 25); // Ukuran bintang
        this.texture = ResourceManager.getInstance().getTexture("star.png");
        this.isActive = false;
    }

    public void init(float x, float y) {
        this.position.set(x, y);
        this.isActive = true;
        updateBounds();
    }

    @Override
    public void update(float delta) {
        if (!isActive) return;
        position.y -= speed * delta; // Jatuh ke bawah
        updateBounds();
        if (position.y < -50) isActive = false;
    }

    @Override
    public void render(SpriteBatch batch) {
        if (isActive) batch.draw(texture, position.x, position.y, bounds.width, bounds.height);
    }
}
