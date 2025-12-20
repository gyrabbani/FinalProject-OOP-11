package com.finpro.frontend.services;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

public class ResourceManager implements Disposable {

    private static final ResourceManager instance = new ResourceManager();
    private final AssetManager assetManager;

    private ResourceManager() {
        assetManager = new AssetManager();
    }

    public static ResourceManager getInstance() {
        return instance;
    }

    public void loadAll() {
        assetManager.load("playership.png", Texture.class);
        assetManager.load("bullet.png", Texture.class);
        assetManager.load("meteor.png", Texture.class);
        assetManager.load("spaceshooter.png", Texture.class);
        assetManager.load("enemy_small.png", Texture.class);
        assetManager.load("enemy_medium.png", Texture.class);
        assetManager.load("enemy_big.png", Texture.class);
        assetManager.load("projectile_enemy.png", Texture.class); // Peluru Small
        assetManager.load("missile.png", Texture.class);          // Roket Medium/Big
        assetManager.load("explosion.png", Texture.class);        // Ledakan
        assetManager.load("laser_tex.png", Texture.class);        // Tekstur Laser
        assetManager.load("shield.png", Texture.class);           // Icon Shield
        assetManager.load("star.png", Texture.class);
        assetManager.finishLoading();
    }

    public Texture getTexture(String name) {
        if (assetManager.isLoaded(name)) {
            return assetManager.get(name, Texture.class);
        } else {
            if (name.contains("player")) return createPlaceholder(32, 32, Color.CYAN);
            if (name.contains("bullet")) return createPlaceholder(15, 30, Color.ORANGE);
            if (name.contains("projectile_enemy")) return createPlaceholder(10, 20, Color.YELLOW);
            if (name.contains("missile")) return createPlaceholder(15, 35, Color.ORANGE);
            if (name.contains("explosion")) return createPlaceholder(64, 64, Color.RED);
            if (name.contains("laser")) return createPlaceholder(10, 10, Color.CYAN); // Putih kebiruan
            if (name.contains("shield")) return createPlaceholder(32, 32, new Color(0, 1, 1, 0.5f)); // Cyan transparan
            if (name.contains("star")) return createPlaceholder(20, 20, Color.YELLOW);
            if (name.contains("background")) {
                return createSpaceBackground((int) GameConfig.SCREEN_WIDTH, (int)GameConfig.SCREEN_HEIGHT);
            }

            if (name.contains("enemy")) return createPlaceholder(32, 32, Color.RED);
            return createPlaceholder(32, 32, Color.MAGENTA);
        }
    }

    private Texture createPlaceholder(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture t = new Texture(pixmap);
        pixmap.dispose();
        return t;
    }

    private Texture createSpaceBackground(int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        pixmap.setColor(Color.BLACK);
        pixmap.fill();

        pixmap.setColor(Color.WHITE);
        int starCount = 150;

        for (int i = 0; i < starCount; i++) {
            int x = (int) (Math.random() * width);
            int y = (int) (Math.random() * height);
            pixmap.drawPixel(x, y);

            if (Math.random() > 0.95) {
                pixmap.drawPixel(x + 1, y);
                pixmap.drawPixel(x, y + 1);
            }
        }

        Texture t = new Texture(pixmap);
        pixmap.dispose();
        return t;
    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }
}
