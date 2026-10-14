package com.quoc.webspire;

public class DarkKnightEnemy extends Enemy {
    private int turnCycle = 0;

    public DarkKnightEnemy(String name, int maxHp) {
        super(name, maxHp, 0);
    }

    @Override
    public void rollIntent() {
        turnCycle++;
        if (turnCycle % 2 == 1) {
            this.currentIntent = "Chém Bóng Đêm";
            this.intentValue = 10;
        } else {
            this.currentIntent = "Khiên Bóng Tối";
            this.intentValue = 10;
        }
    }
}
