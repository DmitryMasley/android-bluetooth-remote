package com.example.dmasley.androidbluetoothremote;

import android.bluetooth.BluetoothDevice;
/**
 * Created by dmasley on 11/4/16.
 */

public class BluetoothDeviceItemView implements Comparable<BluetoothDeviceItemView>{
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
        if(null != this.deviceName) {
            return this.deviceName;
        } else {
            return this.deviceAddress;
        }
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }
    public int compareTo(BluetoothDeviceItemView device){
        return Integer.compare(this.device.getBondState(), device.device.getBondState());
    }
}
