package com.quoc.webspire.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import com.quoc.webspire.Main;
import com.quoc.webspire.managers.Assets;
import com.quoc.webspire.cards.*;
import com.quoc.webspire.entities.*;
import com.quoc.webspire.entities.Character;
import com.quoc.webspire.managers.*;
import com.quoc.webspire.map.MapNode;
import com.quoc.webspire.network.GameSyncService;
import com.quoc.webspire.potions.*;
import com.quoc.webspire.relics.*;
import com.quoc.webspire.ui.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GameScreen implements Screen {
    private final Main game;
    private final MapScreen mapScreen;
    private Stage stage;
    private MapNode.Type battleType = MapNode.Type.COMBAT;

    private Character player;
    private Enemy monster;
    private DeckManager deckManager;
    private List<Card> masterDeck;

    private Label playerLabel, monsterLabel, intentLabel, floorLabel, relicTooltipLabel;
    private Label playerStatusLabel, monsterStatusLabel;
    private Label playerBlockBadgeLabel, monsterBlockBadgeLabel;
    private ProgressBar playerHpBar, monsterHpBar;
    private Label drawPileLabel, discardPileLabel, exhaustPileLabel;

    private RelicBar relicBar;
    private Table potionBar;

    private Table handTable;
    private TextButton endTurnButton;

    private Table gameOverOverlay;
    private Label gameOverTitleLabel;
    private Table gameOverStatsTable;

    private Table rewardOverlay, rewardCardsTable;
    private Label rewardGoldLabel, rewardRelicLabel, rewardPotionLabel;

    private Table pauseOverlay; // Cửa sổ Pause/Tạm Dừng

    private Texture combatBgTexture;
    private Texture playerTexture;
    private Texture monsterTexture;
    private Random random = new Random();

    private ParticleManager particleManager;
    private float stateTime = 0f;

    private float shakeTime = 0f;
    private float shakeIntensity = 0f;

    private float monsterHitFlashTime = 0f;
    private float playerHitFlashTime = 0f;
    private float playerVisualHp = -1f;
    private float monsterVisualHp = -1f;

    // Các biến trạng thái hoạt ảnh sống động (Lunge & Recoil)
    private float playerLungeOffset = 0f;
    private float monsterLungeOffset = 0f;
    private float playerHurtOffset = 0f;
    private float monsterHurtOffset = 0f;

    public GameScreen(Main game, MapScreen mapScreen) {
        this(game, mapScreen, MapNode.Type.COMBAT);
    }

    public GameScreen(Main game, MapScreen mapScreen, MapNode.Type battleType) {
        this.game = game;
        this.mapScreen = mapScreen;
        this.battleType = battleType != null ? battleType : MapNode.Type.COMBAT;
        this.stage = new Stage(new FitViewport(1280, 720), game.batch);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        SoundManager.getInstance().playBgm("bgm_combat.mp3");
        combatBgTexture = Assets.getTexture("bg_combat.png");
        playerTexture = Assets.getTexture("hero.png");

        particleManager = new ParticleManager();

        initGameData();

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.top();

        Label.LabelStyle labelStyle = new Label.LabelStyle(game.font, Color.WHITE);
        Label.LabelStyle intentStyle = new Label.LabelStyle(game.font, Color.SCARLET);
        Label.LabelStyle pileStyle = new Label.LabelStyle(game.font, Color.LIGHT_GRAY);
        Label.LabelStyle floorStyle = new Label.LabelStyle(game.font, Color.GOLD);
        Label.LabelStyle statusStyle = new Label.LabelStyle(game.font, Color.CYAN);
        Label.LabelStyle relicTooltipStyle = new Label.LabelStyle(game.font, Color.YELLOW);

        floorLabel = new Label("TẦNG: " + RunState.getInstance().getCurrentFloor(), floorStyle);
        relicTooltipLabel = new Label("", relicTooltipStyle);
        relicTooltipLabel.setVisible(false);

        relicBar = new RelicBar(game.font, relicTooltipLabel);

        Label.LabelStyle shieldStyle = new Label.LabelStyle(game.font, Color.CYAN);
        playerBlockBadgeLabel = new Label("", shieldStyle);
        monsterBlockBadgeLabel = new Label("", shieldStyle);

        playerLabel = new Label("", labelStyle);
        monsterLabel = new Label("", labelStyle);
        intentLabel = new Label("", intentStyle);

        playerStatusLabel = new Label("", statusStyle);
        monsterStatusLabel = new Label("", statusStyle);

        drawPileLabel = new Label("", pileStyle);
        discardPileLabel = new Label("", pileStyle);
        exhaustPileLabel = new Label("", pileStyle);

        drawPileLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                DeckInspectorDialog.show(stage, game.font, "DANH SÁCH BÀI RÚT", deckManager.getDrawPile());
            }
        });

        discardPileLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                DeckInspectorDialog.show(stage, game.font, "DANH SÁCH BÀI BỎ", deckManager.getDiscardPile());
            }
        });

        exhaustPileLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                DeckInspectorDialog.show(stage, game.font, "DANH SÁCH BÀI TIÊU BIẾN", deckManager.getExhaustPile());
            }
        });

        playerHpBar = new ProgressBar(0, player.getMaxHp(), 1, false, createHpBarStyle(Color.LIME));
        monsterHpBar = new ProgressBar(0, monster.getMaxHp(), 1, false, createHpBarStyle(Color.FIREBRICK));

        // Nút Menu Tạm Dừng ở góc trên màn hình
        TextButton.TextButtonStyle menuBtnStyle = new TextButton.TextButtonStyle();
        menuBtnStyle.font = game.font;
        menuBtnStyle.fontColor = Color.WHITE;
        menuBtnStyle.up = Assets.createColorDrawable(0.2f, 0.2f, 0.2f, 0.8f);

        TextButton menuBtn = new TextButton("Menu", menuBtnStyle);
        menuBtn.pad(5, 12, 5, 12);
        menuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pauseOverlay.setVisible(true);
            }
        });

        Table topBar = new Table();
        topBar.setBackground(Assets.createColorDrawable(0f, 0f, 0f, 0.6f));
        topBar.add(menuBtn).left().pad(10, 10, 10, 10);
        topBar.add(floorLabel).left().pad(10, 10, 10, 0);

        potionBar = new Table();
        refreshPotionBar();
        topBar.add(potionBar).left().pad(10, 20, 10, 0);

        topBar.add(relicBar).expandX().right().pad(10, 0, 10, 20).row();
        topBar.add(relicTooltipLabel).colspan(4).right().padRight(20).padBottom(5);

        rootTable.add(topBar).colspan(2).fillX().row();

        Table playerBox = new Table();
        playerBox.setBackground(Assets.createBorderedDrawable(250, 110, new Color(0.08f, 0.12f, 0.22f, 0.88f), new Color(0.25f, 0.50f, 0.85f, 0.85f), 1));
        playerBox.pad(10);
        playerBox.add(playerLabel).row();
        playerBox.add(playerBlockBadgeLabel).padTop(4).row();
        playerBox.add(playerHpBar).width(220).height(20).padTop(6).row();
        playerBox.add(playerStatusLabel).padTop(6);

        Table monsterBox = new Table();
        monsterBox.setBackground(Assets.createBorderedDrawable(250, 110, new Color(0.22f, 0.08f, 0.12f, 0.88f), new Color(0.85f, 0.25f, 0.35f, 0.85f), 1));
        monsterBox.pad(10);
        monsterBox.add(intentLabel).padBottom(4).row();
        monsterBox.add(monsterLabel).row();
        monsterBox.add(monsterBlockBadgeLabel).padTop(4).row();
        monsterBox.add(monsterHpBar).width(220).height(20).padTop(6).row();
        monsterBox.add(monsterStatusLabel).padTop(6);

        rootTable.add(playerBox).expandX().padTop(15);
        rootTable.add(monsterBox).expandX().padTop(15);
        rootTable.row();

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = game.font;
        btnStyle.fontColor = Color.GOLD;
        btnStyle.overFontColor = Color.YELLOW;
        btnStyle.up = Assets.createBorderedDrawable(150, 48, new Color(0.12f, 0.12f, 0.28f, 0.95f), new Color(0.85f, 0.65f, 0.20f, 1f), 2);
        btnStyle.over = Assets.createBorderedDrawable(150, 48, new Color(0.24f, 0.24f, 0.45f, 0.98f), new Color(1.0f, 0.85f, 0.35f, 1f), 2);

        endTurnButton = new TextButton("HẾT LƯỢT ▶", btnStyle);
        endTurnButton.pad(10, 14, 10, 14);
        endTurnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                endPlayerTurn();
            }
        });

        Table bottomTable = new Table();
        handTable = new Table();

        // Cột bên trái: Bài rút
        bottomTable.add(drawPileLabel).width(120).padLeft(20).bottom().padBottom(15);
        // Cột giữa: Các lá bài trên tay
        bottomTable.add(handTable).expandX().bottom();
        // Cột bên phải: Nút Kết Thúc Lượt & Bài Bỏ
        Table rightControlBox = new Table();
        rightControlBox.add(endTurnButton).padBottom(6).row();
        rightControlBox.add(discardPileLabel).padBottom(4).row();
        rightControlBox.add(exhaustPileLabel);
        bottomTable.add(rightControlBox).width(160).padRight(20).bottom().padBottom(10);

        rootTable.add(bottomTable).colspan(2).expandY().bottom().padBottom(10).fillX();

        stage.addActor(rootTable);

        initPauseOverlay();
        initGameOverOverlay();
        initRewardOverlay();

        startNewCombat();
    }

    private void initPauseOverlay() {
        pauseOverlay = new Table();
        pauseOverlay.setFillParent(true);
        pauseOverlay.setBackground(Assets.createColorDrawable(0f, 0f, 0f, 0.88f));

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.font, Color.GOLD);
        Label titleLabel = new Label("TẠM DỪNG TRẬN ĐẤU", titleStyle);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = game.font;
        btnStyle.fontColor = Color.WHITE;

        // 1. Nút Tiếp tục
        btnStyle.up = Assets.createColorDrawable(0.2f, 0.5f, 0.2f, 1f);
        TextButton resumeBtn = new TextButton("Tiếp Tục Trận Đấu", btnStyle);
        resumeBtn.pad(10, 25, 10, 25);
        resumeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pauseOverlay.setVisible(false);
            }
        });

        // 2. Nút Quay về Bản đồ
        TextButton.TextButtonStyle mapBtnStyle = new TextButton.TextButtonStyle();
        mapBtnStyle.font = game.font;
        mapBtnStyle.fontColor = Color.WHITE;
        mapBtnStyle.up = Assets.createColorDrawable(0.2f, 0.3f, 0.6f, 1f);

        TextButton backToMapBtn = new TextButton("Quay Về Chọn Màn", mapBtnStyle);
        backToMapBtn.pad(10, 25, 10, 25);
        backToMapBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pauseOverlay.setVisible(false);
                game.setScreen(mapScreen);
            }
        });

        // 3. Nút Từ bỏ (Thua cuộc)
        TextButton.TextButtonStyle giveUpBtnStyle = new TextButton.TextButtonStyle();
        giveUpBtnStyle.font = game.font;
        giveUpBtnStyle.fontColor = Color.WHITE;
        giveUpBtnStyle.up = Assets.createColorDrawable(0.6f, 0.1f, 0.1f, 1f);

        TextButton giveUpBtn = new TextButton("Từ Bỏ (Chấp Nhận Thất Bại)", giveUpBtnStyle);
        giveUpBtn.pad(10, 25, 10, 25);
        giveUpBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pauseOverlay.setVisible(false);
                player.setHp(0);
                showGameOverScreen("BẠN ĐÃ TỪ BỎ TRẬN ĐẤU!", Color.RED);
            }
        });

        pauseOverlay.add(titleLabel).padBottom(25).row();
        pauseOverlay.add(resumeBtn).width(280).padBottom(15).row();
        pauseOverlay.add(backToMapBtn).width(280).padBottom(15).row();
        pauseOverlay.add(giveUpBtn).width(280);

        pauseOverlay.setVisible(false);
        stage.addActor(pauseOverlay);
    }

    private void triggerShake(float intensity, float duration) {
        this.shakeIntensity = intensity;
        this.shakeTime = duration;
    }

    private void initGameData() {
        RunState runState = RunState.getInstance();
        this.player = runState.getPlayer();
        this.masterDeck = runState.getMasterDeck();

        int floor = runState.getCurrentFloor();

        if (battleType == MapNode.Type.BOSS) {
            if (floor == 8) {
                monster = new MidBossEnemy("TRÙM TRUNG GIAN: Hộ Vệ Cổ Vương", 125);
                monsterTexture = Assets.getTexture("midboss_guardian.png");
            } else {
                monster = new BossEnemy("TRÙM CUỐI: Slime Khổng Lồ", 160);
                monsterTexture = Assets.getTexture("boss_slime.png");
            }
        } else if (battleType == MapNode.Type.ELITE) {
            int eliteHp = 85 + (floor * 5);
            monster = new EliteEnemy("QUÁI TINH ANH: Kỵ Sĩ Thiết Giáp [ELITE]", eliteHp);
            monsterTexture = Assets.getTexture("elite_knight.png");
        } else if (floor > 10) {
            monster = new DarkKnightEnemy("Hiệp Sĩ Bóng Đêm", 60 + (floor * 3));
            monsterTexture = Assets.getTexture("dark_knight.png");
        } else {
            int monsterHp = 30 + (floor * 4);
            monster = new Enemy("Goblin Quái Vật", monsterHp, 0);
            monsterTexture = Assets.getTexture("goblin.png");
        }

        deckManager = new DeckManager();
        deckManager.initCombat(masterDeck);
    }

    private void startNewCombat() {
        player.resetCombatState();
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

        RunState.getInstance().incrementTurnsCount();
        player.triggerEndTurnStatuses();

        int prevHp = player.getHp();
        monster.startTurn();

        // Hoạt ảnh quái vật lao lên tấn công người chơi
        monsterLungeOffset = -85f;
        monster.executeIntent(player);
        int damageTaken = prevHp - player.getHp();

        if (damageTaken > 0) {
            playerHurtOffset = -18f;
            triggerShake(12f, 0.3f);
            playerHitFlashTime = 0.22f;
            particleManager.spawnSlash(320, 430);
            FloatingTextManager.spawnText(stage, game.font, "-" + damageTaken, Color.RED, 320, 520);
        }

        updateUI();

        if (!checkGameOver()) {
            startPlayerTurn();
        }
    }

    private boolean checkGameOver() {
        if (monster.getHp() <= 0) {
            player.triggerVictoryRelics();
            SoundManager.getInstance().playSound("sfx_victory.wav");

            if (monster instanceof BossEnemy || monster instanceof MidBossEnemy) {
                RunState.getInstance().incrementBossesKilled();
            } else if (monster instanceof EliteEnemy || battleType == MapNode.Type.ELITE) {
                RunState.getInstance().incrementElitesKilled();
            } else {
                RunState.getInstance().incrementEnemiesKilled();
            }

            if (RunState.getInstance().getCurrentFloor() >= 15 && battleType == MapNode.Type.BOSS) {
                GameSyncService.submitScore(RunState.getInstance(), null);
                game.setScreen(new VictoryScreen(game));
                return true;
            }

            showRewardScreen();
            return true;
        } else if (player.getHp() <= 0) {
            GameSyncService.submitScore(RunState.getInstance(), null);
            showGameOverScreen("GAME OVER! CẬU ĐÃ BỎ MẠNG TRONG THÁP!", Color.RED);
            return true;
        }
        return false;
    }

    private void showRewardScreen() {
        int rewardGold;
        int cardCount = 3;
        boolean allowBasicCards = true;

        if (battleType == MapNode.Type.BOSS) {
            rewardGold = 80 + random.nextInt(35);
            cardCount = 4;
            allowBasicCards = false;
        } else if (battleType == MapNode.Type.ELITE) {
            rewardGold = 55 + random.nextInt(30);
            cardCount = 4;
            allowBasicCards = false;
        } else {
            rewardGold = 20 + random.nextInt(15);
            allowBasicCards = !(monster instanceof DarkKnightEnemy);
        }

        player.addGold(rewardGold);
        rewardGoldLabel.setText("Nhận được: " + rewardGold + " Vàng!");

        // Phần thưởng Cổ vật đặc biệt cho Elite & Boss
        if (battleType == MapNode.Type.ELITE || battleType == MapNode.Type.BOSS) {
            Relic droppedRelic = getRandomRewardRelic();
            if (droppedRelic != null) {
                player.addRelic(droppedRelic);
                rewardRelicLabel.setText("CỔ VẬT RƠI RA: [" + droppedRelic.getName() + "] - " + droppedRelic.getDescription());
                rewardRelicLabel.setVisible(true);
            } else {
                rewardRelicLabel.setVisible(false);
            }
        } else {
            rewardRelicLabel.setVisible(false);
        }

        // Tỷ lệ rơi Bình Thuốc trợ chiến (Elite: 100%, Combat thường: 45%)
        if (battleType == MapNode.Type.ELITE || random.nextFloat() < 0.45f) {
            Potion droppedPotion = getRandomRewardPotion();
            if (droppedPotion != null && player.getPotions().size() < 3) {
                player.addPotion(droppedPotion);
                rewardPotionLabel.setText("BÌNH THUỐC RƠI RA: [" + droppedPotion.getName() + "] - " + droppedPotion.getDescription());
                rewardPotionLabel.setVisible(true);
            } else {
                rewardPotionLabel.setVisible(false);
            }
        } else {
            rewardPotionLabel.setVisible(false);
        }

        rewardCardsTable.clear();
        List<Card> rewardOptions = generateRewardCards(cardCount, allowBasicCards);

        for (Card card : rewardOptions) {
            CardActor cardActor = new CardActor(card, game.font, () -> {
                masterDeck.add(card);
                finishCombatAndReturnToMap();
            });
            rewardCardsTable.add(cardActor).size(140, 195).pad(10);
        }

        rewardOverlay.setVisible(true);
        endTurnButton.setVisible(false);
    }

    private Relic getRandomRewardRelic() {
        List<Relic> pool = new ArrayList<>();
        pool.add(new AnchorRelic());
        pool.add(new VajraRelic());
        pool.add(new LanternRelic());
        pool.add(new EnergyRingRelic());
        pool.add(new StrawberryRelic());
        pool.add(new SmoothStoneRelic());
        pool.add(new MeatOnTheBoneRelic());
        pool.add(new BagOfMarblesRelic());

        List<Relic> unowned = new ArrayList<>();
        for (Relic r : pool) {
            boolean owned = false;
            for (Relic p : player.getRelics()) {
                if (p.getName().equalsIgnoreCase(r.getName())) {
                    owned = true;
                    break;
                }
            }
            if (!owned) unowned.add(r);
        }
        if (unowned.isEmpty()) return null;
        Collections.shuffle(unowned);
        return unowned.get(0);
    }

    private Potion getRandomRewardPotion() {
        List<Potion> pool = new ArrayList<>();
        pool.add(new HealthPotion());
        pool.add(new EnergyPotion());
        pool.add(new StrengthPotion());
        pool.add(new BlockPotion());
        pool.add(new PoisonPotion());
        Collections.shuffle(pool);
        return pool.get(0);
    }

    private void finishCombatAndReturnToMap() {
        player.resetCombatState();
        rewardOverlay.setVisible(false);
        endTurnButton.setVisible(true);

        // Tăng tầng tiến trình lên +1 sau khi hoàn thành trận đấu (chỉ khi chưa đến tầng cuối)
        int currentFloor = RunState.getInstance().getCurrentFloor();
        if (currentFloor < 15) {
            RunState.getInstance().setCurrentFloor(currentFloor + 1);
        }

        game.setScreen(mapScreen);
    }

    private List<Card> generateRewardCards(int count, boolean allowBasic) {
        List<Card> cardPool = new ArrayList<>();
        if (allowBasic) {
            cardPool.add(new StrikeCard());
            cardPool.add(new DefendCard());
        }
        cardPool.add(new BashCard());
        cardPool.add(new InflameCard());
        cardPool.add(new MetallicizeCard());
        cardPool.add(new IronWaveCard());
        cardPool.add(new PoisonStabCard());
        cardPool.add(new ShockwaveCard());
        cardPool.add(new HeavyBladeCard());
        cardPool.add(new CarnageCard());
        cardPool.add(new TwinStrikeCard());
        cardPool.add(new PommelStrikeCard());
        cardPool.add(new UppercutCard());
        cardPool.add(new CleaveCard());
        cardPool.add(new ShrugItOffCard());
        cardPool.add(new ImperviousCard());
        cardPool.add(new EntrenchCard());
        cardPool.add(new BloodlettingCard());
        cardPool.add(new BattleTranceCard());
        cardPool.add(new DemonFormCard());

        Collections.shuffle(cardPool);
        return cardPool.subList(0, Math.min(count, cardPool.size()));
    }

    private void showGameOverScreen(String message, Color titleColor) {
        gameOverTitleLabel.setText(message);
        gameOverTitleLabel.getStyle().fontColor = titleColor;

        gameOverStatsTable.clear();
        Label.LabelStyle headerStyle = new Label.LabelStyle(game.font, Color.GOLD);
        Label.LabelStyle keyStyle = new Label.LabelStyle(game.font, Color.LIGHT_GRAY);
        Label.LabelStyle valStyle = new Label.LabelStyle(game.font, Color.CYAN);

        Label subHeader = new Label("--- BẢNG THỐNG KÊ CHI TIẾT HÀNH TRÌNH ---", headerStyle);
        gameOverStatsTable.add(subHeader).colspan(2).padBottom(15).row();

        RunState rs = RunState.getInstance();
        addStatRow("Tầng cao nhất đạt được:", rs.getCurrentFloor() + " / 15", keyStyle, valStyle);
        addStatRow("Kẻ địch thường đã diệt:", "" + rs.getEnemiesKilled(), keyStyle, valStyle);
        addStatRow("Quái Tinh Anh (Elite) đã diệt:", "" + rs.getElitesKilled(), keyStyle, valStyle);
        addStatRow("Thủ Lĩnh (Boss) đã hạ:", "" + rs.getBossesKilled(), keyStyle, valStyle);
        addStatRow("Tổng sát thương đã gây ra:", "" + rs.getTotalDamageDealt(), keyStyle, valStyle);
        addStatRow("Tổng sát thương gánh chịu:", "" + rs.getTotalDamageTaken(), keyStyle, valStyle);
        addStatRow("Tổng Vàng đã tích lũy:", "" + rs.getTotalGoldEarned(), keyStyle, valStyle);
        addStatRow("Số lá bài đã sử dụng:", "" + rs.getCardsPlayed(), keyStyle, valStyle);
        addStatRow("Tổng số lượt đã đánh:", "" + rs.getTurnsCount(), keyStyle, valStyle);
        addStatRow("Quy mô bộ bài hiện tại:", masterDeck.size() + " lá bài", keyStyle, valStyle);
        addStatRow("Số Cổ Vật đã sở hữu:", player.getRelics().size() + " cổ vật", keyStyle, valStyle);

        gameOverOverlay.setVisible(true);
        endTurnButton.setVisible(false);
    }

    private void addStatRow(String key, String value, Label.LabelStyle keyStyle, Label.LabelStyle valStyle) {
        Label k = new Label(key, keyStyle);
        Label v = new Label(value, valStyle);
        gameOverStatsTable.add(k).left().padRight(40).padBottom(5);
        gameOverStatsTable.add(v).right().padBottom(5).row();
    }

    private void refreshHandUI() {
        handTable.clear();
        int handSize = deckManager.getHand().size();
        float cardWidth = 138f;
        float cardHeight = 192f;
        float cardPad = 4f;

        if (handSize > 5) {
            cardWidth = Math.max(98f, 760f / handSize);
            cardHeight = cardWidth * 1.39f;
            cardPad = Math.max(1f, 3f - (handSize - 5) * 0.5f);
        }

        int index = 0;
        for (Card card : deckManager.getHand()) {
            final int cardIdx = index++;
            CardActor cardActor = new CardActor(card, game.font, () -> {
                if (player.getEnergy() < card.getCost()) {
                    FloatingTextManager.spawnText(stage, game.font, "Không đủ Năng Lượng!", Color.ORANGE, 320, 520);
                    return;
                }

                int monsterPrevHp = monster.getHp();
                int playerPrevBlock = player.getBlock();

                if (deckManager.playCard(card, player, monster)) {
                    int damageDealt = monsterPrevHp - monster.getHp();
                    int blockGained = player.getBlock() - playerPrevBlock;

                    switch (card.getType()) {
                        case ATTACK:
                            playerLungeOffset = 80f;
                            monsterHurtOffset = 18f;
                            SoundManager.getInstance().playSound("sfx_attack.wav");
                            if (card instanceof HeavyBladeCard) {
                                particleManager.spawnHeavyBlade(960, 430);
                                triggerShake(12f, 0.35f);
                            } else if (card instanceof CleaveCard) {
                                particleManager.spawnSlash(960, 430);
                                particleManager.spawnSlash(930, 400);
                                triggerShake(9f, 0.25f);
                            } else {
                                particleManager.spawnSlash(960, 430);
                                triggerShake(8f, 0.2f);
                            }
                            break;
                        case DEFENSE:
                            SoundManager.getInstance().playSound("sfx_defend.wav");
                            if (card instanceof ImperviousCard) {
                                particleManager.spawnImpervious(320, 430);
                                triggerShake(8f, 0.3f);
                            } else {
                                particleManager.spawnShield(320, 430);
                            }
                            break;
                        case SKILL:
                        case POWER:
                            SoundManager.getInstance().playSound("sfx_skill.wav");
                            if (card instanceof DemonFormCard) {
                                particleManager.spawnDemonForm(320, 430);
                                triggerShake(14f, 0.45f);
                            } else if (card instanceof ShockwaveCard) {
                                particleManager.spawnShield(960, 430);
                                particleManager.spawnPoison(960, 430);
                                triggerShake(10f, 0.3f);
                            } else {
                                particleManager.spawnPoison(320, 430);
                            }
                            break;
                    }

                    if (damageDealt > 0) {
                        monsterHitFlashTime = 0.22f;
                        FloatingTextManager.spawnText(stage, game.font, "-" + damageDealt, Color.YELLOW, 960, 520);
                    }
                    if (blockGained > 0) {
                        FloatingTextManager.spawnText(stage, game.font, "+" + blockGained + " Giáp", Color.CYAN, 320, 520);
                    }

                    updateUI();
                    refreshHandUI();
                    checkGameOver();
                }
            });

            boolean canPlay = (player.getEnergy() >= card.getCost());
            cardActor.setPlayable(canPlay);

            float mid = (handSize - 1) / 2f;
            float angle = (mid - cardIdx) * 3.0f;
            cardActor.setFanRotation(angle);

            handTable.add(cardActor).size(cardWidth, cardHeight).pad(cardPad);
        }
    }

    private void refreshPotionBar() {
        if (potionBar == null) return;
        potionBar.clear();
        Label.LabelStyle labelStyle = new Label.LabelStyle(game.font, Color.ORANGE);
        potionBar.add(new Label("Thuốc: ", labelStyle)).padRight(5);

        List<Potion> potions = player.getPotions();
        for (int i = 0; i < 3; i++) {
            if (i < potions.size()) {
                final Potion potion = potions.get(i);
                TextButton.TextButtonStyle pBtnStyle = new TextButton.TextButtonStyle();
                pBtnStyle.font = game.font;
                pBtnStyle.fontColor = Color.WHITE;
                pBtnStyle.up = Assets.createColorDrawable(potion.getColor().r * 0.7f, potion.getColor().g * 0.7f, potion.getColor().b * 0.7f, 1f);

                String shortName = potion.getName().replace("Bình ", "🧪 ");
                TextButton pBtn = new TextButton(shortName, pBtnStyle);
                pBtn.pad(2, 6, 2, 6);
                pBtn.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        potion.use(player, monster);
                        player.removePotion(potion);
                        particleManager.spawnPotionBurst(320, 430, potion.getColor());
                        FloatingTextManager.spawnText(stage, game.font, potion.getName() + "!", potion.getColor(), 320, 540);
                        SoundManager.getInstance().playSound("sfx_skill.wav");
                        updateUI();
                        refreshHandUI();
                        checkGameOver();
                    }
                });
                potionBar.add(pBtn).padRight(4);
            } else {
                TextButton.TextButtonStyle emptyStyle = new TextButton.TextButtonStyle();
                emptyStyle.font = game.font;
                emptyStyle.fontColor = Color.DARK_GRAY;
                emptyStyle.up = Assets.createColorDrawable(0.15f, 0.15f, 0.15f, 0.6f);
                TextButton emptyBtn = new TextButton("🧪 [Trống]", emptyStyle);
                emptyBtn.setDisabled(true);
                emptyBtn.pad(2, 6, 2, 6);
                potionBar.add(emptyBtn).padRight(4);
            }
        }
    }

    private void updateUI() {
        relicBar.refresh();
        refreshPotionBar();

        String playerText = "⚔ " + player.getName() + " (Tầng " + RunState.getInstance().getCurrentFloor() + ")\n"
            + "❤ HP: " + player.getHp() + " / " + player.getMaxHp() + "\n"
            + "💰 " + player.getGold() + " Vàng  |  ⚡ " + player.getEnergy() + "/" + player.getMaxEnergy() + " NL";
        playerLabel.setText(playerText);

        String monsterText = "👹 " + monster.getName() + "\n"
            + "❤ HP: " + monster.getHp() + " / " + monster.getMaxHp();
        monsterLabel.setText(monsterText);

        playerHpBar.setValue(player.getHp());
        monsterHpBar.setValue(monster.getHp());

        if (player.getBlock() > 0) {
            playerBlockBadgeLabel.setText("🛡 GIÁP BẢO VỆ: +" + player.getBlock());
            playerBlockBadgeLabel.setVisible(true);
        } else {
            playerBlockBadgeLabel.setVisible(false);
        }

        if (monster.getBlock() > 0) {
            monsterBlockBadgeLabel.setText("🛡 GIÁP ĐANG CHẶN: +" + monster.getBlock());
            monsterBlockBadgeLabel.setVisible(true);
        } else {
            monsterBlockBadgeLabel.setVisible(false);
        }

        StringBuilder pStatus = new StringBuilder();
        for (Map.Entry<StatusEffect.Type, Integer> entry : player.getStatuses().entrySet()) {
            if (entry.getValue() > 0) {
                pStatus.append(formatStatusBadge(entry.getKey(), entry.getValue())).append(" ");
            }
        }
        playerStatusLabel.setText(pStatus.toString().trim());

        StringBuilder mStatus = new StringBuilder();
        for (Map.Entry<StatusEffect.Type, Integer> entry : monster.getStatuses().entrySet()) {
            if (entry.getValue() > 0) {
                mStatus.append(formatStatusBadge(entry.getKey(), entry.getValue())).append(" ");
            }
        }
        monsterStatusLabel.setText(mStatus.toString().trim());

        drawPileLabel.setText("📖 Bài rút (Xem):\n" + deckManager.getDrawPile().size() + " lá");
        discardPileLabel.setText("🗑 Bài bỏ (Xem):\n" + deckManager.getDiscardPile().size() + " lá");
        exhaustPileLabel.setText("☠ Tiêu biến (Xem):\n" + deckManager.getExhaustPile().size() + " lá");

        if (monster.getCurrentIntent() != null) {
            intentLabel.setText("Ý định: " + monster.getCurrentIntent() + " (" + monster.getIntentValue() + ")");
        }
    }

    private String formatStatusBadge(StatusEffect.Type type, int val) {
        switch (type) {
            case STRENGTH:   return "⚔ Sức Mạnh: +" + val;
            case DEXTERITY:  return "🍃 Khéo Léo: +" + val;
            case VULNERABLE: return "💥 Dễ Tổn Thương: " + val;
            case WEAK:       return "🕸 Yếu Mềm: " + val;
            case POISON:     return "☠ Độc: " + val;
            case METALLICIZE: return "🛡 Kim Hóa: +" + val;
            case DEMON_FORM: return "😈 Hình Thái Ác Quỷ: +" + val;
            default: return "[" + type.getName() + ": " + val + "]";
        }
    }

    private void initRewardOverlay() {
        rewardOverlay = new Table();
        rewardOverlay.setFillParent(true);
        rewardOverlay.setBackground(Assets.createColorDrawable(0f, 0f, 0f, 0.88f));

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.font, Color.GOLD);
        Label.LabelStyle goldStyle = new Label.LabelStyle(game.font, Color.YELLOW);
        Label.LabelStyle relicRewardStyle = new Label.LabelStyle(game.font, Color.CYAN);
        Label.LabelStyle potionRewardStyle = new Label.LabelStyle(game.font, Color.GREEN);

        Label titleLabel = new Label("CHIẾN THẮNG! CHỌN 1 PHẦN THƯỞNG", titleStyle);
        rewardGoldLabel = new Label("", goldStyle);
        rewardRelicLabel = new Label("", relicRewardStyle);
        rewardPotionLabel = new Label("", potionRewardStyle);
        rewardCardsTable = new Table();

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = game.font;
        btnStyle.fontColor = Color.LIGHT_GRAY;
        btnStyle.up = Assets.createColorDrawable(0.3f, 0.3f, 0.3f, 1f);

        TextButton skipBtn = new TextButton("Bỏ qua phần thưởng", btnStyle);
        skipBtn.pad(10, 20, 10, 20);
        skipBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                finishCombatAndReturnToMap();
            }
        });

        rewardOverlay.add(titleLabel).padBottom(10).row();
        rewardOverlay.add(rewardGoldLabel).padBottom(5).row();
        rewardOverlay.add(rewardRelicLabel).padBottom(5).row();
        rewardOverlay.add(rewardPotionLabel).padBottom(15).row();
        rewardOverlay.add(rewardCardsTable).padBottom(20).row();
        rewardOverlay.add(skipBtn);

        rewardOverlay.setVisible(false);
        stage.addActor(rewardOverlay);
    }

    private void initGameOverOverlay() {
        gameOverOverlay = new Table();
        gameOverOverlay.setFillParent(true);
        gameOverOverlay.setBackground(Assets.createColorDrawable(0f, 0f, 0f, 0.92f));

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.font, Color.RED);
        gameOverTitleLabel = new Label("", titleStyle);
        gameOverStatsTable = new Table();

        TextButton.TextButtonStyle restartBtnStyle = new TextButton.TextButtonStyle();
        restartBtnStyle.font = game.font;
        restartBtnStyle.fontColor = Color.YELLOW;
        restartBtnStyle.up = Assets.createColorDrawable(0.15f, 0.45f, 0.2f, 1f);

        TextButton restartBtn = new TextButton("Chơi Lại Ván Mới", restartBtnStyle);
        restartBtn.pad(10, 25, 10, 25);
        restartBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                RunState.getInstance().startNewRun();
                game.setScreen(new MapScreen(game));
            }
        });

        TextButton.TextButtonStyle menuBtnStyle = new TextButton.TextButtonStyle();
        menuBtnStyle.font = game.font;
        menuBtnStyle.fontColor = Color.WHITE;
        menuBtnStyle.up = Assets.createColorDrawable(0.3f, 0.3f, 0.3f, 1f);

        TextButton backToMenuBtn = new TextButton("Quay Về Menu Chính", menuBtnStyle);
        backToMenuBtn.pad(10, 25, 10, 25);
        backToMenuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        Table buttonsTable = new Table();
        buttonsTable.add(restartBtn).padRight(20);
        buttonsTable.add(backToMenuBtn);

        gameOverOverlay.add(gameOverTitleLabel).padBottom(15).row();
        gameOverOverlay.add(gameOverStatsTable).padBottom(25).row();
        gameOverOverlay.add(buttonsTable);
        gameOverOverlay.setVisible(false);

        stage.addActor(gameOverOverlay);
    }

    private ProgressBar.ProgressBarStyle createHpBarStyle(Color fillColor) {
        ProgressBar.ProgressBarStyle style = new ProgressBar.ProgressBarStyle();
        style.background = Assets.createBorderedDrawable(220, 20, new Color(0.06f, 0.06f, 0.10f, 0.95f), new Color(0.35f, 0.40f, 0.55f, 0.85f), 1);
        style.knobBefore = Assets.createColorDrawable(fillColor.r, fillColor.g, fillColor.b, 1f);
        return style;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.08f, 0.08f, 0.12f, 1f);

        stateTime += delta;
        particleManager.update(delta);
        particleManager.updateAmbient(delta, 1280, 720, new Color(0.85f, 0.65f, 0.30f, 0.6f), 5);

        // Hồi vị hoạt ảnh Lunge & Recoil
        playerLungeOffset = MathUtils.lerp(playerLungeOffset, 0f, 10f * delta);
        monsterLungeOffset = MathUtils.lerp(monsterLungeOffset, 0f, 10f * delta);
        playerHurtOffset = MathUtils.lerp(playerHurtOffset, 0f, 8f * delta);
        monsterHurtOffset = MathUtils.lerp(monsterHurtOffset, 0f, 8f * delta);

        float offsetX = 0f;
        float offsetY = 0f;
        if (shakeTime > 0) {
            shakeTime -= delta;
            offsetX = MathUtils.random(-shakeIntensity, shakeIntensity);
            offsetY = MathUtils.random(-shakeIntensity, shakeIntensity);
        }

        stage.getCamera().position.set(640 + offsetX, 360 + offsetY, 0);
        stage.getCamera().update();

        float playerIdleY = (float) Math.sin(stateTime * 2.8f) * 6f;
        float monsterIdleY = (float) Math.cos(stateTime * 2.8f) * 6f;

        game.batch.setProjectionMatrix(stage.getCamera().combined);
        game.batch.begin();

        if (combatBgTexture != null) {
            game.batch.draw(combatBgTexture, 0, 0, 1280, 720);
        }

        // Smooth Lerp HP Bar decrease
        if (playerVisualHp < 0) playerVisualHp = player.getHp();
        if (monsterVisualHp < 0) monsterVisualHp = monster.getHp();
        playerVisualHp = MathUtils.lerp(playerVisualHp, player.getHp(), 8f * delta);
        monsterVisualHp = MathUtils.lerp(monsterVisualHp, monster.getHp(), 8f * delta);
        playerHpBar.setValue(playerVisualHp);
        monsterHpBar.setValue(monsterVisualHp);

        // Flash timer countdown
        if (playerHitFlashTime > 0) playerHitFlashTime -= delta;
        if (monsterHitFlashTime > 0) monsterHitFlashTime -= delta;

        // 1. Vẽ bóng đổ tiếp đất (Ground Shadows) cho Nhân vật & Quái vật
        Texture shadowTex = Assets.getShadowTexture();
        if (shadowTex != null) {
            game.batch.setColor(1f, 1f, 1f, 0.65f);
            game.batch.draw(shadowTex, 230 + playerLungeOffset + playerHurtOffset, 310, 180, 42);
            float shadowMW = monster instanceof BossEnemy ? 210 : 180;
            game.batch.draw(shadowTex, 870 + monsterLungeOffset + monsterHurtOffset - (shadowMW - 180) / 2f, 310, shadowMW, 42);
            game.batch.setColor(Color.WHITE);
        }

        // 2. Vẽ Nhân vật với hiệu ứng nhịp thở phập phồng (Breathing Squash & Stretch)
        float pSquashX = 1.0f + 0.02f * MathUtils.sin(stateTime * 2.8f);
        float pSquashY = 1.0f - 0.02f * MathUtils.sin(stateTime * 2.8f);

        if (playerTexture != null) {
            Color pColor = playerHitFlashTime > 0 ? new Color(1.0f, 0.25f, 0.25f, 1f) : Color.WHITE;
            game.batch.setColor(pColor);
            float drawPX = 240 + playerLungeOffset + playerHurtOffset;
            float drawPY = 330 + playerIdleY;
            game.batch.draw(new com.badlogic.gdx.graphics.g2d.TextureRegion(playerTexture), drawPX, drawPY, 80f, 0f, 160f, 200f, pSquashX, pSquashY, 0f);
            game.batch.setColor(Color.WHITE);
        }

        // 3. Vẽ Quái vật với màu hào quang tương ứng & nhịp thở
        if (monsterTexture != null) {
            Color savedBatchColor = game.batch.getColor().cpy();
            float mWidth = 160;
            float mHeight = 200;
            float mX = 880 + monsterLungeOffset + monsterHurtOffset;
            float mY = 330 + monsterIdleY;

            if (monsterHitFlashTime > 0) {
                game.batch.setColor(1.0f, 0.2f, 0.2f, 1f);
            } else if (monster instanceof BossEnemy) {
                game.batch.setColor(1.0f, 0.65f, 0.95f, 1f);
                mWidth = 190;
                mHeight = 225;
                mX = 865 + monsterLungeOffset + monsterHurtOffset;
            } else if (monster instanceof MidBossEnemy) {
                game.batch.setColor(1.0f, 0.85f, 0.35f, 1f);
                mWidth = 180;
                mHeight = 215;
                mX = 870 + monsterLungeOffset + monsterHurtOffset;
            } else if (monster instanceof EliteEnemy) {
                game.batch.setColor(1.0f, 0.35f, 0.35f, 1f);
                mWidth = 175;
                mHeight = 210;
                mX = 872 + monsterLungeOffset + monsterHurtOffset;
            }

            float mSquashX = 1.0f + 0.025f * MathUtils.cos(stateTime * 2.5f);
            float mSquashY = 1.0f - 0.025f * MathUtils.cos(stateTime * 2.5f);
            game.batch.draw(new com.badlogic.gdx.graphics.g2d.TextureRegion(monsterTexture), mX, mY, mWidth / 2f, 0f, mWidth, mHeight, mSquashX, mSquashY, 0f);
            game.batch.setColor(savedBatchColor);

            // 4. Huy hiệu Ý định (Intent Overhead Display) lơ lửng trên đầu quái
            if (monster.getCurrentIntent() != null) {
                float intentX = mX + mWidth / 2f - 65f;
                float intentY = mY + mHeight + 10f;
                String intentIcon = "⚔ ";
                Color intentBg = new Color(0.45f, 0.12f, 0.12f, 0.92f);
                Color intentBorder = new Color(0.95f, 0.35f, 0.35f, 1f);

                String intentName = monster.getCurrentIntent();
                if (intentName.contains("Thủ") || intentName.contains("Giáp")) {
                    intentIcon = "🛡 ";
                    intentBg = new Color(0.12f, 0.25f, 0.45f, 0.92f);
                    intentBorder = new Color(0.35f, 0.65f, 0.95f, 1f);
                } else if (intentName.contains("Độc")) {
                    intentIcon = "☠ ";
                    intentBg = new Color(0.12f, 0.40f, 0.18f, 0.92f);
                    intentBorder = new Color(0.35f, 0.95f, 0.45f, 1f);
                } else if (intentName.contains("Cường") || intentName.contains("Hóa") || intentName.contains("Phép")) {
                    intentIcon = "🔮 ";
                    intentBg = new Color(0.35f, 0.12f, 0.45f, 0.92f);
                    intentBorder = new Color(0.85f, 0.45f, 0.95f, 1f);
                }

                Assets.createBorderedDrawable(130, 28, intentBg, intentBorder, 1).draw(game.batch, intentX, intentY, 130, 28);
                String badgeText = intentIcon + (monster.getIntentValue() > 0 ? (monster.getIntentValue() + " ST") : intentName);
                game.font.setColor(Color.WHITE);
                game.font.draw(game.batch, badgeText, intentX + 12, intentY + 20);
            }
        }

        // 5. Viên Ngọc Năng Lượng (Iconic Slay the Spire Energy Orb)
        Texture orbTex = Assets.getEnergyOrbTexture();
        if (orbTex != null) {
            float baseOrbSize = 90f;
            float pulse = (player.getEnergy() > 0) ? (1.0f + 0.045f * MathUtils.sin(stateTime * 4.2f)) : 0.92f;
            float orbSize = baseOrbSize * pulse;
            float orbX = 65f;
            float orbY = 145f;

            Color orbColor = (player.getEnergy() > 0) ? Color.WHITE : new Color(0.45f, 0.50f, 0.65f, 0.85f);
            game.batch.setColor(orbColor);
            game.batch.draw(new com.badlogic.gdx.graphics.g2d.TextureRegion(orbTex), orbX - (orbSize - baseOrbSize) / 2f, orbY - (orbSize - baseOrbSize) / 2f, 
                baseOrbSize / 2f, baseOrbSize / 2f, orbSize, orbSize, 1f, 1f, stateTime * 12f);
            game.batch.setColor(Color.WHITE);

            // Số Năng lượng hiển thị nổi bật trong tâm ngọc
            String energyStr = player.getEnergy() + "/" + player.getMaxEnergy();
            float fontScaleBefore = game.font.getData().scaleX;
            game.font.getData().setScale(1.22f);
            game.font.setColor(player.getEnergy() > 0 ? Color.GOLD : Color.LIGHT_GRAY);
            game.font.draw(game.batch, energyStr, orbX + 22, orbY + 54);
            game.font.getData().setScale(fontScaleBefore);
            game.font.setColor(Color.WHITE);
        }

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
    }
}
