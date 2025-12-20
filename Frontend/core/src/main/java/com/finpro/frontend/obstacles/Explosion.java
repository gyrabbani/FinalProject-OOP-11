package com.finpro.frontend.obstacles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.finpro.frontend.BaseEntity;
import com.finpro.frontend.services.ResourceManager;

public class Explosion extends BaseEntity {
    private float timer;
    private float maxDuration = 0.5f; // Durasi ledakan
    private Texture texture;

    public Explosion() {
        super(0, 0, 50, 50); // AoE Size
        this.texture = ResourceManager.getInstance().getTexture("explosion.png");
        isActive = false;
    }

    public void init(float x, float y) {
        // Center explosion
        this.position.set(x - 40, y - 40);
        this.timer = 0;
        this.isActive = true;
        updateBounds();
    }

    @Override
    public void update(float delta) {
        if (!isActive) return;
        timer += delta;
        if (timer >= maxDuration) isActive = false;
    }

    @Override
    public void render(SpriteBatch batch) {
        if (isActive) {
            // Efek fade out
            float alpha = 1.0f - (timer / maxDuration);
            batch.setColor(1, 1, 1, alpha);
            batch.draw(texture, position.x, position.y, bounds.width, bounds.height);
            batch.setColor(1, 1, 1, 1);
        }
    }
}
