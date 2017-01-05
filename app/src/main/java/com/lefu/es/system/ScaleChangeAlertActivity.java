package com.lefu.es.system;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.lefu.es.constant.AppData;
import com.lefu.es.constant.UtilConstants;
import com.lefu.es.util.SharedPreferencesUtil;
import com.lefu.iwellness.newes.cn.system.R;

/**
 * 更换秤弹窗
 * @author lfl
 */
public class ScaleChangeAlertActivity extends Activity {

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
		setContentView(R.layout.activity_check_alert);
		 if(UtilConstants.su==null){
			 UtilConstants.su = new SharedPreferencesUtil(ScaleChangeAlertActivity.this);
		 }
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			return true;
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void saveOrCancle(View v) {
		if (v instanceof Button) {
			switch (v.getId()) {
				case R.id.cancle_datacbtn :
					UtilConstants.su.editSharedPreferences("lefuconfig", "reCheck", false);
					ScaleChangeAlertActivity.this.finish();
					break;
				case R.id.save_databtn :
					AppData.reCheck=true;
					UtilConstants.su.editSharedPreferences("lefuconfig", "reCheck", true);
					ScaleChangeAlertActivity.this.finish();
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
			ScaleChangeAlertActivity.this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
