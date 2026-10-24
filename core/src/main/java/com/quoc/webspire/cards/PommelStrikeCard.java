package com.quoc.webspire.cards;

import com.quoc.webspire.entities.Character;
import com.quoc.webspire.entities.Enemy;
import com.quoc.webspire.managers.DeckManager;

public class PommelStrikeCard extends Card {
    public PommelStrikeCard() {
        super("Đòn Chuôi Kiếm", 1, Type.ATTACK, "Gây 9 sát thương. Rút 1 lá bài.");
    }

    @Override
    public boolean use(Character player, Enemy enemy) {
        return use(player, enemy, null);
    }

    @Override
    public boolean use(Character player, Enemy monster, DeckManager deckManager) {
        if (player.useEnergy(getCost())) {
            int damage = player.calculateDamageOutput(isUpgraded() ? 12 : 9);
            monster.takeDamage(damage);
            if (deckManager != null) {
                deckManager.draw(isUpgraded() ? 2 : 1);
            }
            return true;
        }
        return false;
    }

    @Override
    public void upgrade() {
        if (!isUpgraded()) {
            setUpgraded(true);
            setName("Đòn Chuôi Kiếm+");
            setDescription("Gây 12 sát thương. Rút 2 lá bài.");
        }
    }
}
