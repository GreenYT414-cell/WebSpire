package com.quoc.webspire;

public class StrikeCard extends Card {
    public StrikeCard() {
        super("Đánh", 1, CardType.ATTACK, "Gây 6 sát thương.");
    }

    @Override
    public boolean use(Character player, Enemy monster) {
        if (player.useEnergy(getCost())) {
            int damage = player.calculateDamageOutput(isUpgraded() ? 9 : 6);
            monster.takeDamage(damage);
            return true;
        }
        return false;
    }

    @Override
    public void upgrade() {
        if (!isUpgraded()) {
            setUpgraded(true);
            setName("Đánh+");
            setDescription("Gây 9 sát thương.");
        }
    }
}
