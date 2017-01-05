package com.lefu.es.system;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.KeyEvent;
import android.view.Window;

import com.lefu.iwellness.newes.cn.system.R;

/**
 * 脂肪数据错误
 * @author Leon
 * 2015-11-21
 */
public class GetFatDateErrorActivity extends Activity {
	public static Handler handler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_fat_date_error);
		if(LoadingActivity.isPad){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
		  }else{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	@Override
	protected void onPause() {
		finish();
		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			GetFatDateErrorActivity.this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
