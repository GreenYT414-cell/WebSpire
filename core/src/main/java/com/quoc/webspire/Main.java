package com.quoc.webspire;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class Main extends Game {
    public SpriteBatch batch;
    public BitmapFont font;

    public static final String VIETNAMESE_CHARS =
        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789" +
            "àáảãạăằắẳẵặâầấẩẫậèéẻẽẹêềếểễệìíỉĩịòóỏõọôồốổỗộơờớởỡợùúủũụưừứửữựỳýỷỹỵđ" +
            "ÀÁẢÃẠĂẰẮẲẴẶÂẦẤẨẪẬÈÉẺẼẸÊỀẾỂỄỆÌÍỈĨỊÒÓỎÕỌÔỒỐỔỖỘƠỜỚỞỠỢÙÚỦŨỤƯỪỨỬỮỰỲÝỶỸỴĐ" +
            ".,:;?!-+=[]{}()/\\\"'<>*%_°|";

    @Override
    public void create() {
        batch = new SpriteBatch();

        // Nạp toàn bộ tài nguyên hình ảnh / âm thanh bằng AssetManager
        Assets.load();

        if (Gdx.files.internal("Roboto-Regular.ttf").exists()) {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Regular.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

            parameter.size = 18;
            parameter.color = Color.WHITE;
            parameter.characters = VIETNAMESE_CHARS;

            font = generator.generateFont(parameter);
            generator.dispose();
        } else if (Gdx.files.internal("Arial.ttf").exists()) {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Arial.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

            parameter.size = 18;
            parameter.color = Color.WHITE;
            parameter.characters = VIETNAMESE_CHARS;

            font = generator.generateFont(parameter);
            generator.dispose();
        } else {
            font = new BitmapFont();
        }

        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        Assets.dispose();
    }
}
