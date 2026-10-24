package com.quoc.webspire.relics;

import com.quoc.webspire.entities.Character;
import com.quoc.webspire.entities.Enemy;

public class Relic {
    private String name;
    private String description;

    public Relic(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Lắng nghe sự kiện khi vừa sở hữu / trang bị Cổ Vật
    public void onEquip(Character player) {}

    // Lắng nghe sự kiện khi bắt đầu trận chiến
    public void onCombatStart(Character player, Enemy enemy) {}

    // Lắng nghe sự kiện khi chiến thắng trận đấu
    public void onVictory(Character player) {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
