package com.monadpad.omgbananas;

import android.os.Process;

import com.monadpad.omgbananas.dsp.Dac;

import java.util.ArrayList;

/**
 * User: m
 * Date: 11/15/13
 * Time: 1:37 PM
 */
public class DialpadThread extends Thread {

    private ArrayList<Dac> mDacs;

    public DialpadThread(ArrayList<Dac> dacs) {

        mDacs = dacs;

    }

    public void run() {

        int i;
        Dac dac;

        Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
        //setPriority();
//        setPriority(Process.THREAD_PRIORITY_AUDIO);

        while (!isInterrupted()) {
            for (i = 0; i < mDacs.size(); i++) {
                dac = mDacs.get(i);

                dac.tick();
            }
        }


    }


}
