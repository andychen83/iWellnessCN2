package com.lefu.es.service;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
/**
 * APP退出
 * @author Leon
 * 2015-11-17
 */
public class ExitApplication extends Application {
	private List<Activity> list = new ArrayList<Activity>();

	private static ExitApplication ea;

	private ExitApplication() {
	}

	public static ExitApplication getInstance() {
		if (null == ea) {
			ea = new ExitApplication();
		}
		return ea;
	}

	public void addActivity(Activity activity) {
		list.add(activity);
	}

	public void exit(Context context) {
		for (Activity activity : list) {
			activity.finish();
		}
	}
}
