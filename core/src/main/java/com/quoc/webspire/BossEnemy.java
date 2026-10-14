package com.quoc.webspire;

public class BossEnemy extends Enemy {
    private int turnCycle = 0;

    public BossEnemy(String name, int maxHp) {
        super(name, maxHp, 0);
    }

    @Override
    public void rollIntent() {
        turnCycle++;
        int step = (turnCycle - 1) % 3;

        if (step == 0) {
            this.currentIntent = "Tấn Công Vũ Bão";
            this.intentValue = 15;
        } else if (step == 1) {
            this.currentIntent = "Gồng Giáp Tích Lực";
            this.intentValue = 12;
        } else {
            this.currentIntent = "Dồn Hủy Diệt";
            this.intentValue = 25;
        }
    }
}
