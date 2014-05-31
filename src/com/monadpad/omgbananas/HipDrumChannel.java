package com.monadpad.omgbananas;

import android.content.Context;
import android.media.SoundPool;

public class HipDrumChannel extends DrumChannel {

    public void loadPool() {
        ids = new int[8];
        int i= 0;

        ids[i++] = mPool.load(context, R.raw.hh_kick, 1);
        ids[i++] = mPool.load(context, R.raw.hh_clap, 1);
        ids[i++] = mPool.load(context, R.raw.rock_hithat_closed, 1);
        ids[i++] = mPool.load(context, R.raw.hh_hihat, 1);
        ids[i++] = mPool.load(context, R.raw.hh_tamb, 1);
        ids[i++] = mPool.load(context, R.raw.hh_tom_mh, 1);
        ids[i++] = mPool.load(context, R.raw.hh_tom_ml, 1);
        ids[i++] = mPool.load(context, R.raw.hh_tom_l, 1);

    }

    public HipDrumChannel(Context context, SoundPool pool, MonadJam jam) {
        super(context, pool, jam);

        isAScale = false;
        highNote = 7;
        lowNote = 0;

        presetNames = new String[] {
                "PRESET_HH_KICK",
                "PRESET_HH_CLAP",
                "PRESET_ROCK_HIHAT_CLOSED",
                "PRESET_HH_HIHAT",
                "PRESET_HH_TAMB",
                "PRESET_HH_TOM_MH",
                "PRESET_HH_TOM_ML",
                "PRESET_HH_TOM_L",
        };

        mCaptions = new String[] {"kick", "clap", "closed hi-hat", "open hi-hat",
            "tambourine", "h tom", "m tom", "l tom"};

        kitName = "PRESET_HIPKIT";
    }

}
