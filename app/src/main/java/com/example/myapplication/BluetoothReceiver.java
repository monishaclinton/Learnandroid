package com.example.myapplication;

import android.Manifest;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class BluetoothReceiver extends BroadcastReceiver {

    private static final String TAG = "MY_BLUETOOTH";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent == null) {
            return;
        }

        String action = intent.getAction();

        Log.d(
                TAG,
                "Received: " + action
        );

        // =========================================
        // CHECK BLUETOOTH PERMISSION
        // =========================================

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED) {

                Log.d(
                        TAG,
                        "BLUETOOTH_CONNECT permission not granted"
                );

                return;
            }
        }

        // =========================================
        // A2DP CONNECTION STATE
        // =========================================

        if (BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED
                .equals(action)) {

            int state = intent.getIntExtra(
                    BluetoothA2dp.EXTRA_STATE,
                    BluetoothA2dp.STATE_DISCONNECTED
            );

            BluetoothDevice device = null;

            // Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                device = intent.getParcelableExtra(
                        BluetoothDevice.EXTRA_DEVICE,
                        BluetoothDevice.class
                );

            } else {

                device = intent.getParcelableExtra(
                        BluetoothDevice.EXTRA_DEVICE
                );
            }

            String deviceName = "Bluetooth audio device";

            if (device != null) {

                try {

                    if (device.getName() != null) {
                        deviceName = device.getName();
                    }

                } catch (SecurityException e) {

                    Log.e(
                            TAG,
                            "Cannot get Bluetooth device name"
                    );
                }
            }

            // =====================================
            // CONNECTED
            // =====================================

            if (state == BluetoothA2dp.STATE_CONNECTED) {

                Log.d(
                        TAG,
                        "Bluetooth audio connected: "
                                + deviceName
                );
            }

            // =====================================
            // DISCONNECTED
            // =====================================

            else if (state == BluetoothA2dp.STATE_DISCONNECTED) {

                Log.d(
                        TAG,
                        "Bluetooth audio disconnected: "
                                + deviceName
                );

                // =================================
                // USE YOUR EXISTING METHOD
                // =================================

                MusicManager
                        .getInstance()
                        .pause();

                Log.d(
                        TAG,
                        "MusicManager.pause() called"
                );
            }
        }

        // =========================================
        // ACL DISCONNECTED
        // =========================================

        else if (
                BluetoothDevice.ACTION_ACL_DISCONNECTED
                        .equals(action)
        ) {

            Log.d(
                    TAG,
                    "Bluetooth ACL disconnected"
            );

            // =====================================
            // PAUSE USING EXISTING METHOD
            // =====================================

            MusicManager
                    .getInstance()
                    .pause();

            Log.d(
                    TAG,
                    "MusicManager.pause() called"
            );
        }

        // =========================================
        // ACL CONNECTED
        // =========================================

        else if (
                BluetoothDevice.ACTION_ACL_CONNECTED
                        .equals(action)
        ) {

            Log.d(
                    TAG,
                    "Bluetooth ACL connected"
            );
        }
    }
}