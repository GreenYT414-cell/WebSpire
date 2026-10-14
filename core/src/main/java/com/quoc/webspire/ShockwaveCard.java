package com.quoc.webspire;

public class ShockwaveCard extends Card {
    public ShockwaveCard() {
        super("Sóng Xung Kích", 2, CardType.SKILL, "Gán 2 Yếu Ớt và 2 Lỗ Hổng cho kẻ địch.");
    }

    @Override
    public boolean use(Character player, Enemy monster) {
        if (player.useEnergy(getCost())) {
            int stacks = isUpgraded() ? 3 : 2;
            monster.applyStatus(StatusEffect.Type.WEAK, stacks);
            monster.applyStatus(StatusEffect.Type.VULNERABLE, stacks);
            return true;
        }
        return false;
    }

    @Override
    public void upgrade() {
        if (!isUpgraded()) {
            setUpgraded(true);
            setName("Sóng Xung Kích+");
            setDescription("Gán 3 Yếu Ớt và 3 Lỗ Hổng cho kẻ địch.");
        }
    }
}
