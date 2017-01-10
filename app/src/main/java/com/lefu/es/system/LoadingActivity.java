package com.lefu.es.system;

import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import com.lefu.es.constant.UtilConstants;
import com.lefu.es.entity.UserModel;
import com.lefu.es.service.ExitApplication;
import com.lefu.es.service.UserService;
import com.lefu.es.util.SharedPreferencesUtil;
import com.lefu.iwellness.newes.cn.system.R;

/**
 * 初始化加载界面
 * @author Leon
 * 2015-11-21
 */
public class LoadingActivity extends Activity {
	private static final String TAG = LoadingActivity.class.getSimpleName();
	
	public static boolean isPad = false,isV = true;
	private UserService uservice;

	public static LoadingActivity mainActivty = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		mainActivty = this;
		
		//isPad();
		
		UtilConstants.su = new SharedPreferencesUtil(LoadingActivity.this);
//		UtilConstants.FIRST_INSTALL_BODY = (String) UtilConstants.su.readbackUp("lefuconfig", "first_install_body","");
		UtilConstants.FIRST_INSTALL_BABY_SCALE = (String) UtilConstants.su.readbackUp("lefuconfig", "first_install_baby_scale","");
		UtilConstants.FIRST_INSTALL_BATH_SCALE = (String) UtilConstants.su.readbackUp("lefuconfig", "first_install_bath_scale","");
		UtilConstants.FIRST_INSTALL_BODYFAT_SCALE = (String) UtilConstants.su.readbackUp("lefuconfig", "first_install_bodyfat_scale","");
		UtilConstants.FIRST_INSTALL_KITCHEN_SCALE = (String) UtilConstants.su.readbackUp("lefuconfig", "first_install_kitchen_scale","");
		
		UtilConstants.FIRST_INSTALL_DETAIL = (String) UtilConstants.su.readbackUp("lefuconfig", "first_install_detail","");
		UtilConstants.FIRST_INSTALL_DAILOG = (String) UtilConstants.su.readbackUp("lefuconfig", "first_install_dailog","");
		UtilConstants.FIRST_INSTALL_SHARE = (String) UtilConstants.su.readbackUp("lefuconfig", "first_install_share","");
		
		UtilConstants.FIRST_RECEIVE_BODYFAT_SCALE_KEEP_STAND_WITH_BARE_FEET = (String) UtilConstants.su.readbackUp("lefuconfig", "first_badyfat_scale_keep_stand_with_bare_feet","");
		
		uservice = new UserService(this);
		ExitApplication.getInstance().addActivity(this);
		
		/*秤检测次数为0*/
		UtilConstants.checkScaleTimes=0;
		
		UtilConstants.su = new SharedPreferencesUtil(LoadingActivity.this);
		UtilConstants.SELECT_LANGUAGE = 1;

	}

	@Override
	protected void onResume() {
		super.onResume();
		loadData();
	}

	@Override
	protected void onDestroy() {
		if (null != UtilConstants.serveIntent) {
			stopService(UtilConstants.serveIntent);
		}
		System.exit(0);
		super.onDestroy();
	}

	/**判断是否是pad*/
	private void isPad(){
		DisplayMetrics dm = new DisplayMetrics();
		dm = getResources().getDisplayMetrics();
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		float density = dm.density;
		double diagonalPixels = Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2));
		double screenSize = diagonalPixels / (160 * density);
		if (screenSize >= 6) {
			isPad = true;
		}
		if (width > height) {
			isV = false;
		} else {
			isV = true;
		}
	}

	/**加载数据*/
	public void loadData() {
		/*加载选择用户*/
		UtilConstants.SELECT_USER = (Integer) UtilConstants.su.readbackUp("lefuconfig", "user", Integer.valueOf(0));
		
		/**加载用户数据*/
		if (UtilConstants.SELECT_USER == 0) {
			/*加载第一个用户数据*/
			getFirstUser();
		} else {
			try {
				UtilConstants.CURRENT_USER = uservice.find(UtilConstants.SELECT_USER);
				/*如果查询不到用户也默认第一个用户*/
				if (null == UtilConstants.CURRENT_USER) {
					getFirstUser();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**判断是否存在用户*/
		if (null == UtilConstants.CURRENT_USER) {
			/**不存在用户跳转到用户添加界面*/
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClass(LoadingActivity.this, UserAddActivity.class);
			LoadingActivity.this.startActivity(intent);
		} else {
			/*加载称类型*/
			UtilConstants.CURRENT_SCALE = UtilConstants.CURRENT_USER.getScaleType();
			
			/*蓝牙类型*/
			String blueTooth_type=(String) UtilConstants.su.readbackUp("lefuconfig", "bluetooth_type"+UtilConstants.CURRENT_USER.getId(), String.class);
			/*蓝牙类型不为空跳到测量主界面，为空跳到检测界面*/
			if(blueTooth_type!=null&&!"".equals(blueTooth_type)){
				/*是否重新检测*/
				UtilConstants.su.editSharedPreferences("lefuconfig", "reCheck", false);
				Intent intent1 = new Intent();
				if (UtilConstants.BATHROOM_SCALE.equals(UtilConstants.CURRENT_SCALE)) {
					intent1.setClass(LoadingActivity.this, BodyScaleNewActivity.class);
					Log.e(TAG, "to BathScaleActivity");
				} else if(UtilConstants.BABY_SCALE.equals(UtilConstants.CURRENT_SCALE)) {
					intent1.setClass(LoadingActivity.this, BabyScaleActivity.class);
					Log.e(TAG, "to BabyScaleActivity");
				}else if(UtilConstants.KITCHEN_SCALE.equals(UtilConstants.CURRENT_SCALE)) {
					intent1.setClass(LoadingActivity.this, KitchenScaleActivity.class);
					Log.e(TAG, "to KitchenScaleActivity");
				}else{
					intent1.setClass(LoadingActivity.this, BodyFatNewActivity.class);
					Log.e(TAG, "to BodyFatScaleActivity");
				}
				intent1.putExtra("ItemID", UtilConstants.SELECT_USER);
				LoadingActivity.this.startActivityForResult(intent1, 99);
			}else{
				int currentapiVersion = android.os.Build.VERSION.SDK_INT;
				Intent intent1 = new Intent();
				intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				/*安卓系统低于4.3只支持BT*/
				if (currentapiVersion < 18) {
					intent1.setClass(LoadingActivity.this, AutoBTActivity.class);
				}else{
					intent1.setClass(LoadingActivity.this, AutoBLEActivity.class);
				}
				LoadingActivity.this.startActivity(intent1);
			}
		}
	}
	
	/**加载第一个用户为当前用户*/
	private void getFirstUser() {
		try {
			List<UserModel> users = uservice.getAllDatas();
			if (null != users && users.size() > 0) {
				UtilConstants.CURRENT_USER = users.get(0);
				UtilConstants.SELECT_USER = users.get(0).getId();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
