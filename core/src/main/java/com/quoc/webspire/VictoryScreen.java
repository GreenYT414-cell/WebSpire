package com.quoc.webspire;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

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

        Table root = new Table();
        root.setFillParent(true);

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.font, Color.GOLD);
        Label.LabelStyle infoStyle = new Label.LabelStyle(game.font, Color.WHITE);
        Label.LabelStyle couponStyle = new Label.LabelStyle(game.font, Color.GREEN);

        Label titleLabel = new Label("CHÚC MỪNG! BẠN ĐÃ CHINH PHỤC WEBSPIRE!", titleStyle);
        Label subLabel = new Label("Phần thưởng dành riêng cho tài khoản GameHub (" + PlayerData.email + "):", infoStyle);
        couponLabel = new Label("Đang truy xuất mã ưu đãi từ GameHubDB...", couponStyle);

        // Gọi API lấy Voucher mua sắm GameHub
        GameSyncService.claimCoupon(RunState.getInstance().getCurrentFloor(), new GameSyncService.CouponCallback() {
            @Override
            public void onSuccess(CouponDto coupon) {
                couponLabel.setText("MÃ GIẢM GIÁ: " + coupon.getCode() + "\n" + coupon.getDescription());
            }

            @Override
            public void onError(String errorMessage) {
                couponLabel.setText("Lỗi nhận ưu đãi: " + errorMessage);
            }
        });

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = game.font;
        btnStyle.fontColor = Color.WHITE;
        btnStyle.up = Assets.createColorDrawable(0.2f, 0.6f, 0.2f, 1f);

        TextButton menuBtn = new TextButton("Xác Nhận & Về Menu Chính", btnStyle);
        menuBtn.pad(12, 30, 12, 30);
        menuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        root.add(titleLabel).padBottom(15).row();
        root.add(subLabel).padBottom(15).row();
        root.add(couponLabel).padBottom(30).row();
        root.add(menuBtn);

        stage.addActor(root);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.05f, 0.1f, 0.05f, 1f);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); }
}
