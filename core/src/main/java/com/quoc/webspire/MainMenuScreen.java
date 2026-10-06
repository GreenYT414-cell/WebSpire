package com.quoc.webspire;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MainMenuScreen implements Screen {
    private final Main game;
    private Stage stage;
    private Texture bgTexture;

    public MainMenuScreen(final Main game) {
        this.game = game;
        this.stage = new Stage(new FitViewport(1280, 720), game.batch);

        // Tải hình nền nếu có (Ví dụ: assets/menu_bg.png), nếu chưa có sẽ dùng màu nền
        if (Gdx.files.internal("menu_bg.png").exists()) {
            bgTexture = new Texture(Gdx.files.internal("menu_bg.png"));
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.font, Color.GOLD);
        Label titleLabel = new Label("WEBSPIRE: THE TOWER", titleStyle);
        titleLabel.setFontScale(1.5f);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = game.font;
        btnStyle.fontColor = Color.WHITE;
        btnStyle.downFontColor = Color.YELLOW;
        btnStyle.up = createDrawable(0.2f, 0.15f, 0.3f, 0.9f);

        TextButton startBtn = new TextButton("Bắt Đầu Chuyến Đi", btnStyle);
        startBtn.pad(15, 40, 15, 40);
        startBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Chuyển sang màn hình chơi
                game.setScreen(new GameScreen(game));
            }
        });

        TextButton exitBtn = new TextButton("Thoát Game", btnStyle);
        exitBtn.pad(15, 40, 15, 40);
        exitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        mainTable.add(titleLabel).padBottom(50).row();
        mainTable.add(startBtn).width(300).height(60).padBottom(20).row();
        mainTable.add(exitBtn).width(300).height(60);

        stage.addActor(mainTable);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.08f, 0.05f, 0.12f, 1f);

        game.batch.begin();
        if (bgTexture != null) {
            game.batch.draw(bgTexture, 0, 0, 1280, 720);
        }
        game.batch.end();

        stage.act(delta);
        stage.draw();
    }

    private TextureRegionDrawable createDrawable(float r, float g, float b, float a) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(r, g, b, a);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        if (bgTexture != null) bgTexture.dispose();
    }
}
