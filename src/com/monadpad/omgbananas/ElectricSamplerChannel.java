package com.monadpad.omgbananas;

import android.content.Context;
import android.media.SoundPool;

/**
 * User: m
 * Date: 11/15/13
 * Time: 1:56 PM
 */
public class ElectricSamplerChannel extends Channel {


    public ElectricSamplerChannel(Context context, SoundPool pool) {
        super(context, pool);

        highNote = 85;
        lowNote = 40;

        volume = 0.15f;
        ids = new int[46];

    }

    public int loadPool() {
        int i= 0;

        ids[i++] = mPool.load(context, R.raw.electric_e, 1);
        ids[i++] = mPool.load(context, R.raw.electric_f, 1);
        ids[i++] = mPool.load(context, R.raw.electric_fs, 1);
        ids[i++] = mPool.load(context, R.raw.electric_g, 1);
        ids[i++] = mPool.load(context, R.raw.electric_gs, 1);
        ids[i++] = mPool.load(context, R.raw.electric_a, 1);
        ids[i++] = mPool.load(context, R.raw.electric_bf, 1);
        ids[i++] = mPool.load(context, R.raw.electric_b, 1);
        ids[i++] = mPool.load(context, R.raw.electric_c, 1);
        ids[i++] = mPool.load(context, R.raw.electric_cs, 1);
        ids[i++] = mPool.load(context, R.raw.electric_d, 1);
        ids[i++] = mPool.load(context, R.raw.electric_ds, 1);
        ids[i++] = mPool.load(context, R.raw.electric_e2, 1);
        ids[i++] = mPool.load(context, R.raw.electric_f2, 1);
        ids[i++] = mPool.load(context, R.raw.electric_fs2, 1);
        ids[i++] = mPool.load(context, R.raw.electric_g2, 1);
        ids[i++] = mPool.load(context, R.raw.electric_gs2, 1);
        ids[i++] = mPool.load(context, R.raw.electric_a2, 1);
        ids[i++] = mPool.load(context, R.raw.electric_bf2, 1);
        ids[i++] = mPool.load(context, R.raw.electric_b2, 1);
        ids[i++] = mPool.load(context, R.raw.electric_c2, 1);
        ids[i++] = mPool.load(context, R.raw.electric_cs2, 1);
        ids[i++] = mPool.load(context, R.raw.electric_d2, 1);
        ids[i++] = mPool.load(context, R.raw.electric_ds2, 1);
        ids[i++] = mPool.load(context, R.raw.electric_e3, 1);
        ids[i++] = mPool.load(context, R.raw.electric_f3, 1);
        ids[i++] = mPool.load(context, R.raw.electric_fs3, 1);
        ids[i++] = mPool.load(context, R.raw.electric_g3, 1);
        ids[i++] = mPool.load(context, R.raw.electric_gs3, 1);
        ids[i++] = mPool.load(context, R.raw.electric_a3, 1);
        ids[i++] = mPool.load(context, R.raw.electric_bf3, 1);
        ids[i++] = mPool.load(context, R.raw.electric_b3, 1);
        ids[i++] = mPool.load(context, R.raw.electric_c3, 1);
        ids[i++] = mPool.load(context, R.raw.electric_cs3, 1);
        ids[i++] = mPool.load(context, R.raw.electric_d3, 1);
        ids[i++] = mPool.load(context, R.raw.electric_ds3, 1);
        ids[i++] = mPool.load(context, R.raw.electric_e4, 1);
        ids[i++] = mPool.load(context, R.raw.electric_f4, 1);
        ids[i++] = mPool.load(context, R.raw.electric_fs4, 1);
        ids[i++] = mPool.load(context, R.raw.electric_g4, 1);
        ids[i++] = mPool.load(context, R.raw.electric_gs4, 1);
        ids[i++] = mPool.load(context, R.raw.electric_a4, 1);
        ids[i++] = mPool.load(context, R.raw.electric_bf4, 1);
        ids[i++] = mPool.load(context, R.raw.electric_b4, 1);
        ids[i++] = mPool.load(context, R.raw.electric_c4, 1);
        ids[i++] = mPool.load(context, R.raw.electric_cs4, 1);

        return ids.length;
    }

}
