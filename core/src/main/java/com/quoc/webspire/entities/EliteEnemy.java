package com.quoc.webspire.entities;

import com.quoc.webspire.enums.EnemyIntent;

public class EliteEnemy extends Enemy {
    private int turnCycle = 0;

    public EliteEnemy(String name, int maxHp) {
        super(name, maxHp, 0);
    }

    @Override
    public void rollIntent() {
        turnCycle++;
        int step = (turnCycle - 1) % 4;

        if (step == 0) {
            this.currentIntent = EnemyIntent.ATTACK;
            this.intentLabel = "Gầm Rú Tụ Lực (+2 SM, Đánh 8)";
            this.intentValue = 8;
        } else if (step == 1) {
            this.currentIntent = EnemyIntent.ATTACK;
            this.intentLabel = "Cuồng Trảm Tinh Anh";
            this.intentValue = 14;
        } else if (step == 2) {
            this.currentIntent = EnemyIntent.DEFEND;
            this.intentLabel = "Khiên Thép Kiên Cố";
            this.intentValue = 15;
        } else {
            this.currentIntent = EnemyIntent.ATTACK;
            this.intentLabel = "Đòn Nghiền Nát";
            this.intentValue = 20;
        }
    }

    @Override
    public void executeIntent(Character player) {
        int step = (turnCycle - 1) % 4;
        if (step == 0) {
            applyStatus(StatusEffect.Type.STRENGTH, 2);
        } else if (step == 1) {
            player.applyStatus(StatusEffect.Type.VULNERABLE, 1);
        }
        super.executeIntent(player);
    }
}
