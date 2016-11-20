package com.example.dmasley.androidbluetoothremote;
import com.example.dmasley.androidbluetoothremote.bluetooth.ConnectionCreationThread;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import java.util.Set;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    private BluetoothDevice device;
    ConnectionCreationThread create;
    FullscreenActivity(){


    }
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    Button onButton;
    Button offButton;
    SeekBar speedBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        Intent intent  = getIntent();
        device = intent.getParcelableExtra("device");
        create = new ConnectionCreationThread(device, BluetoothAdapter.getDefaultAdapter());
        create.start();

        this.initializeUI();
    }

    void initializeUI(){
        onButton = (Button) findViewById(R.id.on);
        offButton = (Button) findViewById(R.id.off);
        speedBar = (SeekBar) findViewById(R.id.speed);

        onButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create.connectionThread.write("A".getBytes());
            }
        });
        offButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create.connectionThread.write("B".getBytes());
            }
        });
        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    protected void onDestroy(){
        if(null != create){
            create.cancel();
        }
        super.onDestroy();
    }
}
