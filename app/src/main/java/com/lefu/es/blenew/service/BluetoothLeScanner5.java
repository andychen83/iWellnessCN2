package com.lefu.es.blenew.service;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.lefu.es.blenew.bean.BluetoothLeDevice1;
import com.lefu.es.blenew.constant.BluetoolUtil1;

/**
 * 作者: andy on 2016/11/9.
 * 作用: 蓝牙扫描类，系统5.0及其以上系统
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BluetoothLeScanner5 extends BluetoothLeScannerInterface {
    private final Handler mHandler;
    private final BluetoothUtils1 mBluetoothUtils1;
    private final Handler notifyHandler; //界面通知handler
    private boolean mScanning;
    private BluetoothLeScanner scanner;

    public BluetoothLeScanner5(Handler nHandler, BluetoothUtils1 bluetoothUtils1){
        mHandler = new Handler();
        mBluetoothUtils1 = bluetoothUtils1;
        notifyHandler = nHandler;
    }

    public boolean isScanning() {
        return mScanning;
    }

    @Override
    public void stopScane(){
        if(null!=mBluetoothUtils1 && null!=mBluetoothUtils1.getBluetoothAdapter() && null!=mBluetoothUtils1.getBluetoothAdapter().getBluetoothLeScanner())mBluetoothUtils1.getBluetoothAdapter().getBluetoothLeScanner().stopScan(mLeScanCallback);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
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
                        if(null!=mBluetoothUtils1 && null!=mBluetoothUtils1.getBluetoothAdapter() && null!=mBluetoothUtils1.getBluetoothAdapter().getBluetoothLeScanner())mBluetoothUtils1.getBluetoothAdapter().getBluetoothLeScanner().stopScan(mLeScanCallback);
                    }
                }, duration);
            }
            Log.e("TAG", "BEGIN scane devices scanLeDevice:=");
            mScanning = true;
            if(null!=mBluetoothUtils1 && null!=mBluetoothUtils1.getBluetoothAdapter() && null!=mBluetoothUtils1.getBluetoothAdapter().getBluetoothLeScanner()) mBluetoothUtils1.getBluetoothAdapter().getBluetoothLeScanner().startScan(mLeScanCallback);
        } else {
            Log.e("TAG", "~ Stopping Scan");
            mScanning = false;
            if(null!=mBluetoothUtils1 && null!=mBluetoothUtils1.getBluetoothAdapter() && null!=mBluetoothUtils1.getBluetoothAdapter().getBluetoothLeScanner())mBluetoothUtils1.getBluetoothAdapter().getBluetoothLeScanner().stopScan(mLeScanCallback);
        }
    }

    private ScanCallback  mLeScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            if(null!=device){
                String  deviceName = device.getName();
                if(TextUtils.isEmpty(deviceName) && null!=result.getScanRecord()){
                    deviceName = result.getScanRecord().getDeviceName();
                }
                Log.e("BluetoothLeScanner5","发现BLE称=" + deviceName + "[" + device.getAddress() + "]");
                if (device != null && deviceName != null && deviceName.toLowerCase().indexOf("scale") >= 0) {
                    Log.i("BluetoothLeScanner5","发现BLE称=" + deviceName + "[" + device.getAddress() + "]");
                    /* 停止蓝牙扫描 */
                    if (mScanning) {
                        if(null!=mBluetoothUtils1 && null!=mBluetoothUtils1.getBluetoothAdapter() && null!=mBluetoothUtils1.getBluetoothAdapter().getBluetoothLeScanner())mBluetoothUtils1.getBluetoothAdapter().getBluetoothLeScanner().stopScan(mLeScanCallback);
                        mScanning = false;
                    }
                    final BluetoothLeDevice1 deviceLe = new BluetoothLeDevice1(result.getDevice(), result.getRssi(), null==result.getScanRecord()?null:result.getScanRecord().getBytes(), System.currentTimeMillis());
                    //通知发现蓝牙了
                    if(null!=notifyHandler){
                        Message msg1 = mHandler.obtainMessage(BluetoolUtil1.FOUND_DEVICE);
                        msg1.obj = deviceLe;
                        notifyHandler.sendMessage(msg1);
                    }
                }

            }

        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            //Log.e("搜索失败");
        }
    };
}
