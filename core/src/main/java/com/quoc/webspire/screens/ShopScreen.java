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
import com.quoc.webspire.cards.*;
import com.quoc.webspire.entities.Character;
import com.quoc.webspire.potions.*;
import com.quoc.webspire.relics.*;
import com.quoc.webspire.managers.Assets;
import com.quoc.webspire.managers.RunState;
import com.quoc.webspire.managers.SoundManager;
import com.quoc.webspire.cards.CardActor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ShopScreen implements Screen {
    private final Main game;
    private final MapScreen mapScreen;
    private Stage stage;

    private Label goldLabel, statusLabel;
    private Character player;
    private List<Card> masterDeck;

    private boolean cardRemovalUsed = false;
    private List<Card> forSaleCards;
    private List<Relic> shopRelics;
    private List<Potion> potionsForSale;
    private com.quoc.webspire.ui.ParticleManager particleManager = new com.quoc.webspire.ui.ParticleManager();

    private Set<Card> boughtCards = new HashSet<>();
    private Set<Relic> boughtRelics = new HashSet<>();
    private Set<Potion> boughtPotions = new HashSet<>();

    public ShopScreen(Main game, MapScreen mapScreen) {
        this.game = game;
        this.mapScreen = mapScreen;
        this.stage = new Stage(new FitViewport(1280, 720), game.batch);

        this.player = RunState.getInstance().getPlayer();
        this.masterDeck = RunState.getInstance().getMasterDeck();

        // Khởi tạo hàng hóa của shop 1 lần duy nhất cho lượt ghé thăm này
        this.forSaleCards = generateSpecialShopCards(4);
        this.shopRelics = generateShopRelics(2);
        this.potionsForSale = generateShopPotions(2);
    }

    private void proceedToNextFloor() {
        int nextFloor = RunState.getInstance().getCurrentFloor() + 1;
        RunState.getInstance().setCurrentFloor(nextFloor);
        game.setScreen(mapScreen);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        SoundManager.getInstance().playBgm("bgm_menu.mp3");

        stage.clear();

        Table root = new Table();
        root.setFillParent(true);
        root.top().pad(10);

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.font, Color.GOLD);
        Label.LabelStyle goldStyle = new Label.LabelStyle(game.font, Color.YELLOW);
        Label.LabelStyle statusStyle = new Label.LabelStyle(game.font, Color.CYAN);
        Label.LabelStyle sectionHeaderStyle = new Label.LabelStyle(game.font, Color.ORANGE);

        Label titleLabel = new Label("CỬA HÀNG THƯƠNG NHÂN", titleStyle);
        goldLabel = new Label("Vàng của bạn: " + player.getGold() + "  |  Túi Thuốc: " + player.getPotions().size() + "/3", goldStyle);
        statusLabel = new Label("Chào mừng! Bạn muốn mua Bài, Cổ Vật, Bình Thuốc hay Xóa Bài?", statusStyle);

        root.add(titleLabel).padBottom(4).row();
        root.add(goldLabel).padBottom(4).row();
        root.add(statusLabel).padBottom(10).row();

        Table contentTable = new Table();

        // 1. DANH SÁCH BÀI ĐẶC BIỆT
        Table shopCardsTable = new Table();
        for (Card card : forSaleCards) {
            Table itemBox = new Table();
            CardActor cardActor = new CardActor(card, game.font, null);

            TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
            btnStyle.font = game.font;

            boolean isBought = boughtCards.contains(card);
            btnStyle.fontColor = isBought ? Color.GRAY : Color.YELLOW;
            btnStyle.up = Assets.createColorDrawable(isBought ? 0.2f : 0.2f, isBought ? 0.2f : 0.4f, isBought ? 0.2f : 0.2f, 1f);

            TextButton buyBtn = new TextButton(isBought ? "Đã Mua" : "Mua (60 Vàng)", btnStyle);
            buyBtn.setDisabled(isBought);

            buyBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (buyBtn.isDisabled()) return;
                    if (player.getGold() >= 60) {
                        player.addGold(-60);
                        masterDeck.add(card);
                        boughtCards.add(card);
                        buyBtn.setDisabled(true);
                        buyBtn.setText("Đã Mua");
                        buyBtn.getStyle().up = Assets.createColorDrawable(0.2f, 0.2f, 0.2f, 0.5f);
                        updateHeaderLabels();
                        statusLabel.setText("Đã mua thành công lá [" + card.getName() + "]!");
                        SoundManager.getInstance().playSound("sfx_skill.wav");
                    } else {
                        statusLabel.setText("Không đủ Vàng để mua lá bài này!");
                    }
                }
            });

            itemBox.add(cardActor).size(130, 180).row();
            itemBox.add(buyBtn).padTop(4);
            shopCardsTable.add(itemBox).pad(6);
        }
        contentTable.add(shopCardsTable).padBottom(10).row();

        // 2. MUA CỔ VẬT
        Table relicShopTable = new Table();
        if (shopRelics.isEmpty()) {
            Label.LabelStyle emptyStyle = new Label.LabelStyle(game.font, Color.GRAY);
            relicShopTable.add(new Label("Thương nhân: Cậu đã sở hữu tất cả Cổ Vật ta có!", emptyStyle));
        } else {
            for (final Relic relicForSale : shopRelics) {
                final int relicPrice = 140;
                Table singleRelicBox = new Table();
                singleRelicBox.setBackground(Assets.createColorDrawable(0.1f, 0.12f, 0.16f, 0.8f));
                singleRelicBox.pad(6, 12, 6, 12);

                Label.LabelStyle relicNameStyle = new Label.LabelStyle(game.font, Color.ORANGE);
                Label relicLabel = new Label("[" + relicForSale.getName() + "]: " + relicForSale.getDescription(), relicNameStyle);

                TextButton.TextButtonStyle relicBtnStyle = new TextButton.TextButtonStyle();
                relicBtnStyle.font = game.font;

                boolean isBought = boughtRelics.contains(relicForSale);
                relicBtnStyle.fontColor = isBought ? Color.GRAY : Color.GOLD;
                relicBtnStyle.up = Assets.createColorDrawable(isBought ? 0.2f : 0.5f, isBought ? 0.2f : 0.3f, isBought ? 0.2f : 0.1f, 1f);

                final TextButton buyRelicBtn = new TextButton(isBought ? "Đã Sở Hữu" : "Mua (" + relicPrice + " Vàng)", relicBtnStyle);
                buyRelicBtn.setDisabled(isBought);
                buyRelicBtn.pad(4, 10, 4, 10);

                buyRelicBtn.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (buyRelicBtn.isDisabled()) return;
                        if (player.getGold() >= relicPrice) {
                            player.addGold(-relicPrice);
                            player.addRelic(relicForSale);
                            boughtRelics.add(relicForSale);
                            buyRelicBtn.setDisabled(true);
                            buyRelicBtn.setText("Đã Sở Hữu");
                            buyRelicBtn.getStyle().up = Assets.createColorDrawable(0.2f, 0.2f, 0.2f, 0.5f);
                            updateHeaderLabels();
                            statusLabel.setText("Sở hữu Cổ vật [" + relicForSale.getName() + "]!");
                            SoundManager.getInstance().playSound("sfx_skill.wav");
                        } else {
                            statusLabel.setText("Không đủ Vàng để mua Cổ vật!");
                        }
                    }
                });

                singleRelicBox.add(relicLabel).padRight(12);
                singleRelicBox.add(buyRelicBtn);
                relicShopTable.add(singleRelicBox).padBottom(6).row();
            }
        }
        contentTable.add(relicShopTable).padBottom(10).row();

        // 3. MUA BÌNH THUỐC TRỢ CHIẾN
        Table potionShopTable = new Table();
        for (Potion potion : potionsForSale) {
            Table pBox = new Table();
            pBox.setBackground(Assets.createColorDrawable(0.12f, 0.12f, 0.18f, 0.85f));
            pBox.pad(5, 12, 5, 12);

            Label.LabelStyle pNameStyle = new Label.LabelStyle(game.font, potion.getColor());
            Label.LabelStyle pDescStyle = new Label.LabelStyle(game.font, Color.LIGHT_GRAY);
            Label pName = new Label("🧪 [" + potion.getName() + "]: ", pNameStyle);
            Label pDesc = new Label(potion.getDescription(), pDescStyle);

            TextButton.TextButtonStyle pBtnStyle = new TextButton.TextButtonStyle();
            pBtnStyle.font = game.font;

            boolean isBought = boughtPotions.contains(potion);
            pBtnStyle.fontColor = isBought ? Color.GRAY : Color.CYAN;
            pBtnStyle.up = Assets.createColorDrawable(isBought ? 0.2f : 0.15f, isBought ? 0.2f : 0.3f, isBought ? 0.2f : 0.5f, 1f);

            TextButton buyPBtn = new TextButton(isBought ? "Đã Mua" : "Mua (45 Vàng)", pBtnStyle);
            buyPBtn.setDisabled(isBought);
            buyPBtn.pad(3, 8, 3, 8);

            buyPBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (buyPBtn.isDisabled()) return;
                    if (player.getGold() >= 45) {
                        if (player.getPotions().size() < 3) {
                            player.addGold(-45);
                            player.addPotion(potion);
                            boughtPotions.add(potion);
                            buyPBtn.setDisabled(true);
                            buyPBtn.setText("Đã Mua");
                            buyPBtn.getStyle().up = Assets.createColorDrawable(0.2f, 0.2f, 0.2f, 0.5f);
                            updateHeaderLabels();
                            statusLabel.setText("Đã mua thành công bình thuốc [" + potion.getName() + "]!");
                            SoundManager.getInstance().playSound("sfx_skill.wav");
                        } else {
                            statusLabel.setText("Túi thuốc đã đầy! Tối đa 3 bình.");
                        }
                    } else {
                        statusLabel.setText("Không đủ Vàng để mua bình thuốc này!");
                    }
                }
            });

            pBox.add(pName).left().padRight(8);
            pBox.add(pDesc).left().padRight(12);
            pBox.add(buyPBtn);
            potionShopTable.add(pBox).pad(3).row();
        }
        contentTable.add(potionShopTable).padBottom(10).row();

        // 4. DỊCH VỤ XÓA BÀI & RỜI CỬA HÀNG
        TextButton.TextButtonStyle serviceBtnStyle = new TextButton.TextButtonStyle();
        serviceBtnStyle.font = game.font;

        TextButton removeCardBtn;
        if (cardRemovalUsed) {
            serviceBtnStyle.fontColor = Color.GRAY;
            serviceBtnStyle.up = Assets.createColorDrawable(0.2f, 0.2f, 0.2f, 0.5f);
            removeCardBtn = new TextButton("Đã Dùng Dịch Vụ Xóa Bài", serviceBtnStyle);
            removeCardBtn.setDisabled(true);
        } else {
            serviceBtnStyle.fontColor = Color.WHITE;
            serviceBtnStyle.up = Assets.createColorDrawable(0.5f, 0.2f, 0.2f, 1f);
            removeCardBtn = new TextButton("Xóa 1 Lá Bài Thừa (75 Vàng)", serviceBtnStyle);
            removeCardBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (player.getGold() >= 75) {
                        showCardRemovalScreen();
                    } else {
                        statusLabel.setText("Không đủ Vàng cho dịch vụ xóa bài!");
                    }
                }
            });
        }
        removeCardBtn.pad(8, 15, 8, 15);

        TextButton.TextButtonStyle exitBtnStyle = new TextButton.TextButtonStyle();
        exitBtnStyle.font = game.font;
        exitBtnStyle.fontColor = Color.WHITE;
        exitBtnStyle.up = Assets.createColorDrawable(0.25f, 0.4f, 0.25f, 1f);

        TextButton exitBtn = new TextButton("Rời Cửa Hàng", exitBtnStyle);
        exitBtn.pad(8, 25, 8, 25);
        exitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                proceedToNextFloor();
            }
        });

        Table bottomBtnTable = new Table();
        bottomBtnTable.add(removeCardBtn).padRight(20);
        bottomBtnTable.add(exitBtn);

        contentTable.add(bottomBtnTable).padTop(5).padBottom(15);

        ScrollPane scroll = new ScrollPane(contentTable);
        scroll.setFadeScrollBars(false);
        root.add(scroll).expand().fill();

        stage.addActor(root);
    }

    private void updateHeaderLabels() {
        goldLabel.setText("Vàng của bạn: " + player.getGold() + "  |  Túi Thuốc: " + player.getPotions().size() + "/3");
    }

    private List<Card> generateSpecialShopCards(int count) {
        List<Card> pool = new ArrayList<>();
        pool.add(new BashCard());
        pool.add(new InflameCard());
        pool.add(new MetallicizeCard());
        pool.add(new IronWaveCard());
        pool.add(new PoisonStabCard());
        pool.add(new ShockwaveCard());
        pool.add(new HeavyBladeCard());
        pool.add(new CarnageCard());
        pool.add(new TwinStrikeCard());
        pool.add(new PommelStrikeCard());
        pool.add(new UppercutCard());
        pool.add(new CleaveCard());
        pool.add(new ShrugItOffCard());
        pool.add(new ImperviousCard());
        pool.add(new EntrenchCard());
        pool.add(new BloodlettingCard());
        pool.add(new BattleTranceCard());
        pool.add(new DemonFormCard());

        Collections.shuffle(pool);
        return pool.subList(0, Math.min(count, pool.size()));
    }

    private List<Relic> generateShopRelics(int count) {
        List<Relic> candidatePool = new ArrayList<>();
        candidatePool.add(new AnchorRelic());
        candidatePool.add(new VajraRelic());
        candidatePool.add(new LanternRelic());
        candidatePool.add(new EnergyRingRelic());
        candidatePool.add(new StrawberryRelic());
        candidatePool.add(new SmoothStoneRelic());
        candidatePool.add(new MeatOnTheBoneRelic());
        candidatePool.add(new BagOfMarblesRelic());

        List<Relic> unownedPool = new ArrayList<>();
        for (Relic candidate : candidatePool) {
            boolean owned = false;
            for (Relic playerRelic : player.getRelics()) {
                if (playerRelic.getName().equalsIgnoreCase(candidate.getName())) {
                    owned = true;
                    break;
                }
            }
            if (!owned) unownedPool.add(candidate);
        }

        Collections.shuffle(unownedPool);
        return unownedPool.subList(0, Math.min(count, unownedPool.size()));
    }

    private List<Potion> generateShopPotions(int count) {
        List<Potion> pool = new ArrayList<>();
        pool.add(new HealthPotion());
        pool.add(new EnergyPotion());
        pool.add(new StrengthPotion());
        pool.add(new BlockPotion());
        pool.add(new PoisonPotion());
        Collections.shuffle(pool);
        return pool.subList(0, Math.min(count, pool.size()));
    }

    private void showCardRemovalScreen() {
        stage.clear();

        Table removeTable = new Table();
        removeTable.setFillParent(true);
        removeTable.setBackground(Assets.createColorDrawable(0.08f, 0.04f, 0.04f, 0.95f));

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.font, Color.RED);
        Label titleLabel = new Label("CHỌN 1 LÁ BÀI ĐỂ XÓA VĨNH VIỄN KHỎI BỘ BÀI", titleStyle);

        Table cardsGrid = new Table();
        List<Card> deckSnapshot = new ArrayList<>(masterDeck);
        for (Card card : deckSnapshot) {
            CardActor cardActor = new CardActor(card, game.font, () -> {
                player.addGold(-75);
                masterDeck.remove(card);
                cardRemovalUsed = true;
                SoundManager.getInstance().playSound("sfx_skill.wav");
                show(); // Quay lại giao diện Cửa Hàng
            });
            cardsGrid.add(cardActor).size(135, 190).pad(6);
            if ((cardsGrid.getChildren().size % 5) == 0) cardsGrid.row();
        }

        TextButton.TextButtonStyle cancelBtnStyle = new TextButton.TextButtonStyle();
        cancelBtnStyle.font = game.font;
        cancelBtnStyle.fontColor = Color.WHITE;
        cancelBtnStyle.up = Assets.createColorDrawable(0.3f, 0.3f, 0.3f, 1f);

        TextButton cancelBtn = new TextButton("Quay Lại Cửa Hàng", cancelBtnStyle);
        cancelBtn.pad(8, 20, 8, 20);
        cancelBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                show();
            }
        });

        ScrollPane scrollPane = new ScrollPane(cardsGrid);
        scrollPane.setFadeScrollBars(false);

        removeTable.add(titleLabel).padTop(15).padBottom(10).row();
        removeTable.add(scrollPane).expand().fill().padBottom(10).row();
        removeTable.add(cancelBtn).padBottom(15);

        stage.addActor(removeTable);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.08f, 0.06f, 0.08f, 1f);

        particleManager.update(delta);
        particleManager.updateAmbient(delta, 1280, 720, new Color(0.95f, 0.80f, 0.25f, 0.5f), 7);

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
