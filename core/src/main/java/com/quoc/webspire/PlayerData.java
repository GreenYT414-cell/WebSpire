package com.quoc.webspire;

public class PlayerData {
    // Lưu thông tin người chơi tạm thời khi chưa gắn API
    public static String email = "guest@gamehub.com";
    public static String token = "";
    public static int highestFloor = 1;
    public static int gold = 100;
    public static boolean isLoggedIn = false;

    public static void reset() {
        highestFloor = 1;
        gold = 100;
    }
}
