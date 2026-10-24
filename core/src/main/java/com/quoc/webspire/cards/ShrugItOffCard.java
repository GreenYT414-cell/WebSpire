package com.quoc.webspire.cards;

import com.quoc.webspire.entities.Character;
import com.quoc.webspire.entities.Enemy;
import com.quoc.webspire.managers.DeckManager;

public class ShrugItOffCard extends Card {
    public ShrugItOffCard() {
        super("Phủi Bụi", 1, Type.DEFENSE, "Nhận 8 Giáp. Rút 1 lá bài.");
    }

    @Override
    public boolean use(Character player, Enemy enemy) {
        return use(player, enemy, null);
    }

    @Override
    public boolean use(Character player, Enemy enemy, DeckManager deckManager) {
        if (player.useEnergy(getCost())) {
            player.addBlock(isUpgraded() ? 11 : 8);
            if (deckManager != null) {
                deckManager.draw(1);
            }
            return true;
        }
        return false;
    }

    @Override
    public void upgrade() {
        if (!isUpgraded()) {
            setUpgraded(true);
            setName("Phủi Bụi+");
            setDescription("Nhận 11 Giáp. Rút 1 lá bài.");
        }
    }
}
