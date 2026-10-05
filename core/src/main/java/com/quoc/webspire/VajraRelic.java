package com.quoc.webspire;

public class VajraRelic extends Relic {
    public VajraRelic() {
        super("Kim Cang Chùy", "Bắt đầu mỗi trận đấu với +1 Sức mạnh.");
    }

    @Override
    public void onCombatStart(Character player, Enemy monster) {
        player.addStrength(1);
    }
}
