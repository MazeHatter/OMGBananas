package com.monadpad.omgbananas;

import android.content.Context;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Random;

public class Jam {

    private Random rand = new Random();

    private int subbeats = 4;
    private int beats = 8;
    private int totalsubbeats = subbeats * beats;
    private int subbeatLength = 125; //70 + rand.nextInt(125); // 125;

    private DrumChannel drumChannel;

    private Channel basslineChannel;

    private Channel keyboardChannel;
    private Channel guitarChannel;
    private SamplerChannel samplerChannel;

    private DialpadChannel dialpadChannel;

    private OMGSoundPool pool;

    private Context mContext;

    PlaybackThread playbackThread;

    boolean cancel = false;

    private MelodyMaker mm;

    private boolean chordsEnabled = true;

    private boolean playing = false;

    private int progressionI = -1;

    private ArrayList<View> viewsToInvalidateOnBeat = new ArrayList<View>();
    private ArrayList<View> viewsToInvalidateOnNewMeasure = new ArrayList<View>();

    private int currentChord = 0;

    private int soundsToLoad = 0;

    private boolean soundPoolInitialized = false;

    public Jam(Context context, OMGSoundPool pool) {

        this.pool = pool;

        mContext = context;
        mm = new MelodyMaker(mContext);
        mm.makeMotif();
        mm.makeMelodyFromMotif(beats);

        setDrumset(0);

    }

    public void makeChannels(final ProgressBar progressBar, final Runnable callback) {

        pool.allowLoading();
        if (progressBar != null) {
            progressBar.setProgress(0);
        }

        boolean usingListener = false;
        boolean updatePB = false;

        drumChannel = new HipDrumChannel(mContext, this, pool);
        basslineChannel = new BassSamplerChannel(mContext, this, pool,
                "BASSLINE", "PRESET_BASS");

        guitarChannel = new ElectricSamplerChannel(mContext, this, pool,
                "MELODY", "PRESET_GUITAR1");
        samplerChannel = new SamplerChannel(mContext, this, pool);
        keyboardChannel = new KeyboardSamplerChannel(mContext, this, pool,
                "MELODY", "PRESET_SYNTH1");

        dialpadChannel = new DialpadChannel(mContext, this, pool, "MELODY",
                new DialpadChannelSettings());

        soundsToLoad = drumChannel.getSoundCount() +
                basslineChannel.getSoundCount() +
                guitarChannel.getSoundCount() +
                samplerChannel.getSoundCount() +
                keyboardChannel.getSoundCount();

        if (Build.VERSION.SDK_INT >= 11 && progressBar != null) {
            usingListener = true;
            progressBar.setMax(soundsToLoad);
            pool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                int loadedSounds = 0;
                @Override
                public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                    loadedSounds++;
                    progressBar.incrementProgressBy(1);

                    if (loadedSounds == soundsToLoad) {

                        pool.setLoaded(true);

                        if (callback != null && !pool.isCanceled())
                            callback.run();

                    }
                }
            });
        }


        if (!usingListener) {
            updatePB = progressBar != null;
            if (updatePB)
                progressBar.setMax(5);
        }

        if (drumChannel.loadPool() == -1) return;
        if (updatePB) progressBar.incrementProgressBy(1);

        if (basslineChannel.loadPool() == -1) return;
        if (updatePB) progressBar.incrementProgressBy(1);

        if (guitarChannel.loadPool() == -1) return;
        if (updatePB) progressBar.incrementProgressBy(1);

        if (samplerChannel.loadPool() == -1) return;
        if (updatePB) progressBar.incrementProgressBy(1);

        if (keyboardChannel.loadPool() == -1) return;
        if (updatePB) progressBar.incrementProgressBy(1);

        if (!usingListener) {
            if (!pool.isCanceled())
                callback.run();
        }

        soundPoolInitialized = true;
    }



    public void playBeatSampler(int subbeat) {

        if (subbeat == 0) {
            basslineChannel.resetI();
            guitarChannel.resetI();
            keyboardChannel.resetI();
            dialpadChannel.resetI();

        }

        drumChannel.playBeat(subbeat);
        samplerChannel.playBeat(subbeat);

        double beat = subbeat / (double)subbeats;
        playChannelBeat(basslineChannel, beat);

        playChannelBeat(guitarChannel, beat);

        playChannelBeat(keyboardChannel, beat);

        playChannelBeat(dialpadChannel, beat);

    }

    public void makeChannelNotes(Channel channel) {

        if (rand.nextInt(5) == 0) {
            mm.makeMotif();
            mm.makeMelodyFromMotif(beats);
        }
        else if (rand.nextInt(3) == 0) {
            // keep the motif, but change melody
            mm.makeMelodyFromMotif(beats);
        }

        mm.cloneCurrentMelodyTo(channel.getNotes());

        mm.applyScale(channel, currentChord);
        channel.resetI();


    }


    public void kickIt() {

        if (!playing) {
            cancel = false;
            playbackThread = new PlaybackThread();
            playbackThread.start();
        }

    }

    public boolean  toggleMuteBassline() {
        basslineChannel.toggleEnabled();
        return basslineChannel.enabled;

    }
    public boolean  toggleMuteGuitar() {
        guitarChannel.toggleEnabled();
        return guitarChannel.enabled;
    }


    public boolean toggleMuteRhythm() {
        keyboardChannel.toggleEnabled();
        return keyboardChannel.enabled;
    }

    public boolean toggleMuteDrums() {
        drumChannel.toggleEnabled();
        return drumChannel.enabled;
    }

    public boolean toggleMuteSampler() {
        samplerChannel.toggleEnabled();
        return samplerChannel.enabled;
    }
    public boolean  toggleMuteDsp() {
        dialpadChannel.toggleEnabled();
        return dialpadChannel.enabled;
    }

    public int getCurrentSubbeat() {
        int i = playbackThread.ibeat;
        if (i == 0) i = beats * subbeats;
        return i - 1;

    }

    public int getClosestSubbeat(DebugTouch debugTouch) {
        if (playbackThread == null)
            return 0;

        int i = playbackThread.lastI;

        debugTouch.iclosestsubbeat = i;
        debugTouch.dbeat = (i + playbackThread.timeSinceLast / (double)subbeatLength) / subbeats;

        // don't use 16th notes
        //if (i % 2 > 0)
        //    i = (i - 1) % (totalsubbeats);

        //if (i < 0) i = totalsubbeats - 1;

        if (playbackThread.timeSinceLast > subbeatLength / 2) {
            i = i + 1;
            if (i == totalsubbeats)
                i = 0;

            //if (i == -1) i = beats * subbeats - 1;
        }

        debugTouch.isubbeatgiven = i;


        return i;

    }


    class PlaybackThread extends Thread {

        int ibeat;
        int lastI;
        long timeSinceLast;

        public void run() {

            playing = true;
            progressionI = -1; // gets incremented by onNewLoop
            onNewLoop();

            long lastBeatPlayed = 0;
            long now;

            ibeat = 0;


            while (!cancel) {

                now = System.currentTimeMillis();
                timeSinceLast = now - lastBeatPlayed;

                if (timeSinceLast < subbeatLength) {
                    pollFinishedNotes(now);
                    continue;
                }

                lastBeatPlayed = now;
                playBeatSampler(ibeat);

                lastI = ibeat++;

                if (ibeat == beats * subbeats) {
                    ibeat = 0;
                    onNewLoop();

                    for (View iv : viewsToInvalidateOnNewMeasure) {
                        iv.postInvalidate();
                    }

                }

                for (View iv : viewsToInvalidateOnBeat) {
                    iv.postInvalidate();
                }

            }

            playing = false;

        }

        public void pollFinishedNotes(long now) {
            long finishAt = basslineChannel.getFinishAt();
            if (finishAt > 0 && now >= finishAt) {
                basslineChannel.mute();
            }
            finishAt = keyboardChannel.getFinishAt();
            if (finishAt > 0 && now >= finishAt) {
                keyboardChannel.mute();
            }

        }

    }

    private int[] progression = {0};

    void onNewLoop() {

        //long now = System.currentTimeMillis();
        //if (lastHumanInteraction > now - 30000) {
        //    randomRuleChange(now);
        //}

        if (chordsEnabled)
            progressionI++;

        if (progressionI >= progression.length || progressionI < 0) {
            progressionI = 0;
        }
        int chord = progression[progressionI];

        updateChord(chord);

    }

    private void updateChord(int chord) {

        mm.applyScale(basslineChannel, chord);
        mm.applyScale(guitarChannel, chord);
        mm.applyScale(keyboardChannel, chord);
        mm.applyScale(dialpadChannel, chord);
    }

    public void finish() {
        cancel = true;
        keyboardChannel.mute();
        basslineChannel.mute();
        guitarChannel.mute();
        samplerChannel.mute();

        dialpadChannel.mute();

        //mPool.release();
        //mPool = null;
    }


    public void randomRuleChange(long now) {

        int change = rand.nextInt(7);

        switch (change) {
            case 0:
                //subbeatLength += rand.nextBoolean() ? 10 : -10;
                subbeatLength = 70 + rand.nextInt(125); // 125
                break;
            case 1:
                mm.pickRandomKey();
                break;
            case 2:
                mm.pickRandomScale();
                break;
            case 3:
                makeChordProgression();
                break;
            case 4:
                makeDrumbeatFromMelody();

                break;
            case 5:
                makeChannelNotes(basslineChannel);

                break;
            case 6:
                makeChannelNotes(keyboardChannel);

                break;

        }

    }

    public void everyRuleChange() {

        subbeatLength = 70 + rand.nextInt(125);

        mm.pickRandomKey();
        mm.pickRandomScale();
        makeChordProgression();

        mm.makeMotif();
        mm.makeMelodyFromMotif(beats);

        // effects probabilities of makeDrumBeats
        setDrumset(rand.nextInt(2));

        monkeyWithBass();
        if (rand.nextInt(20) == 0) {
            basslineChannel.disable();
        }
        else {
            basslineChannel.enable();
        }

        monkeyWithDrums();
        if (rand.nextInt(20) == 0) {
            drumChannel.disable();
        }
        else {
            drumChannel.enable();
        }

        monkeyWithGuitar();
        if (rand.nextInt(3) == 0) {
            guitarChannel.enable();
        }
        else {
            guitarChannel.disable();
        }

        monkeyWithSynth();
        if (rand.nextInt(3) == 0) {
            keyboardChannel.enable();
        }
        else {
            keyboardChannel.disable();
        }

        monkeyWithDsp();
        if (rand.nextInt(3) == 0) {
            dialpadChannel.enable();
        }
        else {
            dialpadChannel.disable();
        }

        samplerChannel.makeFill();
        if (rand.nextInt(3) == 0) {
            samplerChannel.disable();
        }
        else {
            samplerChannel.enable();
        }

        makeChannelNotes(guitarChannel);
        makeChannelNotes(keyboardChannel);

        makeChannelNotes(dialpadChannel);

        playbackThread.ibeat = 0;
    }


    public String getKeyName() {
        return mm.getKeyName();
    }

    public void makeChordProgression() {
        progressionI = 0;

        int pattern = rand.nextInt(10);
        int scaleLength = mm.getScaleLength();

        if (pattern < 2) {
            int rc =  2 + rand.nextInt(scaleLength - 2);
            progression = new int[] {0, 0, rc, rc};
        }
        else if (pattern < 4)
            progression = new int[] {0,
                                    rand.nextInt(scaleLength),
                                    rand.nextInt(scaleLength),
                                    rand.nextInt(scaleLength)};

        else if (pattern < 6) {
            if (scaleLength == 6)
                progression = new int[] {0, 4, 5, 2};
            else if (scaleLength == 5) {
                progression = new int[] {0, 3, 4, 2};
            }
            else {
                progression = new int[] {0, 4, 5, 3};
            }
        }

        else if (pattern < 8) {
            if (scaleLength == 6)
                progression = new int[] {0, 0, 2, 0, 4, 2, 0, 4};
            else if (scaleLength == 5) {
                progression = new int[] {3, 4, 2, 0, };
            }
            else {
                progression = new int[] {0, 0, 3, 0, 4, 3, 0, 4};
            }
        }

        else if (pattern == 8)
            progression = new int[] {rand.nextInt(scaleLength), rand.nextInt(scaleLength),
                    rand.nextInt(scaleLength)};

        else {
            int changes = 1 + rand.nextInt(8);
            progression = new int[changes];
            for (int i = 0; i < changes; i++) {
                progression[i] = rand.nextInt(scaleLength);
            }
        }

        currentChord = progression[0];

    }

    public int getBPM() {
        return 60000 / (subbeatLength * subbeats);
    }

    public boolean isPlaying() {
        return playing;
    }

    public String getData() {

        StringBuilder sb = new StringBuilder();

        sb.append("{\"type\": \"SECTION\", \"scale\": \"");
        sb.append(mm.getScale());
        sb.append("\", \"ascale\": [");
        sb.append(mm.getScale());
        sb.append("], \"rootNote\": ");
        sb.append(mm.getKey());
        sb.append(", \"beats\" :");
        sb.append(beats);
        sb.append(", \"subbeats\" :");
        sb.append(subbeats);
        sb.append(", \"subbeatMillis\" :");
        sb.append(subbeatLength);

        sb.append(", \"parts\" : [");

        drumChannel.getData(sb);

        sb.append(", ");

        samplerChannel.getData(sb);

        sb.append(", ");

        getChannelData(basslineChannel, sb);

        sb.append(", ");

        getChannelData(keyboardChannel, sb);

        sb.append(", ");

        getChannelData(guitarChannel, sb);

        sb.append(", ");

        getChannelData(dialpadChannel, sb);

        sb.append(", ");

        getChordsData(sb);

        sb.append("]}");

        Log.d("MGH getData", sb.toString());
        return sb.toString();

    }

    public void getChannelData(Channel channel, StringBuilder sb) {

        sb.append("{\"type\" : \"");
        sb.append(channel.getType());
        sb.append("\", \"sound\": \"");
        sb.append(channel.getSoundName());
        sb.append("\", \"scale\": \"");
        sb.append(mm.getScale());
        sb.append("\", \"rootNote\": ");
        sb.append(mm.getKey());
        sb.append(", \"octave\": ");
        sb.append(channel.getOctave());
        sb.append(", \"volume\": ");
        sb.append(channel.getVolume());
        if (!channel.enabled)
            sb.append(", \"mute\": true");
        sb.append(", \"notes\" : [");

        boolean first = true;
        for (Note note : channel.getNotes()) {

            if (first)
                first = false;
            else
                sb.append(", ");

            sb.append("{\"rest\": ");
            sb.append(note.isRest());
            sb.append(", \"beats\": ");
            sb.append(note.getBeats());
            if (!note.isRest()) {
                sb.append(", \"note\" :");
                sb.append(note.getBasicNote());
            }
            sb.append("}");
        }
        sb.append("]}");

    }



    public void getChordsData(StringBuilder sb) {

        sb.append("{\"type\" : \"CHORDPROGRESSION\", \"data\" : [");

        boolean first = true;

        for (int chord : progression) {
            if (first)
                first = false;
            else
                sb.append(", ");

            sb.append(chord);
        }
        sb.append("]}");

    }

    public void monkeyWithEverything() {
        everyRuleChange();

    }

    public void monkeyWithSampler() {
        samplerChannel.makeFill();
    }

    public void monkeyWithDrums() {

        //half the time, drums from the bass
        if (false && rand.nextBoolean()) {
            drumChannel.makeDrumBeatsFromMelody(basslineChannel.getNotes());
        }
        else {
            // make them separate
            drumChannel.makeDrumBeats();
        }

    }

    public void monkeyWithBass() {
        makeChannelNotes(basslineChannel);
    }

    public void monkeyWithSynth() {
        makeChannelNotes(keyboardChannel);
    }

    public void monkeyWithGuitar() {
        makeChannelNotes(guitarChannel);
    }

    public void monkeyWithDsp() {
        makeChannelNotes(dialpadChannel);
    }

    public void monkeyWithChords() {

        makeChordProgression();
        //progressionI = 0;
    }


    public int[] getProgression() {
        return progression;
    }

    public int getChordInProgression() {
        return progressionI;
    }

    public int[] getScale() {
        return mm.getScaleArray();
    }

    public int getScaleIndex() {
        return mm.getScaleIndex();
    }

    public String getScaleString() {
        return mm.getScale();
    }

    public Channel getGuitarChannel() {
        return guitarChannel;
    }

    public Channel getBassChannel() {
        return basslineChannel;
    }

    public Channel getSynthChannel() {
        return keyboardChannel;
    }

    public int getKey() {
        return mm.getKey() % 12;
    }

    public void setDrumset(int set) {
        //drumset = set;
        //setCaptions();
    }

    public void addInvalidateOnBeatListener(View view) {
        viewsToInvalidateOnBeat.add(view);
    }

    public void addInvalidateOnNewMeasureListener(View view) {
        viewsToInvalidateOnNewMeasure.add(view);
    }


    public void removeInvalidateOnBeatListener(View view)  {
        viewsToInvalidateOnBeat.remove(view);
    }

    public DrumChannel getSamplerChannel() {
        return samplerChannel;
    }

    public DrumChannel getDrumChannel() {
        return drumChannel;
    }

    public void setScale(String scale) {
        mm.setScale(scale);
    }
    public void setScale(int scaleI) {
        mm.setScale(scaleI);
    }
    public void setKey(int keyI) {
        mm.setKey(keyI);
    }

    private void playChannelBeat(Channel channel, double beat) {

        if (channel.getState() != Channel.STATE_PLAYBACK)
            return;

        int i = channel.getI();
        if (i <  channel.getNotes().size()) {
            if (channel.getNextBeat() == beat) {
                Note note = channel.getNotes().get(i);

                channel.playRecordedNote(note);
                channel.finishCurrentNoteAt(System.currentTimeMillis() +
                        (long)(note.getBeats() * 4 * subbeatLength) - 50);

                channel.setNextBeat(channel.getNextBeat() +  note.getBeats());

            }
        }
    }

    public void setBPM(float bpm) {
        subbeatLength = (int)((60000 / bpm) / subbeats);
        //bpm = 60000 / (subbeatLength * subbeats);
    }

    public void setSubbeatLength(int length) {
        subbeatLength = length;
    }

    public void setChordProgression(int[] chordProgression) {
        // ifeel like this should be negative one?
        progressionI = 0;
        progression = chordProgression;
        currentChord = progression[0];

        if (chordProgression.length == 1) {
            updateChord(currentChord);
        }
    }

    public int getBeats() {
        return beats;
    }

    public int getSubbeats() {
        return subbeats;
    }

    public int getTotalSubbeats() {
        return totalsubbeats;
    }

    public Random getRand() {
        return rand;
    }

    public void makeDrumbeatFromMelody() {
        drumChannel.makeDrumBeatsFromMelody(basslineChannel.getNotes());
    }

    public boolean isSoundPoolInitialized() {
        return soundPoolInitialized;
    }


    public Channel getDialpadChannel() {
        return dialpadChannel;
    }

    public int getScaledNoteNumber(int basicNote) {
        return mm.scaleNote(basicNote, 0);
    }
}
