package com.quoc.webspire;

public class MetallicizeCard extends Card {
    public MetallicizeCard() {
        super("Kim Loại Hóa", 1, 0, 0, "Nhận +3 Giáp tự động ở cuối mỗi lượt.");
        setPower(true);
    }

    @Override
    public void use(Character player, Enemy monster) {
        int amount = isUpgraded() ? 4 : 3;
        player.addMetallicize(amount);
    }

    @Override
    public void upgrade() {
        if (!isUpgraded()) {
            setUpgraded(true);
            setName("Kim Loại Hóa+");
            setDescription("Nhận +4 Giáp tự động ở cuối mỗi lượt.");
        }
    }
}
