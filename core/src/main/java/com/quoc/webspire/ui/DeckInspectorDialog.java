package com.quoc.webspire.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.quoc.webspire.cards.Card;
import com.quoc.webspire.cards.CardActor;
import com.quoc.webspire.managers.Assets;

import java.util.ArrayList;
import java.util.List;

public class DeckInspectorDialog {

    public static void show(Stage stage, BitmapFont font, String title, List<Card> cards) {
        Table overlay = new Table();
        overlay.setFillParent(true);
        overlay.setBackground(Assets.createColorDrawable(0.02f, 0.02f, 0.05f, 0.92f));

        Table window = new Table();
        window.setBackground(Assets.createBorderedDrawable(1100, 620, 
            new Color(0.06f, 0.06f, 0.12f, 0.98f), 
            new Color(0.75f, 0.60f, 0.25f, 0.95f), 2));
        window.pad(15);

        // Header Tiêu đề
        Label.LabelStyle titleStyle = new Label.LabelStyle(font, Color.GOLD);
        Label titleLabel = new Label("✦  " + title.toUpperCase() + "  ✦", titleStyle);
        titleLabel.setFontScale(1.2f);

        // Đếm số lượng từng loại bài
        int attackCount = 0;
        int defenseCount = 0;
        int skillCount = 0;
        int powerCount = 0;
        int totalCost = 0;

        for (Card c : cards) {
            totalCost += c.getCost();
            if (c.getType() == Card.Type.ATTACK) attackCount++;
            else if (c.getType() == Card.Type.DEFENSE) defenseCount++;
            else if (c.getType() == Card.Type.SKILL) skillCount++;
            else if (c.getType() == Card.Type.POWER) powerCount++;
        }
        float avgCost = cards.isEmpty() ? 0 : (float) totalCost / cards.size();

        // Thanh Thống kê Nhanh
        Table statsTable = new Table();
        statsTable.add(createBadge(font, "Tổng số: " + cards.size() + " lá", Color.WHITE)).padRight(10);
        statsTable.add(createBadge(font, "⚔ Tấn Công: " + attackCount, new Color(1.0f, 0.45f, 0.35f, 1f))).padRight(10);
        statsTable.add(createBadge(font, "🛡 Phòng Thủ: " + defenseCount, new Color(0.45f, 0.75f, 1.0f, 1f))).padRight(10);
        statsTable.add(createBadge(font, "🍃 Kỹ Năng: " + skillCount, new Color(0.50f, 1.0f, 0.70f, 1f))).padRight(10);
        statsTable.add(createBadge(font, "🔮 Năng Lực: " + powerCount, new Color(0.95f, 0.65f, 1.0f, 1f))).padRight(10);
        statsTable.add(createBadge(font, String.format("⚡ Chi phí TB: %.1f", avgCost), Color.YELLOW));

        // Lưới hiển thị bài
        Table cardsGrid = new Table();
        renderCards(cardsGrid, cards, null, font);

        ScrollPane scrollPane = new ScrollPane(cardsGrid);
        scrollPane.setFadeScrollBars(false);

        // Thanh Bộ Lọc (Tabs)
        Table filterBar = new Table();
        filterBar.add(createFilterBtn(font, "Tất Cả (" + cards.size() + ")", () -> renderCards(cardsGrid, cards, null, font))).padRight(8);
        filterBar.add(createFilterBtn(font, "⚔ Tấn Công (" + attackCount + ")", () -> renderCards(cardsGrid, cards, Card.Type.ATTACK, font))).padRight(8);
        filterBar.add(createFilterBtn(font, "🛡 Phòng Thủ (" + defenseCount + ")", () -> renderCards(cardsGrid, cards, Card.Type.DEFENSE, font))).padRight(8);
        filterBar.add(createFilterBtn(font, "🍃 Kỹ Năng (" + skillCount + ")", () -> renderCards(cardsGrid, cards, Card.Type.SKILL, font))).padRight(8);
        filterBar.add(createFilterBtn(font, "🔮 Năng Lực (" + powerCount + ")", () -> renderCards(cardsGrid, cards, Card.Type.POWER, font)));

        // Nút Đóng
        TextButton.TextButtonStyle closeStyle = new TextButton.TextButtonStyle();
        closeStyle.font = font;
        closeStyle.fontColor = Color.WHITE;
        closeStyle.overFontColor = Color.YELLOW;
        closeStyle.up = Assets.createBorderedDrawable(160, 42, new Color(0.35f, 0.12f, 0.12f, 0.95f), new Color(0.85f, 0.35f, 0.35f, 1f), 1);
        closeStyle.over = Assets.createBorderedDrawable(160, 42, new Color(0.55f, 0.18f, 0.18f, 1f), new Color(1.0f, 0.65f, 0.65f, 1f), 2);

        TextButton closeBtn = new TextButton("✖ Đóng", closeStyle);
        closeBtn.pad(8, 25, 8, 25);
        closeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                overlay.remove();
            }
        });

        window.add(titleLabel).padBottom(8).row();
        window.add(statsTable).padBottom(10).row();
        window.add(filterBar).padBottom(10).row();
        window.add(scrollPane).width(1050).height(410).padBottom(12).row();
        window.add(closeBtn).center();

        overlay.add(window).center();
        stage.addActor(overlay);
    }

    private static void renderCards(Table container, List<Card> allCards, Card.Type filterType, BitmapFont font) {
        container.clearChildren();
        int col = 0;
        for (Card card : allCards) {
            if (filterType == null || card.getType() == filterType) {
                CardActor cardActor = new CardActor(card, font, null);
                container.add(cardActor).size(145, 205).pad(8);
                col++;
                if (col % 6 == 0) container.row();
            }
        }
        if (col == 0) {
            Label.LabelStyle emptyStyle = new Label.LabelStyle(font, Color.GRAY);
            container.add(new Label("Không có lá bài nào trong danh mục này.", emptyStyle)).pad(40);
        }
    }

    private static Table createBadge(BitmapFont font, String text, Color textColor) {
        Table badge = new Table();
        badge.setBackground(Assets.createBorderedDrawable(140, 26, new Color(0.08f, 0.08f, 0.14f, 0.9f), new Color(0.25f, 0.35f, 0.55f, 0.7f), 1));
        badge.pad(2, 8, 2, 8);
        Label.LabelStyle style = new Label.LabelStyle(font, textColor);
        badge.add(new Label(text, style));
        return badge;
    }

    private static TextButton createFilterBtn(BitmapFont font, String text, Runnable onSelect) {
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = font;
        btnStyle.fontColor = new Color(0.85f, 0.85f, 0.90f, 1f);
        btnStyle.overFontColor = Color.GOLD;
        btnStyle.up = Assets.createBorderedDrawable(140, 32, new Color(0.12f, 0.15f, 0.24f, 0.9f), new Color(0.35f, 0.45f, 0.65f, 0.8f), 1);
        btnStyle.over = Assets.createBorderedDrawable(140, 32, new Color(0.18f, 0.24f, 0.38f, 0.95f), new Color(0.65f, 0.80f, 1f, 1f), 1);

        TextButton btn = new TextButton(text, btnStyle);
        btn.pad(3, 10, 3, 10);
        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (onSelect != null) onSelect.run();
            }
        });
        return btn;
    }
}
