package com.quoc.webspire;

public class StrikeCard extends Card {

    public StrikeCard() {
        // Tham số: Name, Cost, Damage, Block, Description
        super("Đánh", 1, 6, 0, "Gây 6 sát thương.");
    }

    @Override
    public void use(Character player, Enemy monster) {
        int totalDamage = isUpgraded() ? 9 : 6;
        monster.takeDamage(totalDamage + player.getStrength());
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
