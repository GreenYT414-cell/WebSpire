package com.quoc.webspire;

public abstract class Relic {
    private String name;
    private String description;

    public Relic(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Sự kiện kích hoạt khi bắt đầu trận đấu mới
    public void onCombatStart(Character player, Enemy monster) {}

    // Sự kiện kích hoạt khi kết thúc trận đấu (chiến thắng)
    public void onVictory(Character player) {}

    public String getName() { return name; }
    public String getDescription() { return description; }
}
