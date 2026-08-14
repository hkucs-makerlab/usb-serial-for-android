package com.zgkxzx.modbustest;

import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class SerialPortInputStream extends InputStream implements SerialInputOutputManager.Listener {
    UsbSerialPort mPort;
    private byte[] buffer;
    private int count;
    private int position = 0;

    SerialInputOutputManager usbIoManager;

    public SerialPortInputStream(UsbSerialPort port) {
        buffer = new byte[1024];
        count = 0;

        mPort = port;
        usbIoManager = new SerialInputOutputManager(port, this);
        usbIoManager.start();

    }

    @Override
    public void onRunError(Exception e) {

    }

    @Override
    public void onNewData(byte[] receivedData) {

        if (receivedData.length - buffer.length > 0) {
            grow(receivedData.length);
        }
        System.arraycopy(receivedData, 0, buffer, count - position, receivedData.length);
        count += receivedData.length;
    }


    private void grow(int minCapacity) {
        int oldCapacity = buffer.length;
        int newCapacity = oldCapacity << 1; // Double the capacity
        if (newCapacity - minCapacity < 0) {
            newCapacity = minCapacity;
        }
        buffer = Arrays.copyOf(buffer, newCapacity);
    }

    // Returns the total number of bytes written so far
    public int size() {
        return count - position;
    }


    public synchronized int read() throws IOException {
        // Check if we have reached the end of the array
        if (position >= count) {
            return -1;
        }

        // Read the byte and increment the position index
        byte nextByte = buffer[position++];

        // Bitwise AND operation (& 0xFF) converts a signed byte (-128 to 127)
        // into an unsigned integer value (0 to 255) as required by the API specs.
        return nextByte & 0xFF;
    }


    // Retrieves a copy of the valid written bytes
    public byte[] toByteArray() {
        byte[] r = Arrays.copyOfRange(buffer, position, size());
        position = count = 0;
        return r;
    }
}

