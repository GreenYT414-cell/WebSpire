package com.quoc.webspire;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class CardActor extends Table {
    private Card card;

    public CardActor(Card card, BitmapFont font, Runnable onClick) {
        this.card = card;

        // Tạo khung nền màu cho lá bài (không cần dùng file ảnh bên ngoài)
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0.25f, 0.25f, 0.4f, 0.95f); // Màu xanh xám đậm
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        this.setBackground(new TextureRegionDrawable(new TextureRegion(texture)));
        this.setSize(140, 190);

        // Kiểu chữ cho từng thông tin
        Label.LabelStyle titleStyle = new Label.LabelStyle(font, Color.GOLD);
        Label.LabelStyle costStyle = new Label.LabelStyle(font, Color.CYAN);
        Label.LabelStyle descStyle = new Label.LabelStyle(font, Color.WHITE);

        // Xếp bố cục bên trong lá bài
        this.top().pad(10);
        this.add(new Label("[" + card.getCost() + " Năng Lượng]", costStyle)).right().row();
        this.add(new Label(card.getName(), titleStyle)).padTop(10).row();

        Label descLabel = new Label(card.getDescription(), descStyle);
        descLabel.setWrap(true);
        this.add(descLabel).width(120).padTop(15).row();

        // Bắt sự kiện click chuột vào lá bài
        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onClick.run();
            }
        });
    }

    public Card getCard() {
        return card;
    }
}
