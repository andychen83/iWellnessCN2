package com.lefu.es.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.lefu.es.constant.AppData;
import com.lefu.es.constant.BTConstant;
import com.lefu.es.constant.BluetoolUtil;
import com.lefu.es.constant.BluetoothTools;
import com.lefu.es.constant.UtilConstants;
import com.lefu.es.db.RecordDao;
import com.lefu.es.entity.Records;
import com.lefu.es.entity.UserModel;
import com.lefu.es.system.ReceiveAlertActivity;
import com.lefu.es.util.MyUtil;
import com.lefu.es.util.StringUtils;
import com.lefu.iwellness.newes.cn.system.R;

/**
 * BT连接服务
 * @author lfl
 */
@SuppressLint("HandlerLeak")
public class TimeService extends Service {
	private UserService uservice;
	/*是否正在处理*/
	static boolean isdoings = false;
	/*发送数据次数*/
	private int count = 0;
	/*称类型*/
	public static String scaleType;
	/*连接状态*/
	public static TextView scale_connect_state=null;

	private RecordService recordService;
	
	public static synchronized boolean isIsdoing() {
		return isdoings;
	}

	public static synchronized void setIsdoing(boolean isdoing) {
		isdoings = isdoing;
	}

	@Override
	public void onCreate() {
		uservice = new UserService(this);
		recordService = new RecordService(this);

		if (BluetoolUtil.mChatService == null) {
			BluetoolUtil.mChatService = new BluetoothChatService(this, handler);
		}
		if (BluetoolUtil.mChatService != null) {
			if (BluetoolUtil.mChatService.getState() == BTConstant.STATE_NONE) {
				BluetoolUtil.mChatService.start();
			}
		}
		
		/*判断用户信息是否被清空*/
		if(UtilConstants.CURRENT_USER==null){
			UtilConstants.CURRENT_USER=JSONObject.parseObject((String)UtilConstants.su.readbackUp("lefuconfig", "addUser", ""),UserModel.class);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		stopSelf();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/** 发送数据到设备 */
	private void save2Device() {
		if (BluetoolUtil.mChatService != null) {
			String data = MyUtil.getUserInfo();
			System.out.println("发送数据："+data);
			final byte[] dataArray = StringUtils.hexStringToByteArray(data);
			BluetoolUtil.mChatService.write(dataArray);
		}
	}
	

	/** 发送关机指令到设备 */
	private void send_shutdown() {
		if (BluetoolUtil.mChatService != null) {
			String data =UtilConstants.SCALE_ORDER_SHUTDOWN;
			final byte[] dataArray = StringUtils.hexStringToByteArray(data);
			BluetoolUtil.mChatService.write(dataArray);
		}
	}

	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Intent selectDeviceIntent = null;
			switch (msg.what) {
				case BluetoolUtil.MESSAGE_STATE_CHANGE :
					switch (msg.arg1) {
						case BTConstant.STATE_CONNECTED :
							count=0;
							/*检测到数据*/
							AppData.hasCheckData=true;
							Toast.makeText(TimeService.this, getString(R.string.scale_connection_success), Toast.LENGTH_SHORT).show();
							Toast.makeText(TimeService.this, getString(R.string.scale_connection_success), Toast.LENGTH_SHORT).show();
							save2Device();
							if(scale_connect_state!=null){
								scale_connect_state.setText(R.string.scale_connect_state_connected);
							}
							break;
						case BTConstant.STATE_CONNECTING :
							break;
						case BTConstant.STATE_LISTEN :
						case BTConstant.STATE_NONE :
							break;
					}
					break;
				case BluetoolUtil.MESSAGE_WRITE :
					break;
				case BluetoolUtil.MESSAGE_READ :
					byte[] readBuf = (byte[]) msg.obj;
					byte[] read = new byte[msg.arg1];
					if (null != read) {
						for (int i = 0; i < read.length; i++) {
							read[i] = readBuf[i];
						}
					}
					String readMessage = StringUtils.bytes2HexString(read);
					selectDeviceIntent = new Intent(BluetoothTools.ACTION_READ_DATA);
					selectDeviceIntent.putExtra("readMessage", readMessage);
					sendBroadcast(selectDeviceIntent);
					System.out.println("读取到数据：" + readMessage);
					if(readMessage.length()>=31){
						/**判断是不是两次连续的数据*/
						if(System.currentTimeMillis()-UtilConstants.receiveDataTime<1000){
							return;
						}
						UtilConstants.receiveDataTime=System.currentTimeMillis();
					}
					if (readMessage.equals(UtilConstants.ERROR_CODE)) {
						if (count < 3) {
							count++;
							save2Device();
						} else {
							Toast.makeText(TimeService.this, getString(R.string.user_data_error), Toast.LENGTH_LONG).show();
							Toast.makeText(TimeService.this, getString(R.string.user_data_error), Toast.LENGTH_LONG).show();
						}
					} else if (readMessage.equals(UtilConstants.ERROR_CODE_TEST)) {
						Toast.makeText(TimeService.this, getString(R.string.scale_measurement_error), Toast.LENGTH_SHORT).show();
					}
					if (readMessage.equals(UtilConstants.ERROR_CODE_GETDATE)) {
						return;
					}
					if(readMessage.length()<32){
						return;
					}
					String blueTooth_type = (String) UtilConstants.su.readbackUp("lefuconfig", "bluetooth_type"+UtilConstants.CURRENT_USER.getId(), String.class);
					if (blueTooth_type != null&&!"".equals(blueTooth_type)&&!AppData.isCheckScale) {
						/*判断称的类似是否改变*/
						
						
						dueDate(readMessage);
						/*关机*/
						send_shutdown();
						if(scale_connect_state!=null){
							scale_connect_state.setText(R.string.scale_connect_state_not_connected);
						}
					} else {
						/** 称类型判断 */
						String choice_scale = "";
						if ((readMessage.toLowerCase().startsWith(UtilConstants.BODY_SCALE) && readMessage.length() > 31)) {
							choice_scale = UtilConstants.BODY_SCALE;
						}
						if ((readMessage.toLowerCase().startsWith(UtilConstants.BATHROOM_SCALE) && readMessage.length() > 31)) {
							choice_scale = UtilConstants.BATHROOM_SCALE;
						}
						if ((readMessage.toLowerCase().startsWith(UtilConstants.BABY_SCALE) && readMessage.length() > 31)) {
							choice_scale = UtilConstants.BABY_SCALE;
						}
						if ((readMessage.toLowerCase().startsWith(UtilConstants.KITCHEN_SCALE) && readMessage.length() > 31)) {
							choice_scale = UtilConstants.KITCHEN_SCALE;
						}
						UtilConstants.CURRENT_SCALE = choice_scale;
						scaleType=UtilConstants.CURRENT_SCALE;
						UtilConstants.su.editSharedPreferences("lefuconfig", "scale", UtilConstants.CURRENT_SCALE);
						
						/*是否是重新检测*/
						Boolean reCheck=(Boolean) UtilConstants.su.readbackUp("lefuconfig", "reCheck", false);
						UtilConstants.CURRENT_USER.setScaleType(UtilConstants.CURRENT_SCALE);
						if(reCheck==null||!reCheck){
							/*添加用户信息*/
							try {
								uservice.save(UtilConstants.CURRENT_USER);
								UtilConstants.CURRENT_USER = uservice.find(uservice.maxid());
								UtilConstants.SELECT_USER = UtilConstants.CURRENT_USER.getId();
								UtilConstants.su.editSharedPreferences("lefuconfig", "user", UtilConstants.SELECT_USER);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}else{
							/*更新用户信息*/
							try {
								uservice.update(UtilConstants.CURRENT_USER);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						/*保存蓝牙类型*/
						UtilConstants.su.editSharedPreferences("lefuconfig", "bluetooth_type"+UtilConstants.CURRENT_USER.getId(), "BT");
						/*保存数据*/
						RecordDao.dueDate(recordService, readMessage);
						/*关机*/
						send_shutdown();
					}
					break;
				case BluetoolUtil.MESSAGE_DEVICE_NAME :
					break;
				case BluetoolUtil.MESSAGE_TOAST :
					break;
			}
		}
	};

	/**处理数据*/
	public synchronized void dueDate(String message) {
		if (!isdoings) {
			setIsdoing(true);
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("duedate", message);
			Records recod = MyUtil.parseMeaage(this.recordService, message);
			if (null != recod.getScaleType() && recod.getScaleType().equalsIgnoreCase(UtilConstants.CURRENT_SCALE)) {
				if (recod != null && recod.getUgroup() != null) {
					int ugroup = Integer.parseInt(recod.getUgroup().replace("P", ""));
					if (ugroup < 10) {
						Bundle mBundle = new Bundle();
						mBundle.putSerializable("record", recod);
						intent.putExtras(mBundle);
						intent.setClass(getApplicationContext(), ReceiveAlertActivity.class);
						startActivity(intent);
					}
				}
			}
		}
	}
	
}
