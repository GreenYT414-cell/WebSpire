package com.quoc.webspire;

public class BashCard extends Card {
    public BashCard() {
        super("Cú Nện", 2, CardType.ATTACK, "Gây 8 sát thương. Gán 2 Lỗ Hổng.");
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
            setDescription("Gây 10 sát thương. Gán 3 Lỗ Hổng.");
        }
    }
}
