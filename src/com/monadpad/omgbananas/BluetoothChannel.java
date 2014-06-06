package com.monadpad.omgbananas;

import android.content.Context;

/**
 * User: m
 * Date: 11/15/13
 * Time: 1:56 PM
 */
public class BluetoothChannel extends Channel {

    private BluetoothConnection mConnection;

    public BluetoothChannel(Context context, OMGSoundPool pool,
                            BluetoothConnection connection) {
        super(context, pool);
        mConnection = connection;

        highNote = 85;
        lowNote = 40;


        volume = 0.15f;

    }

    @Override
    public void playNote(Note note) {
        mConnection.writeString("CHANNEL_PLAY_NOTE=" + note.getInstrumentNote() + ";");
    }
}
