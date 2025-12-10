package com.finpro.frontend.obstacles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.finpro.frontend.BaseEntity;
import com.finpro.frontend.GameConfig;

public class Meteor extends BaseEntity {

    private Texture texture;
    private Vector2 velocity;
    private int hp;

    public Meteor() {
        super(0, 0, GameConfig.METEOR_WIDTH, GameConfig.METEOR_HEIGHT);
        createTexture();
        this.velocity = new Vector2(0, 0);
        this.isActive = false;
    }

    public void init(float startX, float startY, float targetX, float targetY) {
        this.position.set(startX, startY);
        this.updateBounds();
        this.isActive = true;
        this.hp = 2;

        Vector2 direction = new Vector2(targetX - startX, targetY - startY);
        direction.x += MathUtils.random(-100, 100);
        float speed = MathUtils.random(GameConfig.METEOR_MIN_SPEED, GameConfig.METEOR_MAX_SPEED);
        this.velocity = direction.nor().scl(speed);
    }

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

        if (position.y < -getHeight() || position.x < -getWidth() || position.x > GameConfig.SCREEN_WIDTH) {
            isActive = false;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (isActive) {
            if (hp == 1) batch.setColor(1f, 0.5f, 0.5f, 1f);
            batch.draw(texture, position.x, position.y, bounds.width, bounds.height);
            batch.setColor(Color.WHITE);
        }
    }

    private void createTexture() {
        Pixmap p = new Pixmap((int)bounds.width, (int)bounds.height, Pixmap.Format.RGBA8888);
        p.setColor(Color.BROWN);
        p.fillCircle((int)bounds.width/2, (int)bounds.height/2, (int)bounds.width/2 - 2);
        this.texture = new Texture(p);
        p.dispose();
    }
}
