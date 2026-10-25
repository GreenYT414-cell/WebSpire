package com.quoc.webspire.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.quoc.webspire.Main;
import com.quoc.webspire.entities.Character;
import com.quoc.webspire.managers.Assets;
import com.quoc.webspire.managers.RunState;
import com.quoc.webspire.managers.SoundManager;
import com.quoc.webspire.map.MapGenerator;
import com.quoc.webspire.map.MapNode;
import com.quoc.webspire.network.GameSyncService;
import com.quoc.webspire.ui.DeckInspectorDialog;
import com.quoc.webspire.ui.ParticleManager;

import java.util.List;

public class MapScreen implements Screen {
    private final Main game;
    private Stage stage;
    private List<MapNode> mapNodes;
    private ParticleManager particleManager;
    private float stateTime = 0f;
    private Label syncStatusLabel;

    public MapScreen(Main game) {
        this.game = game;
        this.stage = new Stage(new FitViewport(1280, 720), game.batch);
        this.mapNodes = RunState.getInstance().getMapNodes();
        this.particleManager = new ParticleManager();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        SoundManager.getInstance().playBgm("bgm_menu.mp3");

        this.mapNodes = RunState.getInstance().getMapNodes();
        RunState.getInstance().updateNodeAccessibility();

        stage.clear();

        Table root = new Table();
        root.setFillParent(true);
        root.top();

        // 1. THANH CÔNG CỤ PHÍA TRÊN (TOP BAR GLASSMORPHISM)
        Table topBar = new Table();
        topBar.setBackground(Assets.createBorderedDrawable(1280, 74, 
            new Color(0.04f, 0.04f, 0.08f, 0.95f), 
            new Color(0.25f, 0.38f, 0.60f, 0.85f), 1));

        Character player = RunState.getInstance().getPlayer();
        int floor = RunState.getInstance().getCurrentFloor();

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.font, Color.GOLD);
        Label titleLabel = new Label("✦  THÁP WEBSPIRE — TẦNG " + floor + " / 15  ✦", titleStyle);

        // Nút Lưu Cloud Sync
        TextButton.TextButtonStyle syncBtnStyle = new TextButton.TextButtonStyle();
        syncBtnStyle.font = game.font;
        syncBtnStyle.fontColor = new Color(0.40f, 0.85f, 1f, 1f);
        syncBtnStyle.overFontColor = Color.WHITE;
        syncBtnStyle.up = Assets.createBorderedDrawable(135, 34, new Color(0.08f, 0.22f, 0.35f, 0.9f), new Color(0.25f, 0.70f, 0.95f, 0.85f), 1);
        syncBtnStyle.over = Assets.createBorderedDrawable(135, 34, new Color(0.12f, 0.32f, 0.50f, 0.95f), new Color(0.50f, 0.88f, 1f, 1f), 1);

        TextButton syncCloudBtn = new TextButton("☁ Lưu Cloud", syncBtnStyle);
        syncCloudBtn.pad(4, 10, 4, 10);
        syncCloudBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                syncStatusLabel.setText("Đang đồng bộ...");
                GameSyncService.saveProfile(RunState.getInstance(), new GameSyncService.SyncCallback() {
                    @Override
                    public void onSuccess(String message) {
                        syncStatusLabel.setText("✔ Đã lưu Cloud!");
                    }

                    @Override
                    public void onError(String errorMessage) {
                        syncStatusLabel.setText("Lỗi: " + errorMessage);
                    }
                });
            }
        });

        // Nút xem bộ bài ngay từ bản đồ
        TextButton.TextButtonStyle deckBtnStyle = new TextButton.TextButtonStyle();
        deckBtnStyle.font = game.font;
        deckBtnStyle.fontColor = Color.CYAN;
        deckBtnStyle.overFontColor = Color.WHITE;
        deckBtnStyle.up = Assets.createBorderedDrawable(135, 34, new Color(0.12f, 0.18f, 0.32f, 0.9f), new Color(0.35f, 0.65f, 0.95f, 0.9f), 1);
        deckBtnStyle.over = Assets.createBorderedDrawable(135, 34, new Color(0.18f, 0.28f, 0.48f, 0.95f), new Color(0.65f, 0.85f, 1f, 1f), 1);

        TextButton viewDeckBtn = new TextButton("📖 Xem Bộ Bài", deckBtnStyle);
        viewDeckBtn.pad(4, 10, 4, 10);
        viewDeckBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                DeckInspectorDialog.show(stage, game.font, "BỘ BÀI HIỆN TẠI", RunState.getInstance().getMasterDeck());
            }
        });

        // Nút Về Menu
        TextButton.TextButtonStyle menuBtnStyle = new TextButton.TextButtonStyle();
        menuBtnStyle.font = game.font;
        menuBtnStyle.fontColor = Color.LIGHT_GRAY;
        menuBtnStyle.overFontColor = Color.WHITE;
        menuBtnStyle.up = Assets.createBorderedDrawable(100, 34, new Color(0.22f, 0.12f, 0.12f, 0.9f), new Color(0.55f, 0.25f, 0.25f, 0.9f), 1);
        menuBtnStyle.over = Assets.createBorderedDrawable(100, 34, new Color(0.35f, 0.16f, 0.16f, 0.95f), new Color(0.85f, 0.35f, 0.35f, 1f), 1);

        TextButton mainMenuBtn = new TextButton("🚪 Menu", menuBtnStyle);
        mainMenuBtn.pad(4, 10, 4, 10);
        mainMenuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        // Hàng 1: Tiêu đề bản đồ & Nút thao tác
        Table row1 = new Table();
        row1.add(titleLabel).left();

        Label.LabelStyle syncStyle = new Label.LabelStyle(game.font, Color.LIME);
        syncStatusLabel = new Label("", syncStyle);
        row1.add(syncStatusLabel).padLeft(15);

        row1.add(syncCloudBtn).expandX().right().padRight(8);
        row1.add(viewDeckBtn).right().padRight(8);
        row1.add(mainMenuBtn).right();
        topBar.add(row1).fillX().pad(6, 16, 4, 16).row();

        // Hàng 2: Huy hiệu Thông số nhân vật dạng Pill Box
        Table row2 = new Table();
        float hpRatio = (float) player.getHp() / player.getMaxHp();
        Color hpColor = hpRatio > 0.5f ? Color.LIME : (hpRatio > 0.25f ? Color.YELLOW : Color.RED);

        row2.add(createPill("❤ " + player.getHp() + "/" + player.getMaxHp() + " HP", hpColor)).padRight(10);
        row2.add(createPill("💰 " + player.getGold() + " Vàng", Color.YELLOW)).padRight(10);
        row2.add(createPill("📖 " + RunState.getInstance().getMasterDeck().size() + " Lá Bài", Color.CYAN)).padRight(10);
        row2.add(createPill("🔮 " + player.getRelics().size() + " Cổ Vật", Color.ORANGE)).padRight(10);
        row2.add(createPill("🧪 " + player.getPotions().size() + "/3 Thuốc", Color.GREEN)).expandX().left();
        topBar.add(row2).fillX().pad(0, 16, 6, 16).row();

        root.add(topBar).fillX().row();

        // 2. HIỂN THỊ SƠ ĐỒ BẢN ĐỒ DẠNG THÁP (TẦNG 15 -> TẦNG 1)
        Table mapGrid = new Table();
        mapGrid.pad(25);

        int currentFloor = RunState.getInstance().getCurrentFloor();

        for (int f = 15; f >= 1; f--) {
            Table floorRow = new Table();

            boolean isCurrent = (f == currentFloor);
            Label.LabelStyle floorNumStyle = new Label.LabelStyle(game.font, isCurrent ? Color.GOLD : Color.GRAY);
            Label floorNumLabel = new Label((isCurrent ? "▶ TẦNG " : "  Tầng ") + f + ": ", floorNumStyle);
            floorRow.add(floorNumLabel).padRight(20);

            for (MapNode node : mapNodes) {
                if (node.getFloor() == f) {
                    TextButton.TextButtonStyle nodeBtnStyle = new TextButton.TextButtonStyle();
                    nodeBtnStyle.font = game.font;

                    String nodeName = getNodeTypeName(node.getType(), f);
                    String btnText;

                    boolean isAccessible = node.isAccessible();
                    if (f == currentFloor) {
                        if (isAccessible) {
                            btnText = "★ " + nodeName + " ★";
                            Color upInner, upBorder;
                            if (node.getType() == MapNode.Type.BOSS) {
                                nodeBtnStyle.fontColor = Color.GOLD;
                                upInner = new Color(0.45f, 0.12f, 0.40f, 0.98f);
                                upBorder = new Color(1.0f, 0.85f, 0.25f, 1f);
                            } else if (node.getType() == MapNode.Type.ELITE) {
                                nodeBtnStyle.fontColor = Color.YELLOW;
                                upInner = new Color(0.55f, 0.12f, 0.15f, 0.98f);
                                upBorder = new Color(1.0f, 0.45f, 0.35f, 1f);
                            } else if (node.getType() == MapNode.Type.REST) {
                                nodeBtnStyle.fontColor = Color.LIME;
                                upInner = new Color(0.12f, 0.38f, 0.18f, 0.98f);
                                upBorder = new Color(0.45f, 0.95f, 0.55f, 1f);
                            } else if (node.getType() == MapNode.Type.SHOP) {
                                nodeBtnStyle.fontColor = Color.GOLD;
                                upInner = new Color(0.42f, 0.32f, 0.10f, 0.98f);
                                upBorder = new Color(0.95f, 0.82f, 0.25f, 1f);
                            } else if (node.getType() == MapNode.Type.EVENT) {
                                nodeBtnStyle.fontColor = Color.CYAN;
                                upInner = new Color(0.22f, 0.15f, 0.42f, 0.98f);
                                upBorder = new Color(0.65f, 0.45f, 0.95f, 1f);
                            } else {
                                nodeBtnStyle.fontColor = Color.WHITE;
                                upInner = new Color(0.12f, 0.28f, 0.42f, 0.98f);
                                upBorder = new Color(0.45f, 0.75f, 1.0f, 1f);
                            }
                            nodeBtnStyle.up = Assets.createBorderedDrawable(220, 48, upInner, upBorder, 2);
                        } else {
                            btnText = "🔒 " + nodeName;
                            nodeBtnStyle.fontColor = Color.GRAY;
                            Color upInner = new Color(0.08f, 0.08f, 0.12f, 0.85f);
                            Color upBorder = new Color(0.25f, 0.25f, 0.32f, 0.7f);
                            nodeBtnStyle.up = Assets.createBorderedDrawable(220, 48, upInner, upBorder, 1);
                        }
                    } else if (f < currentFloor) {
                        btnText = (node.isVisited() ? "✔ " : "─ ") + nodeName;
                        nodeBtnStyle.fontColor = Color.GRAY;
                        nodeBtnStyle.up = Assets.createBorderedDrawable(220, 48, new Color(0.08f, 0.08f, 0.10f, 0.6f), new Color(0.22f, 0.22f, 0.25f, 0.7f), 1);
                    } else {
                        btnText = nodeName;
                        Color futureColor = new Color(0.12f, 0.14f, 0.20f, 0.85f);
                        Color futureBorder = new Color(0.25f, 0.30f, 0.42f, 0.75f);
                        nodeBtnStyle.fontColor = Color.LIGHT_GRAY;
                        nodeBtnStyle.up = Assets.createBorderedDrawable(220, 48, futureColor, futureBorder, 1);
                    }

                    TextButton nodeBtn = new TextButton(btnText, nodeBtnStyle);
                    nodeBtn.pad(8, 16, 8, 16);

                    // Hiệu ứng phát sáng nhấp nháy cho các node tầng hiện tại nếu mở khóa
                    if (f == currentFloor && isAccessible) {
                        nodeBtn.addAction(Actions.forever(Actions.sequence(
                            Actions.alpha(0.82f, 0.6f, Interpolation.sine),
                            Actions.alpha(1.0f, 0.6f, Interpolation.sine)
                        )));

                        final MapScreen self = this;
                        nodeBtn.addListener(new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                RunState.getInstance().setCurrentNode(node);
                                switch (node.getType()) {
                                    case COMBAT:
                                    case ELITE:
                                    case BOSS:
                                        game.setScreen(new GameScreen(game, self, node.getType()));
                                        break;
                                    case SHOP:
                                        game.setScreen(new ShopScreen(game, self));
                                        break;
                                    case REST:
                                        game.setScreen(new RestScreen(game, self));
                                        break;
                                    case EVENT:
                                        game.setScreen(new EventScreen(game, self));
                                        break;
                                }
                            }
                        });
                    }

                    floorRow.add(nodeBtn).pad(6);
                }
            }

            mapGrid.add(floorRow).padBottom(6).row();

            // Hiển thị đường mũi tên phong cách fantasy dẫn lối leo tháp giữa các tầng
            if (f > 1) {
                Table connectorRow = new Table();
                Label.LabelStyle connStyle = new Label.LabelStyle(game.font, new Color(0.35f, 0.55f, 0.90f, 0.5f));
                Label connLabel = new Label("▲ ────── ▲ ────── ▲", connStyle);
                connectorRow.add(connLabel).padLeft(60);
                mapGrid.add(connectorRow).padBottom(6).row();
            }
        }

        ScrollPane scrollPane = new ScrollPane(mapGrid);
        scrollPane.setFadeScrollBars(false);

        root.add(scrollPane).expand().fill().pad(10);
        stage.addActor(root);

        stage.act();
        scrollPane.setScrollY(scrollPane.getMaxY());
    }

    private Table createPill(String text, Color textColor) {
        Table pill = new Table();
        pill.setBackground(Assets.createBorderedDrawable(140, 26, new Color(0.08f, 0.08f, 0.14f, 0.85f), new Color(0.25f, 0.35f, 0.55f, 0.75f), 1));
        pill.pad(3, 10, 3, 10);
        Label.LabelStyle style = new Label.LabelStyle(game.font, textColor);
        pill.add(new Label(text, style));
        return pill;
    }

    private String getNodeTypeName(MapNode.Type type, int floor) {
        if (type == null) return "Địa Điểm";
        switch (type) {
            case COMBAT: return "⚔  Quái Vật";
            case ELITE: return "💀  Tinh Anh";
            case BOSS: return floor == 8 ? "👑  Hộ Vệ [Tầng 8]" : "👹  Chúa Tể Slime [Tầng 15]";
            case SHOP: return "🛒  Thương Nhân";
            case REST: return "⛺  Lửa Trại";
            case EVENT: return "❓  Kỳ Ngộ";
            default: return type.name();
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.04f, 0.03f, 0.07f, 1f);

        stateTime += delta;
        particleManager.update(delta);
        particleManager.updateAmbient(delta, 1280, 720, new Color(0.40f, 0.55f, 0.95f, 0.35f), 6);

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
