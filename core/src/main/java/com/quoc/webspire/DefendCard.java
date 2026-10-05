package com.quoc.webspire;

import com.quoc.webspire.enums.CardType;

public class DefendCard extends Card {
    private int block;

    public DefendCard() {
        super("Phòng Thủ", 1, CardType.SKILL, "Nhận 5 giáp.");
        this.block = 5;
    }

    @Override
    public void play(Character player, Enemy monster) {
        player.addBlock(this.block);
    }
}
