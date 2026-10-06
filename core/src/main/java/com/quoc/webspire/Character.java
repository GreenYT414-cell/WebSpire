package com.quoc.webspire;

import java.util.ArrayList;
import java.util.List;

public class Character {
    private String name;
    private int hp;
    private int maxHp;
    private int block;
    private int energy;
    private int maxEnergy;

    private int gold;
    private List<Relic> relics = new ArrayList<>();

    // Trạng thái (Status Effects)
    private int strength = 0;
    private int vulnerableTurns = 0;
    private int weakTurns = 0;

    public Character(String name, int hp, int maxEnergy) {
        this.name = name;
        this.hp = hp;
        this.maxHp = hp;
        this.block = 0;
        this.maxEnergy = maxEnergy;
        this.energy = maxEnergy;
        this.gold = 99;
    }

    public void startTurn() {
        this.energy = maxEnergy;
        this.block = 0;

        if (vulnerableTurns > 0) vulnerableTurns--;
        if (weakTurns > 0) weakTurns--;
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

    public void heal(int amount) {
        if (amount <= 0) return;
        this.hp += amount;
        if (this.hp > maxHp) this.hp = maxHp;
    }

    public void addBlock(int amount) {
        this.block += amount;
    }

    public void addRelic(Relic relic) {
        this.relics.add(relic);
    }

    public void triggerCombatStartRelics(Enemy monster) {
        this.strength = 0; // Reset strength cơ bản trước trận
        for (Relic relic : relics) {
            relic.onCombatStart(this, monster);
        }
    }

    public void triggerVictoryRelics() {
        for (Relic relic : relics) {
            relic.onVictory(this);
        }
    }
    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public void applyVulnerable(int turns) { vulnerableTurns += turns; }
    public void applyWeak(int turns) { weakTurns += turns; }
    public void addStrength(int amount) { strength += amount; }
    public void addGold(int amount) { this.gold += amount; }

    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getBlock() { return block; }
    public int getEnergy() { return energy; }
    public void useEnergy(int amount) { energy -= amount; }
    public int getGold() { return gold; }
    public int getStrength() { return strength; }
    public int getVulnerableTurns() { return vulnerableTurns; }
    public int getWeakTurns() { return weakTurns; }
    public List<Relic> getRelics() { return relics; }
}
