package com.quoc.webspire;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapGenerator {
    private static final int TOTAL_FLOORS = 15;
    private static final int LANES_PER_FLOOR = 3; // Số làn (cột) mỗi tầng
    private static final Random random = new Random();

    public static List<List<MapNode>> generateMap() {
        List<List<MapNode>> map = new ArrayList<>();

        // 1. Tạo các Node cho từng tầng (Giữ nguyên)
        for (int floor = 0; floor < TOTAL_FLOORS; floor++) {
            List<MapNode> floorNodes = new ArrayList<>();

            if (floor == 0) {
                for (int lane = 0; lane < LANES_PER_FLOOR; lane++) {
                    MapNode node = new MapNode(floor, lane, MapNode.NodeType.MONSTER);
                    node.setAccessible(true);
                    floorNodes.add(node);
                }
            } else if (floor == TOTAL_FLOORS - 1) {
                floorNodes.add(new MapNode(floor, 1, MapNode.NodeType.BOSS));
            } else if (floor == 8) {
                for (int lane = 0; lane < LANES_PER_FLOOR; lane++) {
                    floorNodes.add(new MapNode(floor, lane, MapNode.NodeType.TREASURE));
                }
            } else {
                for (int lane = 0; lane < LANES_PER_FLOOR; lane++) {
                    MapNode.NodeType type = getRandomNodeType(floor);
                    floorNodes.add(new MapNode(floor, lane, type));
                }
            }
            map.add(floorNodes);
        }

        // 2. Nối đường đi: LUÔN NỐI ĐỐI DIỆN VÀ LIỀN KỀ (Trái - Thẳng - Phải)
        for (int floor = 0; floor < TOTAL_FLOORS - 1; floor++) {
            List<MapNode> currentFloor = map.get(floor);
            List<MapNode> nextFloor = map.get(floor + 1);

            if (nextFloor.size() == 1) {
                // Nếu tầng tiếp theo chỉ có 1 nút (Ví dụ: Boss), nối tất cả vào đó
                for (MapNode node : currentFloor) {
                    node.addNextNode(nextFloor.get(0));
                }
            } else {
                // Tầng bình thường: Nối làn hiện tại sang làn (lane - 1), (lane), (lane + 1) của tầng trên
                for (int lane = 0; lane < currentFloor.size(); lane++) {
                    MapNode current = currentFloor.get(lane);

                    // Nối liền kề bên trái (Lane - 1)
                    if (lane - 1 >= 0 && lane - 1 < nextFloor.size()) {
                        current.addNextNode(nextFloor.get(lane - 1));
                    }

                    // Nối thẳng / đối diện (Lane)
                    if (lane < nextFloor.size()) {
                        current.addNextNode(nextFloor.get(lane));
                    }

                    // Nối liền kề bên phải (Lane + 1)
                    if (lane + 1 < nextFloor.size()) {
                        current.addNextNode(nextFloor.get(lane + 1));
                    }
                }
            }
        }

        return map;
    }

    private static MapNode.NodeType getRandomNodeType(int floor) {
        int roll = random.nextInt(100);
        if (floor == 6 || floor == 13) return MapNode.NodeType.REST;

        if (roll < 45) return MapNode.NodeType.MONSTER;
        if (roll < 65) return MapNode.NodeType.ELITE;
        if (roll < 80) return MapNode.NodeType.SHOP;
        return MapNode.NodeType.REST;
    }
}
