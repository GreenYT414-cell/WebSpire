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

public class Main extends ApplicationAdapter {
    private Stage stage;
    private SpriteBatch batch;
    private BitmapFont font;

    private Character player;
    private Enemy monster;
    private DeckManager deckManager;

    private Label playerLabel;
    private Label monsterLabel;
    private Label intentLabel;
    private ProgressBar playerHpBar;
    private ProgressBar monsterHpBar;

    private Label drawPileLabel;
    private Label discardPileLabel;

    private Table handTable;
    private TextButton endTurnButton;

    private Table gameOverOverlay;
    private Label gameOverTitleLabel;

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

        playerLabel = new Label("", labelStyle);
        monsterLabel = new Label("", labelStyle);
        intentLabel = new Label("", intentStyle);

        drawPileLabel = new Label("", pileStyle);
        discardPileLabel = new Label("", pileStyle);

        playerHpBar = new ProgressBar(0, player.getMaxHp(), 1, false, createHpBarStyle(Color.GREEN));
        monsterHpBar = new ProgressBar(0, monster.getMaxHp(), 1, false, createHpBarStyle(Color.RED));

        // Bố cục phần trên: Thông số nhân vật
        Table playerBox = new Table();
        playerBox.add(playerLabel).row();
        playerBox.add(playerHpBar).width(200).height(20).padTop(8);

        Table monsterBox = new Table();
        monsterBox.add(intentLabel).row();
        monsterBox.add(monsterLabel).row();
        monsterBox.add(monsterHpBar).width(200).height(20).padTop(8);

        rootTable.add(playerBox).expandX().padTop(20);
        rootTable.add(monsterBox).expandX().padTop(20);
        rootTable.row();

        // Nút Kết thúc lượt
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
        rootTable.add(endTurnButton).colspan(2).padTop(20).row();

        // Bố cục phần dưới: Chồng bài rút - Bài trên tay - Chồng bài bỏ
        Table bottomTable = new Table();
        handTable = new Table();

        bottomTable.add(drawPileLabel).width(120).padLeft(20);
        bottomTable.add(handTable).expandX();
        bottomTable.add(discardPileLabel).width(120).padRight(20);

        rootTable.add(bottomTable).colspan(2).expandY().bottom().padBottom(20).fillX();

        stage.addActor(rootTable);

        initGameOverOverlay();
        startPlayerTurn();
    }

    private void initGameData() {
        player = new Character("Hiệp Sĩ", 50, 3);
        monster = new Enemy("Goblin", 40, 0);

        List<Card> masterDeck = new ArrayList<>();
        for (int i = 0; i < 5; i++) masterDeck.add(new StrikeCard());
        for (int i = 0; i < 4; i++) masterDeck.add(new DefendCard());

        deckManager = new DeckManager();
        deckManager.initCombat(masterDeck);
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
            showGameOverScreen("VICTORY! CẬU ĐÃ THẮNG!", Color.GOLD);
            return true;
        } else if (player.getHp() <= 0) {
            showGameOverScreen("GAME OVER! CẬU ĐÃ THẤT BẠI!", Color.RED);
            return true;
        }
        return false;
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

        startPlayerTurn();
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
        playerLabel.setText(player.getName() + "\nHP: " + player.getHp() + "/" + player.getMaxHp() + "\nGiáp: " + player.getBlock() + "\nNăng lượng: " + player.getEnergy());
        monsterLabel.setText(monster.getName() + "\nHP: " + monster.getHp() + "/" + monster.getMaxHp() + "\nGiáp: " + monster.getBlock());

        playerHpBar.setValue(player.getHp());
        monsterHpBar.setValue(monster.getHp());

        drawPileLabel.setText("Bài rút:\n" + deckManager.getDrawPile().size() + " lá");
        discardPileLabel.setText("Bài bỏ:\n" + deckManager.getDiscardPile().size() + " lá");

        if (monster.getCurrentIntent() != null) {
            intentLabel.setText("Ý định: " + monster.getCurrentIntent() + " (" + monster.getIntentValue() + ")");
        }
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

        TextButton restartBtn = new TextButton("Chơi lại", btnStyle);
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
