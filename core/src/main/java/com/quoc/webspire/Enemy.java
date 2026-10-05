package com.quoc.webspire;

import com.quoc.webspire.enums.EnemyIntent;
import java.util.Random;

public class Enemy {
    private String name;
    private int hp;
    private int maxHp;
    private int block;

    private EnemyIntent currentIntent;
    private int intentValue;
    private Random random = new Random();

    private int strength = 0;
    private int vulnerableTurns = 0;
    private int weakTurns = 0;

    public Enemy(String name, int hp, int block) {
        this.name = name;
        this.hp = hp;
        this.maxHp = hp;
        this.block = block;
    }

    public void rollIntent() {
        int roll = random.nextInt(100);
        if (roll < 60) {
            currentIntent = EnemyIntent.ATTACK;
            intentValue = 6;
        } else {
            currentIntent = EnemyIntent.DEFEND;
            intentValue = 5;
        }
    }

    public void startTurn() {
        this.block = 0;
        if (vulnerableTurns > 0) vulnerableTurns--;
        if (weakTurns > 0) weakTurns--;
    }

    public void executeIntent(Character player) {
        if (currentIntent == EnemyIntent.ATTACK) {
            int finalDamage = intentValue + strength;
            if (weakTurns > 0) finalDamage = (int)(finalDamage * 0.75f);
            if (player.getVulnerableTurns() > 0) finalDamage = (int)(finalDamage * 1.5f);

            player.takeDamage(finalDamage);
        } else if (currentIntent == EnemyIntent.DEFEND) {
            this.block += intentValue;
        }
    }

    public void takeDamage(int damage) {
        if (damage <= 0) return;
        if (block > 0) {
            if (block >= damage) {
                block -= damage;
                return;
            } else {
                damage -= block;
                block = 0;
            }
        }
        hp -= damage;
        if (hp < 0) hp = 0;
    }

    public void applyVulnerable(int turns) { vulnerableTurns += turns; }
    public void applyWeak(int turns) { weakTurns += turns; }

    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getBlock() { return block; }
    public EnemyIntent getCurrentIntent() { return currentIntent; }
    public int getIntentValue() { return intentValue; }
    public int getVulnerableTurns() { return vulnerableTurns; }
    public int getWeakTurns() { return weakTurns; }
}
