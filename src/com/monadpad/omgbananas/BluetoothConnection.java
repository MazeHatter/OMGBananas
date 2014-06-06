package com.monadpad.omgbananas;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BluetoothConnection extends Thread {
    private BluetoothFactory bluetoothFactory;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final BluetoothSocket socket;
    private BluetoothCallback mConnectedCallback;

    private final static String TAG = "MGH bluetooth connection";
    
    public BluetoothConnection(BluetoothFactory bluetoothFactory, BluetoothSocket socket, BluetoothCallback callback){
        this.bluetoothFactory = bluetoothFactory;
        mConnectedCallback = callback;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        this.socket = socket;

        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            bluetoothFactory.newStatus(mConnectedCallback, BluetoothFactory.STATUS_IO_OPEN_STREAMS);
            Log.d(TAG, e.getMessage());
        }
        mmInStream = tmpIn;
        mmOutStream = tmpOut;

    }

    public void run(){

        if (mConnectedCallback != null)
            mConnectedCallback.onConnected(this);

        int bytes;
        boolean hasData;

        while (!isInterrupted()){

            byte[] buffer = new byte[1024];
            hasData = false;

            try {
                bytes = mmInStream.read(buffer);
                if (bytes > 0) {
                    hasData = true;
                }
                else {
                    Log.d("MGH", "InStream read but zero bytes");
                }
            } catch (IOException e){
                Log.d(TAG, e.getMessage());

                if (!bluetoothFactory.cleaningUp) {
                    bluetoothFactory.newStatus(mConnectedCallback, BluetoothFactory.STATUS_IO_CONNECTED_THREAD);
                }
                break;
            }

            if (hasData)  {

                final String data = new String(buffer).substring(0, bytes);

                Log.d("MGH", data);

                bluetoothFactory.newData(mConnectedCallback, data);
            }

        }
//            if (!cleaningUp) {
//                resetConnections();
//            }
    }

    public void write(byte[] bytes){
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void writeString(String toWrite){
        try {
            mmOutStream.write(toWrite.getBytes());
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }


    void resetConnections() {
        try {
            mmOutStream.close();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
        try {
            mmInStream.close();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
        try {
            socket.close();
            Log.d("MGH", "socket closed");
        }
        catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }

    }

}
