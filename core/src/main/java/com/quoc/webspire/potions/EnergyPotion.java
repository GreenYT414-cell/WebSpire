package com.quoc.webspire.potions;

import com.badlogic.gdx.graphics.Color;
import com.quoc.webspire.entities.Character;
import com.quoc.webspire.entities.Enemy;

public class EnergyPotion implements Potion {
    @Override
    public String getName() {
        return "Bình Năng Lượng";
    }

    @Override
    public String getDescription() {
        return "Nhận thêm ngay 2 Năng Lượng trong lượt hiện tại.";
    }

    @Override
    public Color getColor() {
        return new Color(0.2f, 0.8f, 1f, 1f); // Xanh dương phát sáng
    }

    @Override
    public void use(Character player, Enemy monster) {
        player.addEnergy(2);
    }
}
