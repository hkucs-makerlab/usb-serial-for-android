package com.zgkxzx.modbustest;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.zgkxzx.modbus4And.exception.ModbusInitException;
import com.zgkxzx.modbus4And.exception.ModbusTransportException;
import com.zgkxzx.modbus4And.msg.ReadCoilsRequest;
import com.zgkxzx.modbus4And.msg.ReadCoilsResponse;
import com.zgkxzx.modbus4And.msg.ReadDiscreteInputsRequest;
import com.zgkxzx.modbus4And.msg.ReadDiscreteInputsResponse;
import com.zgkxzx.modbus4And.msg.ReadHoldingRegistersRequest;
import com.zgkxzx.modbus4And.msg.ReadHoldingRegistersResponse;
import com.zgkxzx.modbus4And.msg.WriteCoilRequest;
import com.zgkxzx.modbus4And.msg.WriteCoilResponse;
import com.zgkxzx.modbus4And.msg.WriteRegisterRequest;
import com.zgkxzx.modbus4And.msg.WriteRegisterResponse;
import com.zgkxzx.modbus4And.msg.WriteRegistersRequest;
import com.zgkxzx.modbus4And.msg.WriteRegistersResponse;
import com.zgkxzx.modbus4And.serial.rtu.RtuMaster;

import java.util.HashMap;
import java.util.Iterator;

public class MainActivityRTU extends AppCompatActivity {
    private final static String TAG = MainActivityRTU.class.getSimpleName();
    private static final String ACTION_USB_PERMISSION =
            "com.example.USB_PERMISSION";
    int slaveID = 1;
    //ModbusMaster m;
    RtuMaster m;
    PendingIntent permissionIntent;
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            UsbDevice _device;
            String action = intent.getAction();
            Log.d(TAG, "onReceive: " + action);
            if (ACTION_USB_PERMISSION.equals(action)) {
                Log.d(TAG, "onReceive: permission granted for device ");
                synchronized (this) {
                    _device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (_device == null) {
                            Log.d(TAG, "onReceive: no device from parcelable");
                        } else {
                            UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
                            m = new RtuMaster(new UsbSerialPortRTU(manager, _device));
                            Log.d(TAG, "onReceive: permission granted for device " + _device);
                        }
                    }
                }
            } else {
                //Log.d(TAG, "onReceive: permission denied for device " + _device.getProductName());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Log.d(TAG, "onCreate");
        //
        permissionIntent = PendingIntent.getBroadcast(this, 0,
                new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(usbReceiver, filter, RECEIVER_EXPORTED);
        // Find all available drivers from attached devices.
        // mManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        UsbDevice device = null;
        while (deviceIterator.hasNext()) {
            device = deviceIterator.next();
            usbManager.requestPermission(device, permissionIntent);
            Log.d(TAG, "found usb device " + device.getProductName() + "," + device.getDeviceName());
            break;
        }
        //m = ModbusFactory.createRtuMaster(new UsbSerialPort4RTU(manager));
        //if (device != null) {
        // m = new RtuMaster(new UsbSerialPortRTU(usbManager, device));
        // }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        if (m == null) return;
        try {
            m.init();
        } catch (ModbusInitException e) {
            Log.d(TAG, e.toString());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        if (m == null) return;
        m.destroy();
    }


    public void readCoilClickEvent(View view) {
        Log.d(TAG, "readCoil called");
        if (m == null) return;
        int len = 2;
        try {
            ReadCoilsRequest request = new ReadCoilsRequest(slaveID, 0, len);
            ReadCoilsResponse response = (ReadCoilsResponse) m.send(request);
            if (response.isException())
                Log.d(TAG, response.getExceptionMessage());
            else {
                boolean[] booleanData = response.getBooleanData();
                boolean[] resultByte = new boolean[len];
                System.arraycopy(booleanData, 0, resultByte, 0, len);
                Log.d(TAG, "readCoil success! boolean data len = " + booleanData.length);
            }

        } catch (ModbusTransportException e) {
            Log.d(TAG, e.toString());
        }

    }

    public void readDiscreteInputClickEvent(View view) {
        Log.d(TAG, "readDiscreteInput called");
        if (m == null) return;
        int len = 5;
        try {
            ReadDiscreteInputsRequest request = new ReadDiscreteInputsRequest(slaveID, 0, len);
            ReadDiscreteInputsResponse response = (ReadDiscreteInputsResponse) m.send(request);

            if (response.isException())
                Log.d(TAG, response.getExceptionMessage());
            else {
                boolean[] booleanData = response.getBooleanData();
                boolean[] resultByte = new boolean[len];
                System.arraycopy(booleanData, 0, resultByte, 0, len);
                Log.d(TAG, "readDiscreteInput success! boolean data len = " + booleanData.length);
            }
        } catch (ModbusTransportException e) {
            Log.d(TAG, e.toString());
        }
    }

    public void readHoldingRegistersClickEvent(View view) throws ModbusTransportException {
        int len = 10;
        Log.d(TAG, "readHoldingRegisters called");
        if (m == null) return;
        ReadHoldingRegistersRequest request = new ReadHoldingRegistersRequest(slaveID, 0, len);
        try {
            ReadHoldingRegistersResponse response = (ReadHoldingRegistersResponse) m.send(request);
            if (response.isException())
                Log.d(TAG, response.getExceptionMessage());
            else {
                short[] data = response.getShortData();
                Log.d(TAG, "ReadHoldingRegistersRequest success! data len = " + data.length);
            }
        } catch (ModbusTransportException e) {
            Log.d(TAG, e.toString());
        }
    }

    public void readInputRegistersClickEvent(View view) {
        Log.d(TAG, "readInputRegisters called");
        if (m == null) return;
        int len = 8;
        try {
            ReadDiscreteInputsRequest request = new ReadDiscreteInputsRequest(slaveID, 2, len);
            ReadDiscreteInputsResponse response = (ReadDiscreteInputsResponse) m.send(request);

            if (response.isException())
                Log.d(TAG, response.getExceptionMessage());
            else {
                boolean[] booleanData = response.getBooleanData();
                boolean[] resultByte = new boolean[len];
                System.arraycopy(booleanData, 0, resultByte, 0, len);
                Log.d(TAG, "readInputRegisters success! boolean data len = " + booleanData.length);
            }
        } catch (ModbusTransportException e) {
            Log.d(TAG, e.toString());
        }
    }

    public void writeCoilClickEvent(View view) {
        Log.d(TAG, "writeCoil called");
        if (m == null) return;
        int len = 0;
        int offset = 1;
        boolean value = true;
        try {
            WriteCoilRequest request = new WriteCoilRequest(slaveID, offset, value);
            WriteCoilResponse response = (WriteCoilResponse) m.send(request);

            if (response.isException())
                Log.d(TAG, response.getExceptionMessage());
            else
                Log.d(TAG, "writeCoil - Success");
        } catch (ModbusTransportException e) {
            Log.d(TAG, e.toString());
        }
    }

    public void writeRegisterClickEvent(View view) {
        Log.d(TAG, "writeRegister called");
        if (m == null) return;
        int offset = 1, value = 234;
        try {
            WriteRegisterRequest request = new WriteRegisterRequest(slaveID, offset, value);
            WriteRegisterResponse response = (WriteRegisterResponse) m.send(request);

            if (response.isException())
                Log.d(TAG, response.getExceptionMessage());
            else
                Log.d(TAG, "writeRegister - Success");
        } catch (ModbusTransportException e) {
            Log.d(TAG, e.toString());
        }
    }

    public void writeRegistersClickEvent(View view) {
        Log.d(TAG, "writeRegisters called");
        if (m == null) return;
        final short[] values = new short[]{211, 52, 34};
        int start = 2;
        try {
            WriteRegistersRequest request = new WriteRegistersRequest(slaveID, start, values);
            WriteRegistersResponse response = (WriteRegistersResponse) m.send(request);

            if (response.isException())
                Log.d(TAG, response.getExceptionMessage());
            else
                Log.d(TAG, "writeRegisters - Success");
        } catch (ModbusTransportException e) {
            Log.d(TAG, e.toString());
        }
    }

}