package com.monadpad.omgbananas;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;
import android.view.WindowManager;

public class Main extends FragmentActivity {

    Jam mJam;
    OMGSoundPool mPool = new OMGSoundPool(8, AudioManager.STREAM_MUSIC, 0);
    BluetoothFactory mBtf;


    private WelcomeFragment mWelcomeFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBtf = new BluetoothFactory(this);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN|
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN|WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        setContentView(R.layout.main);


        mJam = new Jam(this, mPool);

        if (mWelcomeFragment == null) {
            mWelcomeFragment = new WelcomeFragment();
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.main_layout, mWelcomeFragment);
        ft.commit();


        //headbob = new HeadBob((ImageView)mLibenizView.findViewById(R.id.libeniz_head));
        //headbob.start(500);

    }



    @Override
    public void onStop() {
        super.onStop();
        mJam.finish();
    }


}
