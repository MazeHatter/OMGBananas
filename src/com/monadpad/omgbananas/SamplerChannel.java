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

    public void makeFill() {

        clearPattern();

        boolean[][] toms = new boolean[][] {pattern[0], pattern[1], pattern[2],
                                            pattern[3], pattern[4]};

        boolean sparse = rand.nextBoolean();
        boolean on;
        int tom;
        for (int i = 0; i < 16; i++) {

            on = (sparse && rand.nextBoolean()) ||
                    (!sparse && (rand.nextBoolean() || rand.nextBoolean()));
            tom = rand.nextInt(5);

            toms[tom][i] = on;
            toms[tom][i + 16] = on;
        }

    }


}
