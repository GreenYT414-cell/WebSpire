package com.quoc.webspire.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.HashMap;
import java.util.Map;

public class Assets {
    public static final AssetManager manager = new AssetManager();

    // Cache các TextureRegionDrawable theo key color để tránh memory leak
    private static final Map<String, TextureRegionDrawable> colorDrawableCache = new HashMap<>();

    public static void load() {
        // Nạp phông nền
        if (Gdx.files.internal("bg_combat.png").exists()) manager.load("bg_combat.png", Texture.class);
        if (Gdx.files.internal("menu_bg.png").exists()) manager.load("menu_bg.png", Texture.class);

        // Nạp Sprite Nhân vật & Quái vật
        if (Gdx.files.internal("hero.png").exists()) manager.load("hero.png", Texture.class);
        if (Gdx.files.internal("goblin.png").exists()) manager.load("goblin.png", Texture.class);
        if (Gdx.files.internal("dark_knight.png").exists()) manager.load("dark_knight.png", Texture.class);
        if (Gdx.files.internal("elite_knight.png").exists()) manager.load("elite_knight.png", Texture.class);
        if (Gdx.files.internal("midboss_guardian.png").exists()) manager.load("midboss_guardian.png", Texture.class);
        if (Gdx.files.internal("boss_slime.png").exists()) manager.load("boss_slime.png", Texture.class);

        // Nạp Artwork Lá bài
        if (Gdx.files.internal("card_art_attack.png").exists()) manager.load("card_art_attack.png", Texture.class);
        if (Gdx.files.internal("card_art_defense.png").exists()) manager.load("card_art_defense.png", Texture.class);
        if (Gdx.files.internal("card_art_skill.png").exists()) manager.load("card_art_skill.png", Texture.class);
        if (Gdx.files.internal("card_art_power.png").exists()) manager.load("card_art_power.png", Texture.class);

        manager.finishLoading();
    }

    public static Texture getTexture(String name) {
        if (manager.isLoaded(name, Texture.class)) {
            return manager.get(name, Texture.class);
        }
        return null;
    }

    private static Texture shadowTexture;
    private static Texture energyOrbTexture;

    /**
     * Tạo hoặc lấy từ cache texture bóng đổ mềm mại dưới chân nhân vật / quái.
     */
    public static Texture getShadowTexture() {
        if (shadowTexture == null) {
            int w = 180;
            int h = 48;
            Pixmap pix = new Pixmap(w, h, Pixmap.Format.RGBA8888);
            pix.setColor(0, 0, 0, 0);
            pix.fill();
            float cx = w / 2f;
            float cy = h / 2f;
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    float dx = (x - cx) / (w / 2f);
                    float dy = (y - cy) / (h / 2f);
                    float d = dx * dx + dy * dy;
                    if (d < 1f) {
                        float alpha = (1f - (float) Math.sqrt(d)) * 0.60f;
                        pix.setColor(0f, 0f, 0f, alpha);
                        pix.drawPixel(x, y);
                    }
                }
            }
            shadowTexture = new Texture(pix);
            pix.dispose();
        }
        return shadowTexture;
    }

    /**
     * Tạo hoặc lấy từ cache texture Ngọc Năng Lượng huyền bí phát sáng.
     */
    public static Texture getEnergyOrbTexture() {
        if (energyOrbTexture == null) {
            int size = 120;
            Pixmap pix = new Pixmap(size, size, Pixmap.Format.RGBA8888);
            pix.setColor(0, 0, 0, 0);
            pix.fill();
            float c = size / 2f;
            float r = size / 2f - 4;
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    float dx = (x - c);
                    float dy = (y - c);
                    float dist = (float) Math.sqrt(dx * dx + dy * dy);
                    if (dist <= r) {
                        float norm = dist / r;
                        float rCol = (1f - norm) * 0.08f + norm * 0.35f;
                        float gCol = (1f - norm) * 0.35f + norm * 0.85f;
                        float bCol = (1f - norm) * 0.95f + norm * 1.0f;
                        float aCol = 0.96f;
                        if (dist > r - 6) {
                            aCol = ((r - dist) / 6f) * 0.96f;
                        }
                        pix.setColor(rCol, gCol, bCol, aCol);
                        pix.drawPixel(x, y);
                    }
                }
            }
            energyOrbTexture = new Texture(pix);
            pix.dispose();
        }
        return energyOrbTexture;
    }

    /**
     * Tạo một TextureRegionDrawable có khung viền màu sang trọng.
     */
    public static TextureRegionDrawable createBorderedDrawable(int w, int h, Color innerColor, Color borderColor, int borderWidth) {
        String key = "border_" + w + "x" + h + "_" + innerColor + "_" + borderColor + "_" + borderWidth;
        if (colorDrawableCache.containsKey(key)) {
            return colorDrawableCache.get(key);
        }
        Pixmap pix = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pix.setColor(borderColor);
        pix.fill();
        pix.setColor(innerColor);
        pix.fillRectangle(borderWidth, borderWidth, Math.max(1, w - borderWidth * 2), Math.max(1, h - borderWidth * 2));
        Texture tex = new Texture(pix);
        pix.dispose();
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(tex));
        colorDrawableCache.put(key, drawable);
        return drawable;
    }

    /**
     * Tạo hoặc lấy từ cache một TextureRegionDrawable đơn sắc.
     * Fix memory leak: các Texture trước đây bị tạo mới mỗi lần gọi và không được dispose().
     * Nay được cache theo key color để tái sử dụng và dispose sạch khi thoát game.
     */
    public static TextureRegionDrawable createColorDrawable(float r, float g, float b, float a) {
        // Tạo key dạng "r,g,b,a" để tra cache
        String key = r + "," + g + "," + b + "," + a;
        if (colorDrawableCache.containsKey(key)) {
            return colorDrawableCache.get(key);
        }

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(r, g, b, a);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(texture));
        colorDrawableCache.put(key, drawable);
        return drawable;
    }

    public static void dispose() {
        // Dispose tất cả các Texture đã cache để tránh VRAM leak
        for (TextureRegionDrawable drawable : colorDrawableCache.values()) {
            drawable.getRegion().getTexture().dispose();
        }
        colorDrawableCache.clear();
        if (shadowTexture != null) {
            shadowTexture.dispose();
            shadowTexture = null;
        }
        if (energyOrbTexture != null) {
            energyOrbTexture.dispose();
            energyOrbTexture = null;
        }
        manager.dispose();
    }
}
