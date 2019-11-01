package com.led.ledetext;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.led.ledetext.util.DataUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android_serialport_api.SerialPort;

/**
 * 通过串口用于接收或发送数据
 */

public class SerialPortUtil {

    private final static String TAG = SerialPortUtil.class.getName();

    private SerialPort serialPort = null;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;
    private ReceiveThread mReceiveThread = null;
    private boolean isStart = false;

    private String tempStr = "";
    private int allLength = 0;

    /**
     * 打开串口，接收数据
     * 通过串口，接收单片机发送来的数据
     */
    public void openSerialPort(String device, int baudRate) {
        if (device.isEmpty()) {
            try {
                throw new Exception("设备为空");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            serialPort = new SerialPort(new File(device), baudRate, 0);
            //调用对象SerialPort方法，获取串口中"读和写"的数据流
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();
            isStart = true;

        } catch (IOException e) {
            e.printStackTrace();
        }
        getSerialPort();
    }

    /**
     * 关闭串口
     * 关闭串口中的输入输出流
     */
    public void closeSerialPort() {
        Log.i("test", "关闭串口");
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            isStart = false;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 发送数据
     * 通过串口，发送数据到单片机
     *
     * @param data 要发送的数据
     */
    public void sendSerialPort(String data) {
        System.out.println("send Serial data is:" + data);
        try {
            byte[] sendData = DataUtils.hexToByteArr(data);
            outputStream.write(sendData);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getSerialPort() {
        if (mReceiveThread == null) {

            mReceiveThread = new ReceiveThread();
        }
        mReceiveThread.start();
    }

    /**
     * 接收串口数据的线程
     */
    private class ReceiveThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (isStart) {
                if (inputStream == null) {
                    return;
                }
                byte[] readData = new byte[1024];

                try {
                    StringBuilder sb = new StringBuilder();
                    // 为了一次性读完，做了延迟读取
                    if (inputStream.available() > 0) {
                        SystemClock.sleep(200);
                        int size = inputStream.read(readData);
                        if (size > 0) {
                            String readString = DataUtils.byteArrToHex(readData, 0, size);
                            Log.d(TAG, "rec: " + readString);
                            EventBus.getDefault().post(readString);
                            sb.setLength(0);
                            ackOK();
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

//                try {
////                    int size = inputStream.read(readData);
////                    Log.d(TAG, "size: " + size);
////                    String str = DataUtils.byteArrToHex(readData, 0, size);
//                    byte[] buffer = new byte[1024];
//                    int size = inputStream.read(buffer);
//                    byte[] readBytes = new byte[size];
//                    System.arraycopy(buffer, 0, readBytes, 0, size);
//                    String str = DataUtils.bytesToHexString(readBytes);
//                    System.out.println("received data => " + new String(readBytes));
//                    Log.d(TAG, "rec data: " + str);
//                    if (!(tempStr.startsWith("0000FF") || tempStr.startsWith("0000ff"))) {
//                        tempStr = tempStr + str;
//                        Log.d(TAG, "tempStr 111: " + tempStr);
//                    } else {
//                        tempStr = tempStr + str;
//                        if (tempStr.length() >= 8) {
//                            allLength = Integer.parseInt(tempStr.substring(6, 8), 16);
//                            if (tempStr.length() == allLength * 2 + 14) {
//                                Log.d(TAG, "final str: " + tempStr);
//                                EventBus.getDefault().post(tempStr);
//                                ackOK();
//                                tempStr = "";
//                            }
//                        }
//                        Log.d(TAG, "tempStr 222: " + tempStr);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }

        }
    }

    private void ackOK() {
        sendSerialPort("0000FF00FF00");
    }
}
