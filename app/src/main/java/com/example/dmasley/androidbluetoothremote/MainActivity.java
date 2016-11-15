package com.example.dmasley.androidbluetoothremote;

import com.example.dmasley.androidbluetoothremote.BluetoothDeviceItemView;
import com.example.dmasley.androidbluetoothremote.FullscreenActivity;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.View;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import com.example.dmasley.androidbluetoothremote.bluetooth.ConnectionThread;

import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    ListView devicesList;
    int REQUEST_ENABLE_BT;
    final BluetoothAdapter adapter;
    private BroadcastReceiver btReceiver;
    private ArrayAdapter<BluetoothDeviceItemView> devicesArrayAdapter;
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
                this.initBt();
            }
        }

    }
    protected void onDestroy(){
        if(null !=btReceiver){
            unregisterReceiver(btReceiver);
        }
        super.onDestroy();
    }
    protected void initializeUi(){

        devicesList = (ListView) findViewById(R.id.devices);
        devicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDeviceItemView item = (BluetoothDeviceItemView) devicesList.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this, FullscreenActivity.class);
                intent.putExtra("deviceAddress", item.deviceAddress);
                startActivity(intent);
            }
        });


    }
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    void initBt(){
        devicesArrayAdapter = new ArrayAdapter<BluetoothDeviceItemView>(this, android.R.layout.simple_list_item_1 );
        devicesList.setAdapter(devicesArrayAdapter);

        btReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(BluetoothDevice.ACTION_FOUND.equals(action)){
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    BluetoothDeviceItemView item = new BluetoothDeviceItemView(device.getName(), device.getAddress(), device);
                    devicesArrayAdapter.add(item);
                    devicesArrayAdapter.sort(new Comparator<BluetoothDeviceItemView>(){
                        @Override
                        public int compare(BluetoothDeviceItemView device1, BluetoothDeviceItemView device2){
                            return device2.compareTo(device1);
                        }
                    });
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(btReceiver, intentFilter);
        adapter.startDiscovery();
    }
    void showBTDevices(){

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                this.initBt();
            } else {

            }
        }
    }
}
