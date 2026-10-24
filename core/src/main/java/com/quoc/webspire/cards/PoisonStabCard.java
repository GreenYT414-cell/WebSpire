package com.quoc.webspire.cards;

import com.quoc.webspire.entities.Character;
import com.quoc.webspire.entities.Enemy;
import com.quoc.webspire.entities.StatusEffect;

public class PoisonStabCard extends Card {
    public PoisonStabCard() {
        super("Đâm Độc", 1, Type.ATTACK, "Gây 6 sát thương. Cho 3 Độc.");
    }

    @Override
    public boolean use(Character player, Enemy monster) {
        if (player.useEnergy(getCost())) {
            int damage = player.calculateDamageOutput(isUpgraded() ? 8 : 6);
            monster.takeDamage(damage);
            monster.applyStatus(StatusEffect.Type.POISON, isUpgraded() ? 4 : 3);
            return true;
        }
        return false;
    }

    @Override
    public void upgrade() {
        if (!isUpgraded()) {
            setUpgraded(true);
            setName("Đâm Độc+");
            setDescription("Gây 8 sát thương. Cho 4 Độc.");
        }
    }
}
