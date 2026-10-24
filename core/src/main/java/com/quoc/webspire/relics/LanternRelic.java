package com.quoc.webspire.relics;

import com.quoc.webspire.entities.Character;
import com.quoc.webspire.entities.Enemy;

public class LanternRelic extends Relic {
    public LanternRelic() {
        super("Lồng Đèn", "Bắt đầu mỗi trận đấu, nhận thêm +1 Năng Lượng.");
    }

    @Override
    public void onCombatStart(Character player, Enemy enemy) {
        player.setMaxEnergy(player.getMaxEnergy() + 1);
        player.setEnergy(player.getEnergy() + 1);
    }
}
