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
                int optionsCount = 2 + random.nextInt(2); // 2 hoặc 3 ngã rẽ
                for (int lane = 0; lane < optionsCount; lane++) {
                    MapNode.Type type = getRandomNodeType(floor);
                    map.add(new MapNode(floor, lane, type));
                }
            }
        }

        connectFloors(map, totalFloors);

        return map;
    }

    private static void connectFloors(List<MapNode> map, int totalFloors) {
        // Thiết lập đường nối có hướng giữa tầng F và tầng F + 1
        for (int floor = 1; floor < totalFloors; floor++) {
            List<MapNode> currentNodes = new ArrayList<>();
            List<MapNode> nextNodes = new ArrayList<>();
            for (MapNode n : map) {
                if (n.getFloor() == floor) currentNodes.add(n);
                else if (n.getFloor() == floor + 1) nextNodes.add(n);
            }

            if (nextNodes.size() == 1) {
                // Boss tầng 8 hoặc Boss cuối tầng 15: Tất cả các nhánh đều dẫn về Boss
                for (MapNode cur : currentNodes) {
                    cur.addNextNode(nextNodes.get(0));
                }
            } else if (currentNodes.size() == 1) {
                // Từ 1 node tỏa ra nhiều nhánh tiếp theo
                for (MapNode next : nextNodes) {
                    currentNodes.get(0).addNextNode(next);
                }
            } else {
                // Phân nhánh logic theo lane gần nhau (khoảng cách lane <= 1)
                for (MapNode cur : currentNodes) {
                    int cLane = cur.getLane();
                    for (MapNode next : nextNodes) {
                        int nLane = next.getLane();
                        if (Math.abs(cLane - nLane) <= 1) {
                            cur.addNextNode(next);
                        }
                    }
                    if (cur.getNextNodes().isEmpty() && !nextNodes.isEmpty()) {
                        cur.addNextNode(nextNodes.get(0));
                    }
                }

                // Đảm bảo không có node nào ở tầng tiếp theo bị cô lập (không có lối vào)
                for (MapNode next : nextNodes) {
                    boolean hasParent = false;
                    for (MapNode cur : currentNodes) {
                        if (cur.getNextNodes().contains(next)) {
                            hasParent = true;
                            break;
                        }
                    }
                    if (!hasParent && !currentNodes.isEmpty()) {
                        MapNode closest = currentNodes.get(0);
                        int minDiff = Math.abs(closest.getLane() - next.getLane());
                        for (MapNode cur : currentNodes) {
                            int diff = Math.abs(cur.getLane() - next.getLane());
                            if (diff < minDiff) {
                                minDiff = diff;
                                closest = cur;
                            }
                        }
                        closest.addNextNode(next);
                    }
                }
            }
        }

        // Tầng 1 ban đầu luôn có thể tiếp cận
        for (MapNode node : map) {
            node.setAccessible(node.getFloor() == 1);
        }
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
