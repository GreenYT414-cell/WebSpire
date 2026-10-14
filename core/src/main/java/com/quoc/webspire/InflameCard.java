package com.quoc.webspire;

public class InflameCard extends Card {
    public InflameCard() {
        super("Thiêu Đốt", 1, CardType.POWER, "Nhận +2 Sức Mạnh.");
    }

    @Override
    public boolean use(Character player, Enemy monster) {
        if (player.useEnergy(getCost())) {
            player.applyStatus(StatusEffect.Type.STRENGTH, isUpgraded() ? 3 : 2);
            return true;
        }
        return false;
    }

    @Override
    public void upgrade() {
        if (!isUpgraded()) {
            setUpgraded(true);
            setName("Thiêu Đốt+");
            setDescription("Nhận +3 Sức Mạnh.");
        }
    }
}
