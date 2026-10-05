package com.quoc.webspire;

import com.quoc.webspire.enums.CardType;

public class BashCard extends Card {
    private int damage;
    private int vulnerableTurns;

    public BashCard() {
        super("Đòn Tàn Khốc", 2, CardType.ATTACK, "Gây 8 sát thương. Gây 2 Dễ bị tổn thương.");
        this.damage = 8;
        this.vulnerableTurns = 2;
    }

    @Override
    public void play(Character player, Enemy monster) {
        int finalDamage = damage + player.getStrength();
        if (player.getWeakTurns() > 0) {
            finalDamage = (int)(finalDamage * 0.75f);
        }
        if (monster.getVulnerableTurns() > 0) {
            finalDamage = (int)(finalDamage * 1.5f);
        }

        monster.takeDamage(finalDamage);
        monster.applyVulnerable(vulnerableTurns);
    }
}
