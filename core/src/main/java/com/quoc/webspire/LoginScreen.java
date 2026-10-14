package com.quoc.webspire;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class LoginScreen implements Screen {
    private final Main game;
    private Stage stage;
    private Label statusLabel;

    public LoginScreen(Main game) {
        this.game = game;
        this.stage = new Stage(new FitViewport(1280, 720), game.batch);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        Table root = new Table();
        root.setFillParent(true);

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.font, Color.GOLD);
        Label.LabelStyle labelStyle = new Label.LabelStyle(game.font, Color.WHITE);
        Label.LabelStyle statusStyle = new Label.LabelStyle(game.font, Color.CYAN);

        Label titleLabel = new Label("ĐĂNG NHẬP TÀI KHOẢN GAMEHUB", titleStyle);
        statusLabel = new Label("Nhập thông tin tài khoản GameHub của bạn", statusStyle);

        TextField.TextFieldStyle tfStyle = new TextField.TextFieldStyle();
        tfStyle.font = game.font;
        tfStyle.fontColor = Color.WHITE;
        tfStyle.background = Assets.createColorDrawable(0.2f, 0.2f, 0.3f, 0.9f);

        TextField emailField = new TextField("", tfStyle);
        emailField.setMessageText("Email GameHub...");

        TextField passwordField = new TextField("", tfStyle);
        passwordField.setPasswordCharacter('*');
        passwordField.setPasswordMode(true);
        passwordField.setMessageText("Mật khẩu...");

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = game.font;
        btnStyle.fontColor = Color.WHITE;
        btnStyle.up = Assets.createColorDrawable(0.2f, 0.5f, 0.3f, 1f);

        TextButton loginBtn = new TextButton("Đăng Nhập", btnStyle);
        loginBtn.pad(10, 20, 10, 20);
        loginBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String email = emailField.getText();
                String pwd = passwordField.getText();

                AuthService.login(email, pwd, new AuthService.AuthCallback() {
                    @Override
                    public void onSuccess(String message, String token) {
                        statusLabel.setText(message);
                        game.setScreen(new MainMenuScreen(game));
                    }

                    @Override
                    public void onError(String errorMessage) {
                        statusLabel.setText("Lỗi: " + errorMessage);
                    }
                });
            }
        });

        TextButton offlineBtn = new TextButton("Chơi Offline (Khách)", btnStyle);
        offlineBtn.pad(10, 20, 10, 20);
        offlineBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PlayerData.email = "Khach@GameHub";
                game.setScreen(new MainMenuScreen(game));
            }
        });

        root.add(titleLabel).padBottom(20).row();
        root.add(statusLabel).padBottom(20).row();
        root.add(emailField).width(350).height(45).padBottom(15).row();
        root.add(passwordField).width(350).height(45).padBottom(20).row();

        Table btnTable = new Table();
        btnTable.add(loginBtn).padRight(15);
        btnTable.add(offlineBtn);

        root.add(btnTable);

        stage.addActor(root);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.08f, 0.05f, 0.12f, 1f);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); }
}
