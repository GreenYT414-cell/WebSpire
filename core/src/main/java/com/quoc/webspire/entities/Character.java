package com.quoc.webspire.entities;

import com.quoc.webspire.relics.Relic;
import com.quoc.webspire.potions.Potion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Character {
    private String name;
    private int hp;
    private int maxHp;
    private int block = 0;
    private int energy = 3;
    private int maxEnergy = 3;
    private int gold = 100;

    private Map<StatusEffect.Type, Integer> statuses = new HashMap<>();
    private List<Relic> relics = new ArrayList<>();
    private List<Potion> potions = new ArrayList<>();

    // Constructor 2 tham số
    public Character(String name, int maxHp) {
        this(name, maxHp, 3);
    }

    // Constructor 3 tham số (để sửa lỗi ở RunState)
    public Character(String name, int maxHp, int maxEnergy) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.maxEnergy = maxEnergy;
        this.energy = maxEnergy;
    }

    /**
     * Đặt lại toàn bộ trạng thái chiến đấu (Giáp, Năng lượng, Buff Sức mạnh, Demon Form, Debuff...)
     * Đảm bảo các hiệu ứng chỉ có tác dụng trong 1 trận đấu và không bị rò rỉ sang màn mới.
     */
    public void resetCombatState() {
        this.block = 0;
        this.energy = maxEnergy;
        this.statuses.clear();
    }

    public void startTurn() {
        this.block = 0;
        this.energy = maxEnergy;
        int demonForm = getStatus(StatusEffect.Type.DEMON_FORM);
        if (demonForm > 0) {
            addStatus(StatusEffect.Type.STRENGTH, demonForm);
        }
    }

    public void triggerEndTurnStatuses() {
        int metallicizeValue = getStatus(StatusEffect.Type.METALLICIZE);
        if (metallicizeValue > 0) {
            addBlock(metallicizeValue);
        }

        int poisonValue = getStatus(StatusEffect.Type.POISON);
        if (poisonValue > 0) {
            takeDamage(poisonValue);
            applyStatus(StatusEffect.Type.POISON, poisonValue - 1);
        }

        int weakValue = getStatus(StatusEffect.Type.WEAK);
        if (weakValue > 0) applyStatus(StatusEffect.Type.WEAK, weakValue - 1);

        int vulnValue = getStatus(StatusEffect.Type.VULNERABLE);
        if (vulnValue > 0) applyStatus(StatusEffect.Type.VULNERABLE, vulnValue - 1);
    }

    public boolean useEnergy(int amount) {
        if (energy >= amount) {
            energy -= amount;
            return true;
        }
        return false;
    }

    public void addEnergy(int amount) {
        if (amount > 0) this.energy += amount;
    }

    public List<Potion> getPotions() {
        return potions;
    }

    public boolean addPotion(Potion potion) {
        if (potions.size() < 3) {
            potions.add(potion);
            return true;
        }
        return false;
    }

    public void removePotion(Potion potion) {
        potions.remove(potion);
    }

    public void takeDamage(int damage) {
        if (damage <= 0) return;
        int finalDamage = damage;
        if (getStatus(StatusEffect.Type.VULNERABLE) > 0) {
            finalDamage = (int) (damage * 1.5f);
        }
        if (block >= finalDamage) {
            block -= finalDamage;
        } else {
            int remaining = finalDamage - block;
            block = 0;
            int prevHp = hp;
            hp = Math.max(0, hp - remaining);
            com.quoc.webspire.managers.RunState.getInstance().addDamageTaken(prevHp - hp);
        }
    }

    public void addBlock(int amount) {
        if (amount > 0) this.block += amount;
    }

    public void heal(int amount) {
        if (amount > 0) this.hp = Math.min(maxHp, hp + amount);
    }

    public void addGold(int amount) {
        this.gold = Math.max(0, this.gold + amount);
        if (amount > 0) {
            com.quoc.webspire.managers.RunState.getInstance().addGoldEarned(amount);
        }
    }

    public void applyStatus(StatusEffect.Type type, int amount) {
        statuses.put(type, Math.max(0, amount));
    }

    public void addStatus(StatusEffect.Type type, int amount) {
        statuses.put(type, getStatus(type) + amount);
    }

    public int getStatus(StatusEffect.Type type) {
        return statuses.getOrDefault(type, 0);
    }

    public int calculateDamageOutput(int baseDamage) {
        int damage = baseDamage + getStatus(StatusEffect.Type.STRENGTH);
        if (getStatus(StatusEffect.Type.WEAK) > 0) {
            damage = (int) (damage * 0.75f);
        }
        return Math.max(0, damage);
    }

    public void triggerCombatStartRelics(Enemy monster) {
        for (Relic relic : relics) {
            relic.onCombatStart(this, monster);
        }
    }

    public void triggerVictoryRelics() {
        for (Relic relic : relics) {
            relic.onVictory(this);
        }
    }

    public void addRelic(Relic relic) {
        relics.add(relic);
        relic.onEquip(this);
    }

    public String getName() { return name; }
    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = hp; }
    public int getMaxHp() { return maxHp; }
    public void setMaxHp(int maxHp) { this.maxHp = maxHp; }
    public int getBlock() { return block; }
    public int getEnergy() { return energy; }
    public void setEnergy(int energy) { this.energy = energy; }
    public int getMaxEnergy() { return maxEnergy; }
    public void setMaxEnergy(int maxEnergy) { this.maxEnergy = maxEnergy; }
    public int getGold() { return gold; }
    public Map<StatusEffect.Type, Integer> getStatuses() { return statuses; }
    public List<Relic> getRelics() { return relics; }
}
