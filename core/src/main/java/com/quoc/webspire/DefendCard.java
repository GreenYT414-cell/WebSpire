package com.quoc.webspire;

import com.quoc.webspire.enums.CardType;
import com.quoc.webspire.enums.TargetType;

public class DefendCard extends Card {
    private int block;

    public DefendCard() {
        super(
            "DEFEND_01",
            "Phòng Thủ",
            1,                  // Tốn 1 Energy
            CardType.SKILL,     // Loại bài Kỹ năng
            TargetType.SELF,    // Tự tác dụng lên bản thân
            "Nhận 5 giáp."
        );
        this.block = 5;
    }

    @Override
    public void play(Character user, Character targetEntity) {
        if (user.useEnergy(getCost())) {
            System.out.println(user.getName() + " sử dụng [" + getName() + "]!");
            user.addBlock(this.block);
        }
    }

    public int getBlock() {
        return block;
    }
}
