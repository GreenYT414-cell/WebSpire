package com.quoc.webspire.screens;

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

import com.quoc.webspire.Main;
import com.quoc.webspire.cards.Card;
import com.quoc.webspire.entities.Character;
import com.quoc.webspire.managers.Assets;
import com.quoc.webspire.managers.RunState;
import com.quoc.webspire.managers.SoundManager;
import com.quoc.webspire.cards.CardActor;
import com.quoc.webspire.ui.ParticleManager;

import java.util.List;

public class RestScreen implements Screen {
    private final Main game;
    private final MapScreen mapScreen;
    private Stage stage;

    private ParticleManager particleManager = new ParticleManager();
    private float stateTime = 0f;

    public RestScreen(Main game, MapScreen mapScreen) {
        this.game = game;
        this.mapScreen = mapScreen;
        this.stage = new Stage(new FitViewport(1280, 720), game.batch);
    }

    private void proceedToNextFloor() {
        int nextFloor = Math.min(15, RunState.getInstance().getCurrentFloor() + 1);
        RunState.getInstance().setCurrentFloor(nextFloor);
        game.setScreen(mapScreen);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        SoundManager.getInstance().playBgm("bgm_menu.mp3");

        Character player = RunState.getInstance().getPlayer();

        Table root = new Table();
        root.setFillParent(true);

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.font, Color.GOLD);
        Label.LabelStyle infoStyle = new Label.LabelStyle(game.font, new Color(1f, 0.9f, 0.7f, 1f));

        Label titleLabel = new Label("🔥  BÃI NGHỈ LỬA TRẠI THẦN BÍ  🔥", titleStyle);
        titleLabel.setFontScale(1.4f);

        Label infoLabel = new Label("Sức Khỏe Hiện Tại: " + player.getHp() + " / " + player.getMaxHp() + " HP", infoStyle);

        // 1. NÚT HỒI MÁU
        int healAmount = (int) (player.getMaxHp() * 0.30f);
        TextButton.TextButtonStyle healBtnStyle = new TextButton.TextButtonStyle();
        healBtnStyle.font = game.font;
        healBtnStyle.fontColor = Color.LIME;
        healBtnStyle.overFontColor = Color.WHITE;
        healBtnStyle.up = Assets.createBorderedDrawable(260, 60, new Color(0.10f, 0.28f, 0.15f, 0.95f), new Color(0.35f, 0.85f, 0.45f, 1f), 2);
        healBtnStyle.over = Assets.createBorderedDrawable(260, 60, new Color(0.18f, 0.42f, 0.25f, 0.98f), new Color(0.65f, 1.0f, 0.75f, 1f), 2);

        TextButton healBtn = new TextButton("⛺ Nghỉ Ngơi (+ " + healAmount + " HP)", healBtnStyle);
        healBtn.pad(14, 25, 14, 25);
        healBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                player.heal(healAmount);
                SoundManager.getInstance().playSound("sfx_skill.wav");
                proceedToNextFloor();
            }
        });

        // 2. NÚT RÈN LUYỆN (NÂNG CẤP BÀI)
        TextButton.TextButtonStyle upgradeBtnStyle = new TextButton.TextButtonStyle();
        upgradeBtnStyle.font = game.font;
        upgradeBtnStyle.fontColor = Color.GOLD;
        upgradeBtnStyle.overFontColor = Color.WHITE;
        upgradeBtnStyle.up = Assets.createBorderedDrawable(260, 60, new Color(0.35f, 0.22f, 0.08f, 0.95f), new Color(1.0f, 0.82f, 0.25f, 1f), 2);
        upgradeBtnStyle.over = Assets.createBorderedDrawable(260, 60, new Color(0.48f, 0.32f, 0.12f, 0.98f), new Color(1.0f, 0.95f, 0.55f, 1f), 2);

        TextButton upgradeBtn = new TextButton("🔨 Rèn Luyện (Nâng Cấp Bài)", upgradeBtnStyle);
        upgradeBtn.pad(14, 25, 14, 25);
        upgradeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showUpgradeCardPicker();
            }
        });

        Table actionBox = new Table();
        actionBox.setBackground(Assets.createBorderedDrawable(640, 180, new Color(0.08f, 0.05f, 0.05f, 0.88f), new Color(0.85f, 0.45f, 0.15f, 0.75f), 1));
        actionBox.pad(25);

        actionBox.add(titleLabel).padBottom(10).row();
        actionBox.add(infoLabel).padBottom(25).row();

        Table btnRow = new Table();
        btnRow.add(healBtn).padRight(20);
        btnRow.add(upgradeBtn);
        actionBox.add(btnRow);

        root.add(actionBox);
        stage.addActor(root);
    }

    // Cửa sổ chọn bài để nâng cấp
    private void showUpgradeCardPicker() {
        stage.clear();

        Table root = new Table();
        root.setFillParent(true);
        root.pad(20);

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.font, Color.GOLD);
        Label titleLabel = new Label("✦  CHỌN 1 LÁ BÀI ĐỂ RÈN LUYỆN NÂNG CẤP (+)  ✦", titleStyle);

        Table cardsGrid = new Table();
        cardsGrid.pad(10);
        List<Card> masterDeck = RunState.getInstance().getMasterDeck();

        int count = 0;
        for (Card card : masterDeck) {
            if (!card.isUpgraded()) {
                CardActor cardActor = new CardActor(card, game.font, () -> {
                    card.upgrade();
                    SoundManager.getInstance().playSound("sfx_skill.wav");
                    proceedToNextFloor();
                });
                cardsGrid.add(cardActor).size(138, 192).pad(8);
                count++;
                if (count % 5 == 0) cardsGrid.row();
            }
        }

        if (count == 0) {
            Label noCardsLabel = new Label("Tất cả lá bài trong bộ đã được nâng cấp!", titleStyle);
            cardsGrid.add(noCardsLabel).pad(30);
        }

        com.badlogic.gdx.scenes.scene2d.ui.ScrollPane scrollPane = new com.badlogic.gdx.scenes.scene2d.ui.ScrollPane(cardsGrid);
        scrollPane.setFadeScrollBars(false);

        root.add(titleLabel).padTop(10).padBottom(15).row();
        root.add(scrollPane).expand().fill().padBottom(15);

        stage.addActor(root);
    }

    @Override
    public void render(float delta) {
        stateTime += delta;
        float warmFlicker = (float) Math.sin(stateTime * 5f) * 0.015f;
        ScreenUtils.clear(0.09f + warmFlicker, 0.05f + warmFlicker * 0.5f, 0.05f, 1f);

        particleManager.update(delta);
        particleManager.updateAmbient(delta, 1280, 720, new Color(1.0f, 0.55f, 0.12f, 0.75f), 14);

        game.batch.begin();
        particleManager.draw(game.batch);
        game.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); }
}
