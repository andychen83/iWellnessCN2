package com.lefu.es.system;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lefu.es.constant.AppData;
import com.lefu.es.constant.BluetoolUtil;
import com.lefu.es.constant.UtilConstants;
import com.lefu.es.progressbar.NumberProgressBar;
import com.lefu.es.service.ExitApplication;
import com.lefu.es.service.TimeService;
import com.lefu.es.util.SharedPreferencesUtil;
import com.lefu.iwellness.newes.cn.system.R;

/**
 * 自动判断蓝牙2.1
 * @author Leon
 */
@SuppressLint("HandlerLeak")
public class AutoBTActivity extends Activity {
	private Button backButton;
	private SearchDevicesView search_device_view;
	private NumberProgressBar bnp;
	private BluetoothAdapter mBtAdapter;
	/**需要连接的蓝牙设备*/
	private BluetoothDevice connectdevice;
	/**开始时间*/
	private long startTime=System.currentTimeMillis();
	/**扫描时间*/
	private static int checkTime=30000;
	/**是否版本低于4.3*/
	private static boolean isOnlySupprotBT=false;
	/**是否注册回调*/
	private static boolean isRegisterReceiver=false;
	/**是否已经返回*/
	private boolean isBack=false;
	/**显示提示语*/
	private boolean keepScaleWorking=true;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auto);
		backButton=(Button) this.findViewById(R.id.btn_mback);
		backButton.setOnClickListener(OnClickListener);
		
		ExitApplication.getInstance().addActivity(this);
		
		UtilConstants.su = new SharedPreferencesUtil(AutoBTActivity.this);

		// 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
		if (null == BluetoolUtil.mBluetoothAdapter)
			BluetoolUtil.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (null != BluetoolUtil.mBluetoothAdapter && !BluetoolUtil.mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, BluetoolUtil.REQUEST_ENABLE_BT);
		}
		if (null == UtilConstants.serveIntent) {
			UtilConstants.serveIntent = new Intent(this, TimeService.class);
			this.startService(UtilConstants.serveIntent);
		}
		
		/*扫描蓝牙*/
		new Thread(ScanRunnable).start();
		/*超时检测线程*/
        new Thread(TimeoutRunnable).start();
        
		//开始扫描
		search_device_view = (SearchDevicesView) findViewById(R.id.search_device_view);
		search_device_view.setWillNotDraw(false);
		search_device_view.setSearching(true);
		
		/*进度条*/
		bnp = (NumberProgressBar) findViewById(R.id.numberbar1);
		
		/*判断是不是不支持BLE，只扫描BT*/
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if(currentapiVersion<18){
			isOnlySupprotBT=true;
		}
		
		if(isOnlySupprotBT){
			checkTime=25000;
			bnp.setProgress(0);
		}else{
			bnp.setProgress(40);
		}
        
		/*秤识别中*/
		AppData.isCheckScale=true;
	}
	
	/**点击事件*/
	android.view.View.OnClickListener OnClickListener = new android.view.View.OnClickListener() {
		public void onClick(View v) {
			if(v.getId()==R.id.btn_mback){
				backToUserEdit();
			}
		}
	};
	
	/**退出到用户编辑界面*/
	private void backToUserEdit(){
		isBack=true;
		/*是否是重新检测*/
		Boolean reCheck=(Boolean) UtilConstants.su.readbackUp("lefuconfig", "reCheck", false);
		/*结束程序*/
		ExitApplication.getInstance().exit(AutoBTActivity.this);
		if(reCheck!=null&reCheck){
			startActivity(new Intent(AutoBTActivity.this, LoadingActivity.class));
		}else{
			startActivity(new Intent(AutoBTActivity.this, UserEditActivity.class));
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case 2 :
					if(UtilConstants.checkScaleTimes==0){
						Toast.makeText(AutoBTActivity.this, getString(R.string.detect_timeout_once_more), Toast.LENGTH_LONG).show();
						UtilConstants.checkScaleTimes++;
						AutoBTActivity.this.startActivity(new Intent(AutoBTActivity.this, AutoBLEActivity.class));
						finish();
					}else{
						Toast.makeText(AutoBTActivity.this, getString(R.string.detect_timeout), Toast.LENGTH_LONG).show();
					}
					break;
				case 3 :
					handler.removeCallbacks(TimeoutRunnable);
					
					UtilConstants.serveIntent=null;
					/*跳转*/
					Intent intent2 = new Intent();
					intent2.setClass(AutoBTActivity.this, LoadingActivity.class);
					ExitApplication.getInstance().exit(AutoBTActivity.this);
					AutoBTActivity.this.startActivity(intent2);
					break;
				case 4 :
					if(keepScaleWorking){
						Toast.makeText(AutoBTActivity.this, getString(R.string.scale_keep_scale_work), Toast.LENGTH_SHORT).show();
						keepScaleWorking=false;
					}else{
						keepScaleWorking=true;
					}
					
					if(isOnlySupprotBT){
						bnp.incrementProgressBy(4);
					}else{
						bnp.incrementProgressBy(2);
					}
					break;
			}
		}
	};

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		if(isRegisterReceiver){
  		  AutoBTActivity.this.unregisterReceiver(mReceiver);
  		  isRegisterReceiver=false;
  	  }
		if (null != UtilConstants.serveIntent){
			stopService(UtilConstants.serveIntent);
		}
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
        
        handler.removeCallbacks(ScanRunnable);
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK ) {
			backToUserEdit();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_HOME) {
			if (null != UtilConstants.serveIntent)
				stopService(UtilConstants.serveIntent);
		}
		return super.onKeyDown(keyCode, event);
	}
	
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device!=null){
                	 String deviceName=device.getName();
                     System.out.println(deviceName + "=" + device.getAddress());
                     if(deviceName!=null&&deviceName.equalsIgnoreCase(UtilConstants.scaleName)&&connectdevice==null){
                     	connectdevice=device;
                     	BluetoolUtil.mChatService.connect(device, true);
                     	stopDiscovery();
                     }
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                stopDiscovery();
            }
        }
    };
    
    /**开始检测蓝牙*/
    public void startDiscovery(){
		// Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);
        
        isRegisterReceiver=true;
        
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        mBtAdapter.startDiscovery();
    }
    
    /**停止扫描*/
    public void stopDiscovery(){
    	  mBtAdapter.cancelDiscovery();
    	  if(isRegisterReceiver){
    		  AutoBTActivity.this.unregisterReceiver(mReceiver);
    		  isRegisterReceiver=false;
    	  }
    }
    
	/**蓝牙扫描线程*/
	private Runnable ScanRunnable= new Runnable() {
        public void run() { 
        	startDiscovery();
        	handler.postDelayed(this, 7*1000);
        }  
    };
    
	/**超时检测线程*/
	private Runnable TimeoutRunnable= new Runnable() {    
        public void run() { 
        	if(!isBack){
        		if(TimeService.scaleType==null){
    				if((System.currentTimeMillis()-startTime)>checkTime){
    					/*判断是否已经连接*/
    					if(!AppData.hasCheckData){
    						handler.sendEmptyMessage(2);
    					}
        			}else{
        				/*判断是否已经连接*/
            			if(!AppData.hasCheckData){
            				handler.sendEmptyMessage(4);
            			}else{
            				/*更新进度为100*/
            				bnp.setProgress(100);
            			}
            			handler.postDelayed(this, 1000);
        			}
        		}else{
        			handler.sendEmptyMessage(3);
        		}
        	}
        }  
    };

}
