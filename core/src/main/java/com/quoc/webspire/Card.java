package com.quoc.webspire;

import com.quoc.webspire.enums.CardType;
import com.quoc.webspire.enums.TargetType;

public abstract class Card {
    private String id;
    private String name;
    private int cost;
    private CardType type;
    private TargetType target;
    private String description;

    public Card(String id, String name, int cost, CardType type, TargetType target, String description) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.type = type;
        this.target = target;
        this.description = description;
    }

    // Phương thức bắt buộc mọi lá bài phải tự định nghĩa
    public abstract void play(Character user, Character targetEntity);

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public int getCost() { return cost; }
    public CardType getType() { return type; }
    public TargetType getTarget() { return target; }
    public String getDescription() { return description; }
}
