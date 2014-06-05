package com.monadpad.omgbananas;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

public class MainFragment extends OMGFragment {

    private View mView;

    private Button playButton;

    private Button guitarMuteButton;

    private View drumControls;
    private Button drumMuteButton;

    private View bassControls;
    private Button bassMuteButton;

    private View keyboardControls;
    private Button synthMuteButton;

    private View guitarControls;

    private View samplerControls;
    private Button samplerMuteButton;

    private Jam mJam;

    private Button mKeyButton;
    private ChordsView mChordsButton;

    private ImageView mainLibenizHead;

    private View drumMonkeyHead;
    private View synthMonkeyHead;
    private View bassMonkeyHead;
    private View guitarMonkeyHead;
    private View samplerMonkeyHead;

    private Button bpmButton;



    public MainFragment(Jam jam) {
        mJam = jam;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.main_fragment,
                container, false);

        setupDrumPanel();
        setupBassPanel();
        setupGuitarPanel();
        setupKeyboardPanel();
        setupSamplerPanel();

        setupSectionInfoPanel();

        setupMainControls();

        return mView;
    }

    public void setupDrumPanel() {

        drumControls = mView.findViewById(R.id.drums);

        ((Button)drumControls.findViewById(R.id.track_button)).setText("Drums");

        drumMuteButton = (Button)drumControls.findViewById(R.id.mute_button);
        drumMuteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drumMuteButton.setBackgroundColor(mJam.toggleMuteDrums() ?
                        Color.GREEN : Color.RED);
            }
        });

        drumMonkeyHead = drumControls.findViewById(R.id.libeniz_head);
        drumMonkeyHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play();
                mJam.monkeyWithDrums();
                Animation turnin = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
                view.startAnimation(turnin);
            }
        });

        drumControls.findViewById(R.id.track_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DrumFragment f = new DrumFragment();
                f.setJam(mJam, mJam.getDrumChannel());
                showFragment(f);
            }
        });


    }

    public void setupBassPanel() {

        bassControls = mView.findViewById(R.id.bass_controls);
        ((Button)bassControls.findViewById(R.id.track_button)).setText("Bass");


        bassMuteButton = (Button)bassControls.findViewById(R.id.mute_button);
        bassMuteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setBackgroundColor(mJam.toggleMuteBassline() ?
                        Color.GREEN : Color.RED);
            }
        });

        bassMonkeyHead = bassControls.findViewById(R.id.libeniz_head);
        bassMonkeyHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play();
                mJam.monkeyWithBass();
                Animation turnin = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
                view.startAnimation(turnin);
            }
        });

        bassControls.findViewById(R.id.track_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GuitarFragment f = new GuitarFragment();
                f.setJam(mJam, mJam.getBassChannel());
                showFragment(f);
            }
        });

    }

    public void setupGuitarPanel() {

        guitarControls = mView.findViewById(R.id.guitar);
        ((Button)guitarControls.findViewById(R.id.track_button)).setText("Guitar");

        guitarMonkeyHead = guitarControls.findViewById(R.id.libeniz_head);
        guitarMonkeyHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play();
                mJam.monkeyWithGuitar();
                Animation turnin = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
                view.startAnimation(turnin);
            }
        });


        guitarMuteButton = (Button)guitarControls.findViewById(R.id.mute_button);
        guitarMuteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setBackgroundColor(mJam.toggleMuteGuitar() ?
                        Color.GREEN : Color.RED);
            }
        });

        guitarControls.findViewById(R.id.track_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GuitarFragment f = new GuitarFragment();
                f.setJam(mJam, mJam.getGuitarChannel());

                showFragment(f);
            }
        });

    }

    public void setupSamplerPanel() {

        samplerControls = mView.findViewById(R.id.sampler);
        ((Button)samplerControls.findViewById(R.id.track_button)).setText("Sampler");

        samplerControls.findViewById(R.id.track_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DrumFragment f = new DrumFragment();
                f.setJam(mJam, mJam.getSamplerChannel());
                showFragment(f);

            }
        });

        samplerMonkeyHead = samplerControls.findViewById(R.id.libeniz_head);
        samplerMonkeyHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play();
                mJam.monkeyWithSampler();
                Animation turnin = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
                view.startAnimation(turnin);
            }
        });


        samplerMuteButton = (Button)samplerControls.findViewById(R.id.mute_button);
        samplerMuteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setBackgroundColor(mJam.toggleMuteSampler() ?
                        Color.GREEN : Color.RED);
            }
        });

    }

    public void setupKeyboardPanel() {
        keyboardControls = mView.findViewById(R.id.rhythm_controls);
        ((Button)(keyboardControls.findViewById(R.id.track_button))).setText("Keyboard");


        synthMuteButton = (Button) keyboardControls.findViewById(R.id.mute_button);
        synthMuteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setBackgroundColor(mJam.toggleMuteRhythm() ?
                        Color.GREEN : Color.RED);
            }
        });


        synthMonkeyHead = keyboardControls.findViewById(R.id.libeniz_head);
        synthMonkeyHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play();
                mJam.monkeyWithSynth();
                Animation turnin = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
                view.startAnimation(turnin);
            }
        });

        keyboardControls.findViewById(R.id.track_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GuitarFragment f = new GuitarFragment();
                f.setJam(mJam, mJam.getSynthChannel());
                showFragment(f);
            }
        });

    }

    public void setupSectionInfoPanel() {

        mKeyButton = (Button)mView.findViewById(R.id.key_button);
        mKeyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                KeyFragment fragment = new KeyFragment();
                fragment.setJam(mJam, MainFragment.this);
                showFragment(fragment);


            }
        });

        bpmButton = (Button)mView.findViewById(R.id.tempo_button);
        bpmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BeatsFragment fragment = new BeatsFragment();
                fragment.setJam(mJam, MainFragment.this);
                showFragment(fragment);

            }
        });

        mChordsButton = (ChordsView)mView.findViewById(R.id.chordprogression_button);
        mChordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ChordsFragment fragment = new ChordsFragment();
                fragment.setJam(mJam, MainFragment.this);
                showFragment(fragment);


            }
        });

        mChordsButton.setJam((Main)getActivity(), mJam);
        mJam.addInvalidateOnNewMeasureListener(mChordsButton);


    }

    public void showFragment(Fragment f) {


        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
        );
        //ft.remove(MainFragment.this);
        ft.replace(R.id.main_layout, f);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();

    }

    private void setupMainControls() {

        playButton = (Button)mView.findViewById(R.id.play_button);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mJam.isPlaying()) {
                    mJam.finish();
                    playButton.setText("Play");
                }
                else {
                    play();
                }
            }
        });


        mainLibenizHead = (ImageView)mView.findViewById(R.id.libeniz_head);
        mainLibenizHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play();
                mJam.monkeyWithEverything();
                Animation turnin = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
                view.startAnimation(turnin);

                drumMonkeyHead.startAnimation(turnin);
                synthMonkeyHead.startAnimation(turnin);
                bassMonkeyHead.startAnimation(turnin);
                guitarMonkeyHead.startAnimation(turnin);
                samplerMonkeyHead.startAnimation(turnin);

                updateUI();
            }
        });

        setupMainBanana();

    }

    private void setupMainBanana() {

        final ImageView mainBanana = (ImageView) mView.findViewById(R.id.main_banana);
        mainBanana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                OMGHelper omgHelper = new OMGHelper(getActivity(), OMGHelper.Type.SECTION,
                        mJam.getData());
                omgHelper.submitWithTags("");

                Animation turnin = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
                view.startAnimation(turnin);

                /*

                if (mainBananaClicked) {
                    mainBananaClicked = false;

                    mainBanana.setImageDrawable(getResources().getDrawable(R.drawable.banana48));

                    showDialog(11);

                }
                else {
                    mainBanana.setImageDrawable(getResources().getDrawable(R.drawable.add_tag_white));
                    mainBananaClicked = true;

                    mainBanana.clearAnimation();

                    omgHelper = new OMGHelper(Main.this, OMGHelper.Type.SECTION,
                            mJam.getData());

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                            }
                            if (mainBananaClicked) {

                                mainBananaClicked = false;

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showBanana(mainBanana);
                                    }
                                });

                                omgHelper.submitWithTags("");

                            }

                        }
                    }).start();
                }
        */
            }
        });

    }


    private void play() {
        if (!mJam.isPlaying()) {
            mJam.kickIt();
            playButton.setText("Stop");
        }
    }

    public void onViewStateRestored(Bundle bundle) {
        super.onViewStateRestored(bundle);
        updateUI();

    }

    public void updateUI() {
        if (mChordsButton != null) {
            updateKeyUI();
            updateBPMUI();
            mChordsButton.invalidate();
        }

    }

    public void updateBPMUI() {
        bpmButton.setText(Integer.toString(mJam.getBPM()) + " bpm");
    }

    public void updateKeyUI() {
        mKeyButton.setText(mJam.getKeyName());
    }
}

