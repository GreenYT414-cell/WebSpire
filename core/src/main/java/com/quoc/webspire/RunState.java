package com.quoc.webspire;

import java.util.ArrayList;
import java.util.List;

public class RunState {
    private static RunState instance;

    private Character player;
    private List<Card> masterDeck;
    private int currentFloor;

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
        this.player = new Character("Hiệp Sĩ", 80, 3);
        this.player.addRelic(new BurningBloodRelic());

        this.masterDeck = new ArrayList<>();
        for (int i = 0; i < 4; i++) masterDeck.add(new StrikeCard());
        for (int i = 0; i < 4; i++) masterDeck.add(new DefendCard());
        masterDeck.add(new BashCard());
    }

    public Character getPlayer() { return player; }
    public List<Card> getMasterDeck() { return masterDeck; }
    public int getCurrentFloor() { return currentFloor; }
    public void setCurrentFloor(int floor) { this.currentFloor = floor; }
}
