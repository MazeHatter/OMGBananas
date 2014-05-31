package com.monadpad.omgbananas;

import android.content.Context;
import android.media.SoundPool;

import java.util.ArrayList;

public abstract class Channel {

    public static int STATE_LIVEPLAY = 0;
    public static int STATE_PLAYBACK = 1;

    protected int state = 0;

    protected boolean enabled = true;

    private long finishAt;

    private Note lastPlayedNote;
    protected int playingNoteNumber = -1;

    protected int highNote = 0;
    protected int lowNote = 0;

    protected boolean isAScale = true;

    protected NoteList mNoteList = new NoteList();

    protected int playingI = 0;

    protected SoundPool mPool;

    protected int[] ids;

    protected int playingId = -1;

    protected Context context;

    protected float volume;

    public Channel(Context context, SoundPool pool) {
        mPool = pool;
        this.context = context;
    }

    public void playNote(Note note) {

        if (lastPlayedNote != null)
            lastPlayedNote.isPlaying(false);

        if (!enabled)
            return;


        playingNoteNumber = note.getNote();

        note.isPlaying(true);
        lastPlayedNote = note;

        if (playingId > -1) {
            mPool.stop(playingId);
            playingId = -1;
        }

        finishCurrentNoteAt(-1);

        if (!note.isRest()) {

            int noteToPlay = note.getInstrumentNote();
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
            mPool.stop(playingId);
            playingId = -1;
        }

    }

    abstract public int loadPool();


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

    public void setNotes(ArrayList<Note> notes) {
        mNoteList.list = notes;

        for (Note note : notes) {
            int noteToPlay = note.getNote() - lowNote;

            while (noteToPlay < 0) {
                noteToPlay += 12;
            }
            while (noteToPlay >= highNote - lowNote) {
                noteToPlay -= 12;
            }

            note.setInstrumentNote(noteToPlay);
        }

        state = STATE_PLAYBACK;
    }

    public void bumpI() {
        playingI++;
    }

    public int getI() {
        return playingI;
    }

    public void resetI() {
        playingI = 0;
    }

    public int getState() {
        return state;
    }

    public void clearNotes() {
        mNoteList.list.clear();
        state = STATE_LIVEPLAY;
    }

    public int getSoundCount() {
        return ids.length;
    }
}
