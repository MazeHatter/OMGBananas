package com.monadpad.omgbananas;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


public class BluetoothRemoteFragment extends OMGFragment {

    private BluetoothConnection mConnection;
    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.bluetooth_remote,
                container, false);

        getActivityMembers();

        final TextView statusView = (TextView)mView.findViewById(R.id.bt_status);

        final ImageView spinningImage = (ImageView)mView.findViewById(R.id.remote_logo);
        Animation turnin = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        turnin.setDuration(4000);
        turnin.setRepeatCount(100);
        spinningImage.startAnimation(turnin);

        mBtf.startAccepting(new BluetoothCallback() {
            @Override
            public void newStatus(final String status) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        statusView.setText(status);
                    }
                });
            }

            @Override
            public void newData(String name, String data) {

                processCommand(name, data);

            }

            @Override
            public void onConnected(BluetoothConnection connection) {
                mConnection = connection;

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        statusView.setText("Connected");
                        spinningImage.setImageResource(R.drawable.device_blue);
                    }
                });

            }
        });


        return mView;
    }

    private void launchFretboard(int low, int high) {
        GuitarFragment f = new GuitarFragment();
        BluetoothChannel channel = new BluetoothChannel(getActivity(), mPool, mConnection);
        channel.setLowHigh(low, high);
        f.setJam(mJam, channel);

        showFragment(f);
    }

    private void launchDrumpad() {
        DrumFragment f = new DrumFragment();
        BluetoothDrumChannel channel = new BluetoothDrumChannel(getActivity(), mPool, mJam, mConnection);
        f.setJam(mJam, channel);

        showFragment(f);
    }

    private void showFragment(Fragment f) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_up,
                R.anim.slide_out_up,
                R.anim.slide_in_down,
                R.anim.slide_out_down
        );
        ft.replace(R.id.main_layout, f);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void processCommand(String name, String value) {

        if ("LAUNCH_FRETBOARD".equals(name)) {
            String[] lowhigh = value.split(",");
            launchFretboard(Integer.parseInt(lowhigh[0]), Integer.parseInt(lowhigh[1]));

        }
        else if ("LAUNCH_DRUMPAD".equals(name)) {
            launchDrumpad();

        }
        else if (name.equals("JAM_SET_KEY")) {
            mJam.setKey(Integer.parseInt(value));
        }
        else if (name.equals("JAM_SET_SCALE")) {
            mJam.setScale(Integer.parseInt(value));
        }


    }
}