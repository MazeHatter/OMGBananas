package com.monadpad.omgbananas;

import android.content.Context;
import android.media.SoundPool;

public class RockDrumChannel extends DrumChannel {

    public RockDrumChannel(Context context, SoundPool pool, MonadJam jam) {
        super(context, pool, jam);

        isAScale = false;
        highNote = 7;
        lowNote = 0;

        presetNames = new String[]{"PRESET_ROCK_KICK",
                "PRESET_ROCK_SNARE",
                "PRESET_ROCK_HIHAT_MED",
                "PRESET_ROCK_HIHAT_OPEN",
                "PRESET_ROCK_CRASH",
                "PRESET_ROCK_TOM_MH",
                "PRESET_ROCK_TOM_ML",
                "PRESET_ROCK_TOM_L"};

        mCaptions = new String[] {"kick", "snare", "hi-hat", "open hi-hat",
                "crash", "h tom", "m tom", "l tom"};

        kitName = "PRESET_ROCKKIT";
    }

    public void loadPool() {
        ids = new int[8];
        int i= 0;

        ids[i++] = mPool.load(context, R.raw.rock_kick, 1);
        ids[i++] = mPool.load(context, R.raw.rock_snare, 1);
        ids[i++] = mPool.load(context, R.raw.rock_hithat_med, 1);
        ids[i++] = mPool.load(context, R.raw.rock_hihat_open, 1);
        ids[i++] = mPool.load(context, R.raw.rock_crash, 1);
        ids[i++] = mPool.load(context, R.raw.rock_tom_mh, 1);
        ids[i++] = mPool.load(context, R.raw.rock_tom_ml, 1);
        ids[i++] = mPool.load(context, R.raw.rock_tom_l, 1);

    }

}
