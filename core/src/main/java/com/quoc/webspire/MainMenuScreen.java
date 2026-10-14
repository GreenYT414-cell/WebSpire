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
    private Label statusLabel;

    public MainMenuScreen(final Main game) {
        this.game = game;
        this.stage = new Stage(new FitViewport(1280, 720), game.batch);

        if (Gdx.files.internal("menu_bg.png").exists()) {
            bgTexture = new Texture(Gdx.files.internal("menu_bg.png"));
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        SoundManager.getInstance().playBgm("bgm_menu.mp3");

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.font, Color.GOLD);
        Label.LabelStyle statusStyle = new Label.LabelStyle(game.font, Color.CYAN);

        Label titleLabel = new Label("WEBSPIRE: THE TOWER", titleStyle);
        titleLabel.setFontScale(1.5f);

        statusLabel = new Label("Tài khoản: " + PlayerData.email, statusStyle);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = game.font;
        btnStyle.fontColor = Color.WHITE;
        btnStyle.downFontColor = Color.YELLOW;
        btnStyle.up = createDrawable(0.2f, 0.15f, 0.3f, 0.9f);

        TextButton startBtn = new TextButton("Bắt Đầu Chuyến Đi Mới", btnStyle);
        startBtn.pad(15, 40, 15, 40);
        startBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                RunState.getInstance().startNewRun();
                game.setScreen(new MapScreen(game));
            }
        });

        TextButton continueBtn = new TextButton("Tiếp Tục Chuyến Đi", btnStyle);
        continueBtn.pad(15, 40, 15, 40);
        continueBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameSyncService.loadProfile(PlayerData.userId, new GameSyncService.LoadProfileCallback() {
                    @Override
                    public void onSuccess(GameProfileDto profile) {
                        game.setScreen(new MapScreen(game));
                    }

                    @Override
                    public void onError(String errorMessage) {
                        statusLabel.setText(errorMessage);
                    }
                });
            }
        });

        TextButton loginBtn = new TextButton("Tài Khoản GameHub", btnStyle);
        loginBtn.pad(15, 40, 15, 40);
        loginBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LoginScreen(game));
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

        mainTable.add(titleLabel).padBottom(20).row();
        mainTable.add(statusLabel).padBottom(30).row();
        mainTable.add(startBtn).width(340).height(55).padBottom(15).row();
        mainTable.add(continueBtn).width(340).height(55).padBottom(15).row();
        mainTable.add(loginBtn).width(340).height(55).padBottom(15).row();
        mainTable.add(exitBtn).width(340).height(55);

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
