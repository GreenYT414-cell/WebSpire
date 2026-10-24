package com.quoc.webspire.relics;

import com.quoc.webspire.entities.Character;

public class MeatOnTheBoneRelic extends Relic {
    public MeatOnTheBoneRelic() {
        super("Thịt Rừng Cổ Đại", "Khi kết thúc trận chiến, nếu Máu <= 50%, hồi phục ngay 12 Máu.");
    }

    @Override
    public void onVictory(Character player) {
        if (player.getHp() <= player.getMaxHp() / 2) {
            player.heal(12);
        }
    }
}
