package com.monadpad.omgbananas;

import android.content.Context;
import android.media.SoundPool;

public class SamplerChannel extends DrumChannel {

    public SamplerChannel(Context context, SoundPool pool, Jam jam) {
        super(context, pool, jam);

        isAScale = false;
        highNote = 7;
        lowNote = 0;

        mCaptions = new String[] {
                "bongo l", "bongo h", "click l", "click h", "shhk", "scrape", "woop", "chimes"
        };
        presetNames = new String[] {
                "PRESET_bongol", "PRESET_bongoh", "PRESET_clickl", "PRESET_clickh",
                "PRESET_shhk", "PRESET_scrape", "PRESET_woop", "PRESET_chimes"
        };
        ids = new int[8];

        kitName = "PRESET_PERCUSSION_SAMPLER";
    }

    public int loadPool() {

        ids[0] = mPool.load(context, R.raw.sampler_8_bongol, 1);
        ids[1] = mPool.load(context, R.raw.sampler_7_bongoh, 1);
        ids[2] = mPool.load(context, R.raw.sampler_1_click, 1);
        ids[3] = mPool.load(context, R.raw.sampler_2_click, 1);
        ids[4] = mPool.load(context, R.raw.sampler_6_shhk, 1);
        ids[5] = mPool.load(context, R.raw.sampler_4_scrape, 1);
        ids[6] = mPool.load(context, R.raw.sampler_5_whoop, 1);
        ids[7] = mPool.load(context, R.raw.sampler_3_chimes, 1);

        return ids.length;
    }

    public void makeFill() {

        clearPattern();

        int fillLevel = rand.nextInt(4);

        if (fillLevel == 0)
            return;

        boolean[][] toms = new boolean[][] {pattern[0], pattern[1], pattern[2],
                                            pattern[3], pattern[4]};
        boolean on;
        int tom;
        for (int i = 0; i < 16; i++) {

            on = (fillLevel == 1 && (rand.nextBoolean() && rand.nextBoolean())) ||
                 (fillLevel == 2 && rand.nextBoolean()) ||
                 (fillLevel == 3 && (rand.nextBoolean() || rand.nextBoolean()));
            tom = rand.nextInt(5);

            toms[tom][i] = on;
            toms[tom][i + 16] = on;
        }

    }


}
