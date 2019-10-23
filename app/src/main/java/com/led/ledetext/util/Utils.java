package com.led.ledetext.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Color;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;

public class Utils {
    private final static String TAG = Utils.class.getName();

    public final static String FONT_COLOR_RED = "01000000";
    public final static String FONT_COLOR_GREEN = "00010000";
    public final static String FONT_COLOR_ORANGE = "010100000";
    public final static String FONT_COLOR_BLUE = "00000100";
    public final static String FONT_COLOR_PURPLE = "01000100";
    public final static String FONT_COLOR_CYAN = "01010100";
    public final static String FONT_COLOR_WHITE = "00010100";

    public final static int SCROLL_STOP = 1000;
    public final static int SCROLL_TO_LEFT = 1001;
    public final static int SCROLL_TO_UP = 1002;
    public final static int SCROLL_TO_DOWN = 1003;

    public static int getFontColor(String colorString) {
        Log.d(TAG, "getFontColor: " + colorString);
        if (FONT_COLOR_RED.equals(colorString)) {
            return Color.RED;
        } else if (FONT_COLOR_GREEN.equals(colorString)) {
            return Color.GREEN;
        } else if (FONT_COLOR_ORANGE.equals(colorString)) {
            return Color.YELLOW;
        } else if (FONT_COLOR_BLUE.equals(colorString)) {
            return Color.BLUE;
        } else if (FONT_COLOR_PURPLE.equals(colorString)) {
            return Color.parseColor("#800080");
        } else if (FONT_COLOR_CYAN.equals(colorString)) {
            return Color.parseColor("#00FFFF");
        } else if (FONT_COLOR_WHITE.equals(colorString)) {
            return Color.WHITE;
        } else {
            return Color.BLACK;
        }
    }

    public static String toStringHex2(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(
                        i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "gb2312");// UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        Log.d(TAG, "toStringHex2: " + s);
        return s;
    }

    //获取是否屏幕自动调节
    public static boolean isAutoBrightness(ContentResolver aContentResolver) {
        boolean automicBrightness = false;
        try {
            automicBrightness = Settings.System.getInt(aContentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return automicBrightness;
    }


    /**
     * 获取屏幕的亮度
     *
     * @param activity
     * @return
     */
    public static int getScreenBrightness(Activity activity) {
        int nowBrightnessValue = 0;
        ContentResolver resolver = activity.getContentResolver();
        try {
            nowBrightnessValue = android.provider.Settings.System.getInt(
                    resolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nowBrightnessValue;
    }

    /**
     * 设置亮度
     *
     * @param activity
     * @param brightness
     */
    public static void setBrightness(Activity activity, int brightness) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
        Log.d(TAG, "set  lp.screenBrightness == " + lp.screenBrightness);
        activity.getWindow().setAttributes(lp);
    }

    /**
     * 停止自动亮度调节
     *
     * @param activity
     */
    public static void stopAutoBrightness(Activity activity) {
        Settings.System.putInt(activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }

    public static void main(String[] args) {
        LRC("A1FF0029000F");
    }

    public static int LRC(String hexdata) {
        if (hexdata == null || hexdata.equals("")) {
            return 0;
        }
        hexdata = hexdata.replaceAll(" ", "");
        int total = 0;
        int len = hexdata.length();
        if (len % 2 != 0) {
            return 0;
        }
        int num = 0;
        while (num < len) {
            String s = hexdata.substring(num, num + 2);
            total += Integer.parseInt(s, 16);
            num = num + 2;
        }

        int m = 0x100 - total % 0x100;
        System.out.println("lrc: "+intToHex(m));
        return m;
    }


    public static String hexInt(int total) {
        int a = total / 256;
        int b = total % 256;
        if (a > 255) {
            return hexInt(a) + format(b);
        }
        return format(a) + format(b);
    }

    private static String format(int hex) {
        String hexa = Integer.toHexString(hex);
        int len = hexa.length();
        if (len < 2) {
            hexa = "0" + hexa;
        }
        return hexa;
    }

    public static String intToHex(int n) {
        StringBuffer s = new StringBuffer();
        String a;
        char []b = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        while(n != 0){
            s = s.append(b[n%16]);
            n = n/16;
        }
        a = s.reverse().toString();
        return a;
    }

}
