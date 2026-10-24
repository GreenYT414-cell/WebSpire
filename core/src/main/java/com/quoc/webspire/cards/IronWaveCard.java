package com.quoc.webspire.cards;

import com.quoc.webspire.entities.Character;
import com.quoc.webspire.entities.Enemy;

public class IronWaveCard extends Card {
    public IronWaveCard() {
        super("Sóng Sắt", 1, Type.ATTACK, "Nhận 5 Giáp. Gây 5 sát thương.");
    }

    @Override
    public boolean use(Character player, Enemy monster) {
        if (player.useEnergy(getCost())) {
            player.addBlock(isUpgraded() ? 7 : 5);
            int damage = player.calculateDamageOutput(isUpgraded() ? 7 : 5);
            monster.takeDamage(damage);
            return true;
        }
        return false;
    }

    @Override
    public void upgrade() {
        if (!isUpgraded()) {
            setUpgraded(true);
            setName("Sóng Sắt+");
            setDescription("Nhận 7 Giáp. Gây 7 sát thương.");
        }
    }
}
