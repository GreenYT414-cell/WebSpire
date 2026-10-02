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
    private Table handTable;
    private TextButton endTurnButton;

    @Override
    public void create() {
        batch = new SpriteBatch();
        stage = new Stage(new FitViewport(1280, 720), batch);
        Gdx.input.setInputProcessor(stage);

        initFont();

        // 1. Khởi tạo dữ liệu
        player = new Character("Hiệp Sĩ", 50, 3);
        monster = new Enemy("Goblin", 40, 0);

        List<Card> masterDeck = new ArrayList<>();
        for (int i = 0; i < 5; i++) masterDeck.add(new StrikeCard());
        for (int i = 0; i < 4; i++) masterDeck.add(new DefendCard());

        deckManager = new DeckManager();
        deckManager.initCombat(masterDeck);

        // 2. Bố cục UI
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.top();

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label.LabelStyle intentStyle = new Label.LabelStyle(font, Color.RED);

        playerLabel = new Label("", labelStyle);
        monsterLabel = new Label("", labelStyle);
        intentLabel = new Label("", intentStyle);

        // Cột thông số
        rootTable.add(playerLabel).expandX().padTop(20);
        Table monsterContainer = new Table();
        monsterContainer.add(intentLabel).row();
        monsterContainer.add(monsterLabel);
        rootTable.add(monsterContainer).expandX().padTop(20);
        rootTable.row();

        // Nút Kết thúc lượt
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = font;
        btnStyle.fontColor = Color.YELLOW;
        btnStyle.up = createDrawable(0.3f, 0.3f, 0.3f);

        endTurnButton = new TextButton("Kết thúc lượt", btnStyle);
        endTurnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                endPlayerTurn();
            }
        });
        rootTable.add(endTurnButton).colspan(2).padTop(30).row();

        // Hàng chứa các lá bài trên tay
        handTable = new Table();
        rootTable.add(handTable).colspan(2).expandY().bottom().padBottom(30);

        stage.addActor(rootTable);

        // Bắt đầu Lượt 1
        startPlayerTurn();
    }

    private void startPlayerTurn() {
        if (player.getHp() <= 0 || monster.getHp() <= 0) return;

        monster.rollIntent();
        player.startTurn();
        deckManager.draw(5);

        updateIntentLabel();
        refreshHandUI();
    }

    private void endPlayerTurn() {
        deckManager.discardHand();
        refreshHandUI();

        // Lượt kẻ thù
        monster.startTurn();
        monster.executeIntent(player);

        if (player.getHp() > 0 && monster.getHp() > 0) {
            startPlayerTurn();
        }
    }

    private void refreshHandUI() {
        handTable.clear();
        for (Card card : deckManager.getHand()) {
            CardActor cardActor = new CardActor(card, font, () -> {
                // Tương tác khi click lá bài
                if (deckManager.playCard(card, player, monster)) {
                    refreshHandUI();
                }
            });
            handTable.add(cardActor).size(140, 190).pad(5);
        }
    }

    private void updateIntentLabel() {
        if (monster.getCurrentIntent() != null) {
            intentLabel.setText("Ý định: " + monster.getCurrentIntent() + " (" + monster.getIntentValue() + ")");
        }
    }

    private TextureRegionDrawable createDrawable(float r, float g, float b) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(r, g, b, 1f);
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
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        playerLabel.setText(player.getName() + "\nHP: " + player.getHp() + "/" + player.getMaxHp() + "\nGiáp: " + player.getBlock() + "\nNăng lượng: " + player.getEnergy());
        monsterLabel.setText(monster.getName() + "\nHP: " + monster.getHp() + "/" + monster.getMaxHp() + "\nGiáp: " + monster.getBlock());

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
