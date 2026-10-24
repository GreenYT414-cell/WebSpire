package com.quoc.webspire.cards;

import com.quoc.webspire.entities.Character;
import com.quoc.webspire.entities.Enemy;

public abstract class Card {
    public enum Type { ATTACK, DEFENSE, SKILL, POWER }

    private String name;
    private int cost;
    private Type type;
    private String description;
    private boolean upgraded = false;

    public Card(String name, int cost, Type type, String description) {
        this.name = name;
        this.cost = cost;
        this.type = type;
        this.description = description;
    }

    // Phương thức kích hoạt hiệu ứng bài (Trả về true nếu bài sử dụng thành công)
    public abstract boolean use(Character player, Enemy enemy);

    // Mặc định gọi use(player, enemy), các lá bài có chức năng rút bài/tác động deck có thể override
    public boolean use(Character player, Enemy enemy, com.quoc.webspire.managers.DeckManager deckManager) {
        return use(player, enemy);
    }

    public abstract void upgrade();

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getCost() { return cost; }
    public void setCost(int cost) { this.cost = cost; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isUpgraded() { return upgraded; }
    public void setUpgraded(boolean upgraded) { this.upgraded = upgraded; }
}
