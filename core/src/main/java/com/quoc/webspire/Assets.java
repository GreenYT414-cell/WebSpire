package com.quoc.webspire;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Assets {
    public static final AssetManager manager = new AssetManager();

    public static void load() {
        if (Gdx.files.internal("card_frame.png").exists()) {
            manager.load("card_frame.png", Texture.class);
        }
        if (Gdx.files.internal("bg_combat.png").exists()) {
            manager.load("bg_combat.png", Texture.class);
        }
        if (Gdx.files.internal("menu_bg.png").exists()) {
            manager.load("menu_bg.png", Texture.class);
        }

        manager.finishLoading();
    }

    public static Texture getTexture(String name) {
        if (manager.isLoaded(name, Texture.class)) {
            return manager.get(name, Texture.class);
        }
        return null;
    }

    public static TextureRegionDrawable createColorDrawable(float r, float g, float b, float a) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(r, g, b, a);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    public static void dispose() {
        manager.dispose();
    }
}
