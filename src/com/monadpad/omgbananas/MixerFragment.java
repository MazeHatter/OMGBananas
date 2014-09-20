package com.monadpad.omgbananas;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MixerFragment extends OMGFragment {

    private View mView;

    private View drumControls;

    private View bassControls;

    private View keyboardControls;

    private View guitarControls;

    private View samplerControls;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivityMembers();

        mView = inflater.inflate(R.layout.mixer_fragment,
                container, false);

        setupDrumPanel();
        setupBassPanel();
        setupGuitarPanel();
        setupKeyboardPanel();
        setupSamplerPanel();

        return mView;
    }

    public void setupDrumPanel() {

        drumControls = mView.findViewById(R.id.drums);

        //((Button)drumControls.findViewById(R.id.track_button)).setText("Drums");

    }

    public void setupBassPanel() {

        bassControls = mView.findViewById(R.id.bass_controls);
        //((Button)bassControls.findViewById(R.id.track_button)).setText("Bass");


    }



    public void setupGuitarPanel() {

        guitarControls = mView.findViewById(R.id.guitar);
        //((Button)guitarControls.findViewById(R.id.track_button)).setText("Guitar");

    }

    public void setupSamplerPanel() {

        samplerControls = mView.findViewById(R.id.sampler);
        //((Button)samplerControls.findViewById(R.id.track_button)).setText("Sampler");

    }

    public void setupKeyboardPanel() {
        keyboardControls = mView.findViewById(R.id.rhythm_controls);
        //((Button)(keyboardControls.findViewById(R.id.track_button))).setText("Keyboard");


    }

    public void showFragmentRight(Fragment f) {


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

    public void showFragmentDown(Fragment f) {


        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_up,
                R.anim.slide_out_up,
                R.anim.slide_in_down,
                R.anim.slide_out_down
        );
        //ft.remove(MainFragment.this);
        ft.replace(R.id.main_layout, f);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();

    }

    public void showFragmentUp(Fragment f) {


        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_down,
                R.anim.slide_out_down,
                R.anim.slide_in_up,
                R.anim.slide_out_up
        );
        //ft.remove(MainFragment.this);
        ft.replace(R.id.main_layout, f);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();

    }


}

