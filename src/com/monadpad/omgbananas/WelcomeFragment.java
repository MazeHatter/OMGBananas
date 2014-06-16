package com.monadpad.omgbananas;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

public class WelcomeFragment extends OMGFragment {

    private View mView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivityMembers();

        mView = inflater.inflate(R.layout.welcome,
                container, false);

        if (!mJam.isSoundPoolInitialized()) {
            setupSoundPool();
        }
        else {
            hideWelcome();
        }

        mView.findViewById(R.id.return_to_omg_bananas).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainFragment mainFragment = new MainFragment();
                showFragment(mainFragment);
            }
        });


        mView.findViewById(R.id.bt_remote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (!mJam.isSoundPoolInitialized()) {
                            mPool.cancelLoading();
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        BluetoothRemoteFragment f = new BluetoothRemoteFragment();

                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.setCustomAnimations(R.anim.slide_in_up,
                                R.anim.slide_out_up,
                                R.anim.slide_in_down,
                                R.anim.slide_out_down
                        );
                        ft.add(R.id.main_layout, f);
                        ft.remove(WelcomeFragment.this);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        ft.addToBackStack(null);
                        ft.commit();

                    }
                }).start();


            }
        });


        return mView;
    }

    public void showFragment(Fragment f) {


        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
        );
        ft.replace(R.id.main_layout, f);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();

    }

    private void setupSoundPool() {
        final ProgressBar progressBar = (ProgressBar)mView.findViewById(R.id.loading_progress);

        final Runnable callback = new Runnable() {
            @Override
            public void run() {
                MainFragment mainFragment = new MainFragment();
                showFragment(mainFragment);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideWelcome();
                    }
                });

            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                mJam.makeChannels(progressBar, callback);
            }
        }).start();


    }

    private void hideWelcome() {
        mView.findViewById(R.id.welcome_info).setVisibility(View.GONE);
        mView.findViewById(R.id.loading_info).setVisibility(View.GONE);
        mView.findViewById(R.id.goback).setVisibility(View.VISIBLE);

    }
}
