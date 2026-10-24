package com.quoc.webspire.entities;

import com.quoc.webspire.enums.EnemyIntent;

public class BossEnemy extends Enemy {
    private int turnCycle = 0;
    private boolean isEnraged = false;

    public BossEnemy(String name, int maxHp) {
        super(name, maxHp, 0);
    }

    public boolean isEnraged() {
        return isEnraged;
    }

    @Override
    public void rollIntent() {
        turnCycle++;

        // Kiểm tra ngưỡng máu < 50% để kích hoạt Phase 2 (Enrage)
        if (!isEnraged && this.hp <= this.maxHp / 2) {
            isEnraged = true;
            this.name = "TRÙM CUỐI: Slime Bạo Nộ [PHASE 2]";
            // Khi phân tách vào Phase 2: Tự động kích hoạt khiên hộ thể và tăng sức mạnh
            this.block += 25;
            applyStatus(StatusEffect.Type.STRENGTH, 4);
        }

        if (!isEnraged) {
            // ===== GIAI ĐOẠN 1 (PHASE 1: HP >= 50%) =====
            int step = (turnCycle - 1) % 3;
            if (step == 0) {
                this.currentIntent = EnemyIntent.ATTACK;
                this.intentLabel = "Ăn Mòn Dịch Thể (12 ST, 2 Yếu)";
                this.intentValue = 12;
            } else if (step == 1) {
                this.currentIntent = EnemyIntent.DEFEND;
                this.intentLabel = "Tích Tụ Nhựa Sống (+18 Giáp, +2 SM)";
                this.intentValue = 18;
            } else {
                this.currentIntent = EnemyIntent.ATTACK;
                this.intentLabel = "Cú Đè Nặng Nề (22 ST, 2 Dễ Tổn Thương)";
                this.intentValue = 22;
            }
        } else {
            // ===== GIAI ĐOẠN 2 (PHASE 2: ENRAGE / BẠO NỘ KHI HP < 50%) =====
            int step = (turnCycle - 1) % 3;
            if (step == 0) {
                this.currentIntent = EnemyIntent.ATTACK;
                this.intentLabel = "Phân Tách Dịch Độc (18 ST, 4 Độc)";
                this.intentValue = 18;
            } else if (step == 1) {
                this.currentIntent = EnemyIntent.ATTACK;
                this.intentLabel = "Axit Phún Trào (24 ST, +3 SM)";
                this.intentValue = 24;
            } else {
                this.currentIntent = EnemyIntent.ATTACK;
                this.intentLabel = "ĐẠI THẢM HỌA SLIME (35 ST Tối Thượng)";
                this.intentValue = 35;
            }
        }
    }

    @Override
    public void executeIntent(Character player) {
        if (!isEnraged) {
            int step = (turnCycle - 1) % 3;
            if (step == 0) {
                player.addStatus(StatusEffect.Type.WEAK, 2);
            } else if (step == 1) {
                applyStatus(StatusEffect.Type.STRENGTH, 2);
            } else {
                player.addStatus(StatusEffect.Type.VULNERABLE, 2);
            }
        } else {
            int step = (turnCycle - 1) % 3;
            if (step == 0) {
                player.addStatus(StatusEffect.Type.POISON, 4);
            } else if (step == 1) {
                applyStatus(StatusEffect.Type.STRENGTH, 3);
            }
        }
        super.executeIntent(player);
    }
}
