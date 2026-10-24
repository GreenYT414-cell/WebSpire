package com.quoc.webspire.cards;

import com.quoc.webspire.entities.Character;
import com.quoc.webspire.entities.Enemy;
import com.quoc.webspire.managers.DeckManager;

public class BattleTranceCard extends Card {
    public BattleTranceCard() {
        super("Xuất Thần Chiến Trận", 0, Type.SKILL, "Rút 3 lá bài.");
    }

    @Override
    public boolean use(Character player, Enemy enemy) {
        return use(player, enemy, null);
    }

    @Override
    public boolean use(Character player, Enemy enemy, DeckManager deckManager) {
        if (player.useEnergy(getCost())) {
            if (deckManager != null) {
                deckManager.draw(isUpgraded() ? 4 : 3);
            }
            return true;
        }
        return false;
    }

    @Override
    public void upgrade() {
        if (!isUpgraded()) {
            setUpgraded(true);
            setName("Xuất Thần Chiến Trận+");
            setDescription("Rút 4 lá bài.");
        }
    }
}
