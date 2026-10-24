package com.quoc.webspire.cards;

import com.quoc.webspire.entities.Character;
import com.quoc.webspire.entities.Enemy;
import com.quoc.webspire.entities.StatusEffect;

public class MetallicizeCard extends Card {
    public MetallicizeCard() {
        super("Kim Loại Hóa", 1, Type.POWER, "Cuối mỗi lượt, tự động nhận 3 Giáp.");
    }

    @Override
    public boolean use(Character player, Enemy monster) {
        if (player.useEnergy(getCost())) {
            player.applyStatus(StatusEffect.Type.METALLICIZE, isUpgraded() ? 5 : 3);
            return true;
        }
        return false;
    }

    @Override
    public void upgrade() {
        if (!isUpgraded()) {
            setUpgraded(true);
            setName("Kim Loại Hóa+");
            setDescription("Cuối mỗi lượt, tự động nhận 5 Giáp.");
        }
    }
}
