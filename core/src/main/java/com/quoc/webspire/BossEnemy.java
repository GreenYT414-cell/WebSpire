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
            this.currentIntent = "ĐÒN HỦY DIỆT";
            this.intentValue = 25;
        }
    }

    @Override
    public void executeIntent(Character player) {
        if ("Tấn Công Vũ Bão".equals(getCurrentIntent())) {
            player.takeDamage(getIntentValue());
        } else if ("Gồng Giáp Tích Lực".equals(getCurrentIntent())) {
            this.block += getIntentValue();
        } else if ("ĐÒN HỦY DIỆT".equals(getCurrentIntent())) {
            player.takeDamage(getIntentValue());
        }
    }
}
