package com.lefu.es.system;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lefu.iwellness.newes.cn.system.R;

/**
 * 自定义弹窗界面
 * @author Leon
 * 2015-11-19
 */
public class CustomDialogActivity extends Activity {
	public static Handler handler;
	LinearLayout layout1, layout2;
	TextView tv1;
	ProgressBar pbar1;
	Button okBtn;
	@SuppressLint("HandlerLeak")
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
		setContentView(R.layout.waiting_alert);
		layout1 = (LinearLayout) findViewById(R.id.waiting_alert_layout_1);
		layout2 = (LinearLayout) findViewById(R.id.waiting_alert_layout_2);
		tv1 = (TextView) findViewById(R.id.textView1);
		pbar1 = (ProgressBar) findViewById(R.id.progressBar1);
		okBtn = (Button) findViewById(R.id.okBtn);
		okBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				CustomDialogActivity.this.finish();
			}
		});
		Bundle bundle = this.getIntent().getExtras();
		String er = null;
		try {
			if (bundle.containsKey("error")){
				er = bundle.getString("error");
			}
		} catch (Exception e) {
			er = null;
		}
		if (null != er && "2".equals(er)) {
			layout1.setVisibility(View.GONE);
			layout2.setVisibility(View.VISIBLE);
		} else if (null != er && "3".equals(er)) {
			layout1.setVisibility(View.VISIBLE);
			okBtn.setVisibility(View.VISIBLE);
			layout2.setVisibility(View.GONE);
			pbar1.setVisibility(View.GONE);
			tv1.setText(this.getText(R.string.scaleerror));
		} else {
			layout1.setVisibility(View.VISIBLE);
			layout2.setVisibility(View.GONE);
		}
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case 1 :
						layout1.setVisibility(View.GONE);
						layout2.setVisibility(View.VISIBLE);
						finish();
						break;
					case 2 :
						finish();
						break;
					default :
						finish();
						break;
				}
				super.handleMessage(msg);
			}
		};

		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				CustomDialogActivity.this.finish();
			}
		}, 10000);

	}

	@Override
	protected void onPause() {
		finish();
		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			CustomDialogActivity.this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
