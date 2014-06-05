package com.monadpad.omgbananas;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class BluetoothFactory {

    public static final String STATUS_CONNECTED = "Connected";
    public static final String STATUS_IO_CONNECTED_THREAD  = "IOException in ConnectedThread";
    public static final String STATUS_IO_OPEN_STREAMS  = "IOException opening streams";
    public static final String STATUS_ACCEPTING_CONNECTIONS = "Accepting Connections";
    public static final String STATUS_CONNECTING_TO = "Connecting to ";
    public static final String STATUS_IO_CONNECT_THREAD  = "IOException in ConnectThread";
    public static final String STATUS_BLUETOOTH_TURNED_ON = "Bluetooth has been turned on";
    public static final int REQUEST_ENABLE_BT = 2;

    private static final String NAME = "OMG BANANAS";
    private static final UUID MY_UUID = UUID.fromString("e0358210-6406-11e1-b86c-0800200c9a66");
    private BluetoothAdapter mBluetooth;
    private static final int MESSAGE_READ = 0;
    public static final int MESSAGE_STATUS = 1;
    private AcceptThread acceptThread;
    private final Activity ctx;

    private final static String TAG = "MGH Bluetooth";

    private BluetoothStatusCallback statusCallback;

    private ArrayList<ConnectedThread> connectionThreads = new ArrayList<ConnectedThread>();

    private boolean cleaningUp = false;

    BluetoothServerSocket mServerSocket;

    private Set<BluetoothDevice> paired;
    private int devices = 0;

    private boolean[] isConnected = {false, false, false};

    private boolean isServer = false;
    private BluetoothSocket socketToWriteTo;

    private Activity mCallingActivity;

    private boolean isSetup = false;

    public BluetoothFactory(Activity context) {
        ctx = context;
        mBluetooth = BluetoothAdapter.getDefaultAdapter();
    }

    public boolean setup(BluetoothStatusCallback callback) {

        // for the receiver
        statusCallback = callback;

        if (mBluetooth == null){
            newStatus(callback, "Bluetooth is not available", -1);
            return false;
        }

        if (!mBluetooth.isEnabled()){

            newStatus(callback, "Bluetooth is off", -1);

            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ctx.startActivityForResult(enableBT, REQUEST_ENABLE_BT);
            ctx.registerReceiver(btStateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        }
        else {
            isSetup = true;
        }

        return isSetup;
    }


    public void startAccepting(final BluetoothStatusCallback callback) {

        // a new callback to relay the old one, plus a few things

        if (isSetup || setup(new BluetoothStatusCallback() {
                @Override
                public void newStatus(String status, int deviceI) {
                    callback.newStatus(status, deviceI);

                    if (BluetoothFactory.STATUS_BLUETOOTH_TURNED_ON.equals(status)) {
                        isSetup = true;
                        startAccepting(callback);
                    }
                }

                @Override
                public void newData(String data, int deviceI) {
                    callback.newData(data, deviceI);
                }

                @Override
                public void onConnected(BluetoothFactory.ConnectedThread connection) {
                    callback.onConnected(connection);
                }
            })) {

            newStatus(callback, STATUS_BLUETOOTH_TURNED_ON, -1);
        }
        else {

            // wait for the  bluetooth to turn on
            return;
        }


        statusCallback = callback;
        isServer = true;

        newStatus(statusCallback, STATUS_ACCEPTING_CONNECTIONS, -1);

        acceptThread = new AcceptThread();
        acceptThread.start();
    }

    /*public void toggleDeviceStatus(int deviceI) {

        if (deviceI < 0 && deviceI > isConnected.length)
            return;

        if (!isConnected[deviceI]) {
            Iterator<BluetoothDevice> iterator = paired.iterator();
            BluetoothDevice device = iterator.next();
            int currentDeviceI = 0;

            while (currentDeviceI < deviceI && iterator.hasNext()) {
                device = iterator.next();
                currentDeviceI++;
            }

            newStatus(statusCallback, STATUS_CONNECTING_TO + device.getName(), deviceI);
            new ConnectThread(device, deviceI).start();
        }
        else {
            for (ConnectedThread ct : connectionThreads) {
                if (ct.deviceI == deviceI) {
                    Log.d("MGH", "reseting...");
                    ct.resetConnections();
                    Log.d("MGH", "connections are reset");
                    connectionThreads.remove(ct);
                    isConnected[deviceI] = false;
                    Log.d("MGH", "disconnected");
                    return;
                }
            }
        }
    }*/


    private class AcceptThread extends Thread{

        public AcceptThread(){

            if (mServerSocket == null ) {
                BluetoothServerSocket tmp = null;
                try {
                    tmp =  mBluetooth.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);

                }    catch (IOException e) {
                    newStatus(statusCallback, "IOException in listenUsingRfcomm", -1);
                }
                mServerSocket = tmp;
            }
        }

        public void run(){

            BluetoothSocket socket;
            while (!isInterrupted()){
                try {
                    socket = mServerSocket.accept();
                } catch (IOException e){
                    if (!cleaningUp)
                        newStatus(statusCallback, "IOException in accept()", -1);

                    break;
                }

                if (socket != null){
                    readSocket(socket, statusCallback);
                }

            }
        }

    }

    public void connectToPairedDevices(final BluetoothStatusCallback callback) {

        if (isSetup ||setup(new BluetoothStatusCallback() {
                @Override
                public void newStatus(String status, int deviceI) {
                    callback.newStatus(status, deviceI);

                    if (BluetoothFactory.STATUS_BLUETOOTH_TURNED_ON.equals(status)) {
                        isSetup = true;
                        connectToPairedDevices(callback);
                    }
                }

                @Override
                public void newData(String data, int deviceI) {
                    callback.newData(data, deviceI);
                }

                @Override
                public void onConnected(ConnectedThread connection) {
                    callback.onConnected(connection);
                }
                })) {
            newStatus(callback, STATUS_BLUETOOTH_TURNED_ON, -1);
        }
        else {
            // wait for the  bluetooth to turn on
            return;
        }


        devices = 0;

        paired = mBluetooth.getBondedDevices();
        Iterator<BluetoothDevice> iterator = paired.iterator();
        while (iterator.hasNext()) {
            BluetoothDevice device = iterator.next();
            newStatus(callback, STATUS_CONNECTING_TO + device.getName(), devices);

            new ConnectThread(device, devices, callback).start();
            devices++;
        }
    }


    private class ConnectThread extends Thread {
        BluetoothSocket mSocket;
        int deviceI = 0;
        BluetoothStatusCallback mConnectCallback;

        public ConnectThread(BluetoothDevice device, int deviceI, BluetoothStatusCallback callback) {
            this.deviceI = deviceI;
            mConnectCallback = callback;
            BluetoothSocket tmp = null;

            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                newStatus(mConnectCallback, "IOException in createRfcommSocket", deviceI);
                Log.d(TAG, e.getMessage());}
            mSocket = tmp;
        }

        public void run() {
            mBluetooth.cancelDiscovery();
            Log.d(TAG, "connectthread");
            boolean good = false;
            try {
                mSocket.connect();
                good = true;

            } catch (IOException connectException) {
                Log.d(TAG, connectException.getMessage());
                newStatus(mConnectCallback, STATUS_IO_CONNECT_THREAD, deviceI);
            }
            if (good)
                readSocket(mSocket, mConnectCallback);
            else {
                try {
                    mSocket.close();
                }
                catch (IOException e) {
                    Log.d(TAG, e.getMessage());
                }
            }
        }
    }

    public class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private final BluetoothSocket socket;
        private int deviceI;
        private BluetoothStatusCallback mConnectedCallback;

        public ConnectedThread(BluetoothSocket socket, BluetoothStatusCallback callback){
            //this.deviceI = deviceI;
            mConnectedCallback = callback;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            this.socket = socket;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                newStatus(mConnectedCallback, STATUS_IO_OPEN_STREAMS, deviceI);
                Log.d(TAG, e.getMessage());
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;

            if (deviceI > -1 && deviceI < isConnected.length)
                isConnected[deviceI] = true;

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

                    if (!cleaningUp) {
                        newStatus(mConnectedCallback, STATUS_IO_CONNECTED_THREAD, deviceI);
                    }
                    break;
                }

                if (hasData)  {

                    final String data = new String(buffer).substring(0, bytes);

                    Log.d("MGH", data);

                    newData(mConnectedCallback, data, deviceI);
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

            if (deviceI > -1 && deviceI < isConnected.length)
                isConnected[deviceI] = false;

        }

    }

    private void readSocket(BluetoothSocket socket, BluetoothStatusCallback callback){


        ConnectedThread ct = new ConnectedThread(socket, callback);
        connectionThreads.add(ct);

        // if you don't add to the arrayList before you start
        // any Write's that occur on CONNECT will fail
        ct.start();

    }

    private void writeSocket(String toWrite){
        //Log.d(TAG, "write socket");
        //newStatus("write socket");

        //new ConnectedThread(socket).write(toWrite.getBytes());

        for (ConnectedThread ct : connectionThreads) {
            ct.write(toWrite.getBytes());
        }

    }

    public void writeToBluetooth(String toWrite){
        writeSocket(toWrite);
    }


    public void cleanUp() {
        cleaningUp = true;

        Log.d("MGH", "cleanup 2");
        for (ConnectedThread ct : connectionThreads) {
            ct.resetConnections();
        }
        connectionThreads.clear();

        if (mServerSocket != null) {
            try {
                mServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (acceptThread != null && !acceptThread.isInterrupted()){
            Log.d("MGH", "cleanup 3");
            acceptThread.interrupt();
            try {
                acceptThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                newStatus(statusCallback, "cleanup catch", -1);
            }
        }
        Log.d("MGH", "cleanup 4");
    }

    void newStatus(BluetoothStatusCallback callback, String newString, int deviceI) {
        Log.d("MGH newStatus", newString);

        if (callback != null) {
            callback.newStatus(newString, deviceI);
        }
    }
    void newData(BluetoothStatusCallback callback, String newString, int deviceI) {
        Log.d("MGH newData", newString);

        if (callback != null) {
            callback.newData(newString, deviceI);
        }
    }

    boolean hasConnections() {
        return isConnected[0] || isConnected[1] || isConnected[2];
    }

    BroadcastReceiver btStateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null &&
                    BluetoothAdapter.STATE_ON == intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                            BluetoothAdapter.ERROR)) {

                isSetup = true;
                newStatus(statusCallback, STATUS_BLUETOOTH_TURNED_ON, -1);
                context.unregisterReceiver(this);

            }
        }
    };

    public boolean isEnabled() {
        return mBluetooth != null && mBluetooth.isEnabled();
    }

    public ArrayList<BluetoothDevice> getPairedDevices() {

        ArrayList<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();

        paired = mBluetooth.getBondedDevices();
        Iterator<BluetoothDevice> iterator = paired.iterator();

        while (iterator.hasNext()) {
            BluetoothDevice device = iterator.next();
            devices.add(device);
        }
        return devices;
    }
}
