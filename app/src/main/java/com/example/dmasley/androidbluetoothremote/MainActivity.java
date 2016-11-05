package com.example.dmasley.androidbluetoothremote;

import com.example.dmasley.androidbluetoothremote.BluetoothDeviceItemView;

import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import com.example.dmasley.androidbluetoothremote.bluetooth.ConnectionThread;

import java.util.Set;

public class MainActivity extends AppCompatActivity {
    ListView devicesList;
    int REQUEST_ENABLE_BT;
    final BluetoothAdapter adapter;
    ConnectionThread connectionThread;

    MainActivity(){
        adapter = BluetoothAdapter.getDefaultAdapter();
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // initialize adapter
        if(adapter == null){
            finishActivity(1);
        } else {
            this.initializeUi();

            if (!adapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                this.initBluetooth();
            }
        }

    }
    protected void initializeUi(){

        devicesList = (ListView) findViewById(R.id.devices);
        devicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDeviceItemView item = (BluetoothDeviceItemView) devicesList.getItemAtPosition(position);
            }
        });


    }
    void showBTDevices(){

    }
    void initBluetooth(){
        ArrayAdapter<BluetoothDeviceItemView> devicesArrayAdapter = new ArrayAdapter<BluetoothDeviceItemView>(this, android.R.layout.simple_list_item_1 );
        devicesList.setAdapter(devicesArrayAdapter);
        Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
        if(pairedDevices.size() > 0){
            for(BluetoothDevice device : pairedDevices){
                BluetoothDeviceItemView item = new BluetoothDeviceItemView(device.getName(), device.getAddress(), device);
                devicesArrayAdapter.add(item);
            }
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                this.initBluetooth();
            } else {

            }
        }
    }
}
