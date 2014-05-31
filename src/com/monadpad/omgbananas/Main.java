package com.monadpad.omgbananas;

import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class Main extends FragmentActivity {

    Jam mJam;

    boolean mainBananaClicked = false;

    private final static int DIALOG_TAGS = 11;

    private OMGHelper omgHelper;

    private SoundPool pool = new SoundPool(8, AudioManager.STREAM_MUSIC, 0);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN|
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN|WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        setContentView(R.layout.main);

        mJam = new Jam(this, pool);

        final ProgressBar progressBar = (ProgressBar)findViewById(R.id.loading_progress);

        final Runnable callback = new Runnable() {
            @Override
            public void run() {
                MainFragment mainFragment = new MainFragment(mJam);
                showFragment(mainFragment);

                //new Welcome(this);

            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                mJam.makeChannels(progressBar, callback);
            }
        }).start();


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






    private void showBanana(ImageView view) {
        view.setImageDrawable(getResources().getDrawable(R.drawable.banana48));

        Animation turnin = AnimationUtils.loadAnimation(this, R.anim.rotate);
        view.startAnimation(turnin);

    }


    public void showFragment(Fragment f) {


        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
        );
        ft.add(R.id.main_layout, f);
        ft.hide(getSupportFragmentManager().findFragmentById(R.id.welcome_fragment));
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();

    }

}
