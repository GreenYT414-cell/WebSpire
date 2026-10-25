package com.quoc.webspire.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class AuthService {
    private static final String BASE_URL = "http://localhost:8080/api/auth";

    public interface AuthCallback {
        void onSuccess(String message, String token);
        void onError(String errorMessage);
    }

    public static void login(String username, String password, AuthCallback callback) {
        if (username == null || username.trim().isEmpty()) {
            username = "guest";
        }

        final String finalUsername = username.trim();
        String jsonPayload = "{\"username\":\"" + escapeJson(finalUsername) + "\",\"password\":\"" + escapeJson(password) + "\"}";

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpRequest = requestBuilder.newRequest()
            .method(Net.HttpMethods.POST)
            .url(BASE_URL + "/login")
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .content(jsonPayload)
            .timeout(4000)
            .build();

        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                String responseString = httpResponse.getResultAsString();

                Gdx.app.postRunnable(() -> {
                    try {
                        JsonValue root = new JsonReader().parse(responseString);
                        boolean success = root.getBoolean("success", false);
                        String msg = root.getString("message", "Đăng nhập thành công!");

                        if (statusCode >= 200 && statusCode < 300 && success) {
                            String token = root.getString("token", "");
                            JsonValue userObj = root.get("user");
                            if (userObj != null) {
                                PlayerData.userId = userObj.getString("id", "USER_1001");
                                PlayerData.email = userObj.getString("username", finalUsername);
                            }
                            PlayerData.token = token;
                            PlayerData.isLoggedIn = true;

                            if (callback != null) callback.onSuccess(msg, token);
                        } else {
                            if (callback != null) callback.onError(msg);
                        }
                    } catch (Exception e) {
                        fallbackOfflineLogin(finalUsername, callback, "Phản hồi máy chủ không hợp lệ, chuyển sang chế độ Ngoại Tuyến!");
                    }
                });
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() -> {
                    Gdx.app.log("AuthService", "Không thể kết nối Backend Server: " + t.getMessage());
                    fallbackOfflineLogin(finalUsername, callback, "Không kết nối được Máy chủ Cloud, tự động đăng nhập Ngoại Tuyến!");
                });
            }

            @Override
            public void cancelled() {
                Gdx.app.postRunnable(() -> {
                    if (callback != null) callback.onError("Yêu cầu đăng nhập đã bị hủy.");
                });
            }
        });
    }

    public static void register(String username, String password, AuthCallback callback) {
        if (username == null || username.trim().isEmpty()) {
            if (callback != null) callback.onError("Vui lòng nhập tên tài khoản!");
            return;
        }

        final String finalUsername = username.trim();
        String jsonPayload = "{\"username\":\"" + escapeJson(finalUsername) + "\",\"email\":\"" + escapeJson(finalUsername) + "@gamehub.com\",\"password\":\"" + escapeJson(password) + "\"}";

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpRequest = requestBuilder.newRequest()
            .method(Net.HttpMethods.POST)
            .url(BASE_URL + "/register")
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .content(jsonPayload)
            .timeout(4000)
            .build();

        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                String responseString = httpResponse.getResultAsString();

                Gdx.app.postRunnable(() -> {
                    try {
                        JsonValue root = new JsonReader().parse(responseString);
                        boolean success = root.getBoolean("success", false);
                        String msg = root.getString("message", "Đăng ký thành công!");

                        if (statusCode >= 200 && statusCode < 300 && success) {
                            String token = root.getString("token", "");
                            JsonValue userObj = root.get("user");
                            if (userObj != null) {
                                PlayerData.userId = userObj.getString("id", "USER_" + System.currentTimeMillis() % 10000);
                                PlayerData.email = userObj.getString("username", finalUsername);
                            }
                            PlayerData.token = token;
                            PlayerData.isLoggedIn = true;

                            if (callback != null) callback.onSuccess(msg, token);
                        } else {
                            if (callback != null) callback.onError(msg);
                        }
                    } catch (Exception e) {
                        fallbackOfflineLogin(finalUsername, callback, "Đăng ký Ngoại Tuyến thành công!");
                    }
                });
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() -> {
                    fallbackOfflineLogin(finalUsername, callback, "Máy chủ Cloud chưa bật, kích hoạt tài khoản Ngoại Tuyến!");
                });
            }

            @Override
            public void cancelled() {
                Gdx.app.postRunnable(() -> {
                    if (callback != null) callback.onError("Yêu cầu đăng ký đã bị hủy.");
                });
            }
        });
    }

    private static void fallbackOfflineLogin(String username, AuthCallback callback, String notice) {
        PlayerData.userId = "OFFLINE_" + (Math.abs(username.hashCode()) % 10000);
        PlayerData.email = username;
        PlayerData.token = "offline-token-mock";
        PlayerData.isLoggedIn = true;
        if (callback != null) {
            callback.onSuccess(notice, PlayerData.token);
        }
    }

    private static String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
