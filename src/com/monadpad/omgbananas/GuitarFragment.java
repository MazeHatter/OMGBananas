package com.monadpad.omgbananas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * User: m
 * Date: 5/6/14
 * Time: 7:11 PM
 */
public class GuitarFragment extends OMGFragment {

    private Jam mJam;
    private GuitarView guitarView;
    private Channel mChannel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.guitar_fragment,
                container, false);

        guitarView = (GuitarView)view.findViewById(R.id.guitarfrets);
        if (mJam != null)
            guitarView.setJam(mJam, mChannel);

        return view;
    }

    public void setJam(Jam jam, Channel channel) {
        mJam = jam;
        mChannel = channel;

        if (guitarView != null) {
            guitarView.setJam(mJam, mChannel);
        }
    }

}
