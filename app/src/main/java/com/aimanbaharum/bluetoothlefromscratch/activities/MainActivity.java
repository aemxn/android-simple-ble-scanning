package com.aimanbaharum.bluetoothlefromscratch.activities;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.aimanbaharum.bluetoothlefromscratch.R;
import com.aimanbaharum.bluetoothlefromscratch.adapters.LeDeviceListAdapter;
import com.aimanbaharum.bluetoothlefromscratch.containers.BluetoothLeDeviceStore;
import com.aimanbaharum.bluetoothlefromscratch.util.BluetoothLeScanner;
import com.aimanbaharum.bluetoothlefromscratch.util.BluetoothUtils;
import com.aimanbaharum.bluetoothlefromscratch.util.Constants;

import uk.co.alt236.bluetoothlelib.device.BluetoothLeDevice;
import uk.co.alt236.bluetoothlelib.device.IBeaconDevice;
import uk.co.alt236.bluetoothlelib.util.IBeaconUtils;
import uk.co.alt236.easycursor.objectcursor.EasyObjectCursor;


public class MainActivity extends ListActivity {

    private final static String TAG = MainActivity.class.getSimpleName();

    private BluetoothUtils mBluetoothUtils;
    private BluetoothLeScanner mScanner;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothLeDeviceStore mDeviceStore;

    private TextView mTvBluetoothStatus;
    private TextView mTvBluetoothLeStatus;

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

            final BluetoothLeDevice deviceLe = new BluetoothLeDevice(device, rssi, scanRecord, System.currentTimeMillis());
            mDeviceStore.addDevice(deviceLe);
            final EasyObjectCursor<BluetoothLeDevice> c = mDeviceStore.getDeviceCursor();

            if (IBeaconUtils.isThisAnIBeacon(deviceLe)){
                final IBeaconDevice iBeacon = new IBeaconDevice(deviceLe);

                if (iBeacon.getUUID().equals(Constants.HARDCODE_UUID)){
                    mScanner.scanLeDevice(false);
                    invalidateOptionsMenu();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "uuid= "+iBeacon.getUUID(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    mScanner.scanLeDevice(false);
                    invalidateOptionsMenu();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "No beacon found", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLeDeviceListAdapter.swapCursor(c);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvBluetoothStatus = (TextView) findViewById(R.id.tvBluetoothLe);
        mTvBluetoothLeStatus = (TextView) findViewById(R.id.tvBluetoothStatus);

        mDeviceStore = new BluetoothLeDeviceStore();
        mBluetoothUtils = new BluetoothUtils(this);
        mScanner = new BluetoothLeScanner(mLeScanCallback, mBluetoothUtils);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScanner.scanLeDevice(false);
    }

    private void startScan(){
        final boolean mIsBluetoothOn = mBluetoothUtils.isBluetoothOn();
        final boolean mIsBluetoothLePresent = mBluetoothUtils.isBluetoothLeSupported();
        mDeviceStore.clear();

        mLeDeviceListAdapter = new LeDeviceListAdapter(this, mDeviceStore.getDeviceCursor());
        setListAdapter(mLeDeviceListAdapter);

        mBluetoothUtils.askUserToEnableBluetoothIfNeeded();
        if(mIsBluetoothOn && mIsBluetoothLePresent){
            mScanner.scanLeDevice(true);
            invalidateOptionsMenu();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        final boolean mIsBluetoothOn = mBluetoothUtils.isBluetoothOn();
        final boolean mIsBluetoothLePresent = mBluetoothUtils.isBluetoothLeSupported();

        if(mIsBluetoothOn){
            mTvBluetoothStatus.setText(R.string.on);
        } else {
            mTvBluetoothStatus.setText(R.string.off);
        }

        if(mIsBluetoothLePresent){
            mTvBluetoothLeStatus.setText(R.string.supported);
        } else {
            mTvBluetoothLeStatus.setText(R.string.not_supported);
        }

        invalidateOptionsMenu();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mScanner.isScanning()) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_progress_indeterminate);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                startScan();
                break;
            case R.id.menu_stop:
                mScanner.scanLeDevice(false);
                invalidateOptionsMenu();
                break;
        }
        return true;
    }
}
