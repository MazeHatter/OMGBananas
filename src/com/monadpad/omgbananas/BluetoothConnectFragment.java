package com.monadpad.omgbananas;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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
        final TextView logView = (TextView)mView.findViewById(R.id.bluetooth_log);

        buttons = new Button[]{
                (Button) mView.findViewById(R.id.bt_device_1),
                (Button) mView.findViewById(R.id.bt_device_2),
                (Button) mView.findViewById(R.id.bt_device_3)
        };

        for (BluetoothConnection connection : mBtf.getConnections()) {
            setupNextButton(connection);
        }

        final Activity activity = getActivity();
        mBtf.connectToPairedDevices(new BluetoothCallback() {
            @Override
            public void newStatus(final String status) {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (BluetoothFactory.STATUS_BLUETOOTH_TURNED_ON.equals(status)) {
                            //statusView.setVisibility(View.GONE);
                            statusView.setText("Select a Remote:");
                        } else {
                            logView.append("\n");
                            logView.append(status);

                        }
                    }
                });


            }

            @Override
            public void newData(String name, String value) {

                if (name.equals("CHANNEL_PLAY_NOTE")) {
                    Note note = new Note();
                    int noteNumber = Integer.parseInt(value);
                    note.setInstrumentNote(noteNumber);
                    if (noteNumber == -1) {
                        note.setRest(true);
                    }

                    mChannel.playNote(note);
                }

                if (name.equals("CHANNEL_SET_PATTERN")) {

                    String[] params = value.split(",");
                    int track = Integer.parseInt(params[0]);
                    int subbeat = Integer.parseInt(params[1]);
                    boolean patternValue = params[2].equals("true");

                    ((DrumChannel)mChannel).setPattern(track, subbeat, patternValue);
                }

            }

            @Override
            public void onConnected(final BluetoothConnection connection) {
                // we have a winner
                connection.writeString("JAM_SET_KEY=" + mJam.getKey() + ";");
                connection.writeString("JAM_SET_SCALE=" + mJam.getScaleIndex() + ";");
                connection.writeString("JAM_SET_BPM=" + mJam.getBPM() + ";");


                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setupNextButton(connection);
                    }
                });

            }
        });


    }

    private void setupNextButton(final BluetoothConnection connection) {
        Button button = buttons[devicesUsed];
        button.setCompoundDrawablesWithIntrinsicBounds(0,
                R.drawable.device_blue, 0, 0);
        button.setEnabled(true);
        button.setText(connection.getDevice().getName());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mChannel instanceof DrumChannel) {
                    String pattern = ((DrumChannel) mChannel).pattern.toString();
                    Log.d("MGH bluetooth pattern", pattern);
                    connection.writeString("LAUNCH_DRUMPAD=x;");

                }
                else {
                    connection.writeString("LAUNCH_FRETBOARD=" +
                            mChannel.getLowNote() + "," + mChannel.getHighNote() + ";");
                }

                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        devicesUsed++;

    }
}
