package com.lefu.es.system;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

import com.lefu.es.constant.UtilConstants;
import com.lefu.iwellness.newes.cn.system.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ZoomButtonsController;
/**
 * Info信息界面
 * @author Leon 
 * 2015-11-21
 */
public class InfoActivity extends Activity {
	WebView weView;
	@SuppressWarnings("unused")
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (LoadingActivity.isPad) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		setContentView(R.layout.activity_info);
		weView = (WebView) findViewById(R.id.wv_activity_help);
		weView.getSettings().setJavaScriptEnabled(true);
		weView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		weView.getSettings().setSupportZoom(true);

		weView.getSettings().setBuiltInZoomControls(true);

		int screenDensity = getResources().getDisplayMetrics().densityDpi;
		WebSettings.ZoomDensity zoomDensity = WebSettings.ZoomDensity.MEDIUM;
		switch (screenDensity) {
			case DisplayMetrics.DENSITY_LOW :
				zoomDensity = WebSettings.ZoomDensity.CLOSE;
				break;
			case DisplayMetrics.DENSITY_MEDIUM :
				zoomDensity = WebSettings.ZoomDensity.MEDIUM;
				break;
			case DisplayMetrics.DENSITY_HIGH :
				zoomDensity = WebSettings.ZoomDensity.FAR;
				break;
		}
		weView.setInitialScale(25);
		weView.getSettings().setUseWideViewPort(true);
		weView.getSettings().setLoadWithOverviewMode(true);
		if (UtilConstants.CHOICE_KG.equals(UtilConstants.UNIT_KG)) {
			if (UtilConstants.CURRENT_SCALE.equals(UtilConstants.BABY_SCALE)) {
				weView.loadUrl("file:///android_asset/info_baby_kg.htm");
			} else {
				weView.loadUrl("file:///android_asset/info.htm");
			}
		} else {
			if (UtilConstants.CURRENT_SCALE.equals(UtilConstants.BABY_SCALE)) {
				weView.loadUrl("file:///android_asset/info_baby_lboz.htm");
			} else {
				weView.loadUrl("file:///android_asset/info_lb.htm");
			}
		}

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
		finish();
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

}
