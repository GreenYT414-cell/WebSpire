package com.quoc.webspire;

public class InflameCard extends Card {
    public InflameCard() {
        super("Bùng Cháy", 1, 0, 0, "Nhận +2 Sức mạnh.");
        setPower(true);
    }

    @Override
    public void use(Character player, Enemy monster) {
        int amount = isUpgraded() ? 3 : 2;
        player.addStrength(amount);
    }

    @Override
    public void upgrade() {
        if (!isUpgraded()) {
            setUpgraded(true);
            setName("Bùng Cháy+");
            setDescription("Nhận +3 Sức mạnh.");
        }
    }
}
