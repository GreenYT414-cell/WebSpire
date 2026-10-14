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

import java.util.List;

public class RestScreen implements Screen {
    private final Main game;
    private final MapScreen mapScreen;
    private Stage stage;

    private Character player;
    private List<Card> masterDeck;

    private Label statusLabel;
    private Table forgeCardsTable;

    public RestScreen(Main game, MapScreen mapScreen) {
        this.game = game;
        this.mapScreen = mapScreen;
        this.stage = new Stage(new FitViewport(1280, 720), game.batch);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        RunState runState = RunState.getInstance();
        this.player = runState.getPlayer();
        this.masterDeck = runState.getMasterDeck();

        Table root = new Table();
        root.setFillParent(true);

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.font, Color.CHARTREUSE);
        Label.LabelStyle infoStyle = new Label.LabelStyle(game.font, Color.WHITE);

        statusLabel = new Label("TRẠM NGHỈ (TẦNG " + runState.getCurrentFloor() + ")\nHP hiện tại: "
            + player.getHp() + "/" + player.getMaxHp(), titleStyle);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = game.font;
        btnStyle.fontColor = Color.WHITE;
        btnStyle.up = createDrawable(0.2f, 0.5f, 0.2f, 1f);

        TextButton restBtn = new TextButton("Nghỉ Ngơi (+30% HP)", btnStyle);
        restBtn.pad(12, 25, 12, 25);
        restBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int healAmount = (int) (player.getMaxHp() * 0.3f);
                player.heal(healAmount);
                game.setScreen(mapScreen);
            }
        });

        TextButton forgeBtn = new TextButton("Rèn Luyện (Nâng Cấp Bài)", btnStyle);
        forgeBtn.pad(12, 25, 12, 25);
        forgeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                openForgeMenu();
            }
        });

        Table actionTable = new Table();
        actionTable.add(restBtn).pad(15);
        actionTable.add(forgeBtn).pad(15);

        forgeCardsTable = new Table();

        root.add(statusLabel).padBottom(20).row();
        root.add(actionTable).padBottom(20).row();
        root.add(forgeCardsTable).row();

        stage.addActor(root);
    }

    private void openForgeMenu() {
        forgeCardsTable.clear();
        for (Card card : masterDeck) {
            if (!card.isUpgraded()) {
                CardActor cardActor = new CardActor(card, game.font, () -> {
                    card.upgrade();
                    game.setScreen(mapScreen);
                });
                forgeCardsTable.add(cardActor).size(130, 180).pad(5);
            }
        }
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
        ScreenUtils.clear(0.05f, 0.12f, 0.05f, 1f);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); }
}
