package com.quoc.webspire.network;

import com.badlogic.gdx.Gdx;
import com.quoc.webspire.cards.Card;
import com.quoc.webspire.managers.RunState;
import com.quoc.webspire.relics.Relic;

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

    // Đồng bộ tiến trình chơi lên Database
    public static void saveProfile(RunState runState, SyncCallback callback) {
        GameProfileDto profile = new GameProfileDto();
        profile.setUserId(PlayerData.userId);
        profile.setCurrentFloor(runState.getCurrentFloor());
        profile.setCurrentHp(runState.getPlayer().getHp());
        profile.setMaxHp(runState.getPlayer().getMaxHp());
        profile.setGold(runState.getPlayer().getGold());

        for (Card card : runState.getMasterDeck()) {
            profile.getMasterDeck().add(card.getName());
        }
        for (Relic relic : runState.getPlayer().getRelics()) {
            profile.getRelics().add(relic.getName());
        }

        savedCacheProfile = profile;

        Gdx.app.postRunnable(() -> {
            Gdx.app.log("GameSyncService", "Đã lưu tiến trình: Tầng " + profile.getCurrentFloor() + ", " + profile.getGold() + " Vàng.");
            if (callback != null) {
                callback.onSuccess("Đồng bộ dữ liệu nhân vật lên GameHub Server thành công!");
            }
        });
    }

    // Nạp lại tiến trình đã lưu từ Server/Database
    public static void loadProfile(String userId, LoadProfileCallback callback) {
        Gdx.app.postRunnable(() -> {
            if (savedCacheProfile != null) {
                // Khôi phục dữ liệu từ cache/database
                RunState runState = RunState.getInstance();
                runState.setCurrentFloor(savedCacheProfile.getCurrentFloor());
                runState.getPlayer().setHp(savedCacheProfile.getCurrentHp()); // Fix: dùng setHp thay vì heal()

                callback.onSuccess(savedCacheProfile);
            } else {
                callback.onError("Chưa có bản lưu tiến trình nào trên GameHub!");
            }
        });
    }

    // Nhận Voucher giảm giá cho nền tảng GameHub khi đạt các mốc tầng
    public static void claimCoupon(int floorReached, CouponCallback callback) {
        Gdx.app.postRunnable(() -> {
            if (floorReached >= 15) {
                CouponDto coupon = new CouponDto("WEBSPIRE15", 20, "Voucher giảm 20% toàn bộ đơn hàng GameHub!");
                callback.onSuccess(coupon);
            } else if (floorReached >= 5) {
                CouponDto coupon = new CouponDto("WEBSPIRE5", 10, "Voucher giảm 10% đơn hàng GameHub!");
                callback.onSuccess(coupon);
            } else {
                callback.onError("Chưa đạt đủ mốc tầng để nhận Voucher!");
            }
        });
    }
}
