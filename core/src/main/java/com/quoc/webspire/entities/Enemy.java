package com.quoc.webspire.entities;

import com.quoc.webspire.enums.EnemyIntent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Enemy {
    protected String name;
    protected int hp;
    protected int maxHp;
    protected int block;

    // Fix: dùng enum EnemyIntent thay vì String để tránh lỗi so sánh typo
    protected EnemyIntent currentIntent;
    protected String intentLabel;   // Chuỗi hiển thị cho người dùng (tiếng Việt)
    protected int intentValue;
    protected Random random = new Random();

    protected Map<StatusEffect.Type, Integer> statuses = new HashMap<>();

    public Enemy(String name, int maxHp, int block) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.block = block;
    }

    public void startTurn() {
        this.block = 0;
    }

    public void rollIntent() {
        int roll = random.nextInt(100);
        if (roll < 65) {
            currentIntent = EnemyIntent.ATTACK;
            intentLabel = "Tấn công";
            intentValue = 6 + random.nextInt(5);
        } else {
            currentIntent = EnemyIntent.DEFEND;
            intentLabel = "Phòng thủ";
            intentValue = 8 + random.nextInt(4);
        }
    }

    public void executeIntent(Character player) {
        if (currentIntent == EnemyIntent.ATTACK) {
            int dmg = calculateDamageOutput(intentValue);
            player.takeDamage(dmg);
        } else if (currentIntent == EnemyIntent.DEFEND) {
            this.block += intentValue;
        }
        triggerEndTurnStatuses();
    }

    public void applyStatus(StatusEffect.Type type, int amount) {
        statuses.put(type, statuses.getOrDefault(type, 0) + amount);
    }

    public int getStatusAmount(StatusEffect.Type type) {
        return statuses.getOrDefault(type, 0);
    }

    public int calculateDamageOutput(int baseDamage) {
        int dmg = baseDamage + getStatusAmount(StatusEffect.Type.STRENGTH);
        if (getStatusAmount(StatusEffect.Type.WEAK) > 0) {
            dmg = (int) (dmg * 0.75f);
        }
        return Math.max(0, dmg);
    }

    public void takeDamage(int rawDamage) {
        int finalDamage = rawDamage;
        if (getStatusAmount(StatusEffect.Type.VULNERABLE) > 0) {
            finalDamage = (int) (rawDamage * 1.5f);
        }

        if (this.block >= finalDamage) {
            this.block -= finalDamage;
        } else {
            int remaining = finalDamage - this.block;
            this.block = 0;
            int prevHp = this.hp;
            this.hp = Math.max(0, this.hp - remaining);
            com.quoc.webspire.managers.RunState.getInstance().addDamageDealt(prevHp - this.hp);
        }
    }

    public void triggerEndTurnStatuses() {
        int poison = getStatusAmount(StatusEffect.Type.POISON);
        if (poison > 0) {
            this.hp = Math.max(0, this.hp - poison);
            statuses.put(StatusEffect.Type.POISON, poison - 1);
        }

        int vuln = getStatusAmount(StatusEffect.Type.VULNERABLE);
        if (vuln > 0) statuses.put(StatusEffect.Type.VULNERABLE, vuln - 1);

        int weak = getStatusAmount(StatusEffect.Type.WEAK);
        if (weak > 0) statuses.put(StatusEffect.Type.WEAK, weak - 1);
    }

    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getBlock() { return block; }
    /** Trả về label tiếng Việt để hiển thị intent cho người dùng */
    public String getCurrentIntent() { return intentLabel; }
    public EnemyIntent getIntentType() { return currentIntent; }
    public int getIntentValue() { return intentValue; }
    public Map<StatusEffect.Type, Integer> getStatuses() { return statuses; }
}
