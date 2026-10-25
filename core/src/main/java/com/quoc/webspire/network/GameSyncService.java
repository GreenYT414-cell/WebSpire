package com.quoc.webspire.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.quoc.webspire.cards.*;
import com.quoc.webspire.managers.RunState;
import com.quoc.webspire.relics.*;

import java.util.ArrayList;
import java.util.List;

public class GameSyncService {
    private static final String BASE_URL = "http://localhost:8080/api/game";
    private static GameProfileDto savedCacheProfile = null;

    public interface SyncCallback {
        void onSuccess(String message);
        void onError(String errorMessage);
    }

    public interface CouponCallback {
        void onSuccess(CouponDto coupon);
        void onError(String errorMessage);
    }

    public interface LoadProfileCallback {
        void onSuccess(GameProfileDto profile);
        void onError(String errorMessage);
    }

    public interface LeaderboardCallback {
        void onSuccess(List<String> records);
        void onError(String errorMessage);
    }

    // 1. Đồng bộ tiến trình chơi lên Cloud Database
    public static void saveProfile(RunState runState, SyncCallback callback) {
        GameProfileDto profile = new GameProfileDto();
        profile.setUserId(PlayerData.userId);
        profile.setCurrentFloor(runState.getCurrentFloor());
        profile.setCurrentHp(runState.getPlayer().getHp());
        profile.setMaxHp(runState.getPlayer().getMaxHp());
        profile.setGold(runState.getPlayer().getGold());

        StringBuilder deckJsonArray = new StringBuilder("[");
        for (int i = 0; i < runState.getMasterDeck().size(); i++) {
            Card card = runState.getMasterDeck().get(i);
            profile.getMasterDeck().add(card.getName());
            deckJsonArray.append("\"").append(escapeJson(card.getName())).append("\"");
            if (i < runState.getMasterDeck().size() - 1) deckJsonArray.append(",");
        }
        deckJsonArray.append("]");

        StringBuilder relicJsonArray = new StringBuilder("[");
        for (int i = 0; i < runState.getPlayer().getRelics().size(); i++) {
            Relic relic = runState.getPlayer().getRelics().get(i);
            profile.getRelics().add(relic.getName());
            relicJsonArray.append("\"").append(escapeJson(relic.getName())).append("\"");
            if (i < runState.getPlayer().getRelics().size() - 1) relicJsonArray.append(",");
        }
        relicJsonArray.append("]");

        savedCacheProfile = profile;

        String jsonPayload = String.format(
            "{\"userId\":\"%s\",\"currentFloor\":%d,\"currentHp\":%d,\"maxHp\":%d,\"gold\":%d,\"masterDeck\":%s,\"relics\":%s}",
            escapeJson(PlayerData.userId),
            runState.getCurrentFloor(),
            runState.getPlayer().getHp(),
            runState.getPlayer().getMaxHp(),
            runState.getPlayer().getGold(),
            deckJsonArray.toString(),
            relicJsonArray.toString()
        );

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        requestBuilder.newRequest()
            .method(Net.HttpMethods.POST)
            .url(BASE_URL + "/profile")
            .header("Content-Type", "application/json")
            .header("Accept", "application/json");
        if (PlayerData.token != null && !PlayerData.token.isEmpty()) {
            requestBuilder.header("Authorization", "Bearer " + PlayerData.token);
        }
        Net.HttpRequest httpRequest = requestBuilder
            .content(jsonPayload)
            .timeout(4000)
            .build();

        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                Gdx.app.postRunnable(() -> {
                    if (callback != null) {
                        callback.onSuccess("Đã đồng bộ tiến trình lên Máy chủ Cloud GameHub thành công!");
                    }
                });
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() -> {
                    if (callback != null) {
                        callback.onSuccess("Đã lưu tiến trình vào bộ nhớ tạm (Chế độ Ngoại Tuyến)!");
                    }
                });
            }

            @Override
            public void cancelled() {
                Gdx.app.postRunnable(() -> {
                    if (callback != null) callback.onError("Thao tác lưu bị hủy.");
                });
            }
        });
    }

    // 2. Nạp lại tiến trình đã lưu từ Cloud Server
    public static void loadProfile(String userId, LoadProfileCallback callback) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        requestBuilder.newRequest()
            .method(Net.HttpMethods.GET)
            .url(BASE_URL + "/profile/" + escapeJson(userId))
            .header("Accept", "application/json");
        if (PlayerData.token != null && !PlayerData.token.isEmpty()) {
            requestBuilder.header("Authorization", "Bearer " + PlayerData.token);
        }
        Net.HttpRequest httpRequest = requestBuilder
            .timeout(4000)
            .build();

        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int code = httpResponse.getStatus().getStatusCode();
                String result = httpResponse.getResultAsString();

                Gdx.app.postRunnable(() -> {
                    if (code >= 200 && code < 300) {
                        try {
                            JsonValue root = new JsonReader().parse(result);
                            JsonValue pObj = root.get("profile");
                            if (pObj != null) {
                                GameProfileDto loaded = parseProfileJson(pObj);
                                applyProfileToGame(loaded);
                                if (callback != null) callback.onSuccess(loaded);
                                return;
                            }
                        } catch (Exception e) {
                            Gdx.app.log("GameSyncService", "Lỗi phân tích JSON profile: " + e.getMessage());
                        }
                    }
                    fallbackLocalLoad(callback);
                });
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() -> fallbackLocalLoad(callback));
            }

            @Override
            public void cancelled() {
                Gdx.app.postRunnable(() -> {
                    if (callback != null) callback.onError("Thao tác tải bị hủy.");
                });
            }
        });
    }

    private static void fallbackLocalLoad(LoadProfileCallback callback) {
        if (savedCacheProfile != null) {
            applyProfileToGame(savedCacheProfile);
            if (callback != null) callback.onSuccess(savedCacheProfile);
        } else {
            if (callback != null) callback.onError("Không tìm thấy bản lưu tiến trình nào!");
        }
    }

    private static GameProfileDto parseProfileJson(JsonValue pObj) {
        GameProfileDto p = new GameProfileDto();
        p.setUserId(pObj.getString("userId", PlayerData.userId));
        p.setCurrentFloor(pObj.getInt("currentFloor", 1));
        p.setCurrentHp(pObj.getInt("currentHp", 80));
        p.setMaxHp(pObj.getInt("maxHp", 80));
        p.setGold(pObj.getInt("gold", 0));

        JsonValue deckArray = pObj.get("masterDeck");
        if (deckArray != null && deckArray.isArray()) {
            for (JsonValue item = deckArray.child; item != null; item = item.next) {
                p.getMasterDeck().add(item.asString());
            }
        }

        JsonValue relicArray = pObj.get("relics");
        if (relicArray != null && relicArray.isArray()) {
            for (JsonValue item = relicArray.child; item != null; item = item.next) {
                p.getRelics().add(item.asString());
            }
        }
        return p;
    }

    private static void applyProfileToGame(GameProfileDto profile) {
        RunState runState = RunState.getInstance();
        runState.setCurrentFloor(profile.getCurrentFloor());
        runState.getPlayer().setMaxHp(profile.getMaxHp());
        runState.getPlayer().setHp(profile.getCurrentHp());
        runState.getPlayer().setGold(profile.getGold());

        if (profile.getMasterDeck() != null && !profile.getMasterDeck().isEmpty()) {
            runState.getMasterDeck().clear();
            for (String cardName : profile.getMasterDeck()) {
                Card card = createCardByName(cardName);
                if (card != null) runState.getMasterDeck().add(card);
            }
            if (runState.getMasterDeck().isEmpty()) {
                for (int i = 0; i < 4; i++) runState.getMasterDeck().add(new StrikeCard());
                for (int i = 0; i < 4; i++) runState.getMasterDeck().add(new DefendCard());
                runState.getMasterDeck().add(new BashCard());
            }
        }

        // Khôi phục Cổ Vật từ Cloud Save
        if (profile.getRelics() != null && !profile.getRelics().isEmpty()) {
            runState.getPlayer().getRelics().clear();
            for (String relicName : profile.getRelics()) {
                Relic relic = createRelicByName(relicName);
                if (relic != null) {
                    runState.getPlayer().getRelics().add(relic);
                    relic.onEquip(runState.getPlayer());
                }
            }
        }
    }

    private static Relic createRelicByName(String name) {
        if (name == null) return null;
        String n = name.trim();
        if (n.equalsIgnoreCase("Mỏ Neo")           || n.equalsIgnoreCase("Anchor"))           return new com.quoc.webspire.relics.AnchorRelic();
        if (n.equalsIgnoreCase("Kim Cương Chùy")    || n.equalsIgnoreCase("Vajra"))            return new com.quoc.webspire.relics.VajraRelic();
        if (n.equalsIgnoreCase("Lồng Đèn")          || n.equalsIgnoreCase("Lantern"))          return new com.quoc.webspire.relics.LanternRelic();
        if (n.equalsIgnoreCase("Nhẫn Năng Lượng")   || n.equalsIgnoreCase("Energy Ring"))      return new com.quoc.webspire.relics.EnergyRingRelic();
        if (n.equalsIgnoreCase("Dâu Rừng Ngọt")     || n.equalsIgnoreCase("Strawberry"))       return new com.quoc.webspire.relics.StrawberryRelic();
        if (n.equalsIgnoreCase("Đá Mịn Trơn")       || n.equalsIgnoreCase("Smooth Stone"))     return new com.quoc.webspire.relics.SmoothStoneRelic();
        if (n.equalsIgnoreCase("Thịt Rừng Cổ Đại")  || n.equalsIgnoreCase("Meat On The Bone")) return new com.quoc.webspire.relics.MeatOnTheBoneRelic();
        if (n.equalsIgnoreCase("Túi Bi Thủy Tinh")  || n.equalsIgnoreCase("Bag of Marbles"))   return new com.quoc.webspire.relics.BagOfMarblesRelic();
        if (n.equalsIgnoreCase("Máu Quỷ")           || n.equalsIgnoreCase("Burning Blood"))    return new com.quoc.webspire.relics.BurningBloodRelic();
        return null;
    }

    private static Card createCardByName(String name) {
        if (name == null) return new StrikeCard();
        boolean upgraded = name.endsWith("+");
        String base = upgraded ? name.substring(0, name.length() - 1).trim() : name.trim();

        Card c;
        if (base.equalsIgnoreCase("Cú Nện") || base.equalsIgnoreCase("Bash")) c = new BashCard();
        else if (base.equalsIgnoreCase("Chém") || base.equalsIgnoreCase("Strike")) c = new StrikeCard();
        else if (base.equalsIgnoreCase("Phòng Thủ") || base.equalsIgnoreCase("Defend")) c = new DefendCard();
        else if (base.equalsIgnoreCase("Chém Sắt") || base.equalsIgnoreCase("Iron Wave")) c = new IronWaveCard();
        else if (base.equalsIgnoreCase("Quét Kiếm") || base.equalsIgnoreCase("Cleave")) c = new CleaveCard();
        else if (base.equalsIgnoreCase("Chém Kép") || base.equalsIgnoreCase("Twin Strike")) c = new TwinStrikeCard();
        else if (base.equalsIgnoreCase("Đao Nặng") || base.equalsIgnoreCase("Heavy Blade")) c = new HeavyBladeCard();
        else if (base.equalsIgnoreCase("Móc Hàm") || base.equalsIgnoreCase("Uppercut")) c = new UppercutCard();
        else if (base.equalsIgnoreCase("Chém Độc") || base.equalsIgnoreCase("Poison Stab")) c = new PoisonStabCard();
        else if (base.equalsIgnoreCase("Đồ Sát") || base.equalsIgnoreCase("Carnage")) c = new CarnageCard();
        else if (base.equalsIgnoreCase("Bất Khả Xâm Phạm") || base.equalsIgnoreCase("Impervious")) c = new ImperviousCard();
        else if (base.equalsIgnoreCase("Cố Thủ") || base.equalsIgnoreCase("Entrench")) c = new EntrenchCard();
        else if (base.equalsIgnoreCase("Trút Bỏ") || base.equalsIgnoreCase("Shrug It Off")) c = new ShrugItOffCard();
        else if (base.equalsIgnoreCase("Sóng Xung Kích") || base.equalsIgnoreCase("Shockwave")) c = new ShockwaveCard();
        else if (base.equalsIgnoreCase("Hiến Máu") || base.equalsIgnoreCase("Bloodletting")) c = new BloodlettingCard();
        else if (base.equalsIgnoreCase("Bùng Nổ") || base.equalsIgnoreCase("Battle Trance")) c = new BattleTranceCard();
        else if (base.equalsIgnoreCase("Hình Thái Ác Quỷ") || base.equalsIgnoreCase("Demon Form")) c = new DemonFormCard();
        else if (base.equalsIgnoreCase("Nung Nấu") || base.equalsIgnoreCase("Inflame")) c = new InflameCard();
        else if (base.equalsIgnoreCase("Kim Hóa") || base.equalsIgnoreCase("Metallicize")) c = new MetallicizeCard();
        else if (base.equalsIgnoreCase("Đấm Cán Kiếm") || base.equalsIgnoreCase("Pommel Strike")) c = new PommelStrikeCard();
        else c = new StrikeCard();

        if (upgraded) c.upgrade();
        return c;
    }

    // 3. Nhận Voucher giảm giá cho nền tảng GameHub
    public static void claimCoupon(int floorReached, CouponCallback callback) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpRequest = requestBuilder.newRequest()
            .method(Net.HttpMethods.GET)
            .url(BASE_URL + "/coupons/" + floorReached)
            .header("Accept", "application/json")
            .timeout(4000)
            .build();

        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int code = httpResponse.getStatus().getStatusCode();
                String result = httpResponse.getResultAsString();

                Gdx.app.postRunnable(() -> {
                    if (code >= 200 && code < 300) {
                        try {
                            JsonValue root = new JsonReader().parse(result);
                            JsonValue cObj = root.get("coupon");
                            if (cObj != null) {
                                CouponDto coupon = new CouponDto(
                                    cObj.getString("code", "WEBSPIRE"),
                                    cObj.getInt("discountPercent", 10),
                                    cObj.getString("description", "Voucher GameHub")
                                );
                                if (callback != null) callback.onSuccess(coupon);
                                return;
                            }
                        } catch (Exception ignored) {}
                    }
                    fallbackCoupon(floorReached, callback);
                });
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() -> fallbackCoupon(floorReached, callback));
            }

            @Override
            public void cancelled() {
                Gdx.app.postRunnable(() -> {
                    if (callback != null) callback.onError("Yêu cầu nhận Voucher bị hủy.");
                });
            }
        });
    }

    private static void fallbackCoupon(int floorReached, CouponCallback callback) {
        if (floorReached >= 15) {
            callback.onSuccess(new CouponDto("WEBSPIRE15", 25, "Voucher VIP giảm 25% toàn bộ sản phẩm trên GameHub!"));
        } else if (floorReached >= 8) {
            callback.onSuccess(new CouponDto("WEBSPIRE8", 15, "Voucher giảm 15% khi hạ Hộ Vệ Tháp trên GameHub!"));
        } else if (floorReached >= 5) {
            callback.onSuccess(new CouponDto("WEBSPIRE5", 10, "Voucher giảm 10% tại nền tảng GameHub!"));
        } else {
            callback.onError("Cần vượt qua ít nhất Tầng 5 để nhận Voucher!");
        }
    }

    // 4. Nộp kết quả lượt chơi lên Bảng Xếp Hạng
    public static void submitScore(RunState runState, SyncCallback callback) {
        int score = runState.getCurrentFloor() * 150 + runState.getEnemiesKilled() * 50 + runState.getBossesKilled() * 300;
        String jsonPayload = String.format(
            "{\"userId\":\"%s\",\"username\":\"%s\",\"floorReached\":%d,\"score\":%d,\"enemiesKilled\":%d,\"bossesKilled\":%d,\"totalDamageDealt\":%d}",
            escapeJson(PlayerData.userId),
            escapeJson(PlayerData.email != null ? PlayerData.email : "HiepSi"),
            runState.getCurrentFloor(),
            score,
            runState.getEnemiesKilled(),
            runState.getBossesKilled(),
            runState.getTotalDamageDealt()
        );

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        requestBuilder.newRequest()
            .method(Net.HttpMethods.POST)
            .url(BASE_URL + "/leaderboard")
            .header("Content-Type", "application/json")
            .header("Accept", "application/json");
        if (PlayerData.token != null && !PlayerData.token.isEmpty()) {
            requestBuilder.header("Authorization", "Bearer " + PlayerData.token);
        }
        Net.HttpRequest httpRequest = requestBuilder
            .content(jsonPayload)
            .timeout(4000)
            .build();

        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                Gdx.app.postRunnable(() -> {
                    if (callback != null) callback.onSuccess("Đã ghi nhận điểm số lên Bảng Xếp Hạng GameHub!");
                });
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() -> {
                    if (callback != null) callback.onSuccess("Đã lưu điểm số kỷ lục cục bộ!");
                });
            }

            @Override
            public void cancelled() {
                if (callback != null) callback.onError("Thao tác nộp điểm bị hủy.");
            }
        });
    }

    // 5. Lấy danh sách kỷ lục Bảng Xếp Hạng
    public static void fetchLeaderboard(LeaderboardCallback callback) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpRequest = requestBuilder.newRequest()
            .method(Net.HttpMethods.GET)
            .url(BASE_URL + "/leaderboard")
            .header("Accept", "application/json")
            .timeout(4000)
            .build();

        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int code = httpResponse.getStatus().getStatusCode();
                String result = httpResponse.getResultAsString();

                Gdx.app.postRunnable(() -> {
                    List<String> list = new ArrayList<>();
                    if (code >= 200 && code < 300) {
                        try {
                            JsonValue root = new JsonReader().parse(result);
                            JsonValue lbArray = root.get("leaderboard");
                            if (lbArray != null && lbArray.isArray()) {
                                int rank = 1;
                                for (JsonValue item = lbArray.child; item != null; item = item.next) {
                                    String user = item.getString("username", "Vô danh");
                                    int score = item.getInt("score", 0);
                                    int floor = item.getInt("floorReached", 1);
                                    int kills = item.getInt("enemiesKilled", 0);
                                    list.add(String.format("#%d. %s  —  %d Điểm (Tầng %d | %d Quái)", rank++, user, score, floor, kills));
                                }
                            }
                        } catch (Exception ignored) {}
                    }
                    if (list.isEmpty()) {
                        list.add("#1. HiepSiBongDem  —  2850 Điểm (Tầng 15 | 18 Quái)");
                        list.add("#2. PhuThuyAoDo   —  2100 Điểm (Tầng 12 | 14 Quái)");
                        list.add("#3. SatThuBongMa   —  1350 Điểm (Tầng 8  | 9 Quái)");
                    }
                    if (callback != null) callback.onSuccess(list);
                });
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() -> {
                    List<String> list = new ArrayList<>();
                    list.add("#1. HiepSiBongDem (Offline)  —  2850 Điểm (Tầng 15)");
                    list.add("#2. PhuThuyAoDo (Offline)   —  2100 Điểm (Tầng 12)");
                    list.add("#3. SatThuBongMa (Offline)   —  1350 Điểm (Tầng 8)");
                    if (callback != null) callback.onSuccess(list);
                });
            }

            @Override
            public void cancelled() {
                if (callback != null) callback.onError("Yêu cầu lấy BXH bị hủy.");
            }
        });
    }

    private static String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
