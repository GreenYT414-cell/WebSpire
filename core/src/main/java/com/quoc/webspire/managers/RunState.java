package com.quoc.webspire.managers;

import com.quoc.webspire.cards.BashCard;
import com.quoc.webspire.cards.Card;
import com.quoc.webspire.cards.DefendCard;
import com.quoc.webspire.cards.StrikeCard;
import com.quoc.webspire.entities.Character;
import com.quoc.webspire.relics.BurningBloodRelic;

import java.util.ArrayList;
import java.util.List;

public class RunState {
    private static RunState instance;

    private com.quoc.webspire.entities.Character player;
    private List<Card> masterDeck;
    private int currentFloor;

    // Thống kê chi tiết cho toàn bộ lượt chơi (Run Statistics)
    private int enemiesKilled = 0;
    private int elitesKilled = 0;
    private int bossesKilled = 0;
    private int totalDamageDealt = 0;
    private int totalDamageTaken = 0;
    private int totalGoldEarned = 0;
    private int cardsPlayed = 0;
    private int turnsCount = 0;

    private RunState() {
        startNewRun();
    }

    public static RunState getInstance() {
        if (instance == null) {
            instance = new RunState();
        }
        return instance;
    }

    public void startNewRun() {
        this.currentFloor = 1;
        this.player = new com.quoc.webspire.entities.Character("Hiệp Sĩ", 80, 3);
        this.player.addRelic(new BurningBloodRelic());
        this.player.addPotion(new com.quoc.webspire.potions.HealthPotion());

        this.masterDeck = new ArrayList<>();
        for (int i = 0; i < 4; i++) masterDeck.add(new StrikeCard());
        for (int i = 0; i < 4; i++) masterDeck.add(new DefendCard());
        masterDeck.add(new BashCard());

        // Reset toàn bộ thống kê cho ván mới
        this.enemiesKilled = 0;
        this.elitesKilled = 0;
        this.bossesKilled = 0;
        this.totalDamageDealt = 0;
        this.totalDamageTaken = 0;
        this.totalGoldEarned = player.getGold();
        this.cardsPlayed = 0;
        this.turnsCount = 0;
    }

    public Character getPlayer() { return player; }
    public List<Card> getMasterDeck() { return masterDeck; }
    public int getCurrentFloor() { return currentFloor; }
    public void setCurrentFloor(int floor) { this.currentFloor = floor; }

    public int getEnemiesKilled() { return enemiesKilled; }
    public void incrementEnemiesKilled() { this.enemiesKilled++; }

    public int getElitesKilled() { return elitesKilled; }
    public void incrementElitesKilled() { this.elitesKilled++; }

    public int getBossesKilled() { return bossesKilled; }
    public void incrementBossesKilled() { this.bossesKilled++; }

    public int getTotalDamageDealt() { return totalDamageDealt; }
    public void addDamageDealt(int amount) { if (amount > 0) this.totalDamageDealt += amount; }

    public int getTotalDamageTaken() { return totalDamageTaken; }
    public void addDamageTaken(int amount) { if (amount > 0) this.totalDamageTaken += amount; }

    public int getTotalGoldEarned() { return totalGoldEarned; }
    public void addGoldEarned(int amount) { if (amount > 0) this.totalGoldEarned += amount; }

    public int getCardsPlayed() { return cardsPlayed; }
    public void incrementCardsPlayed() { this.cardsPlayed++; }

    public int getTurnsCount() { return turnsCount; }
    public void incrementTurnsCount() { this.turnsCount++; }
}
