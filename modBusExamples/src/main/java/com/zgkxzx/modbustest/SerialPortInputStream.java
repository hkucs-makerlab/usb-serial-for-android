package com.zgkxzx.modbustest;

import android.util.Log;

import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;

public class SerialPortInputStream extends InputStream implements SerialInputOutputManager.Listener {
    private final static String TAG = SerialPortInputStream.class.getSimpleName();
    UsbSerialPort mPort;
    private byte[] buffer;
    private int count;
    private int head;
    private int tail;
    SerialInputOutputManager usbIoManager;

    public SerialPortInputStream(UsbSerialPort port) {
        buffer = new byte[4096];
        count = 0;
        head = 0;
        tail = 0;
        mPort = port;
        usbIoManager = new SerialInputOutputManager(port, this);
        usbIoManager.start();

    }

    @Override
    public void onRunError(Exception e) {
        Log.d(TAG, "onRunError " + e.toString());
    }


    public void onNewData(byte[] receivedData) {
        for (int i=0; i < receivedData.length; i++) {
            buffer[tail] = receivedData[i];
            tail = (tail + 1) % buffer.length;
            count++;
        }
        //Log.d(TAG, "on onNewData(), len = " + receivedData.length);
    }

    public void close() throws IOException {
        usbIoManager.stop();
    }

    public synchronized int available() {
        // Log.d(TAG, "on available");
        return count;
    }

    public synchronized int read() throws IOException {
        //Log.d(TAG, "on read() count = " +count);
        if (count == 0) {
            return -1;
        }
        byte nextByte = buffer[head];
        head = (head + 1) % buffer.length;
        count--;
        return nextByte & 0xFF;
    }

}

