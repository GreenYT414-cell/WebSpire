package com.quoc.webspire.potions;

import com.badlogic.gdx.graphics.Color;
import com.quoc.webspire.entities.Character;
import com.quoc.webspire.entities.Enemy;

public class HealthPotion implements Potion {
    @Override
    public String getName() {
        return "Bình Hồi Máu";
    }

    @Override
    public String getDescription() {
        return "Hồi phục ngay lập tức 20 HP.";
    }

    @Override
    public Color getColor() {
        return new Color(0.9f, 0.2f, 0.2f, 1f); // Đỏ thắm
    }

    @Override
    public void use(Character player, Enemy monster) {
        player.heal(20);
    }
}
