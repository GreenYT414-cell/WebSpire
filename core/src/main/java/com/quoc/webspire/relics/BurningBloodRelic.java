package com.quoc.webspire.relics;

import com.quoc.webspire.entities.Character;

public class BurningBloodRelic extends Relic {
    public BurningBloodRelic() {
        super("Máu Quỷ", "Cuối mỗi trận đấu, hồi lại 6 HP.");
    }

    @Override
    public void onVictory(Character player) {
        player.heal(6);
    }
}
