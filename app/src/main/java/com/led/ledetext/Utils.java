package com.led.ledetext;

import android.graphics.Color;
import android.util.Log;

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
    public final static int SCROLL_TO_UP =1002;
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


}
