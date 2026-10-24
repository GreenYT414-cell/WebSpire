package com.quoc.webspire.relics;

import com.quoc.webspire.entities.Character;
import com.quoc.webspire.entities.Enemy;

public class AnchorRelic extends Relic {
    public AnchorRelic() {
        super("Mỏ Neo", "Bắt đầu mỗi trận đấu với 10 Giáp.");
    }

    @Override
    public void onCombatStart(Character player, Enemy monster) {
        player.addBlock(10);
    }
}
