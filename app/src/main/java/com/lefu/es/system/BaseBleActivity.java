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
import android.media.AudioManager;
import android.media.SoundPool;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lefu.es.ble.BlueSingleton;
import com.lefu.es.blenew.bean.BluetoothLeDevice1;
import com.lefu.es.blenew.constant.BluetoolUtil1;
import com.lefu.es.blenew.service.BluetoothLeScannerInterface;
import com.lefu.es.blenew.service.BluetoothLeService1;
import com.lefu.es.blenew.service.BluetoothUtils1;
import com.lefu.es.cache.CacheHelper;
import com.lefu.es.constant.AppData;
import com.lefu.es.constant.BLEConstant;
import com.lefu.es.constant.BluetoolUtil;
import com.lefu.es.constant.UtilConstants;
import com.lefu.es.db.RecordDao;
import com.lefu.es.entity.NutrientBo;
import com.lefu.es.entity.Records;
import com.lefu.es.entity.UserModel;
import com.lefu.es.service.RecordService;
import com.lefu.es.service.TimeService;
import com.lefu.iwellness.newes.cn.system.R;

import java.util.ArrayList;
import java.util.List;

import static com.lefu.iwellness.newes.cn.system.R.id.cancle_datacbtn;
import static com.lefu.iwellness.newes.cn.system.R.id.home_img_btn;
import static com.lefu.iwellness.newes.cn.system.R.id.save_databtn;

/**
 * Created by Administrator on 2017/1/6.
 * 蓝牙基础界面
 */

public abstract class BaseBleActivity extends Activity {
    public final static String TAG = BaseBleActivity.class.getSimpleName();

    public BluetoothUtils1 mBluetoothUtils;
    public BluetoothLeScannerInterface mScanner;

    public BluetoothLeService1 mBluetoothLeService;
    public String mDeviceAddress;
    public String mDeviceName;
    public boolean mConnected = false;
    public boolean mActivty = false; //页面是否激活
    public Handler scanHandler;

    protected static final int REQUEST_ACCESS_COARSE_LOCATION_PERMISSION = 101;

    public RecordService recordService;

    protected SoundPool soundpool;

    protected Records receiveRecod = null;

    protected Records  secondRecod = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recordService = new RecordService(this);
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

    protected final ServiceConnection mServiceConnection = new ServiceConnection() {
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
                Message msg1 = nofityHandler.obtainMessage(BluetoolUtil1.DESCIVE_CONNECTED);
                nofityHandler.sendMessage(msg1);


                //invalidateOptionsMenu();
            } else if (BLEConstant.ACTION_GATT_DISCONNECTED.equals(action)) {//蓝牙断开连接
                mConnected = false;
                Log.e(TAG, "蓝牙断开");
                Message msg1 = nofityHandler.obtainMessage(BluetoolUtil1.DESCIVE_DISCONNECT);
                nofityHandler.sendMessage(msg1);


            } else if (BLEConstant.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) { //发现蓝牙服务
                // Show all the supported services and characteristics on the user interface.
                Log.e(TAG, "发现服务");
                if (null != mBluetoothLeService) {
                    Message msg1 = nofityHandler.obtainMessage(BluetoolUtil1.DESCIVE_SERVICE);
                    nofityHandler.sendMessage(msg1);
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
                    reveiveBleData(data);
                    break;
                case BluetoolUtil1.DESCIVE_SERVICE: //发现服务
                    discoverBleService();
                    break;
                case BluetoolUtil1.DESCIVE_DISCONNECT: //蓝牙断开
                    updateConnectionState(R.string.disconnected);
                    break;
                case BluetoolUtil1.DESCIVE_CONNECTED: //蓝牙连接
                    updateConnectionState(R.string.connected);
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

    /**自定义弹窗*/
    public void openErrorDiolg(String code) {
        try {
            Intent openDialog = new Intent(this, CustomDialogActivity.class);
            Bundle mBundle = new Bundle();
            mBundle.putString("error", code);
            openDialog.putExtras(mBundle);
            openDialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(openDialog);
        } catch (Exception e) {
        }
    }

    /**播放声音*/
    public void playSound() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                soundpool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
                int sourceid = -1;
                sourceid = soundpool.load(BaseBleActivity.this, R.raw.ring, 0);
                AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                int streamVolume = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                }
                soundpool.play(sourceid, streamVolume, streamVolume, 1, 0, 1F);
            }
        }).start();
    }

    protected AlertDialog receiveDataDialog;
    private Button cancleBtn,saveBtn;
    /**
     * 接收到数据提示
     */
    protected void showReceiveDataDialog() {
        // 初始化自定义布局参数
        LayoutInflater layoutInflater = getLayoutInflater();
        // 为了能在下面的OnClickListener中获取布局上组件的数据，必须定义为final类型.
        View customLayout = layoutInflater.inflate(R.layout.activity_receive_alert, (ViewGroup) findViewById(R.id.receiveDataDialog));

        cancleBtn = (Button) customLayout.findViewById(R.id.cancle_datacbtn);
        saveBtn = (Button) customLayout.findViewById(save_databtn);

        cancleBtn.setOnClickListener(imgOnClickListener);
        saveBtn.setOnClickListener(imgOnClickListener);

        receiveDataDialog = new AlertDialog.Builder(this).setView(customLayout).show();

        Window window = receiveDataDialog.getWindow();
        window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.mystyle); // 添加动画
    }

    View.OnClickListener imgOnClickListener = new View.OnClickListener() {
        @SuppressWarnings("deprecation")
        public void onClick(View v) {
            switch (v.getId()) {
                case cancle_datacbtn:
                    receiveDataDialog.dismiss();
                    receiveDataDialog = null;
                    break;
                case save_databtn:
                    try {
                        AppData.hasCheckData=true;
                        if (!BluetoolUtil.bleflag)
                            TimeService.setIsdoing(true);
                        else
                            BlueSingleton.setIsdoing(true);
                        if (null != receiveRecod && null != receiveRecod.getScaleType()) {
                            if (UtilConstants.KITCHEN_SCALE.equals(UtilConstants.CURRENT_SCALE)) {
                                NutrientBo nutrient = null;
                                if(!TextUtils.isEmpty(receiveRecod.getRphoto())){
                                    nutrient = CacheHelper.queryNutrientsByName(receiveRecod.getRphoto());
                                }
                                RecordDao.dueKitchenDate2(recordService, receiveRecod,nutrient);
                            } else {
                                RecordDao.handleData2(recordService, receiveRecod);
                            }

                            if (!BluetoolUtil.bleflag){
                                TimeService.setIsdoing(false);
                            }else{
                                BlueSingleton.setIsdoing(false);
                            }
                            receiveDataDialog.dismiss();
                            receiveDataDialog = null;
                            receiveRecod = null;
                        }

                    } catch (Exception e) {
                        Log.e(TAG, "保存用户测量数据异常"+e.getMessage());
                    }
                    break;
            }
        }
    };


    /**
     * 将数据ArrayList中
     *
     * @return
     */
    protected ArrayList<Baby> getData() {
        ArrayList<Baby> items = new ArrayList<Baby>();
        for (int i = 0; i < 7; i++) {
            Baby baby = new Baby();
            baby.setName("baby"+i);
            items.add(baby);
        }
        return items;
    }

    class Baby{
        public String headUrl;
        public String name;
        public void setHeadUrl(String headUrl){
            this.headUrl = headUrl;
        }
        public String getHeadUrl(){
            return headUrl;
        }
        public void setName(String name){
            this.name = name;
        }
        public String getName(){
            return name;
        }
    }

    class BabyGirdViewAdpter extends BaseAdapter {
        private LayoutInflater inflater;
        private List<Baby> list;

        public BabyGirdViewAdpter(LayoutInflater inflater, ArrayList<Baby> list) {
            this.inflater = inflater;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Baby baby =list.get(position);
            View view;
            ViewHolder viewHolder;
            if(convertView==null){
                view=inflater.inflate(R.layout.selet_baby, null);
                viewHolder=new ViewHolder();
                viewHolder.image=(ImageView) view.findViewById(R.id.imageview);
                viewHolder.name=(TextView) view.findViewById(R.id.textview);
                view.setTag(viewHolder);
            }else{
                view=convertView;
                viewHolder=(ViewHolder) view.getTag();
            }
//            viewHolder.image.setImageResource();
            viewHolder.image.setImageDrawable(getDrawable(R.drawable.baby));
            viewHolder.name.setText(baby.getName());
            return view;
        }

    }
    class ViewHolder{
        ImageView image;
        TextView name;
    }


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
