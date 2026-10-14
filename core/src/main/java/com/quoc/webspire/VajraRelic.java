package com.quoc.webspire;

public class VajraRelic extends Relic {
    public VajraRelic() {
        super("Kim Cương Chùy", "Bắt đầu mỗi trận đấu với +1 Sức Mạnh.");
    }

    @Override
    public void onCombatStart(Character player, Enemy monster) {
        player.applyStatus(StatusEffect.Type.STRENGTH, 1);
    }
}
