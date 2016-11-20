package com.example.dmasley.androidbluetoothremote.bluetooth;
import com.example.dmasley.androidbluetoothremote.bluetooth.ConnectionThread;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.support.annotation.RequiresPermission;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by dmasley on 11/1/16.
 */
public class ConnectionCreationThread extends Thread {
    private final BluetoothDevice device;
    private final BluetoothSocket socket;
    public ConnectionThread connectionThread;
    final BluetoothAdapter adapter;
    private UUID DEFAULT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    String MY_UUID = "MY_UID";
    public ConnectionCreationThread(BluetoothDevice d, BluetoothAdapter a){
        device = d;
        adapter = a;
        BluetoothSocket tmpSocket = null;
        try {
            UUID deviceUUID;
            if(device.getUuids().length > 0){
                deviceUUID = device.getUuids()[0].getUuid();
            } else {
                deviceUUID = DEFAULT_UUID;
            }
            device.getUuids()[0].getUuid();
            tmpSocket = device.createRfcommSocketToServiceRecord(deviceUUID);
        } catch (IOException e){

        }
        socket = tmpSocket;
    }
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    public void run(){
        adapter.cancelDiscovery();

        try {
            socket.connect();
        } catch (IOException connectionException){
            try {
                socket.close();
            } catch (IOException closeException){
                return;
            }
            return;
        }
        manageConnectedSocket(socket);

    }
    private void manageConnectedSocket(BluetoothSocket socket){
        connectionThread = new ConnectionThread(socket);
    }
    public void cancel() {
        try {
            socket.close();
            if(null != connectionThread){
                connectionThread.cancel();
            }
        } catch (IOException e) { }
    }
}
