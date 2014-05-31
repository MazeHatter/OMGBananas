package com.monadpad.omgbananas;

import android.content.Context;
import android.media.SoundPool;

public class SamplerChannel extends DrumChannel {

    public SamplerChannel(Context context, SoundPool pool, MonadJam jam) {
        super(context, pool, jam);

        isAScale = false;
        highNote = 7;
        lowNote = 0;

        mCaptions = new String[] {
                "bongo l", "bongo h", "click l", "click h", "shhk", "scrape", "woop", "chimes"

        };
    }

    public void loadPool() {
        ids = new int[8];

        ids[0] = mPool.load(context, R.raw.sampler_8_bongol, 1);
        ids[1] = mPool.load(context, R.raw.sampler_7_bongoh, 1);
        ids[2] = mPool.load(context, R.raw.sampler_1_click, 1);
        ids[3] = mPool.load(context, R.raw.sampler_2_click, 1);
        ids[4] = mPool.load(context, R.raw.sampler_6_shhk, 1);
        ids[5] = mPool.load(context, R.raw.sampler_4_scrape, 1);
        ids[6] = mPool.load(context, R.raw.sampler_5_whoop, 1);
        ids[7] = mPool.load(context, R.raw.sampler_3_chimes, 1);

    }
}
