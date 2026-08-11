package com.zgkxzx.modbustest;

import android.util.Log;

import com.zgkxzx.modbus4And.serial.SerialPortWrapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class UsbSerialPort4RTU implements SerialPortWrapper {
    private final static String TAG = UsbSerialPort4RTU.class.getSimpleName();
    ByteArrayOutputStream outputStream;
    ByteArrayInputStream inputStream;
    byte[] buffer = new byte[128];

    @Override
    public void close() throws Exception {
        outputStream.close();
        Log.d(TAG,"close called");
    }

    @Override
    public void open() throws Exception {
        outputStream = new ByteArrayOutputStream();
        inputStream = new ByteArrayInputStream(buffer);
        Log.d(TAG,"open called");
    }

    @Override
    public InputStream getInputStream() {
        Log.d(TAG,"getInputStream called");
        return inputStream;
    }

    @Override
    public OutputStream getOutputStream() {
        Log.d(TAG,"getOutputStream called");
        return outputStream;
    }

    @Override
    public int getBaudRate() {
        Log.d(TAG,"getBaudRate called");
        return 115200;
    }

    @Override
    public int getFlowControlIn() {
        Log.d(TAG,"getFlowControlIn called");
        return 0;
    }

    @Override
    public int getFlowControlOut() {
        Log.d(TAG,"getFlowControlOut called");
        return 0;
    }

    @Override
    public int getDataBits() {
        Log.d(TAG,"getDataBits called");
        return 8;
    }

    @Override
    public int getStopBits() {
        Log.d(TAG,"getStopBits called");
        return 1;
    }

    @Override
    public int getParity() {
        Log.d(TAG,"getParity called");
        return 0;
    }
}
