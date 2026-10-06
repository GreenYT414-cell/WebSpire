package com.quoc.webspire;

public class EnergyRingRelic extends Relic {
    public EnergyRingRelic() {
        super("Nhẫn Năng Lượng", "+1 Năng lượng tối đa mỗi trận đấu.");
    }

    @Override
    public void onCombatStart(Character player, Enemy monster) {
        player.setEnergy(player.getEnergy() + 1);
    }
}
