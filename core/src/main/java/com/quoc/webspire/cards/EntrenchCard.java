package com.quoc.webspire.cards;

import com.quoc.webspire.entities.Character;
import com.quoc.webspire.entities.Enemy;

public class EntrenchCard extends Card {
    public EntrenchCard() {
        super("Cố Thủ", 2, Type.SKILL, "Nhân đôi lượng Giáp hiện có của bạn.");
    }

    @Override
    public boolean use(Character player, Enemy monster) {
        if (player.useEnergy(getCost())) {
            player.addBlock(player.getBlock());
            return true;
        }
        return false;
    }

    @Override
    public void upgrade() {
        if (!isUpgraded()) {
            setUpgraded(true);
            setCost(1);
            setName("Cố Thủ+");
            setDescription("Nhân đôi lượng Giáp hiện có của bạn. (Tiêu hao 1 NL)");
        }
    }
}
