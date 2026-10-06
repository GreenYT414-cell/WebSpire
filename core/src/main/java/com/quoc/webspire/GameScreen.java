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
    private final MapScreen mapScreen;
    private Stage stage;

    private Character player;
    private Enemy monster;
    private DeckManager deckManager;
    private List<Card> masterDeck;

    private Label playerLabel, monsterLabel, intentLabel, relicListLabel;
    private ProgressBar playerHpBar, monsterHpBar;
    private Label drawPileLabel, discardPileLabel;

    private Table handTable;
    private TextButton endTurnButton;

    private Table gameOverOverlay;
    private Label gameOverTitleLabel;

    private Table rewardOverlay, rewardCardsTable;
    private Label rewardGoldLabel, rewardRelicLabel;

    private Random random = new Random();

    public GameScreen(Main game, MapScreen mapScreen) {
        this.game = game;
        this.mapScreen = mapScreen;
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
        Label.LabelStyle relicStyle = new Label.LabelStyle(game.font, Color.ORANGE);

        relicListLabel = new Label("", relicStyle);
        playerLabel = new Label("", labelStyle);
        monsterLabel = new Label("", labelStyle);
        intentLabel = new Label("", intentStyle);

        drawPileLabel = new Label("", pileStyle);
        discardPileLabel = new Label("", pileStyle);

        playerHpBar = new ProgressBar(0, player.getMaxHp(), 1, false, createHpBarStyle(Color.GREEN));
        monsterHpBar = new ProgressBar(0, monster.getMaxHp(), 1, false, createHpBarStyle(Color.RED));

        Table topBar = new Table();
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

        startNewCombat();
    }

    private void initGameData() {
        player = new Character("Hiệp Sĩ", 50, 3);
        player.addRelic(new BurningBloodRelic());

        masterDeck = new ArrayList<>();
        for (int i = 0; i < 4; i++) masterDeck.add(new StrikeCard());
        for (int i = 0; i < 4; i++) masterDeck.add(new DefendCard());
        masterDeck.add(new BashCard());

        int monsterHp = 35 + random.nextInt(15);
        monster = new Enemy("Goblin Quái Vật", monsterHp, 0);

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
        int rewardGold = 20 + random.nextInt(15);
        player.addGold(rewardGold);
        rewardGoldLabel.setText("Nhận được: " + rewardGold + " Vàng!");
        rewardRelicLabel.setVisible(false);

        rewardCardsTable.clear();
        List<Card> rewardOptions = generateRewardCards(3);

        for (Card card : rewardOptions) {
            CardActor cardActor = new CardActor(card, game.font, () -> {
                masterDeck.add(card);
                finishCombatAndReturnToMap();
            });
            rewardCardsTable.add(cardActor).size(140, 190).pad(10);
        }

        rewardOverlay.setVisible(true);
        endTurnButton.setVisible(false);
    }

    private void finishCombatAndReturnToMap() {
        rewardOverlay.setVisible(false);
        endTurnButton.setVisible(true);

        // Quay về Màn hình Bản đồ để người chơi chọn bước đi kế tiếp
        game.setScreen(mapScreen);
    }

    private List<Card> generateRewardCards(int count) {
        List<Card> options = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int roll = random.nextInt(5);
            switch (roll) {
                case 0: options.add(new StrikeCard()); break;
                case 1: options.add(new DefendCard()); break;
                case 2: options.add(new BashCard()); break;
                case 3: options.add(new IronWaveCard()); break;
                default: options.add(new DefendCard()); break;
            }
        }
        return options;
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
        StringBuilder relicsText = new StringBuilder("Cổ vật: ");
        for (Relic relic : player.getRelics()) {
            relicsText.append("[").append(relic.getName()).append("] ");
        }
        relicListLabel.setText(relicsText.toString());

        String playerStatus = player.getName() + "\nHP: " + player.getHp() + "/" + player.getMaxHp()
            + "\nGiáp: " + player.getBlock() + " | NL: " + player.getEnergy()
            + "\nVàng: " + player.getGold();
        if (player.getStrength() > 0) playerStatus += "\n[Sức mạnh: +" + player.getStrength() + "]";
        playerLabel.setText(playerStatus);

        String monsterStatus = monster.getName() + "\nHP: " + monster.getHp() + "/" + monster.getMaxHp()
            + "\nGiáp: " + monster.getBlock();
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
                finishCombatAndReturnToMap();
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
