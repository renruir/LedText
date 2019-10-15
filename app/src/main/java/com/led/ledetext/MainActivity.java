package com.led.ledetext;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sunfusheng.marqueeview.MarqueeView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android_serialport_api.SerialPortFinder;

public class MainActivity extends AppCompatActivity implements RecSerialInfoCallback, View.OnClickListener {

    private static final String TAG = MainActivity.class.getName();

    private Application mApplication;
    private String[] devDevices;
    private SerialPortFinder mSerialPortFinder;
    private ScrollTextView line1;
    private ScrollTextView line2;
    private Button testBtn;

    private SerialPortUtil serialPortUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initApplication();

        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);

        line1 = (ScrollTextView)findViewById(R.id.line1);
        line2 = (ScrollTextView) findViewById(R.id.line2);
        testBtn = (Button)findViewById(R.id.test_serial);
        testBtn.setOnClickListener(this);

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
        initSerial("/dev/ttyS3", 38400);
    }

    private void initSerial(final String device,final int baudRate){
        serialPortUtil = new SerialPortUtil();
        serialPortUtil.openSerialPort(device, baudRate);
    }

    @Override
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void recSerialData(String data) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(String message) {
        Log.d(TAG, "Event: " + message);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.test_serial:
                serialPortUtil.sendSerialPort("01010100");
                break;
        }
    }
}
