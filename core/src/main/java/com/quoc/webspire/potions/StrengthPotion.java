package com.quoc.webspire.potions;

import com.badlogic.gdx.graphics.Color;
import com.quoc.webspire.entities.Character;
import com.quoc.webspire.entities.Enemy;
import com.quoc.webspire.entities.StatusEffect;

public class StrengthPotion implements Potion {
    @Override
    public String getName() {
        return "Bình Cuồng Lực";
    }

    @Override
    public String getDescription() {
        return "Nhận thêm +2 Sức Mạnh cho đến hết trận đấu.";
    }

    @Override
    public Color getColor() {
        return new Color(0.95f, 0.5f, 0.1f, 1f); // Cam lửa
    }

    @Override
    public void use(Character player, Enemy monster) {
        player.addStatus(StatusEffect.Type.STRENGTH, 2);
    }
}
