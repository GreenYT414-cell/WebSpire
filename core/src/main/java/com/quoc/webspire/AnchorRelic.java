package com.quoc.webspire;

public class AnchorRelic extends Relic {
    public AnchorRelic() {
        super("Mỏ Neo", "Bắt đầu mỗi trận đấu với 10 Giáp.");
    }

    @Override
    public void onCombatStart(Character player, Enemy monster) {
        player.addBlock(10);
    }
}
