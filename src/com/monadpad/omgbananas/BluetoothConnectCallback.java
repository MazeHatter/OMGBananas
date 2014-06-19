package com.monadpad.omgbananas;

public abstract class BluetoothConnectCallback {

    public abstract void newStatus(String status);
    public abstract void onConnected(BluetoothConnection connection);


}
