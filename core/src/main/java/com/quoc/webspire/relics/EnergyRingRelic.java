package com.quoc.webspire.relics;

import com.quoc.webspire.entities.Character;
import com.quoc.webspire.entities.Enemy;

public class EnergyRingRelic extends Relic {
    public EnergyRingRelic() {
        super("Nhẫn Năng Lượng", "+1 Năng lượng tối đa mỗi trận đấu.");
    }

    @Override
    public void onCombatStart(Character player, Enemy monster) {
        player.setMaxEnergy(player.getMaxEnergy() + 1);
        player.setEnergy(player.getEnergy() + 1);
    }
}
