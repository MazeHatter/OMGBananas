package com.monadpad.omgbananas;

/**
 * User: m
 * Date: 11/15/13
 * Time: 2:06 PM
 */
public class Note {

    private double mBeatPosition;
    private double mBeats;
    private int noteNumber;

    private int instrumentNoteNumber;

    private boolean isrest = false;

    private boolean isplaying = false;

    public void setBeats(double beats) {

        mBeats = beats;

    }

    public void setRest(boolean value) {
        isrest = value;

    }

    public void setNote(int number) {
        noteNumber = number;
    }

    public int getNote() {
        return noteNumber;
    }

    public void setInstrumentNote(int number) {
        instrumentNoteNumber = number;
    }

    public int getInstrumentNote() {
        return instrumentNoteNumber;
    }

    public boolean isRest() {
        return isrest;
    }

    public double getBeats() {
        return mBeats;
    }


    public Note clone() {
        Note ret = new Note();
        ret.mBeats = mBeats;
        ret.isrest = isrest;
        ret.noteNumber = noteNumber;
        ret.mBeatPosition = mBeatPosition;

        return ret;
    }


    public void setBeatPosition(double pos) {
        mBeatPosition = pos;
    }

    public double getBeatPosition() {
        return mBeatPosition;
    }

    public boolean isPlaying() {
        return isplaying;
    }
    public void isPlaying(boolean value) {
        isplaying = value;
    }

}
