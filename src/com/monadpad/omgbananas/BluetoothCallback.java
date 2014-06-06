package com.monadpad.omgbananas;

public abstract class BluetoothCallback {

    public abstract void newStatus(String status);
    public abstract void newData(String name, String value);
    public abstract void onConnected(BluetoothConnection connection);


}
