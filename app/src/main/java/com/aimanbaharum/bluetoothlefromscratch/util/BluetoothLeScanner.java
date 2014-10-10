package com.aimanbaharum.bluetoothlefromscratch.util;

import android.bluetooth.BluetoothAdapter;
import android.os.Handler;
import android.util.Log;

/**
 * Created by piracyde25 on 7/3/2014.
 */
public class BluetoothLeScanner {
    private final Handler mHandler;
    private final BluetoothAdapter.LeScanCallback mLeScanCallback;
    private final BluetoothUtils mBluetoothUtils;
    private boolean mScanning;

    public BluetoothLeScanner(BluetoothAdapter.LeScanCallback leScanCallback, BluetoothUtils bluetoothUtils){
        mHandler = new Handler();
        mLeScanCallback = leScanCallback;
        mBluetoothUtils = bluetoothUtils;
    }

    public boolean isScanning() {
        return mScanning;
    }

    public void scanLeDevice(final boolean enable) {
        if (enable) {
            if(mScanning){return;}
            Log.d("TAG", "~ Starting Scan");
            // Stops scanning after a pre-defined scan period.
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("TAG", "~ Stopping Scan (timeout)");
                        mScanning = false;
                        mBluetoothUtils.getBluetoothAdapter().stopLeScan(mLeScanCallback);
                    }
                }, 10000);
            mScanning = true;
            mBluetoothUtils.getBluetoothAdapter().startLeScan(mLeScanCallback);
        } else {
            Log.d("TAG", "~ Stopping Scan");
            mScanning = false;
            mBluetoothUtils.getBluetoothAdapter().stopLeScan(mLeScanCallback);
        }
    }
}
