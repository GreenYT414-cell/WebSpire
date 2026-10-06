package com.quoc.webspire;

import com.quoc.webspire.enums.CardType;

public class IronWaveCard extends Card {
    private int damage;
    private int block;

    public IronWaveCard() {
        super("Sóng Sắt", 1, CardType.ATTACK, "Gây 5 sát thương. Nhận 5 giáp.");
        this.damage = 5;
        this.block = 5;
    }

    @Override
    public void upgrade() {
        if (!isUpgraded()) {
            setUpgraded(true);
            this.damage = 7;
            this.block = 7;
            setDescription("Gây 7 sát thương. Nhận 7 giáp.");
        }
    }

    @Override
    public void play(Character player, Enemy monster) {
        int finalDamage = damage + player.getStrength();
        if (player.getWeakTurns() > 0) finalDamage = (int)(finalDamage * 0.75f);
        if (monster.getVulnerableTurns() > 0) finalDamage = (int)(finalDamage * 1.5f);

        monster.takeDamage(finalDamage);
        player.addBlock(block);
    }
}
