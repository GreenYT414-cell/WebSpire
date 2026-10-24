package com.quoc.webspire.managers;

import com.quoc.webspire.cards.Card;
import com.quoc.webspire.entities.Character;
import com.quoc.webspire.entities.Enemy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeckManager {
    private List<Card> drawPile = new ArrayList<>();
    private List<Card> hand = new ArrayList<>();
    private List<Card> discardPile = new ArrayList<>();
    private List<Card> exhaustPile = new ArrayList<>(); // Xấp bài bị loại (dùng 1 lần)

    public void initCombat(List<Card> masterDeck) {
        drawPile.clear();
        hand.clear();
        discardPile.clear();
        exhaustPile.clear();
        drawPile.addAll(masterDeck);
        Collections.shuffle(drawPile);
    }

    public void draw(int amount) {
        for (int i = 0; i < amount; i++) {
            if (drawPile.isEmpty()) {
                if (discardPile.isEmpty()) break;
                drawPile.addAll(discardPile);
                discardPile.clear();
                Collections.shuffle(drawPile);
            }
            if (!drawPile.isEmpty()) {
                hand.add(drawPile.remove(0));
            }
        }
    }

    public boolean playCard(Card card, Character player, Enemy enemy) {
        if (card.use(player, enemy, this)) {
            RunState.getInstance().incrementCardsPlayed();
            hand.remove(card);

            // Các lá thuộc loại POWER sẽ chỉ dùng 1 lần mỗi trận đấu
            if (card.getType() == Card.Type.POWER) {
                exhaustPile.add(card);
            } else {
                discardPile.add(card);
            }
            return true;
        }
        return false;
    }

    public void discardHand() {
        discardPile.addAll(hand);
        hand.clear();
    }

    public List<Card> getDrawPile() { return drawPile; }
    public List<Card> getHand() { return hand; }
    public List<Card> getDiscardPile() { return discardPile; }
    public List<Card> getExhaustPile() { return exhaustPile; }
}
