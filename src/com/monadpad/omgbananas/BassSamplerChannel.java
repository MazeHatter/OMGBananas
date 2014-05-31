package com.monadpad.omgbananas;

import android.content.Context;
import android.media.SoundPool;
import android.util.Log;

/**
 * User: m
 * Date: 11/15/13
 * Time: 1:56 PM
 */
public class BassSamplerChannel extends Channel {


    public BassSamplerChannel(Context context, SoundPool pool) {
        super(context, pool);

        highNote = 48;
        lowNote = 28;

        volume = 0.8f;
        ids = new int[21];
    }

    public int loadPool() {
        int i= 0;

        ids[i++] = mPool.load(context, R.raw.bass_e, 1);
        ids[i++] = mPool.load(context, R.raw.bass_f, 1);
        ids[i++] = mPool.load(context, R.raw.bass_fs, 1);
        ids[i++] = mPool.load(context, R.raw.bass_g, 1);
        ids[i++] = mPool.load(context, R.raw.bass_gs, 1);
        ids[i++] = mPool.load(context, R.raw.bass_a, 1);
        ids[i++] = mPool.load(context, R.raw.bass_bf, 1);
        ids[i++] = mPool.load(context, R.raw.bass_b, 1);
        ids[i++] = mPool.load(context, R.raw.bass_c, 1);
        ids[i++] = mPool.load(context, R.raw.bass_cs, 1);
        ids[i++] = mPool.load(context, R.raw.bass_d, 1);
        ids[i++] = mPool.load(context, R.raw.bass_ds, 1);
        ids[i++] = mPool.load(context, R.raw.bass_e2, 1);
        ids[i++] = mPool.load(context, R.raw.bass_f2, 1);
        ids[i++] = mPool.load(context, R.raw.bass_fs2, 1);
        ids[i++] = mPool.load(context, R.raw.bass_g2, 1);
        ids[i++] = mPool.load(context, R.raw.bass_gs2, 1);
        ids[i++] = mPool.load(context, R.raw.bass_a2, 1);
        ids[i++] = mPool.load(context, R.raw.bass_bf2, 1);
        ids[i++] = mPool.load(context, R.raw.bass_b2, 1);
        ids[i++] = mPool.load(context, R.raw.bass_c2, 1);

        return ids.length;
    }

}
