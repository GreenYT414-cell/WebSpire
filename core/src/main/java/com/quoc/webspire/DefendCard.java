package com.quoc.webspire;

public class DefendCard extends Card {

    public DefendCard() {
        // Tham số: Name, Cost, Damage, Block, Description
        super("Phòng Thủ", 1, 0, 5, "Nhận 5 Giáp.");
    }

    @Override
    public void use(Character player, Enemy monster) {
        int totalBlock = isUpgraded() ? 8 : 5;
        player.addBlock(totalBlock);
    }

    @Override
    public void upgrade() {
        if (!isUpgraded()) {
            setUpgraded(true);
            setName("Phòng Thủ+");
            setDescription("Nhận 8 Giáp.");
        }
    }
}
