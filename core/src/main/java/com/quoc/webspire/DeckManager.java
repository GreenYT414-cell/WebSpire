package com.quoc.webspire;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeckManager {
    private List<Card> drawPile;
    private List<Card> hand;
    private List<Card> discardPile;

    public DeckManager() {
        this.drawPile = new ArrayList<>();
        this.hand = new ArrayList<>();
        this.discardPile = new ArrayList<>();
    }

    // Bắt đầu trận đấu: Sao chép toàn bộ bộ bài vào Draw Pile và xào bài
    public void initCombat(List<Card> masterDeck) {
        drawPile.clear();
        hand.clear();
        discardPile.clear();

        drawPile.addAll(masterDeck);
        Collections.shuffle(drawPile);
        System.out.println("Khởi tạo trận đấu! Đã xào " + drawPile.size() + " lá bài vào Bộ bài rút.");
    }

    // Rút N lá bài
    public void draw(int amount) {
        for (int i = 0; i < amount; i++) {
            // Nếu Bộ bài rút bị trống, tự động xào bài từ Mộ sang
            if (drawPile.isEmpty()) {
                if (discardPile.isEmpty()) {
                    System.out.println("Cả Bộ bài rút và Mộ đều hết bài! Không thể rút thêm.");
                    break;
                }
                reshuffle();
            }

            Card drawnCard = drawPile.remove(0); // Lấy lá bài trên cùng
            hand.add(drawnCard);
            System.out.println("Rút được lá: [" + drawnCard.getName() + "]");
        }
    }

    // Cơ chế xào lại bài từ Discard Pile sang Draw Pile
    private void reshuffle() {
        System.out.println("--> Bộ bài rút đã hết! Đang xào lại Mộ bài (" + discardPile.size() + " lá) vào Bộ bài rút...");
        drawPile.addAll(discardPile);
        discardPile.clear();
        Collections.shuffle(drawPile);
    }

    // Thực thi đánh 1 lá bài trên tay
    public boolean playCard(Card card, Character user, Character target) {
        if (!hand.contains(card)) {
            System.out.println("Lá bài [" + card.getName() + "] không có trên tay!");
            return false;
        }

        // Kiểm tra đủ năng lượng không
        if (user.getEnergy() < card.getCost()) {
            System.out.println("Không đủ Năng lượng để đánh lá [" + card.getName() + "]!");
            return false;
        }

        // Đánh bài và di chuyển lá bài vào Mộ (Discard Pile)
        card.play(user, target);
        hand.remove(card);
        discardPile.add(card);
        return true;
    }

    // Bỏ toàn bộ bài còn lại trên tay vào Mộ khi kết thúc lượt
    public void discardHand() {
        System.out.println("Bỏ " + hand.size() + " lá bài trên tay vào Mộ.");
        discardPile.addAll(hand);
        hand.clear();
    }

    // In trạng thái hiện tại ra Console để debug
    public void printStatus() {
        System.out.println("STATUS -> [Bài rút: " + drawPile.size() + " | Trên tay: " + hand.size() + " | Mộ: " + discardPile.size() + "]");
    }

    // Getters
    public List<Card> getHand() { return hand; }
    public List<Card> getDrawPile() { return drawPile; }
    public List<Card> getDiscardPile() { return discardPile; }
}
