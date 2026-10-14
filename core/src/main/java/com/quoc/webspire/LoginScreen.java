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
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class LoginScreen implements Screen {
    private final Main game;
    private Stage stage;

    private TextField usernameField;
    private TextField passwordField;
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
        Label.LabelStyle statusStyle = new Label.LabelStyle(game.font, Color.YELLOW);

        Label titleLabel = new Label("ĐĂNG NHẬP WEBSPIRE", titleStyle);
        statusLabel = new Label("", statusStyle);

        // Khởi tạo Style cho Input Field
        TextField.TextFieldStyle tfStyle = new TextField.TextFieldStyle();
        tfStyle.font = game.font;
        tfStyle.fontColor = Color.WHITE;
        tfStyle.background = createDrawable(0.2f, 0.2f, 0.2f, 1f);
        tfStyle.cursor = createDrawable(1f, 1f, 1f, 1f);
        tfStyle.selection = createDrawable(0.4f, 0.4f, 0.8f, 1f);

        usernameField = new TextField("", tfStyle);
        usernameField.setMessageText(" Tên tài khoản...");

        passwordField = new TextField("", tfStyle);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        passwordField.setMessageText(" Mật khẩu...");

        // Khởi tạo Style cho Button
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = game.font;
        btnStyle.fontColor = Color.WHITE;
        btnStyle.up = createDrawable(0.2f, 0.5f, 0.2f, 1f);

        TextButton loginBtn = new TextButton("Đăng Nhập", btnStyle);
        TextButton registerBtn = new TextButton("Đăng Ký", btnStyle);

        // Sự kiện Đăng nhập
        loginBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String user = usernameField.getText().trim();
                String pass = passwordField.getText().trim();

                if (user.isEmpty() || pass.isEmpty()) {
                    statusLabel.setText("Vui lòng điền đầy đủ thông tin!");
                    return;
                }

                statusLabel.setText("Đang kết nối...");
                AuthService.login(user, pass, new AuthService.AuthCallback() {
                    @Override
                    public void onSuccess(String message, String token) {
                        statusLabel.setText(message);
                        // Đăng nhập thành công -> Chuyển sang Menu chính
                        game.setScreen(new MainMenuScreen(game));
                    }

                    @Override
                    public void onError(String errorMessage) {
                        statusLabel.setText(errorMessage);
                    }
                });
            }
        });

        // Sự kiện Đăng ký
        registerBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String user = usernameField.getText().trim();
                String pass = passwordField.getText().trim();

                if (user.isEmpty() || pass.isEmpty()) {
                    statusLabel.setText("Vui lòng nhập tài khoản & mật khẩu để đăng ký!");
                    return;
                }

                statusLabel.setText("Đang tạo tài khoản...");
                AuthService.register(user, pass, new AuthService.AuthCallback() {
                    @Override
                    public void onSuccess(String message, String token) {
                        statusLabel.setText("Đăng ký thành công! Hãy bấm Đăng nhập.");
                    }

                    @Override
                    public void onError(String errorMessage) {
                        statusLabel.setText(errorMessage);
                    }
                });
            }
        });

        // Sắp xếp Giao diện
        root.add(titleLabel).padBottom(30).colspan(2).row();

        root.add(new Label("Tài khoản:", labelStyle)).right().padRight(10);
        root.add(usernameField).width(300).height(45).padBottom(15).row();

        root.add(new Label("Mật khẩu:", labelStyle)).right().padRight(10);
        root.add(passwordField).width(300).height(45).padBottom(20).row();

        Table btnTable = new Table();
        btnTable.add(loginBtn).size(140, 45).padRight(10);
        btnTable.add(registerBtn).size(140, 45);

        root.add(btnTable).colspan(2).padBottom(20).row();
        root.add(statusLabel).colspan(2);

        stage.addActor(root);
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
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1f);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); }
}
