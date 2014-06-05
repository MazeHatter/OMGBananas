package com.monadpad.omgbananas;

public abstract class BluetoothStatusCallback {

    public abstract void newStatus(String status, int deviceI);
    public abstract void newData(String data, int deviceI);
    public abstract void onConnected(BluetoothFactory.ConnectedThread connection);


}
