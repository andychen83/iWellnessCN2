package com.lefu.es.constant;

import java.io.File;

import android.content.Intent;

import com.lefu.es.entity.UserModel;
import com.lefu.es.util.SharedPreferencesUtil;
/**
 * 工具常量
 * @author Leon
 * 2015-11-17
 */
public class UtilConstants {
	/***是否首次安装***/
//	public static String FIRST_INSTALL_BODY = "";
	public static String FIRST_INSTALL_BABY_SCALE = "";
	public static String FIRST_INSTALL_BATH_SCALE = "";
	public static String FIRST_INSTALL_BODYFAT_SCALE = "";
	public static String FIRST_INSTALL_KITCHEN_SCALE = "";
	public static String FIRST_RECEIVE_BODYFAT_SCALE_KEEP_STAND_WITH_BARE_FEET = "";
	
	public static String FIRST_INSTALL_DETAIL = "";
	public static String FIRST_INSTALL_DAILOG = "";
	public static String FIRST_INSTALL_SHARE = "";
	
	/***是否打印log*****/
	public static boolean               LOG_DEBUG                               = true;
	/**用户头像保存路径*/
	public static final String USER_HEADER_CACHE_PATH = "/sdcard/healthscale/userheader/";
	/**SD卡路径*/
	public static final String SDCARD_ROOT_PATH = "/mnt/sdcard/";
	/**APP子目录*/
	public static final String APPLICATION_PATH = "HealthScale/";
	/**头像子目录*/
	public static final String IMAGE_CACHE_PATH = "Photos";
	/**主页logo链接网址*/
	public static String homeUrl = "http://www.familyscale.com/";
	/**脂肪称*/
	public static String BODY_SCALE = "cf";
	/**人体称*/
	public static String BATHROOM_SCALE = "ce";
	/**婴儿称*/
	public static String BABY_SCALE = "cb";
	/**选择的称*/
	public static String SELECT_SCALE = "cf";
	/**厨房秤*/
	public static String KITCHEN_SCALE = "ca";
	/**当前选择用户id*/
	public static int SELECT_USER = 0;
	/**当前称类型*/
	public static String CURRENT_SCALE = BODY_SCALE;

	/**当前用户数据*/
	public static UserModel CURRENT_USER = null;
	
	/**选择的语言**/
	public static int SELECT_LANGUAGE = 0;

	/**相关参数标识*/
	public static final int WEIGHT_SINGLE = 0;
	public static final int BONE_SINGLE = 1;
	public static final int BODYFAT_SINGLE = 2;
	public static final int MUSCALE_SINGLE = 3;
	public static final int BODYWATER_SINGLE = 4;
	public static final int VISCALEFAT_SINGLE = 5;
	public static final int BMI_SINGLE = 6;
	public static final int BMR_SINGLE = 7;
	public static final int PHSICALAGE_SINGLE = 8;

	/**单位*/
	public static String CHOICE_KG = "kg";
	public static String UNIT_KG = "kg";
	public static String UNIT_ST = "st:lb";
	public static String UNIT_LB = "lb";
	public static String UNIT_G = "g";
	public static String UNIT_ML = "ml";
	public static String UNIT_ML2 = "ml(milk)";
	public static String UNIT_FATLB = "lb:oz";
	public static String UNIT_FLOZ = "fl:oz";
	
	/**是否是身高*/
	public static boolean isWeight = false;
	/**最大分组*/
	public static int MAX_GROUP = 10;

	/**称回复错误代码*/
	public static String ERROR_CODE = "fd31000000000031";
	public static String ERROR_CODE_TEST = "fd33000000000033";
	public static String ERROR_CODE_GETDATE = "fd33000000000033";
	/**关机指令*/
	public static String SCALE_ORDER_SHUTDOWN = "fd35000000000035";
	
	/**Intent引用*/
	public static Intent serveIntent;
	/**SharedPreferences引用*/
	public static SharedPreferencesUtil su = null;
	
	/**秤类型变化*/
	public static final int scaleChangeMessage=101;
	
	/**秤蓝牙名称*/
	public static final String scaleName="Electronic Scale";
	
	/**接收到秤数据的时间*/
	public static long receiveDataTime= 0;
	
	/**是否提示已经更换设备*/
	public static boolean isTipChangeScale=false;
	
	/**秤检测次数*/
	public static int checkScaleTimes=0;
	
	/**初始化目录*/
	public static void initDir() {
		File dirPhoto = new File(SDCARD_ROOT_PATH + APPLICATION_PATH + IMAGE_CACHE_PATH);
		if (!dirPhoto.exists()) {
			dirPhoto.mkdirs();
		}
	}
}
