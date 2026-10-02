package com.quoc.webspire;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    @Override
    public void create() {
        System.out.println("=== KIỂM THỬ LOGIC NGÀY 1 ===");

        // 1. Khởi tạo Người chơi (80 HP, 3 Energy) và Quái vật (30 HP, 0 Energy)
        Character player = new Character("Hiệp Sĩ", 80, 3);
        Character goblin = new Character("Goblin", 30, 0);

        // 2. Khởi tạo lá bài Strike
        Card strikeCard = new StrikeCard();

        // 3. Người chơi bắt đầu lượt (Năng lượng hồi về 3)
        player.startTurn();
        System.out.println("Năng lượng hiện tại: " + player.getEnergy());

        // 4. Người chơi đánh lá Strike vào Goblin lần 1
        strikeCard.play(player, goblin);
        System.out.println("Năng lượng còn lại: " + player.getEnergy());

        // 5. Đánh tiếp lần 2
        strikeCard.play(player, goblin);

        // 6. Đánh tiếp lần 3
        strikeCard.play(player, goblin);

        // 7. Cố gắng đánh lần 4 (Sẽ báo không đủ năng lượng)
        strikeCard.play(player, goblin);

        System.out.println("================================");
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
    }

    @Override
    public void dispose() {
    }
}
