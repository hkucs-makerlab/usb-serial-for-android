package com.zgkxzx.modbustest;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

public class MainActivityRTU extends AppCompatActivity {
    private final static String TAG = MainActivityRTU.class.getSimpleName();
    int slaveID = 1;
    //ModbusMaster m;
    RtuMaster m;

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

        //m = ModbusFactory.createRtuMaster(new BluetoothSerialPort());
        m = new RtuMaster(new UsbSerialPort4RTU());
        Log.d(TAG, "onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        try {
            m.init();
        } catch (ModbusInitException e) {
            Log.d(TAG, e.toString());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        m.destroy();
        Log.d(TAG, "onStop");
    }

    public void readCoilClickEvent(View view) {
        Log.d(TAG, "readCoil called");
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