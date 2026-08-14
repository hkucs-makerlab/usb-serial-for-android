package com.zgkxzx.modbustest;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.zgkxzx.modbus4And.serial.SerialPortWrapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class UsbSerialPortRTU implements SerialPortWrapper {
    private final static String TAG = UsbSerialPortRTU.class.getSimpleName();
    private static final int WRITE_WAIT_MILLIS = 1000;

    UsbSerialPort mPort;
    UsbDeviceConnection mConnection;
    SerialPortOutputStream outputStream;
    ByteArrayInputStream inputStream;
    byte[] buffer = new byte[128];
    boolean ready = false;

    public UsbSerialPortRTU(UsbManager manager, UsbDevice device) {
        super();
        UsbSerialDriver driver = UsbSerialProber.getDefaultProber().probeDevice(device);
        mConnection = manager.openDevice(device);
        mPort = driver.getPorts().get(0);

        Log.d(TAG, "connection is null");
        /*
        mManager = manager;
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        if(availableDrivers.size() > 0) {
            ListIterator iterator = availableDrivers.listIterator();
            while(iterator.hasNext()) {
                UsbSerialDriver driver = (UsbSerialDriver) iterator.next();
                Log.d(TAG, "driver: "+driver.toString());
                List<UsbSerialPort> ports =driver.getPorts();
                Log.d(TAG, "number of ports: "+ports.size());
                ListIterator iterator2 = ports.listIterator();
                while(iterator2.hasNext()) {
                    UsbSerialPort port = (UsbSerialPort) iterator2.next();
                    Log.d(TAG, "port: "+port.toString());
                }
            }
        }

        UsbSerialDriver driver = availableDrivers.get(0);
        UsbSerialPort port = driver.getPorts().get(0);
        UsbDeviceConnection connection = mManager.openDevice(driver.getDevice());
        if (connection != null ) {
            try {
                port.open(connection);
                port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
                port.write("hello".getBytes(), WRITE_WAIT_MILLIS);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            Log.d(TAG, "connection is null");
        }
         */
    }

    @Override
    public void close() throws Exception {
        if (mConnection == null) return;
        if (outputStream != null) {
            outputStream.close();
        }
        mPort.close();
        mConnection.close();

        Log.d(TAG, "close called");
    }

    @Override
    public void open() throws Exception {
        if (mConnection == null) {
            return;
        }
        try {
            mPort.open(mConnection);
            mPort.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            outputStream = new SerialPortOutputStream((mPort));
        } catch (IOException e) {
            mPort = null;
            throw new RuntimeException(e);
        }
        inputStream = new ByteArrayInputStream(buffer);
        Log.d(TAG, "open called");
    }

    @Override
    public InputStream getInputStream() {
        if (mConnection == null) {
            return null;
        }
        Log.d(TAG, "getInputStream called");
        return inputStream;
    }

    @Override
    public OutputStream getOutputStream() {
        if (mConnection == null) {
            return null;
        }
        Log.d(TAG, "getOutputStream called");
        return outputStream;
    }

    @Override
    public int getBaudRate() {
        Log.d(TAG, "getBaudRate called");
        return 115200;
    }

    @Override
    public int getFlowControlIn() {
        Log.d(TAG, "getFlowControlIn called");
        return 0;
    }

    @Override
    public int getFlowControlOut() {
        Log.d(TAG, "getFlowControlOut called");
        return 0;
    }

    @Override
    public int getDataBits() {
        Log.d(TAG, "getDataBits called");
        return 8;
    }

    @Override
    public int getStopBits() {
        Log.d(TAG, "getStopBits called");
        return 1;
    }

    @Override
    public int getParity() {
        Log.d(TAG, "getParity called");
        return 0;
    }
}
