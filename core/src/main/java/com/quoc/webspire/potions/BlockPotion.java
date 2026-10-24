package com.quoc.webspire.potions;

import com.badlogic.gdx.graphics.Color;
import com.quoc.webspire.entities.Character;
import com.quoc.webspire.entities.Enemy;

public class BlockPotion implements Potion {
    @Override
    public String getName() {
        return "Bình Hộ Giáp";
    }

    @Override
    public String getDescription() {
        return "Nhận ngay 12 Giáp chống đỡ sát thương.";
    }

    @Override
    public Color getColor() {
        return new Color(0.3f, 0.6f, 0.9f, 1f); // Xanh khiên
    }

    @Override
    public void use(Character player, Enemy monster) {
        player.addBlock(12);
    }
}
