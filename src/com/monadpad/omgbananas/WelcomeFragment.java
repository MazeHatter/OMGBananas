package com.monadpad.omgbananas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

/**
 * User: m
 * Date: 5/6/14
 * Time: 7:11 PM
 */
public class WelcomeFragment extends OMGFragment {

    private View mView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.welcome,
                container, false);



        return mView;
    }

}
