package com.monadpad.omgbananas;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class BluetoothFragment extends OMGFragment {

    private View mView;

    private BluetoothFactory btf;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.bluetooth_remote,
                container, false);

        final TextView statusView = (TextView)mView.findViewById(R.id.bt_status);

        View spinningImage = mView.findViewById(R.id.remote_logo);
        Animation turnin = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        turnin.setDuration(4000);
        turnin.setRepeatCount(100);
        spinningImage.startAnimation(turnin);

        //if (BluetoothFactory.initialize(getActivity())) {
        btf = new BluetoothFactory(getActivity(), new BluetoothStatusCallback() {
            @Override
            public void newStatus(String status, int deviceI) {
                statusView.setText(status);
            }

            @Override
            public void newData(String data, int deviceI) {

            }

            @Override
            public void onConnected(BluetoothFactory.ConnectedThread connection) {
                statusView.setText("CONNECTED!");
            }
        });
        if (btf.isEnabled())
            btf.startAccepting();


        return mView;
    }

}