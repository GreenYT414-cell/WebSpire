package com.quoc.webspire;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Enemy {
    protected String name;
    protected int hp;
    protected int maxHp;
    protected int block;

    protected String currentIntent;
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
            currentIntent = "Tấn công";
            intentValue = 6 + random.nextInt(5);
        } else {
            currentIntent = "Phòng thủ";
            intentValue = 8 + random.nextInt(4);
        }
    }

    public void executeIntent(Character player) {
        if ("Tấn công".equals(currentIntent) || "Tấn Công Vũ Bão".equals(currentIntent) || "Dồn Hủy Diệt".equals(currentIntent) || "Chém Bóng Đêm".equals(currentIntent)) {
            int dmg = calculateDamageOutput(intentValue);
            player.takeDamage(dmg);
        } else if ("Phòng thủ".equals(currentIntent) || "Gồng Giáp Tích Lực".equals(currentIntent) || "Khiên Bóng Tối".equals(currentIntent)) {
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
            this.hp -= remaining;
            if (this.hp < 0) this.hp = 0;
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
    public String getCurrentIntent() { return currentIntent; }
    public int getIntentValue() { return intentValue; }
    public Map<StatusEffect.Type, Integer> getStatuses() { return statuses; }
}
