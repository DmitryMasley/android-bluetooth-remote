package com.example.dmasley.androidbluetoothremote;
import com.example.dmasley.androidbluetoothremote.bluetooth.ConnectionCreationThread;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import java.math.BigInteger;

enum STATE {
    NOT_CONNECTED,
    CONNECTING,
    CONNECTED
}
enum Direction {
    FORWARD,
    BACK
}
enum Steering {
    LEFT,
    RIGHT,
    STRAIGHT
}

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    private BluetoothDevice device;
    private STATE state;
    private Direction dir = Direction.FORWARD;
    private int speed = 0;
    private Steering steer = Steering.STRAIGHT;
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
    Button forwardButton;
    Button backButton;
    Button leftButton;
    Button rightButton;
    Button streightButton;
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
        forwardButton = (Button) findViewById(R.id.forward);
        backButton = (Button) findViewById(R.id.back);
        leftButton = (Button) findViewById(R.id.left);
        rightButton = (Button) findViewById(R.id.right);
        streightButton = (Button) findViewById(R.id.straight);
        speedBar = (SeekBar) findViewById(R.id.speed);

        speedBar.setMax(255);

        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dir = Direction.FORWARD;
                sendMessage();
//                create.connectionThread.write("A".getBytes());
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dir = Direction.BACK;
                sendMessage();
//                create.connectionThread.write("B".getBytes());
            }
        });
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                steer = Steering.LEFT;
                sendMessage();
//                create.connectionThread.write("L".getBytes());
            }
        });
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                steer = Steering.RIGHT;
                sendMessage();
//                create.connectionThread.write("R".getBytes());
            }
        });
        streightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                steer = Steering.STRAIGHT;
                sendMessage();
//                create.connectionThread.write("S".getBytes());
            }
        });

        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                byte[] value = BigInteger.valueOf((long)progress).toByteArray();
                speed = progress;
                sendMessage();
//                create.connectionThread.write(value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    void sendMessage(){
        if(null != create && null != create.connectionThread) {
            create.connectionThread.write(this.getCommand().getBytes());
        }
    }
    private String getCommand(){
        return "Dir:"+dir.name()+"|Speed:"+String.valueOf(speed)+"|Steer:"+steer.name()+"|\n";
    }
//    public void onPause(){
//        super.onPause();
//        create.cancel();
//        create = null;
//    }
//    public void onResume(){
//        super.onResume();
//        create = new ConnectionCreationThread(device, BluetoothAdapter.getDefaultAdapter());
//        create.start();
//    }
    protected void onDestroy(){
        if(null != create){
            create.cancel();
        }
        super.onDestroy();
    }
}
