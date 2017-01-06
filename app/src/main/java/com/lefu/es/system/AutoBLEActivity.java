package com.lefu.es.system;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.lefu.es.ble.BlueSingleton;
import com.lefu.es.ble.BluetoothLeService;
import com.lefu.es.constant.AppData;
import com.lefu.es.constant.BLEConstant;
import com.lefu.es.constant.BluetoolUtil;
import com.lefu.es.constant.UtilConstants;
import com.lefu.es.db.RecordDao;
import com.lefu.es.entity.UserModel;
import com.lefu.es.progressbar.NumberProgressBar;
import com.lefu.es.service.ExitApplication;
import com.lefu.es.service.RecordService;
import com.lefu.es.service.UserService;
import com.lefu.es.util.MyUtil;
import com.lefu.es.util.SharedPreferencesUtil;
import com.lefu.es.util.StringUtils;
import com.lefu.iwellness.newes.cn.system.R;

/**
 * 自动判断蓝牙4.0
 * @author Leon
 */
@SuppressLint("HandlerLeak")
public class AutoBLEActivity extends BaseBleActivity {
	private static final String TAG = "AutoBLEActivity";
	private Button backButton;
	//private BlueSingleton singleton;
	private static final int REQUEST_ENABLE_BT_CLICK = 31;
	//private BluetoothLeService mBluetoothLeService;
	//private BluetoothAdapter mBluetoothAdapter;
	private boolean isServiceReg = false; // mServiceConnection是否已绑定
	private static boolean receiverReleased = false; // mGattUpdateReceiver是否已释放注册
	private RecordService recordService;
	private UserService uservice;

	private int sendCodeCount = 0;

	private SearchDevicesView search_device_view;
	private NumberProgressBar bnp;

	private String scaleType = null;
	private long startTime = System.currentTimeMillis();
	
	/**是否已经返回*/
	private boolean isBack=false;
	/**蓝牙连接标记*/
	private boolean isConneced=false;
	/**显示提示语*/
	private boolean keepScaleWorking=true;

	//protected static final int REQUEST_ACCESS_COARSE_LOCATION_PERMISSION = 101;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auto);
		backButton=(Button) this.findViewById(R.id.btn_mback);
		backButton.setOnClickListener(OnClickListener);
		recordService = new RecordService(this);
		uservice = new UserService(this);
		ExitApplication.getInstance().addActivity(this);
		UtilConstants.su = new SharedPreferencesUtil(AutoBLEActivity.this);
		//开始扫描
		search_device_view = (SearchDevicesView) findViewById(R.id.search_device_view);
		search_device_view.setWillNotDraw(false);
		search_device_view.setSearching(true);
		
		/*进度条*/
		bnp = (NumberProgressBar) findViewById(R.id.numberbar1);

		/* 启动超时线程 */
		new Thread(TimeoutRunnable).start();
		
		/*秤识别中*/
		AppData.isCheckScale=true;
		
		/*判断用户信息是否被清空*/
		if(UtilConstants.CURRENT_USER==null){
			UtilConstants.CURRENT_USER= JSONObject.parseObject((String) UtilConstants.su.readbackUp("lefuconfig", "addUser", ""),UserModel.class);
		}
	}

	@Override
	public void updateConnectionState(int resourceId) {

	}

	@Override
	public void discoverBleService() {

	}

	@Override
	public void reveiveBleData(String data) {

	}

	/**点击事件*/
	View.OnClickListener OnClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			if(v.getId()==R.id.btn_mback){
				backToUserEdit();
			}
		}
	};
	
	/**返回到个人信息编辑界面*/
	private void backToUserEdit(){
		try {
			isBack=true;
		/*是否是重新检测*/
			Boolean reCheck=(Boolean) UtilConstants.su.readbackUp("lefuconfig", "reCheck", false);
		/*结束程序*/
			ExitApplication.getInstance().exit(AutoBLEActivity.this);
			if(reCheck!=null&&reCheck){
				startActivity(new Intent(AutoBLEActivity.this, LoadingActivity.class));
			}else{
				startActivity(new Intent(AutoBLEActivity.this, UserEditActivity.class));
			}
		}catch (Exception e){

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
				case 1 :
					if(!isBack){
						Intent intent0 = new Intent();
						intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent0.setClass(AutoBLEActivity.this, AutoBTActivity.class);
						AutoBLEActivity.this.startActivity(intent0);
						finish();
					}
					break;
				case 2 :
					handler.removeCallbacks(TimeoutRunnable);
					Log.i(TAG, "connected and jump to LoadingActivity");
					/*跳转*/
					ExitApplication.getInstance().exit(AutoBLEActivity.this);
					Intent intent2 = new Intent();
					intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent2.setClass(AutoBLEActivity.this, LoadingActivity.class);
					AutoBLEActivity.this.startActivity(intent2);
					break;
				case 3 :
					if(keepScaleWorking){
						Toast.makeText(AutoBLEActivity.this, getString(R.string.scale_keep_scale_work), Toast.LENGTH_SHORT).show();
						keepScaleWorking=false;
					}else{
						keepScaleWorking=true;
					}
					
					bnp.incrementProgressBy(2);
					break;
				case 4 :
					if(null!=bnp)bnp.setProgress(100);
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
		/*停止BLE扫描*/
		//singleton.isExit=true;
		/* 取消广播接收 */
		//unregisterReceiver(mGattUpdateReceiver);
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			backToUserEdit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}



	/** 数据发送接收 */
//	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			final String action = intent.getAction();
//			if (BLEConstant.ACTION_GATT_CONNECTED.equals(action)) {
//				singleton.setmConnected(true);
//			} else if (BLEConstant.ACTION_GATT_DISCONNECTED.equals(action)) {
//			} else if (BLEConstant.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
//				sendCodeCount = 0;
//				isConneced=true;
//				/* 提示连接成功 */
//				Toast tost = Toast.makeText(AutoBLEActivity.this, getString(R.string.scale_paired_success), Toast.LENGTH_SHORT);
//				tost.setGravity(Gravity.CENTER, 0, 0);
//				tost.show();
//				//Toast.makeText(AutoBLEActivity.this, getString(R.string.scale_paired_success), Toast.LENGTH_SHORT).setGravity(Gravity.CENTER, 0, 0).show();
//				Toast tost1 = Toast.makeText(AutoBLEActivity.this, getString(R.string.scale_paired_success), Toast.LENGTH_SHORT);
//				tost1.setGravity(Gravity.CENTER, 0, 0);
//				tost1.show();
//				Thread thread = new Thread(new Runnable() {
//					@Override
//					public void run() {
//						if (singleton.getmConnected()) {
//							if (mBluetoothLeService != null) {
//								send_data();
//							}
//						}
//					}
//				});
//				thread.start();
//			} else if (BLEConstant.ACTION_DATA_AVAILABLE.equals(action)) {
//				String readMessage = intent.getStringExtra(BLEConstant.EXTRA_DATA);
//				System.out.println("检测读取到数据：" + readMessage);
//				if(TextUtils.isEmpty(readMessage)) return;
//				if (readMessage.startsWith(UtilConstants.BABY_SCALE)) {
//					if (UtilConstants.CURRENT_USER.getAgeYear() < 1 || UtilConstants.CURRENT_USER.getBheigth()<30) {
//						if(UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_KG)){
//							Toast.makeText(AutoBLEActivity.this, getString(R.string.age_error_5), Toast.LENGTH_SHORT).show();
//						}else{
//							Toast.makeText(AutoBLEActivity.this, getString(R.string.age_error_7), Toast.LENGTH_SHORT).show();
//						}
//
//						return;
//					}
//				}else{
//					if (UtilConstants.CURRENT_USER.getAgeYear() < 10 || UtilConstants.CURRENT_USER.getBheigth()<100) {
//						if(UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_KG)){
//							Toast.makeText(AutoBLEActivity.this, getString(R.string.age_error_4), Toast.LENGTH_SHORT).show();
//						}else{
//							Toast.makeText(AutoBLEActivity.this, getString(R.string.age_error_6), Toast.LENGTH_SHORT).show();
//						}
//						return;
//					}
//				}
//				boolean newScale = false;
//				try {
//					if ((readMessage.startsWith("0306"))) {
//						newScale = true;
//						UtilConstants.CURRENT_SCALE = UtilConstants.BODY_SCALE;
//					}else{
//						newScale = false;
//						if (readMessage.equals(UtilConstants.ERROR_CODE)) {
//							if (sendCodeCount <1) {
//								if (mBluetoothLeService != null) {
//									send_data();
//								}
//								sendCodeCount++;
//							} else {
//								if(UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_ST) || UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_LB)){
//									Toast.makeText(AutoBLEActivity.this, getString(R.string.user_data_error), Toast.LENGTH_LONG).show();
//								}else{
//									Toast.makeText(AutoBLEActivity.this, getString(R.string.user_data_error_lb), Toast.LENGTH_LONG).show();
//								}
//
//								//Toast.makeText(AutoBLEActivity.this, getString(R.string.user_data_error), Toast.LENGTH_LONG).show();
//							}
//						} else if (readMessage.equals(UtilConstants.ERROR_CODE_TEST)) {
//							Toast.makeText(AutoBLEActivity.this, getString(R.string.scale_measurement_error), Toast.LENGTH_LONG).show();
//						}
//
//						/** 称类型判断 */
//						String choice_scale = "";
//						if ((readMessage.toLowerCase().startsWith(UtilConstants.BODY_SCALE) && readMessage.length() > 31)) {
//							choice_scale = UtilConstants.BODY_SCALE;
//						}else if ((readMessage.toLowerCase().startsWith(UtilConstants.BATHROOM_SCALE) && readMessage.length() > 31)) {
//							choice_scale = UtilConstants.BATHROOM_SCALE;
//						}else if ((readMessage.toLowerCase().startsWith(UtilConstants.BABY_SCALE) && readMessage.length() > 31)) {
//							choice_scale = UtilConstants.BABY_SCALE;
//						}else if ((readMessage.toLowerCase().startsWith(UtilConstants.KITCHEN_SCALE) && readMessage.length() > 31)) {
//							choice_scale = UtilConstants.KITCHEN_SCALE;
//						}
//						UtilConstants.CURRENT_SCALE = choice_scale;
//					}
//
//
//					/**判断是不是两次连续的数据*/
//					if (readMessage.length() > 31 && (System.currentTimeMillis()- UtilConstants.receiveDataTime>1000)) {
//						UtilConstants.receiveDataTime=System.currentTimeMillis();
//
//						scaleType= UtilConstants.CURRENT_SCALE;
//						UtilConstants.su.editSharedPreferences("lefuconfig", "scale", UtilConstants.CURRENT_SCALE);
//
//						/*是否是重新检测*/
//						Boolean reCheck=(Boolean) UtilConstants.su.readbackUp("lefuconfig", "reCheck", false);
//						UtilConstants.CURRENT_USER.setScaleType(UtilConstants.CURRENT_SCALE);
//						if(reCheck==null||!reCheck){
//							/*添加用户信息*/
//							try {
//								uservice.save(UtilConstants.CURRENT_USER);
//								UtilConstants.CURRENT_USER = uservice.find(uservice.maxid());
//								UtilConstants.CURRENT_USER.setScaleType(UtilConstants.CURRENT_SCALE);
//								UtilConstants.SELECT_USER = UtilConstants.CURRENT_USER.getId();
//								UtilConstants.su.editSharedPreferences("lefuconfig", "user", UtilConstants.SELECT_USER);
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//						}else{
//							/*更新用户信息*/
//							try {
//								uservice.update(UtilConstants.CURRENT_USER);
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//						}
//						/*保存蓝牙类型*/
//						UtilConstants.su.editSharedPreferences("lefuconfig", "bluetooth_type"+ UtilConstants.CURRENT_USER.getId(), "BLE");
//						if (readMessage.toLowerCase().startsWith(UtilConstants.KITCHEN_SCALE)) {
//							RecordDao.dueKitchenDate(recordService, readMessage, null);
//						}else{
//							if(newScale){
//								UtilConstants.CURRENT_USER.setScaleType(UtilConstants.BODY_SCALE);
//								RecordDao.parseZuKangMeaage(recordService,readMessage, UtilConstants.CURRENT_USER);
//							}else{
//								RecordDao.dueDate(recordService,readMessage);
//							}
//						}
//						handler.sendEmptyMessage(2);
//					}
//				} catch (Exception e) {
//					Log.e(TAG, "解析数据异常"+e.getMessage());
//				}
//
//			}
//		}
//	};
//
//	/** 发送数据*/
//	private void send_data() {
//		System.out.println("蓝牙名称:" + BluetoolUtil.mConnectedDeviceName);
//		if(null!= BluetoolUtil.mConnectedDeviceName && (BluetoolUtil.mConnectedDeviceName.toLowerCase().startsWith("heal")
//				|| BluetoolUtil.mConnectedDeviceName.toLowerCase().startsWith("yu"))){
//
//			try {
//				if(null!=mBluetoothLeService){
//					final BluetoothGattCharacteristic characteristic = mBluetoothLeService.getCharacteristicNew(mBluetoothLeService.getSupportedGattServices(), "2a9c");
//					mBluetoothLeService.setCharacteristicIndaicate(characteristic, true); //开始监听通道
//					//发送用户组数据
//					String unit = "00";
//					if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_ST)) {
//						unit = "02";
//					} else if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_LB)) {
//						unit = "01";
//					} else {
//						unit = "00";
//					}
//					// 获取用户组
//					String p = UtilConstants.CURRENT_USER.getGroup().replace("P", "0");
//					// 获取 校验位
//					String xor = Integer.toHexString(StringUtils.hexToTen("fd") ^ StringUtils.hexToTen("37")^ StringUtils.hexToTen(unit) ^ StringUtils.hexToTen(p));
//					Log.e(TAG, "发送新称数据：" + "fd37"+unit + p + "000000000000" + xor);
//					// 发送数据
//					sendDateToScale1("fd37"+unit + p + "000000000000" + xor);
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}else{
//			sendDateToScale1(MyUtil.getUserInfo());
//		}
//	}
//
//	private void sendDateToScale1(String data) {
//		sendDateToScale(data);
//	}
//
//
//	private void sendDateToScale(String data) {
//		final BluetoothGattCharacteristic characteristic2 = mBluetoothLeService.getCharacteristic(mBluetoothLeService.getSupportedGattServices(), "fff4");
//		if (characteristic2 != null) {
//			mBluetoothLeService.setCharacteristicNotification(characteristic2, true);
//		}
//		try {
//			Thread.sleep(300);
//
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		final BluetoothGattCharacteristic characteristic = mBluetoothLeService.getCharacteristic(mBluetoothLeService.getSupportedGattServices(), "fff1");
//		if (characteristic != null) {
//			final byte[] dataArray = StringUtils.hexStringToByteArray(data);
//			characteristic.setValue(dataArray);
//			mBluetoothLeService.wirteCharacteristic(characteristic);
//			characteristic.getProperties();
//		}
//		try {
//			Thread.sleep(500);
//
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		//final BluetoothGattCharacteristic characteristic = mBluetoothLeService.getCharacteristic(mBluetoothLeService.getSupportedGattServices(), "fff1");
//		if (characteristic != null) {
//			final byte[] dataArray = StringUtils.hexStringToByteArray(data);
//			characteristic.setValue(dataArray);
//			mBluetoothLeService.wirteCharacteristic(characteristic);
//			characteristic.getProperties();
//		}
//	}
//
//	/** 休眠 */
//	private void sleep(long time) {
//		try {
//			Thread.sleep(time);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//	}

	/** 超时检测线程 */
	private Runnable TimeoutRunnable = new Runnable() {
		public void run() {
			if (scaleType == null&&!isBack) {
				if ((System.currentTimeMillis() - startTime) > 20000) {
					/*是否已经连接*/
					if(!isConneced){
						handler.sendEmptyMessage(1);
					}
				} else {
					/*是否已经连接*/
					if(!isConneced){
						handler.sendEmptyMessage(3);
					}else{
						/*更新进度为100*/
						handler.sendEmptyMessage(4);
					}
					
					handler.postDelayed(this, 1000);
				}
			}
		}
	};
}
