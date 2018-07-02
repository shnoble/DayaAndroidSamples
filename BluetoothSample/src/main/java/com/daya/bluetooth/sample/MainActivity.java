/*
 * Create by shnoble on 2018. 6. 30.
 * Copyright (c) 2018. shnoble. All rights reserved.
 */

package com.daya.bluetooth.sample;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "BluetoothSample";
    private static final int REQUEST_ENABLE_BT = 100;
    private BluetoothAdapter mBluetoothAdapter;

    private final BroadcastReceiver mBluetoothBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Toast.makeText(MainActivity.this, "Bluetooth off", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Toast.makeText(MainActivity.this, "Turning Bluetooth off...", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Toast.makeText(MainActivity.this, "Bluetooth on", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Toast.makeText(MainActivity.this, "Turning Bluetooth on...", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.d(TAG, "ACTION_DISCOVERY_STARTED");
            }

            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d(TAG, "ACTION_DISCOVERY_FINISHED");
            }

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                displayBluetoothDevice(device);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth.
            error("Device does not support Bluetooth.");
            finish();
            return;
        }
        // Register for broadcasts on BluetoothAdapter state change.
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBluetoothBroadcastReceiver, intentFilter);

        // Needs permission "android.permission.BLUETOOTH"
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mBluetoothBroadcastReceiver);
    }

    void debug(@NonNull String message) {
        Log.d(TAG, message);
        showAlert(message);
    }

    void error(@NonNull String message) {
        Log.e(TAG, message);
        showAlert(message);
    }

    void showAlert(@NonNull String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                debug("블루투스가 활성화 되었습니다.");
            } else if (resultCode == Activity.RESULT_CANCELED) {
                error("블루투스가 설정이 취소되었습니다.");
            }
        }
    }

    public void queryPairedDevice(View view) {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                displayBluetoothDevice(device);
            }
        }
    }

    public void searchDevice(View view) {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

        // Needs permission "android.permission.BLUETOOTH_ADMIN"
        if (!mBluetoothAdapter.startDiscovery()) {
            Log.d(TAG, "Start discovery failed.");
        }
    }

    private void displayBluetoothDevice(@NonNull BluetoothDevice device) {
        // Needs permission "android.permission.BLUETOOTH"
        try {
            BluetoothClass bluetoothClass = device.getBluetoothClass();
            JSONObject bluetoothClassJson = new JSONObject()
                    .putOpt("deviceClass", bluetoothClass.getDeviceClass())
                    .putOpt("majorDeviceClass", bluetoothClass.getMajorDeviceClass());

            ParcelUuid uuids[] = device.getUuids();
            JSONArray uuidsJson = new JSONArray();
            for (ParcelUuid uuid : uuids) {
                uuidsJson.put(uuid.getUuid().toString());
            }

            Log.d(TAG, new JSONObject()
                    .putOpt("name" , device.getName())
                    .putOpt("type" , device.getType())
                    .putOpt("address" , device.getAddress())
                    .putOpt("bluetoothClass" , bluetoothClassJson)
                    .putOpt("bondState" , device.getBondState())
                    .putOpt("Uuids" , uuidsJson)
                    .toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
