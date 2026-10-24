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
import com.quoc.webspire.managers.Assets;
import com.quoc.webspire.cards.Card;
import com.quoc.webspire.cards.CardActor;

import java.util.List;

public class DeckInspectorDialog {

    public static void show(Stage stage, BitmapFont font, String title, List<Card> cards) {
        Table overlay = new Table();
        overlay.setFillParent(true);
        overlay.setBackground(Assets.createColorDrawable(0f, 0f, 0f, 0.88f));

        Label.LabelStyle titleStyle = new Label.LabelStyle(font, Color.GOLD);
        Label titleLabel = new Label(title + " (" + cards.size() + " lá)", titleStyle);

        Table cardsGrid = new Table();
        int count = 0;
        for (Card card : cards) {
            CardActor cardActor = new CardActor(card, font, null);
            cardsGrid.add(cardActor).size(135, 185).pad(8);
            count++;
            if (count % 5 == 0) cardsGrid.row();
        }

        ScrollPane scrollPane = new ScrollPane(cardsGrid);
        scrollPane.setScrollingDisabled(true, false);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = font;
        btnStyle.fontColor = Color.WHITE;
        btnStyle.up = Assets.createColorDrawable(0.4f, 0.2f, 0.2f, 1f);

        TextButton closeBtn = new TextButton("Đóng", btnStyle);
        closeBtn.pad(10, 30, 10, 30);
        closeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                overlay.remove();
            }
        });

        overlay.add(titleLabel).padTop(20).padBottom(15).row();
        overlay.add(scrollPane).expand().fill().padBottom(15).row();
        overlay.add(closeBtn).padBottom(20);

        stage.addActor(overlay);
    }
}
