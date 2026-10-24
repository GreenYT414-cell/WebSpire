package com.quoc.webspire.relics;

import com.quoc.webspire.entities.Character;
import com.quoc.webspire.entities.Enemy;
import com.quoc.webspire.entities.StatusEffect;

public class VajraRelic extends Relic {
    public VajraRelic() {
        super("Kim Cương Chùy", "Bắt đầu mỗi trận đấu với +1 Sức Mạnh.");
    }

    @Override
    public void onCombatStart(Character player, Enemy monster) {
        player.addStatus(StatusEffect.Type.STRENGTH, 1);
    }
}
