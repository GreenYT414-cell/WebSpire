package com.quoc.webspire;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * @deprecated Dùng {@link com.quoc.webspire.managers.Assets} thay thế.
 * Class này chỉ là wrapper để tương thích ngược, tránh lỗi import cũ.
 * Mọi logic thực tế đã được chuyển sang managers.Assets.
 */
public class Assets {
    public static void load() {
        com.quoc.webspire.managers.Assets.load();
    }

    public static Texture getTexture(String name) {
        return com.quoc.webspire.managers.Assets.getTexture(name);
    }

    public static TextureRegionDrawable createColorDrawable(float r, float g, float b, float a) {
        return com.quoc.webspire.managers.Assets.createColorDrawable(r, g, b, a);
    }

    public static void dispose() {
        com.quoc.webspire.managers.Assets.dispose();
    }
}
