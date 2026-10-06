package com.quoc.webspire;

public class IronWaveCard extends Card {

    public IronWaveCard() {
        // Tham số: Name, Cost, Damage, Block, Description
        super("Sóng Sắt", 1, 5, 5, "Nhận 5 Giáp. Gây 5 sát thương.");
    }

    @Override
    public void use(Character player, Enemy monster) {
        int totalBlock = isUpgraded() ? 7 : 5;
        int totalDamage = isUpgraded() ? 7 : 5;

        player.addBlock(totalBlock);
        monster.takeDamage(totalDamage + player.getStrength());
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
