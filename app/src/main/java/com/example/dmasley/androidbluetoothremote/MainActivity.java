package com.example.dmasley.androidbluetoothremote;

import com.example.dmasley.androidbluetoothremote.BluetoothDeviceItemView;
import com.example.dmasley.androidbluetoothremote.FullscreenActivity;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.view.View;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.widget.ProgressBar;

import com.example.dmasley.androidbluetoothremote.bluetooth.ConnectionThread;

import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    ListView devicesList;
    ProgressBar progress;
    Button startDiscovery;
    Button stopDiscovery;
    final int REQUEST_ENABLE_BT = 1;
    final int REQUEST_COARSE_LOCATION_PERMISSIONS = 2;
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

        progress = (ProgressBar) findViewById(R.id.progressBar);
        startDiscovery = (Button) findViewById(R.id.start_discovery);
        stopDiscovery = (Button) findViewById(R.id.stop_discovery);

        startDiscovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDiscoverDevices();
            }
        });
        stopDiscovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopDiscoverDevices();
            }
        });
        progress.setVisibility(View.GONE);
        devicesList = (ListView) findViewById(R.id.devices);
        devicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDeviceItemView item = devicesArrayAdapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, FullscreenActivity.class);
                intent.putExtra("device", item.device);
                startActivity(intent);
            }
        });


    }
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    void initBt(){
        devicesArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1 );
        devicesList.setAdapter(devicesArrayAdapter);

        btReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(BluetoothDevice.ACTION_FOUND.equals(action)){
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    BluetoothDeviceItemView item = new BluetoothDeviceItemView(device.getName(), device.getAddress(), device);
                    boolean alreadyInList = false;
                    for(int i = 0; i< devicesArrayAdapter.getCount(); i++){
                        BluetoothDeviceItemView btItem = devicesArrayAdapter.getItem(i);
                        if(btItem.getDeviceAddress().equals(item.getDeviceAddress())){
                            alreadyInList = true;
                        }
                    }
                    if(!alreadyInList){
                        devicesArrayAdapter.add(item);
                        // show paired devices first
                        devicesArrayAdapter.sort(new Comparator<BluetoothDeviceItemView>(){
                            @Override
                            public int compare(BluetoothDeviceItemView device1, BluetoothDeviceItemView device2){
                                return device2.compareTo(device1);
                            }
                        });
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(btReceiver, intentFilter);
        startDiscoverDevices();
    }
    void startDiscoverDevices(){
        int hasPermission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (hasPermission == PackageManager.PERMISSION_GRANTED) {
            adapter.startDiscovery();
            progress.setVisibility(View.VISIBLE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, REQUEST_COARSE_LOCATION_PERMISSIONS);
        }
    }
    void stopDiscoverDevices(){
        adapter.cancelDiscovery();
        progress.setVisibility(View.GONE);
    }
    public void onResume(){
        if(null != progress) {
            if (adapter.isDiscovering()) {
                progress.setVisibility(View.VISIBLE);
            } else {
                progress.setVisibility(View.GONE);
            }
        }
        super.onResume();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                this.initBt();
            }
        }
        if(requestCode == REQUEST_COARSE_LOCATION_PERMISSIONS){
            if(resultCode == RESULT_OK){
                this.startDiscoverDevices();
            }
        }

    }
}
