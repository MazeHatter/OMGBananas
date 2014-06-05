package com.monadpad.omgbananas;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class BluetoothConnectFragment extends OMGFragment {

    private View mView;

    private int devicesUsed = 0;

    private Button[] buttons;

    private Channel mChannel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.bluetooth_connect,
                container, false);

        getActivityMembers();
        setup();

        return mView;
    }

    public void setChannel(Channel channel) {
        mChannel = channel;
    }

    private void setup() {
        final TextView statusView = (TextView)mView.findViewById(R.id.bt_status);

        buttons = new Button[]{
                (Button) mView.findViewById(R.id.bt_device_1),
                (Button) mView.findViewById(R.id.bt_device_2),
                (Button) mView.findViewById(R.id.bt_device_3)
        };

        final Activity activity = getActivity();
        //btf = new BluetoothFactory(getActivity());
        mBtf.connectToPairedDevices(new BluetoothStatusCallback() {
            @Override
            public void newStatus(final String status, int deviceI) {

                if (BluetoothFactory.STATUS_BLUETOOTH_TURNED_ON.equals(status)) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            statusView.setVisibility(View.GONE);
                        }
                    });
                }
            }

            @Override
            public void newData(String data, int deviceI) {
            }

            @Override
            public void onConnected(BluetoothFactory.ConnectedThread connection) {
                // we have a winner
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Button button = buttons[devicesUsed];
                        button.setCompoundDrawablesWithIntrinsicBounds(0,
                                R.drawable.device_blue, 0, 0);
                        //button.setText(device.getName());

                    }
                });

                // so tell the new device to launch a panel
                // attached to right channel
                // and then disappear

                //connection.writeString();
            }
        });


    }
}
