package com.zgkxzx.modbustest;

import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.Arrays;

public class SerialPortInputStream implements SerialInputOutputManager.Listener {
    UsbSerialPort mPort;
    private byte[] buffer;
    private int count;
    final int capacity = 1024;
    SerialInputOutputManager usbIoManager;
    public SerialPortInputStream(UsbSerialPort port) {
        mPort = port;
        usbIoManager = new SerialInputOutputManager(port, this);
        this.buffer = new byte[capacity];
        this.count = 0;
    }

    @Override
    public void onNewData(byte[] receivedData) {
//        if ((off < 0) || (off > b.length) || (len < 0) ||
//                ((off + len) > b.length) || ((off + len) < 0)) {
//            throw new IndexOutOfBoundsException();
//        } else if (len == 0) {
//            return;
//        }

        try {
            ensureCapacity(receivedData.length);
            System.arraycopy(receivedData, 0, buffer, count, receivedData.length);
            count += receivedData.length;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onRunError(Exception e) {

    }

    // Dynamic resizing logic (similar to how ArrayList or standard ByteArrayOutputStream grows)
    private void ensureCapacity(int minCapacity) throws IOException {
        if (minCapacity - buffer.length > 0) {
            grow(minCapacity);
        }
    }

    private void grow(int minCapacity) {
        int oldCapacity = buffer.length;
        int newCapacity = oldCapacity << 1; // Double the capacity
        if (newCapacity - minCapacity < 0) {
            newCapacity = minCapacity;
        }
        buffer = Arrays.copyOf(buffer, newCapacity);
    }

    // Retrieves a copy of the valid written bytes
    public byte[] toByteArray() {
        return Arrays.copyOf(buffer, count);
    }

    // Returns the total number of bytes written so far
    public int size() {
        return count;
    }

    // Resets the counter so the existing buffer memory can be reused
    public void reset() {
        count = 0;
    }
}

