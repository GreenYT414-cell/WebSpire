package com.quoc.webspire;

public class DarkKnightEnemy extends Enemy {
    private int turnCount = 0;

    public DarkKnightEnemy(String name, int maxHp) {
        super(name, maxHp, 10);
    }

    @Override
    public void rollIntent() {
        turnCount++;
        if (turnCount % 2 == 1) {
            this.currentIntent = "Chém Bóng Tối";
            this.intentValue = 18;
        } else {
            this.currentIntent = "Khiên Hắc Khí";
            this.intentValue = 15;
        }
    }

    @Override
    public void executeIntent(Character player) {
        if ("Chém Bóng Tối".equals(getCurrentIntent())) {
            player.takeDamage(getIntentValue());
        } else if ("Khiên Hắc Khí".equals(getCurrentIntent())) {
            this.block += getIntentValue();
        }
    }
}
