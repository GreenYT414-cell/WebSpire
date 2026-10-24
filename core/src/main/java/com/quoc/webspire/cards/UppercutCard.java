package com.quoc.webspire.cards;

import com.quoc.webspire.entities.Character;
import com.quoc.webspire.entities.Enemy;
import com.quoc.webspire.entities.StatusEffect;

public class UppercutCard extends Card {
    public UppercutCard() {
        super("Cú Móc Hàm", 2, Type.ATTACK, "Gây 13 sát thương. Gây 1 Yếu Mềm và 1 Dễ Tổn Thương.");
    }

    @Override
    public boolean use(Character player, Enemy monster) {
        if (player.useEnergy(getCost())) {
            int damage = player.calculateDamageOutput(isUpgraded() ? 17 : 13);
            monster.takeDamage(damage);
            int debuff = isUpgraded() ? 2 : 1;
            monster.applyStatus(StatusEffect.Type.WEAK, debuff);
            monster.applyStatus(StatusEffect.Type.VULNERABLE, debuff);
            return true;
        }
        return false;
    }

    @Override
    public void upgrade() {
        if (!isUpgraded()) {
            setUpgraded(true);
            setName("Cú Móc Hàm+");
            setDescription("Gây 17 sát thương. Gây 2 Yếu Mềm và 2 Dễ Tổn Thương.");
        }
    }
}
