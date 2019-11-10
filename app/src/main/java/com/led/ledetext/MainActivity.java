package com.led.ledetext;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.led.ledetext.bean.DaoSession;
import com.led.ledetext.bean.TextBean;
import com.led.ledetext.bean.TextBeanDao;
import com.led.ledetext.util.Utils;
import com.led.ledetext.view.ScrollTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android_serialport_api.SerialPortFinder;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getName();

    private static final int FUNCTION_INDEX_START = 16;
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
    private ScrollTextView singleLine;
    private LinearLayout singleLayout;
    private Button testBtn;

    private SerialPortUtil serialPortUtil;
    private DaoSession daoSession;
    private TextBeanDao textBeanDao;

    private FrameLayout fullScreenTest;
    private LinearLayout scrollLayout;

    private EditText testInput;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1){
                fullScreenTest.setBackgroundColor(Utils.colorTest[msg.arg1]);
            } else if(msg.what == 2){
                fullScreenTest.setVisibility(View.GONE);
                scrollLayout.setVisibility(View.VISIBLE);
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initApplication();

        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);

        line1 = (ScrollTextView) findViewById(R.id.line1);
        line2 = (ScrollTextView) findViewById(R.id.line2);
        singleLine = (ScrollTextView)findViewById(R.id.single_line);
        singleLayout = (LinearLayout)findViewById(R.id.single_layout);
        testBtn = (Button) findViewById(R.id.test_serial);
        testInput = (EditText) findViewById(R.id.test_input);
        testBtn.setOnClickListener(this);
        fullScreenTest = (FrameLayout) findViewById(R.id.full_srceen_layout);
        scrollLayout = (LinearLayout) findViewById(R.id.scroll_layout);

        int index = getIndex();
        Log.d(TAG, "index: " + index);
        if(index == -1){
            line1.setText("欢迎来到深圳！");
            line2.setText("Welcome to ShenZhen!");
        } else {
            List<TextBean> result = textBeanDao.queryBuilder().where(TextBeanDao.Properties.Num.eq(index)).list();
            if (result.size() == 0) {
                Log.d(TAG, "未获取到信息号为" + index + "的驻留信息");
            }
            Log.d(TAG, "init: " + result.get(0).getLine1());
            updateText(result.get(0));
        }

        line1.setSpeed(4);
        line2.setSpeed(4);
        singleLine.setSpeed(4);
    }

    private void initApplication() {
        mApplication = (Application) getApplication();
        mSerialPortFinder = mApplication.mSerialPortFinder;
        devDevices = mSerialPortFinder.getAllDevices();
        for (String device : devDevices) {
            Log.d(TAG, "device: " + device);
        }
//        initSerial("/dev/ttyS4", 19200); //test
        initSerial("/dev/ttyS1", 19200);

        daoSession = ((Application) getApplicationContext()).getDaoSession();
        textBeanDao = daoSession.getTextBeanDao();
    }

    private void initSerial(final String device, final int baudRate) {
        serialPortUtil = new SerialPortUtil();
        serialPortUtil.openSerialPort(device, baudRate);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void recSerialData(String data) {
        formatRecData(data);
    }

    private void formatRecData(String data) {
        if (!data.startsWith("0000FF")) {
            return;
        }
        try {
            TextBean textBean;
            int function = Integer.parseInt(data.substring(FUNCTION_INDEX_START, FUNCTION_INDEX_START + 2), 16);
            Log.d(TAG, "function: " + function);

            switch (function) {
                case 04:
                    //查询版本
                    serialPortUtil.sendSerialPort("0000FF1010A1FF000500312E30305F3132303731360C00");
                    break;
                case 224:
                    //模块自测
                    Log.d(TAG, "formatRecData: " + data);
                    fullScreenTest.setVisibility(View.VISIBLE);
                    scrollLayout.setVisibility(View.INVISIBLE);
                    selfTest();
                    break;
                case 40:
                    //查询亮度
                    int screenBrightness = Utils.getScreenBrightness(this);
                    int brightness = (int) screenBrightness / 17;
                    String s = "A1FF0029000" + Integer.toHexString(brightness);
                    int lrc = Utils.LRC(s);
                    String r = "0000FF0606" + s + Utils.intToHex(lrc) + "00";
                    Log.d(TAG, "result r : " + r);
                    serialPortUtil.sendSerialPort(r);
                    break;
                case 34:
                    //显示驻留信息
                    int index = Integer.parseInt(data.substring(18, 20), 16);
                    Log.d(TAG, "index: " + index);
                    List<TextBean> result = textBeanDao.queryBuilder().where(TextBeanDao.Properties.Num.eq(index)).list();
                    if (result.size() == 0) {
                        Log.d(TAG, "未获取到信息号为" + index + "的驻留信息");
                        break;
                    }
                    Log.d(TAG, "formatRecData: " + result.get(0).getLine1());
                    updateText(result.get(0));
                    serialPortUtil.sendSerialPort("0000FF0505A1FF0023003D00");
                    saveIndex(index);
                    break;
                case 38:
                    //亮度调节
                    int preBrightness = Integer.parseInt(data.substring(18, 20), 16);
                    int bt = preBrightness * 17;
                    Log.d(TAG, "brightness: " + bt);
                    setScreenBrightness(bt);
                    serialPortUtil.sendSerialPort("0000FF0505A1FF0027003900");
                    break;
                case 36:
                    //临时显示
                    textBean = processText(data);
                    updateText(textBean);
                    serialPortUtil.sendSerialPort("0000FF0505A1FF0025003B00");
                    break;
                case 32:
                    //下载驻留信息
                    textBean = processText(data);
                    long i = textBeanDao.insertOrReplace(textBean);
                    Log.d(TAG, "download: " + i);
                    serialPortUtil.sendSerialPort("0000FF0505A1FF0021003F00");
                    saveIndex(textBean.getNum());
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private TextBean processText(String data) {
        TextBean textBean = new TextBean();
        int index = Integer.parseInt(data.substring(18, 20), 16);
        textBean.setNum(index);
        int startPos = Integer.parseInt(data.substring(LINE_POS_START, LINE_POS_START + 2), 16);
        textBean.setStartpos(startPos);
        int color = Utils.getFontColor(data.substring(COLOR_POSITION_START, COLOR_POSITION_END));
        textBean.setColor(color);
        int line1Num = Integer.parseInt(data.substring(TEXT_NUM_START, TEXT_NUM_END), 16);
        String line1StrHex = data.substring(LINE1_POSITION_START, LINE1_POSITION_START + line1Num * 2);
        String line1Text = Utils.toStringHex2(line1StrHex);
        int line2Num = Integer.parseInt(data.substring(TEXT_NUM_START + 2, TEXT_NUM_START + 2 + 2), 16);
        String line2StrHex = data.substring(LINE1_POSITION_START + line1Num * 2, LINE1_POSITION_START + line1Num * 2 + line2Num * 2);
        String line2Text = Utils.toStringHex2(line2StrHex);
        textBean.setLine1(line1Text);
        textBean.setLine2(line2Text);
        int scrollModel = Integer.parseInt(data.substring(SCROLL_MODEL_START, SCROLL_MODEL_START + 2), 16);
        switch (scrollModel) {
            case 1:
                textBean.setLine1ScrollModel(Utils.SCROLL_STOP);
                textBean.setLine2ScrollModel(Utils.SCROLL_TO_LEFT);
                break;
            case 2:
                textBean.setLine1ScrollModel(Utils.SCROLL_STOP);
                textBean.setLine2ScrollModel(Utils.SCROLL_STOP);
                break;
            case 3:
                textBean.setLine1ScrollModel(Utils.SCROLL_TO_LEFT);
                textBean.setLine2ScrollModel(Utils.SCROLL_TO_LEFT);
                break;
            case 4:
                textBean.setLine1ScrollModel(Utils.SCROLL_TO_LEFT);
                textBean.setLine2ScrollModel(Utils.SCROLL_STOP);
                break;
            case 5:
                textBean.setLine1ScrollModel(Utils.SCROLL_TO_UP);
                textBean.setLine2ScrollModel(Utils.SCROLL_TO_UP);
                break;
            case 6:
                textBean.setLine1ScrollModel(Utils.SCROLL_TO_DOWN);
                textBean.setLine2ScrollModel(Utils.SCROLL_TO_DOWN);
                break;
            default:
                textBean.setLine1ScrollModel(Utils.SCROLL_TO_LEFT);
                textBean.setLine2ScrollModel(Utils.SCROLL_TO_LEFT);
        }
        Log.d(TAG, "textBean: " + textBean.toString());
        return textBean;
    }

    private void updateText(TextBean textBean) {
        if(textBean.getLine2().isEmpty()){
            line1.setVisibility(View.GONE);
            line2.setVisibility(View.GONE);
            singleLayout.setVisibility(View.VISIBLE);
            singleLine.updateText(textBean, 1);
        } else {
            line1.setVisibility(View.VISIBLE);
            line2.setVisibility(View.VISIBLE);
            singleLayout.setVisibility(View.GONE);
            line1.updateText(textBean, 1);
            line2.updateText(textBean, 2);
        }

    }

    private void selfTest() {
        final Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            int count = 0;
            int colorIndex = 0;

            @Override
            public void run() {
                count += 5;
                colorIndex++;
                if (colorIndex == 6) {
                    colorIndex = 0;
                }

                if (count >= 60) {
                    Message message = new Message();
                    message.what = 2;
                    handler.sendMessage(message);
                    timer.cancel();
                } else {
                    Message message = new Message();
                    message.what = 1;
                    message.arg1 = colorIndex;
                    handler.sendMessage(message);
                }
            }
        };
        timer.schedule(timerTask, 0, 5000);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBusMsg(String message) {
        Log.d(TAG, "Event: " + message);
        recSerialData(message);
    }

    private void setScreenBrightness(int brightness) {

        if (Utils.isAutoBrightness(this.getContentResolver())) {
            Utils.stopAutoBrightness(this);
        }
        Utils.setBrightness(this, brightness);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.test_serial:
//                serialPortUtil.sendSerialPort("01010100");

//                recSerialData("0000FF0505A1FF00260AD000");
//                recSerialData("0000FF0505A1FF002201C300");
                //下载驻留信息
//                recSerialData(testInput.getText().toString());
//                recSerialData("0000FF3838A1FF002001011501000000011416BBB6D3ADB3CBD7F8C9EEDBDAB9ECB5C0BDBBCDA857454C434F4D4520544F20535A204D4554524F2121215A00");
//                int lrc = Utils.LRC("A1FF0029000F");
//                Log.d(TAG, "lrc: " + lrc);
//                int preBrightness = Integer.parseInt(testInput.getText().toString());
//                int bt = preBrightness * 17;
//                Log.d(TAG, "brightness: " + bt);
//                setScreenBrightness(bt);
                recSerialData(testInput.getText().toString());
                break;
        }
    }

    private void saveIndex(int index){
        SharedPreferences sharedPreferences= getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("index", index);
        editor.commit();
    }

    private int getIndex(){
        SharedPreferences sharedPreferences= getSharedPreferences("data", Context .MODE_PRIVATE);
        int index=sharedPreferences.getInt("index",-1);
        return index;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
