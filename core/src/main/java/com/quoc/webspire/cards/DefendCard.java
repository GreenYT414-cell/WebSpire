package com.quoc.webspire.cards;

import com.quoc.webspire.entities.Character;
import com.quoc.webspire.entities.Enemy;

public class DefendCard extends Card {
    public DefendCard() {
        super("Phòng Thủ", 1, Type.DEFENSE, "Nhận 5 Giáp.");
    }

    @Override
    public boolean use(Character player, Enemy monster) {
        if (player.useEnergy(getCost())) {
            player.addBlock(isUpgraded() ? 8 : 5);
            return true;
        }
        return false;
    }

    @Override
    public void upgrade() {
        if (!isUpgraded()) {
            setUpgraded(true);
            setName("Phòng Thủ+");
            setDescription("Nhận 8 Giáp.");
        }
    }
}
