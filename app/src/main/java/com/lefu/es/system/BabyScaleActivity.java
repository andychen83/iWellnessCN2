package com.lefu.es.system;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lefu.es.adapter.MyPageAdapter;
import com.lefu.es.ble.BlueSingleton;
import com.lefu.es.ble.BluetoothLeService;
import com.lefu.es.cache.CacheHelper;
import com.lefu.es.constant.AppData;
import com.lefu.es.constant.BLEConstant;
import com.lefu.es.constant.BluetoolUtil;
import com.lefu.es.constant.UtilConstants;
import com.lefu.es.constant.imageUtil;
import com.lefu.es.db.RecordDao;
import com.lefu.es.entity.Records;
import com.lefu.es.event.NoRecordsEvent;
import com.lefu.es.service.ExitApplication;
import com.lefu.es.service.RecordService;
import com.lefu.es.service.TimeService;
import com.lefu.es.service.UserService;
import com.lefu.es.util.DisplayUtil;
import com.lefu.es.util.MyUtil;
import com.lefu.es.util.SharedPreferencesUtil;
import com.lefu.es.util.StringUtils;
import com.lefu.es.util.UtilTooth;
import com.lefu.es.view.GuideView;
import com.lefu.es.view.MyTextView;
import com.lefu.es.view.MyTextView2;
import com.lefu.es.view.guideview.HighLightGuideView;
import com.lefu.es.view.guideview.HighLightGuideView.OnDismissListener;
import com.lefu.iwellness.newes.cn.system.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 婴儿称
 * @author lfl
 */
@SuppressLint("HandlerLeak")
public class BabyScaleActivity extends Activity implements Runnable {
	private static final String TAG = "BathScaleActivity";
	private static final boolean D = true;
	private RecordService recordService;
	private BluetoothAdapter mBtAdapter;
	private List<Records> rList;
	private ArrayList<View> views = new ArrayList<View>();
	/** 跳转过来ItemID **/
	private int ItemID;
	private ViewPager pager;
	private Records curRecord;
	private MyPageAdapter adapter = null;
	private int selectedPosition = -1;
	private TextView norecord_tv;
	private TextView username_tv;
	private MyTextView2 compare_tv;
	private UserService uservice;
	private ImageView headImg;

	private Button infoImg;
	private Button setingImg;
	private Button deletdImg;

	private ImageView leftImg;
	private ImageView rightImg;
	private ImageView backgroundImage;

	private ImageView intentImg;
	public final static String PAR_KEY = "com.tutor.objecttran.par";

	private TextView tvBmi = null;
	private TextView time_tv;
	private TextView scale_connect_state;

	RelativeLayout rlGuide = null;

	private MyTextView2 targetv = null;
	
	private int sendCodeCount = 0;
	
	private LayoutInflater inflater;
	/*是否是当前Activity*/
	private boolean isCurrentActivoty=true;

	private void showTipMask() {
//		HighLightGuideView.builder(this).setText(getString(R.string.click_see_data))
//		.addNoHighLightGuidView(R.drawable.ic_ok).addHighLightGuidView(pager, 0).addTargetViewRadiusDiff(-50)
//		.setTouchOutsideDismiss(false).setOnDismissListener(new OnDismissListener() {
//			@Override
//			public void onDismiss() {
//				if (null == UtilConstants.su) {
//					UtilConstants.su = new SharedPreferencesUtil(BabyScaleActivity.this);
//				}
//				UtilConstants.su.editSharedPreferences("lefuconfig", "first_install_body", "1");
//				UtilConstants.FIRST_INSTALL_BODY = "1";
//
//			}
//
//		}).setHighLightStyle(HighLightGuideView.VIEWSTYLE_OVAL).show();
		
		HighLightGuideView.builder(this).setText(getString(R.string.click_see_data))
		.addNoHighLightGuidView(R.drawable.ic_ok).addHighLightGuidView(pager, 0,0.5f,HighLightGuideView.VIEWSTYLE_CIRCLE)
		.setTouchOutsideDismiss(false).setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				if (null == UtilConstants.su) {
					UtilConstants.su = new SharedPreferencesUtil(BabyScaleActivity.this);
				}
				UtilConstants.su.editSharedPreferences("lefuconfig", "first_install_baby_scale", "1");
				UtilConstants.FIRST_INSTALL_BABY_SCALE = "1";

			}

		}).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 99) {
			ItemID = data.getIntExtra("ItemID", 0);
			handler.sendEmptyMessage(0);
		} else if (requestCode == REQUEST_ENABLE_BT_CLICK && resultCode == Activity.RESULT_CANCELED) {
			Toast.makeText(this, getString(R.string.enable_bluetooth), Toast.LENGTH_LONG).show();
			return;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_baby_scale);
		EventBus.getDefault().register(this);
		recordService = new RecordService(this);
		inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ItemID = getIntent().getIntExtra("ItemID", 0);
		initView();
		
		/*初始化用户选择蓝牙类型*/
		String blueTooth_type=null;
		if(UtilConstants.su!=null){
			blueTooth_type=(String) UtilConstants.su.readbackUp("lefuconfig", "bluetooth_type"+UtilConstants.CURRENT_USER.getId(), String.class);
		}
		System.out.println("蓝牙类型:"+blueTooth_type);
		if(blueTooth_type!=null&&"BT".equals(blueTooth_type)){
			BluetoolUtil.bleflag = false;
		}else{
			BluetoolUtil.bleflag = true;
		}

		// 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
		if (BluetoolUtil.bleflag) {
			// 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
			try {
				final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
				mBluetoothAdapter = bluetoothManager.getAdapter();
			} catch (NoClassDefFoundError e) {
				e.printStackTrace();
			}
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT_CLICK);
			}
		} else {
			if (null == BluetoolUtil.mBluetoothAdapter)
				BluetoolUtil.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			if (null != BluetoolUtil.mBluetoothAdapter && !BluetoolUtil.mBluetoothAdapter.isEnabled()) {
				Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableIntent, BluetoolUtil.REQUEST_ENABLE_BT);
			}
			if (null == UtilConstants.serveIntent) {
				UtilConstants.serveIntent = new Intent(this, TimeService.class);
				this.startService(UtilConstants.serveIntent);
			}
		}

		ExitApplication.getInstance().addActivity(this);
		
	}

	/**初始化视图*/
	private void initView() {
		uservice = new UserService(this);

		norecord_tv = (TextView) this.findViewById(R.id.norecord_textView);
		username_tv = (TextView) this.findViewById(R.id.username_tv);
		compare_tv = (MyTextView2) this.findViewById(R.id.compare_tv);
		targetv = (MyTextView2) this.findViewById(R.id.textView2);
		//targetv.setTexts("60.0", null);
		if (null != UtilConstants.su){
			UtilConstants.CHOICE_KG = (String) UtilConstants.su.readbackUp("lefuconfig", "unit", UtilConstants.UNIT_KG);
		}
		if (null != UtilConstants.CURRENT_USER) {
			compare_tv.setTexts("0.0" + UtilConstants.CURRENT_USER.getDanwei(), null);
		}
		leftImg = (ImageView) this.findViewById(R.id.imageView3);
		rightImg = (ImageView) this.findViewById(R.id.imageView4);
		rlGuide = (RelativeLayout) this.findViewById(R.id.rl_guide);
		tvBmi = (TextView) this.findViewById(R.id.tv_guide_value);
		time_tv = (TextView) this.findViewById(R.id.textView19);
		scale_connect_state = (TextView) this.findViewById(R.id.scale_connect_state);
		
		infoImg = (Button) this.findViewById(R.id.info_img_btn);
		setingImg = (Button) this.findViewById(R.id.seting_img_btn);
		deletdImg = (Button) this.findViewById(R.id.delete_img_btn);
		headImg = (ImageView) this.findViewById(R.id.reviseHead);
		intentImg = (ImageView) this.findViewById(R.id.imageView1);
		backgroundImage = (ImageView) this.findViewById(R.id.imageView2);
		infoImg.setOnClickListener((android.view.View.OnClickListener) menuBtnOnClickListener);
		deletdImg.setOnClickListener((android.view.View.OnClickListener) menuBtnOnClickListener);
		setingImg.setOnClickListener((android.view.View.OnClickListener) menuBtnOnClickListener);
		headImg.setOnClickListener((android.view.View.OnClickListener) btnOnClickListener);
		intentImg.setOnClickListener((android.view.View.OnClickListener) btnOnClickListener);
		pager = (ViewPager) this.findViewById(R.id.weight_contains);

		/*测量记录分页*/
		pager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				if (null != views && views.size() > 0) {
					if (arg0 == views.size() - 1 || arg0 == 0) {
						if (arg0 == 0) {
							pager.setCurrentItem(arg0 + 1);
						} else {
							pager.setCurrentItem(arg0 - 1);
						}
					} else {
						if (arg0 > 0)
							selectedPosition = arg0 - 1;
						else
							selectedPosition = 0;
						curRecord = CacheHelper.recordListDesc.get(selectedPosition);
						handler.sendEmptyMessage(1);
					}
					if (pager.getCurrentItem() < 2) {
						leftImg.setImageDrawable(getResources().getDrawable(R.drawable.left_to));
					} else {
						leftImg.setImageDrawable(getResources().getDrawable(R.drawable.arrowleft_blue));
					}
					if (pager.getCurrentItem() >= (views.size() - 2)) {
						rightImg.setImageDrawable(getResources().getDrawable(R.drawable.right_to));
					} else {
						rightImg.setImageDrawable(getResources().getDrawable(R.drawable.arrowright_blue));
					}
				} else {
					leftImg.setImageDrawable(getResources().getDrawable(R.drawable.left_to));
					rightImg.setImageDrawable(getResources().getDrawable(R.drawable.right_to));
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		if (null != UtilConstants.CURRENT_USER) {
			this.username_tv.setText(UtilConstants.CURRENT_USER.getUserName());
			/*设置目标体重*/
			if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_KG)) {
				targetv.setTexts(UtilTooth.toOnePonit(UtilConstants.CURRENT_USER.getTargweight()) + "" + this.getText(R.string.kg_danwei), null);
			} else if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_LB) || UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_FATLB) || UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_ST)) {
				targetv.setTexts(UtilTooth.onePoint(UtilTooth.kgToLB_target(UtilConstants.CURRENT_USER.getTargweight())) + this.getText(R.string.lb_danwei), null);
			}else{
				targetv.setTexts(UtilTooth.toOnePonit(UtilConstants.CURRENT_USER.getTargweight()) + "" + this.getText(R.string.kg_danwei), null);
			}
			/*设置头像*/
			if (null != UtilConstants.CURRENT_USER.getPer_photo() && !"".equals(UtilConstants.CURRENT_USER.getPer_photo()) && !UtilConstants.CURRENT_USER.getPer_photo().equals("null")) {
				Bitmap bitmap = imageUtil.getBitmapfromPath(UtilConstants.CURRENT_USER.getPer_photo());
				headImg.setImageBitmap(bitmap);
			}
		}
		
		CheckBox cb = (CheckBox)this.findViewById(R.id.cb_scan_device);
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.i(TAG, "click to start scan");
				if (mBluetoothAdapter.isEnabled() && singleton != null) {
					Log.i(TAG, "start scan");
                    singleton.scanLeDevice(false, BabyScaleActivity.this, mServiceConnection);
					
					if(mBluetoothLeService != null){
						mBluetoothLeService.disconnect();
					}
					
					singleton.scanLeDevice(true, BabyScaleActivity.this, mServiceConnection);
				}
			}
		});
	}

	/**底部菜单监听*/
	android.view.View.OnClickListener menuBtnOnClickListener = new android.view.View.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.info_img_btn :
					/*Info界面*/
					Intent intent0 = new Intent();
					intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent0.setClass(BabyScaleActivity.this, InfoActivity.class);
					BabyScaleActivity.this.startActivity(intent0);
					break;
				case R.id.seting_img_btn :
					/*设置界面*/
					Intent intent = new Intent();
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setClass(BabyScaleActivity.this, SettingActivity.class);
					BabyScaleActivity.this.startActivity(intent);
					break;
				case R.id.delete_img_btn :
					/*删除弹窗*/
					dialog(getString(R.string.deleted_waring), v.getId());
					break;
			}
		}
	};

	/**初始化数据*/
	private void setViews() {
		if (null != UtilConstants.CURRENT_USER) {
			this.username_tv.setText(UtilConstants.CURRENT_USER.getUserName());
			/*设置目标体重*/
			if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_KG)) {
				targetv.setTexts(UtilTooth.toOnePonit(UtilConstants.CURRENT_USER.getTargweight()) + "" + this.getText(R.string.kg_danwei), null);
			} else if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_LB) || UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_FATLB) || UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_ST)) {
				targetv.setTexts(UtilTooth.onePoint(UtilTooth.kgToLB_target(UtilConstants.CURRENT_USER.getTargweight())) + this.getText(R.string.lb_danwei), null);
			}
			/*设置头像*/
			if (null != UtilConstants.CURRENT_USER.getPer_photo() && !"".equals(UtilConstants.CURRENT_USER.getPer_photo()) && !UtilConstants.CURRENT_USER.getPer_photo().equals("null")) {
				Bitmap bitmap = imageUtil.getBitmapfromPath(UtilConstants.CURRENT_USER.getPer_photo());
				headImg.setImageBitmap(bitmap);
			}
		}
		
		if (null != curRecord) {
			Date iDate = UtilTooth.stringToTime(curRecord.getRecordTime());
			if (iDate != null) {
				Locale local = Locale.getDefault();
				time_tv.setText(StringUtils.getDateString(iDate, 5));
			}
			float bmi = UtilTooth.countBMI2(curRecord.getRweight(), (UtilConstants.CURRENT_USER.getBheigth() / 100));

			curRecord.setRbmi(UtilTooth.myround(bmi));
			tvBmi.setText(curRecord.getRbmi() + "");
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			int dp20 = DisplayUtil.dip2px(this, 64);
			int rlWidth = DisplayUtil.getWidth(this) - dp20;
			lp.setMargins((int) (UtilTooth.changeBMIBaby(curRecord.getRbmi(), rlWidth)), 0, 0, 0);
			rlGuide.setLayoutParams(lp);
			if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_KG)) {
				backgroundImage.setBackgroundResource(R.drawable.babyscale_bg);
				if (null == curRecord.getCompareRecord() || "".equals(curRecord.getCompareRecord())) {
					compare_tv.setTexts("0.0", null);
					compare_tv.setTexts("0.0 " + this.getText(R.string.kg_danwei), null);
				} else {
					BigDecimal b = new BigDecimal(Double.parseDouble(curRecord.getCompareRecord()));
					float cr = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (cr > 0) {
						compare_tv.setTexts("↑" + UtilTooth.myroundString3(Math.abs(cr) + "") + this.getText(R.string.kg_danwei), null);
					} else if (cr < 0) {
						compare_tv.setTexts("↓" + UtilTooth.myroundString3(Math.abs(cr) + "") + this.getText(R.string.kg_danwei), null);
					} else {
						compare_tv.setTexts(UtilTooth.myroundString3(curRecord.getCompareRecord() + "") + this.getText(R.string.kg_danwei), null);
					}
				}
			} else if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_LB) || UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_FATLB)) {
				backgroundImage.setBackgroundResource(R.drawable.babyscale_bg_lb);
				if (null == curRecord.getCompareRecord() || "".equals(curRecord.getCompareRecord().trim())) {
					curRecord.setCompareRecord("0");
					compare_tv.setTexts("0.0 " + " " + this.getText(R.string.lb_danwei), null);
				} else {
					float cr = Float.parseFloat(curRecord.getCompareRecord());
					if (cr > 0) {
						compare_tv.setTexts("↑" + UtilTooth.kgToLB(Math.abs(Float.parseFloat(curRecord.getCompareRecord()))) + " " + this.getText(R.string.lb_danwei), null);
					} else if (cr < 0) {
						compare_tv.setTexts("↓" + UtilTooth.kgToLB(Math.abs(Float.parseFloat(curRecord.getCompareRecord()))) + " " + this.getText(R.string.lb_danwei), null);
					} else {
						compare_tv.setTexts("0.0" + " " + this.getText(R.string.lb_danwei), null);
					}
				}
			} else if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_ST)) {
				if (null == curRecord.getCompareRecord() || "".equals(curRecord.getCompareRecord().trim())) {
					curRecord.setCompareRecord("0");
					compare_tv.setTexts("0.0 " + this.getText(R.string.stlb_danwei), null);
				}
				float cr = Float.parseFloat(curRecord.getCompareRecord());
				String wei = UtilTooth.kgToLB_new(Math.abs(Float.parseFloat(curRecord.getCompareRecord())));
				String[] fatTemp = UtilTooth.kgToStLbForScaleFat2(Math.abs(Float.parseFloat(curRecord.getCompareRecord())));
				if (cr > 0) {
					if (curRecord.getScaleType().equals(UtilConstants.BODY_SCALE)) {
						if (fatTemp[1] != null) {
							compare_tv.setTexts("↑" + fatTemp[0], fatTemp[1]);
						} else {
							compare_tv.setTexts("↑" + fatTemp[0] + this.getText(R.string.stlb_danwei), null);
						}
					} else {
						compare_tv.setTexts("↑" + wei + this.getText(R.string.stlb_danwei), null);
					}
				} else if (cr < 0) {
					if (curRecord.getScaleType().equals(UtilConstants.BODY_SCALE)) {
						if (fatTemp[1] != null) {
							compare_tv.setTexts("↓" + fatTemp[0], fatTemp[1]);
						} else {
							compare_tv.setTexts("↓" + fatTemp[0] + this.getText(R.string.stlb_danwei), null);
						}
					} else {
						compare_tv.setTexts("↓" + wei + this.getText(R.string.stlb_danwei), null);
					}
				} else {
					compare_tv.setTexts("0.0 " + this.getText(R.string.stlb_danwei), null);
				}
			}else{
				backgroundImage.setBackgroundResource(R.drawable.babyscale_bg);
				if (null == curRecord.getCompareRecord() || "".equals(curRecord.getCompareRecord())) {
					compare_tv.setTexts("0.0", null);
					compare_tv.setTexts("0.0 " + this.getText(R.string.kg_danwei), null);
				} else {
					BigDecimal b = new BigDecimal(Double.parseDouble(curRecord.getCompareRecord()));
					float cr = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (cr > 0) {
						compare_tv.setTexts("↑" + UtilTooth.myroundString3(Math.abs(cr) + "") + this.getText(R.string.kg_danwei), null);
					} else if (cr < 0) {
						compare_tv.setTexts("↓" + UtilTooth.myroundString3(Math.abs(cr) + "") + this.getText(R.string.kg_danwei), null);
					} else {
						compare_tv.setTexts(UtilTooth.myroundString3(curRecord.getCompareRecord() + "") + this.getText(R.string.kg_danwei), null);
					}
				}
			}
		} else {
			if (null != UtilConstants.CURRENT_USER){
				compare_tv.setTexts("0.0 " + UtilConstants.CURRENT_USER.getDanwei(), null);
			}
			tvBmi.setText("0.0");
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			int dp20 = DisplayUtil.dip2px(this, 64);
			int rlWidth = DisplayUtil.getWidth(this) - dp20;
			lp.setMargins((int) (UtilTooth.changeBMIBaby(0, rlWidth)), 0, 0, 0);
			rlGuide.setLayoutParams(lp);
		}
	}

	/**头部图片点击事件*/
	android.view.View.OnClickListener btnOnClickListener = new android.view.View.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.reviseHead :
					/*跳转到用户列表*/
					Intent intent1 = new Intent();
					intent1.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
					intent1.setClass(BabyScaleActivity.this, UserListActivity.class);
					BabyScaleActivity.this.startActivity(intent1);
					break;
				case R.id.imageView1 :
					/*跳转到链接*/
//					Intent intent2 = new Intent();
//					intent2.setData(Uri.parse(UtilConstants.homeUrl));
//					intent2.setAction(Intent.ACTION_VIEW);
//					BabyScaleActivity.this.startActivity(intent2);
					break;
			}
		}
	};

	/**弹窗*/
	protected void dialog(String title, final int id) {
		AlertDialog.Builder builder = new Builder(BabyScaleActivity.this);
		builder.setMessage(title);
		builder.setNegativeButton(R.string.cancle_btn, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}

		});
		builder.setPositiveButton(R.string.ok_btn, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					switch (id) {
						case R.id.delete_img_btn :
							if (null != curRecord)
								recordService.delete(curRecord);
							CacheHelper.recordListDesc = recordService.getAllDatasByScaleAndIDDesc(UtilConstants.CURRENT_USER.getScaleType(), ItemID, 167f);
							if (null != CacheHelper.recordListDesc && CacheHelper.recordListDesc.size() > 0) {
								curRecord = CacheHelper.recordListDesc.get(0);
							} else {
								curRecord = null;
							}
							handler.sendEmptyMessage(0);
							break;
					}
				} catch (Exception e) {
				}
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	@Override
	protected void onResume() {
		super.onResume();
		/*是否提示更换称弹窗*/
		isCurrentActivoty=true;
		if(!AppData.hasCheckData){
			handler.postDelayed(CheckHasDataRunnable, 30*1000);
		}
		
		/*秤识别中*/
		AppData.isCheckScale=false;
		
		/*是否重新检测称*/
		if(AppData.reCheck){
			/*跳转到秤检测界面*/
			Intent intent1 = new Intent();
			intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent1.setClass(BabyScaleActivity.this, ReCheckActivity.class);
			BabyScaleActivity.this.startActivity(intent1);
			exit();
			ExitApplication.getInstance().exit(this);
		}else{
		new Thread(this).start();

			if (BluetoolUtil.bleflag) {
				// 再次进入界面后判断是否要重新搜索
				singleton = BlueSingleton.getInstance(handler);
				if (mBluetoothAdapter.isEnabled() && !singleton.getmConnected() && !singleton.getmScanning()) {
					singleton.scanLeDevice(true, this, mServiceConnection);
				}
			}
		}
	}

	@Override
	protected void onPause() {
		/*非当前Activity*/
		isCurrentActivoty=false;
		
		super.onPause();
	}

	/**初始化分页*/
	private void Init(List<Records> rList) {
		if (null == views) {
			views = new ArrayList<View>();
		} else {
			views.clear();
		}
		LayoutInflater mLi = LayoutInflater.from(this);
		if (null != rList && rList.size() > 0) {
			rightImg.setVisibility(View.VISIBLE);
			leftImg.setVisibility(View.VISIBLE);
			this.rightImg.setImageDrawable(getResources().getDrawable(R.drawable.arrowright_white));
			norecord_tv.setVisibility(View.INVISIBLE);
			View view0 = mLi.inflate(R.layout.nullwhats, null);
			views.add(view0);
			for (Records record : rList) {
				View vie = creatView(record, null);
				views.add(vie);
			}
			View view00 = mLi.inflate(R.layout.nullwhats, null);
			views.add(view00);

			adapter = new MyPageAdapter(views);
			pager.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			pager.setCurrentItem(1);
		} else {
			leftImg.setImageDrawable(getResources().getDrawable(R.drawable.left_to));
			rightImg.setImageDrawable(getResources().getDrawable(R.drawable.right_to));
			rightImg.setVisibility(View.INVISIBLE);
			leftImg.setVisibility(View.INVISIBLE);
			norecord_tv.setVisibility(View.INVISIBLE);
			View view0 = mLi.inflate(R.layout.nullwhats, null);
			views.add(view0);
			adapter = new MyPageAdapter(views);
			pager.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			pager.setCurrentItem(0);
		}
	}

	/**创建视图*/
	public View creatView(Records record, ViewGroup viewGroup) {
		View convertView = null;
		MyTextView tvDetail_weight_title = null;
		TextView unit_tv = null;
		TextView weight_tv = null;
		View space_view = null;
		if (null == convertView) {
			convertView = inflater.inflate(R.layout.user_weight_pager, null);
			tvDetail_weight_title = (MyTextView) convertView.findViewById(R.id.weight_textView);
			unit_tv = (TextView) convertView.findViewById(R.id.unti_tv);
			weight_tv = (TextView) convertView.findViewById(R.id.weight_tv);
			space_view = (View) convertView.findViewById(R.id.space_view);
			weight_tv.setVisibility(View.GONE);
			space_view.setVisibility(View.GONE);
			convertView.setTag(record);
			convertView.setOnClickListener((android.view.View.OnClickListener) imgOnClickListener);
		}
		if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_ST)) {
			if (UtilConstants.CURRENT_SCALE.equals(UtilConstants.BODY_SCALE)) {
				String[] tempS = UtilTooth.kgToStLbForScaleFat2(record.getRweight());
				tvDetail_weight_title.setTexts(tempS[0], tempS[1]);
				if (null != unit_tv)
					unit_tv.setText(this.getText(R.string.stlb_danwei));
			}else if (UtilConstants.CURRENT_SCALE.equals(UtilConstants.BABY_SCALE)) {
				tvDetail_weight_title.setTexts(UtilTooth.lbozToString(record.getRweight()), null);
				if (null != unit_tv)
					unit_tv.setText("");
			} else {
				tvDetail_weight_title.setTexts(UtilTooth.kgToLB_new(record.getRweight()), null);
				if (null != unit_tv)
					unit_tv.setText(this.getText(R.string.stlb_danwei));
			}
		} else if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_LB)) {
			if (UtilConstants.CURRENT_SCALE.equals(UtilConstants.BABY_SCALE)) {
				tvDetail_weight_title.setTexts(UtilTooth.lbozToString(record.getRweight()), null);
				if (null != unit_tv)
					unit_tv.setText("");
			}else{
				tvDetail_weight_title.setTexts(UtilTooth.kgToLB(record.getRweight()), null);
				if (null != unit_tv)
					unit_tv.setText(this.getText(R.string.lb_danwei));
			}
			
		}else if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_FATLB)) {
			tvDetail_weight_title.setTexts(UtilTooth.lbozToString(record.getRweight()), null);
			if (null != unit_tv)
				unit_tv.setText("");
		}  else {
			tvDetail_weight_title.setTexts(record.getRweight() + "", null);
			if (null != unit_tv)
				unit_tv.setText(this.getText(R.string.kg_danwei));
		}
		return convertView;
	}

	/**分页图片监听*/
	android.view.View.OnClickListener imgOnClickListener = new android.view.View.OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(BabyScaleActivity.this, RecordListActivity.class);
			intent.putExtra("type", UtilConstants.WEIGHT_SINGLE);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivityForResult(intent, 0);
		}
	};
	
	@Override
	public void run() {
		if (ItemID != 0) {
			try {
				UtilConstants.CURRENT_USER = this.uservice.find(ItemID);
				SharedPreferencesUtil su = new SharedPreferencesUtil(this);
				su.editSharedPreferences("lefuconfig", "user", ItemID);
				CacheHelper.recordListDesc = this.recordService.getAllDatasByScaleAndIDDesc(UtilConstants.BABY_SCALE, ItemID, 167f);
				if (null != CacheHelper.recordListDesc && CacheHelper.recordListDesc.size() > 0) {
					curRecord = CacheHelper.recordListDesc.get(0);
				} else {
					curRecord = null;
				}
				handler.sendEmptyMessage(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onMessageEvent(NoRecordsEvent noRecordsEvent) {
		CacheHelper.recordListDesc = null;
		curRecord = null;
		handler.sendEmptyMessage(0);
	};

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case 0 :
					/*设置视图*/
					setViews();
					/*初始化数据*/
					Init(CacheHelper.recordListDesc);
					if (TextUtils.isEmpty(UtilConstants.FIRST_INSTALL_BABY_SCALE) && CacheHelper.recordListDesc.size()>0) {
						showTipMask();
					}
					break;
				case 1 :
					/*设置视图*/
					setViews();
					break;
				case 5 :
					exit();
					ExitApplication.getInstance().exit(BabyScaleActivity.this);
					break;
				case UtilConstants.scaleChangeMessage :
					/*保存秤类型*/
					if(UtilConstants.su==null){
						UtilConstants.su=new SharedPreferencesUtil(LoadingActivity.mainActivty);
					}
					UtilConstants.su.editSharedPreferences("lefuconfig", "scale", UtilConstants.CURRENT_SCALE);
					/*保存用户信息*/
					try {
						uservice.update(UtilConstants.CURRENT_USER);
					} catch (Exception e) {
						e.printStackTrace();
					}
					/*跳转*/
					ExitApplication.getInstance().exit(BabyScaleActivity.this);
					Intent intent = new Intent();
					intent.setClass(BabyScaleActivity.this, LoadingActivity.class);
					BabyScaleActivity.this.startActivity(intent);
					break;
				case 6 :
					/* 保存秤类型 */
					if (UtilConstants.su == null) {
						UtilConstants.su = new SharedPreferencesUtil(LoadingActivity.mainActivty);
					}
					UtilConstants.su.editSharedPreferences("lefuconfig", "scale", UtilConstants.CURRENT_SCALE);
					/* 保存用户信息 */
					try {
						uservice.update(UtilConstants.CURRENT_USER);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
			}
		}

	};

	@Override
	protected void onStart() {
		if (!BluetoolUtil.bleflag && null == UtilConstants.serveIntent) {
			UtilConstants.serveIntent = new Intent(this, TimeService.class);
			this.startService(UtilConstants.serveIntent);
			/*蓝牙BT扫描线程*/
			new Thread(ScanRunnable).start();
			/*连接状态*/
			TimeService.scale_connect_state=scale_connect_state;
		}
		if (BluetoolUtil.bleflag && !receiverReleased) {
			/*注册GATT状态更新广播*/
			registerReceiver(mGattUpdateReceiver, BluetoothLeService.makeGattUpdateIntentFilter());
			receiverReleased = false;
		}
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		exit();
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			handler.sendEmptyMessage(5);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_HOME) {
			exit();
			ExitApplication.getInstance().exit(this);
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**退出*/
	private void exit(){
		stopScanService();
		NotificationManager notificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(0);
		if (null != LoadingActivity.mainActivty){
			LoadingActivity.mainActivty.finish();
		}
		BabyScaleActivity.this.finish();
	}
	
	/**切换秤*/
	private void stopScanService(){
		if (null != UtilConstants.serveIntent){
			stopService(UtilConstants.serveIntent);
		}
	}

	/*****************************蓝牙BLE相关******************************/
	private BlueSingleton singleton;
	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
	private static final int REQUEST_ENABLE_BT_CLICK = 31;
	private BluetoothLeService mBluetoothLeService;
	private BluetoothAdapter mBluetoothAdapter;
	private boolean isServiceReg = false; // mServiceConnection是否已绑定
	private static boolean receiverReleased = false; // mGattUpdateReceiver是否已释放注册

	/**蓝牙服务连接*/
	public final ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName componentName, IBinder service) {
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
			if (!mBluetoothLeService.initialize()) {
				finish();
			}
			mBluetoothLeService.connect(BluetoolUtil.mDeviceAddress);
			isServiceReg = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
			isServiceReg = false;
		}
	};
	
	/**Gatt交互*/
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BLEConstant.ACTION_GATT_CONNECTED.equals(action)) {
				singleton.setmConnected(true);
			} else if (BLEConstant.ACTION_GATT_DISCONNECTED.equals(action)) {
				/*连接状态修改*/
				scale_connect_state.setText(R.string.scale_connect_state_not_connected);
				if (singleton.getmConnected()) {
					singleton.setmConnected(false);
					if (mBluetoothLeService != null) {
						final boolean result = mBluetoothLeService.connect(BluetoolUtil.mDeviceAddress);
						Log.d(TAG, "Connect request result=" + result);
					}
					singleton.scanLeDevice(true, BabyScaleActivity.this, mServiceConnection);
				}
			} else if (BLEConstant.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				sendCodeCount = 0;
				/*连接状态修改*/
				scale_connect_state.setText(R.string.scale_connect_state_connected);
				/*检测到数据*/
				AppData.hasCheckData=true;
				Toast.makeText(BabyScaleActivity.this, getString(R.string.scale_connection_success), Toast.LENGTH_LONG).show();
				//Toast.makeText(BabyScaleActivity.this, getString(R.string.scale_connection_success), Toast.LENGTH_LONG).show();
				if (mBluetoothLeService == null) {
					return;
				}
				if(null!=BluetoolUtil.mConnectedDeviceName && 
						(BluetoolUtil.mConnectedDeviceName.toLowerCase().startsWith("heal") 
						|| BluetoolUtil.mConnectedDeviceName.toLowerCase().startsWith("yu"))){
					// 监听 阿里秤 读通道
					final BluetoothGattCharacteristic characteristic = mBluetoothLeService.getCharacteristicNew(mBluetoothLeService.getSupportedGattServices(), "2a9c");
					mBluetoothLeService.setCharacteristicIndaicate(characteristic, true); // 开始监听通道

					String unit = "00";
					if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_ST)) {
						unit = "02";
					} else if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_LB)) {
						unit = "01";
					} else {
						unit = "00";
					}
					// 获取用户组
					String p = UtilConstants.CURRENT_USER.getGroup().replace("P", "0");
					// 获取 校验位
					String xor = Integer.toHexString(StringUtils.hexToTen("fd") ^ StringUtils.hexToTen("37")^ StringUtils.hexToTen(unit) ^ StringUtils.hexToTen(p));
					Log.e(TAG, "发送新称数据：" + "fd37"+unit + p + "000000000000" + xor);
					// 发送数据
					sendDateToScale("fd37"+unit + p + "000000000000" + xor);
				}else if(null!=BluetoolUtil.mConnectedDeviceName){
					Thread thread = new Thread(new Runnable() {
						@Override
						public void run() {
							if (singleton.getmConnected()) {
								if (mBluetoothLeService != null) {
									send_Data(MyUtil.getUserInfo());
								}
							}
						}
					});
					thread.start();
				}
				
			} else if (BLEConstant.ACTION_DATA_AVAILABLE.equals(action)) {
				String readMessage = intent.getStringExtra(BLEConstant.EXTRA_DATA);
				System.out.println("收到数据：" + readMessage);
				try {
					if(TextUtils.isEmpty(readMessage)) return;
					if (readMessage.startsWith(UtilConstants.BABY_SCALE)) {
						if (UtilConstants.CURRENT_USER.getAgeYear() < 1 || UtilConstants.CURRENT_USER.getBheigth()<30) {
							if(UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_KG)){
								Toast.makeText(BabyScaleActivity.this, getString(R.string.age_error_5), Toast.LENGTH_SHORT).show();
							}else{
								Toast.makeText(BabyScaleActivity.this, getString(R.string.age_error_7), Toast.LENGTH_SHORT).show();
							}
							
							return;
						}
					}else{
						if (UtilConstants.CURRENT_USER.getAgeYear() < 10 || UtilConstants.CURRENT_USER.getBheigth()<100) {
							if(UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_KG)){
								Toast.makeText(BabyScaleActivity.this, getString(R.string.age_error_4), Toast.LENGTH_SHORT).show();
							}else{
								Toast.makeText(BabyScaleActivity.this, getString(R.string.age_error_6), Toast.LENGTH_SHORT).show();
							}
							return;
						}
					}
					boolean newScale = false;
					if ((readMessage.startsWith("0306"))) {
						newScale = true;
						UtilConstants.CURRENT_SCALE = UtilConstants.BODY_SCALE;
						UtilConstants.CURRENT_USER.setScaleType(UtilConstants.CURRENT_SCALE);
					}else{
						newScale = false;
					}
					
					/**判断是不是两次连续的数据*/
					if (readMessage.length() > 31 && (System.currentTimeMillis()-UtilConstants.receiveDataTime>1000)) {
						UtilConstants.receiveDataTime=System.currentTimeMillis();
						
						if(newScale){
							/*脂肪秤*/
							UtilConstants.CURRENT_SCALE=UtilConstants.BODY_SCALE;
							UtilConstants.CURRENT_USER.setScaleType(UtilConstants.CURRENT_SCALE);
							dueDate(readMessage,1);
						}else{
							if (readMessage.equals(UtilConstants.ERROR_CODE)) {
								if (sendCodeCount <= 2) {
									if (mBluetoothLeService != null)
										send_Data(MyUtil.getUserInfo());
									sendCodeCount++;
								} else{
									Toast.makeText(BabyScaleActivity.this, getString(R.string.user_data_error), Toast.LENGTH_SHORT).show();
								}
							} else if (readMessage.equals(UtilConstants.ERROR_CODE_TEST)) {
								Toast.makeText(BabyScaleActivity.this, getString(R.string.scale_measurement_error), Toast.LENGTH_SHORT).show();
							}
							readMessage = readMessage.toLowerCase();
							/*显示称类型错误*/
							if (!readMessage.startsWith(UtilConstants.BABY_SCALE)&&readMessage.length()>31) {
								/*跳转到制定的秤界面*/
								if(readMessage.startsWith(UtilConstants.BODY_SCALE)){
									/*脂肪秤*/
									UtilConstants.CURRENT_SCALE=UtilConstants.BODY_SCALE;
									UtilConstants.CURRENT_USER.setScaleType(UtilConstants.CURRENT_SCALE);
									if(UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_FATLB)){
										UtilConstants.CURRENT_USER.setDanwei(UtilConstants.UNIT_LB);
										UtilConstants.CHOICE_KG = UtilConstants.UNIT_LB;
									}
									/*保存测量数据*/
									RecordDao.dueDate(recordService, readMessage);
								}else if(readMessage.startsWith(UtilConstants.BATHROOM_SCALE)){
									/*人体秤*/
									UtilConstants.CURRENT_SCALE=UtilConstants.BATHROOM_SCALE;
									UtilConstants.CURRENT_USER.setScaleType(UtilConstants.CURRENT_SCALE);
									if(UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_FATLB)){
										UtilConstants.CURRENT_USER.setDanwei(UtilConstants.UNIT_LB);
										UtilConstants.CHOICE_KG = UtilConstants.UNIT_LB;
									}
									/*保存测量数据*/
									RecordDao.dueDate(recordService, readMessage);
								}else if (readMessage.startsWith(UtilConstants.KITCHEN_SCALE)) {
									/* 厨房秤 */
									UtilConstants.CURRENT_SCALE = UtilConstants.KITCHEN_SCALE;
									UtilConstants.CURRENT_USER.setScaleType(UtilConstants.CURRENT_SCALE);
									if(UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_FATLB)){
										UtilConstants.CURRENT_USER.setDanwei(UtilConstants.UNIT_LB);
										UtilConstants.CHOICE_KG = UtilConstants.UNIT_LB;
									}
									/* 保存测量数据 */
									RecordDao.dueKitchenDate(recordService, readMessage, null);
								}
								handler.sendEmptyMessage(UtilConstants.scaleChangeMessage);
								return;
							}
							if (readMessage.equals(UtilConstants.ERROR_CODE_GETDATE)) {
								openErrorDiolg("2");
								return;
							}
							if ((readMessage.startsWith("c") && readMessage.length() == 32)) {
								dueDate(readMessage,0);
							}
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				
				
			}
		}
	};
	
	/** 发送数据 */
	private void send_Data(String data) {
		if(TextUtils.isEmpty(data))return;
		sendDateToScale(data);
		try {
			Thread.sleep(500);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		sendDateToScale(data);
	}
	
	private void sendDateToScale(String data){
		//System.out.println("发送数据=" + MyUtil.getUserInfo());
		final BluetoothGattCharacteristic characteristic2 = mBluetoothLeService.getCharacteristic(mBluetoothLeService.getSupportedGattServices(), "fff4");
		if (characteristic2 != null) {
			mBluetoothLeService.setCharacteristicNotification(characteristic2, true);
		}
		
		try {
			Thread.sleep(500);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		final BluetoothGattCharacteristic characteristic = mBluetoothLeService.getCharacteristic(mBluetoothLeService.getSupportedGattServices(), "fff1");
		if (characteristic != null) {
			final byte[] dataArray = StringUtils.hexStringToByteArray(data);
			characteristic.setValue(dataArray);
			mBluetoothLeService.wirteCharacteristic(characteristic);
			characteristic.getProperties();
		}
	}
	
	/**处理数据*/
	public synchronized void dueDate(String message,int type) {
		if (!BlueSingleton.isIsdoing()) {
			BlueSingleton.setIsdoing(true);
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("duedate", message);
			Records recod = null;
			if(type==1){
				recod = MyUtil.parseZuKangMeaage(this.recordService, message,UtilConstants.CURRENT_USER);
				if(null==recod || recod.getRweight()==0)return;
				if(null!=handler)handler.sendEmptyMessage(6);
			}else{
				recod = MyUtil.parseMeaage(this.recordService, message);
			}
			if (null != recod.getScaleType() && recod.getScaleType().equalsIgnoreCase(UtilConstants.CURRENT_SCALE)) {
				if (recod != null && recod.getUgroup() != null) {
					int ugroup = Integer.parseInt(recod.getUgroup().replace("P", ""));
					if (ugroup < 10) {
						Bundle mBundle = new Bundle();
						mBundle.putSerializable("record", recod);
						intent.putExtras(mBundle);
						intent.setClass(getApplicationContext(), ReceiveAlertActivity.class);
						startActivity(intent);
					}
				}
			}
		}
	}
	
	/**自定义弹窗*/
	private void openErrorDiolg(String code) {
		try {
			Intent openDialog = new Intent(this, CustomDialogActivity.class);
			Bundle mBundle = new Bundle();
			mBundle.putString("error", code);
			openDialog.putExtras(mBundle);
			openDialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(openDialog);
		} catch (Exception e) {
		}
	}
	
	/**BT广播接收器*/
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            	BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device!=null){
					String deviceName = device.getName();
					System.out.println(deviceName + "=" + device.getAddress());
					if (deviceName != null && deviceName.equalsIgnoreCase(UtilConstants.scaleName)) {
						BluetoolUtil.mChatService.connect(device, true);
						stopDiscovery();
						handler.postDelayed(ScanRunnable, 15 * 1000);
					}
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                stopDiscovery();
                handler.postDelayed(ScanRunnable, 10*1000);
            }
        }
    };
    
    /**开始检测蓝牙*/
    public void startDiscovery(){
		// Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);
        
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        mBtAdapter.startDiscovery();
    }
    
    /**停止扫描*/
    public void stopDiscovery(){
    	  mBtAdapter.cancelDiscovery();
    	  BabyScaleActivity.this.unregisterReceiver(mReceiver);
    }
    
	/**蓝牙扫描线程*/
	private Runnable ScanRunnable= new Runnable() {
        public void run() { 
        	startDiscovery();
        }  
    };
    
    
	/**检测是否有测量记录线程*/
	private Runnable CheckHasDataRunnable= new Runnable() {    
        public void run() { 
        	if(!AppData.hasCheckData&&isCurrentActivoty&&!UtilConstants.isTipChangeScale){
        		scaleChangeAlert();
        		UtilConstants.isTipChangeScale=true;
        	}
        }  
    };
    
    /**秤改变弹窗*/
    public void scaleChangeAlert(){
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), ScaleChangeAlertActivity.class);
		startActivity(intent);
    }

}
