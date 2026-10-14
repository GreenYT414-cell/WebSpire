package com.quoc.webspire;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class CardActor extends Table {
    private Card card;

    public CardActor(Card card, BitmapFont font, Runnable onClick) {
        this.card = card;

        // Định màu sắc riêng cho từng loại thẻ bài (Đỏ: Tấn công, Xanh dương: Phòng thủ, Xanh lá: Kỹ năng, Vàng: Sức mạnh)
        Color bgColor;

        switch (card.getType()) {
            case ATTACK:
                bgColor = new Color(0.35f, 0.12f, 0.12f, 0.95f);
                break;
            case DEFENSE:
                bgColor = new Color(0.12f, 0.22f, 0.38f, 0.95f);
                break;
            case SKILL:
                bgColor = new Color(0.12f, 0.30f, 0.20f, 0.95f);
                break;
            case POWER:
                bgColor = new Color(0.35f, 0.28f, 0.10f, 0.95f);
                break;
            default:
                bgColor = new Color(0.2f, 0.2f, 0.2f, 0.95f);
                break;
        }

        setBackground(Assets.createColorDrawable(bgColor.r, bgColor.g, bgColor.b, bgColor.a));
        pad(8);

        Label.LabelStyle costStyle = new Label.LabelStyle(font, Color.CYAN);
        Label.LabelStyle nameStyle = new Label.LabelStyle(font, card.isUpgraded() ? Color.GREEN : Color.GOLD);
        Label.LabelStyle descStyle = new Label.LabelStyle(font, Color.WHITE);

        Label costLabel = new Label("[" + card.getCost() + " NL]", costStyle);
        Label nameLabel = new Label(card.getName(), nameStyle);

        Label descLabel = new Label(card.getDescription(), descStyle);
        descLabel.setWrap(true);

        add(costLabel).right().padBottom(5).row();
        add(nameLabel).center().padBottom(8).row();
        add(descLabel).width(120).expandY().top().padBottom(5).row();

        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (onClick != null) {
                    onClick.run();
                }
            }
        });
    }

    public Card getCard() {
        return card;
    }
}
