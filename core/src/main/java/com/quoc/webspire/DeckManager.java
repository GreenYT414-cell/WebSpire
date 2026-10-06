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

        for (Card card : masterDeck) {
            drawPile.add(card);
        }
        Collections.shuffle(drawPile);
    }

    public void draw(int count) {
        for (int i = 0; i < count; i++) {
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

    public boolean playCard(Card card, Character player, Enemy monster) {
        if (card.getCost() <= player.getEnergy()) {
            player.setEnergy(player.getEnergy() - card.getCost());
            card.use(player, monster);
            hand.remove(card);

            // Lá Power sử dụng xong sẽ biến mất khỏi trận đánh
            if (!card.isPower()) {
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
}
