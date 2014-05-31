package com.monadpad.omgbananas;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class Main extends FragmentActivity {

    MonadJam mJam;

    View chordControls;

    boolean mainBananaClicked = false;


    private final static int DIALOG_TAGS = 11;

    private OMGHelper omgHelper;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN|
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN|WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        setContentView(R.layout.main);

        mJam = new MonadJam(this);


        ((MainFragment)getSupportFragmentManager().findFragmentById(R.id.mixerFragment)).setJam(mJam);

        setupMainBanana();

//        setupChordsPanel();


        new MakeChannelsAndPlay().execute();

        new Welcome(this);

        //headbob = new HeadBob((ImageView)mLibenizView.findViewById(R.id.libeniz_head));
        //headbob.start(500);

    }



    @Override
    public void onStop() {
        super.onStop();
        mJam.finish();
    }


    protected Dialog onCreateDialog(int dialog){

        switch (dialog){

            case DIALOG_TAGS:

                final Dialog dl = new Dialog(this);
                dl.setTitle(getString(R.string.tags_dialog_title));
                dl.setContentView(R.layout.gettags);


                dl.findViewById(R.id.why_button).setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        removeDialog(DIALOG_TAGS);
                    }
                });

                dl.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        String tags = ((EditText) dl.findViewById(R.id.txt_tags)).getText().toString();

                        removeDialog(DIALOG_TAGS);

                        omgHelper.submitWithTags(tags);
                    }
                });
                return dl;
        }
        return null;
    }


    private void setupMainBanana() {

        final ImageView mainBanana = (ImageView)findViewById(R.id.main_banana);
        mainBanana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
            }
        });

    }




    public void setupChordsPanel() {
//        chordControls = findViewById(R.id.chords);

        chordControls.findViewById(R.id.libeniz_head).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mJam.monkeyWithChords();
                Animation turnin = AnimationUtils.loadAnimation(Main.this, R.anim.rotate);
                view.startAnimation(turnin);

            }
        });
    }


    private void showBanana(ImageView view) {
        view.setImageDrawable(getResources().getDrawable(R.drawable.banana48));

        Animation turnin = AnimationUtils.loadAnimation(this, R.anim.rotate);
        view.startAnimation(turnin);

    }


    class MakeChannelsAndPlay extends AsyncTask<Void, Void, Void> {

        public Void doInBackground(Void... v) {

            mJam.makeChannels();

            return null;
        }

    }

}
