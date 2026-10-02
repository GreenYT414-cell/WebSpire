package com.quoc.webspire;

import java.util.HashMap;
import java.util.Map;

public class Character {
    private String name;
    private int hp;
    private int maxHp;
    private int block;
    private int energy;
    private int maxEnergy;

    // Lưu trữ các buff/debuff
    private Map<String, Integer> statuses;

    public Character(String name, int maxHp, int maxEnergy) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.maxEnergy = maxEnergy;
        this.energy = maxEnergy;
        this.block = 0;
        this.statuses = new HashMap<>();
    }

    public void startTurn() {
        this.block = 0; // Xóa giáp cũ khi bắt đầu hiệp
        this.energy = this.maxEnergy; // Hồi đầy năng lượng
        System.out.println("--- Lượt của " + name + " bắt đầu ---");
    }

    public void takeDamage(int damage) {
        if (block >= damage) {
            block -= damage;
            System.out.println(name + " chặn được toàn bộ sát thương. Giáp còn: " + block);
        } else {
            int remainingDamage = damage - block;
            block = 0;
            hp -= remainingDamage;
            if (hp < 0) hp = 0;
            System.out.println(name + " mất " + remainingDamage + " HP. Máu còn: " + hp);
        }
    }

    public void addBlock(int amount) {
        this.block += amount;
        System.out.println(name + " nhận " + amount + " Giáp. Tổng giáp: " + block);
    }

    public boolean useEnergy(int cost) {
        if (this.energy >= cost) {
            this.energy -= cost;
            return true;
        }
        System.out.println(name + " không đủ Năng lượng!");
        return false;
    }

    // Getters
    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getBlock() { return block; }
    public int getEnergy() { return energy; }
}
