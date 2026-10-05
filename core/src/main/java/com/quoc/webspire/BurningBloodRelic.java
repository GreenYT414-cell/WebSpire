package com.quoc.webspire;

public class BurningBloodRelic extends Relic {
    public BurningBloodRelic() {
        super("Máu Quỷ", "Hồi 6 HP khi kết thúc trận đấu.");
    }

    @Override
    public void onVictory(Character player) {
        player.heal(6);
    }
}
