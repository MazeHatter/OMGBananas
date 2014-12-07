package com.monadpad.omgbananas;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

public abstract class Channel {

    public static int STATE_LIVEPLAY = 0;
    public static int STATE_PLAYBACK = 1;

    protected int state = 1;

    protected boolean enabled = true;

    private long finishAt;

    protected Note lastPlayedNote;
    protected int playingNoteNumber = -1;

    protected int highNote = 0;
    protected int lowNote = 0;

    protected boolean isAScale = true;

    protected NoteList mNoteList = new NoteList();
    //protected NoteList mBasicMelody = new NoteList();

    protected int playingI = 0;

    protected OMGSoundPool mPool;

    protected int[] ids;
    protected int[] rids;

    protected int playingId = -1;

    protected Context context;

    protected float volume;

    protected int octave = 3;

    protected Note recordingNote;
    protected int recordingStartedAtSubbeat;

    protected Jam mJam;

    protected double nextBeat = 0.0d;

    protected int subbeats;
    protected int mainbeats;
    protected int totalsubbeats;
    private double dsubbeats;

    ArrayList<DebugTouch> debugTouchData = new ArrayList<DebugTouch>();

    private String mType;
    private String mSound;
    private String mMainSound;

    public Channel(Context context, Jam jam, OMGSoundPool pool, String type, String sound) {
        mPool = pool;
        this.context = context;
        mJam = jam;

        mType = type;
        mMainSound = sound;
        mSound = sound;

        subbeats = mJam.getSubbeats();
        mainbeats = mJam.getBeats();
        totalsubbeats = subbeats * mainbeats;
        dsubbeats = (double)subbeats;
    }

    public void playLiveNote(Note note) {

        playNote(note);

        if (!mJam.isPlaying())
            return;

        if (note.isRest()) {
            stopRecording();
            state = STATE_PLAYBACK;
        }
        else {
            startRecordingNote(note);
            state = STATE_LIVEPLAY;
        }

    }

    public void playRecordedNote(Note note) {
        if (state == STATE_PLAYBACK) {
            playNote(note);
        }
    }

    public void playNote(Note note) {

        if (lastPlayedNote != null)
            lastPlayedNote.isPlaying(false);

        if (!enabled)
            return;


        playingNoteNumber = note.getScaledNote();

        note.isPlaying(true);
        lastPlayedNote = note;

        if (playingId > -1) {
            mPool.pause(playingId);
            mPool.stop(playingId);
            playingId = -1;
        }

        finishCurrentNoteAt(-1);

        if (!note.isRest()) {
            int noteToPlay = note.getInstrumentNote();
            Log.d("MGH noteToPlay", Integer.toString(noteToPlay));

            playingId = mPool.play(ids[noteToPlay], volume, volume, 10, 0, 1);

        }

    }

    public void toggleEnabled() {
        if (enabled) {
            disable();
        }
        else {
            enable();
        }
    }

    public void disable() {
        enabled = false;
        mute();
    }

    public void enable() {
        enabled = true;
    }

    public void finishCurrentNoteAt(long time) {
        finishAt = time;
    }

    public long getFinishAt() {
        return finishAt;
    }

    public void mute() {

        if (playingId > -1) {
            mPool.pause(playingId);
            mPool.stop(playingId);
            playingId = -1;
        }

    }

    public int loadPool() {
        ids = new int[rids.length];
        for (int i = 0; i < rids.length; i++) {

            ids[i] = mPool.load(context, rids[i], 1);

            if (mPool.isCanceled())
                return -1;

        }
        return ids.length;
    }


    public int getHighNote() {
        return highNote;
    }

    public int getLowNote() {
        return lowNote;
    }

    public boolean isAScale() {
        return isAScale;
    }

    public int getPlayingNoteNumber() {
        return playingNoteNumber;
    }

    public NoteList getNotes() {
        return mNoteList;
    }


    public void fitNotesToInstrument() {

        for (Note note : mNoteList) {

            int noteToPlay = note.getScaledNote() + 12 * octave;
            while (noteToPlay < lowNote) {
                noteToPlay += 12;
            }
            while (noteToPlay > highNote) {
                noteToPlay -= 12;
            }

            note.setInstrumentNote(noteToPlay - lowNote);
        }

        state = STATE_PLAYBACK;
    }

    public double getNextBeat() {
        return nextBeat;
    }

    public void setNextBeat(double beats) {
        nextBeat = beats;
        playingI++;
    }

    public int getI() {
        return playingI;
    }

    public void resetI() {
        nextBeat = 0.0d;
        playingI = 0;

        if (recordingNote == null)
            state = STATE_PLAYBACK;
    }

    public int getState() {
        return state;
    }

    public void clearNotes() {
        mNoteList.clear();
        state = STATE_LIVEPLAY;
    }

    public int getSoundCount() {
        return rids.length;
    }

    public int getOctave() {
        return octave;
    }


    public void startRecordingNote(Note note) {

        if (recordingNote != null) {
            stopRecording();
        }

        DebugTouch debugTouch = new DebugTouch();
        debugTouch.mode = "START";
        debugTouchData.add(debugTouch);

        recordingStartedAtSubbeat = mJam.getClosestSubbeat(debugTouch);

        recordingNote = note;

    }

    public void stopRecording() {

        if (recordingNote == null)
            return;

        DebugTouch debugTouch = new DebugTouch();
        debugTouch.mode = "STOP";
        debugTouchData.add(debugTouch);

        int nowSubbeat = mJam.getClosestSubbeat(debugTouch);

        if (nowSubbeat < recordingStartedAtSubbeat) {
            nowSubbeat += totalsubbeats;
        }
        if (nowSubbeat - recordingStartedAtSubbeat < 2) {
            nowSubbeat = recordingStartedAtSubbeat + 2;
        }

        double beats = (nowSubbeat - recordingStartedAtSubbeat) / dsubbeats;
        double startBeat = recordingStartedAtSubbeat / dsubbeats;

        recordingNote.setBeats(beats);

        mNoteList.overwrite(recordingNote, startBeat);

        recordingNote = null;
        recordingStartedAtSubbeat = -1;
    }

    public float getVolume() {
        return volume;
    }

    public String getSoundName() {
        return mSound;
    }
    public String getType() {
        return mType;
    }

    public String getMainSound() {
        return mMainSound;
    }
}
