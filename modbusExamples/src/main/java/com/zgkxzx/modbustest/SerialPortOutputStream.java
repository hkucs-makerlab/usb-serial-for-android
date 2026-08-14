package com.zgkxzx.modbustest;

import com.hoho.android.usbserial.driver.UsbSerialPort;

import java.io.IOException;
import java.io.OutputStream;

public class SerialPortOutputStream extends OutputStream {
    UsbSerialPort mPort;

    public SerialPortOutputStream(UsbSerialPort port) {
        mPort = port;
    }
    @Override
    public void write(int b) throws IOException {
        byte[] buff = {(byte)b};
        mPort.write(buff, 1,200);
    }
    public void write(byte[] b, int off, int len) throws IOException {
        mPort.write(b,len,100);
    }
}
