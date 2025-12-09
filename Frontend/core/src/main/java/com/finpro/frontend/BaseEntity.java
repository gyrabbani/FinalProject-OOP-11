package com.finpro.frontend;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class BaseEntity {

    protected Vector2 position;
    protected Rectangle bounds;

    protected boolean isActive = true;

    public BaseEntity(float x, float y, float width, float height) {
        this.position = new Vector2(x, y);
        this.bounds = new Rectangle(x, y, width, height);
    }


    public abstract void update(float delta);

    public abstract void render(SpriteBatch batch);

    protected void updateBounds() {
        bounds.setPosition(position.x, position.y);
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
        updateBounds();
    }

    public float getX() { return position.x; }
    public float getY() { return position.y; }
    public float getWidth() { return bounds.width; }
    public float getHeight() { return bounds.height; }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }
}
