package com.lefu.es.system;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.ZoomButtonsController;

import com.lefu.es.constant.UtilConstants;
import com.lefu.iwellness.newes.cn.system.R;

/**
 * 网页界面
 * @author Leon
 * 2015-11-21
 */
public class HelpActivity extends Activity {
	WebView weView;
	TextView tvTitle;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (LoadingActivity.isPad) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		setContentView(R.layout.activity_helper);
		
		tvTitle = (TextView) findViewById(R.id.textView_title);
		weView = (WebView) findViewById(R.id.wv_activity_help);
		weView.getSettings().setJavaScriptEnabled(true);
		weView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		weView.getSettings().setSupportZoom(true);

		/*关于界面*/
		weView.getSettings().setBuiltInZoomControls(true);
		weView.setInitialScale(200);
		weView.setWebChromeClient(new MyWebChromeClient());
		weView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				weView.loadUrl("file:///android_asset/help.html");
				return false;
			}
		});
		weView.loadUrl("file:///android_asset/help.html");
			
		int sysVersion = Integer.parseInt(VERSION.SDK);
		if (sysVersion >= 11) {
			setZoomControlGoneX(weView.getSettings(), new Object[]{false});
		} else {
			setZoomControlGone(weView);
		}
		weView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_UP :
					case MotionEvent.ACTION_POINTER_UP :
						break;
					default :
						break;
				}
				return false;
			}
		});
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	public void setZoomControlGoneX(WebSettings view, Object[] args) {
		Class classType = view.getClass();
		try {
			Class[] argsClass = new Class[args.length];

			for (int i = 0, j = args.length; i < j; i++) {
				argsClass[i] = args[i].getClass();
			}
			Method[] ms = classType.getMethods();
			for (int i = 0; i < ms.length; i++) {
				if (ms[i].getName().equals("setDisplayZoomControls")) {
					try {
						ms[i].invoke(view, false);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void setZoomControlGone(View view) {
		Class classType;
		Field field;
		try {
			classType = WebView.class;
			field = classType.getDeclaredField("mZoomButtonsController");
			field.setAccessible(true);
			ZoomButtonsController mZoomButtonsController = new ZoomButtonsController(view);
			mZoomButtonsController.getZoomControls().setVisibility(View.GONE);
			try {
				field.set(view, mZoomButtonsController);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	public void onClickBack(View v) {
		this.finish();
	}

	public void openIntentate(View v) {
		Intent intent = new Intent();
		intent.setData(Uri.parse(UtilConstants.homeUrl));
		intent.setAction(Intent.ACTION_VIEW);
		this.startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			NotificationManager notificationManager = (NotificationManager) this.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
			notificationManager.cancel(0);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	class MyWebChromeClient extends WebChromeClient {
		@Override
		public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
			result.cancel();
			return true;
		}
	}
}
