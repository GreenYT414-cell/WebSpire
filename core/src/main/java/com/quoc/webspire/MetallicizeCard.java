package com.quoc.webspire;

public class MetallicizeCard extends Card {
    public MetallicizeCard() {
        super("Kim Loại Hóa", 1, CardType.POWER, "Nhận 3 Giáp ngay lập tức.");
    }

    @Override
    public boolean use(Character player, Enemy monster) {
        if (player.useEnergy(getCost())) {
            player.addBlock(isUpgraded() ? 5 : 3);
            return true;
        }
        return false;
    }

    @Override
    public void upgrade() {
        if (!isUpgraded()) {
            setUpgraded(true);
            setName("Kim Loại Hóa+");
            setDescription("Nhận 5 Giáp ngay lập tức.");
        }
    }
}
