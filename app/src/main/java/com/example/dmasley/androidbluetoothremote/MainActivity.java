package com.example.dmasley.androidbluetoothremote;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends AppCompatActivity {
    Button onButton;
    Button offButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onButton = (Button) findViewById(R.id.on);
        offButton = (Button) findViewById(R.id.off);

        onButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        offButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
