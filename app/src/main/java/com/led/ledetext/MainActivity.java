package com.led.ledetext;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.sunfusheng.marqueeview.MarqueeView;

import android_serialport_api.SerialPortFinder;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    private Application mApplication;
    private String[] devDevices;
    private SerialPortFinder mSerialPortFinder;
    private ScrollTextView line1;
    private ScrollTextView line2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initApplication();

        setContentView(R.layout.activity_main);
        line1 = (ScrollTextView)findViewById(R.id.line1);
        line2 = (ScrollTextView) findViewById(R.id.line2);

        line1.setText("欢迎来深圳！");
        line2.setText("Welcome to ShenZhen!");
        line1.setSpeed(4);
        line2.setSpeed(4);
    }

    private void initApplication(){
        mApplication = (Application) getApplication();
        mSerialPortFinder = mApplication.mSerialPortFinder;
        devDevices = mSerialPortFinder.getAllDevices();
        for(String device:devDevices){
            Log.d(TAG, "device: " + device);
        }
    }
}
