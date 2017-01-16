package com.lefu.es.blenew.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.lefu.es.blenew.bean.BleAdvertisedData1;
import com.lefu.es.blenew.bean.BluetoothLeDevice1;
import com.lefu.es.blenew.constant.BluetoolUtil1;
import com.lefu.es.blenew.utils.BleUtil1;

/**
 * 作者: andy on 2016/11/9.
 * 作用: 4.3 版本以上，5.0版本以下的系统
 */
public class BluetoothLeScanner extends BluetoothLeScannerInterface {
    private final Handler mHandler;
    //private final BluetoothAdapter.LeScanCallback mLeScanCallback;
    private final BluetoothUtils1 mBluetoothUtils1;
    private boolean mScanning;
    private final Handler notifyHandler; //界面通知handler

    public BluetoothLeScanner(Handler nHandler, BluetoothUtils1 bluetoothUtils1){
        mHandler = new Handler();
        //mLeScanCallback = leScanCallback;
        mBluetoothUtils1 = bluetoothUtils1;
        notifyHandler = nHandler;
    }

    public boolean isScanning() {
        return mScanning;
    }

    @Override
    public void stopScane(){
        if(null!=mBluetoothUtils1) mBluetoothUtils1.getBluetoothAdapter().stopLeScan(mLeScanCallback);
    }

    @Override
    public void scanLeDevice(final int duration, final boolean enable) {
        if (enable) {
            if(mScanning){return;}
            Log.e("TAG", "~ Starting Scan");
            // Stops scanning after a pre-defined scan period.
            if(duration > 0){
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("TAG", "~ Stopping Scan (timeout)");
                        mScanning = false;
                        mBluetoothUtils1.getBluetoothAdapter().stopLeScan(mLeScanCallback);
                    }
                }, duration);
            }
            Log.e("TAG", "BEGIN scane devices scanLeDevice:=");
            mScanning = true;
            mBluetoothUtils1.getBluetoothAdapter().startLeScan(mLeScanCallback);
        } else {
            Log.e("TAG", "~ Stopping Scan");
            mScanning = false;
            mBluetoothUtils1.getBluetoothAdapter().stopLeScan(mLeScanCallback);
        }
    }


    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            //Log.e("TAG LeScanCallback", "device name:"+device.getName() + "  rssi:"+rssi);
            final BleAdvertisedData1 badata = BleUtil1.parseAdertisedData(scanRecord);
            String deviceName = device.getName();
            if (deviceName == null) {
                deviceName = badata.getName();
            }
            Log.e("BluetoothLeScanner","发现BLE称=" + deviceName + "[" + device.getAddress() + "]");
            if (device != null && deviceName != null && deviceName.toLowerCase().indexOf("scale") >= 0) {
                Log.i("BluetoothLeScanner","发现BLE称=" + deviceName + "[" + device.getAddress() + "]");
                /* 停止蓝牙扫描 */
                if (mScanning) {
                    mBluetoothUtils1.getBluetoothAdapter().stopLeScan(mLeScanCallback);
                    mScanning = false;
                }
                final BluetoothLeDevice1 deviceLe = new BluetoothLeDevice1(device, rssi, scanRecord, System.currentTimeMillis());
                //通知发现蓝牙了
                if(null!=notifyHandler){
                    Message msg1 = mHandler.obtainMessage(BluetoolUtil1.FOUND_DEVICE);
                    msg1.obj = deviceLe;
                    notifyHandler.sendMessage(msg1);
                }
            }


        }



    };
}
