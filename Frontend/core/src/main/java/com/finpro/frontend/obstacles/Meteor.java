package com.finpro.frontend.obstacles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.finpro.frontend.BaseEntity;
import com.finpro.frontend.services.DifficultyManager;
import com.finpro.frontend.services.GameConfig;
import com.finpro.frontend.services.ResourceManager;

public class Meteor extends BaseEntity {

    private Texture texture;
    private Vector2 velocity;
    private int hp;

    // Variabel untuk menyimpan batas dunia yang dinamis
    private float worldWidthLimit;
    private float worldHeightLimit;

    public Meteor() {
        super(0, 0, GameConfig.METEOR_WIDTH, GameConfig.METEOR_HEIGHT);
        this.texture = ResourceManager.getInstance().getTexture("meteor.png");
        this.velocity = new Vector2(0, 0);
        this.isActive = false;
    }

    // UPDATE: Menerima worldWidth dan worldHeight saat inisialisasi
    public void init(float startX, float startY, float targetX, float targetY, float worldWidth, float worldHeight) {
        this.position.set(startX, startY);
        this.updateBounds();
        this.isActive = true;
        this.hp = 2;

        // Simpan batas dunia agar tahu kapan harus dihapus (Culling) yang benar
        this.worldWidthLimit = worldWidth;
        this.worldHeightLimit = worldHeight;

        Vector2 direction = new Vector2(targetX - startX, targetY - startY);
        direction.x += MathUtils.random(-100, 100);

        float minSpeed = DifficultyManager.getInstance().getMinMeteorSpeed();
        float maxSpeed = DifficultyManager.getInstance().getMaxMeteorSpeed();
        float speed = MathUtils.random(minSpeed, maxSpeed);
        this.velocity = direction.nor().scl(speed);
    }

    // Override dari BaseEntity
    @Override
    public void takeDamage() {
        hp--;
    }

    public boolean isDestroyed() {
        return hp <= 0;
    }

    @Override
    public void update(float delta) {
        if (!isActive) return;

        position.mulAdd(velocity, delta);
        updateBounds();

        // FIX: Gunakan worldWidthLimit, bukan GameConfig.SCREEN_WIDTH
        // Ini memastikan meteor tidak hilang tiba-tiba saat di area kanan layar lebar
        if (position.y < -getHeight() ||
            position.x < -getWidth() ||
            position.x > worldWidthLimit + getWidth()) {
            isActive = false;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (isActive) {
            if (hp == 1) {
                batch.setColor(1f, 0.5f, 0.5f, 1f);
            }

            batch.draw(texture, position.x, position.y, bounds.width, bounds.height);

            batch.setColor(Color.WHITE);
        }
    }
}
