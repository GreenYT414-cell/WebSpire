package com.quoc.webspire;

import com.quoc.webspire.enums.CardType;
import com.quoc.webspire.enums.TargetType;

public class StrikeCard extends Card {
    private int damage;

    public StrikeCard() {
        super(
            "STRIKE_01",
            "Chém",
            1,                  // Tốn 1 Energy
            CardType.ATTACK,    // Loại bài Tấn Công
            TargetType.ENEMY,   // Nhắm vào 1 kẻ thù
            "Gây 6 sát thương."
        );
        this.damage = 6;
    }

    @Override
    public void play(Character user, Character targetEntity) {
        if (targetEntity == null) {
            System.out.println("Không thể dùng [" + getName() + "] vì không có mục tiêu!");
            return;
        }

        // Kiểm tra đủ Năng lượng không trước khi đánh
        if (user.useEnergy(getCost())) {
            System.out.println(user.getName() + " sử dụng [" + getName() + "]!");
            targetEntity.takeDamage(this.damage);
        }
    }

    public int getDamage() {
        return damage;
    }
}
