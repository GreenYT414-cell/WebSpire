package com.quoc.webspire;

public abstract class Relic {
    private String name;
    private String description;

    public Relic(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void onCombatStart(Character player, Enemy monster) {}
    public void onVictory(Character player) {}

    public String getName() { return name; }
    public String getDescription() { return description; }
}
