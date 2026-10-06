package com.quoc.webspire;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
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

public class Main extends ApplicationAdapter {
    private Stage stage;
    private SpriteBatch batch;
    private BitmapFont font;

    private Character player;
    private Enemy monster;
    private DeckManager deckManager;
    private List<Card> masterDeck;

    private int floor = 1;

    private Label playerLabel;
    private Label monsterLabel;
    private Label intentLabel;
    private Label floorLabel;
    private Label relicListLabel;

    private ProgressBar playerHpBar;
    private ProgressBar monsterHpBar;

    private Label drawPileLabel;
    private Label discardPileLabel;

    private Table handTable;
    private TextButton endTurnButton;

    // Overlay Game Over & Phần thưởng
    private Table gameOverOverlay;
    private Label gameOverTitleLabel;

    private Table rewardOverlay;
    private Table rewardCardsTable;
    private Label rewardGoldLabel;
    private Label rewardRelicLabel;

    // Overlay Trạm Nghỉ
    private Table restSiteOverlay;
    private Table forgeCardsTable;
    private Label restSiteStatusLabel;

    // Overlay Cửa Hàng (Shop)
    private Table shopOverlay;
    private Label shopTitleLabel;
    private Label shopGoldLabel;
    private Table shopCardsTable;
    private Table shopRelicTable;
    private Table removeCardTable;
    private TextButton removeCardBtn;

    private List<Card> currentShopCards = new ArrayList<>();
    private boolean boughtRelicInShop = false;
    private boolean usedRemoveService = false;

    private Random random = new Random();

    @Override
    public void create() {
        batch = new SpriteBatch();
        stage = new Stage(new FitViewport(1280, 720), batch);
        Gdx.input.setInputProcessor(stage);

        initFont();
        initGameData();

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.top();

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label.LabelStyle intentStyle = new Label.LabelStyle(font, Color.SCARLET);
        Label.LabelStyle pileStyle = new Label.LabelStyle(font, Color.LIGHT_GRAY);
        Label.LabelStyle floorStyle = new Label.LabelStyle(font, Color.GOLD);
        Label.LabelStyle relicStyle = new Label.LabelStyle(font, Color.ORANGE);

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
        btnStyle.font = font;
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
            // Tầng 10 là Trùm Slime Khổng Lồ 100 HP
            monster = new BossEnemy("TRÙM: Slime Khổng Lồ", 100);
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
            // Hạ gục Boss Tầng 10!
            showGameOverScreen("CHIẾN THẮNG TRÙM! CẬU ĐÃ PHÁ ĐẢO CHƯƠNG 1!", Color.GOLD);
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
            CardActor cardActor = new CardActor(card, font, () -> {
                masterDeck.add(card);
                nextFloor();
            });
            rewardCardsTable.add(cardActor).size(140, 190).pad(10);
        }

        rewardOverlay.setVisible(true);
        endTurnButton.setVisible(false);
    }

    private List<Card> generateRewardCards(int count) {
        List<Card> options = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int roll = random.nextInt(4);
            switch (roll) {
                case 0: options.add(new StrikeCard()); break;
                case 1: options.add(new DefendCard()); break;
                case 2: options.add(new BashCard()); break;
                default: options.add(new IronWaveCard()); break;
            }
        }
        return options;
    }

    private void nextFloor() {
        rewardOverlay.setVisible(false);
        endTurnButton.setVisible(true);

        floor++;

        if (floor == 10) {
            // Tiến tới Màn Trùm
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

        Label.LabelStyle titleStyle = new Label.LabelStyle(font, Color.CHARTREUSE);
        restSiteStatusLabel = new Label("", titleStyle);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = font;
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
                CardActor cardActor = new CardActor(card, font, () -> {
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

        Label.LabelStyle titleStyle = new Label.LabelStyle(font, Color.GOLD);
        Label.LabelStyle goldStyle = new Label.LabelStyle(font, Color.YELLOW);

        shopTitleLabel = new Label("", titleStyle);
        shopGoldLabel = new Label("", goldStyle);

        shopCardsTable = new Table();
        shopRelicTable = new Table();
        removeCardTable = new Table();

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = font;
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
            CardActor cardActor = new CardActor(card, font, () -> {
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
            relicBtnStyle.font = font;
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
            Label soldOutLabel = new Label("[Cổ vật đã được mua]", new Label.LabelStyle(font, Color.GRAY));
            shopRelicTable.add(soldOutLabel);
        }
    }

    private void openRemoveCardMenu() {
        removeCardTable.clear();
        for (Card card : masterDeck) {
            CardActor cardActor = new CardActor(card, font, () -> {
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

    private void restartGame() {
        gameOverOverlay.setVisible(false);
        endTurnButton.setVisible(true);

        initGameData();
        playerHpBar.setRange(0, player.getMaxHp());
        monsterHpBar.setRange(0, monster.getMaxHp());

        startNewCombat();
    }

    private void refreshHandUI() {
        handTable.clear();
        for (Card card : deckManager.getHand()) {
            CardActor cardActor = new CardActor(card, font, () -> {
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

        Label.LabelStyle titleStyle = new Label.LabelStyle(font, Color.GOLD);
        Label.LabelStyle goldStyle = new Label.LabelStyle(font, Color.YELLOW);
        Label.LabelStyle relicRewardStyle = new Label.LabelStyle(font, Color.CYAN);

        Label titleLabel = new Label("CHIẾN THẮNG! CHỌN 1 PHẦN THƯỞNG", titleStyle);
        rewardGoldLabel = new Label("", goldStyle);
        rewardRelicLabel = new Label("", relicRewardStyle);
        rewardCardsTable = new Table();

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = font;
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

        Label.LabelStyle titleStyle = new Label.LabelStyle(font, Color.WHITE);
        gameOverTitleLabel = new Label("", titleStyle);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = font;
        btnStyle.fontColor = Color.WHITE;
        btnStyle.up = createDrawable(0.2f, 0.6f, 0.2f, 1f);

        TextButton restartBtn = new TextButton("Chơi lại từ đầu", btnStyle);
        restartBtn.pad(10, 30, 10, 30);
        restartBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                restartGame();
            }
        });

        gameOverOverlay.add(gameOverTitleLabel).padBottom(30).row();
        gameOverOverlay.add(restartBtn);
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

    private void initFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
        parameter.characters = "aAàÀảẢãÃáÁạẠăĂằẰẳẲẵẴắẮặẶâÂầẦẩẨẫẪấẤậẬbBcCdDđĐeEèÈẻẺẽẼéÉẹẸêÊềỀểỂễỄếẾệỆfFgGhHiIìÌỉỈĩĨíÍịỊjJkKlLmMnNoOòÒỏỎõÕóÓọỌôÔồỒổỔỗỖốỐộỘơƠờỜởỞỡỠớỚợỢpPqQrRsStTuUùÙủỦũŨúÚụỤưƯừỪửỬữỮứỨựỰvVwWxXyYỳỲỷỶỹỸýÝỵỴzZ0123456789!@#$%^&*()_+-=[]{}|;:',.<>/? ";

        font = generator.generateFont(parameter);
        generator.dispose();
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.12f, 0.12f, 0.18f, 1f);

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        font.dispose();
    }
}
