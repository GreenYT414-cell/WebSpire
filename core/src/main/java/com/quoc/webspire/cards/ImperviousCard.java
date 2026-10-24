package com.quoc.webspire.cards;

import com.quoc.webspire.entities.Character;
import com.quoc.webspire.entities.Enemy;

public class ImperviousCard extends Card {
    public ImperviousCard() {
        super("Bất Khả Xâm Phạm", 2, Type.DEFENSE, "Tập trung toàn lực, nhận ngay 30 Giáp kiên cố.");
    }

    @Override
    public boolean use(Character player, Enemy monster) {
        if (player.useEnergy(getCost())) {
            player.addBlock(isUpgraded() ? 40 : 30);
            return true;
        }
        return false;
    }

    @Override
    public void upgrade() {
        if (!isUpgraded()) {
            setUpgraded(true);
            setName("Bất Khả Xâm Phạm+");
            setDescription("Tập trung toàn lực, nhận ngay 40 Giáp kiên cố.");
        }
    }
}
