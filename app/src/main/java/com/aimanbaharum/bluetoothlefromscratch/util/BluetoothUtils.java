package com.aimanbaharum.bluetoothlefromscratch.util;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

/**
 * Created by piracyde25 on 7/3/2014.
 */
public final class BluetoothUtils {
    private final Activity mActivity;
    private final BluetoothAdapter mBluetoothAdapter;
    private final BluetoothManager mBluetoothManager;

    public final static int REQUEST_ENABLE_BT = 2001;

    public BluetoothUtils(Activity activity){
        mActivity = activity;
        mBluetoothManager = (BluetoothManager) mActivity.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
    }

    public void askUserToEnableBluetoothIfNeeded(){
        if (isBluetoothLeSupported() && (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled())) {
            final Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    public BluetoothAdapter getBluetoothAdapter(){
        return mBluetoothAdapter;
    }

    public boolean isBluetoothLeSupported(){
        return mActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    public boolean isBluetoothOn(){
        if (mBluetoothAdapter == null) {
            return false;
        } else {
            return mBluetoothAdapter.isEnabled();
        }
    }
}

