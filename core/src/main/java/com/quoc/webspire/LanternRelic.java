package com.quoc.webspire;

public class LanternRelic extends Relic {
    public LanternRelic() {
        super("Đèn Bão", "Nhận thêm +1 Năng lượng khi bắt đầu trận đấu.");
    }

    @Override
    public void onCombatStart(Character player, Enemy monster) {
        player.setEnergy(player.getEnergy() + 1);
    }
}
