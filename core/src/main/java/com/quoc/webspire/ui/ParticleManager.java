package com.quoc.webspire.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.quoc.webspire.managers.Assets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ParticleManager {
    private static class SimpleParticle {
        float x, y;
        float vx, vy;
        float size;
        float life;
        float maxLife;
        Color color;

        public SimpleParticle(float x, float y, float vx, float vy, float size, float maxLife, Color color) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.size = size;
            this.maxLife = maxLife;
            this.life = maxLife;
            this.color = new Color(color);
        }

        public boolean update(float delta) {
            x += vx * delta;
            y += vy * delta;
            life -= delta;
            color.a = Math.max(0, life / maxLife);
            return life <= 0;
        }
    }

    private final List<SimpleParticle> particles = new ArrayList<>();
    private final TextureRegionDrawable pixelDrawable;

    public ParticleManager() {
        this.pixelDrawable = Assets.createColorDrawable(1f, 1f, 1f, 1f);
    }

    // Hiệu ứng Vệt Chém / Tấn Công (Màu Đỏ/Vàng)
    public void spawnSlash(float targetX, float targetY) {
        for (int i = 0; i < 25; i++) {
            float angle = MathUtils.random(-0.5f, 0.5f) + MathUtils.PI * 0.25f;
            float speed = MathUtils.random(150f, 350f);
            float vx = MathUtils.cos(angle) * speed;
            float vy = MathUtils.sin(angle) * speed;
            Color color = MathUtils.randomBoolean() ? Color.RED : Color.GOLD;

            particles.add(new SimpleParticle(
                targetX + MathUtils.random(-20f, 20f),
                targetY + MathUtils.random(-20f, 20f),
                vx, vy,
                MathUtils.random(4f, 10f),
                MathUtils.random(0.2f, 0.45f),
                color
            ));
        }
    }

    // Hiệu ứng Giáp / Phòng Thủ (Màu Xanh Dương/Xanh Lục)
    public void spawnShield(float targetX, float targetY) {
        for (int i = 0; i < 30; i++) {
            float vx = MathUtils.random(-80f, 80f);
            float vy = MathUtils.random(60f, 200f);
            Color color = MathUtils.randomBoolean() ? Color.CYAN : Color.SKY;

            particles.add(new SimpleParticle(
                targetX + MathUtils.random(-40f, 40f),
                targetY + MathUtils.random(-50f, -10f),
                vx, vy,
                MathUtils.random(3f, 8f),
                MathUtils.random(0.4f, 0.8f),
                color
            ));
        }
    }

    // Hiệu ứng Độc / Kỹ Năng (Màu Xanh Lá Mạ)
    public void spawnPoison(float targetX, float targetY) {
        for (int i = 0; i < 25; i++) {
            float vx = MathUtils.random(-50f, 50f);
            float vy = MathUtils.random(-30f, 120f);
            Color color = new Color(0.2f, 0.9f, 0.2f, 0.8f);

            particles.add(new SimpleParticle(
                targetX + MathUtils.random(-30f, 30f),
                targetY + MathUtils.random(-30f, 30f),
                vx, vy,
                MathUtils.random(5f, 12f),
                MathUtils.random(0.5f, 1.0f),
                color
            ));
        }
    }

    // Hiệu ứng Hóa Ma / Demon Form (Lửa địa ngục đỏ tím cuồn cuộn bốc lên)
    public void spawnDemonForm(float targetX, float targetY) {
        for (int i = 0; i < 65; i++) {
            float vx = MathUtils.random(-70f, 70f);
            float vy = MathUtils.random(120f, 320f);
            Color color = MathUtils.randomBoolean(0.6f) 
                ? (MathUtils.randomBoolean() ? new Color(1f, 0.15f, 0.05f, 1f) : new Color(0.9f, 0.45f, 0.1f, 1f))
                : new Color(0.7f, 0.1f, 0.85f, 1f);

            particles.add(new SimpleParticle(
                targetX + MathUtils.random(-45f, 45f),
                targetY + MathUtils.random(-60f, -10f),
                vx, vy,
                MathUtils.random(6f, 15f),
                MathUtils.random(0.6f, 1.2f),
                color
            ));
        }
    }

    // Hiệu ứng Bất Khả Xâm Phạm / Impervious (Vụ nổ kim cương vàng ngọc lam cực đại)
    public void spawnImpervious(float targetX, float targetY) {
        for (int i = 0; i < 55; i++) {
            float angle = MathUtils.random(0f, MathUtils.PI2);
            float speed = MathUtils.random(100f, 260f);
            float vx = MathUtils.cos(angle) * speed;
            float vy = MathUtils.sin(angle) * speed;
            Color color = MathUtils.randomBoolean(0.5f) ? Color.GOLD : (MathUtils.randomBoolean() ? Color.CYAN : Color.WHITE);

            particles.add(new SimpleParticle(
                targetX, targetY,
                vx, vy,
                MathUtils.random(5f, 12f),
                MathUtils.random(0.5f, 0.9f),
                color
            ));
        }
    }

    // Hiệu ứng Trọng Kiếm / Heavy Blade (Bão tia lửa chém cực mạnh)
    public void spawnHeavyBlade(float targetX, float targetY) {
        for (int i = 0; i < 45; i++) {
            float angle = MathUtils.random(-1.2f, -0.2f);
            float speed = MathUtils.random(250f, 450f);
            float vx = MathUtils.cos(angle) * speed;
            float vy = MathUtils.sin(angle) * speed;
            Color color = MathUtils.randomBoolean() ? Color.ORANGE : Color.YELLOW;

            particles.add(new SimpleParticle(
                targetX + MathUtils.random(-30f, 30f),
                targetY + MathUtils.random(-30f, 30f),
                vx, vy,
                MathUtils.random(6f, 14f),
                MathUtils.random(0.25f, 0.5f),
                color
            ));
        }
    }

    // Hiệu ứng Thuốc Trợ Chiến bùng nổ (Bọt khí ma thuật tỏa sáng)
    public void spawnPotionBurst(float targetX, float targetY, Color potionColor) {
        for (int i = 0; i < 35; i++) {
            float angle = MathUtils.random(0f, MathUtils.PI2);
            float speed = MathUtils.random(80f, 220f);
            float vx = MathUtils.cos(angle) * speed;
            float vy = MathUtils.sin(angle) * speed;

            particles.add(new SimpleParticle(
                targetX + MathUtils.random(-15f, 15f),
                targetY + MathUtils.random(-15f, 15f),
                vx, vy,
                MathUtils.random(5f, 11f),
                MathUtils.random(0.4f, 0.75f),
                potionColor
            ));
        }
    }

    // Hiệu ứng Tàn Lửa Bay / Hạt ma thuật nền lơ lửng (Ambient floating embers)
    public void updateAmbient(float delta, float screenWidth, float screenHeight, Color emberColor, int spawnPerSec) {
        float chance = spawnPerSec * delta;
        while (chance >= 1f || MathUtils.random() < chance) {
            chance -= 1f;
            float startX = MathUtils.random(0f, screenWidth);
            float startY = MathUtils.random(-10f, screenHeight * 0.4f);
            float vx = MathUtils.random(-25f, 25f);
            float vy = MathUtils.random(35f, 95f);
            float size = MathUtils.random(2.5f, 6.5f);
            float life = MathUtils.random(2.5f, 5.0f);

            Color c = new Color(emberColor.r, emberColor.g, emberColor.b, MathUtils.random(0.4f, 0.85f));
            if (MathUtils.randomBoolean(0.3f)) {
                c.set(Color.GOLD);
                c.a = MathUtils.random(0.5f, 0.9f);
            }

            particles.add(new SimpleParticle(startX, startY, vx, vy, size, life, c));
        }
    }

    // Hiệu ứng Nổ Va Chạm (Impact burst)
    public void spawnImpactBurst(float targetX, float targetY, Color color, int count) {
        for (int i = 0; i < count; i++) {
            float angle = MathUtils.random(0f, MathUtils.PI2);
            float speed = MathUtils.random(120f, 320f);
            float vx = MathUtils.cos(angle) * speed;
            float vy = MathUtils.sin(angle) * speed;

            particles.add(new SimpleParticle(
                targetX, targetY,
                vx, vy,
                MathUtils.random(4f, 9f),
                MathUtils.random(0.2f, 0.5f),
                color
            ));
        }
    }

    // Hiệu ứng Lấp Lánh Vàng (Gold sparkles)
    public void spawnGoldSparkles(float targetX, float targetY) {
        for (int i = 0; i < 20; i++) {
            float vx = MathUtils.random(-60f, 60f);
            float vy = MathUtils.random(40f, 160f);
            particles.add(new SimpleParticle(
                targetX + MathUtils.random(-20f, 20f),
                targetY + MathUtils.random(-20f, 20f),
                vx, vy,
                MathUtils.random(3f, 7f),
                MathUtils.random(0.4f, 0.8f),
                Color.GOLD
            ));
        }
    }

    public void clear() {
        particles.clear();
    }

    public void update(float delta) {
        Iterator<SimpleParticle> it = particles.iterator();
        while (it.hasNext()) {
            SimpleParticle p = it.next();
            if (p.update(delta)) {
                it.remove();
            }
        }
    }

    public void draw(SpriteBatch batch) {
        for (SimpleParticle p : particles) {
            batch.setColor(p.color);
            pixelDrawable.draw(batch, p.x - p.size / 2f, p.y - p.size / 2f, p.size, p.size);
        }
        batch.setColor(Color.WHITE); // Reset màu batch về mặc định
    }
}
