package com.monadpad.omgbananas;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JamLoader {

    private Jam mJam;

    public JamLoader(Jam jam) {
        mJam = jam;
    }

    public boolean loadData(String data) {

        boolean good = false;
        try {

            JSONObject jsonData = new JSONObject(data);

            JSONArray parts;
            if (jsonData.has("parts")) {
                Log.d("MGH loading parts", data);
                parts = jsonData.getJSONArray("parts");
            } else {
                Log.d("MGH loading data", data);
                parts = jsonData.getJSONArray("data");
            }

            if (jsonData.has("subbeatMillis")) {
                mJam.setSubbeatLength(jsonData.getInt("subbeatMillis"));
            }

            if (jsonData.has("rootNote")) {
                mJam.setKey(jsonData.getInt("rootNote") % 12);
            }

            if (jsonData.has("scale")) {
                mJam.setScale(jsonData.getString("scale"));
            }


            for (int ip = 0; ip < parts.length(); ip++) {
                JSONObject part = parts.getJSONObject(ip);
                String type = part.getString("type");

                if ("DRUMBEAT".equals(type)) {

                    if ("PRESET_PERCUSSION_SAMPLER".equals(part.getString("kit"))) {
                        loadDrums(mJam.getSamplerChannel(), part);
                    } else {
                        loadDrums(mJam.getDrumChannel(), part);

                    }
                } else if ("MELODY".equals(type)) {
                    if ("PRESET_SYNTH1".equals(part.getString("sound"))) {
                        loadMelody(mJam.getSynthChannel(), part);
                    } else if ("PRESET_GUITAR1".equals(part.getString("sound"))) {
                        loadMelody(mJam.getGuitarChannel(), part);

                    }
                } else if ("BASSLINE".equals(type)) {
                    loadMelody(mJam.getBassChannel(), part);

                } else if ("CHORDPROGRESSION".equals(type)) {
                    JSONArray chordsData = part.getJSONArray("data");
                    int[] newChords = new int[chordsData.length()];
                    for (int ic = 0; ic < chordsData.length(); ic++) {
                        newChords[ic] = chordsData.getInt(ic);
                    }
                    mJam.setChordProgression(newChords);

                }

            }

            mJam.onNewLoop();

            good = true;

        } catch (JSONException e) {
            Log.d("MGH loaddata exception", e.getMessage());
            e.printStackTrace();
        }

        return good;
    }

    private void loadDrums(DrumChannel jamChannel, JSONObject part) throws JSONException {

        //todo    drumset = jsonData.getInt("kit");


        JSONArray channels = part.getJSONArray("data");
        JSONObject channel;
        JSONArray channelData;

        boolean[][] pattern = jamChannel.pattern;

        //underrun overrun?
        //match the right channels?
        // this assumes things are in the right order

        for (int i = 0; i < channels.length(); i++) {
            channel = channels.getJSONObject(i);
            channelData = channel.getJSONArray("data");

            Log.d("MGH loadding drum channel", Integer.toString(i));

            for (int j = 0; j < channelData.length(); j++) {
                pattern[i][j] = channelData.getInt(j) == 1;
            }

        }

    }

    private void loadMelody(Channel channel, JSONObject part) throws JSONException {

        NoteList notes = new NoteList();
        channel.setBasicMelody(notes);
        double playedBeats = 0.0d;

        JSONArray notesData = part.getJSONArray("notes");

        Note newNote;
        JSONObject noteData;

        for (int i = 0; i < notesData.length(); i++) {
            noteData = notesData.getJSONObject(i);

            newNote = new Note();
            newNote.setBeats(noteData.getDouble("beats"));
            newNote.setBeatPosition(playedBeats);
            playedBeats += newNote.getBeats();
            newNote.setRest(noteData.getBoolean("rest"));

            if (!newNote.isRest()) {
                newNote.setNote(noteData.getInt("note"));

            }
            notes.add(newNote);
        }

    }
}