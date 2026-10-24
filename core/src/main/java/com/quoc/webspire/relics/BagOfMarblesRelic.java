package com.quoc.webspire.relics;

import com.quoc.webspire.entities.Character;
import com.quoc.webspire.entities.Enemy;
import com.quoc.webspire.entities.StatusEffect;

public class BagOfMarblesRelic extends Relic {
    public BagOfMarblesRelic() {
        super("Túi Bi Thủy Tinh", "Bắt đầu mỗi trận đấu, khiến kẻ địch chịu 1 Dễ Tổn Thương.");
    }

    @Override
    public void onCombatStart(Character player, Enemy enemy) {
        if (enemy != null) {
            enemy.applyStatus(StatusEffect.Type.VULNERABLE, 1);
        }
    }
}
