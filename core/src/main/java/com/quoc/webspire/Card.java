package com.quoc.webspire;

import com.quoc.webspire.enums.CardType;

public abstract class Card {
    private String name;
    private int cost;
    private CardType type;
    private String description;

    public Card(String name, int cost, CardType type, String description) {
        this.name = name;
        this.cost = cost;
        this.type = type;
        this.description = description;
    }

    public abstract void play(Character player, Enemy monster);

    public String getName() { return name; }
    public int getCost() { return cost; }
    public CardType getType() { return type; }
    public String getDescription() { return description; }
}
