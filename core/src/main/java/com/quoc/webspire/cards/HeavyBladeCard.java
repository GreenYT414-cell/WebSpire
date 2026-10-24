package com.quoc.webspire.cards;

import com.quoc.webspire.entities.Character;
import com.quoc.webspire.entities.Enemy;
import com.quoc.webspire.entities.StatusEffect;

public class HeavyBladeCard extends Card {
    public HeavyBladeCard() {
        super("Đại Đao Trảm", 2, Type.ATTACK, "Gây 14 sát thương. Sức Mạnh tăng hiệu quả gấp 3 lần.");
    }

    @Override
    public boolean use(Character player, Enemy monster) {
        if (player.useEnergy(getCost())) {
            int str = player.getStatus(StatusEffect.Type.STRENGTH);
            int multiplier = isUpgraded() ? 5 : 3;
            int base = isUpgraded() ? 16 : 14;
            // base + (multiplier - 1) * str is passed to calculateDamageOutput which adds str once
            int effectiveBase = base + (multiplier - 1) * Math.max(0, str);
            int damage = player.calculateDamageOutput(effectiveBase);
            monster.takeDamage(damage);
            return true;
        }
        return false;
    }

    @Override
    public void upgrade() {
        if (!isUpgraded()) {
            setUpgraded(true);
            setName("Đại Đao Trảm+");
            setDescription("Gây 16 sát thương. Sức Mạnh tăng hiệu quả gấp 5 lần.");
        }
    }
}
