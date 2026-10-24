package com.quoc.webspire.cards;

import com.quoc.webspire.entities.Character;
import com.quoc.webspire.entities.Enemy;

public class BloodlettingCard extends Card {
    public BloodlettingCard() {
        super("Hiến Máu Lấy Lực", 0, Type.SKILL, "Mất 3 Máu. Nhận ngay 2 Năng Lượng.");
    }

    @Override
    public boolean use(Character player, Enemy monster) {
        player.takeDamage(3);
        int gain = isUpgraded() ? 3 : 2;
        player.setEnergy(player.getEnergy() + gain);
        return true;
    }

    @Override
    public void upgrade() {
        if (!isUpgraded()) {
            setUpgraded(true);
            setName("Hiến Máu Lấy Lực+");
            setDescription("Mất 3 Máu. Nhận ngay 3 Năng Lượng.");
        }
    }
}
