package com.quoc.webspire;

import com.quoc.webspire.enums.CardType;

public abstract class Card {
    private String name;
    private int cost;
    private CardType type;
    private String description;
    private boolean upgraded = false;

    public Card(String name, int cost, CardType type, String description) {
        this.name = name;
        this.cost = cost;
        this.type = type;
        this.description = description;
    }

    public abstract void play(Character player, Enemy monster);
    public abstract void upgrade();

    public String getName() { return upgraded ? name + "+" : name; }
    public void setName(String name) { this.name = name; }
    public int getCost() { return cost; }
    public void setCost(int cost) { this.cost = cost; }
    public CardType getType() { return type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isUpgraded() { return upgraded; }
    public void setUpgraded(boolean upgraded) { this.upgraded = upgraded; }
}
