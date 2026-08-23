package com.zgkxzx.modbustest;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
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
            "com.zgkxzx.USB_PERMISSION";
    int slaveID = 1;
    //ModbusMaster m;
    RtuMaster m;
    boolean mUsbPermission;
    boolean mPermissionGranted = false;
    UsbDevice mUsbDevice = null;
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            Log.d(TAG, "onReceive: " + action);
            UsbDevice _device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (ACTION_USB_PERMISSION.equals(action)) {
                mUsbPermission = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
                //Log.d(TAG,"usbPermission " + mUsbPermission);
                if (mUsbPermission && _device != null) {
                    mUsbDevice = _device;
                    Log.d(TAG, "onReceive: permission granted for device " + _device);
                    UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
                    try {
                        m = new RtuMaster(new UsbSerialPortRTU(usbManager, mUsbDevice));
                        m.init();
                        m.setTimeout(1500);
                        Log.d(TAG, "created RTU master ");
                    } catch (ModbusInitException e) {
                        m = null;
                        Log.d(TAG, e.toString());
                    }
                } else {
                    Log.d(TAG, "onReceive: permission NOT granted for device " + _device);
                }
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
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(usbReceiver, filter, RECEIVER_NOT_EXPORTED);
        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_MUTABLE : 0;
            Intent intent = new Intent(ACTION_USB_PERMISSION);
            intent.setPackage(getPackageName());
            PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 1234, intent, flags);
            UsbDevice device = deviceIterator.next();
            usbManager.requestPermission(device, permissionIntent);
            Log.d(TAG, "found usb device " + device.getProductName() + "," + device.getDeviceName());
            //break;
        }

        if (mUsbDevice != null && mPermissionGranted) {
            //m = ModbusFactory.createRtuMaster(new UsbSerialPort4RTU(manager));
//            m = new RtuMaster(new UsbSerialPortRTU(usbManager, mUsbDevice));
//            Log.d(TAG, "create RTU master ");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        if (m == null) return;
        m.destroy();
    }


    public void readCoilClickEvent(View view) {
        view.setEnabled(false);
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
        view.setEnabled(true);
    }

    public void readDiscreteInputClickEvent(View view) {
        view.setEnabled(false);
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
        view.setEnabled(true);
    }

    public void readHoldingRegistersClickEvent(View view) throws ModbusTransportException {
        view.setEnabled(false);
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
        view.setEnabled(true);
    }

    public void readInputRegistersClickEvent(View view) {
        view.setEnabled(false);
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
        view.setEnabled(true);
    }

    public void writeCoilClickEvent(View view) {
        view.setEnabled(false);
        Log.d(TAG, "writeCoil called");
        if (m == null) return;
        int offset = 0;
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
        view.setEnabled(true);
    }

    public void writeRegisterClickEvent(View view) {
        view.setEnabled(false);
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
        view.setEnabled(true);
    }

    public void writeRegistersClickEvent(View view) {
        view.setEnabled(false);
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
        view.setEnabled(true);
    }

}