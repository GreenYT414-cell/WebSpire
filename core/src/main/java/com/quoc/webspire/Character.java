package com.quoc.webspire;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Character {
    private String name;
    private int hp;
    private int maxHp;
    private int block;
    private int energy;
    private int maxEnergy;
    private int gold;

    private Map<StatusEffect.Type, Integer> statuses = new HashMap<>();
    private List<Relic> relics = new ArrayList<>();

    public Character(String name, int maxHp, int energy) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.maxEnergy = energy;
        this.energy = energy;
        this.block = 0;
        this.gold = 100;
    }

    public void startTurn() {
        this.energy = maxEnergy;
        this.block = 0; // Giáp bị reset mỗi đầu lượt
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

    public void addBlock(int amount) { this.block += amount; }
    public void heal(int amount) { this.hp = Math.min(this.maxHp, this.hp + amount); }
    public void addGold(int amount) { this.gold += amount; }

    public boolean useEnergy(int amount) {
        if (this.energy >= amount) {
            this.energy -= amount;
            return true;
        }
        return false;
    }

    public void gainEnergy(int amount) { this.energy += amount; }
    public void setEnergy(int energy) { this.energy = energy; }
    public void setMaxEnergy(int maxEnergy) { this.maxEnergy = maxEnergy; }

    public void addRelic(Relic relic) { relics.add(relic); }
    public void triggerCombatStartRelics(Enemy monster) {
        for (Relic r : relics) r.onCombatStart(this, monster);
    }
    public void triggerVictoryRelics() {
        for (Relic r : relics) r.onVictory(this);
    }

    // Getter & Setter
    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getBlock() { return block; }
    public int getEnergy() { return energy; }
    public int getMaxEnergy() { return maxEnergy; }
    public int getGold() { return gold; }
    public Map<StatusEffect.Type, Integer> getStatuses() { return statuses; }
    public List<Relic> getRelics() { return relics; }
    public int getStrength() { return getStatusAmount(StatusEffect.Type.STRENGTH); }
}
