package com.quoc.webspire.cards;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
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
        String artTextureName;

        switch (card.getType()) {
            case ATTACK:
                bgColor = new Color(0.32f, 0.10f, 0.10f, 0.98f);
                borderColor = new Color(0.85f, 0.28f, 0.28f, 1f);
                artTextureName = "card_art_attack.png";
                break;
            case DEFENSE:
                bgColor = new Color(0.10f, 0.20f, 0.36f, 0.98f);
                borderColor = new Color(0.28f, 0.58f, 0.92f, 1f);
                artTextureName = "card_art_defense.png";
                break;
            case SKILL:
                bgColor = new Color(0.10f, 0.28f, 0.18f, 0.98f);
                borderColor = new Color(0.28f, 0.82f, 0.48f, 1f);
                artTextureName = "card_art_skill.png";
                break;
            case POWER:
                bgColor = new Color(0.32f, 0.24f, 0.08f, 0.98f);
                borderColor = new Color(0.92f, 0.72f, 0.22f, 1f);
                artTextureName = "card_art_power.png";
                break;
            default:
                bgColor = new Color(0.18f, 0.18f, 0.18f, 0.98f);
                borderColor = Color.GRAY;
                artTextureName = "";
                break;
        }

        // Nếu lá bài đã nâng cấp (+): Viền vàng kim hoàng gia rực rỡ
        if (card.isUpgraded()) {
            borderColor = new Color(1f, 0.88f, 0.25f, 1f);
        }

        // Lớp viền ngoài (Border Frame)
        setBackground(Assets.createBorderedDrawable(140, 196, bgColor, borderColor, card.isUpgraded() ? 3 : 2));
        pad(card.isUpgraded() ? 3 : 2);

        // Thân bên trong của lá bài
        Table innerCard = new Table();
        innerCard.setBackground(Assets.createColorDrawable(bgColor.r, bgColor.g, bgColor.b, bgColor.a));
        innerCard.pad(5);

        // Header: Năng lượng (Energy Orb) + Tên lá bài
        Table headerTable = new Table();

        // Huy hiệu năng lượng dạng ngọc ma thuật phát sáng
        Table energyOrb = new Table();
        energyOrb.setBackground(Assets.createBorderedDrawable(24, 24, new Color(0.10f, 0.40f, 0.85f, 1f), new Color(0.65f, 0.90f, 1.0f, 1f), 1));
        Label.LabelStyle orbStyle = new Label.LabelStyle(font, Color.WHITE);
        Label costLabel = new Label(String.valueOf(card.getCost()), orbStyle);
        energyOrb.add(costLabel).pad(2, 5, 2, 5);

        Label.LabelStyle nameStyle = new Label.LabelStyle(font, card.isUpgraded() ? Color.GREEN : Color.GOLD);
        Label nameLabel = new Label(card.getName(), nameStyle);
        nameLabel.setEllipsis(true);

        headerTable.add(energyOrb).left().padRight(4);
        headerTable.add(nameLabel).expandX().center();
        innerCard.add(headerTable).fillX().padBottom(4).row();

        // Artwork khung tranh lá bài
        Texture artTex = Assets.getTexture(artTextureName);
        Table artBox = new Table();
        artBox.setBackground(Assets.createColorDrawable(0.05f, 0.05f, 0.05f, 0.9f));
        artBox.pad(2);
        if (artTex != null) {
            artBox.add(new Image(artTex)).size(112, 62);
        } else {
            artBox.add(new Table()).size(112, 62);
        }
        innerCard.add(artBox).size(116, 66).padBottom(4).row();

        // Loại lá bài (Tag badge)
        String typeText;
        switch (card.getType()) {
            case ATTACK: typeText = "— TẤN CÔNG —"; break;
            case DEFENSE: typeText = "— PHÒNG THỦ —"; break;
            case SKILL: typeText = "— KỸ NĂNG —"; break;
            case POWER: typeText = "— NĂNG LỰC —"; break;
            default: typeText = "— BÀI —"; break;
        }
        Label.LabelStyle typeStyle = new Label.LabelStyle(font, new Color(0.8f, 0.85f, 0.9f, 0.7f));
        Label typeBadge = new Label(typeText, typeStyle);
        innerCard.add(typeBadge).padBottom(3).row();

        // Mô tả hiệu ứng lá bài
        Label.LabelStyle descStyle = new Label.LabelStyle(font, Color.WHITE);
        Label descLabel = new Label(card.getDescription(), descStyle);
        descLabel.setWrap(true);
        innerCard.add(descLabel).width(120).expandY().top().padBottom(2).row();

        add(innerCard).expand().fill();

        buildKeywordTooltip(font);

        setTransform(true);
        setOrigin(Align.center);

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
                        Actions.scaleTo(1.18f, 1.18f, 0.12f, Interpolation.pow2Out),
                        Actions.moveTo(getX(), originalY + 30f, 0.12f, Interpolation.pow2Out),
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
            setColor(0.62f, 0.62f, 0.68f, 0.85f);
        } else {
            setColor(1f, 1f, 1f, 1f);
        }
    }

    private void buildKeywordTooltip(BitmapFont font) {
        String desc = card.getDescription().toLowerCase();
        String cardName = card.getName().toLowerCase();
        StringBuilder sb = new StringBuilder();

        if (desc.contains("độc")) {
            sb.append("• ĐỘC: Sát thương trực tiếp xuyên Giáp vào cuối lượt.\n");
        }
        if (desc.contains("giáp")) {
            sb.append("• GIÁP: Chặn sát thương nhận vào trong lượt này.\n");
        }
        if (desc.contains("yếu mềm") || cardName.contains("shockwave")) {
            sb.append("• YẾU MỀM: Giảm 25% sát thương gây ra từ các đòn đánh.\n");
        }
        if (desc.contains("sức mạnh")) {
            sb.append("• SỨC MẠNH: Tăng thêm sát thương cho tất cả lá Tấn Công.\n");
        }
        if (desc.contains("dễ tổn thương") || desc.contains("choáng") || cardName.contains("cú nện") || cardName.contains("bash")) {
            sb.append("• DỄ TỔN THƯƠNG: Mục tiêu chịu thêm 50% sát thương từ đòn đánh.\n");
        }

        if (sb.length() > 0) {
            keywordTooltipTable = new Table();
            keywordTooltipTable.setBackground(Assets.createColorDrawable(0f, 0f, 0f, 0.92f));
            keywordTooltipTable.pad(6);

            Label.LabelStyle tooltipStyle = new Label.LabelStyle(font, Color.LIME);
            Label label = new Label(sb.toString().trim(), tooltipStyle);
            label.setWrap(true);

            keywordTooltipTable.add(label).width(155);
            keywordTooltipTable.setPosition(0, 205);
            keywordTooltipTable.setVisible(false);

            addActor(keywordTooltipTable);
        }
    }

    public Card getCard() {
        return card;
    }
}
