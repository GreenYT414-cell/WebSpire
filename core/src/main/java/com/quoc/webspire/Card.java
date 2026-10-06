package com.quoc.webspire;

public abstract class Card {
    private String name;
    private int cost;
    private int damage;
    private int block;
    private String description;
    private boolean upgraded = false;
    private boolean isPower = false;

    public Card(String name, int cost, int damage, int block, String description) {
        this.name = name;
        this.cost = cost;
        this.damage = damage;
        this.block = block;
        this.description = description;
    }

    public abstract void use(Character player, Enemy monster);
    public abstract void upgrade();

    public String getName() { return name; }
    public int getCost() { return cost; }
    public int getDamage() { return damage; }
    public int getBlock() { return block; }
    public String getDescription() { return description; }
    public boolean isUpgraded() { return upgraded; }
    public boolean isPower() { return isPower; }

    public void setName(String name) { this.name = name; }
    public void setCost(int cost) { this.cost = cost; }
    public void setDamage(int damage) { this.damage = damage; }
    public void setBlock(int block) { this.block = block; }
    public void setDescription(String description) { this.description = description; }
    public void setUpgraded(boolean upgraded) { this.upgraded = upgraded; }
    public void setPower(boolean isPower) { this.isPower = isPower; }
}
