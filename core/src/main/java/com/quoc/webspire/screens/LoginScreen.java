package com.quoc.webspire.screens;

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
import com.quoc.webspire.Main;
import com.quoc.webspire.managers.Assets;
import com.quoc.webspire.network.AuthService;
import com.quoc.webspire.network.PlayerData;

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
        root.center();

        Table cardBox = new Table();
        cardBox.setBackground(Assets.createBorderedDrawable(520, 480, 
            new Color(0.06f, 0.05f, 0.12f, 0.95f), 
            new Color(0.75f, 0.60f, 0.25f, 0.95f), 2));
        cardBox.pad(30);

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.font, Color.GOLD);
        Label.LabelStyle statusStyle = new Label.LabelStyle(game.font, Color.CYAN);

        Label titleLabel = new Label("TÀI KHOẢN ĐỒNG BỘ GAMEHUB", titleStyle);
        titleLabel.setFontScale(1.15f);

        statusLabel = new Label("Đăng nhập hoặc đăng ký để lưu tiến trình Cloud", statusStyle);

        TextField.TextFieldStyle tfStyle = new TextField.TextFieldStyle();
        tfStyle.font = game.font;
        tfStyle.fontColor = Color.WHITE;
        tfStyle.messageFontColor = Color.GRAY;
        tfStyle.background = Assets.createBorderedDrawable(380, 45, new Color(0.12f, 0.14f, 0.22f, 0.9f), new Color(0.35f, 0.45f, 0.70f, 0.8f), 1);

        TextField usernameField = new TextField("", tfStyle);
        usernameField.setMessageText("Tên đăng nhập / Email...");

        TextField passwordField = new TextField("", tfStyle);
        passwordField.setPasswordCharacter('*');
        passwordField.setPasswordMode(true);
        passwordField.setMessageText("Mật khẩu...");

        // Nút Đăng nhập
        TextButton.TextButtonStyle loginBtnStyle = new TextButton.TextButtonStyle();
        loginBtnStyle.font = game.font;
        loginBtnStyle.fontColor = Color.WHITE;
        loginBtnStyle.overFontColor = Color.YELLOW;
        loginBtnStyle.up = Assets.createBorderedDrawable(170, 44, new Color(0.12f, 0.42f, 0.25f, 0.95f), new Color(0.35f, 0.85f, 0.50f, 1f), 1);
        loginBtnStyle.over = Assets.createBorderedDrawable(170, 44, new Color(0.18f, 0.55f, 0.35f, 1f), new Color(0.55f, 1.0f, 0.70f, 1f), 2);

        TextButton loginBtn = new TextButton("✔ Đăng Nhập", loginBtnStyle);
        loginBtn.pad(8, 15, 8, 15);
        loginBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String user = usernameField.getText();
                String pwd = passwordField.getText();
                statusLabel.setText("Đang xác thực...");

                AuthService.login(user, pwd, new AuthService.AuthCallback() {
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

        // Nút Đăng ký
        TextButton.TextButtonStyle regBtnStyle = new TextButton.TextButtonStyle();
        regBtnStyle.font = game.font;
        regBtnStyle.fontColor = Color.WHITE;
        regBtnStyle.overFontColor = Color.YELLOW;
        regBtnStyle.up = Assets.createBorderedDrawable(170, 44, new Color(0.15f, 0.25f, 0.50f, 0.95f), new Color(0.35f, 0.65f, 0.95f, 1f), 1);
        regBtnStyle.over = Assets.createBorderedDrawable(170, 44, new Color(0.22f, 0.35f, 0.65f, 1f), new Color(0.65f, 0.85f, 1f, 1f), 2);

        TextButton registerBtn = new TextButton("✨ Đăng Ký Mới", regBtnStyle);
        registerBtn.pad(8, 15, 8, 15);
        registerBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String user = usernameField.getText();
                String pwd = passwordField.getText();
                statusLabel.setText("Đang khởi tạo tài khoản...");

                AuthService.register(user, pwd, new AuthService.AuthCallback() {
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

        // Nút Chơi Khách / Quay lại
        TextButton.TextButtonStyle backBtnStyle = new TextButton.TextButtonStyle();
        backBtnStyle.font = game.font;
        backBtnStyle.fontColor = Color.LIGHT_GRAY;
        backBtnStyle.overFontColor = Color.WHITE;
        backBtnStyle.up = Assets.createBorderedDrawable(355, 38, new Color(0.18f, 0.12f, 0.12f, 0.85f), new Color(0.45f, 0.30f, 0.30f, 0.8f), 1);

        TextButton offlineBtn = new TextButton("⚔ Tiếp Tục Chế Độ Khách (Ngoại Tuyến)", backBtnStyle);
        offlineBtn.pad(6, 15, 6, 15);
        offlineBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PlayerData.userId = "GUEST_" + (System.currentTimeMillis() % 100000);
                PlayerData.email = "KhachKhởiHành";
                PlayerData.token = "offline-token-guest";
                PlayerData.isLoggedIn = false;
                game.setScreen(new MainMenuScreen(game));
            }
        });

        cardBox.add(titleLabel).padBottom(10).row();
        cardBox.add(statusLabel).padBottom(20).row();
        cardBox.add(usernameField).width(360).height(45).padBottom(12).row();
        cardBox.add(passwordField).width(360).height(45).padBottom(20).row();

        Table btnRow = new Table();
        btnRow.add(loginBtn).padRight(15);
        btnRow.add(registerBtn);
        cardBox.add(btnRow).padBottom(15).row();
        cardBox.add(offlineBtn).width(355);

        root.add(cardBox);
        stage.addActor(root);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.04f, 0.03f, 0.07f, 1f);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); }
}
