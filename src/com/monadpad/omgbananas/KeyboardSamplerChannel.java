package com.monadpad.omgbananas;

import android.content.Context;
import android.media.SoundPool;

/**
 * User: m
 * Date: 11/15/13
 * Time: 1:56 PM
 */
public class KeyboardSamplerChannel extends Channel {


    public KeyboardSamplerChannel(Context context, SoundPool pool) {
        super(context, pool);

        highNote = 69;
        lowNote = 9;

        volume = 0.2f;
        ids = new int[61];
    }

    public int loadPool() {
        int i= 0;

        ids[i++] = mPool.load(context, R.raw.kb1_a1, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_bf1, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_b1, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_c2, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_cs2, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_d2, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_ds2, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_e2, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_f2, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_fs2, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_g2, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_gs2, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_a2, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_bf2, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_b2, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_c3, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_cs3, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_d3, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_ds3, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_e3, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_f3, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_fs3, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_g3, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_gs3, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_a3, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_bf3, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_b3, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_c4, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_cs4, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_d4, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_ds4, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_e4, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_f4, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_fs4, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_g4, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_gs4, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_a4, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_bf4, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_b4, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_c5, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_cs5, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_d5, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_ds5, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_e5, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_f5, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_fs5, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_g5, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_gs5, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_a5, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_bf5, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_b5, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_c6, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_cs6, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_d6, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_ds6, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_e6, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_f6, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_fs6, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_g6, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_gs6, 1);
        ids[i++] = mPool.load(context, R.raw.kb1_a6, 1);

        return ids.length;
    }
}
