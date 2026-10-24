package com.quoc.webspire.entities;

import com.quoc.webspire.enums.EnemyIntent;

public class MidBossEnemy extends Enemy {
    private int turnCycle = 0;

    public MidBossEnemy(String name, int maxHp) {
        super(name, maxHp, 0);
    }

    @Override
    public void rollIntent() {
        turnCycle++;
        int step = (turnCycle - 1) % 4;

        if (step == 0) {
            this.currentIntent = EnemyIntent.DEFEND;
            this.intentLabel = "Khiên Ma Năng (+18 Giáp, +1 SM)";
            this.intentValue = 18;
        } else if (step == 1) {
            this.currentIntent = EnemyIntent.ATTACK;
            this.intentLabel = "Bão Kiếm Song Trảm (2x8 Sát Thương)";
            this.intentValue = 8;
        } else if (step == 2) {
            this.currentIntent = EnemyIntent.ATTACK;
            this.intentLabel = "Lôi Điện Tê Liệt (15 Sát Thương, Yếu Mềm)";
            this.intentValue = 15;
        } else {
            this.currentIntent = EnemyIntent.ATTACK;
            this.intentLabel = "HỦY DIỆT CỔ VƯƠNG (24 Sát Thương)";
            this.intentValue = 24;
        }
    }

    @Override
    public void executeIntent(Character player) {
        int step = (turnCycle - 1) % 4;
        if (step == 0) {
            applyStatus(StatusEffect.Type.STRENGTH, 1);
            this.block += intentValue;
            triggerEndTurnStatuses();
            return;
        } else if (step == 1) {
            // Song trảm (2 hits)
            int dmg1 = calculateDamageOutput(intentValue);
            player.takeDamage(dmg1);
            int dmg2 = calculateDamageOutput(intentValue);
            player.takeDamage(dmg2);
            triggerEndTurnStatuses();
            return;
        } else if (step == 2) {
            player.applyStatus(StatusEffect.Type.WEAK, 2);
        }
        super.executeIntent(player);
    }
}
