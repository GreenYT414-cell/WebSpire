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
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameScreen implements Screen {
    private final Main game;
    private Stage stage;

    private Character player;
    private Enemy monster;
    private DeckManager deckManager;
    private List<Card> masterDeck;

    private int floor = 1;

    private Label playerLabel, monsterLabel, intentLabel, floorLabel, relicListLabel;
    private ProgressBar playerHpBar, monsterHpBar;
    private Label drawPileLabel, discardPileLabel;

    private Table handTable;
    private TextButton endTurnButton;

    private Table gameOverOverlay;
    private Label gameOverTitleLabel;

    private Table rewardOverlay, rewardCardsTable;
    private Label rewardGoldLabel, rewardRelicLabel;

    private Table restSiteOverlay, forgeCardsTable;
    private Label restSiteStatusLabel;

    private Table shopOverlay, shopCardsTable, shopRelicTable, removeCardTable;
    private Label shopTitleLabel, shopGoldLabel;
    private TextButton removeCardBtn;

    private Table bossRewardOverlay, bossRelicsTable;

    private List<Card> currentShopCards = new ArrayList<>();
    private boolean boughtRelicInShop = false;
    private boolean usedRemoveService = false;

    private Random random = new Random();

    public GameScreen(Main game) {
        this.game = game;
        this.stage = new Stage(new FitViewport(1280, 720), game.batch);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        initGameData();

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.top();

        Label.LabelStyle labelStyle = new Label.LabelStyle(game.font, Color.WHITE);
        Label.LabelStyle intentStyle = new Label.LabelStyle(game.font, Color.SCARLET);
        Label.LabelStyle pileStyle = new Label.LabelStyle(game.font, Color.LIGHT_GRAY);
        Label.LabelStyle floorStyle = new Label.LabelStyle(game.font, Color.GOLD);
        Label.LabelStyle relicStyle = new Label.LabelStyle(game.font, Color.ORANGE);

        floorLabel = new Label("Tầng: " + floor, floorStyle);
        relicListLabel = new Label("", relicStyle);
        playerLabel = new Label("", labelStyle);
        monsterLabel = new Label("", labelStyle);
        intentLabel = new Label("", intentStyle);

        drawPileLabel = new Label("", pileStyle);
        discardPileLabel = new Label("", pileStyle);

        playerHpBar = new ProgressBar(0, player.getMaxHp(), 1, false, createHpBarStyle(Color.GREEN));
        monsterHpBar = new ProgressBar(0, monster.getMaxHp(), 1, false, createHpBarStyle(Color.RED));

        Table topBar = new Table();
        topBar.add(floorLabel).left().padLeft(20);
        topBar.add(relicListLabel).expandX().right().padRight(20);
        rootTable.add(topBar).colspan(2).fillX().padTop(10).row();

        Table playerBox = new Table();
        playerBox.add(playerLabel).row();
        playerBox.add(playerHpBar).width(200).height(20).padTop(8);

        Table monsterBox = new Table();
        monsterBox.add(intentLabel).row();
        monsterBox.add(monsterLabel).row();
        monsterBox.add(monsterHpBar).width(200).height(20).padTop(8);

        rootTable.add(playerBox).expandX().padTop(10);
        rootTable.add(monsterBox).expandX().padTop(10);
        rootTable.row();

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = game.font;
        btnStyle.fontColor = Color.GOLD;
        btnStyle.up = createDrawable(0.2f, 0.2f, 0.3f, 1f);

        endTurnButton = new TextButton("Kết thúc lượt", btnStyle);
        endTurnButton.pad(10, 20, 10, 20);
        endTurnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                endPlayerTurn();
            }
        });
        rootTable.add(endTurnButton).colspan(2).padTop(15).row();

        Table bottomTable = new Table();
        handTable = new Table();

        bottomTable.add(drawPileLabel).width(120).padLeft(20);
        bottomTable.add(handTable).expandX();
        bottomTable.add(discardPileLabel).width(120).padRight(20);

        rootTable.add(bottomTable).colspan(2).expandY().bottom().padBottom(20).fillX();

        stage.addActor(rootTable);

        initGameOverOverlay();
        initRewardOverlay();
        initRestSiteOverlay();
        initShopOverlay();
        initBossRewardOverlay();

        startNewCombat();
    }

    private void initGameData() {
        floor = 1;
        player = new Character("Hiệp Sĩ", 50, 3);
        player.addRelic(new BurningBloodRelic());

        masterDeck = new ArrayList<>();
        for (int i = 0; i < 4; i++) masterDeck.add(new StrikeCard());
        for (int i = 0; i < 4; i++) masterDeck.add(new DefendCard());
        masterDeck.add(new BashCard());

        spawnNextMonster();
    }

    private void spawnNextMonster() {
        if (floor == 10) {
            monster = new BossEnemy("TRÙM CHƯƠNG 1: Slime Khổng Lồ", 100);
        } else if (floor > 10) {
            monster = new DarkKnightEnemy("Hiệp Sĩ Bóng Đêm Tầng " + floor, 65 + (floor * 3));
        } else {
            int monsterHp = 35 + (floor * 5);
            monster = new Enemy("Goblin Tầng " + floor, monsterHp, 0);
        }

        deckManager = new DeckManager();
        deckManager.initCombat(masterDeck);
    }

    private void startNewCombat() {
        player.triggerCombatStartRelics(monster);
        startPlayerTurn();
    }

    private void startPlayerTurn() {
        if (checkGameOver()) return;

        monster.rollIntent();
        player.startTurn();
        deckManager.draw(5);

        updateUI();
        refreshHandUI();
    }

    private void endPlayerTurn() {
        deckManager.discardHand();
        refreshHandUI();

        player.triggerEndTurnPowers();

        monster.startTurn();
        monster.executeIntent(player);

        updateUI();

        if (!checkGameOver()) {
            startPlayerTurn();
        }
    }

    private boolean checkGameOver() {
        if (monster.getHp() <= 0) {
            player.triggerVictoryRelics();
            showRewardScreen();
            return true;
        } else if (player.getHp() <= 0) {
            showGameOverScreen("GAME OVER! CẬU ĐÃ THẤT BẠI!", Color.RED);
            return true;
        }
        return false;
    }

    private void showRewardScreen() {
        if (floor == 10) {
            showBossRewardScreen();
            return;
        }

        int rewardGold = 20 + random.nextInt(15);
        player.addGold(rewardGold);
        rewardGoldLabel.setText("Nhận được: " + rewardGold + " Vàng!");

        if (floor == 2 && player.getRelics().size() < 2) {
            Relic newRelic = new VajraRelic();
            player.addRelic(newRelic);
            rewardRelicLabel.setText("CỔ VẬT MỚI: " + newRelic.getName() + " (" + newRelic.getDescription() + ")");
            rewardRelicLabel.setVisible(true);
        } else {
            rewardRelicLabel.setVisible(false);
        }

        rewardCardsTable.clear();
        List<Card> rewardOptions = generateRewardCards(3);

        for (Card card : rewardOptions) {
            CardActor cardActor = new CardActor(card, game.font, () -> {
                masterDeck.add(card);
                nextFloor();
            });
            rewardCardsTable.add(cardActor).size(140, 190).pad(10);
        }

        rewardOverlay.setVisible(true);
        endTurnButton.setVisible(false);
    }

    private void showBossRewardScreen() {
        bossRelicsTable.clear();

        List<Relic> bossRelics = new ArrayList<>();
        bossRelics.add(new LanternRelic());
        bossRelics.add(new EnergyRingRelic());

        for (Relic relic : bossRelics) {
            TextButton.TextButtonStyle relicBtnStyle = new TextButton.TextButtonStyle();
            relicBtnStyle.font = game.font;
            relicBtnStyle.fontColor = Color.GOLD;
            relicBtnStyle.up = createDrawable(0.2f, 0.1f, 0.3f, 1f);

            TextButton relicBtn = new TextButton(relic.getName() + "\n" + relic.getDescription(), relicBtnStyle);
            relicBtn.pad(15, 20, 15, 20);
            relicBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    player.addRelic(relic);
                    bossRewardOverlay.setVisible(false);
                    startAct2();
                }
            });
            bossRelicsTable.add(relicBtn).pad(15).row();
        }

        bossRewardOverlay.setVisible(true);
        endTurnButton.setVisible(false);
    }

    private void startAct2() {
        floor = 11;
        endTurnButton.setVisible(true);
        spawnNextMonster();
        monsterHpBar.setRange(0, monster.getMaxHp());
        startNewCombat();
    }

    private void initBossRewardOverlay() {
        bossRewardOverlay = new Table();
        bossRewardOverlay.setFillParent(true);
        bossRewardOverlay.setBackground(createDrawable(0.05f, 0f, 0.1f, 0.95f));

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.font, Color.GOLD);
        Label titleLabel = new Label("HẠ GỤC TRÙM! CHỌN 1 CỔ VẬT TRÙM ĐỂ TIẾN VÀO CHƯƠNG 2", titleStyle);

        bossRelicsTable = new Table();

        bossRewardOverlay.add(titleLabel).padBottom(30).row();
        bossRewardOverlay.add(bossRelicsTable).row();

        bossRewardOverlay.setVisible(false);
        stage.addActor(bossRewardOverlay);
    }

    private List<Card> generateRewardCards(int count) {
        List<Card> options = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int roll = random.nextInt(6);
            switch (roll) {
                case 0: options.add(new StrikeCard()); break;
                case 1: options.add(new DefendCard()); break;
                case 2: options.add(new BashCard()); break;
                case 3: options.add(new IronWaveCard()); break;
                case 4: options.add(new InflameCard()); break;
                default: options.add(new MetallicizeCard()); break;
            }
        }
        return options;
    }

    private void nextFloor() {
        rewardOverlay.setVisible(false);
        endTurnButton.setVisible(true);

        floor++;

        if (floor == 10) {
            spawnNextMonster();
            monsterHpBar.setRange(0, monster.getMaxHp());
            startNewCombat();
        } else if (floor % 4 == 0) {
            showShop();
        } else if (floor % 3 == 0) {
            showRestSite();
        } else {
            spawnNextMonster();
            monsterHpBar.setRange(0, monster.getMaxHp());
            startNewCombat();
        }
    }

    private void showRestSite() {
        endTurnButton.setVisible(false);
        restSiteStatusLabel.setText("CẬU ĐÃ ĐẾN TRẠM NGHỈ (TẦNG " + floor + ")");
        forgeCardsTable.clear();
        restSiteOverlay.setVisible(true);
    }

    private void initRestSiteOverlay() {
        restSiteOverlay = new Table();
        restSiteOverlay.setFillParent(true);
        restSiteOverlay.setBackground(createDrawable(0.05f, 0.1f, 0.05f, 0.95f));

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.font, Color.CHARTREUSE);
        restSiteStatusLabel = new Label("", titleStyle);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = game.font;
        btnStyle.fontColor = Color.WHITE;
        btnStyle.up = createDrawable(0.2f, 0.5f, 0.2f, 1f);

        TextButton restBtn = new TextButton("Nghỉ Ngơi (+30% HP)", btnStyle);
        restBtn.pad(10, 20, 10, 20);
        restBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int healAmount = (int)(player.getMaxHp() * 0.3f);
                player.heal(healAmount);
                leaveRestSite();
            }
        });

        TextButton forgeBtn = new TextButton("Rèn Luyện (Nâng Cấp Bài)", btnStyle);
        forgeBtn.pad(10, 20, 10, 20);
        forgeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                openForgeMenu();
            }
        });

        Table btnTable = new Table();
        btnTable.add(restBtn).pad(15);
        btnTable.add(forgeBtn).pad(15);

        forgeCardsTable = new Table();

        restSiteOverlay.add(restSiteStatusLabel).padBottom(20).row();
        restSiteOverlay.add(btnTable).padBottom(20).row();
        restSiteOverlay.add(forgeCardsTable).row();

        restSiteOverlay.setVisible(false);
        stage.addActor(restSiteOverlay);
    }

    private void openForgeMenu() {
        forgeCardsTable.clear();
        for (Card card : masterDeck) {
            if (!card.isUpgraded()) {
                CardActor cardActor = new CardActor(card, game.font, () -> {
                    card.upgrade();
                    leaveRestSite();
                });
                forgeCardsTable.add(cardActor).size(140, 190).pad(5);
            }
        }
    }

    private void leaveRestSite() {
        restSiteOverlay.setVisible(false);
        endTurnButton.setVisible(true);

        spawnNextMonster();
        monsterHpBar.setRange(0, monster.getMaxHp());
        startNewCombat();
    }

    private void showShop() {
        endTurnButton.setVisible(false);
        boughtRelicInShop = false;
        usedRemoveService = false;

        currentShopCards = generateRewardCards(3);

        shopTitleLabel.setText("CỬA HÀNG THƯƠNG NHÂN (TẦNG " + floor + ")");
        refreshShopUI();
        shopOverlay.setVisible(true);
    }

    private void initShopOverlay() {
        shopOverlay = new Table();
        shopOverlay.setFillParent(true);
        shopOverlay.setBackground(createDrawable(0.15f, 0.1f, 0.05f, 0.95f));

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.font, Color.GOLD);
        Label.LabelStyle goldStyle = new Label.LabelStyle(game.font, Color.YELLOW);

        shopTitleLabel = new Label("", titleStyle);
        shopGoldLabel = new Label("", goldStyle);

        shopCardsTable = new Table();
        shopRelicTable = new Table();
        removeCardTable = new Table();

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = game.font;
        btnStyle.fontColor = Color.WHITE;
        btnStyle.up = createDrawable(0.6f, 0.4f, 0.1f, 1f);

        TextButton leaveShopBtn = new TextButton("Rời Cửa Hàng", btnStyle);
        leaveShopBtn.pad(10, 25, 10, 25);
        leaveShopBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                leaveShop();
            }
        });

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

        shopOverlay.add(shopTitleLabel).padBottom(5).row();
        shopOverlay.add(shopGoldLabel).padBottom(15).row();
        shopOverlay.add(shopCardsTable).padBottom(15).row();
        shopOverlay.add(shopRelicTable).padBottom(15).row();
        shopOverlay.add(removeCardBtn).padBottom(10).row();
        shopOverlay.add(removeCardTable).padBottom(15).row();
        shopOverlay.add(leaveShopBtn);

        shopOverlay.setVisible(false);
        stage.addActor(shopOverlay);
    }

    private void refreshShopUI() {
        shopGoldLabel.setText("Vàng hiện tại: " + player.getGold());

        if (usedRemoveService) {
            removeCardBtn.setText("[Đã dùng dịch vụ xóa bài]");
        } else {
            removeCardBtn.setText("Xóa 1 lá bài (75 Vàng)");
        }

        shopCardsTable.clear();
        for (Card card : new ArrayList<>(currentShopCards)) {
            CardActor cardActor = new CardActor(card, game.font, () -> {
                if (player.getGold() >= 50) {
                    player.addGold(-50);
                    masterDeck.add(card);
                    currentShopCards.remove(card);
                    refreshShopUI();
                }
            });
            shopCardsTable.add(cardActor).size(140, 190).pad(10);
        }

        shopRelicTable.clear();
        if (!boughtRelicInShop) {
            Relic anchor = new AnchorRelic();
            TextButton.TextButtonStyle relicBtnStyle = new TextButton.TextButtonStyle();
            relicBtnStyle.font = game.font;
            relicBtnStyle.fontColor = Color.CYAN;
            relicBtnStyle.up = createDrawable(0.1f, 0.3f, 0.4f, 1f);

            TextButton buyRelicBtn = new TextButton("Mua Cổ Vật: " + anchor.getName() + " (150 Vàng)\n" + anchor.getDescription(), relicBtnStyle);
            buyRelicBtn.pad(10, 20, 10, 20);
            buyRelicBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (player.getGold() >= 150) {
                        player.addGold(-150);
                        player.addRelic(anchor);
                        boughtRelicInShop = true;
                        refreshShopUI();
                    }
                }
            });
            shopRelicTable.add(buyRelicBtn);
        } else {
            Label soldOutLabel = new Label("[Cổ vật đã được mua]", new Label.LabelStyle(game.font, Color.GRAY));
            shopRelicTable.add(soldOutLabel);
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
            removeCardTable.add(cardActor).size(120, 160).pad(5);
        }
    }

    private void leaveShop() {
        shopOverlay.setVisible(false);
        endTurnButton.setVisible(true);

        spawnNextMonster();
        monsterHpBar.setRange(0, monster.getMaxHp());
        startNewCombat();
    }

    private void showGameOverScreen(String message, Color titleColor) {
        gameOverTitleLabel.setText(message);
        gameOverTitleLabel.getStyle().fontColor = titleColor;
        gameOverOverlay.setVisible(true);
        endTurnButton.setVisible(false);
    }

    private void refreshHandUI() {
        handTable.clear();
        for (Card card : deckManager.getHand()) {
            CardActor cardActor = new CardActor(card, game.font, () -> {
                if (deckManager.playCard(card, player, monster)) {
                    updateUI();
                    refreshHandUI();
                    checkGameOver();
                }
            });
            handTable.add(cardActor).size(140, 190).pad(5);
        }
    }

    private void updateUI() {
        floorLabel.setText("Tầng: " + floor);

        StringBuilder relicsText = new StringBuilder("Cổ vật: ");
        for (Relic relic : player.getRelics()) {
            relicsText.append("[").append(relic.getName()).append("] ");
        }
        relicListLabel.setText(relicsText.toString());

        String playerStatus = player.getName() + "\nHP: " + player.getHp() + "/" + player.getMaxHp()
            + "\nGiáp: " + player.getBlock() + " | NL: " + player.getEnergy()
            + "\nVàng: " + player.getGold();
        if (player.getStrength() > 0) playerStatus += "\n[Sức mạnh: +" + player.getStrength() + "]";
        if (player.getMetallicize() > 0) playerStatus += "\n[Kim loại hóa: +" + player.getMetallicize() + "]";
        if (player.getVulnerableTurns() > 0) playerStatus += "\n[Dễ tổn thương: " + player.getVulnerableTurns() + "L]";
        playerLabel.setText(playerStatus);

        String monsterStatus = monster.getName() + "\nHP: " + monster.getHp() + "/" + monster.getMaxHp()
            + "\nGiáp: " + monster.getBlock();
        if (monster.getVulnerableTurns() > 0) monsterStatus += "\n[Dễ tổn thương: " + monster.getVulnerableTurns() + "L]";
        monsterLabel.setText(monsterStatus);

        playerHpBar.setValue(player.getHp());
        monsterHpBar.setValue(monster.getHp());

        drawPileLabel.setText("Bài rút:\n" + deckManager.getDrawPile().size() + " lá");
        discardPileLabel.setText("Bài bỏ:\n" + deckManager.getDiscardPile().size() + " lá");

        if (monster.getCurrentIntent() != null) {
            intentLabel.setText("Ý định: " + monster.getCurrentIntent() + " (" + monster.getIntentValue() + ")");
        }
    }

    private void initRewardOverlay() {
        rewardOverlay = new Table();
        rewardOverlay.setFillParent(true);
        rewardOverlay.setBackground(createDrawable(0f, 0f, 0f, 0.88f));

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.font, Color.GOLD);
        Label.LabelStyle goldStyle = new Label.LabelStyle(game.font, Color.YELLOW);
        Label.LabelStyle relicRewardStyle = new Label.LabelStyle(game.font, Color.CYAN);

        Label titleLabel = new Label("CHIẾN THẮNG! CHỌN 1 PHẦN THƯỞNG", titleStyle);
        rewardGoldLabel = new Label("", goldStyle);
        rewardRelicLabel = new Label("", relicRewardStyle);
        rewardCardsTable = new Table();

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = game.font;
        btnStyle.fontColor = Color.LIGHT_GRAY;
        btnStyle.up = createDrawable(0.3f, 0.3f, 0.3f, 1f);

        TextButton skipBtn = new TextButton("Bỏ qua phần thưởng", btnStyle);
        skipBtn.pad(10, 20, 10, 20);
        skipBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                nextFloor();
            }
        });

        rewardOverlay.add(titleLabel).padBottom(10).row();
        rewardOverlay.add(rewardGoldLabel).padBottom(5).row();
        rewardOverlay.add(rewardRelicLabel).padBottom(15).row();
        rewardOverlay.add(rewardCardsTable).padBottom(20).row();
        rewardOverlay.add(skipBtn);

        rewardOverlay.setVisible(false);
        stage.addActor(rewardOverlay);
    }

    private void initGameOverOverlay() {
        gameOverOverlay = new Table();
        gameOverOverlay.setFillParent(true);
        gameOverOverlay.setBackground(createDrawable(0f, 0f, 0f, 0.85f));

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.font, Color.WHITE);
        gameOverTitleLabel = new Label("", titleStyle);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = game.font;
        btnStyle.fontColor = Color.WHITE;
        btnStyle.up = createDrawable(0.2f, 0.6f, 0.2f, 1f);

        TextButton backToMenuBtn = new TextButton("Quay về Menu Chính", btnStyle);
        backToMenuBtn.pad(10, 30, 10, 30);
        backToMenuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        gameOverOverlay.add(gameOverTitleLabel).padBottom(30).row();
        gameOverOverlay.add(backToMenuBtn);
        gameOverOverlay.setVisible(false);

        stage.addActor(gameOverOverlay);
    }

    private ProgressBar.ProgressBarStyle createHpBarStyle(Color fillColor) {
        ProgressBar.ProgressBarStyle style = new ProgressBar.ProgressBarStyle();
        style.background = createDrawable(0.2f, 0.2f, 0.2f, 1f);
        style.knobBefore = createDrawable(fillColor.r, fillColor.g, fillColor.b, 1f);
        return style;
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
        ScreenUtils.clear(0.12f, 0.12f, 0.18f, 1f);

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
    }
}
