package com.lefu.es.system;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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

import com.facebook.drawee.view.SimpleDraweeView;
import com.lefu.es.cache.CacheHelper;
import com.lefu.es.constant.UtilConstants;
import com.lefu.es.constant.imageUtil;
import com.lefu.es.entity.Records;
import com.lefu.es.service.ExitApplication;
import com.lefu.es.service.RecordService;
import com.lefu.es.service.UserService;
import com.lefu.es.util.FileUtils;
import com.lefu.es.util.MoveView;
import com.lefu.es.util.SharedPreferencesUtil;
import com.lefu.es.util.StringUtils;
import com.lefu.es.util.UtilTooth;
import com.lefu.iwellness.newes.cn.system.R;

/**
 * 设置界面
 * @author Leon
 * 2015-11-19
 */
public class SettingActivity extends AppCompatActivity {
	private RelativeLayout info_layout;
	private RelativeLayout scale_layout;
	private RelativeLayout save_layout;
	private RelativeLayout about_layout;
	private RelativeLayout langue_layout;
	private RelativeLayout linke_contract;
	private RelativeLayout infomation_ly;
	private ImageView home_img_btn;
	private SimpleDraweeView head_img;
	private TextView name_tv;
	private Button bodyfat_btn, bathroom_btn,babyScale_btn,kitchenScale_btn,chinesetw_btn,english_btn;
	private AlertDialog scaleAlertDialog;
	private AlertDialog savesAlertDialog;
	private AlertDialog lanageAlertDialog;
	private Button txt_btn, close_btn;

	public static SettingActivity detailActivty = null;

	private FileUtils fileutil = null;
	
	private UserService uservice;
	private RecordService recordService;
	public static Intent creatIntent(Context context){
		Intent intent = new Intent(context,SettingActivity.class);
		return intent;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		uservice = new UserService(this);
		recordService = new RecordService(this);

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
		infomation_ly = (RelativeLayout) findViewById(R.id.infomation_ly);
		langue_layout.setOnClickListener(itemOnClickListener);
		info_layout.setOnClickListener(itemOnClickListener);
		scale_layout.setOnClickListener(itemOnClickListener);
		save_layout.setOnClickListener(itemOnClickListener);
		about_layout.setOnClickListener(itemOnClickListener);
		home_img_btn.setOnClickListener(imgOnClickListener);
		infomation_ly.setOnClickListener(imgOnClickListener);
		linke_contract.setOnClickListener(imgOnClickListener);
		head_img = (SimpleDraweeView) findViewById(R.id.reviseHead);
		name_tv = (TextView) findViewById(R.id.username_tv);

	}

	@Override
	protected void onResume() {
		super.onResume();
		initUIinfo();
	}

	private void initUIinfo(){
		/*设置用户名和头像*/
		if (null != UtilConstants.CURRENT_USER) {
			this.name_tv.setText(UtilConstants.CURRENT_USER.getUserName());
			if (null != UtilConstants.CURRENT_USER.getPer_photo() && !"".equals(UtilConstants.CURRENT_USER.getPer_photo()) && !UtilConstants.CURRENT_USER.getPer_photo().equals("null")) {
				head_img.setImageURI(Uri.fromFile(new File(UtilConstants.CURRENT_USER.getPer_photo())));
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
		View customLayout = layoutInflater.inflate(R.layout.scale_chioce, (ViewGroup) findViewById(R.id.selectScaleDialog));

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
				case R.id.infomation_ly:
					/* 跳转info界面 */
					Intent intent0 = new Intent();
					intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent0.setClass(SettingActivity.this, InfoActivity.class);
					SettingActivity.this.startActivity(intent0);
					break;
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
					try {
						CacheHelper.recordListDesc = recordService.getAllDatasByScaleAndIDDesc(UtilConstants.CURRENT_SCALE, UtilConstants.CURRENT_USER.getId(), UtilConstants.CURRENT_USER.getBheigth());
						if (null != CacheHelper.recordListDesc && 0 != CacheHelper.recordListDesc.size()) {
							try {
								fileutil.createSDFile("myrecords.txt");
								StringBuffer str = new StringBuffer();
								if(null!=UtilConstants.CURRENT_USER){
									str.append(UtilConstants.CURRENT_USER.getUserName());
									str.append("\n");
								}
								if(UtilConstants.CURRENT_SCALE.equals(UtilConstants.KITCHEN_SCALE)){
									for (Records lastRecod : CacheHelper.recordListDesc) {
										str.append("\n");
										str.append(getString(R.string.export_time) + StringUtils.getDateShareString(lastRecod.getRecordTime(), 6));
										str.append("\n");

										str.append(getString(R.string.export_foodname) + lastRecod.getRphoto());
										str.append("\n");

										str.append(getString(R.string.kitchen_water) +UtilTooth.keep2Point(lastRecod.getRmuscle())+"g\n");
										//						str.append(getString(R.string.kitchen_energ) + UtilTooth.keep2Point(lastRecod.getRbodywater())+"g\n");
										str.append(getString(R.string.kitchen_energ) + UtilTooth.keep2Point(lastRecod.getRbodywater())+"kcal\n");
										str.append(getString(R.string.kitchen_protein) + UtilTooth.keep2Point(lastRecod.getRbodyfat())+"g\n");
										str.append(getString(R.string.kitchen_lipid) + UtilTooth.keep2Point(lastRecod.getRbone())+"g\n");
										str.append(getString(R.string.kitchen_vitaminC) + UtilTooth.keep2Point(lastRecod.getRbmr())+"mg\n");
										str.append(getString(R.string.kitchen_fiber) + UtilTooth.keep2Point(lastRecod.getRvisceralfat()) + " g\n");
										str.append(getString(R.string.kitchen_cholesterol) + UtilTooth.keep2Point(lastRecod.getRbmi())+"mg\n");
										str.append(getString(R.string.kitchen_calcium) + UtilTooth.keep2Point(lastRecod.getBodyAge())+"mg\n");
									}

								}else{
									String sex = "1";
									if(null!=UtilConstants.CURRENT_USER)sex = UtilConstants.CURRENT_USER.getSex();
									if(TextUtils.isEmpty(sex) || "null".equalsIgnoreCase(sex))sex = "1";
									int gender = Integer.parseInt(sex);
									for (Records lastRecod : CacheHelper.recordListDesc) {
										str.append(getString(R.string.export_time) + StringUtils.getDateShareString(lastRecod.getRecordTime(), 6));
										str.append("\n");
										str.append(getString(R.string.export_weight) + UtilTooth.keep1Point(lastRecod.getRweight()) + "kg");
										if(null!=UtilConstants.CURRENT_USER)str.append("   "+MoveView.weightString(gender,UtilConstants.CURRENT_USER.getBheigth(),lastRecod.getRweight()));
										str.append("\n");
										if(UtilConstants.CURRENT_SCALE.equals(UtilConstants.BATHROOM_SCALE) || UtilConstants.CURRENT_SCALE.equals(UtilConstants.BABY_SCALE)){

										}else{
											str.append(getString(R.string.export_body_Water) + lastRecod.getRbodywater() + "%");
											str.append("   "+MoveView.moistureString(gender,lastRecod.getRbodywater()));
											str.append("\n");

											str.append(getString(R.string.export_body_Fat) + lastRecod.getRbodyfat() + "%");
											if(null!=UtilConstants.CURRENT_USER)str.append("   "+MoveView.bftString(gender,UtilConstants.CURRENT_USER.getAgeYear(),lastRecod.getRbodyfat()));
											str.append("\n");

											str.append(getString(R.string.export_bone) + lastRecod.getRbone() + "kg");
											str.append("   "+MoveView.boneString(lastRecod.getRbone()));
											str.append("\n");

											str.append(getString(R.string.export_visceral_fat) + lastRecod.getRvisceralfat());
											str.append("   "+MoveView.visceralFatString(lastRecod.getRvisceralfat()));
											str.append("\n");

											str.append(getString(R.string.export_BMR) + lastRecod.getRbmr() + " kcal");
											if(null!=UtilConstants.CURRENT_USER)str.append("   "+MoveView.bmrString(gender,UtilConstants.CURRENT_USER.getAgeYear(),lastRecod.getRweight(),lastRecod.getRbmr()));
											str.append("\n");

											str.append(getString(R.string.export_muscle_mass) + lastRecod.getRmuscle() + "kg");
											if(null!=UtilConstants.CURRENT_USER)str.append("   "+MoveView.muscleString(gender,UtilConstants.CURRENT_USER.getBheigth(),lastRecod.getRmuscle()));
											str.append("\n");
										}
										if(null!=UtilConstants.CURRENT_USER){
											float bmi = UtilTooth.countBMI2(lastRecod.getRweight(), (UtilConstants.CURRENT_USER.getBheigth() / 100));
											bmi = UtilTooth.myround(bmi);
											str.append(getString(R.string.export_BMI) + bmi );
											str.append("   "+MoveView.bmiString(bmi));
											str.append("\n");
										}
									}
								}

//							String str = "\n";
//							for (Records curRecord : CacheHelper.recordListDesc) {
//								str += curRecord.getRecordTime()+","+curRecord.getRphoto() + " Weight:" + curRecord.getRweight() + "g,Carbohydrate:" + curRecord.getRbodywater() + "kcal,Energ:"
//							+ curRecord.getRbodyfat() + "g,Protein:"+ curRecord.getRbone() + "g,Fiber:" + curRecord.getRvisceralfat()  + "g,Lipid:" + curRecord.getRmuscle() + "g,Cholesterol:"
//										+ curRecord.getRbmi() + "g，Calcium:" + curRecord.getBodyAge() + "mg,Vitamin C"+ curRecord.getRbmr() + "mg \n";
//							}
								fileutil.writeSDFile(str.toString(), "myrecords.txt");
								Toast.makeText(SettingActivity.this, getString(R.string.setting_export_txt_succ), Toast.LENGTH_SHORT).show();
							} catch (IOException e) {
								Toast.makeText(SettingActivity.this, getString(R.string.setting_export_createFile_fail), Toast.LENGTH_SHORT).show();
							}
						} else {
							Toast.makeText(SettingActivity.this, getString(R.string.setting_export_noRecord), Toast.LENGTH_SHORT).show();
						}
						savesAlertDialog.dismiss();
					} catch (Exception e) {
						e.printStackTrace();
					}
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
