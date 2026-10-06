package com.quoc.webspire;

import com.quoc.webspire.enums.CardType;

public class StrikeCard extends Card {
    private int damage;

    public StrikeCard() {
        super("Tấn Công", 1, CardType.ATTACK, "Gây 6 sát thương.");
        this.damage = 6;
    }

    @Override
    public void upgrade() {
        if (!isUpgraded()) {
            setUpgraded(true);
            this.damage = 9;
            setDescription("Gây 9 sát thương.");
        }
    }

    @Override
    public void play(Character player, Enemy monster) {
        int finalDamage = damage + player.getStrength();
        if (player.getWeakTurns() > 0) finalDamage = (int)(finalDamage * 0.75f);
        if (monster.getVulnerableTurns() > 0) finalDamage = (int)(finalDamage * 1.5f);
        monster.takeDamage(finalDamage);
    }
}
