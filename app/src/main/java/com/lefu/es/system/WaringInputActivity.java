package com.lefu.es.system;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.lefu.iwellness.newes.cn.system.R;

/**
 * 收到消息提示是否保存
 * @author Leon
 * 2015-11-19
 */
public class WaringInputActivity extends Activity {
    private String readMessage;
    private TextView tx = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(LoadingActivity.isPad){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
		  }else{
			  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_waring_input);
		
		Bundle bundle = this.getIntent().getExtras();
		if(bundle.containsKey("waringmsg"))readMessage = bundle.getString("waringmsg");
		
		tx = (TextView)this.findViewById(R.id.textView2);
		if(null!=tx && null!=readMessage){
			tx.setText(readMessage);
		}
	}
	
	public void saveOrCancle(View v) {
		if (v instanceof Button) {
			switch (v.getId()) {
			case R.id.save_databtn:
				WaringInputActivity.this.finish();
				break;
			}
		}
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
		return (x < -slop) || (y < -slop)|| (x > (decorView.getWidth() + slop))|| (y > (decorView.getHeight() + slop));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			WaringInputActivity.this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


}
