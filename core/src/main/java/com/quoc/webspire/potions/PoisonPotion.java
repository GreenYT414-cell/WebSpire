package com.quoc.webspire.potions;

import com.badlogic.gdx.graphics.Color;
import com.quoc.webspire.entities.Character;
import com.quoc.webspire.entities.Enemy;
import com.quoc.webspire.entities.StatusEffect;

public class PoisonPotion implements Potion {
    @Override
    public String getName() {
        return "Bình Độc Dược";
    }

    @Override
    public String getDescription() {
        return "Gieo ngay 6 tầng Độc lên kẻ địch xuyên qua Giáp.";
    }

    @Override
    public Color getColor() {
        return new Color(0.2f, 0.9f, 0.2f, 1f); // Xanh lá độc
    }

    @Override
    public void use(Character player, Enemy monster) {
        monster.applyStatus(StatusEffect.Type.POISON, 6);
    }
}
