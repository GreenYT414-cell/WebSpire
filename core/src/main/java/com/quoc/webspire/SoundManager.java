package com.quoc.webspire;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private static SoundManager instance;
    private Music currentBgm;
    private Map<String, Sound> sounds = new HashMap<>();

    private SoundManager() {}

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public void playBgm(String fileName) {
        if (currentBgm != null && currentBgm.isPlaying()) {
            currentBgm.stop();
        }

        if (Gdx.files.internal(fileName).exists()) {
            try {
                currentBgm = Gdx.audio.newMusic(Gdx.files.internal(fileName));
                currentBgm.setLooping(true);
                currentBgm.setVolume(0.5f);
                currentBgm.play();
            } catch (Exception e) {
                Gdx.app.error("SoundManager", "Không thể nạp file BGM: " + fileName, e);
            }
        }
    }

    public void stopBgm() {
        if (currentBgm != null) {
            currentBgm.stop();
            currentBgm.dispose();
            currentBgm = null;
        }
    }

    public void playSound(String fileName) {
        if (!sounds.containsKey(fileName)) {
            if (Gdx.files.internal(fileName).exists()) {
                try {
                    Sound sound = Gdx.audio.newSound(Gdx.files.internal(fileName));
                    sounds.put(fileName, sound);
                } catch (Exception e) {
                    Gdx.app.error("SoundManager", "Lỗi định dạng tệp SFX (Cần convert sang chuẩn WAV/OGG): " + fileName);
                    sounds.put(fileName, null); // Đánh dấu null để không nạp lại file lỗi liên tục
                }
            } else {
                return;
            }
        }

        Sound sound = sounds.get(fileName);
        if (sound != null) {
            try {
                sound.play(0.8f);
            } catch (Exception e) {
                Gdx.app.error("SoundManager", "Lỗi phát SFX: " + fileName, e);
            }
        }
    }

    public void dispose() {
        stopBgm();
        for (Sound sound : sounds.values()) {
            if (sound != null) {
                sound.dispose();
            }
        }
        sounds.clear();
    }
}
