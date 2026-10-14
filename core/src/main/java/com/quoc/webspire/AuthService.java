package com.quoc.webspire;

import com.badlogic.gdx.Gdx;

public class AuthService {

    public interface AuthCallback {
        void onSuccess(String message, String token);
        void onError(String errorMessage);
    }

    public static void login(String username, String password, AuthCallback callback) {
        // GIẢ LẬP ĐẮNG NHẬP (Không gửi HTTP Request tới Server)
        Gdx.app.postRunnable(() -> {
            PlayerData.email = username.isEmpty() ? "guest@gamehub.com" : username;
            PlayerData.isLoggedIn = true;
            callback.onSuccess("Đăng nhập chế độ Offline thành công!", "mock-jwt-token-123456");
        });
    }

    public static void register(String username, String password, AuthCallback callback) {
        // GIẢ LẬP ĐĂNG KÝ (Không gửi HTTP Request tới Server)
        Gdx.app.postRunnable(() -> {
            callback.onSuccess("Đăng ký Offline thành công! Bạn có thể bấm Đăng nhập ngay.", "mock-jwt-token-123456");
        });
    }
}
