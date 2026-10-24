package com.quoc.webspire.relics;

import com.quoc.webspire.entities.Character;

public class StrawberryRelic extends Relic {
    public StrawberryRelic() {
        super("Dâu Rừng Ngọt", "Khi nhặt: Tăng vĩnh viễn +7 Máu Tối Đa và hồi phục 7 Máu.");
    }

    @Override
    public void onEquip(Character player) {
        player.setMaxHp(player.getMaxHp() + 7);
        player.heal(7);
    }
}
