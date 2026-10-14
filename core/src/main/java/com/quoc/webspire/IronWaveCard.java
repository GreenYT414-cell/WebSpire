package com.quoc.webspire;

public class IronWaveCard extends Card {
    public IronWaveCard() {
        super("Sóng Sắt", 1, CardType.ATTACK, "Nhận 5 Giáp. Gây 5 sát thương.");
    }

    @Override
    public boolean use(Character player, Enemy monster) {
        if (player.useEnergy(getCost())) {
            player.addBlock(isUpgraded() ? 7 : 5);
            int damage = player.calculateDamageOutput(isUpgraded() ? 7 : 5);
            monster.takeDamage(damage);
            return true;
        }
        return false;
    }

    @Override
    public void upgrade() {
        if (!isUpgraded()) {
            setUpgraded(true);
            setName("Sóng Sắt+");
            setDescription("Nhận 7 Giáp. Gây 7 sát thương.");
        }
    }
}
