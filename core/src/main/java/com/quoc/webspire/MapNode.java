package com.quoc.webspire;

import java.util.ArrayList;
import java.util.List;

public class MapNode {
    public enum NodeType {
        MONSTER("Quái"),
        ELITE("Tinh Anh"),
        REST("Trạm Nghỉ"),
        SHOP("Cửa Hàng"),
        TREASURE("Rương"),
        BOSS("TRÙM");

        private final String label;
        NodeType(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    private final int floor;
    private final int lane; // Vị trí cột (0 -> 3)
    private final NodeType type;
    private final List<MapNode> nextNodes = new ArrayList<>();

    private boolean visited = false;
    private boolean accessible = false;

    public MapNode(int floor, int lane, NodeType type) {
        this.floor = floor;
        this.lane = lane;
        this.type = type;
    }

    public void addNextNode(MapNode node) {
        if (!nextNodes.contains(node)) {
            nextNodes.add(node);
        }
    }

    // Getter & Setter
    public int getFloor() { return floor; }
    public int getLane() { return lane; }
    public NodeType getType() { return type; }
    public List<MapNode> getNextNodes() { return nextNodes; }
    public boolean isVisited() { return visited; }
    public void setVisited(boolean visited) { this.visited = visited; }
    public boolean isAccessible() { return accessible; }
    public void setAccessible(boolean accessible) { this.accessible = accessible; }
}
