package com.monadpad.omgbananas;

import android.content.Context;

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
    protected NoteList mBasicMelody = new NoteList();

    protected int playingI = 0;

    protected OMGSoundPool mPool;

    protected int[] ids;
    protected int[] rids;

    protected int playingId = -1;

    protected Context context;

    protected float volume;

    protected int octave = 3;

    public Channel(Context context, OMGSoundPool pool) {
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

    public void setNotes(NoteList notes) {
        mNoteList = notes;

        for (Note note : notes) {
            /*int noteToPlay = note.getNote() - lowNote;
            while (noteToPlay < 0) {
                noteToPlay += 12;
            }
            while (noteToPlay >= highNote - lowNote) {
                noteToPlay -= 12;
            }
            note.setInstrumentNote(noteToPlay);
            */

            int noteToPlay = note.getNote() + 12 * octave;
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
        mNoteList.clear();
        state = STATE_LIVEPLAY;
    }

    public int getSoundCount() {
        return rids.length;
    }

    public int getOctave() {
        return octave;
    }

    public void setBasicMelody(NoteList noteList) {
        mBasicMelody = noteList;
    }

    public NoteList getBasicMelody() {
        return mBasicMelody;
    }
}
