package com.quoc.webspire.cards;

import com.quoc.webspire.entities.Character;
import com.quoc.webspire.entities.Enemy;

public class CleaveCard extends Card {
    public CleaveCard() {
        super("Quét Kiếm", 1, Type.ATTACK, "Chém một đường quét rộng gây 8 sát thương.");
    }

    @Override
    public boolean use(Character player, Enemy monster) {
        if (player.useEnergy(getCost())) {
            int damage = player.calculateDamageOutput(isUpgraded() ? 11 : 8);
            monster.takeDamage(damage);
            return true;
        }
        return false;
    }

    @Override
    public void upgrade() {
        if (!isUpgraded()) {
            setUpgraded(true);
            setName("Quét Kiếm+");
            setDescription("Chém một đường quét rộng gây 11 sát thương.");
        }
    }
}
