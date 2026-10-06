package com.quoc.webspire;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapScreen implements Screen {
    private final Main game;
    private Stage stage;
    private ShapeRenderer shapeRenderer;

    private List<List<MapNode>> mapData;
    private final Map<MapNode, TextButton> nodeButtonMap = new HashMap<>();

    public MapScreen(Main game) {
        this.game = game;
        this.stage = new Stage(new FitViewport(1280, 720), game.batch);
        this.shapeRenderer = new ShapeRenderer();
        this.mapData = MapGenerator.generateMap();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        stage.clear();
        nodeButtonMap.clear();

        Table root = new Table();
        root.setFillParent(true);

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.font, Color.GOLD);
        Label titleLabel = new Label("BẢN ĐỒ THÁP WEBSPIRE", titleStyle);
        root.add(titleLabel).padTop(10).padBottom(20).row();

        Table mapTable = new Table();
        mapTable.bottom();

        // Vẽ từ tầng 1 đến tầng 15
        for (int floor = 0; floor < mapData.size(); floor++) {
            List<MapNode> floorNodes = mapData.get(floor);
            Table floorTable = new Table();

            for (MapNode node : floorNodes) {
                TextButton btn = createNodeButton(node);
                nodeButtonMap.put(node, btn);

                floorTable.add(btn).size(120, 45).pad(15);
            }
            mapTable.add(floorTable).row();
        }

        ScrollPane scrollPane = new ScrollPane(mapTable);
        scrollPane.setScrollingDisabled(true, false);
        root.add(scrollPane).expand().fill();

        stage.addActor(root);
    }

    private TextButton createNodeButton(MapNode node) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = game.font;

        updateButtonAppearance(style, node);

        TextButton button = new TextButton(node.getType().getLabel(), style);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (node.isAccessible() && !node.isVisited()) {
                    selectNode(node);
                }
            }
        });

        return button;
    }

    private void selectNode(MapNode selectedNode) {
        selectedNode.setVisited(true);
        selectedNode.setAccessible(false);

        // Khóa các nút khác ở cùng tầng
        for (MapNode node : mapData.get(selectedNode.getFloor())) {
            node.setAccessible(false);
        }

        // Mở khóa các nút tầng kế tiếp nối từ nút này
        for (MapNode next : selectedNode.getNextNodes()) {
            next.setAccessible(true);
        }

        // Cập nhật giao diện tất cả các nút
        for (Map.Entry<MapNode, TextButton> entry : nodeButtonMap.entrySet()) {
            updateButtonAppearance(entry.getValue().getStyle(), entry.getKey());
        }

        // Chuyển sang trận đấu
        enterNodeScreen(selectedNode);
    }

    private void enterNodeScreen(MapNode node) {
        // Truyền 'this' (MapScreen hiện tại) sang GameScreen để sau khi đánh xong có thể quay về
        game.setScreen(new GameScreen(game, this));
    }

    private void updateButtonAppearance(TextButton.TextButtonStyle style, MapNode node) {
        if (node.isVisited()) {
            style.fontColor = Color.DARK_GRAY;
            style.up = createDrawable(0.2f, 0.2f, 0.2f, 0.5f);
        } else if (node.isAccessible()) {
            style.fontColor = Color.GREEN;
            style.up = createDrawable(0.1f, 0.5f, 0.1f, 0.9f);
        } else {
            style.fontColor = Color.LIGHT_GRAY;
            style.up = createDrawable(0.3f, 0.3f, 0.3f, 0.7f);
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.08f, 0.08f, 0.12f, 1f);

        // Vẽ các đường nối giữa các Nút
        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.GRAY);

        for (List<MapNode> floorNodes : mapData) {
            for (MapNode node : floorNodes) {
                TextButton currentBtn = nodeButtonMap.get(node);
                if (currentBtn == null) continue;

                for (MapNode nextNode : node.getNextNodes()) {
                    TextButton nextBtn = nodeButtonMap.get(nextNode);
                    if (nextBtn == null) continue;

                    float startX = currentBtn.getX() + currentBtn.getWidth() / 2f + currentBtn.getParent().getX();
                    float startY = currentBtn.getY() + currentBtn.getHeight() / 2f + currentBtn.getParent().getParent().getY();
                    float endX = nextBtn.getX() + nextBtn.getWidth() / 2f + nextBtn.getParent().getX();
                    float endY = nextBtn.getY() + nextBtn.getHeight() / 2f + nextBtn.getParent().getParent().getY();

                    shapeRenderer.rectLine(startX, startY, endX, endY, 3);
                }
            }
        }
        shapeRenderer.end();

        stage.act(delta);
        stage.draw();
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
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        shapeRenderer.dispose();
    }
}
