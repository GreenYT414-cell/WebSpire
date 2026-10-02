package com.quoc.webspire;

import com.quoc.webspire.enums.EnemyIntent;

public class Enemy extends Character {
    private EnemyIntent currentIntent;
    private int intentValue; // Lưu lượng sát thương hoặc lượng giáp chuẩn bị nhận

    public Enemy(String name, int maxHp, int maxEnergy) {
        super(name, maxHp, maxEnergy);
    }

    // Hàm tạo ý định (Gọi vào đầu mỗi lượt mới)
    public void rollIntent() {
        double random = Math.random();
        if (random > 0.4) {
            // 60% tỷ lệ Tấn công
            this.currentIntent = EnemyIntent.ATTACK;
            this.intentValue = 8; // Gây 8 sát thương
            System.out.println("-> [Ý ĐỊNH]: " + getName() + " lườm bạn, chuẩn bị TẤN CÔNG (" + intentValue + " dmg).");
        } else {
            // 40% tỷ lệ Phòng thủ
            this.currentIntent = EnemyIntent.DEFEND;
            this.intentValue = 5; // Tăng 5 giáp
            System.out.println("-> [Ý ĐỊNH]: " + getName() + " co cụm lại, chuẩn bị PHÒNG THỦ (" + intentValue + " block).");
        }
    }

    // Hàm thực thi ý định (Gọi khi đến lượt của Kẻ thù)
    public void executeIntent(Character player) {
        System.out.println("\n--- Lượt của " + getName() + " bắt đầu ---");

        if (currentIntent == EnemyIntent.ATTACK) {
            System.out.println(getName() + " lao tới chém " + player.getName() + " gây " + intentValue + " sát thương!");
            player.takeDamage(intentValue);
        }
        else if (currentIntent == EnemyIntent.DEFEND) {
            System.out.println(getName() + " dựng khiên bảo vệ bản thân!");
            this.addBlock(intentValue);
        }
    }

    // Getters
    public EnemyIntent getCurrentIntent() { return currentIntent; }
    public int getIntentValue() { return intentValue; }
}
