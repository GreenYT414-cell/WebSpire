package com.quoc.webspire.relics;

import com.quoc.webspire.entities.Character;
import com.quoc.webspire.entities.Enemy;
import com.quoc.webspire.entities.StatusEffect;

public class SmoothStoneRelic extends Relic {
    public SmoothStoneRelic() {
        super("Đá Mịn Trơn", "Bắt đầu mỗi trận đấu, nhận +1 Kim Loại Hóa (+1 Giáp cuối mỗi lượt).");
    }

    @Override
    public void onCombatStart(Character player, Enemy enemy) {
        player.addStatus(StatusEffect.Type.METALLICIZE, 1);
    }
}
