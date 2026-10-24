package com.quoc.webspire.network;

import java.util.ArrayList;
import java.util.List;

public class GameProfileDto {
    private String userId;
    private int currentFloor;
    private int currentHp;
    private int maxHp;
    private int gold;
    private List<String> masterDeck = new ArrayList<>();
    private List<String> relics = new ArrayList<>();

    public GameProfileDto() {}

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public int getCurrentFloor() { return currentFloor; }
    public void setCurrentFloor(int currentFloor) { this.currentFloor = currentFloor; }

    public int getCurrentHp() { return currentHp; }
    public void setCurrentHp(int currentHp) { this.currentHp = currentHp; }

    public int getMaxHp() { return maxHp; }
    public void setMaxHp(int maxHp) { this.maxHp = maxHp; }

    public int getGold() { return gold; }
    public void setGold(int gold) { this.gold = gold; }

    public List<String> getMasterDeck() { return masterDeck; }
    public void setMasterDeck(List<String> masterDeck) { this.masterDeck = masterDeck; }

    public List<String> getRelics() { return relics; }
    public void setRelics(List<String> relics) { this.relics = relics; }
}
