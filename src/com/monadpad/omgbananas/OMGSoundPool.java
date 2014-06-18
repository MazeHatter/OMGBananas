package com.monadpad.omgbananas;

import android.media.SoundPool;

/**
 * Created by m on 6/4/14.
 */
public class OMGSoundPool extends SoundPool {

    private boolean isLoaded = false;
    private boolean cancelLoading = false;

    public OMGSoundPool(int i1, int i2, int i3) {
        super(i1, i2, i3);
    }

    public void cancelLoading() {
        cancelLoading = true;
    }

    public void allowLoading() {
        cancelLoading = false;
    }

    public boolean isCanceled() {
        return cancelLoading;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean value) {
        isLoaded = value;
    }
}
