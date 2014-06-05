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
    public static final int REQUEST_ENABLE_BT = 2;

    private static final String NAME = "OMG BANANAS";
    private static final UUID MY_UUID = UUID.fromString("e0358210-6406-11e1-b86c-0800200c9a66");
    private BluetoothAdapter mBluetooth;
    private static final int MESSAGE_READ = 0;
    public static final int MESSAGE_STATUS = 1;
    private AcceptThread acceptThread;
    private final Context ctx;

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

    public BluetoothFactory(Activity context, BluetoothStatusCallback statusCallback) {

        this.statusCallback = statusCallback;
        ctx = context;

        mBluetooth = BluetoothAdapter.getDefaultAdapter();
        if (mBluetooth == null){
            newStatus("Bluetooth is not available.", -1);
            return;
        }

        if (!mBluetooth.isEnabled()){

            newStatus("Bluetooth is not on.", -1);

            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            context.startActivityForResult(enableBT, REQUEST_ENABLE_BT);
            ctx.registerReceiver(btStateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        }
        else {
            newStatus("Bluetooth is on.", -1);
        }

    }


    public void startAccepting() {

        isServer = true;

        newStatus(STATUS_ACCEPTING_CONNECTIONS, -1);

        acceptThread = new AcceptThread();
        acceptThread.start();
    }

    public void toggleDeviceStatus(int deviceI) {

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

            newStatus(STATUS_CONNECTING_TO + device.getName(), deviceI);
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
    }


    private class AcceptThread extends Thread{

        public AcceptThread(){

            if (mServerSocket == null ) {
                BluetoothServerSocket tmp = null;
                try {
                    tmp =  mBluetooth.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);

                }    catch (IOException e) {
                    newStatus("IOException in listenUsingRfcomm", -1);
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
                        newStatus("IOException in accept()", -1);

                    break;
                }

                if (socket != null){
                    readSocket(socket, devices++);
                }

            }
        }

    }

    public void connect() {

        devices = 0;

        paired = mBluetooth.getBondedDevices();
        Iterator<BluetoothDevice> iterator = paired.iterator();
        while (iterator.hasNext()) {
            BluetoothDevice device = iterator.next();
            newStatus(STATUS_CONNECTING_TO + device.getName(), devices);
            new ConnectThread(device, devices).start();
            devices++;
        }
    }

    private class ConnectThread extends Thread {
        BluetoothSocket mSocket;
        int deviceI = 0;

        public ConnectThread(BluetoothDevice device, int deviceI) {
            this.deviceI = deviceI;
            BluetoothSocket tmp = null;

            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                newStatus("IOException in createRfcommSocket", deviceI);
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
                newStatus(STATUS_IO_CONNECT_THREAD, deviceI);
            }
            if (good)
                readSocket(mSocket, deviceI);
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

        public ConnectedThread(BluetoothSocket socket, int deviceI){
            this.deviceI = deviceI;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            this.socket = socket;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                newStatus(STATUS_IO_OPEN_STREAMS, deviceI);
                Log.d(TAG, e.getMessage());
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;

            if (deviceI > -1 && deviceI < isConnected.length)
                isConnected[deviceI] = true;

        }

        public void run(){

            if (statusCallback != null)
                statusCallback.onConnected(this);

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
                        newStatus(STATUS_IO_CONNECTED_THREAD, deviceI);
                    }
                    break;
                }

                if (hasData)  {

                    final String data = new String(buffer).substring(0, bytes);

                    Log.d("MGH", data);

                    newData(data, deviceI);
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

    private void readSocket(BluetoothSocket socket, int deviceI){


        ConnectedThread ct = new ConnectedThread(socket, deviceI);
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
                newStatus("cleanup catch", -1);
            }
        }
        Log.d("MGH", "cleanup 4");
    }

    void newStatus(String newString, int deviceI) {
        Log.d("MGH newStatus", newString);

        if (statusCallback != null) {
            statusCallback.newStatus(newString, deviceI);
        }
    }
    void newData(String newString, int deviceI) {
        Log.d("MGH newData", newString);

        if (statusCallback != null) {
            statusCallback.newData(newString, deviceI);
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

                newStatus("Bluetooth is now on.", -1);
                context.unregisterReceiver(this);

            }
        }
    };

    public boolean isEnabled() {
        return mBluetooth != null && mBluetooth.isEnabled();
    }
}
