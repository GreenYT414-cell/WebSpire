package com.quoc.webspire;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeckManager {
    private List<Card> drawPile = new ArrayList<>();
    private List<Card> hand = new ArrayList<>();
    private List<Card> discardPile = new ArrayList<>();

    public void initCombat(List<Card> masterDeck) {
        drawPile.clear();
        hand.clear();
        discardPile.clear();
        drawPile.addAll(masterDeck);
        shuffleDrawPile();
    }

    public void shuffleDrawPile() {
        Collections.shuffle(drawPile);
    }

    public void draw(int count) {
        for (int i = 0; i < count; i++) {
            if (drawPile.isEmpty()) {
                if (discardPile.isEmpty()) break;
                drawPile.addAll(discardPile);
                discardPile.clear();
                shuffleDrawPile();
            }
            if (!drawPile.isEmpty()) {
                hand.add(drawPile.remove(0));
            }
        }
    }

    public boolean playCard(Card card, Character player, Enemy monster) {
        if (hand.contains(card)) {
            if (card.use(player, monster)) {
                hand.remove(card);
                discardPile.add(card);
                return true;
            }
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
}
