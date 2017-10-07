package com.example.dmasley.androidbluetoothremote;
import com.example.dmasley.androidbluetoothremote.bluetooth.ConnectionCreationThread;
import java.lang.Math;

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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
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
    private int maxValueOfSpeedBar = 510;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private final float[] mRotationMatrix = new float[16];
    private final float[] orientation = new float[3];
    private boolean useSensorForSpeed = false;
    ConnectionCreationThread create;
    public FullscreenActivity(){


    }

    private final Handler mHideHandler = new Handler();

    TextView speedText;
    TextView steerText;;
    Switch useSensor;
    ImageButton frw;
    ImageButton bck;
    ImageButton left;
    ImageButton right;

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
        mSensorManager.registerListener(this, mSensor, 5000);

        orientation[1] = 0;
        orientation[2] = 0;

        this.initializeUI();
    }

    void initializeUI(){
        speedText = (TextView) findViewById(R.id.speed);
        steerText = (TextView) findViewById(R.id.steer);

        useSensor = (Switch) findViewById(R.id.useSensorForSpeed);
        useSensor.setChecked(useSensorForSpeed);


        useSensor.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean value) {
                useSensorForSpeed = value;
            }
        });

        bck = (ImageButton) findViewById(R.id.bck_btn);
        frw = (ImageButton) findViewById(R.id.frw_btn);
        left = (ImageButton) findViewById(R.id.left_btn);
        right = (ImageButton) findViewById(R.id.right_btn);
        bck.setOnTouchListener(new ImageButton.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent e) {
                int action = e.getAction();
                if(action == MotionEvent.ACTION_DOWN) {
                    speed = 255;
                    dir = Direction.BACK;
                } else if (action == MotionEvent.ACTION_UP) {
                    speed = 0;
                }
                sendMessage();
                return true;
            }
        });
        frw.setOnTouchListener(new ImageButton.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent e) {
                int action = e.getAction();
                if(action == MotionEvent.ACTION_DOWN) {
                    speed = 255;
                    dir = Direction.FORWARD;
                } else if (action == MotionEvent.ACTION_UP) {
                    speed = 0;
                }
                sendMessage();
                return true;
            }
        });
        left.setOnTouchListener(new ImageButton.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent e) {
                int action = e.getAction();
                if(action == MotionEvent.ACTION_DOWN) {
                    steerAngle = -100;
                } else if (action == MotionEvent.ACTION_UP) {
                    steerAngle = 0;
                }
                sendMessage();
                return true;
            }
        });
        right.setOnTouchListener(new ImageButton.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent e) {
                int action = e.getAction();
                if(action == MotionEvent.ACTION_DOWN) {
                    steerAngle = 100;
                } else if (action == MotionEvent.ACTION_UP) {
                    steerAngle = 0;
                }
                sendMessage();
                return true;
            }
        });

    }
    void updateUI() {
        double speedValue = speed;
        if(dir == Direction.BACK) {
            speedValue = -speedValue;
        }
        speedText.setText(String.valueOf(speedValue));
        steerText.setText(String.valueOf(steerAngle));
    }
    void sendMessage(){
        updateUI();
        if(null != create && null != create.connectionThread) {
            create.connectionThread.write(this.getCommand().getBytes());
        }
    }
    private String getCommand(){
        String command = dir.ordinal()+"|"+String.valueOf(speed)+"|"+steer.ordinal()+"|" + String.valueOf(steerAngle) + "|\n";
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

        // 45 degr = 100%, -45 deg = -100%, 0 deg = 0
        double XValue = x;
        double angleThreshold = Math.PI / 16;
        double angleCap = Math.PI / 4;
        double maxValue = Math.log(angleCap/angleThreshold);
        if(XValue < angleThreshold && XValue > -angleThreshold){
            steerAngle = 0;
        } else if(XValue > angleCap) {
            steerAngle = 100;
        } else if(XValue < -angleCap) {
            steerAngle = -100;
        } else if(XValue > angleThreshold) {
            steerAngle = (int) (100 * Math.log(XValue/angleThreshold)/maxValue);
        } else if(XValue < -angleThreshold) {

            steerAngle = (int) (-100 * Math.log(-XValue/angleThreshold)/maxValue);
        }
        if(useSensorForSpeed) {
            // speed
            double speedThreshold = Math.PI / 16;
            double maxSpeed = Math.PI / 4;
            double maxSpeedValue = 155;
            double maxLogValue = Math.log(maxSpeed/speedThreshold);
            if (y <= speedThreshold && y >= -speedThreshold) {
                speed = 0;
            } else if (y > maxSpeed) {
                dir = Direction.FORWARD;
                speed = 255;
            } else if (y < -maxSpeed) {
                dir = Direction.BACK;
                speed = 255;
            } else if (y > speedThreshold) {
                dir = Direction.FORWARD;
                speed = (int) (maxSpeedValue * Math.log(y/speedThreshold)/maxLogValue);
            } else if (y < -speedThreshold) {
                dir = Direction.BACK;
                speed = (int) (maxSpeedValue * Math.log(-y/speedThreshold)/maxLogValue);
            }
        }

        this.sendMessage();
    }
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
