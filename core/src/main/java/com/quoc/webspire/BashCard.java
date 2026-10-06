package com.quoc.webspire;

public class BashCard extends Card {

    public BashCard() {
        // Truyền đúng 5 tham số: (Name, Cost, Damage, Block, Description)
        super("Cú Nện", 2, 8, 0, "Gây 8 sát thương. Gây 2 lượt Dễ tổn thương.");
    }

    @Override
    public void use(Character player, Enemy monster) {
        int totalDamage = isUpgraded() ? 10 : 8;

        // Gây sát thương
        monster.takeDamage(totalDamage + player.getStrength());

        // Gây 2 lượt Dễ tổn thương (Vulnerable)
        monster.applyVulnerable(2);
    }

    @Override
    public void upgrade() {
        if (!isUpgraded()) {
            setUpgraded(true);
            setName("Cú Nện+");
            setDescription("Gây 10 sát thương. Gây 2 lượt Dễ tổn thương.");
        }
    }
}
