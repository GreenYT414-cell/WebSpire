package com.quoc.webspire;

public class PoisonStabCard extends Card {
    public PoisonStabCard() {
        super("Đâm Độc", 1, CardType.ATTACK, "Gây 6 sát thương. Gán 3 Độc.");
    }

    @Override
    public boolean use(Character player, Enemy monster) {
        if (player.useEnergy(getCost())) {
            int damage = player.calculateDamageOutput(isUpgraded() ? 9 : 6);
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
            setDescription("Gây 9 sát thương. Gán 4 Độc.");
        }
    }
}
