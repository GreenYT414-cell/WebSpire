package com.quoc.webspire.cards;

import com.quoc.webspire.entities.Character;
import com.quoc.webspire.entities.Enemy;

public class TwinStrikeCard extends Card {
    public TwinStrikeCard() {
        super("Song Kích", 1, Type.ATTACK, "Chém 2 nhát, mỗi nhát gây 5 sát thương.");
    }

    @Override
    public boolean use(Character player, Enemy monster) {
        if (player.useEnergy(getCost())) {
            int base = isUpgraded() ? 7 : 5;
            int damage = player.calculateDamageOutput(base);
            monster.takeDamage(damage);
            monster.takeDamage(damage);
            return true;
        }
        return false;
    }

    @Override
    public void upgrade() {
        if (!isUpgraded()) {
            setUpgraded(true);
            setName("Song Kích+");
            setDescription("Chém 2 nhát, mỗi nhát gây 7 sát thương.");
        }
    }
}
