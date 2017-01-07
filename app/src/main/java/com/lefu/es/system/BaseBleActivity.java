package com.lefu.es.system;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.lefu.es.blenew.bean.BluetoothLeDevice1;
import com.lefu.es.blenew.constant.BluetoolUtil1;
import com.lefu.es.blenew.service.BluetoothLeScannerInterface;
import com.lefu.es.blenew.service.BluetoothLeService1;
import com.lefu.es.blenew.service.BluetoothUtils1;
import com.lefu.es.constant.BLEConstant;
import com.lefu.es.constant.BluetoolUtil;
import com.lefu.iwellness.newes.cn.system.R;

/**
 * Created by Administrator on 2017/1/6.
 * 蓝牙基础界面
 */

public abstract class BaseBleActivity extends Activity {
    private final static String TAG = BaseBleActivity.class.getSimpleName();

    private BluetoothUtils1 mBluetoothUtils;
    private BluetoothLeScannerInterface mScanner;

    private BluetoothLeService1 mBluetoothLeService;
    private String mDeviceAddress;
    private String mDeviceName;
    private boolean mConnected = false;
    private boolean mActivty = false; //页面是否激活
    private Handler scanHandler;

    protected static final int REQUEST_ACCESS_COARSE_LOCATION_PERMISSION = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBluetoothUtils = new BluetoothUtils1(this);
        if(mBluetoothUtils.isBluetoothLeSupported()){
            scanHandler = new Handler();
            mScanner = mBluetoothUtils.initBleScaner(nofityHandler);
            //注册通知
            registerReceiver(mGattUpdateReceiver, BluetoothUtils1.makeGattUpdateIntentFilter());
            //绑定蓝牙服务服务
            final Intent gattServiceIntent = new Intent(BaseBleActivity.this, BluetoothLeService1.class);
            bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED ) {
                requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION,
                        getString(R.string.permission_blurtooth),
                        REQUEST_ACCESS_COARSE_LOCATION_PERMISSION);
            } else {
                //启动扫描
                scanHandler.post(scanThread);
            }
        }else{
            Toast.makeText(this,"该设备不支持BLE",Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void  startScaneBLE(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ) {
            requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION,
                    getString(R.string.permission_blurtooth),
                    REQUEST_ACCESS_COARSE_LOCATION_PERMISSION);
        } else {
            //启动扫描
            scanHandler.post(scanThread);
        }
    }

    /**
     * 请求权限
     *
     * 如果权限被拒绝过，则提示用户需要权限
     */
    protected void requestPermission(final String permission, String rationale, final int requestCode) {
        if (shouldShowRequestPermissionRationale(permission)) {
            showAlertDialog(getString(R.string.permission_title_rationale), rationale,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(new String[]{permission}, requestCode);
                        }
                    }, getString(R.string.ok_btn), null, getString(R.string.cancle_btn));
        } else {
            requestPermissions(new String[]{permission}, requestCode);
        }
    }
    private AlertDialog mAlertDialog;
    /**
     * 显示指定标题和信息的对话框
     *
     * @param title                         - 标题
     * @param message                       - 信息
     * @param onPositiveButtonClickListener - 肯定按钮监听
     * @param positiveText                  - 肯定按钮信息
     * @param onNegativeButtonClickListener - 否定按钮监听
     * @param negativeText                  - 否定按钮信息
     */
    protected void showAlertDialog(@Nullable String title, @Nullable String message,
                                   @Nullable DialogInterface.OnClickListener onPositiveButtonClickListener,
                                   @NonNull String positiveText,
                                   @Nullable DialogInterface.OnClickListener onNegativeButtonClickListener,
                                   @NonNull String negativeText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveText, onPositiveButtonClickListener);
        builder.setNegativeButton(negativeText, onNegativeButtonClickListener);
        mAlertDialog = builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case REQUEST_ACCESS_COARSE_LOCATION_PERMISSION:
                if (null!=grantResults && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(null!=mAlertDialog)mAlertDialog.dismiss();
                    //启动扫描
                    scanHandler.post(scanThread);
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * 扫描线程
     */
    private Runnable scanThread = new Runnable() {

        public void run() {
            // 你的线程所干的事情
            startScan();
            //十秒后再重复工作
            scanHandler.postDelayed(scanThread,10000);
        }
    };
    /**
     * 开始扫描蓝牙
     */
    private void startScan(){
        final boolean mIsBluetoothOn = mBluetoothUtils.isBluetoothOn();
        final boolean mIsBluetoothLePresent = mBluetoothUtils.isBluetoothLeSupported();
        mBluetoothUtils.askUserToEnableBluetoothIfNeeded();
        if(mIsBluetoothOn && mIsBluetoothLePresent && mActivty){//页面激活的状态下才真正扫描
            mScanner.scanLeDevice(8000, true);
            //invalidateOptionsMenu();
        }
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService1.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            Log.e(TAG, "开始连接蓝牙.......");
            // Automatically connects to the device upon successful start-up initialization.
            if(!TextUtils.isEmpty(mDeviceAddress))mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BLEConstant.ACTION_GATT_CONNECTED.equals(action)) { //蓝牙连接了
                mConnected = true;
                Log.e(TAG, "蓝牙已连接");
                updateConnectionState(R.string.connected);
                //invalidateOptionsMenu();
            } else if (BLEConstant.ACTION_GATT_DISCONNECTED.equals(action)) {//蓝牙断开连接
                mConnected = false;
                Log.e(TAG, "蓝牙断开");
               updateConnectionState(R.string.disconnected);

            } else if (BLEConstant.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {//发现蓝牙服务
                // Show all the supported services and characteristics on the user interface.
                Log.e(TAG, "发现服务");
                if (null != mBluetoothLeService) {
                    if (null != mDeviceName) {
                        if(mDeviceName.toLowerCase().startsWith("heal")){
                            Message msg1 = nofityHandler.obtainMessage(BluetoolUtil1.DESCIVE_SERVICE);
                            nofityHandler.sendMessage(msg1);
                            // 监听 阿里秤 读通道
                           // BleHelper.getInstance().listenAliScale(mBluetoothLeService);
                            // 获取用户组
                           // String sendData = BleHelper.getInstance().assemblyAliData(Units.UNIT_KG.getCode(),"01");
                            // 发送数据
                           // BleHelper.getInstance().sendDateToScale(mBluetoothLeService,sendData);
                        }
                    }
                }

            } else if (BLEConstant.ACTION_DATA_AVAILABLE.equals(action)) { //接收到数据
                String readMessage = intent.getStringExtra(BLEConstant.EXTRA_DATA);
                Log.e(TAG, "接收数据"+readMessage);
                if(!TextUtils.isEmpty(readMessage) && readMessage.length()>10){
                    Message msg1 = nofityHandler.obtainMessage(BluetoolUtil1.RECEIVE_DATA);
                    msg1.obj = readMessage;
                    nofityHandler.sendMessage(msg1);
                }
            }
        }
    };

    /**
     * 发现蓝牙通知界面相应
     */
    Handler nofityHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoolUtil1.FOUND_DEVICE:
                    Log.i(TAG, "[蓝牙连接状态]=="+ (null==mBluetoothLeService?"未初始化":mBluetoothLeService.getmConnectionState()));
                    BluetoothLeDevice1 deviceLe = (BluetoothLeDevice1)msg.obj;
                    if(null!=deviceLe && null!=mBluetoothLeService && mBluetoothLeService.getmConnectionState()==BluetoothLeService1.STATE_DISCONNECTED){
                        mDeviceAddress = deviceLe.getAddress();
                        mDeviceName = deviceLe.getName();

                        if(!TextUtils.isEmpty(mDeviceAddress))mBluetoothLeService.connect(mDeviceAddress);
                    }
                    break;

                case BluetoolUtil1.RECEIVE_DATA: //接收到数据
                    String data  = (String)msg.obj;
                    //reciveDataAdapter.addToFirst(data);
                    break;
                case BluetoolUtil1.DESCIVE_SERVICE: //发现服务
                    // String data  = (String)msg.obj;
                    // reciveDataAdapter.
                    break;
            }
            super.handleMessage(msg);

        }
    };


    /**
     * 更新界面蓝牙标志
     * @param resourceId
     */
    public abstract void updateConnectionState(final int resourceId);


    /**
     * 发现蓝牙服务
     */
    public abstract void discoverBleService();

    /**
     * 接收到蓝牙数据
     * @param data
     */
    public abstract void reveiveBleData(String data);

    @Override
    protected void onPause() {
        super.onPause();
        mActivty = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mActivty = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scanHandler.removeCallbacks(scanThread);
        unregisterReceiver(mGattUpdateReceiver);
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }
}
