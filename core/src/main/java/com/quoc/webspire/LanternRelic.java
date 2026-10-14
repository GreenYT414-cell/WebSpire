package com.quoc.webspire;

public class LanternRelic extends Relic {
    public LanternRelic() {
        super("Đèn Lồng", "Nhận thêm +1 Năng lượng ở đầu mỗi trận đấu.");
    }

    @Override
    public void onCombatStart(Character player, Enemy monster) {
        player.gainEnergy(1);
    }
}
