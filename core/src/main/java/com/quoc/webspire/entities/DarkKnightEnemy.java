package com.quoc.webspire.entities;

import com.quoc.webspire.enums.EnemyIntent;

public class DarkKnightEnemy extends Enemy {
    private int turnCycle = 0;

    public DarkKnightEnemy(String name, int maxHp) {
        super(name, maxHp, 0);
    }

    @Override
    public void rollIntent() {
        turnCycle++;
        if (turnCycle % 2 == 1) {
            this.currentIntent = EnemyIntent.ATTACK;
            this.intentLabel = "Chém Bóng Đêm";
            this.intentValue = 10;
        } else {
            this.currentIntent = EnemyIntent.DEFEND;
            this.intentLabel = "Khiên Bóng Tối";
            this.intentValue = 10;
        }
    }
}
