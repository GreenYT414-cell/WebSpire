package com.quoc.webspire.cards;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.quoc.webspire.managers.Assets;

public class CardActor extends Table {
    private Card card;
    private boolean isHovered = false;
    private float originalY = -1f;
    private float originalRotation = 0f;
    private boolean isPlayable = true;
    private Table keywordTooltipTable;

    public CardActor(Card card, BitmapFont font, Runnable onClick) {
        this.card = card;

        Color bgColor;
        Color borderColor;
        Color accentColor;
        String artTextureName;

        switch (card.getType()) {
            case ATTACK:
                bgColor = new Color(0.25f, 0.08f, 0.08f, 0.98f);
                borderColor = new Color(0.92f, 0.28f, 0.24f, 1f);
                accentColor = new Color(1.0f, 0.45f, 0.35f, 1f);
                artTextureName = "card_art_attack.png";
                break;
            case DEFENSE:
                bgColor = new Color(0.08f, 0.16f, 0.32f, 0.98f);
                borderColor = new Color(0.24f, 0.58f, 0.96f, 1f);
                accentColor = new Color(0.45f, 0.78f, 1.0f, 1f);
                artTextureName = "card_art_defense.png";
                break;
            case SKILL:
                bgColor = new Color(0.07f, 0.24f, 0.16f, 0.98f);
                borderColor = new Color(0.22f, 0.85f, 0.52f, 1f);
                accentColor = new Color(0.50f, 1.0f, 0.70f, 1f);
                artTextureName = "card_art_skill.png";
                break;
            case POWER:
                bgColor = new Color(0.26f, 0.12f, 0.38f, 0.98f);
                borderColor = new Color(0.82f, 0.42f, 0.98f, 1f);
                accentColor = new Color(0.95f, 0.65f, 1.0f, 1f);
                artTextureName = "card_art_power.png";
                break;
            default:
                bgColor = new Color(0.15f, 0.15f, 0.18f, 0.98f);
                borderColor = Color.GRAY;
                accentColor = Color.LIGHT_GRAY;
                artTextureName = "";
                break;
        }

        // Nếu lá bài đã nâng cấp (+): Viền Vàng Hoàng Kim & Phát sáng rực rỡ
        if (card.isUpgraded()) {
            borderColor = new Color(1.0f, 0.85f, 0.20f, 1f);
            accentColor = new Color(1.0f, 0.95f, 0.45f, 1f);
        }

        int borderWidth = card.isUpgraded() ? 3 : 2;
        setBackground(Assets.createBorderedDrawable(145, 205, bgColor, borderColor, borderWidth));
        pad(borderWidth);

        // Khung nội dung bên trong của lá bài
        Table innerCard = new Table();
        innerCard.setBackground(Assets.createColorDrawable(bgColor.r, bgColor.g, bgColor.b, bgColor.a));
        innerCard.pad(5);

        // 1. HEADER: Ngọc năng lượng (Energy Orb) + Tên lá bài
        Table headerTable = new Table();

        // Ngọc Năng Lượng ma thuật
        Table energyOrb = new Table();
        Color orbBg = card.isUpgraded() ? new Color(0.35f, 0.25f, 0.05f, 1f) : new Color(0.08f, 0.28f, 0.65f, 1f);
        Color orbBorder = card.isUpgraded() ? new Color(1.0f, 0.85f, 0.25f, 1f) : new Color(0.45f, 0.85f, 1.0f, 1f);
        energyOrb.setBackground(Assets.createBorderedDrawable(26, 26, orbBg, orbBorder, 1));

        Label.LabelStyle orbStyle = new Label.LabelStyle(font, Color.WHITE);
        Label costLabel = new Label(String.valueOf(card.getCost()), orbStyle);
        energyOrb.add(costLabel).pad(1, 6, 1, 6);

        Label.LabelStyle nameStyle = new Label.LabelStyle(font, card.isUpgraded() ? Color.GOLD : Color.WHITE);
        Label nameLabel = new Label(card.getName(), nameStyle);
        nameLabel.setEllipsis(true);

        headerTable.add(energyOrb).left().padRight(5);
        headerTable.add(nameLabel).expandX().center();
        innerCard.add(headerTable).fillX().padBottom(4).row();

        // 2. KHUNG TRANH ARTWORK
        Texture artTex = Assets.getTexture(artTextureName);
        Table artBox = new Table();
        artBox.setBackground(Assets.createBorderedDrawable(118, 68, new Color(0.04f, 0.04f, 0.06f, 0.95f), accentColor, 1));
        artBox.pad(1);
        if (artTex != null) {
            artBox.add(new Image(artTex)).size(116, 66);
        } else {
            artBox.add(new Table()).size(116, 66);
        }
        innerCard.add(artBox).size(120, 70).padBottom(4).row();

        // 3. HUY HIỆU PHÂN LOẠI (TAG BADGE)
        String typeText;
        switch (card.getType()) {
            case ATTACK: typeText = card.isUpgraded() ? "✦ TẤN CÔNG+ ✦" : "— TẤN CÔNG —"; break;
            case DEFENSE: typeText = card.isUpgraded() ? "✦ PHÒNG THỦ+ ✦" : "— PHÒNG THỦ —"; break;
            case SKILL: typeText = card.isUpgraded() ? "✦ KỸ NĂNG+ ✦" : "— KỸ NĂNG —"; break;
            case POWER: typeText = card.isUpgraded() ? "✦ NĂNG LỰC+ ✦" : "— NĂNG LỰC —"; break;
            default: typeText = "— BÀI —"; break;
        }
        Label.LabelStyle typeStyle = new Label.LabelStyle(font, accentColor);
        Label typeBadge = new Label(typeText, typeStyle);
        innerCard.add(typeBadge).padBottom(3).row();

        // 4. KHUNG MÔ TẢ CHI TIẾT HIỆU ỨNG (FROSTED GLASS EFFECT)
        Table descBox = new Table();
        descBox.setBackground(Assets.createBorderedDrawable(125, 48, new Color(0.04f, 0.04f, 0.08f, 0.85f), new Color(0.20f, 0.22f, 0.30f, 0.6f), 1));
        descBox.pad(3, 5, 3, 5);

        Label.LabelStyle descStyle = new Label.LabelStyle(font, new Color(0.95f, 0.95f, 0.98f, 1f));
        Label descLabel = new Label(card.getDescription(), descStyle);
        descLabel.setWrap(true);
        descBox.add(descLabel).width(115).expand().fill();

        innerCard.add(descBox).width(125).height(48).padBottom(2).row();

        add(innerCard).expand().fill();

        buildKeywordTooltip(font);

        setTransform(true);
        setOrigin(Align.center);

        // Hiệu ứng tương tác rê chuột (Hover Micro-animation)
        addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1 && !isHovered) {
                    isHovered = true;
                    if (originalY == -1f) {
                        originalY = getY();
                    }
                    clearActions();
                    toFront();
                    addAction(Actions.parallel(
                        Actions.scaleTo(1.22f, 1.22f, 0.12f, Interpolation.pow2Out),
                        Actions.moveTo(getX(), originalY + 36f, 0.12f, Interpolation.pow2Out),
                        Actions.rotateTo(0f, 0.12f, Interpolation.pow2Out)
                    ));

                    if (keywordTooltipTable != null) {
                        keywordTooltipTable.setVisible(true);
                    }
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (pointer == -1 && isHovered) {
                    isHovered = false;
                    clearActions();
                    if (originalY != -1f) {
                        addAction(Actions.parallel(
                            Actions.scaleTo(1.0f, 1.0f, 0.1f, Interpolation.pow2In),
                            Actions.moveTo(getX(), originalY, 0.1f, Interpolation.pow2In),
                            Actions.rotateTo(originalRotation, 0.1f, Interpolation.pow2In)
                        ));
                    }

                    if (keywordTooltipTable != null) {
                        keywordTooltipTable.setVisible(false);
                    }
                }
            }
        });

        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (onClick != null) {
                    onClick.run();
                }
            }
        });
    }

    public void setFanRotation(float rotation) {
        this.originalRotation = rotation;
        setRotation(rotation);
    }

    public void setPlayable(boolean playable) {
        this.isPlayable = playable;
        if (!playable) {
            setColor(0.55f, 0.55f, 0.65f, 0.75f);
        } else {
            setColor(1f, 1f, 1f, 1f);
        }
    }

    private void buildKeywordTooltip(BitmapFont font) {
        String desc = card.getDescription().toLowerCase();
        String cardName = card.getName().toLowerCase();
        StringBuilder sb = new StringBuilder();

        if (desc.contains("độc")) {
            sb.append("☠ ĐỘC: Sát thương trực tiếp xuyên Giáp vào cuối lượt.\n");
        }
        if (desc.contains("giáp")) {
            sb.append("🛡 GIÁP: Hấp thụ sát thương nhận vào trong lượt này.\n");
        }
        if (desc.contains("yếu mềm") || cardName.contains("shockwave")) {
            sb.append("🕸 YẾU MỀM: Giảm 25% sát thương gây ra từ các đòn đánh.\n");
        }
        if (desc.contains("sức mạnh")) {
            sb.append("⚔ SỨC MẠNH: Tăng thêm sát thương cho mọi lá Tấn Công.\n");
        }
        if (desc.contains("dễ tổn thương") || desc.contains("choáng") || cardName.contains("cú nện") || cardName.contains("bash")) {
            sb.append("💥 DỄ TỔN THƯƠNG: Mục tiêu nhận thêm 50% sát thương từ đòn đánh.\n");
        }
        if (desc.contains("khéo léo")) {
            sb.append("🍃 KHÉO LÉO: Tăng thêm giáp nhận được từ mọi lá Phòng Thủ.\n");
        }

        if (sb.length() > 0) {
            keywordTooltipTable = new Table();
            keywordTooltipTable.setBackground(Assets.createBorderedDrawable(165, 80, new Color(0.04f, 0.04f, 0.08f, 0.95f), new Color(0.35f, 0.55f, 0.85f, 0.85f), 1));
            keywordTooltipTable.pad(6, 8, 6, 8);

            Label.LabelStyle tooltipStyle = new Label.LabelStyle(font, Color.LIME);
            Label label = new Label(sb.toString().trim(), tooltipStyle);
            label.setWrap(true);

            keywordTooltipTable.add(label).width(155);
            keywordTooltipTable.setPosition(0, 215);
            keywordTooltipTable.setVisible(false);

            addActor(keywordTooltipTable);
        }
    }

    public Card getCard() {
        return card;
    }
}
