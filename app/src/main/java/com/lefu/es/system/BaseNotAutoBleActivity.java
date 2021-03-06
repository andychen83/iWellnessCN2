package com.lefu.es.system;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lefu.es.blenew.bean.BluetoothLeDevice1;
import com.lefu.es.blenew.constant.BluetoolUtil1;
import com.lefu.es.blenew.service.BluetoothLeScannerInterface;
import com.lefu.es.blenew.service.BluetoothLeService1;
import com.lefu.es.blenew.service.BluetoothUtils1;
import com.lefu.es.constant.AppData;
import com.lefu.es.constant.BLEConstant;
import com.lefu.es.constant.BluetoolUtil;
import com.lefu.es.constant.UtilConstants;
import com.lefu.es.entity.Records;
import com.lefu.es.service.RecordService;
import com.lefu.es.service.TimeService;
import com.lefu.es.util.SharedPreferencesUtil;
import com.lefu.iwellness.newes.cn.system.R;

import java.util.ArrayList;
import java.util.List;

import static com.lefu.iwellness.newes.cn.system.R.id.save_databtn;

/**
 * Created by Administrator on 2017/1/6.
 * 蓝牙基础界面
 */

public abstract class BaseNotAutoBleActivity extends AppCompatActivity {
    public final static String TAG = BaseNotAutoBleActivity.class.getSimpleName();

    public BluetoothUtils1 mBluetoothUtils;
    public BluetoothLeScannerInterface mScanner;

    public BluetoothLeService1 mBluetoothLeService;
    public String mDeviceAddress;
    public String mDeviceName;
    public boolean mConnected = false;
    public boolean mActivty = true; //页面是否激活
    public Handler scanHandler;

    protected static final int REQUEST_ACCESS_COARSE_LOCATION_PERMISSION = 101;

    public RecordService recordService;

    protected SoundPool soundpool;

    protected Records receiveRecod = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recordService = new RecordService(this);
        mBluetoothUtils = new BluetoothUtils1(this);
        if(mBluetoothUtils.isBluetoothLeSupported()){
            BluetoolUtil.bleflag = true;
            scanHandler = new Handler();
            mScanner = mBluetoothUtils.initBleScaner(nofityHandler);
            //注册通知
            registerReceiver(mGattUpdateReceiver, BluetoothUtils1.makeGattUpdateIntentFilter());
            //绑定蓝牙服务服务
            final Intent gattServiceIntent = new Intent(BaseNotAutoBleActivity.this, BluetoothLeService1.class);
            bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
//                    && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//                    != PackageManager.PERMISSION_GRANTED ) {
//                requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION,
//                        getString(R.string.permission_blurtooth),
//                        REQUEST_ACCESS_COARSE_LOCATION_PERMISSION);
//            } else {
//                //启动扫描
//                scanHandler.post(scanThread);
//            }
        }else{
            BluetoolUtil.bleflag = false;
        }
    }

    /**
     * 藍牙連接
     */
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
                if (null != mBluetoothLeService && mActivty) {
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
                sourceid = soundpool.load(BaseNotAutoBleActivity.this, R.raw.ring, 0);
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

    /**
     * 保存后回调
     * @param records
     */
    protected abstract  void saveDataCallBack(Records records);


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

    protected void showAlertDailog(String title) {
        new com.lefu.es.view.AlertDialog(BaseNotAutoBleActivity.this).builder().setTitle(getResources().getString(R.string.waring_title)).setMsg(title).setPositiveButton(getResources().getString(R.string.ok_btn), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == UtilConstants.su) {
                    UtilConstants.su = new SharedPreferencesUtil(BaseNotAutoBleActivity.this);
                }
                UtilConstants.su.editSharedPreferences("lefuconfig", "first_install_dailog", "1");
                UtilConstants.FIRST_INSTALL_DAILOG = "1";
            }
        }).show();
    }


    protected  boolean ageError = false;
    protected void showAgeOrHeightAlertDailog(String title) {
        new com.lefu.es.view.AlertDialog(BaseNotAutoBleActivity.this).builder().setTitle(getResources().getString(R.string.ageorheight_error_title)).setMsg(title).setPositiveButton(getResources().getString(R.string.ok_btn), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ageError = false;
            }
        }).show();
        ageError = true;
    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }
        @Override
        public void onFinish() {//计时完毕时触发
            if(null!=time){
                time.cancel();
                time = null;
            }
            if(null!=downCountDialog){
                downCountDialog.dismiss();
                downCountDialog = null;
            }
        }
        @Override
        public void onTick(final long millisUntilFinished){//计时过程显示
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(null!=baby_time_title)baby_time_title.setText(millisUntilFinished /1000+"");
                }
            });
        }
    }

    protected Dialog downCountDialog;
    protected  TextView baby_up_title;
    protected  TextView baby_time_title;

    protected TimeCount time = null;
    /**
     * 接收到数据提示
     */
    protected void showDownCountDataDialog(String msg) {
        if(null!=downCountDialog){
            downCountDialog.dismiss();
            downCountDialog = null;
        }
        if(null!=time){
            time.cancel();
            time = null;
        }
        // 初始化自定义布局参数
        LayoutInflater layoutInflater = getLayoutInflater();
        // 为了能在下面的OnClickListener中获取布局上组件的数据，必须定义为final类型.
        View customLayout = layoutInflater.inflate(R.layout.activity_downcount_alert, (ViewGroup) findViewById(R.id.receiveDataDialog));
        baby_up_title = (TextView) customLayout.findViewById(R.id.baby_up_title);
        baby_time_title = (TextView) customLayout.findViewById(R.id.baby_time_title);

        baby_up_title.setText(msg);

        downCountDialog = new Dialog(this,R.style.dialog);
        downCountDialog.setContentView(customLayout);
        downCountDialog .show();

        Window window = downCountDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();

        lp.y = 150;
        window.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.mystyle); // 添加动画
        window.setAttributes(lp);

        time = new TimeCount(60000, 1000);//构造CountDownTimer对象
        time.start();//开始计时

    }

    @Override
    protected void onStart() {
        if (!BluetoolUtil.bleflag && null == UtilConstants.serveIntent) {
            UtilConstants.serveIntent = new Intent(this, TimeService.class);
            this.startService(UtilConstants.serveIntent);
			/* 开机BT循环扫描线程 */
            new Thread(ScanRunnable).start();
			/* 连接状态 */
            // TimeService.scale_connect_state = scale_connect_state;
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mActivty = false;
        stopScanService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        /* 秤识别中 */
        AppData.isCheckScale = false;
        //stopScanService();
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

    protected BluetoothAdapter mBtAdapter;
    Handler BTHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    /** 停止扫描服务 */
    private void stopScanService() {
		/* 蓝牙2.1 */
        if (null != UtilConstants.serveIntent) {
            stopService(UtilConstants.serveIntent);
        }
    }

    /** BT广播接收器 */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
                    String deviceName = device.getName();
                    System.out.println(deviceName + "=" + device.getAddress());
                    if (deviceName != null && deviceName.equalsIgnoreCase(UtilConstants.scaleName)) {
                        BluetoolUtil.mChatService.connect(device, true);
                        stopDiscovery();
                        BTHandler.postDelayed(ScanRunnable, 15 * 1000);
                    }
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                stopDiscovery();
                BTHandler.postDelayed(ScanRunnable, 10 * 1000);
            }
        }
    };

    /** 开始检测蓝牙 */
    public void startDiscovery() {
        try {
            System.out.println("BT开始扫描...");
            // Register for broadcasts when a device is discovered
            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
            intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            this.registerReceiver(mReceiver, intentFilter);
            mBtAdapter = BluetoothAdapter.getDefaultAdapter();
            mBtAdapter.startDiscovery();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    /** 停止扫描 */
    public void stopDiscovery() {
        try {
            mBtAdapter.cancelDiscovery();
            if (null != mReceiver)
                BaseNotAutoBleActivity.this.unregisterReceiver(mReceiver);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    /** 蓝牙扫描线程 */
    private Runnable ScanRunnable = new Runnable() {
        public void run() {
            startDiscovery();
        }
    };

    /** 检测是否有测量记录线程 */
    private Runnable CheckHasDataRunnable = new Runnable() {
        public void run() {
            if (!AppData.hasCheckData && mActivty && !UtilConstants.isTipChangeScale) {
                scaleChangeAlert();
                UtilConstants.isTipChangeScale = true;
            }
        }
    };

    /** 秤改变弹窗 */
    public void scaleChangeAlert() {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), ScaleChangeAlertActivity.class);
        startActivity(intent);
    }
}
