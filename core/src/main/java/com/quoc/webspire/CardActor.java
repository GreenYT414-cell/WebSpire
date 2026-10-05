package com.quoc.webspire;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class CardActor extends Table {
    private Card card;
    private TextureRegionDrawable normalBg;
    private TextureRegionDrawable hoverBg;

    public CardActor(Card card, BitmapFont font, Runnable onClick) {
        this.card = card;

        normalBg = createDrawable(0.25f, 0.25f, 0.4f, 0.95f);
        hoverBg = createDrawable(0.35f, 0.35f, 0.6f, 1f);

        this.setBackground(normalBg);
        this.setSize(140, 190);

        Label.LabelStyle titleStyle = new Label.LabelStyle(font, Color.GOLD);
        Label.LabelStyle costStyle = new Label.LabelStyle(font, Color.CYAN);
        Label.LabelStyle descStyle = new Label.LabelStyle(font, Color.WHITE);

        this.top().pad(10);
        this.add(new Label("[" + card.getCost() + " NL]", costStyle)).right().row();
        this.add(new Label(card.getName(), titleStyle)).padTop(8).row();

        Label descLabel = new Label(card.getDescription(), descStyle);
        descLabel.setWrap(true);
        this.add(descLabel).width(120).padTop(12).row();

        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onClick.run();
            }
        });

        this.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    setBackground(hoverBg);
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (pointer == -1) {
                    setBackground(normalBg);
                }
            }
        });
    }

    private TextureRegionDrawable createDrawable(float r, float g, float b, float a) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(r, g, b, a);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    public Card getCard() {
        return card;
    }
}
