package com.lefu.es.system;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.Button;

import com.lefu.es.ble.BlueSingleton;
import com.lefu.es.cache.CacheHelper;
import com.lefu.es.constant.AppData;
import com.lefu.es.constant.BluetoolUtil;
import com.lefu.es.constant.UtilConstants;
import com.lefu.es.db.RecordDao;
import com.lefu.es.entity.NutrientBo;
import com.lefu.es.entity.Records;
import com.lefu.es.service.RecordService;
import com.lefu.es.service.TimeService;
import com.lefu.es.service.UserService;
import com.lefu.iwellness.newes.cn.system.R;

/**
 * 接收到数据弹窗
 * @author lfl
 */
public class ReceiveAlertActivity extends Activity {
	private static final String TAG = "ReceiveAlertActivity";
	String readMessage;
	private Records recod;
	private UserService uservice;
	private RecordService recordService;
	private SoundPool soundpool;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (LoadingActivity.isPad) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_receive_alert);
		uservice = new UserService(this);
		recordService = new RecordService(this);
		Bundle bundle = this.getIntent().getExtras();
		recod = (Records) getIntent().getSerializableExtra("record");
		readMessage = bundle.getString("duedate");
		playSound();
	}

	/**播放声音*/
	public void playSound() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				soundpool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
				int sourceid = -1;
				sourceid = soundpool.load(ReceiveAlertActivity.this, R.raw.ring, 0);
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

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN && isOutOfBounds(this, event)) {
			return true;
		}
		return super.onTouchEvent(event);
	}

	private boolean isOutOfBounds(Activity context, MotionEvent event) {
		final int x = (int) event.getX();
		final int y = (int) event.getY();
		final int slop = ViewConfiguration.get(context).getScaledWindowTouchSlop();
		final View decorView = context.getWindow().getDecorView();
		return (x < -slop) || (y < -slop) || (x > (decorView.getWidth() + slop)) || (y > (decorView.getHeight() + slop));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (null != uservice) {
			this.uservice.closeDB();
		}
		if (null != this.recordService) {
			this.recordService.closeDB();
		}
	}

	public void saveOrCancle(View v) {
		if (v instanceof Button) {
			switch (v.getId()) {
				case R.id.cancle_datacbtn :
					if (!BluetoolUtil.bleflag)
						TimeService.setIsdoing(false);
					else
						BlueSingleton.setIsdoing(false);
					ReceiveAlertActivity.this.finish();
					break;
				case R.id.save_databtn :
					/*修改标识*/
					try {
						AppData.hasCheckData=true;
						if (!BluetoolUtil.bleflag)
							TimeService.setIsdoing(true);
						else
							BlueSingleton.setIsdoing(true);
						if (null != recod && null != recod.getScaleType()) {
							if (UtilConstants.KITCHEN_SCALE.equals(UtilConstants.CURRENT_SCALE)) {
								NutrientBo nutrient = null;
								if(!TextUtils.isEmpty(recod.getRphoto())){
									nutrient = CacheHelper.queryNutrientsByName(recod.getRphoto());
								}
								RecordDao.dueKitchenDate(recordService, readMessage,nutrient);
							} else {
								RecordDao.handleData(recordService, recod, readMessage);
							}
							
							
							if (!BluetoolUtil.bleflag){
								TimeService.setIsdoing(false);
							}else{
								BlueSingleton.setIsdoing(false);
							}
							Intent intent1 = new Intent();
							if (UtilConstants.BATHROOM_SCALE.equals(UtilConstants.CURRENT_SCALE)) {
								intent1.setClass(ReceiveAlertActivity.this, BathScaleActivity.class);
							} else if(UtilConstants.BODY_SCALE.equals(UtilConstants.CURRENT_SCALE)) {
								intent1.setClass(ReceiveAlertActivity.this, BodyFatScaleActivity.class);
							}else if(UtilConstants.BABY_SCALE.equals(UtilConstants.CURRENT_SCALE)){
								if(null!=uservice && null!=UtilConstants.CURRENT_USER)uservice.update(UtilConstants.CURRENT_USER);
								intent1.setClass(ReceiveAlertActivity.this, BabyScaleActivity.class);
							}else if(UtilConstants.KITCHEN_SCALE.equals(UtilConstants.CURRENT_SCALE)){
								intent1.setClass(ReceiveAlertActivity.this, KitchenScaleActivity.class);
							}
							intent1.putExtra("ItemID", UtilConstants.CURRENT_USER.getId());
							intent1.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
							ReceiveAlertActivity.this.startActivity(intent1);
						}
						
						ReceiveAlertActivity.this.finish();
					} catch (Exception e) {
						Log.e(TAG, "保存用户测量数据异常"+e.getMessage());
					}
					break;
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			ReceiveAlertActivity.this.finish();
			if (!BluetoolUtil.bleflag){
				TimeService.setIsdoing(false);
			}else{
				BlueSingleton.setIsdoing(false);
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_HOME) {
			NotificationManager notificationManager = (NotificationManager) this.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
			notificationManager.cancel(0);
		}
		return super.onKeyDown(keyCode, event);
	}

}
