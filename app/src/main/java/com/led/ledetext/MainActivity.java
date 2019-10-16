package com.led.ledetext;

import androidx.appcompat.app.AppCompatActivity;

import android.icu.lang.UProperty;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getName();

    private static final int LINE_POS_START = 20;
    private static final int COLOR_POSITION_START = 24;
    private static final int COLOR_POSITION_END = 32;
    private static final int SCROLL_MODEL_START = 32;
    private static final int TEXT_NUM_START = 34;
    private static final int TEXT_NUM_END = 36;
    private static final int LINE1_POSITION_START = 38;


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

        line1 = (ScrollTextView) findViewById(R.id.line1);
        line2 = (ScrollTextView) findViewById(R.id.line2);
        testBtn = (Button) findViewById(R.id.test_serial);
        testBtn.setOnClickListener(this);

        line1.setText("欢迎来深圳！");
        line2.setText("Welcome to ShenZhen!");
        line1.setSpeed(4);
        line2.setSpeed(4);
    }

    private void initApplication() {
        mApplication = (Application) getApplication();
        mSerialPortFinder = mApplication.mSerialPortFinder;
        devDevices = mSerialPortFinder.getAllDevices();
        for (String device : devDevices) {
            Log.d(TAG, "device: " + device);
        }
        initSerial("/dev/ttyS3", 38400);
    }

    private void initSerial(final String device, final int baudRate) {
        serialPortUtil = new SerialPortUtil();
        serialPortUtil.openSerialPort(device, baudRate);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void recSerialData(String data) {
        formatRecData(data);
    }

    private TextModel formatRecData(String data) {
        TextModel textModel = new TextModel();
        int startPos = Integer.parseInt(data.substring(LINE_POS_START, LINE_POS_START + 2), 16);
        textModel.setStartpos(startPos);
        int color = Utils.getFontColor(data.substring(COLOR_POSITION_START, COLOR_POSITION_END));
        textModel.setColor(color);
        int line1Num = Integer.parseInt(data.substring(TEXT_NUM_START, TEXT_NUM_END), 16);
        String line1StrHex = data.substring(LINE1_POSITION_START, LINE1_POSITION_START + line1Num * 2);
        String line1Text = Utils.toStringHex2(line1StrHex);
        int line2Num = Integer.parseInt(data.substring(TEXT_NUM_START + 2, TEXT_NUM_START + 2 + 2), 16);
        String line2StrHex = data.substring(LINE1_POSITION_START + line1Num * 2, LINE1_POSITION_START + line1Num * 2 + line2Num * 2);
        String line2Text = Utils.toStringHex2(line2StrHex);
        textModel.setLine1(line1Text);
        textModel.setLine2(line2Text);
        int scrollModel = Integer.parseInt(data.substring(SCROLL_MODEL_START, SCROLL_MODEL_START + 2), 16);
        switch (scrollModel) {
            case 1:
                textModel.setLine1ScrollModel(Utils.SCROLL_STOP);
                textModel.setLine2ScrollModel(Utils.SCROLL_TO_LEFT);
                break;
            case 2:
                textModel.setLine1ScrollModel(Utils.SCROLL_STOP);
                textModel.setLine2ScrollModel(Utils.SCROLL_STOP);
                break;
            case 3:
                textModel.setLine1ScrollModel(Utils.SCROLL_TO_LEFT);
                textModel.setLine2ScrollModel(Utils.SCROLL_TO_LEFT);
                break;
            case 4:
                textModel.setLine1ScrollModel(Utils.SCROLL_TO_LEFT);
                textModel.setLine2ScrollModel(Utils.SCROLL_STOP);
                break;
            case 5:
                textModel.setLine1ScrollModel(Utils.SCROLL_TO_UP);
                textModel.setLine2ScrollModel(Utils.SCROLL_TO_UP);
                break;
            case 6:
                textModel.setLine1ScrollModel(Utils.SCROLL_TO_DOWN);
                textModel.setLine2ScrollModel(Utils.SCROLL_TO_DOWN);
                break;
            default:
                textModel.setLine1ScrollModel(Utils.SCROLL_TO_LEFT);
                textModel.setLine2ScrollModel(Utils.SCROLL_TO_LEFT);
        }
        Log.d(TAG, "textModel: " + textModel.toString());
        updateText(textModel);
        return textModel;
    }

    private void updateText(TextModel textModel) {
        line1.updateText(textModel, 1);
        line2.updateText(textModel, 2);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(String message) {
        Log.d(TAG, "Event: " + message);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.test_serial:
//                serialPortUtil.sendSerialPort("01010100");
                recSerialData("0000FF2A2AA1FF0024000D2100010000060A12BBB6D3ADC0B4C4CFBEA957454C434F4D4520544F204E414E4A494E476800");
//                recSerialData("0000FF2A2AA1FF0024000D2100010000050A12BBB6D3ADC0B4C4CFBEA957454C434F4D4520544F204E414E4A494E476700");
                break;
        }
    }
}
