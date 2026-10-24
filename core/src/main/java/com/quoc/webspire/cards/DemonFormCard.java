package com.quoc.webspire.cards;

import com.quoc.webspire.entities.Character;
import com.quoc.webspire.entities.Enemy;
import com.quoc.webspire.entities.StatusEffect;

public class DemonFormCard extends Card {
    public DemonFormCard() {
        super("Hình Thái Ác Quỷ", 3, Type.POWER, "Đầu mỗi lượt chiến đấu, nhận +2 Sức Mạnh.");
    }

    @Override
    public boolean use(Character player, Enemy monster) {
        if (player.useEnergy(getCost())) {
            player.addStatus(StatusEffect.Type.DEMON_FORM, isUpgraded() ? 3 : 2);
            return true;
        }
        return false;
    }

    @Override
    public void upgrade() {
        if (!isUpgraded()) {
            setUpgraded(true);
            setName("Hình Thái Ác Quỷ+");
            setDescription("Đầu mỗi lượt chiến đấu, nhận +3 Sức Mạnh.");
        }
    }
}
