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

    private int strength = 0;
    private int vulnerableTurns = 0;
    private int metallicize = 0;

    private List<Relic> relics = new ArrayList<>();

    public Character(String name, int maxHp, int maxEnergy) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.maxEnergy = maxEnergy;
        this.energy = maxEnergy;
        this.gold = 100;
    }

    public void startTurn() {
        this.block = 0;
        this.energy = maxEnergy;
        if (vulnerableTurns > 0) {
            vulnerableTurns--;
        }
    }

    public void triggerEndTurnPowers() {
        if (metallicize > 0) {
            addBlock(metallicize);
        }
    }

    public void resetCombatStats() {
        this.strength = 0;
        this.vulnerableTurns = 0;
        this.metallicize = 0;
        this.block = 0;
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

    public void heal(int amount) {
        this.hp += amount;
        if (this.hp > maxHp) this.hp = maxHp;
    }

    public void addGold(int amount) {
        this.gold += amount;
        if (this.gold < 0) this.gold = 0;
    }

    public void addStrength(int amount) {
        this.strength += amount;
    }

    public void addMetallicize(int amount) {
        this.metallicize += amount;
    }

    public void addRelic(Relic relic) {
        relics.add(relic);
    }

    public void triggerCombatStartRelics(Enemy monster) {
        resetCombatStats();
        for (Relic relic : relics) {
            relic.onCombatStart(this, monster);
        }
    }

    public void triggerVictoryRelics() {
        for (Relic relic : relics) {
            relic.onVictory(this);
        }
    }

    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getBlock() { return block; }
    public int getEnergy() { return energy; }
    public int getGold() { return gold; }
    public int getStrength() { return strength; }
    public int getVulnerableTurns() { return vulnerableTurns; }
    public int getMetallicize() { return metallicize; }
    public List<Relic> getRelics() { return relics; }

    public void setEnergy(int energy) { this.energy = energy; }
}
