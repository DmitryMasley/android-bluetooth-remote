package com.example.dmasley.androidbluetoothremote;

import android.bluetooth.BluetoothDevice;
/**
 * Created by dmasley on 11/4/16.
 */

public class BluetoothDeviceItemView {
    public final String deviceName;
    public final String deviceAddress;
    public final BluetoothDevice device;
    BluetoothDeviceItemView(String name, String address, BluetoothDevice bluetoothDevice){
        deviceName = name;
        deviceAddress = address;
        device = bluetoothDevice;
    }
    @Override
    public String toString() {
        return this.deviceName;
    }
}
