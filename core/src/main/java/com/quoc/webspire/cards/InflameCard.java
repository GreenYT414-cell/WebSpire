package com.quoc.webspire.cards;

import com.quoc.webspire.entities.Character;
import com.quoc.webspire.entities.Enemy;
import com.quoc.webspire.entities.StatusEffect;

public class InflameCard extends Card {
    public InflameCard() {
        super("Bùng Cháy", 1, Type.POWER, "Nhận 2 Sức Mạnh. (Tăng sát thương các lá đòn đánh)");
    }

    @Override
    public boolean use(Character player, Enemy monster) {
        if (player.useEnergy(getCost())) {
            player.applyStatus(StatusEffect.Type.STRENGTH, isUpgraded() ? 3 : 2);
            return true;
        }
        return false;
    }

    @Override
    public void upgrade() {
        if (!isUpgraded()) {
            setUpgraded(true);
            setName("Bùng Cháy+");
            setDescription("Nhận 3 Sức Mạnh. (Tăng sát thương các lá đòn đánh)");
        }
    }
}
