package com.example.dmasley.androidbluetoothremote;
import com.example.dmasley.androidbluetoothremote.bluetooth.ConnectionCreationThread;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.hardware.Sensor;
import android.content.Context;

import static java.lang.Math.abs;

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
public class FullscreenActivity extends AppCompatActivity implements SensorEventListener {
    private BluetoothDevice device;
    private boolean active;
    private STATE state;
    private Direction dir = Direction.FORWARD;
    private int speed = 0;
    private Steering steer = Steering.STRAIGHT;
    private int steerAngle = 0;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private final float[] mRotationMatrix = new float[16];
    private final float[] orientation = new float[3];
    ConnectionCreationThread create;
    FullscreenActivity(){


    }

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
        this.active = true;

        setContentView(R.layout.activity_fullscreen);

        Intent intent  = getIntent();

        device = intent.getParcelableExtra("device");
        create = new ConnectionCreationThread(device, BluetoothAdapter.getDefaultAdapter());
        create.start();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mSensorManager.registerListener(this, mSensor, 1000000);

        orientation[1] = 0;
        orientation[2] = 0;

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
//                create.connectionThread.write(value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sendMessage();
            }
        });
    }
    void sendMessage(){
        if(null != create && null != create.connectionThread) {
            create.connectionThread.write(this.getCommand().getBytes());
        }
    }
    private String getCommand(){
        String command = "Dir:"+dir.name()+"|Speed:"+String.valueOf(speed)+"|Steer:"+steer.name()+"|\n";
        Log.d("Command",command);
        return command;
    }
    public void onPause(){
        super.onPause();
        speed = 0;
        steer = Steering.STRAIGHT;
        this.active=false;
        this.sendMessage();
//        create.cancel();
//        create = null;
    }
    public void onResume(){
        super.onResume();
        this.active=true;
//        create = new ConnectionCreationThread(device, BluetoothAdapter.getDefaultAdapter());
//        create.start();
    }
    protected void onDestroy(){
        if(null != create){
            create.cancel();
        }
        super.onDestroy();
    }
    public void onSensorChanged(SensorEvent event) {
        if(this.active == true) {
            // we received a sensor event. it is a good practice to check
            // that we received the proper event
            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                // convert the rotation-vector to a 4x4 matrix. the matrix
                // is interpreted by Open GL as the inverse of the
                // rotation-vector, which is what we want.
                SensorManager.getRotationMatrixFromVector(
                        mRotationMatrix, event.values);
                float[] newOrientation = new float[3];
                SensorManager.getOrientation(mRotationMatrix, newOrientation);
                if (abs(newOrientation[1] - orientation[1]) > 0.1 || abs(newOrientation[2] - orientation[2]) > 0.1) {
                    orientation[1] = newOrientation[1];
                    orientation[2] = newOrientation[2];
                    this.orientationUpdated();
                }
            }
        }
    }
    private void orientationUpdated(){
        float x = orientation[1];
        float y = orientation[2];
        if(x > 0.15){
            steerAngle = 20;
            steer = Steering.LEFT;
        } else if(x < -0.15){
            steerAngle = 160;
            steer = Steering.RIGHT;
        } else {
            steer = Steering.STRAIGHT;
        }
        if(y > -0.2){
            speed = 255;
        } else if(y <= -0.7) {
            speed = 0;
        } else {
            y = y + 0.7f;
            double result = (y/0.5)*255;
            speed = (int) result;
        }

        this.sendMessage();
    }
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
