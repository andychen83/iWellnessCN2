package com.lefu.es.system;

import java.math.BigDecimal;
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
import android.graphics.Color;
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
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

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
 * 脂肪称主界面
 * 
 * @author Leon
 */
@SuppressLint("HandlerLeak")
public class BodyFatScaleActivity extends Activity implements Runnable {
	private static final String TAG = "BodyFatActivity";
	private static final boolean D = true;
	private RecordService recordService;
	private BluetoothAdapter mBtAdapter;
	private List<Records> rList;
	private ArrayList<View> views = new ArrayList<View>();
	/** 跳转过来ItemID **/
	private int ItemID;
	private ViewPager pager;

	private MyPageAdapter adapter = null;

	private TextView norecord_tv;
	private TextView username_tv;
	private MyTextView2 compare_tv;
	private TextView targetv;
	private UserService uservice;

	private Button infoImg;
	private Button setingImg;
	private Button deletdImg;
	private ImageView headImg;
	private ImageView intentImg;

	private Records curRecord;

	private TextView bodywater_tv;
	private TextView bodyfat_tv;
	private TextView bone_tv;
	private TextView musle_tv;
	private TextView visal_tv;
	private TextView bmi_tv;
	private TextView bmr_tv;
	private TextView time_tv;
	private TextView weight_textView17;
	private TextView physicage_tv;

	private TextView tvBmi = null;
	private TextView scale_connect_state;

	RelativeLayout rlGuide = null;

	private int selectedPosition = -1;

	private LinearLayout lineLayout1;
	private LinearLayout lineLayout2;
	private LinearLayout lineLayout3;
	private LinearLayout lineLayout4;
	private LinearLayout lineLayout5;
	private LinearLayout lineLayout7;
	private LinearLayout lineLayout8;
	private LinearLayout linearLayout77;

	private ImageView leftImg;
	private ImageView rightImg;
	/* 重试次数 */
	private int sendCodeCount = 0;
	private boolean isNotOpenBL = false;
	/* 是否是当前Activity */
	private boolean isCurrentActivoty = true;

	private void showTipMask() {
		HighLightGuideView.builder(this).setText(getString(R.string.click_see_data)).addNoHighLightGuidView(R.drawable.ic_ok).addHighLightGuidView(pager, 0, 0.5f, HighLightGuideView.VIEWSTYLE_CIRCLE).addHighLightGuidView(bodywater_tv, 0, 10f, HighLightGuideView.VIEWSTYLE_OVAL)
				.setTouchOutsideDismiss(false).setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss() {
						if (null == UtilConstants.su) {
							UtilConstants.su = new SharedPreferencesUtil(BodyFatScaleActivity.this);
						}
						UtilConstants.su.editSharedPreferences("lefuconfig", "first_install_bodyfat_scale", "1");
						UtilConstants.FIRST_INSTALL_BODYFAT_SCALE = "1";

					}

				}).show();

	}

	private void showAlertDailog(String title) {
		new com.lefu.es.view.AlertDialog(BodyFatScaleActivity.this).builder().setTitle(getResources().getString(R.string.waring_title)).setMsg(title).setPositiveButton(getResources().getString(R.string.ok_btn), new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null == UtilConstants.su) {
					UtilConstants.su = new SharedPreferencesUtil(BodyFatScaleActivity.this);
				}
				UtilConstants.su.editSharedPreferences("lefuconfig", "first_install_dailog", "1");
				UtilConstants.FIRST_INSTALL_DAILOG = "1";
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
		setContentView(R.layout.activity_body_fat);
		EventBus.getDefault().register(this);
		recordService = new RecordService(this);
		inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ItemID = getIntent().getIntExtra("ItemID", 0);
		initView();
		layoutView();
		/* 初始化用户选择蓝牙类型 */
		String blueTooth_type = null;
		if (UtilConstants.su != null) {
			blueTooth_type = (String) UtilConstants.su.readbackUp("lefuconfig", "bluetooth_type" + UtilConstants.CURRENT_USER.getId(), String.class);
		}
		System.out.println("蓝牙类型:" + blueTooth_type);
		if (blueTooth_type != null && "BT".equals(blueTooth_type)) {
			BluetoolUtil.bleflag = false;
		} else {
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
				isNotOpenBL = true;
			}
		} else {
			if (null == BluetoolUtil.mBluetoothAdapter)
				BluetoolUtil.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			if (null != BluetoolUtil.mBluetoothAdapter && !BluetoolUtil.mBluetoothAdapter.isEnabled()) {
				Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableIntent, BluetoolUtil.REQUEST_ENABLE_BT);
			}
		}

		try {

			if (BluetoolUtil.bleflag && !receiverReleased) {
				registerReceiver(mGattUpdateReceiver, BluetoothLeService.makeGattUpdateIntentFilter());
				receiverReleased = false;
			}

		} catch (Exception e) {
			Log.e(TAG, "onCreate==>" + e.getMessage());
		}

		ExitApplication.getInstance().addActivity(this);
	}

	/** 初始化布局 */
	private void layoutView() {
		lineLayout1 = (LinearLayout) this.findViewById(R.id.linearLayout1);
		lineLayout2 = (LinearLayout) this.findViewById(R.id.linearLayout2);
		lineLayout3 = (LinearLayout) this.findViewById(R.id.linearLayout3);
		lineLayout4 = (LinearLayout) this.findViewById(R.id.linearLayout4);
		lineLayout5 = (LinearLayout) this.findViewById(R.id.linearLayout5);
		lineLayout7 = (LinearLayout) this.findViewById(R.id.linearLayout7);
		lineLayout8 = (LinearLayout) this.findViewById(R.id.linearLayout8);
		linearLayout77 = (LinearLayout) this.findViewById(R.id.linearLayout77);
		lineLayout1.setTag(UtilConstants.BODYWATER_SINGLE);
		lineLayout2.setTag(UtilConstants.BODYFAT_SINGLE);
		lineLayout3.setTag(UtilConstants.BMI_SINGLE);
		lineLayout4.setTag(UtilConstants.BONE_SINGLE);
		lineLayout5.setTag(UtilConstants.VISCALEFAT_SINGLE);
		lineLayout7.setTag(UtilConstants.MUSCALE_SINGLE);
		lineLayout8.setTag(UtilConstants.BMR_SINGLE);
		linearLayout77.setTag(UtilConstants.PHSICALAGE_SINGLE);
		linearLayout77.setVisibility(View.GONE);

		lineLayout1.setOnClickListener((android.view.View.OnClickListener) layoutClickListener);
		lineLayout2.setOnClickListener((android.view.View.OnClickListener) layoutClickListener);
		lineLayout3.setOnClickListener((android.view.View.OnClickListener) layoutClickListener);
		lineLayout4.setOnClickListener((android.view.View.OnClickListener) layoutClickListener);
		lineLayout5.setOnClickListener((android.view.View.OnClickListener) layoutClickListener);
		lineLayout7.setOnClickListener((android.view.View.OnClickListener) layoutClickListener);
		lineLayout8.setOnClickListener((android.view.View.OnClickListener) layoutClickListener);
		linearLayout77.setOnClickListener((android.view.View.OnClickListener) layoutClickListener);
	}

	/** 布局点击事件 */
	android.view.View.OnClickListener layoutClickListener = new android.view.View.OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(BodyFatScaleActivity.this, RecordListActivity.class);
			if (null != v.getTag()) {
				intent.putExtra("type", (int) ((Integer) v.getTag()));
			}
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivityForResult(intent, 0);
		}
	};

	/** 初始化视图 */
	private void initView() {
		uservice = new UserService(this);
		weight_textView17 = (TextView) this.findViewById(R.id.weight_textView17);
		norecord_tv = (TextView) this.findViewById(R.id.norecord_textView);
		username_tv = (TextView) this.findViewById(R.id.username_tv);
		compare_tv = (MyTextView2) this.findViewById(R.id.compare_tv);
		targetv = (TextView) this.findViewById(R.id.targetv);

		/* 称单位 */
		if (null != UtilConstants.su) {
			UtilConstants.CHOICE_KG = (String) UtilConstants.su.readbackUp("lefuconfig", "unit", UtilConstants.UNIT_KG);
			Log.i(TAG, "weight unit: " + UtilConstants.CHOICE_KG);
		}

		if (null != UtilConstants.CURRENT_USER) {
			compare_tv.setTexts("0.0" + UtilConstants.CURRENT_USER.getDanwei(), null);
		}

		rlGuide = (RelativeLayout) this.findViewById(R.id.rl_guide);

		/* 文本内容 */
		bodywater_tv = (TextView) this.findViewById(R.id.textView2);
		bodyfat_tv = (TextView) this.findViewById(R.id.textView4);
		bone_tv = (TextView) this.findViewById(R.id.textView6);
		musle_tv = (TextView) this.findViewById(R.id.textView14);
		visal_tv = (TextView) this.findViewById(R.id.textView10);
		bmi_tv = (TextView) this.findViewById(R.id.textView8);
		bmr_tv = (TextView) this.findViewById(R.id.textView12);
		time_tv = (TextView) this.findViewById(R.id.textView19);
		physicage_tv = (TextView) this.findViewById(R.id.tv_physicage);
		tvBmi = (TextView) this.findViewById(R.id.tv_guide_value);
		scale_connect_state = (TextView) this.findViewById(R.id.scale_connect_state);

		/* 按钮 */
		infoImg = (Button) this.findViewById(R.id.info_img_btn);
		setingImg = (Button) this.findViewById(R.id.seting_img_btn);
		deletdImg = (Button) this.findViewById(R.id.delete_img_btn);
		headImg = (ImageView) this.findViewById(R.id.user_header);
		intentImg = (ImageView) this.findViewById(R.id.imageView1);
		leftImg = (ImageView) this.findViewById(R.id.imageView2);
		rightImg = (ImageView) this.findViewById(R.id.imageView3);

		/* 监听 */
		infoImg.setOnClickListener((android.view.View.OnClickListener) menuBtnOnClickListener);
		deletdImg.setOnClickListener((android.view.View.OnClickListener) menuBtnOnClickListener);
		setingImg.setOnClickListener((android.view.View.OnClickListener) menuBtnOnClickListener);
		headImg.setOnClickListener((android.view.View.OnClickListener) btnOnClickListener);
		intentImg.setOnClickListener((android.view.View.OnClickListener) btnOnClickListener);

		/* 分页显示测量记录 */
		pager = (ViewPager) this.findViewById(R.id.weight_contains);
		pager.bringToFront();
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
						if (arg0 > 0) {
							selectedPosition = arg0 - 1;
						} else {
							selectedPosition = 0;
						}
						curRecord = CacheHelper.recordListDesc.get(selectedPosition);
						handler.sendEmptyMessage(1);
					}

					if (pager.getCurrentItem() < 2) {
						leftImg.setImageDrawable(getResources().getDrawable(R.drawable.left_to));
					} else {
						leftImg.setImageDrawable(getResources().getDrawable(R.drawable.arrowleft_white));
					}
					if (pager.getCurrentItem() >= (views.size() - 2)) {
						rightImg.setImageDrawable(getResources().getDrawable(R.drawable.right_to));
					} else {
						rightImg.setImageDrawable(getResources().getDrawable(R.drawable.arrowright_white));
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
		/* 当前用户不为空 */
		if (UtilConstants.CURRENT_USER != null) {
			/* 用户姓名 */
			this.username_tv.setText(UtilConstants.CURRENT_USER.getUserName());
			/* 目标体重 */
			if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_KG)) {
				targetv.setText(UtilTooth.toOnePonit(UtilConstants.CURRENT_USER.getTargweight()) + "" + this.getText(R.string.kg_danwei));
			} else if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_LB) || UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_FATLB) || UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_ST)) {
				targetv.setText(UtilTooth.onePoint(UtilTooth.kgToLB_target(UtilConstants.CURRENT_USER.getTargweight())) + this.getText(R.string.lb_danwei));
			} else {
				targetv.setText(UtilTooth.toOnePonit(UtilConstants.CURRENT_USER.getTargweight()) + "" + this.getText(R.string.kg_danwei));
			}
			/* 设置用户头像 */
			if (null != UtilConstants.CURRENT_USER.getPer_photo() && !"".equals(UtilConstants.CURRENT_USER.getPer_photo()) && !UtilConstants.CURRENT_USER.getPer_photo().equals("null")) {
				Bitmap bitmap = imageUtil.getBitmapfromPath(UtilConstants.CURRENT_USER.getPer_photo());
				headImg.setImageBitmap(bitmap);
			}
		}

		CheckBox cb = (CheckBox) this.findViewById(R.id.cb_scan_device);
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.i(TAG, "click to start scan");
				if (mBluetoothAdapter.isEnabled() && singleton != null) {
					Log.i(TAG, "start scan");
					singleton.scanLeDevice(false, BodyFatScaleActivity.this, mServiceConnection);

					if (mBluetoothLeService != null) {
						mBluetoothLeService.disconnect();
					}

					singleton.scanLeDevice(true, BodyFatScaleActivity.this, mServiceConnection);
				}
			}
		});
	}

	/** 设置视图数据 */
	private void setViews() {
		/* 当前用户不为空 */
		if (UtilConstants.CURRENT_USER != null) {
			/* 用户姓名 */
			this.username_tv.setText(UtilConstants.CURRENT_USER.getUserName());
			/* 目标体重 */
			if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_KG)) {
				targetv.setText(UtilTooth.toOnePonit(UtilConstants.CURRENT_USER.getTargweight()) + "" + this.getText(R.string.kg_danwei));
			} else if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_LB) || UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_FATLB) || UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_ST)) {
				targetv.setText(UtilTooth.onePoint(UtilTooth.kgToLB_target(UtilConstants.CURRENT_USER.getTargweight())) + this.getText(R.string.lb_danwei));
			} else {
				targetv.setText(UtilTooth.toOnePonit(UtilConstants.CURRENT_USER.getTargweight()) + "" + this.getText(R.string.kg_danwei));
			}
			/* 设置用户头像 */
			if (null != UtilConstants.CURRENT_USER.getPer_photo() && !"".equals(UtilConstants.CURRENT_USER.getPer_photo()) && !UtilConstants.CURRENT_USER.getPer_photo().equals("null")) {
				Bitmap bitmap = imageUtil.getBitmapfromPath(UtilConstants.CURRENT_USER.getPer_photo());
				headImg.setImageBitmap(bitmap);
			}
		}
		/* 是否有测量记录 */
		if (curRecord != null) {
			bodywater_tv.setText(curRecord.getRbodywater() + "%");
			bodyfat_tv.setText(curRecord.getRbodyfat() + "%");
			if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_KG)) {
				bone_tv.setText(curRecord.getRbone() + UtilConstants.UNIT_KG);
				musle_tv.setText(curRecord.getRmuscle() + UtilConstants.UNIT_KG);
			} else if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_LB) || UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_FATLB)) {
				bone_tv.setText(UtilTooth.kgToLB(curRecord.getRbone()) + UtilConstants.UNIT_LB);
				musle_tv.setText(UtilTooth.kgToLB(curRecord.getRmuscle()) + UtilConstants.UNIT_LB);
			} else if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_ST)) {
				bone_tv.setText(UtilTooth.kgToLB(curRecord.getRbone()) + UtilConstants.UNIT_ST);
				musle_tv.setText(UtilTooth.kgToLB(curRecord.getRmuscle()) + UtilConstants.UNIT_ST);
			} else {
				bone_tv.setText(curRecord.getRbone() + UtilConstants.UNIT_KG);
				musle_tv.setText(curRecord.getRmuscle() + UtilConstants.UNIT_KG);
			}

			visal_tv.setText(curRecord.getRvisceralfat() + "");
			bmi_tv.setText(curRecord.getRbmi() + "");
			bmr_tv.setText(curRecord.getRbmr() + "kcal");
			/* 判断是否有身体年龄 */
			if (curRecord.getBodyAge() > 0) {
				linearLayout77.setVisibility(View.VISIBLE);
				physicage_tv.setText(UtilTooth.keep0Point(curRecord.getBodyAge()));
			} else {
				linearLayout77.setVisibility(View.GONE);
				physicage_tv.setText("0");
			}
			Date iDate = UtilTooth.stringToTime(curRecord.getRecordTime());
			if (iDate != null) {
				Locale local = Locale.getDefault();
				time_tv.setText(StringUtils.getDateString(iDate, 5));
			}
			if (null != UtilConstants.CURRENT_USER) {
				float bmi = UtilTooth.countBMI2(curRecord.getRweight(), (UtilConstants.CURRENT_USER.getBheigth() / 100));
				curRecord.setRbmi(UtilTooth.myround(bmi));
				tvBmi.setText(curRecord.getRbmi() + "");
			}

			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			int dp20 = DisplayUtil.dip2px(this, 64);
			int rlWidth = DisplayUtil.getWidth(this) - dp20;
			lp.setMargins((int) (UtilTooth.changeBMI(curRecord.getRbmi(), rlWidth)), 0, 0, 0);
			rlGuide.setLayoutParams(lp);

			if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_KG)) {
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
			} else {
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
			/* 无测量记录 */
			bodywater_tv.setText("0.0%");
			bodyfat_tv.setText("0.0%");
			bone_tv.setText("0.0");
			musle_tv.setText("0.0");
			visal_tv.setText("0");
			bmi_tv.setText("0");
			bmr_tv.setText("0kcal");
			tvBmi.setText("0.0");
			physicage_tv.setText("0");
			if (null != UtilConstants.CURRENT_USER) {
				compare_tv.setTexts("0.0" + UtilConstants.CURRENT_USER.getDanwei(), null);
			}

			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			int dp20 = DisplayUtil.dip2px(this, 64);
			int rlWidth = DisplayUtil.getWidth(this) - dp20;
			lp.setMargins((int) (UtilTooth.changeBMI(0, rlWidth)), 0, 0, 0);
			rlGuide.setLayoutParams(lp);
		}
	}

	/** 菜单按钮监听 */
	android.view.View.OnClickListener menuBtnOnClickListener = new android.view.View.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.info_img_btn :
					/* 跳转info界面 */
					Intent intent0 = new Intent();
					intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent0.setClass(BodyFatScaleActivity.this, InfoActivity.class);
					BodyFatScaleActivity.this.startActivity(intent0);
					break;
				case R.id.seting_img_btn :
					/* 跳转设置界面 */
					Intent intent = new Intent();
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setClass(BodyFatScaleActivity.this, SettingActivity.class);
					BodyFatScaleActivity.this.startActivity(intent);
					break;
				case R.id.delete_img_btn :
					/* 弹窗删除 */
					dialog(getString(R.string.deleted_waring), v.getId());
					break;
			}
		}
	};

	/** 顶部图片点击事件 */
	android.view.View.OnClickListener btnOnClickListener = new android.view.View.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.user_header :
					/* 跳转到用户列表界面 */
					Intent intent1 = new Intent();
					intent1.setClass(BodyFatScaleActivity.this, UserListActivity.class);
					BodyFatScaleActivity.this.startActivity(intent1);
					break;
				case R.id.imageView1 :
					/* 跳转到链接 */
					// Intent intent2 = new Intent();
					// intent2.setData(Uri.parse(UtilConstants.homeUrl));
					// intent2.setAction(Intent.ACTION_VIEW);
					// BodyFatScaleActivity.this.startActivity(intent2);
					break;
			}
		}
	};

	/** 弹窗 */
	protected void dialog(String title, final int id) {
		AlertDialog.Builder builder = new Builder(BodyFatScaleActivity.this);
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
							/* 删除当前记录 */
							if (null != curRecord) {
								recordService.delete(curRecord);
							}
							/* 重新设置当前记录 */
							CacheHelper.recordListDesc = recordService.getAllDatasByScaleAndIDDesc(UtilConstants.CURRENT_SCALE, ItemID, UtilConstants.CURRENT_USER.getBheigth());
							if (null != CacheHelper.recordListDesc && CacheHelper.recordListDesc.size() > 0) {
								curRecord = CacheHelper.recordListDesc.get(0);
							} else {
								curRecord = null;
							}
							/* 刷新界面数据 */
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
		new Thread(this).start();
		/* 是否提示更换称弹窗 */
		isCurrentActivoty = true;
		if (!AppData.hasCheckData) {
			handler.postDelayed(CheckHasDataRunnable, 30 * 1000);
		}
		/* 秤识别中 */
		AppData.isCheckScale = false;

		/* 是否重新检测称 */
		if (AppData.reCheck) {
			/* 跳转到秤检测界面 */
			Intent intent1 = new Intent();
			intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent1.setClass(BodyFatScaleActivity.this, ReCheckActivity.class);
			BodyFatScaleActivity.this.startActivity(intent1);
			exit();
			ExitApplication.getInstance().exit(this);
		} else {
			if (BluetoolUtil.bleflag) {
				// 再次进入界面后判断是否要重新搜索
				singleton = BlueSingleton.getInstance(handler);
				if (singleton.getmConnected()) {
					/* 连接状态修改 */
					scale_connect_state.setText(R.string.scale_connect_state_connected);
				} else {
					/* 连接状态修改 */
					scale_connect_state.setText(R.string.scale_connect_state_not_connected);
				}
				if (mBluetoothAdapter.isEnabled() && !singleton.getmConnected() && !singleton.getmScanning()) {
					singleton.scanLeDevice(true, this, mServiceConnection);
				}
			}
		}
	}

	@Override
	protected void onPause() {
		/* 非当前Activity */
		isCurrentActivoty = false;

		stopScanService();
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		stopScanService();
		if(null!=mBluetoothLeService){
			Log.e(TAG, "主界面onStop断开蓝牙");
			//mBluetoothLeService.disconnect();
			mBluetoothLeService.close();
		}
		super.onStop();
	}

	/** 初始化测量记录分页 */
	private void Init(List<Records> rList) {
		if (null == views) {
			views = new ArrayList<View>();
		} else {
			views.clear();
		}
		LayoutInflater mLi = LayoutInflater.from(this);
		if (null != rList && rList.size() > 0) {
			weight_textView17.setVisibility(View.GONE);
			rightImg.setVisibility(View.VISIBLE);
			leftImg.setVisibility(View.VISIBLE);
			if (rList.size() > 1) {
				this.rightImg.setImageDrawable(getResources().getDrawable(R.drawable.arrowright_white));
			}
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
			weight_textView17.setVisibility(View.VISIBLE);
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

	/** 创建分页视图 */
	public View creatView(Records record, ViewGroup viewGroup) {
		View convertView = null;
		MyTextView tvDetail_weight_title = null;
		TextView unit_tv = null;
		if (null == convertView) {
			convertView = inflater.inflate(R.layout.user_weight_pager, null);
			tvDetail_weight_title = (MyTextView) convertView.findViewById(R.id.weight_textView);
			unit_tv = (TextView) convertView.findViewById(R.id.unti_tv);
			convertView.setTag(record);
			convertView.setOnClickListener(imgOnClickListener);
		}
		if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_ST)) {
			if (UtilConstants.CURRENT_SCALE.equals(UtilConstants.BODY_SCALE)) {
				String[] tempS = UtilTooth.kgToStLbForScaleFat2(record.getRweight());

				tvDetail_weight_title.setTexts(tempS[0], tempS[1]);
				if (null != unit_tv) {
					unit_tv.setText(this.getText(R.string.stlb_danwei));
				}
			} else {
				tvDetail_weight_title.setTexts(UtilTooth.kgToStLb(record.getRweight()), null);
				if (null != unit_tv) {
					unit_tv.setText(this.getText(R.string.stlb_danwei));
				}
			}
		} else if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_LB) || UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_FATLB)) {
			tvDetail_weight_title.setTexts(UtilTooth.kgToLB_ForFatScale(record.getRweight()), null);
			if (null != unit_tv) {
				unit_tv.setText(this.getText(R.string.lb_danwei));
			}
		} else {
			tvDetail_weight_title.setTexts(record.getRweight() + "", null);
			if (null != unit_tv) {
				unit_tv.setText(this.getText(R.string.kg_danwei));
			}
		}
		return convertView;
	}

	/** 分页框点击事件 */
	android.view.View.OnClickListener imgOnClickListener = new android.view.View.OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(BodyFatScaleActivity.this, RecordListActivity.class);
			intent.putExtra("type", UtilConstants.WEIGHT_SINGLE);
			if (null != v.getTag()) {
				intent.putExtra("id", ((Records) v.getTag()).getId());
			} else {
				intent.putExtra("id", -1);
			}
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivityForResult(intent, 0);
		}
	};

	private LayoutInflater inflater;

	/** 加载当前测量记录 */
	@Override
	public void run() {
//		if (ItemID != 0) {
//			try {
//				UtilConstants.CURRENT_USER = this.uservice.find(ItemID);
//				CacheHelper.recordListDesc = this.recordService.getAllDatasByScaleAndIDDesc(UtilConstants.CURRENT_SCALE, ItemID, 167f);
//				if (null != CacheHelper.recordListDesc && CacheHelper.recordListDesc.size() > 0) {
//					curRecord = CacheHelper.recordListDesc.get(0);
//				} else {
//					curRecord = null;
//				}
//				Log.i(TAG, "record time: " + String.valueOf(curRecord.getRecordTime()));
//				handler.sendEmptyMessage(0);
//			} catch (Exception e) {
//
//				e.printStackTrace();
//			}
//		}
		try {
			if(null==UtilConstants.CURRENT_USER)return;
			CacheHelper.recordListDesc = this.recordService.getAllDatasByScaleAndIDDesc(UtilConstants.CURRENT_SCALE, UtilConstants.CURRENT_USER.getId(), 167f);
			if (null != CacheHelper.recordListDesc && CacheHelper.recordListDesc.size() > 0) {
				curRecord = CacheHelper.recordListDesc.get(0);
			} else {
				curRecord = null;
			}
			Log.i(TAG, "record time: " + String.valueOf(curRecord.getRecordTime()));
			handler.sendEmptyMessage(0);
		} catch (Exception e) {

			e.printStackTrace();
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
					/* 更新显示内容 */
					setViews();
					/* 更新分页内容 */
					Init(CacheHelper.recordListDesc);
					if (TextUtils.isEmpty(UtilConstants.FIRST_INSTALL_BODYFAT_SCALE) && CacheHelper.recordListDesc.size() == 1) {
						showTipMask();
					}
					break;
				case 1 :
					/* 更新显示内容 */
					setViews();
					break;
				case 5 :
					/* 退出 */
					exit();
					ExitApplication.getInstance().exit(BodyFatScaleActivity.this);
					break;
				case UtilConstants.scaleChangeMessage :
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
					/* 跳转 */
					System.out.println("jump to LoadingActivity");
					ExitApplication.getInstance().exit(BodyFatScaleActivity.this);
					Intent intent = new Intent();
					intent.setClass(BodyFatScaleActivity.this, LoadingActivity.class);
					BodyFatScaleActivity.this.startActivity(intent);
					break;
			}
		}
	};

	@Override
	protected void onStart() {
		if (!BluetoolUtil.bleflag && null == UtilConstants.serveIntent) {
			UtilConstants.serveIntent = new Intent(this, TimeService.class);
			this.startService(UtilConstants.serveIntent);
			/* 开机BT循环扫描线程 */
			new Thread(ScanRunnable).start();
			/* 连接状态 */
			TimeService.scale_connect_state = scale_connect_state;
		}
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
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

	/** 退出 */
	private void exit() {
		/* 停止服务 */
		stopScanService();

		NotificationManager notificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(0);
		if (null != LoadingActivity.mainActivty) {
			LoadingActivity.mainActivty.finish();
		}

		BodyFatScaleActivity.this.finish();
	}

	/** 停止扫描服务 */
	private void stopScanService() {
		/* 蓝牙2.1 */
		if (null != UtilConstants.serveIntent) {
			stopService(UtilConstants.serveIntent);
		}
		/* 蓝牙4.0 */
		if (BluetoolUtil.bleflag) {
			singleton.scanLeDevice(false, this, mServiceConnection);
		}
	}

	/******************************* BLE相关 ********************************/
	private BlueSingleton singleton;
	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
	private static final int REQUEST_ENABLE_BT_CLICK = 31;
	private BluetoothLeService mBluetoothLeService;
	private BluetoothAdapter mBluetoothAdapter;
	private boolean isServiceReg = false; // mServiceConnection是否已绑定
	private static boolean receiverReleased = false; // mGattUpdateReceiver是否已释放注册

	public final ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName componentName, IBinder service) {
			Log.e(TAG, "ServiceConnection  onServiceConnected...");
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
			if (!mBluetoothLeService.initialize()) {
				finish();
			}
			mBluetoothLeService.connect(BluetoolUtil.mDeviceAddress);
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			mBluetoothLeService.connect(BluetoolUtil.mDeviceAddress);
			isServiceReg = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			Log.e(TAG, "ServiceConnection  onServiceDisconnected...");
			isServiceReg = false;
		}
	};
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BLEConstant.ACTION_GATT_CONNECTED.equals(action)) {
				Log.e(TAG, "ACTION_GATT_CONNECTED");
				/* 连接状态修改 */
				// scale_connect_state.setText(R.string.scale_connect_state_connected);
				singleton.setmConnected(true);
			} else if (BLEConstant.ACTION_GATT_DISCONNECTED.equals(action)) {
				Log.e(TAG, "ACTION_GATT_DISCONNECTED");
				/* 连接状态修改 */
				scale_connect_state.setText(R.string.scale_connect_state_not_connected);
				if (!isNotOpenBL) {
					if (singleton.getmConnected()) {
						singleton.setmConnected(false);

						if (mBluetoothLeService != null) {
							final boolean result = mBluetoothLeService.connect(BluetoolUtil.mDeviceAddress);
							Log.d(TAG, "Connect request result=" + result);
						}

						singleton.scanLeDevice(true, BodyFatScaleActivity.this, mServiceConnection);
					}
				}
			} else if (BLEConstant.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				Log.e(TAG, "ACTION_GATT_SERVICES_DISCOVERED");
				sendCodeCount = 0;
				/* 连接状态修改 */
				scale_connect_state.setText(R.string.scale_connect_state_connected);
				/* 检测到数据 */
				AppData.hasCheckData = true;
				Toast.makeText(BodyFatScaleActivity.this, getString(R.string.scale_connection_success), Toast.LENGTH_LONG).show();
				// Toast.makeText(BodyFatScaleActivity.this,
				// getString(R.string.scale_connection_success),
				// Toast.LENGTH_LONG).show();

				if (null != BluetoolUtil.mConnectedDeviceName && (BluetoolUtil.mConnectedDeviceName.toLowerCase().startsWith("heal") || BluetoolUtil.mConnectedDeviceName.toLowerCase().startsWith("yu"))) {
					try {
						if (null != mBluetoothLeService) {
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
							sendDateToScale1("fd37"+unit + p + "000000000000" + xor);
						}

					} catch (Exception e) {
						Log.e(TAG, e.getMessage());
					}
				} else if (null != BluetoolUtil.mConnectedDeviceName) {
					Thread thread = new Thread(new Runnable() {
						@Override
						public void run() {
							if (mBluetoothLeService != null) {
								send_Data();
							}
						}
					});
					thread.start();
				}
			} else if (BLEConstant.ACTION_DATA_AVAILABLE.equals(action)) {
				String readMessage = intent.getStringExtra(BLEConstant.EXTRA_DATA);
				try {
					if (TextUtils.isEmpty(readMessage))
						return;
					boolean newScale = false;
					if (readMessage.startsWith(UtilConstants.BABY_SCALE)) {
						if (UtilConstants.CURRENT_USER.getAgeYear() < 1 || UtilConstants.CURRENT_USER.getBheigth()<30) {
							if(UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_KG)){
								Toast.makeText(BodyFatScaleActivity.this, getString(R.string.age_error_5), Toast.LENGTH_SHORT).show();
							}else{
								Toast.makeText(BodyFatScaleActivity.this, getString(R.string.age_error_7), Toast.LENGTH_SHORT).show();
							}
							
							return;
						}
					}else{
						if (UtilConstants.CURRENT_USER.getAgeYear() < 10 || UtilConstants.CURRENT_USER.getBheigth()<100) {
							if(UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_KG)){
								Toast.makeText(BodyFatScaleActivity.this, getString(R.string.age_error_4), Toast.LENGTH_SHORT).show();
							}else{
								Toast.makeText(BodyFatScaleActivity.this, getString(R.string.age_error_6), Toast.LENGTH_SHORT).show();
							}
							return;
						}
					}
					
					if ((readMessage.startsWith("0306"))) {
						newScale = true;
						UtilConstants.CURRENT_SCALE = UtilConstants.BODY_SCALE;
						UtilConstants.CURRENT_USER.setScaleType(UtilConstants.CURRENT_SCALE);
					} else {
						newScale = false;
					}
					/** 判断是不是两次连续的数据 */
					if (readMessage.length() > 31 && (System.currentTimeMillis() - UtilConstants.receiveDataTime > 1000)) {
						UtilConstants.receiveDataTime = System.currentTimeMillis();

						if (newScale) {
							dueDate(readMessage, 1);
						} else {
							if (readMessage.equals(UtilConstants.ERROR_CODE)) {
								if (sendCodeCount < 1) {
									if (mBluetoothLeService != null)
										send_Data();
									sendCodeCount++;
								} else {
									Toast.makeText(BodyFatScaleActivity.this, getString(R.string.user_data_error), Toast.LENGTH_LONG).show();
									Toast.makeText(BodyFatScaleActivity.this, getString(R.string.user_data_error), Toast.LENGTH_LONG).show();
								}
							} else if (readMessage.equals(UtilConstants.ERROR_CODE_TEST)) {
								Toast.makeText(BodyFatScaleActivity.this, getString(R.string.scale_measurement_error), Toast.LENGTH_SHORT).show();
							}
							/* 判断称类型是否错误 */
							if (!readMessage.startsWith(UtilConstants.BODY_SCALE) && readMessage.length() > 31) {
								/* 跳转到制定的秤界面 */
								if (readMessage.startsWith(UtilConstants.BATHROOM_SCALE)) {
									/* 人体秤 */
									UtilConstants.CURRENT_SCALE = UtilConstants.BATHROOM_SCALE;
									UtilConstants.CURRENT_USER.setScaleType(UtilConstants.CURRENT_SCALE);
									/* 保存测量数据 */
									RecordDao.dueDate(recordService, readMessage);
								} else if (readMessage.startsWith(UtilConstants.BABY_SCALE)) {
									/* 婴儿秤 */
									UtilConstants.CURRENT_SCALE = UtilConstants.BABY_SCALE;
									UtilConstants.CURRENT_USER.setScaleType(UtilConstants.CURRENT_SCALE);
									/* 保存测量数据 */
									RecordDao.dueDate(recordService, readMessage);
								} else if (readMessage.startsWith(UtilConstants.KITCHEN_SCALE)) {
									/* 厨房秤 */
									UtilConstants.CURRENT_SCALE = UtilConstants.KITCHEN_SCALE;
									UtilConstants.CURRENT_USER.setScaleType(UtilConstants.CURRENT_SCALE);
									/* 保存测量数据 */
									RecordDao.dueKitchenDate(recordService, readMessage, null);
								}
								handler.sendEmptyMessage(UtilConstants.scaleChangeMessage);
								// send_CloseData();
								return;
							}
							if (readMessage.equals(UtilConstants.ERROR_CODE_GETDATE)) {
								openErrorDiolg("2");
								return;
							}
							if ((readMessage.startsWith("c") && readMessage.length() > 31)) {
								dueDate(readMessage, 0);
								// send_CloseData();
							}
						}
					}
				} catch (Exception e) {
					Log.e(TAG, "接受数据解析异常==>" + e.getMessage());
				}

			}
		}
	};

	/** 处理数据 */
	public synchronized void dueDate(String message, int type) {
		if (!BlueSingleton.isIsdoing()) {
			BlueSingleton.setIsdoing(true);
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("duedate", message);
			Records recod = null;
			if (type == 1) {
				recod = MyUtil.parseZuKangMeaage(this.recordService, message, UtilConstants.CURRENT_USER);
				if(null==recod || recod.getRweight()==0)return;
			} else {
				recod = MyUtil.parseMeaage(this.recordService, message);
			}
			if (recod != null && null != recod.getScaleType() && recod.getScaleType().equalsIgnoreCase(UtilConstants.CURRENT_SCALE)) {
				// 提示信息
				if (TextUtils.isEmpty(UtilConstants.FIRST_INSTALL_DAILOG)) {
					if (recod.getRbodywater() == 0) {
						// showAlertDailog(getResources().getString(R.string.keep_on_scale_waring));
					}
				}
				// 提示信息
				if (recod.getRweight() != 0 && recod.getBodyAge() == 0 && recod.getRbodyfat() == 0) {
					// 第一次接受数据才提示
					if (TextUtils.isEmpty(UtilConstants.FIRST_RECEIVE_BODYFAT_SCALE_KEEP_STAND_WITH_BARE_FEET)) {
						showAlertDailog(getResources().getString(R.string.keep_stand_with_bare_feet));
						if (null == UtilConstants.su) {
							UtilConstants.su = new SharedPreferencesUtil(BodyFatScaleActivity.this);
						}
						UtilConstants.su.editSharedPreferences("lefuconfig", "first_badyfat_scale_keep_stand_with_bare_feet", "1");
						UtilConstants.FIRST_RECEIVE_BODYFAT_SCALE_KEEP_STAND_WITH_BARE_FEET = "1";
					}

				}

				if (recod.getUgroup() != null) {
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

	/** 错误弹窗 */
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

	/** 发送数据 */
	private void send_Data() {
		String data = MyUtil.getUserInfo();
		// 兼容老版本秤
		sendDateToScale1(data);
		// System.out.println("scale name: " +
		// BluetoolUtil.mConnectedDeviceName);
		// if
		// (BluetoolUtil.mConnectedDeviceName.equals(BluetoolUtil.HEALTH_SCALE))
		// {
		// try {
		// Thread.sleep(300);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// requestOfflineData();
		// } else {
		// sendDateToScale();
		// try {
		// Thread.sleep(500);
		//
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// sendDateToScale();
		// }
	}

	/** 发送同步时间命令 */
	private void syncDate(String date) {
		sendDateToScale(date);
	}

	/** 发送命令，请求离线数据 */
	private void requestOfflineData() {
		sendDateToScale("f200");
	}

	private void sendDateToScale1(String data) {
		
		sendDateToScale(data);
//		try {
//			Thread.sleep(300);
//
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		sendDateToScale(data);
	}

	private void sendDateToScale(String data) {
		System.out.println("发送数据=" + data);
		// 通知秤
		final BluetoothGattCharacteristic characteristic2 = mBluetoothLeService.getCharacteristic(mBluetoothLeService.getSupportedGattServices(), "fff4");
		if (characteristic2 != null) {
			mBluetoothLeService.setCharacteristicNotification(characteristic2, true);
		}
		try {
			Thread.sleep(400);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// 获取秤写通道
		final BluetoothGattCharacteristic characteristic = mBluetoothLeService.getCharacteristic(mBluetoothLeService.getSupportedGattServices(), "fff1");
		if (characteristic != null) {
			final byte[] dataArray = StringUtils.hexStringToByteArray(data);
			characteristic.setValue(dataArray);
			mBluetoothLeService.wirteCharacteristic(characteristic);// 发送数据
			characteristic.getProperties();
		}
		try {
			Thread.sleep(500);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// 获取秤写通道
		//final BluetoothGattCharacteristic characteristic = mBluetoothLeService.getCharacteristic(mBluetoothLeService.getSupportedGattServices(), "fff1");
		if (characteristic != null) {
			final byte[] dataArray = StringUtils.hexStringToByteArray(data);
			characteristic.setValue(dataArray);
			mBluetoothLeService.wirteCharacteristic(characteristic);// 发送数据
			characteristic.getProperties();
		}
	}

	/** BT广播接收器 */
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device != null) {
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
				handler.postDelayed(ScanRunnable, 10 * 1000);
			}
		}
	};

	/** 开始检测蓝牙 */
	public void startDiscovery() {
		try {
			System.out.println("BT开始扫描...");
			// Register for broadcasts when a device is discovered
			final IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
			intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
			this.registerReceiver(mReceiver, intentFilter);
			mBtAdapter = BluetoothAdapter.getDefaultAdapter();
			mBtAdapter.startDiscovery();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/** 停止扫描 */
	public void stopDiscovery() {
		try {
			mBtAdapter.cancelDiscovery();
			if (null != mReceiver)
				BodyFatScaleActivity.this.unregisterReceiver(mReceiver);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/** 蓝牙扫描线程 */
	private Runnable ScanRunnable = new Runnable() {
		public void run() {
			startDiscovery();
		}
	};

	/** 检测是否有测量记录线程 */
	private Runnable CheckHasDataRunnable = new Runnable() {
		public void run() {
			if (!AppData.hasCheckData && isCurrentActivoty && !UtilConstants.isTipChangeScale) {
				scaleChangeAlert();
				UtilConstants.isTipChangeScale = true;
			}
		}
	};

	/** 秤改变弹窗 */
	public void scaleChangeAlert() {
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), ScaleChangeAlertActivity.class);
		startActivity(intent);
	}
}
