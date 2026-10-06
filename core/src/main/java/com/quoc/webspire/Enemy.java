package com.quoc.webspire;

public class Enemy {
    protected String name;
    protected int hp;
    protected int maxHp;
    protected int block;
    protected String currentIntent;
    protected int intentValue;
    protected int vulnerableTurns = 0;

    public Enemy(String name, int maxHp, int block) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.block = block;
    }

    public void rollIntent() {
        this.currentIntent = "Tấn công";
        this.intentValue = 6;
    }

    public void executeIntent(Character player) {
        if ("Tấn công".equals(currentIntent)) {
            player.takeDamage(intentValue);
        }
    }

    public void startTurn() {
        this.block = 0;
        if (vulnerableTurns > 0) {
            vulnerableTurns--;
        }
    }

    public void takeDamage(int damage) {
        if (vulnerableTurns > 0) {
            damage = (int) (damage * 1.5f);
        }
        if (block >= damage) {
            block -= damage;
        } else {
            int unblockedDamage = damage - block;
            block = 0;
            hp -= unblockedDamage;
            if (hp < 0) hp = 0;
        }
    }

    public void addBlock(int amount) {
        this.block += amount;
    }

    public void applyVulnerable(int turns) {
        this.vulnerableTurns += turns;
    }

    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getBlock() { return block; }
    public String getCurrentIntent() { return currentIntent; }
    public int getIntentValue() { return intentValue; }
    public int getVulnerableTurns() { return vulnerableTurns; }

    public void setCurrentIntent(String currentIntent) { this.currentIntent = currentIntent; }
    public void setIntentValue(int intentValue) { this.intentValue = intentValue; }
}
