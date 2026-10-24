package com.quoc.webspire.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapGenerator {
    private static final Random random = new Random();

    public static List<MapNode> generateMap() {
        return generateMap(15);
    }

    public static List<MapNode> generateMap(int totalFloors) {
        List<MapNode> map = new ArrayList<>();

        for (int floor = 1; floor <= totalFloors; floor++) {
            if (floor == totalFloors) {
                map.add(new MapNode(floor, 0, MapNode.Type.BOSS));
            } else if (floor == 8) {
                // Tầng 8: TRÙM TRUNG GIAN (Mid-Boss)
                map.add(new MapNode(8, 0, MapNode.Type.BOSS));
            } else if (floor == 1) {
                map.add(new MapNode(1, 0, MapNode.Type.COMBAT));
                map.add(new MapNode(1, 1, MapNode.Type.COMBAT));
            } else if (floor == 7) {
                // Tầng 7: Nơi nghỉ ngơi chuẩn bị đấu Trùm trung gian
                map.add(new MapNode(7, 0, MapNode.Type.REST));
                map.add(new MapNode(7, 1, MapNode.Type.SHOP));
            } else {
                int optionsCount = 2 + random.nextInt(2);
                for (int lane = 0; lane < optionsCount; lane++) {
                    MapNode.Type type = getRandomNodeType(floor);
                    map.add(new MapNode(floor, lane, type));
                }
            }
        }

        return map;
    }

    private static MapNode.Type getRandomNodeType(int floor) {
        if (floor == 5 || floor == 10) {
            return MapNode.Type.REST;
        }

        int roll = random.nextInt(100);
        if (roll < 40) return MapNode.Type.COMBAT;
        if (roll < 60) return MapNode.Type.EVENT;
        if (roll < 75) return MapNode.Type.SHOP;
        if (roll < 88) return MapNode.Type.REST;
        return MapNode.Type.ELITE;
    }
}
