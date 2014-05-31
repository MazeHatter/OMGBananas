package com.monadpad.omgbananas;

import android.content.Context;
import android.media.SoundPool;

import java.util.ArrayList;
import java.util.Random;

/**
 * User: m
 * Date: 11/15/13
 * Time: 1:56 PM
 */
public abstract class DrumChannel extends Channel {


    protected String[] mCaptions;
    protected String[] presetNames;

    protected boolean[][] pattern;

    protected float volume = 1.0f;

    protected int beats;
    protected int subbeats;

    protected Random rand;

    protected String kitName = "";

    public DrumChannel(Context context, SoundPool pool, MonadJam jam) {
        super(context, pool);

        rand = jam.getRand();

        isAScale = false;
        highNote = 7;
        lowNote = 0;

        mCaptions = new String[] {

        };

        beats = jam.getBeats();
        subbeats = jam.getSubbeats();
        pattern = new boolean[8][beats * subbeats];
    }

    public void playBeat(int subbeat) {
        if (enabled) {
            for (int i = 0; i < pattern.length; i++) {
                if (pattern[i][subbeat]) {
                    playingId = mPool.play(ids[i], volume, volume, 10, 0, 1);
                }
            }
        }

    }

    public String[] getCaptions() {
        return mCaptions;
    }


    public static boolean[] default_kick = new boolean[] {
            true, false, false, false,
            false, false, false, false,
            true, false, false, false,
            false, false, false, false,
            true, false, false, false,
            false, false, false, false,
            true, false, false, false,
            false, false, false, false,
    };
    public static boolean[] default_clap = new boolean[] {
            false, false, false, false,
            true, false, false, false,
            false, false, false, false,
            true, false, false, false,
            false, false, false, false,
            true, false, false, false,
            false, false, false, false,
            false, false, false, false,
    };
    public static boolean[] default_hithat = new boolean[] {
            true, false, false, false,
            true, false, false, false,
            true, false, false, false,
            true, false, true, false,
            true, false, false, false,
            true, false, false, false,
            true, false, false, false,
            true, true, true, true,
    };


    public void makeDrumBeatsFromMelody(ArrayList<Note> bassline) {

        boolean[] kick = pattern[0];
        boolean[] clap = pattern[1];
        double usedBeats = 0.0d;

        boolean snareCutTime = rand.nextBoolean();

        int ib = 0;

        for (Note note : bassline) {
            boolean usekick = false;
            boolean useclap = false;

            if ((snareCutTime && usedBeats % 2 == 1.0d) ||
                    (!snareCutTime && usedBeats % 4 == 2.0d)) {
                useclap = !note.isRest() || rand.nextBoolean();
            }
            else if ((snareCutTime && usedBeats % 2 == 0.0d) ||
                    (!snareCutTime && usedBeats % 4 == 0.0d)) {
                usekick = !note.isRest() || rand.nextBoolean();
            }
            else {
                usekick = !note.isRest() && rand.nextBoolean();
            }

            kick[ib] = usekick;
            clap[ib] = useclap;

            for (ib = ib + 1; ib < (usedBeats  + note.getBeats()) * subbeats; ib++) {
                kick[ib] = rand.nextBoolean() && ((snareCutTime && ib % (2 * subbeats) == 0) ||
                        (!snareCutTime && ib % (4 * subbeats) == 0));
                clap[ib] = (snareCutTime && ib % (2 * subbeats) == subbeats) ||
                        (!snareCutTime && ib % (4 * subbeats) == (2 * subbeats));
            }

            usedBeats += note.getBeats();
        }

        for (ib = ib + 1; ib < (beats - usedBeats) * subbeats; ib++) {
            kick[ib] = rand.nextBoolean() && ((snareCutTime && ib % (2 * subbeats) == 0) ||
                    (!snareCutTime && ib % (4 * subbeats) == 0));
            clap[ib] = rand.nextBoolean() && ((snareCutTime && ib % (2 * subbeats) == subbeats) ||
                    (!snareCutTime && ib % (4 * subbeats) == (2 * subbeats)));
        }

        makeHiHatBeats(false);
    }


    public void makeHiHatBeats(boolean defaultPattern) {

        boolean[] hihat = pattern[2];

        if (defaultPattern) {
            for (int i = 0; i < hihat.length; i++) {
                hihat[i] = default_hithat[i];
            }
            return;
        }

        int pattern = rand.nextInt(5);

//        hihat = new boolean[beats * subbeats];
        for (int i = 0; i < hihat.length; i++) {
            hihat[i] = pattern == 0 ?  i % subbeats == 0 :
                    pattern == 1 ? i % 2 == 0 :
                            pattern == 2 ? rand.nextBoolean() :
                                    rand.nextBoolean() || rand.nextBoolean();
        }
    }

    public void makeKickBeats(boolean defaultPattern) {

        boolean[] kick = pattern[0];
        if (defaultPattern) {
            for (int i = 0; i < kick.length; i++) {
                kick[i] = default_kick[i];
            }
            return;
        }

        int pattern = rand.nextInt(5);

        for (int i = 0; i < kick.length; i++) {
            kick[i] = pattern == 0 ? i % subbeats == 0 :
                    pattern == 1 ? i % 8 == 0 :
                            (i == 0 || i == 8 || i == 16 ||
                                    (rand.nextBoolean() && rand.nextBoolean())); //rand.nextBoolean();
        }
    }
    public void makeClapBeats(boolean defaultPattern) {

        boolean[] clap = pattern[1];
        if (defaultPattern) {
            for (int i = 0; i < clap.length; i++) {
                clap[i] = default_clap[i];
            }
            return;
        }

        int pattern = rand.nextInt(10);

//        clap = new boolean[beats * subbeats];
        for (int i = 0; i < clap.length; i++) {
            clap[i] = pattern != 0 && (
                    pattern == 1 ? i == 4 || i == 12 || i == 20 || i == 28 :
                            pattern == 2 ? i == 4 || i == 12 || i == 13 || i == 20 :
                                    i == 4 || i == 12 || i == 20);

        }

    }

    public boolean[] getTrack(int track)  {
        return pattern[track];
    }

    public void makeDrumBeats() {
        makeKickBeats(false);
        makeClapBeats(false);
        makeHiHatBeats(false);

    }


    public void getData(StringBuilder sb) {

        int totalBeats = beats * subbeats;
        sb.append("{\"type\" : \"DRUMBEAT\", \"kit\": ");
        sb.append(kitName);

        sb.append(", \"data\": [");

        for (int p = 0; p < pattern.length; p++) {
            sb.append("{\"name\": \"");
            sb.append(mCaptions[p]);
            sb.append("\", \"sound\": \"");
            sb.append(presetNames[p]);
            sb.append("\", \"data\": [");
            for (int i = 0; i < totalBeats; i++) {
                sb.append(pattern[p][i] ?1:0) ;
                if (i < totalBeats - 1)
                    sb.append(",");
            }
            sb.append("]}");

            if (p < pattern.length - 1)
                sb.append(",");

        }

        sb.append("]}");

    }

}

