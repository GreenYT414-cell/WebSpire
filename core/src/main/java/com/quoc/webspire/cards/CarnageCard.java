package com.quoc.webspire.cards;

import com.quoc.webspire.entities.Character;
import com.quoc.webspire.entities.Enemy;

public class CarnageCard extends Card {
    public CarnageCard() {
        super("Tàn Sát", 2, Type.ATTACK, "Gây 20 sát thương cực mạnh.");
    }

    @Override
    public boolean use(Character player, Enemy monster) {
        if (player.useEnergy(getCost())) {
            int damage = player.calculateDamageOutput(isUpgraded() ? 26 : 20);
            monster.takeDamage(damage);
            return true;
        }
        return false;
    }

    @Override
    public void upgrade() {
        if (!isUpgraded()) {
            setUpgraded(true);
            setName("Tàn Sát+");
            setDescription("Gây 26 sát thương cực mạnh.");
        }
    }
}
