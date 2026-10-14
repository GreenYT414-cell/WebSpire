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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShopScreen implements Screen {
    private final Main game;
    private final MapScreen mapScreen;
    private Stage stage;

    private Character player;
    private List<Card> masterDeck;

    private Label goldLabel;
    private Table shopCardsTable;
    private Table shopRelicTable;
    private Table removeCardTable;
    private TextButton removeCardBtn;

    private List<Card> shopCards = new ArrayList<>();
    private boolean boughtRelic = false;
    private boolean usedRemoveService = false;
    private Random random = new Random();

    public ShopScreen(Main game, MapScreen mapScreen) {
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

        generateShopCards();

        Table root = new Table();
        root.setFillParent(true);

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.font, Color.GOLD);
        Label.LabelStyle goldStyle = new Label.LabelStyle(game.font, Color.YELLOW);

        Label titleLabel = new Label("CỬA HÀNG THƯƠNG NHÂN (TẦNG " + runState.getCurrentFloor() + ")", titleStyle);
        goldLabel = new Label("Vàng hiện tại: " + player.getGold(), goldStyle);

        shopCardsTable = new Table();
        shopRelicTable = new Table();
        removeCardTable = new Table();

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = game.font;
        btnStyle.fontColor = Color.WHITE;
        btnStyle.up = createDrawable(0.6f, 0.4f, 0.1f, 1f);

        removeCardBtn = new TextButton("Xóa 1 lá bài (75 Vàng)", btnStyle);
        removeCardBtn.pad(8, 15, 8, 15);
        removeCardBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!usedRemoveService && player.getGold() >= 75) {
                    openRemoveCardMenu();
                }
            }
        });

        TextButton leaveBtn = new TextButton("Rời Cửa Hàng", btnStyle);
        leaveBtn.pad(10, 25, 10, 25);
        leaveBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(mapScreen);
            }
        });

        root.add(titleLabel).padBottom(5).row();
        root.add(goldLabel).padBottom(15).row();
        root.add(shopCardsTable).padBottom(15).row();
        root.add(shopRelicTable).padBottom(15).row();
        root.add(removeCardBtn).padBottom(10).row();
        root.add(removeCardTable).padBottom(15).row();
        root.add(leaveBtn);

        stage.addActor(root);

        refreshShopUI();
    }

    private void generateShopCards() {
        shopCards.clear();
        for (int i = 0; i < 3; i++) {
            int roll = random.nextInt(4);
            switch (roll) {
                case 0: shopCards.add(new StrikeCard()); break;
                case 1: shopCards.add(new DefendCard()); break;
                case 2: shopCards.add(new BashCard()); break;
                default: shopCards.add(new IronWaveCard()); break;
            }
        }
    }

    private void refreshShopUI() {
        goldLabel.setText("Vàng hiện tại: " + player.getGold());

        if (usedRemoveService) {
            removeCardBtn.setText("[Đã dùng dịch vụ xóa bài]");
        } else {
            removeCardBtn.setText("Xóa 1 lá bài (75 Vàng)");
        }

        shopCardsTable.clear();
        for (Card card : new ArrayList<>(shopCards)) {
            CardActor cardActor = new CardActor(card, game.font, () -> {
                if (player.getGold() >= 50) {
                    player.addGold(-50);
                    masterDeck.add(card);
                    shopCards.remove(card);
                    refreshShopUI();
                }
            });
            shopCardsTable.add(cardActor).size(130, 180).pad(8);
        }

        shopRelicTable.clear();
        if (!boughtRelic) {
            Relic vajra = new VajraRelic();
            TextButton.TextButtonStyle relicBtnStyle = new TextButton.TextButtonStyle();
            relicBtnStyle.font = game.font;
            relicBtnStyle.fontColor = Color.CYAN;
            relicBtnStyle.up = createDrawable(0.1f, 0.3f, 0.4f, 1f);

            TextButton buyRelicBtn = new TextButton("Mua Cổ Vật: " + vajra.getName() + " (150 Vàng)\n" + vajra.getDescription(), relicBtnStyle);
            buyRelicBtn.pad(10, 20, 10, 20);
            buyRelicBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (player.getGold() >= 150) {
                        player.addGold(-150);
                        player.addRelic(vajra);
                        boughtRelic = true;
                        refreshShopUI();
                    }
                }
            });
            shopRelicTable.add(buyRelicBtn);
        } else {
            Label soldLabel = new Label("[Cổ vật đã mua]", new Label.LabelStyle(game.font, Color.GRAY));
            shopRelicTable.add(soldLabel);
        }
    }

    private void openRemoveCardMenu() {
        removeCardTable.clear();
        for (Card card : masterDeck) {
            CardActor cardActor = new CardActor(card, game.font, () -> {
                if (masterDeck.size() > 1) {
                    player.addGold(-75);
                    masterDeck.remove(card);
                    usedRemoveService = true;
                    removeCardTable.clear();
                    refreshShopUI();
                }
            });
            removeCardTable.add(cardActor).size(110, 150).pad(4);
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
        ScreenUtils.clear(0.15f, 0.1f, 0.05f, 1f);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); }
}
