package com.prompt.multiplebledeviceconnection.model;

import android.bluetooth.BluetoothDevice;

/**
 * Created by root on 10/9/16.
 */

public class BLEDevice {
    private BluetoothDevice bluetoothDevice ;
    private boolean isConnected ;

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }
}
