package com.lefu.es.system;

import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.lefu.es.constant.UtilConstants;
import com.lefu.es.entity.UserModel;
import com.lefu.es.service.ExitApplication;
import com.lefu.es.service.UserService;
import com.lefu.es.util.SharedPreferencesUtil;
/**
 * 重新检测加载用户信息
 * @author lfl
 *
 */
public class ReCheckActivity extends Activity implements Runnable {
	private UserService uservice;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		UtilConstants.su = new SharedPreferencesUtil(ReCheckActivity.this);
		uservice = new UserService(this);
		ExitApplication.getInstance().addActivity(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		new Thread(this).start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void run() {
		UtilConstants.CURRENT_SCALE = (String) UtilConstants.su.readbackUp("lefuconfig", "scale", "");
		UtilConstants.SELECT_USER = (Integer) UtilConstants.su.readbackUp("lefuconfig", "user", Integer.valueOf(0));
		if (UtilConstants.SELECT_USER == 0) {
			try {
				if (null == uservice)
					uservice = new UserService(this);
				List<UserModel> uss = uservice.getAllDatas();
				if (null != uss && uss.size() > 0) {
					UtilConstants.CURRENT_USER = uss.get(0);
					UtilConstants.SELECT_USER = uss.get(0).getId();
				}
			} catch (Exception e) {
			}
		} else {
			try {
				if (null == uservice)
					uservice = new UserService(this);
				UtilConstants.CURRENT_USER = uservice.find(UtilConstants.SELECT_USER);
				if (null == UtilConstants.CURRENT_USER) {
					List<UserModel> uss = uservice.getAllDatas();
					if (null != uss && uss.size() > 0) {
						UtilConstants.CURRENT_USER = uss.get(0);
						UtilConstants.SELECT_USER = uss.get(0).getId();
					}
				}
			} catch (Exception e) {
			}
		}

		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		Intent intent1 = new Intent();
		intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (currentapiVersion < 18) {
			intent1.setClass(ReCheckActivity.this, AutoBTActivity.class);
		}else{
			intent1.setClass(ReCheckActivity.this, AutoBLEActivity.class);
		}
		ReCheckActivity.this.startActivity(intent1);
	}

}
