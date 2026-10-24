package com.quoc.webspire.cards;

import com.quoc.webspire.entities.Character;
import com.quoc.webspire.entities.Enemy;
import com.quoc.webspire.entities.StatusEffect;

public class ShockwaveCard extends Card {
    public ShockwaveCard() {
        super("Sóng Xung Kích", 2, Type.SKILL, "Gây 3 Yếu Mềm và 3 Lỗ Hổng cho kẻ địch.");
    }

    @Override
    public boolean use(Character player, Enemy monster) {
        if (player.useEnergy(getCost())) {
            int turns = isUpgraded() ? 5 : 3;
            monster.applyStatus(StatusEffect.Type.WEAK, turns);
            monster.applyStatus(StatusEffect.Type.VULNERABLE, turns);
            return true;
        }
        return false;
    }

    @Override
    public void upgrade() {
        if (!isUpgraded()) {
            setUpgraded(true);
            setName("Sóng Xung Kích+");
            setDescription("Gây 5 Yếu Mềm và 5 Lỗ Hổng cho kẻ địch.");
        }
    }
}
