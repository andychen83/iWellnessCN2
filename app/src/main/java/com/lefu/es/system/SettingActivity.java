package com.lefu.es.system;

import java.io.IOException;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lefu.es.cache.CacheHelper;
import com.lefu.es.constant.UtilConstants;
import com.lefu.es.constant.imageUtil;
import com.lefu.es.entity.Records;
import com.lefu.es.service.ExitApplication;
import com.lefu.es.service.UserService;
import com.lefu.es.util.FileUtils;
import com.lefu.es.util.SharedPreferencesUtil;
import com.lefu.iwellness.newes.cn.system.R;

/**
 * 设置界面
 * @author Leon
 * 2015-11-19
 */
public class SettingActivity extends Activity {
	private RelativeLayout info_layout;
	private RelativeLayout scale_layout;
	private RelativeLayout save_layout;
	private RelativeLayout about_layout;
	private RelativeLayout langue_layout;
	private RelativeLayout linke_contract;
	private ImageView home_img_btn;
	private ImageView head_img;
	private TextView name_tv;
	private Button bodyfat_btn, bathroom_btn,babyScale_btn,kitchenScale_btn,chinesetw_btn,english_btn;
	private AlertDialog scaleAlertDialog;
	private AlertDialog savesAlertDialog;
	private AlertDialog lanageAlertDialog;
	private Button txt_btn, close_btn;

	public static SettingActivity detailActivty = null;

	private FileUtils fileutil = null;
	
	private UserService uservice;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		uservice = new UserService(this);
		
		setContentView(R.layout.activity_user_detail_info);
		detailActivty = this;
		/*初始化视图*/
		initview();
		
		ExitApplication.getInstance().addActivity(this);
	}

	/**初始化视图*/
	private void initview() {
		fileutil = new FileUtils(this);
		info_layout = (RelativeLayout) findViewById(R.id.info_layout);
		scale_layout = (RelativeLayout) findViewById(R.id.scale_layout);
		save_layout = (RelativeLayout) findViewById(R.id.save_layout);
		about_layout = (RelativeLayout) findViewById(R.id.about_layout);
		langue_layout = (RelativeLayout) findViewById(R.id.langue_layout);
		home_img_btn = (ImageView) findViewById(R.id.home_img_btn);
		linke_contract = (RelativeLayout) findViewById(R.id.linke_contract);
		langue_layout.setOnClickListener(itemOnClickListener);
		info_layout.setOnClickListener(itemOnClickListener);
		scale_layout.setOnClickListener(itemOnClickListener);
		save_layout.setOnClickListener(itemOnClickListener);
		about_layout.setOnClickListener(itemOnClickListener);
		home_img_btn.setOnClickListener(imgOnClickListener);
		linke_contract.setOnClickListener(imgOnClickListener);
		head_img = (ImageView) findViewById(R.id.reviseHead);
		name_tv = (TextView) findViewById(R.id.username_tv);
		/*设置用户名和头像*/
		if (null != UtilConstants.CURRENT_USER) {
			this.name_tv.setText(UtilConstants.CURRENT_USER.getUserName());
			if (null != UtilConstants.CURRENT_USER.getPer_photo() && !"".equals(UtilConstants.CURRENT_USER.getPer_photo()) && !UtilConstants.CURRENT_USER.getPer_photo().equals("null")) {
				Bitmap bitmap = imageUtil.getBitmapfromPath(UtilConstants.CURRENT_USER.getPer_photo());
				head_img.setImageBitmap(bitmap);
			}
		}
	}

	/**点击监听*/
	OnClickListener itemOnClickListener = new OnClickListener() {
		public void onClick(View v) {
			if (v == info_layout) {
				Intent intent = new Intent();
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setClass(SettingActivity.this, UserEditActivity.class);
				startActivity(intent);
			}else if(v == scale_layout){
				showScaleDialog();
			} else if(v == langue_layout){
				showLangueDialog();
			}else if (v == save_layout) {
				showSaveasDialog();
			} else if (v == about_layout) {
				Intent intent = new Intent();
				intent.setClass(SettingActivity.this, HelpActivity.class);
				intent.putExtra("IsHelp", true);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
			}else if(v==linke_contract){
				String subject = getResources().getString(R.string.email_subject)+"("+getResources().getString(R.string.app_name)+")";
				//String myCc = "cc";
				String mybody = " ";

				Uri uri = Uri.parse("mailto:info@lefu.cc");
				//String[] email = {"3802**92@qq.com"};
				Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
				//intent.putExtra(Intent.EXTRA_CC, email); // 抄送人
				intent.putExtra(Intent.EXTRA_SUBJECT, subject); // 主题
				intent.putExtra(Intent.EXTRA_TEXT, mybody); // 正文
				startActivity(Intent.createChooser(intent, subject));
			}
		}
	};
	
	/**显示秤类型对话框 */
	private void showScaleDialog() {
		// 初始化自定义布局参数
		LayoutInflater layoutInflater = getLayoutInflater();
		// 为了能在下面的OnClickListener中获取布局上组件的数据，必须定义为final类型.
		View customLayout = layoutInflater.inflate(R.layout.scale_chioce, (ViewGroup) findViewById(R.id.customDialog));

		bodyfat_btn = (Button) customLayout.findViewById(R.id.bodyfat_button);
		bathroom_btn = (Button) customLayout.findViewById(R.id.bathroom_button);
		babyScale_btn = (Button) customLayout.findViewById(R.id.babyScale_button);
		kitchenScale_btn = (Button) customLayout.findViewById(R.id.kitchenScale_button);
		bodyfat_btn.setOnClickListener(imgOnClickListener);
		bathroom_btn.setOnClickListener(imgOnClickListener);
		babyScale_btn.setOnClickListener(imgOnClickListener);
		kitchenScale_btn.setOnClickListener(imgOnClickListener);
		
		scaleAlertDialog = new AlertDialog.Builder(this).setView(customLayout).show();

		Window window = scaleAlertDialog.getWindow();
		window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
		window.setWindowAnimations(R.style.mystyle); // 添加动画
	}
	
	
	/**显示语言选择对话框 */
	private void showLangueDialog() {
		// 初始化自定义布局参数
		LayoutInflater layoutInflater = getLayoutInflater();
		// 为了能在下面的OnClickListener中获取布局上组件的数据，必须定义为final类型.
		View customLayout = layoutInflater.inflate(R.layout.language_chioce, (ViewGroup) findViewById(R.id.customDialog));

		chinesetw_btn = (Button) customLayout.findViewById(R.id.chinesetw_button);
		english_btn = (Button) customLayout.findViewById(R.id.english_button);
		
		chinesetw_btn.setOnClickListener(imgOnClickListener);
		english_btn.setOnClickListener(imgOnClickListener);

		lanageAlertDialog = new AlertDialog.Builder(this).setView(customLayout).show();

		Window window = lanageAlertDialog.getWindow();
		window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
		window.setWindowAnimations(R.style.mystyle); // 添加动画
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			back2Scale();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_HOME) {
		}
		return super.onKeyDown(keyCode, event);
	}

	OnClickListener imgOnClickListener = new OnClickListener() {
		@SuppressWarnings("deprecation")
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.home_img_btn :
					home_img_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_id));
					/*返回跳转*/
					back2Scale();
					break;

				case R.id.txt_button :
					if (!fileutil.hasSD()) {
						Toast.makeText(SettingActivity.this, getString(R.string.setting_noSDCard), Toast.LENGTH_SHORT).show();
						savesAlertDialog.dismiss();
						return;
					}
					if (null != CacheHelper.recordListDesc && 0 != CacheHelper.recordListDesc.size()) {
						try {
							fileutil.createSDFile("myrecords.txt");
							String str = "\n";
							for (Records curRecord : CacheHelper.recordListDesc) {
								str += curRecord.getRecordTime()+","+curRecord.getRphoto() + " Weight:" + curRecord.getRweight() + "g,Carbohydrate:" + curRecord.getRbodywater() + "kcal,Energ:" 
							+ curRecord.getRbodyfat() + "g,Protein:"+ curRecord.getRbone() + "g,Fiber:" + curRecord.getRvisceralfat()  + "g,Lipid:" + curRecord.getRmuscle() + "g,Cholesterol:"
										+ curRecord.getRbmi() + "g，Calcium:" + curRecord.getBodyAge() + "mg,Vitamin C"+ curRecord.getRbmr() + "mg \n";
							}
							fileutil.writeSDFile(str, "myrecords.txt");
							Toast.makeText(SettingActivity.this, getString(R.string.setting_export_txt_succ), Toast.LENGTH_SHORT).show();
						} catch (IOException e) {
							Toast.makeText(SettingActivity.this, getString(R.string.setting_export_createFile_fail), Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(SettingActivity.this, getString(R.string.setting_export_noRecord), Toast.LENGTH_SHORT).show();
					}
					savesAlertDialog.dismiss();
					break;
				case R.id.close_button :
					savesAlertDialog.dismiss();
					break;
				case R.id.bodyfat_button :
					UtilConstants.CURRENT_SCALE = UtilConstants.BODY_SCALE;
					UtilConstants.CURRENT_USER.setScaleType(UtilConstants.CURRENT_SCALE);
					/*更新秤类型*/
					try {
						uservice.update(UtilConstants.CURRENT_USER);
					} catch (Exception e) {
						e.printStackTrace();
					}
					scaleAlertDialog.dismiss();
					break;
				case R.id.bathroom_button :
					UtilConstants.CURRENT_SCALE = UtilConstants.BATHROOM_SCALE;
					UtilConstants.CURRENT_USER.setScaleType(UtilConstants.CURRENT_SCALE);
					/*更新秤类型*/
					try {
						uservice.update(UtilConstants.CURRENT_USER);
					} catch (Exception e) {
						e.printStackTrace();
					}
					scaleAlertDialog.dismiss();
					break;
				case R.id.babyScale_button :
					UtilConstants.CURRENT_SCALE = UtilConstants.BABY_SCALE;
					UtilConstants.CURRENT_USER.setScaleType(UtilConstants.CURRENT_SCALE);
					/*更新秤类型*/
					try {
						uservice.update(UtilConstants.CURRENT_USER);
					} catch (Exception e) {
						e.printStackTrace();
					}
					scaleAlertDialog.dismiss();
					break;
				case R.id.kitchenScale_button:
					UtilConstants.CURRENT_SCALE = UtilConstants.KITCHEN_SCALE;
					UtilConstants.CURRENT_USER.setScaleType(UtilConstants.CURRENT_SCALE);
					/*更新秤类型*/
					try {
						uservice.update(UtilConstants.CURRENT_USER);
					} catch (Exception e) {
						e.printStackTrace();
					}
					scaleAlertDialog.dismiss();
					break;
				case R.id.chinesetw_button:
					if(null==UtilConstants.su){
						UtilConstants.su = new SharedPreferencesUtil(SettingActivity.this);
					}
					UtilConstants.su.editSharedPreferences("lefuconfig", "lan", 2);
					UtilConstants.SELECT_LANGUAGE = 2;
					Locale.setDefault(Locale.TRADITIONAL_CHINESE); 
					Configuration config = getBaseContext().getResources().getConfiguration(); 
			        config.locale = Locale.TRADITIONAL_CHINESE; 
			        getBaseContext().getResources().updateConfiguration(config
			        		, getBaseContext().getResources().getDisplayMetrics());
			       lanageAlertDialog.dismiss();
			       back2Scale();
					break;
				case R.id.english_button:
					if(null==UtilConstants.su){
						UtilConstants.su = new SharedPreferencesUtil(SettingActivity.this);
					}
					UtilConstants.su.editSharedPreferences("lefuconfig", "lan", 1);
					UtilConstants.SELECT_LANGUAGE = 1;
					Locale.setDefault(Locale.ENGLISH); 
					config = getBaseContext().getResources().getConfiguration(); 
			        config.locale = Locale.ENGLISH; 
			        getBaseContext().getResources().updateConfiguration(config
			        		, getBaseContext().getResources().getDisplayMetrics());
			        lanageAlertDialog.dismiss();
			        back2Scale();
					break;
				case R.id.linke_contract: 
					String subject = getResources().getString(R.string.email_subject)+"("+ getResources().getString(R.string.app_name)+")";
			        //String myCc = "cc";  
			        String mybody = " ";  
			        
			        Uri uri = Uri.parse("mailto:info@lefu.cc"); 
					//String[] email = {"3802**92@qq.com"};
					Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
					//intent.putExtra(Intent.EXTRA_CC, email); // 抄送人
					intent.putExtra(Intent.EXTRA_SUBJECT, subject); // 主题
					intent.putExtra(Intent.EXTRA_TEXT, mybody); // 正文
					startActivity(Intent.createChooser(intent, subject));
					break;
			}
		}
	};
	
	/**返回测量主界面*/
	private void back2Scale(){
		ExitApplication.getInstance().exit(SettingActivity.this);
		Intent intent1 = new Intent(SettingActivity.this, LoadingActivity.class);
		SettingActivity.this.startActivity(intent1);
	}
	
	/** 显示保存Dialog */
	private void showSaveasDialog() {
		// 初始化自定义布局参数
		LayoutInflater layoutInflater = getLayoutInflater();
		// 为了能在下面的OnClickListener中获取布局上组件的数据，必须定义为final类型.
		View customLayout = layoutInflater.inflate(R.layout.savesa, (ViewGroup) findViewById(R.id.customDialog));

		txt_btn = (Button) customLayout.findViewById(R.id.txt_button);
		close_btn = (Button) customLayout.findViewById(R.id.close_button);

		txt_btn.setOnClickListener(imgOnClickListener);
		close_btn.setOnClickListener(imgOnClickListener);

		savesAlertDialog = new AlertDialog.Builder(this).setView(customLayout).show();

		Window window = savesAlertDialog.getWindow();
		window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
		window.setWindowAnimations(R.style.mystyle); // 添加动画
	}

}
