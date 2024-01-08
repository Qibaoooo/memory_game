package nus.iss.sa57.team11;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.HashMap;
import java.util.Map;

enum GameSounds {
    GOOD_MATCH,
    START,
    WIN
}

public class GameSound {
    MediaPlayer mp;

    HashMap<GameSounds, Integer> soundMap;

    public GameSound() {
        this.soundMap = new HashMap<GameSounds, Integer>();
        this.soundMap.put(GameSounds.GOOD_MATCH, R.raw.game_good_match);
        this.soundMap.put(GameSounds.START, R.raw.game_start);
        this.soundMap.put(GameSounds.WIN, R.raw.game_win);
    }

    ;

    public void play(Context context, GameSounds sound) {
        try {
            if ((mp != null) && (mp.isPlaying())) {
                mp.stop();
                mp.release();
            }
            mp = MediaPlayer.create(context, this.soundMap.get(sound));
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
