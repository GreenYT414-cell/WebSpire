package com.quoc.webspire.map;

import java.util.ArrayList;
import java.util.List;

public class MapNode {
    public enum Type {
        COMBAT,
        ELITE,
        BOSS,
        SHOP,
        REST,
        EVENT
    }

    private final int floor;
    private final int lane;
    private final Type type;
    private final List<MapNode> nextNodes = new ArrayList<>();

    private boolean visited = false;
    private boolean accessible = false;

    public MapNode(int floor, int lane, Type type) {
        this.floor = floor;
        this.lane = lane;
        this.type = type;
    }

    public MapNode(int floor, Type type) {
        this(floor, 0, type);
    }

    public void addNextNode(MapNode node) {
        if (!nextNodes.contains(node)) {
            nextNodes.add(node);
        }
    }

    public int getFloor() { return floor; }
    public int getLane() { return lane; }
    public Type getType() { return type; }
    public List<MapNode> getNextNodes() { return nextNodes; }
    public boolean isVisited() { return visited; }
    public void setVisited(boolean visited) { this.visited = visited; }
    public boolean isAccessible() { return accessible; }
    public void setAccessible(boolean accessible) { this.accessible = accessible; }
}
