package com.quoc.webspire.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.quoc.webspire.Main;
import com.quoc.webspire.managers.Assets;
import com.quoc.webspire.managers.RunState;
import com.quoc.webspire.managers.SoundManager;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.quoc.webspire.network.GameProfileDto;
import com.quoc.webspire.network.GameSyncService;
import com.quoc.webspire.network.PlayerData;
import com.quoc.webspire.ui.ParticleManager;
import java.util.List;

public class MainMenuScreen implements Screen {
    private final Main game;
    private Stage stage;
    private Texture bgTexture;
    private Label statusLabel;
    private ParticleManager particleManager;
    private float stateTime = 0f;
    private Table titleTable;

    public MainMenuScreen(final Main game) {
        this.game = game;
        this.stage = new Stage(new FitViewport(1280, 720), game.batch);
        this.particleManager = new ParticleManager();

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

        // 1. Tiêu đề Game với hiệu ứng lơ lửng hoàng kim
        titleTable = new Table();
        titleTable.setTransform(true);
        titleTable.setOrigin(Align.center);

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.font, Color.GOLD);
        Label titleLabel = new Label("WEBSPIRE: THE TOWER", titleStyle);
        titleLabel.setFontScale(1.8f);

        Label.LabelStyle subtitleStyle = new Label.LabelStyle(game.font, new Color(0.85f, 0.75f, 0.45f, 0.85f));
        Label subtitleLabel = new Label("✦  A  R O G U E L I K E   D E C K B U I L D I N G   A S C E N T  ✦", subtitleStyle);

        titleTable.add(titleLabel).padBottom(6).row();
        titleTable.add(subtitleLabel).padBottom(15);

        // Hiệu ứng bồng bềnh hoàng kim cho tiêu đề
        titleTable.addAction(Actions.forever(Actions.sequence(
            Actions.moveBy(0, 6f, 1.3f, Interpolation.sine),
            Actions.moveBy(0, -6f, 1.3f, Interpolation.sine)
        )));

        // 2. Huy hiệu Trạng thái Tài khoản người chơi
        Table accountBadge = new Table();
        accountBadge.setBackground(Assets.createBorderedDrawable(380, 32, new Color(0.06f, 0.05f, 0.12f, 0.85f), new Color(0.25f, 0.45f, 0.75f, 0.8f), 1));
        accountBadge.pad(5, 15, 5, 15);

        Label.LabelStyle statusStyle = new Label.LabelStyle(game.font, Color.CYAN);
        statusLabel = new Label("⚔ Hiệp sĩ: " + (PlayerData.email != null ? PlayerData.email : "Khách Khởi Hành"), statusStyle);
        accountBadge.add(statusLabel);

        // 3. Thiết kế Nút bấm kiểu Gothic Glassmorphism viền Vàng Kim
        TextButton startBtn = createStyledButton("⚔  Bắt Đầu Chuyến Đi Mới");
        startBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                RunState.getInstance().startNewRun();
                game.setScreen(new MapScreen(game));
            }
        });

        TextButton continueBtn = createStyledButton("📜  Tiếp Tục Chuyến Đi");
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
                        statusLabel.setText("Lỗi: " + errorMessage);
                    }
                });
            }
        });

        TextButton loginBtn = createStyledButton("👤  Tài Khoản GameHub");
        loginBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LoginScreen(game));
            }
        });

        TextButton leaderboardBtn = createStyledButton("🏆  Bảng Xếp Hạng Cloud");
        leaderboardBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showLeaderboardDialog();
            }
        });

        TextButton exitBtn = createStyledButton("🚪  Thoát Game");
        exitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        mainTable.add(titleTable).padBottom(15).row();
        mainTable.add(accountBadge).padBottom(25).row();
        mainTable.add(startBtn).width(370).height(52).padBottom(12).row();
        mainTable.add(continueBtn).width(370).height(52).padBottom(12).row();
        mainTable.add(loginBtn).width(370).height(52).padBottom(12).row();
        mainTable.add(leaderboardBtn).width(370).height(52).padBottom(12).row();
        mainTable.add(exitBtn).width(370).height(52);

        stage.addActor(mainTable);
    }

    private void showLeaderboardDialog() {
        Table overlay = new Table();
        overlay.setFillParent(true);
        overlay.setBackground(Assets.createColorDrawable(0.02f, 0.02f, 0.05f, 0.92f));

        Table window = new Table();
        window.setBackground(Assets.createBorderedDrawable(660, 490, 
            new Color(0.06f, 0.06f, 0.12f, 0.98f), 
            new Color(0.75f, 0.60f, 0.25f, 0.95f), 2));
        window.pad(20);

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.font, Color.GOLD);
        Label titleLabel = new Label("🏆  BẢNG XẾP HẠNG CAO THỦ WEBSPIRE  🏆", titleStyle);
        titleLabel.setFontScale(1.1f);

        Table contentTable = new Table();
        contentTable.pad(10);
        Label.LabelStyle loadingStyle = new Label.LabelStyle(game.font, Color.CYAN);
        Label loadingLabel = new Label("Đang kết nối Cloud Server...", loadingStyle);
        contentTable.add(loadingLabel).pad(20);

        GameSyncService.fetchLeaderboard(new GameSyncService.LeaderboardCallback() {
            @Override
            public void onSuccess(List<String> records) {
                contentTable.clearChildren();
                Label.LabelStyle rowStyle = new Label.LabelStyle(game.font, Color.WHITE);
                Label.LabelStyle topStyle = new Label.LabelStyle(game.font, Color.GOLD);

                for (int i = 0; i < records.size(); i++) {
                    String rec = records.get(i);
                    Label l = new Label(rec, i == 0 ? topStyle : rowStyle);
                    contentTable.add(l).left().padBottom(8).row();
                }
            }

            @Override
            public void onError(String errorMessage) {
                contentTable.clearChildren();
                Label.LabelStyle errStyle = new Label.LabelStyle(game.font, Color.RED);
                contentTable.add(new Label("Lỗi: " + errorMessage, errStyle)).pad(20);
            }
        });

        ScrollPane scroll = new ScrollPane(contentTable);
        scroll.setFadeScrollBars(false);

        TextButton.TextButtonStyle closeStyle = new TextButton.TextButtonStyle();
        closeStyle.font = game.font;
        closeStyle.fontColor = Color.WHITE;
        closeStyle.overFontColor = Color.YELLOW;
        closeStyle.up = Assets.createBorderedDrawable(150, 40, new Color(0.35f, 0.12f, 0.12f, 0.95f), new Color(0.85f, 0.35f, 0.35f, 1f), 1);

        TextButton closeBtn = new TextButton("✖ Đóng", closeStyle);
        closeBtn.pad(8, 20, 8, 20);
        closeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                overlay.remove();
            }
        });

        window.add(titleLabel).padBottom(15).row();
        window.add(scroll).width(620).height(330).padBottom(15).row();
        window.add(closeBtn).center();

        overlay.add(window).center();
        stage.addActor(overlay);
    }

    private TextButton createStyledButton(String text) {
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = game.font;
        btnStyle.fontColor = new Color(0.95f, 0.90f, 0.80f, 1f);
        btnStyle.overFontColor = Color.YELLOW;
        btnStyle.downFontColor = Color.GOLD;

        btnStyle.up = Assets.createBorderedDrawable(370, 56, 
            new Color(0.12f, 0.08f, 0.18f, 0.88f), 
            new Color(0.75f, 0.58f, 0.22f, 0.90f), 2);
        btnStyle.over = Assets.createBorderedDrawable(370, 56, 
            new Color(0.24f, 0.15f, 0.35f, 0.95f), 
            new Color(1.0f, 0.85f, 0.35f, 1f), 2);
        btnStyle.down = Assets.createBorderedDrawable(370, 56, 
            new Color(0.32f, 0.18f, 0.42f, 0.98f), 
            new Color(1.0f, 0.95f, 0.55f, 1f), 2);

        TextButton button = new TextButton(text, btnStyle);
        button.setTransform(true);
        button.setOrigin(Align.center);

        // Hiệu ứng phóng to nhẹ nhàng khi rê chuột (Hover Micro-animation)
        button.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    button.clearActions();
                    button.addAction(Actions.scaleTo(1.04f, 1.04f, 0.12f, Interpolation.pow2Out));
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (pointer == -1) {
                    button.clearActions();
                    button.addAction(Actions.scaleTo(1.0f, 1.0f, 0.1f, Interpolation.pow2In));
                }
            }
        });

        return button;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.06f, 0.04f, 0.10f, 1f);

        stateTime += delta;
        particleManager.update(delta);
        particleManager.updateAmbient(delta, 1280, 720, new Color(0.95f, 0.55f, 0.15f, 1f), 12);

        game.batch.begin();
        if (bgTexture != null) {
            // Vẽ nền Tòa tháp huyền bí với độ che phủ toàn màn hình
            game.batch.setColor(Color.WHITE);
            game.batch.draw(bgTexture, 0, 0, 1280, 720);

            // Phủ lớp màn đêm mờ để các nút và tiêu đề hiển thị nổi bật, dễ đọc
            game.batch.setColor(0.04f, 0.03f, 0.08f, 0.45f);
            Assets.createColorDrawable(1f, 1f, 1f, 1f).draw(game.batch, 0, 0, 1280, 720);
            game.batch.setColor(Color.WHITE);
        }

        // Vẽ các hạt tàn lửa ma thuật bốc lên từ chân tháp
        particleManager.draw(game.batch);
        game.batch.end();

        stage.act(delta);
        stage.draw();
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
