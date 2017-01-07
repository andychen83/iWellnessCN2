package com.hetai.ble.ble_hetai_lib.service;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;

import com.hetai.ble.ble_hetai_lib.constant.BLEConstant1;

/**
 * 作者: andy on 2016/11/9.
 * 作用: 蓝牙设备基础检测类
 */
public class BluetoothUtils1 {
    private final Activity mActivity;
    private final BluetoothAdapter mBluetoothAdapter;
    private final BluetoothManager mBluetoothManager;
    private BluetoothLeScannerInterface mScanner;
    public final static int REQUEST_ENABLE_BT = 2001;

    public BluetoothUtils1(Activity activity){
        mActivity = activity;
        mBluetoothManager = (BluetoothManager) mActivity.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
    }

    /**
     * 获取扫描对象
     * @return
     */
    public BluetoothLeScannerInterface initBleScaner(Handler nofityHandler){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mScanner = new BluetoothLeScanner5(nofityHandler,this);
        }else{
            mScanner = new BluetoothLeScanner(nofityHandler,this);
        }
        return mScanner;
    }

    /**
     * 检测蓝牙设备是否可用
     */
    public void askUserToEnableBluetoothIfNeeded(){
        if (isBluetoothLeSupported() && (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled())) {
            final Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    public BluetoothAdapter getBluetoothAdapter(){
        return mBluetoothAdapter;
    }

    /**
     * 检测当前设备是否支持BLE
     * @return
     */
    public boolean isBluetoothLeSupported(){
        return mActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    /**
     * 判断蓝牙是否开启
     * @return
     */
    public boolean isBluetoothOn(){
        if (mBluetoothAdapter == null) {
            return false;
        } else {
            return mBluetoothAdapter.isEnabled();
        }
    }

    /**注册GATT状态变化广播*/
    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BLEConstant1.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BLEConstant1.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BLEConstant1.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BLEConstant1.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}
