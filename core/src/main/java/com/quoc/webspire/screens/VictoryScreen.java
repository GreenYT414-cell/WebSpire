package com.quoc.webspire.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.quoc.webspire.Main;
import com.quoc.webspire.managers.Assets;
import com.quoc.webspire.managers.RunState;
import com.quoc.webspire.managers.SoundManager;
import com.quoc.webspire.network.CouponDto;
import com.quoc.webspire.network.GameSyncService;
import com.quoc.webspire.network.PlayerData;

public class VictoryScreen implements Screen {
    private final Main game;
    private Stage stage;
    private Label couponLabel;

    public VictoryScreen(Main game) {
        this.game = game;
        this.stage = new Stage(new FitViewport(1280, 720), game.batch);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        SoundManager.getInstance().playSound("sfx_victory.wav");

        RunState state = RunState.getInstance();

        Table root = new Table();
        root.setFillParent(true);
        root.setBackground(Assets.createColorDrawable(0.02f, 0.08f, 0.04f, 0.95f));

        Table content = new Table();
        content.pad(20);

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.font, Color.GOLD);
        Label.LabelStyle heroStyle = new Label.LabelStyle(game.font, new Color(0.4f, 1f, 0.5f, 1f));
        Label.LabelStyle headerStyle = new Label.LabelStyle(game.font, Color.CYAN);
        Label.LabelStyle statStyle = new Label.LabelStyle(game.font, Color.WHITE);
        Label.LabelStyle couponStyle = new Label.LabelStyle(game.font, new Color(1f, 0.9f, 0.3f, 1f));
        Label.LabelStyle subStyle = new Label.LabelStyle(game.font, Color.LIGHT_GRAY);

        // Tiêu đề
        Label titleLabel = new Label("★  CHINH PHỤC WEBSPIRE!  ★", titleStyle);
        Label heroLabel = new Label("[ " + PlayerData.email + " ]", heroStyle);
        Label sep1 = new Label("────────────────────────────────────────", subStyle);

        content.add(titleLabel).padBottom(6).row();
        content.add(heroLabel).padBottom(6).row();
        content.add(sep1).padBottom(14).row();

        // Bảng thống kê
        Label statsHeader = new Label("— TỔNG KẾT HÀNH TRÌNH —", headerStyle);
        content.add(statsHeader).padBottom(10).row();

        Table statsTable = new Table();
        statsTable.setBackground(Assets.createColorDrawable(0f, 0.05f, 0.1f, 0.8f));
        statsTable.pad(15, 30, 15, 30);

        addStat(statsTable, statStyle, "Tầng đã chinh phục", state.getCurrentFloor() + " / 15");
        addStat(statsTable, statStyle, "Quái thường tiêu diệt", String.valueOf(state.getEnemiesKilled()));
        addStat(statsTable, statStyle, "Elite tiêu diệt", String.valueOf(state.getElitesKilled()));
        addStat(statsTable, statStyle, "Boss tiêu diệt", String.valueOf(state.getBossesKilled()));
        addStat(statsTable, statStyle, "Tổng sát thương gây ra", String.valueOf(state.getTotalDamageDealt()));
        addStat(statsTable, statStyle, "Tổng sát thương nhận vào", String.valueOf(state.getTotalDamageTaken()));
        addStat(statsTable, statStyle, "Lá bài đã chơi", String.valueOf(state.getCardsPlayed()));
        addStat(statsTable, statStyle, "Số lượt kết thúc", String.valueOf(state.getTurnsCount()));
        addStat(statsTable, statStyle, "Vàng tích lũy", state.getPlayer().getGold() + " Vàng");
        addStat(statsTable, statStyle, "Kích thước bộ bài", state.getMasterDeck().size() + " lá");
        addStat(statsTable, statStyle, "Cổ vật thu thập", String.valueOf(state.getPlayer().getRelics().size()));
        addStat(statsTable, statStyle, "HP còn lại", state.getPlayer().getHp() + " / " + state.getPlayer().getMaxHp());

        content.add(statsTable).width(620).padBottom(16).row();

        // Voucher / Coupon
        Label sep2 = new Label("────────────────────────────────────────", subStyle);
        Label couponHeader = new Label("— PHẦN THƯỞNG ĐẶC BIỆT —", headerStyle);
        couponLabel = new Label("Đang truy xuất mã ưu đãi từ GameHubDB...", couponStyle);
        couponLabel.setWrap(true);

        GameSyncService.claimCoupon(state.getCurrentFloor(), new GameSyncService.CouponCallback() {
            @Override
            public void onSuccess(CouponDto coupon) {
                couponLabel.setText("MÃ GIẢM GIÁ: " + coupon.getCode() + "\n" + coupon.getDescription());
            }

            @Override
            public void onError(String errorMessage) {
                couponLabel.setText("Lỗi nhận ưu đãi: " + errorMessage);
            }
        });

        content.add(sep2).padBottom(8).row();
        content.add(couponHeader).padBottom(6).row();
        content.add(couponLabel).width(620).padBottom(20).row();

        // Nút bấm
        TextButton.TextButtonStyle menuBtnStyle = new TextButton.TextButtonStyle();
        menuBtnStyle.font = game.font;
        menuBtnStyle.fontColor = Color.WHITE;
        menuBtnStyle.up = Assets.createColorDrawable(0.2f, 0.6f, 0.2f, 1f);

        TextButton.TextButtonStyle restartBtnStyle = new TextButton.TextButtonStyle();
        restartBtnStyle.font = game.font;
        restartBtnStyle.fontColor = Color.WHITE;
        restartBtnStyle.up = Assets.createColorDrawable(0.15f, 0.3f, 0.55f, 1f);

        TextButton menuBtn = new TextButton("Về Menu Chính", menuBtnStyle);
        menuBtn.pad(10, 25, 10, 25);
        menuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        TextButton restartBtn = new TextButton("Chơi Lại Ngay", restartBtnStyle);
        restartBtn.pad(10, 25, 10, 25);
        restartBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                RunState.getInstance().startNewRun();
                game.setScreen(new MapScreen(game));
            }
        });

        Table btnRow = new Table();
        btnRow.add(restartBtn).padRight(20);
        btnRow.add(menuBtn);
        content.add(btnRow).padBottom(20);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFadeScrollBars(false);
        root.add(scrollPane).expand().fill();

        stage.addActor(root);
    }

    private void addStat(Table table, Label.LabelStyle style, String name, String value) {
        Label.LabelStyle valStyle = new Label.LabelStyle(game.font, Color.GOLD);
        table.add(new Label(name + ":", style)).left().padRight(25).padBottom(4);
        table.add(new Label(value, valStyle)).right().padBottom(4).row();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.02f, 0.08f, 0.04f, 1f);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); }
}
