package com.quoc.webspire.cards;

import com.quoc.webspire.entities.Character;
import com.quoc.webspire.entities.Enemy;
import com.quoc.webspire.entities.StatusEffect;

public class BashCard extends Card {
    public BashCard() {
        super("Cú Nện", 2, Type.ATTACK, "Gây 8 sát thương. Gây 2 Dễ Tổn Thương.");
    }

    @Override
    public boolean use(Character player, Enemy monster) {
        if (player.useEnergy(getCost())) {
            int damage = player.calculateDamageOutput(isUpgraded() ? 10 : 8);
            monster.takeDamage(damage);
            monster.applyStatus(StatusEffect.Type.VULNERABLE, isUpgraded() ? 3 : 2);
            return true;
        }
        return false;
    }

    @Override
    public void upgrade() {
        if (!isUpgraded()) {
            setUpgraded(true);
            setName("Cú Nện+");
            setDescription("Gây 10 sát thương. Gây 3 Dễ Tổn Thương.");
        }
    }
}
