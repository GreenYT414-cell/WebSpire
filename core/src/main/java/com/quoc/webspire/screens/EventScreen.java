package com.quoc.webspire.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import com.quoc.webspire.Main;
import com.quoc.webspire.cards.*;
import com.quoc.webspire.entities.Character;
import com.quoc.webspire.managers.Assets;
import com.quoc.webspire.managers.RunState;
import com.quoc.webspire.managers.SoundManager;
import com.quoc.webspire.relics.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class EventScreen implements Screen {
    private final Main game;
    private final MapScreen mapScreen;
    private Stage stage;
    private Random random = new Random();

    private Table root;
    private Label titleLabel;
    private Label descLabel;
    private Label outcomeLabel;
    private Table choicesTable;
    private TextButton continueBtn;

    public EventScreen(Main game, MapScreen mapScreen) {
        this.game = game;
        this.mapScreen = mapScreen;
        this.stage = new Stage(new FitViewport(1280, 720), game.batch);
    }

    private void proceedToNextFloor() {
        int nextFloor = Math.min(15, RunState.getInstance().getCurrentFloor() + 1);
        RunState.getInstance().setCurrentFloor(nextFloor);
        game.setScreen(mapScreen);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        SoundManager.getInstance().playBgm("bgm_menu.mp3");

        stage.clear();

        root = new Table();
        root.setFillParent(true);
        root.pad(40);

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.font, Color.GOLD);
        Label.LabelStyle descStyle = new Label.LabelStyle(game.font, Color.LIGHT_GRAY);
        Label.LabelStyle outcomeStyle = new Label.LabelStyle(game.font, Color.CYAN);

        titleLabel = new Label("", titleStyle);
        descLabel = new Label("", descStyle);
        descLabel.setWrap(true);

        outcomeLabel = new Label("", outcomeStyle);
        outcomeLabel.setWrap(true);

        choicesTable = new Table();

        TextButton.TextButtonStyle contBtnStyle = new TextButton.TextButtonStyle();
        contBtnStyle.font = game.font;
        contBtnStyle.fontColor = Color.YELLOW;
        contBtnStyle.up = Assets.createColorDrawable(0.15f, 0.45f, 0.25f, 1f);

        continueBtn = new TextButton("Tiếp Tục Hành Trình", contBtnStyle);
        continueBtn.pad(10, 30, 10, 30);
        continueBtn.setVisible(false);
        continueBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                proceedToNextFloor();
            }
        });

        root.add(titleLabel).padBottom(20).row();
        root.add(descLabel).width(850).padBottom(25).row();
        root.add(choicesTable).padBottom(20).row();
        root.add(outcomeLabel).width(850).padBottom(20).row();
        root.add(continueBtn);

        stage.addActor(root);

        // Chọn ngẫu nhiên 1 trong 8 kịch bản sự kiện
        setupRandomEvent();
    }

    private void setupRandomEvent() {
        int eventId = random.nextInt(8);
        Character player = RunState.getInstance().getPlayer();
        List<Card> masterDeck = RunState.getInstance().getMasterDeck();

        switch (eventId) {
            case 0:
                setupAncientChestEvent(player);
                break;
            case 1:
                setupForgottenShrineEvent(player);
                break;
            case 2:
                setupShadowPeddlerEvent(player);
                break;
            case 3:
                setupPurificationFountainEvent(player, masterDeck);
                break;
            case 4:
                setupDevilGamblingEvent(player, masterDeck);
                break;
            case 5:
                setupMushroomForestEvent(player, masterDeck);
                break;
            case 6:
                setupColossusForgeEvent(player, masterDeck);
                break;
            default:
                setupAncientTomeEvent(player, masterDeck);
                break;
        }
    }

    // 1. RƯƠNG BÁO VẬT CỔ
    private void setupAncientChestEvent(final Character player) {
        titleLabel.setText("SỰ KIỆN: RƯƠNG BÁO VẬT CỔ");
        descLabel.setText("Một chiếc rương cổ bọc sắt nằm ẩn mình dưới lớp rêu phong của ngọn tháp. Xung quanh ổ khóa cắm đầy gai nhọn sắc bén cảnh báo kẻ tò mò.");

        addChoice("Cạy khóa cẩn thận (+45 Vàng)", Color.YELLOW, () -> {
            player.addGold(45);
            finishEvent("Bạn cẩn thận dùng dao găm nạy nắp rương và thu được 45 Vàng sáng lấp lánh!", "sfx_skill.wav");
        });

        addChoice("Phá khóa bạo lực (Nhận 1 Cổ vật ngẫu nhiên, mất 12 Máu)", Color.SALMON, () -> {
            player.takeDamage(12);
            Relic relic = awardRandomRelic(player);
            String relicText = relic != null ? "[" + relic.getName() + "]" : "Không còn cổ vật mới";
            finishEvent("Bạn dùng chuôi kiếm đập vỡ ổ khóa! Gai sắt bắn vào người khiến bạn mất 12 Máu, nhưng bên trong là Cổ vật quý giá: " + relicText, "sfx_attack.wav");
        });

        addChoice("Bỏ qua an toàn (Rời đi)", Color.LIGHT_GRAY, () -> {
            finishEvent("Bạn quyết định không mạo hiểm trước những cạm bẫy chưa rõ và an toàn bước tiếp.", "sfx_skill.wav");
        });
    }

    // 2. ĐỀN THỜ QUÊN LÃNG
    private void setupForgottenShrineEvent(final Character player) {
        titleLabel.setText("SỰ KIỆN: ĐỀN THỜ QUÊN LÃNG");
        descLabel.setText("Một điện thờ cổ bằng đá cẩm thạch đen tỏa ánh hào quang mờ ảo. Trên bệ thờ khắc dòng chữ: 'Hiến dâng sinh mệnh để đổi lấy tiềm năng vô tận, hoặc cầu nguyện để chữa lành vết thương'.");

        addChoice("Hiến tế linh hồn (Mất 10 Máu, Tăng vĩnh viễn +10 Máu Tối Đa)", Color.SCARLET, () -> {
            player.takeDamage(10);
            player.setMaxHp(player.getMaxHp() + 10);
            finishEvent("Một luồng năng lượng nóng rực xuyên qua tim bạn. Máu Tối Đa tăng lên " + player.getMaxHp() + " HP!", "sfx_skill.wav");
        });

        addChoice("Cầu nguyện ánh sáng (Hồi phục 25 Máu)", Color.GREEN, () -> {
            player.heal(25);
            finishEvent("Dòng ánh sáng dịu mát phủ lên người bạn, hồi phục ngay 25 Máu (Hiện tại: " + player.getHp() + "/" + player.getMaxHp() + " HP).", "sfx_defend.wav");
        });

        addChoice("Cúi đầu rời đi", Color.LIGHT_GRAY, () -> {
            finishEvent("Bạn tỏ lòng kính trọng với vị thần cổ rồi lặng lẽ tiếp tục lên đường.", "sfx_skill.wav");
        });
    }

    // 3. THƯƠNG NHÂN ĐÊM TỐI
    private void setupShadowPeddlerEvent(final Character player) {
        titleLabel.setText("SỰ KIỆN: THƯƠNG NHÂN ĐÊM TỐI");
        descLabel.setText("Một kẻ bí ẩn khoác áo choàng trùm đầu bước ra từ bóng tối góc tháp, chìa ra những món cổ vật phát sáng kèm nụ cười ranh mãnh.");

        if (player.getGold() >= 60) {
            addChoice("Giao dịch chợ đen (Trả 60 Vàng, nhận 1 Cổ vật ngẫu nhiên)", Color.GOLD, () -> {
                player.addGold(-60);
                Relic relic = awardRandomRelic(player);
                String relicText = relic != null ? "[" + relic.getName() + "]" : "Cổ vật bí mật";
                finishEvent("Giao dịch thành công! Bạn nhận được Cổ vật " + relicText + " từ tay thương nhân bóng đêm.", "sfx_skill.wav");
            });
        } else {
            addChoice("[Không đủ 60 Vàng] Giao dịch chợ đen", Color.DARK_GRAY, null);
        }

        addChoice("Cướp bóc bóng ma (Nhận 1 Cổ vật và 50 Vàng, mất 16 Máu)", Color.RED, () -> {
            player.takeDamage(16);
            player.addGold(50);
            Relic relic = awardRandomRelic(player);
            String relicText = relic != null ? "[" + relic.getName() + "]" : "Cổ vật";
            finishEvent("Kẻ buôn bóng đêm rút ma đao đánh trả kịch liệt khiến bạn mất 16 Máu trước khi hắn tan biến, bỏ lại 50 Vàng và Cổ vật " + relicText + "!", "sfx_attack.wav");
        });

        addChoice("Lắc đầu từ chối", Color.LIGHT_GRAY, () -> {
            finishEvent("Bạn từ chối mọi lời cám dỗ và tiếp tục tiến lên tầng trên.", "sfx_skill.wav");
        });
    }

    // 4. ĐÀI PHUN NƯỚC THANH TẨY
    private void setupPurificationFountainEvent(final Character player, final List<Card> masterDeck) {
        titleLabel.setText("SỰ KIỆN: ĐÀI PHUN NƯỚC THANH TẨY");
        descLabel.setText("Giữa căn phòng đá ẩm ướt là một đài phun nước bằng lam ngọc. Nước trong vắt như gương có thể thanh lọc tạp niệm trong tâm trí hoặc chữa lành thương tích.");

        // Tìm xem có lá Strike nào để xóa không
        Card strikeToRemove = null;
        for (Card c : masterDeck) {
            if (c instanceof StrikeCard) {
                strikeToRemove = c;
                break;
            }
        }
        final Card finalStrike = strikeToRemove;

        if (finalStrike != null) {
            addChoice("Thanh lọc tâm trí (Xóa 1 lá Đòn Đánh [Strike] cơ bản)", Color.CYAN, () -> {
                masterDeck.remove(finalStrike);
                finishEvent("Tâm trí bạn trở nên thông suốt! Lá bài Đòn Đánh cơ bản đã được thanh tẩy vĩnh viễn khỏi bộ bài.", "sfx_skill.wav");
            });
        }

        addChoice("Uống dòng nước thần (Hồi phục 100% HP)", Color.GREEN, () -> {
            player.setHp(player.getMaxHp());
            finishEvent("Dòng nước thanh khiết tràn ngập cơ thể bạn, toàn bộ sinh lực đã được hồi phục 100% (" + player.getHp() + "/" + player.getMaxHp() + " HP)!", "sfx_defend.wav");
        });

        addChoice("Rửa mặt tỉnh táo (+10 HP)", Color.LIGHT_GRAY, () -> {
            player.heal(10);
            finishEvent("Bạn rửa mặt mát lạnh, hồi phục nhẹ 10 HP và tiếp tục hành trình.", "sfx_skill.wav");
        });
    }

    // 5. BÀN CỜ QUỶ DỮ
    private void setupDevilGamblingEvent(final Character player, final List<Card> masterDeck) {
        titleLabel.setText("SỰ KIỆN: BÀN CỜ QUỶ DỮ");
        descLabel.setText("Một con tiểu quỷ đang ngồi lắc chiếc cốc xương trên chiếc bàn đen. 'Này hiệp sĩ dũng cảm! Có muốn thử vận may với một canh bạc không?'");

        if (player.getGold() >= 35) {
            addChoice("Đặt cược xúc xắc (Mất 35 Vàng: 60% cơ hội thắng 85 Vàng)", Color.YELLOW, () -> {
                player.addGold(-35);
                boolean win = random.nextInt(100) < 60;
                if (win) {
                    player.addGold(85);
                    finishEvent("Thắng cược! Con số đỏ rực hiện lên, tiểu quỷ bực tức ném cho bạn 85 Vàng tươi!", "sfx_victory.wav");
                } else {
                    finishEvent("Thua cược! Tiểu quỷ cười khúc khích và gom sạch 35 Vàng của bạn vào túi.", "sfx_attack.wav");
                }
            });
        }

        addChoice("Ký khế ước quỷ dữ (Mất 14 Máu, nhận lá bài cực mạnh 'Hình Thái Ác Quỷ')", Color.SCARLET, () -> {
            player.takeDamage(14);
            masterDeck.add(new DemonFormCard());
            finishEvent("Bạn ký tên bằng máu tươi! Nhận được lá bài cực mạnh: [Hình Thái Ác Quỷ] (Mỗi lượt nhận vĩnh viễn +2 Sức Mạnh)!", "sfx_skill.wav");
        });

        addChoice("Bỏ qua canh bạc", Color.LIGHT_GRAY, () -> {
            finishEvent("Bạn không tin tưởng vào ma quỷ và dứt khoát bước qua căn phòng.", "sfx_skill.wav");
        });
    }

    // 6. KHU RỪNG NẤM PHÁT QUANG
    private void setupMushroomForestEvent(final Character player, final List<Card> masterDeck) {
        titleLabel.setText("SỰ KIỆN: KHU RỪNG NẤM MA THUẬT");
        descLabel.setText("Những cây nấm khổng lồ phát sáng lân tinh trong bóng tối. Bào tử nấm tỏa ra mùi hương vừa ngọt ngào vừa độc địa.");

        addChoice("Hái nấm độc dược (Chịu 8 sát thương, nhận 40 Vàng và lá 'Nhát Đâm Độc')", Color.GREEN, () -> {
            player.takeDamage(8);
            player.addGold(40);
            masterDeck.add(new PoisonStabCard());
            finishEvent("Gai nấm cào rách da khiến bạn mất 8 Máu, nhưng bạn thu hoạch được 40 Vàng và lá bài [Nhát Đâm Độc]!", "sfx_skill.wav");
        });

        addChoice("Hít phấn nấm tăng lực (Hồi 18 HP, giảm 3 Máu Tối Đa)", Color.ORANGE, () -> {
            player.setMaxHp(Math.max(10, player.getMaxHp() - 3));
            player.heal(18);
            finishEvent("Dược tính mạnh mẽ giúp hồi ngay 18 Máu, nhưng làm tổn hại thể chất lâu dài (-3 Máu Tối Đa).", "sfx_defend.wav");
        });

        addChoice("Bịt mũi bước qua nhanh", Color.LIGHT_GRAY, () -> {
            finishEvent("Bạn nín thở bước qua khu rừng nấm và an toàn sang gian phòng kế tiếp.", "sfx_skill.wav");
        });
    }

    // 7. LÒ RÈN CỦA NGƯỜI KHỔNG LỒ
    private void setupColossusForgeEvent(final Character player, final List<Card> masterDeck) {
        titleLabel.setText("SỰ KIỆN: LÒ RÈN CỦA NGƯỜI KHỔNG LỒ");
        descLabel.setText("Ngọn lửa dung nham ngàn năm vẫn đang cháy rừng rực trên chiếc đe sắt khổng lồ bị lãng quên của người khổng lồ.");

        addChoice("Rèn vũ khí hủy diệt (Nhận lá bài 'Đại Đao Trảm')", Color.GOLD, () -> {
            masterDeck.add(new HeavyBladeCard());
            finishEvent("Bạn trui rèn thanh kiếm vào lửa dung nham và nhận được lá bài tấn công siêu cấp: [Đại Đao Trảm]!", "sfx_attack.wav");
        });

        addChoice("Đúc thêm giáp trụ (Tăng vĩnh viễn +8 Máu Tối Đa và hồi 8 Máu)", Color.CYAN, () -> {
            player.setMaxHp(player.getMaxHp() + 8);
            player.heal(8);
            finishEvent("Bạn tôi luyện bộ giáp kiên cố hơn, nhận +8 Máu Tối Đa và hồi phục 8 Máu!", "sfx_defend.wav");
        });

        addChoice("Thu nhặt sắt vụn quý hiếm (+35 Vàng)", Color.LIGHT_GRAY, () -> {
            player.addGold(35);
            finishEvent("Bạn nhặt được nhiều quặng kim loại quý quanh lò rèn và quy đổi được 35 Vàng!", "sfx_skill.wav");
        });
    }

    // 8. CUỐN SÁCH CỔ KỲ BÍ
    private void setupAncientTomeEvent(final Character player, final List<Card> masterDeck) {
        titleLabel.setText("SỰ KIỆN: CUỐN SÁCH CỔ KỲ BÍ");
        descLabel.setText("Một cuốn sách bìa da khắc cổ tự cấm kỵ lơ lửng giữa không trung. Những trang sách tự lật phát ra tiếng thì thầm ma mị...");

        addChoice("Đọc sâu vào cấm thuật (Mất 10 Máu, nhận lá phòng thủ 'Bất Khả Xâm Phạm')", Color.SCARLET, () -> {
            player.takeDamage(10);
            masterDeck.add(new ImperviousCard());
            finishEvent("Tri thức cổ xưa khiến đầu bạn đau nhói (-10 Máu), nhưng bù lại bạn lĩnh hội được lá bài phòng ngự tối thượng: [Bất Khả Xâm Phạm] (+30 Giáp)!", "sfx_skill.wav");
        });

        addChoice("Giải mã chương chiến lược (+40 Vàng, nhận lá 'Xuất Thần Chiến Trận')", Color.CYAN, () -> {
            player.addGold(40);
            masterDeck.add(new BattleTranceCard());
            finishEvent("Bạn giải mã được bí quyết binh pháp, nhận 40 Vàng và lá bài [Xuất Thần Chiến Trận] (Rút 3 lá bài lập tức)!", "sfx_skill.wav");
        });

        addChoice("Đóng cuốn sách lại", Color.LIGHT_GRAY, () -> {
            finishEvent("Bạn khép cuốn sách lại để tránh những nguyền rủa đen tối và bình an bước tiếp.", "sfx_skill.wav");
        });
    }

    private void addChoice(String labelText, Color color, final Runnable onSelect) {
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = game.font;
        btnStyle.fontColor = color;
        btnStyle.up = Assets.createColorDrawable(0.18f, 0.22f, 0.32f, 0.95f);

        final TextButton button = new TextButton(labelText, btnStyle);
        button.pad(10, 20, 10, 20);

        if (onSelect != null) {
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    onSelect.run();
                }
            });
        } else {
            button.setDisabled(true);
            btnStyle.up = Assets.createColorDrawable(0.15f, 0.15f, 0.15f, 0.5f);
        }

        choicesTable.add(button).width(750).padBottom(10).row();
    }

    private void finishEvent(String outcomeText, String sfx) {
        outcomeLabel.setText(outcomeText);
        choicesTable.clear();
        continueBtn.setVisible(true);
        if (sfx != null) {
            SoundManager.getInstance().playSound(sfx);
        }
    }

    private Relic awardRandomRelic(Character player) {
        List<Relic> pool = new ArrayList<>();
        pool.add(new AnchorRelic());
        pool.add(new VajraRelic());
        pool.add(new LanternRelic());
        pool.add(new EnergyRingRelic());
        pool.add(new StrawberryRelic());
        pool.add(new SmoothStoneRelic());
        pool.add(new MeatOnTheBoneRelic());
        pool.add(new BagOfMarblesRelic());

        List<Relic> unowned = new ArrayList<>();
        for (Relic r : pool) {
            boolean owned = false;
            for (Relic p : player.getRelics()) {
                if (p.getName().equalsIgnoreCase(r.getName())) {
                    owned = true;
                    break;
                }
            }
            if (!owned) unowned.add(r);
        }
        if (unowned.isEmpty()) return null;
        Collections.shuffle(unowned);
        Relic chosen = unowned.get(0);
        player.addRelic(chosen);
        return chosen;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.05f, 0.07f, 0.09f, 1f);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); }
}
