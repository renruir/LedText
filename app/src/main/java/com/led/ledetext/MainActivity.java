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
    private MarqueeView line1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setContentView(R.layout.activity_main);
        line1 = (MarqueeView)findViewById(R.id.line1);

        mApplication = (Application) getApplication();
        mSerialPortFinder = mApplication.mSerialPortFinder;

        devDevices = mSerialPortFinder.getAllDevices();
        for(String device:devDevices){
            Log.d(TAG, "device: " + device);
        }

        line1.startWithText("在内容供给层面，为新玩家带来发展机会");
    }
}
