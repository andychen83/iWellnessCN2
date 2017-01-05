package com.lefu.es.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.FragmentManager;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.lefu.es.entity.NutrientBo;
import com.lefu.es.entity.Records;
import com.lefu.es.event.NoRecordsEvent;
import com.lefu.es.service.ExitApplication;
import com.lefu.es.service.RecordService;
import com.lefu.es.service.TimeService;
import com.lefu.es.service.UserService;
import com.lefu.es.system.fragment.MyDialogFragment;
import com.lefu.es.system.fragment.MyDialogFragment.NatureSelectListener;
import com.lefu.es.util.MyUtil;
import com.lefu.es.util.SharedPreferencesUtil;
import com.lefu.es.util.StringUtils;
import com.lefu.es.util.Tool;
import com.lefu.es.util.UtilTooth;
import com.lefu.es.view.MyTextView;
import com.lefu.es.view.guideview.HighLightGuideView;
import com.lefu.es.view.guideview.HighLightGuideView.OnDismissListener;
import com.lefu.es.view.kmpautotextview.KMPAutoComplTextView;
import com.lefu.iwellness.newes.cn.system.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 营养称(厨房)主界面
 * 
 * @author Leon
 */
@SuppressLint("HandlerLeak")
public class KitchenScaleActivity extends Activity implements Runnable,NatureSelectListener {
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
	private UserService uservice;

	private Button infoImg;
	private Button setingImg;
	private Button deletdImg;
	private ImageView headImg;
	private ImageView intentImg;
	private Button unit_btn;

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

	//private TextView tvBmi = null;
	private TextView scale_connect_state;

	//RelativeLayout rlGuide = null;
    private TextView food_name;
	
	private KMPAutoComplTextView search_et;
	private ArrayAdapter<String> autoAdapter = null;  
	
	private TextView search_tv;
	private TextView save_tv;

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
	
	private NutrientBo selectNutrient = null;
	private TextView milkname_tx;


	private void showTipMask() {
		HighLightGuideView.builder(this).setText(getString(R.string.kitchen_list_see_data))
		.addNoHighLightGuidView(R.drawable.ic_ok).addHighLightGuidView(pager, 0,0.5f,HighLightGuideView.VIEWSTYLE_CIRCLE)
		.addHighLightGuidView(bodywater_tv, 0,10f,HighLightGuideView.VIEWSTYLE_OVAL)
		.setTouchOutsideDismiss(false).setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				if (null == UtilConstants.su) {
					UtilConstants.su = new SharedPreferencesUtil(KitchenScaleActivity.this);
				}
				UtilConstants.su.editSharedPreferences("lefuconfig", "first_install_kitchen_scale", "1");
				UtilConstants.FIRST_INSTALL_KITCHEN_SCALE = "1";

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
		setContentView(R.layout.activity_kitchen);
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
		//linearLayout77.setVisibility(View.GONE);

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
			intent.setClass(KitchenScaleActivity.this, RecordKitchenListActivity.class);
			if (null != v.getTag()) {
				intent.putExtra("type", (int) ((Integer) v.getTag()));
			}
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivityForResult(intent, 0);
		}
	};

	/** 初始化视图 */
	private void initView() {
		try {
			uservice = new UserService(this);
			weight_textView17 = (TextView) this.findViewById(R.id.weight_textView17);
			norecord_tv = (TextView) this.findViewById(R.id.norecord_textView);
			username_tv = (TextView) this.findViewById(R.id.username_tv);
			milkname_tx = (TextView) this.findViewById(R.id.milkname_tx);
			//targetv = (MyTextView2) this.findViewById(R.id.targetv);

			/* 称单位 */
			if (null != UtilConstants.su) {
				UtilConstants.CHOICE_KG = (String) UtilConstants.su.readbackUp("lefuconfig", "unit", UtilConstants.UNIT_KG);
			}

//			if (null != UtilConstants.CURRENT_USER) {
//				compare_tv.setTexts("0.0" + UtilConstants.CURRENT_USER.getDanwei(), null);
//			}

			/* 文本内容 */
			bodywater_tv = (TextView) this.findViewById(R.id.textView2);
			bodyfat_tv = (TextView) this.findViewById(R.id.textView4);
			bone_tv = (TextView) this.findViewById(R.id.textView6);
			bmi_tv = (TextView) this.findViewById(R.id.textView14);
			visal_tv = (TextView) this.findViewById(R.id.textView10);
			musle_tv= (TextView) this.findViewById(R.id.textView8);
			bmr_tv = (TextView) this.findViewById(R.id.textView12);
			time_tv = (TextView) this.findViewById(R.id.textView19);
			physicage_tv = (TextView) this.findViewById(R.id.textView74);
			//tvBmi = (TextView) this.findViewById(R.id.tv_guide_value);
			scale_connect_state = (TextView) this.findViewById(R.id.scale_connect_state);
			
			food_name  = (TextView) this.findViewById(R.id.food_name);
			search_et = (KMPAutoComplTextView) this.findViewById(R.id.search_et);
			search_tv = (TextView) this.findViewById(R.id.search_tv);
			save_tv = (TextView) this.findViewById(R.id.save_tv);
			/* 按钮 */
			infoImg = (Button) this.findViewById(R.id.info_img_btn);
			setingImg = (Button) this.findViewById(R.id.seting_img_btn);
			deletdImg = (Button) this.findViewById(R.id.delete_img_btn);
			headImg = (ImageView) this.findViewById(R.id.user_header);
			intentImg = (ImageView) this.findViewById(R.id.imageView1);
			leftImg = (ImageView) this.findViewById(R.id.imageView2);
			rightImg = (ImageView) this.findViewById(R.id.imageView3);
			unit_btn = (Button) this.findViewById(R.id.unit_btn);
			/* 监听 */
			infoImg.setOnClickListener((android.view.View.OnClickListener) menuBtnOnClickListener);
			deletdImg.setOnClickListener((android.view.View.OnClickListener) menuBtnOnClickListener);
			setingImg.setOnClickListener((android.view.View.OnClickListener) menuBtnOnClickListener);
			headImg.setOnClickListener((android.view.View.OnClickListener) btnOnClickListener);
			intentImg.setOnClickListener((android.view.View.OnClickListener) btnOnClickListener);
			search_tv.setOnClickListener((android.view.View.OnClickListener) btnOnClickListener);
			save_tv.setOnClickListener((android.view.View.OnClickListener) btnOnClickListener);
			unit_btn.setOnClickListener((android.view.View.OnClickListener) btnOnClickListener);
			
			search_et.setDatas(CacheHelper.nutrientTempNameList);
			search_et.inputTextNull(food_name,selectNutrient);
			/*设置自动搜索提示*/
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					if(null!=CacheHelper.nutrientNameList && CacheHelper.nutrientNameList.size()>0)search_et.setDatas(CacheHelper.nutrientNameList);
				}
			}, 5000);
			search_et.setOnPopupItemClickListener(new KMPAutoComplTextView.OnPopupItemClickListener() {
	            @Override
	            public void onPopupItemClick(CharSequence charSequence) {
	                //查找
	            	if(TextUtils.isEmpty(charSequence)){
						return ;
					}
					NutrientBo nutrient = CacheHelper.queryNutrientsByName(charSequence.toString());
					if(null==nutrient){
						Toast.makeText(KitchenScaleActivity.this, "No Data had been found", Toast.LENGTH_SHORT).show();
						return ;
					}
					search_et.setText(nutrient.getNutrientDesc());
					selectNutrient = nutrient;
					natureSelectComplete(nutrient);
	            }
	        });
			 search_et.setOnTouchListener(new View.OnTouchListener() {
		            @Override
		            public boolean onTouch(View v, MotionEvent event) {
		 
		                if (search_et.getCompoundDrawables()[2] == null)
		                    return false;
		 
		                if (event.getAction() != MotionEvent.ACTION_UP)
		                    return false;
		 
		                //触摸点位置判断
		                if (event.getX() > search_et.getWidth() - search_et.getPaddingRight() - getResources().getDimension(R.dimen.activity_horizontal_margin)){
		                	search_et.setText("");
		                }
		 
		                return false;
		            }
		        });
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
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		
	}

	/** 设置视图数据 */
	private void setViews() {
		/* 当前用户不为空 */
		if (UtilConstants.CURRENT_USER != null) {
			/* 用户姓名 */
			this.username_tv.setText(UtilConstants.CURRENT_USER.getUserName());
			
			/* 设置用户头像 */
			if (null != UtilConstants.CURRENT_USER.getPer_photo() && !"".equals(UtilConstants.CURRENT_USER.getPer_photo()) && !UtilConstants.CURRENT_USER.getPer_photo().equals("null")) {
				Bitmap bitmap = imageUtil.getBitmapfromPath(UtilConstants.CURRENT_USER.getPer_photo());
				headImg.setImageBitmap(bitmap);
			}
		}

		/* 是否有测量记录 */
		if (curRecord != null) {
			Date iDate = UtilTooth.stringToTime(curRecord.getRecordTime());
			if (iDate != null) {
				Locale local = Locale.getDefault();
				time_tv.setText(StringUtils.getDateString(iDate, 5));
			}
			bodywater_tv.setText(UtilTooth.keep2Point(curRecord.getRbodywater()) + "kcal"); //卡路里
			bodyfat_tv.setText(UtilTooth.keep2Point(curRecord.getRbodyfat()) + "g");//蛋白质
			bone_tv.setText(UtilTooth.keep2Point(curRecord.getRbone()) + "g"); //脂肪
			musle_tv.setText(UtilTooth.keep2Point(curRecord.getRmuscle()) + "g");//碳水化合物
			visal_tv.setText(UtilTooth.keep2Point(curRecord.getRvisceralfat()) + "g");//纤维
			bmr_tv.setText(UtilTooth.keep2Point(curRecord.getRbmi()) + "mg");//胆固醇
			bmi_tv.setText(UtilTooth.keep2Point(curRecord.getRbmr()) + "mg");//维生素C
			physicage_tv.setText(UtilTooth.keep2Point(curRecord.getBodyAge()) + "mg");//钙
			
			if(null!=UtilConstants.CURRENT_USER && UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_ML2)){
				milkname_tx.setVisibility(View.VISIBLE);
			}else{
				milkname_tx.setVisibility(View.GONE);
			}
		} else {
			/* 无测量记录 */
			bodywater_tv.setText("0.0kcal");
			bodyfat_tv.setText("0.0g");
			bone_tv.setText("0.0g");
			musle_tv.setText("0.0g");
			visal_tv.setText("0.0g");
			bmi_tv.setText("0.0mg");
			bmr_tv.setText("0.0mg");
			//tvBmi.setText("0.0g");
			physicage_tv.setText("0.0mg");
			
		}
		
		CheckBox cb = (CheckBox)this.findViewById(R.id.cb_scan_device);
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.i(TAG, "click to start scan");
				if (mBluetoothAdapter.isEnabled() && singleton != null) {
					Log.i(TAG, "start scan");
					singleton.scanLeDevice(false, KitchenScaleActivity.this, mServiceConnection);
					
					if(mBluetoothLeService != null){
						mBluetoothLeService.disconnect();
					}
					
					singleton.scanLeDevice(true, KitchenScaleActivity.this, mServiceConnection);
				}
			}
		});
	}

	/** 菜单按钮监听 */
	android.view.View.OnClickListener menuBtnOnClickListener = new android.view.View.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.info_img_btn :
					/* 跳转info界面 */
					if(UtilConstants.CURRENT_SCALE.equals(UtilConstants.KITCHEN_SCALE)){
						Intent intent0 = new Intent();
						intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						if(null!=selectNutrient)intent0.putExtra("selectNutrient", selectNutrient);
						intent0.setClass(KitchenScaleActivity.this, KitchenInfoActivity.class);
						KitchenScaleActivity.this.startActivity(intent0);
					}else{
						Intent intent0 = new Intent();
						intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent0.setClass(KitchenScaleActivity.this, InfoActivity.class);
						KitchenScaleActivity.this.startActivity(intent0);
					}
					break;
				case R.id.seting_img_btn :
					/* 跳转设置界面 */
					Intent intent = new Intent();
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setClass(KitchenScaleActivity.this, SettingActivity.class);
					KitchenScaleActivity.this.startActivity(intent);
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
					intent1.setClass(KitchenScaleActivity.this, UserListActivity.class);
					KitchenScaleActivity.this.startActivity(intent1);
					break;
				case R.id.imageView1 :
					/* 跳转到链接 */
//					Intent intent2 = new Intent();
//					intent2.setData(Uri.parse(UtilConstants.homeUrl));
//					intent2.setAction(Intent.ACTION_VIEW);
//					KitchenScaleActivity.this.startActivity(intent2);
					break;
				case R.id.search_tv :
					/* 搜索 */
					if(TextUtils.isEmpty(search_et.getText().toString())){
						Toast.makeText(KitchenScaleActivity.this, "Please input something", Toast.LENGTH_SHORT).show();
						return ;
					}
					ArrayList<NutrientBo> nutrientList = CacheHelper.findNutrientByName(search_et.getText().toString());
					if(null==nutrientList || nutrientList.size()==0){
						Toast.makeText(KitchenScaleActivity.this, "No Data had been found", Toast.LENGTH_SHORT).show();
						return ;
					}
					
					FragmentManager manager = getFragmentManager();
					MyDialogFragment dialog = MyDialogFragment.newInstance(nutrientList);
					dialog.show(manager, "dialog");
					break;
				case R.id.save_tv:
					if (curRecord != null && null!=selectNutrient) {
						try {
							List<Records> ls = recordService.findRecordsByScaleTypeAndFoodNameAndKg(UtilConstants.KITCHEN_SCALE, selectNutrient.getNutrientDesc(), curRecord.getRweight());
							if(null!=ls && ls.size()>0){
								
							}else{
								curRecord.setRphoto(selectNutrient.getNutrientDesc());
								if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_FATLB)) {
									curRecord.setUnitType(3);
								} else if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_LB)) {
									curRecord.setUnitType(1);
								}else if(UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_ML)){
									curRecord.setUnitType(2);
								} else if(UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_ML2)){
									curRecord.setUnitType(4);
								}else if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_KG) || UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_G))  {
									curRecord.setUnitType(0);
								}
								if(!TextUtils.isEmpty(food_name.getText().toString()))selectNutrient.setNutrientDesc(food_name.getText().toString());
								curRecord = RecordDao.handleKitchenData(recordService,curRecord,selectNutrient);
								/* 重新设置当前记录 */
								CacheHelper.recordListDesc = recordService.getAllDatasByScaleAndIDDesc(UtilConstants.CURRENT_SCALE, ItemID, UtilConstants.CURRENT_USER.getBheigth());
								
								/* 刷新界面数据 */
								handler.sendEmptyMessage(0);
								
								//food_name.setText("");
							}
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} 
					break;
				case R.id.unit_btn:
					try {
						if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_FATLB)) {
							UtilConstants.CURRENT_USER.setDanwei(UtilConstants.UNIT_G);
						} else if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_LB)) {
							UtilConstants.CURRENT_USER.setDanwei(UtilConstants.UNIT_FATLB);
						}else if(UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_ML)){
							UtilConstants.CURRENT_USER.setDanwei(UtilConstants.UNIT_LB);
						} else if(UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_ML2)){
							UtilConstants.CURRENT_USER.setDanwei(UtilConstants.UNIT_ML);
						} else if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_KG) || UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_G))  {
							UtilConstants.CURRENT_USER.setDanwei(UtilConstants.UNIT_ML2);
						}
						handler.sendEmptyMessage(0);
						uservice.update(UtilConstants.CURRENT_USER);
					} catch (Exception e) {
						// TODO: handle exception
					}
					
					break;
			}
		}
	};

	/** 弹窗 */
	protected void dialog(String title, final int id) {
		AlertDialog.Builder builder = new Builder(KitchenScaleActivity.this);
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
			intent1.setClass(KitchenScaleActivity.this, ReCheckActivity.class);
			KitchenScaleActivity.this.startActivity(intent1);
			exit();
			ExitApplication.getInstance().exit(this);
		} else {
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
		/* 非当前Activity */
		isCurrentActivoty = false;

		stopScanService();
		super.onPause();
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
			if(null==pager)pager = (ViewPager) this.findViewById(R.id.weight_contains);
			adapter = new MyPageAdapter(views);
			pager.setAdapter(adapter);
			adapter.notifyDataSetChanged();

			pager.setCurrentItem(1);
		} else {
			leftImg.setImageDrawable(getResources().getDrawable(R.drawable.left_to));
			rightImg.setImageDrawable(getResources().getDrawable(R.drawable.right_to));
			weight_textView17.setVisibility(View.GONE);
			rightImg.setVisibility(View.INVISIBLE);
			leftImg.setVisibility(View.INVISIBLE);
			norecord_tv.setVisibility(View.INVISIBLE);
			View view0 = mLi.inflate(R.layout.nullwhats, null);
			views.add(view0);
			if(null==pager)pager = (ViewPager) this.findViewById(R.id.weight_contains);
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
			convertView = inflater.inflate(R.layout.food_weight_pager, null);
			tvDetail_weight_title = (MyTextView) convertView.findViewById(R.id.weight_textView);
			unit_tv = (TextView) convertView.findViewById(R.id.unti_tv);
			convertView.setTag(record);
			convertView.setOnClickListener(imgOnClickListener);
		}
		if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_FATLB)) {
			tvDetail_weight_title.setTexts(UtilTooth.kgToFloz(record.getRweight()), null);
			if (null != unit_tv) {
				unit_tv.setText(this.getText(R.string.oz_danwei2));
			}
		} else if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_LB)) {
			tvDetail_weight_title.setTexts(UtilTooth.kgToLBoz(record.getRweight()), null);
			if (null != unit_tv) {
				unit_tv.setText(this.getText(R.string.lboz_danwei));
			}
		}else if(UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_ML2)){
			tvDetail_weight_title.setTexts(UtilTooth.keep0Point(UtilTooth.kgToML(record.getRweight()))+ "", null);
			if (null != unit_tv) {
				unit_tv.setText(this.getText(R.string.ml_danwei2));
			}
		}else if(UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_ML)){
			tvDetail_weight_title.setTexts(UtilTooth.keep0Point(record.getRweight()) + "", null);
			if (null != unit_tv) {
				unit_tv.setText(this.getText(R.string.ml_danwei));
			}
		} else {
			tvDetail_weight_title.setTexts(UtilTooth.keep0Point(record.getRweight()) + "", null);
			if (null != unit_tv) {
				unit_tv.setText(this.getText(R.string.g_danwei));
			}
		}
		return convertView;
	}

	/** 分页框点击事件 */
	android.view.View.OnClickListener imgOnClickListener = new android.view.View.OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(KitchenScaleActivity.this, RecordKitchenListActivity.class);
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
		if (ItemID != 0) {
			try {
				UtilConstants.CURRENT_USER = this.uservice.find(ItemID);
				CacheHelper.recordListDesc = this.recordService.getAllDatasByScaleAndIDDesc(UtilConstants.CURRENT_SCALE, ItemID, 167f);
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
					/* 更新显示内容 */
					setViews();
					/* 更新分页内容 */
					Init(CacheHelper.recordListDesc);
					if (TextUtils.isEmpty(UtilConstants.FIRST_INSTALL_KITCHEN_SCALE) && CacheHelper.recordListDesc.size()>0) {
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
					ExitApplication.getInstance().exit(KitchenScaleActivity.this);
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
					ExitApplication.getInstance().exit(KitchenScaleActivity.this);
					Intent intent = new Intent();
					intent.setClass(KitchenScaleActivity.this, LoadingActivity.class);
					KitchenScaleActivity.this.startActivity(intent);
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
			/* 开机BT循环扫描线程 */
			new Thread(ScanRunnable).start();
			/* 连接状态 */
			TimeService.scale_connect_state = scale_connect_state;
		}
		if (BluetoolUtil.bleflag && !receiverReleased) {
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

	/** 退出 */
	private void exit() {
		/* 停止服务 */
		stopScanService();

		NotificationManager notificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(0);
		if (null != LoadingActivity.mainActivty) {
			LoadingActivity.mainActivty.finish();
		}

		KitchenScaleActivity.this.finish();
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
			isServiceReg = false;
		}
	};
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BLEConstant.ACTION_GATT_CONNECTED.equals(action)) {
				/* 连接状态修改 */
				scale_connect_state.setText(R.string.scale_connect_state_connected);
				singleton.setmConnected(true);
			} else if (BLEConstant.ACTION_GATT_DISCONNECTED.equals(action)) {
				/* 连接状态修改 */
				scale_connect_state.setText(R.string.scale_connect_state_not_connected);
				if (!isNotOpenBL) {
					if (singleton.getmConnected()) {
						singleton.setmConnected(false);

						if (mBluetoothLeService != null) {
							final boolean result = mBluetoothLeService.connect(BluetoolUtil.mDeviceAddress);
							Log.d(TAG, "Connect request result=" + result);
						}

						singleton.scanLeDevice(true, KitchenScaleActivity.this, mServiceConnection);
					}
				}
			} else if (BLEConstant.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				sendCodeCount = 0;
				/* 连接状态修改 */
				scale_connect_state.setText(R.string.scale_connect_state_connected);
				/* 检测到数据 */
				AppData.hasCheckData = true;
				Toast.makeText(KitchenScaleActivity.this, getString(R.string.scale_paired_success), Toast.LENGTH_LONG).show();
				//Toast.makeText(KitchenScaleActivity.this, getString(R.string.scale_paired_success), Toast.LENGTH_LONG).show();
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
				System.out.println("读取到数据：" + readMessage);
				try {
					if(TextUtils.isEmpty(readMessage)) return;
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
								if (sendCodeCount < 1) {
									if (mBluetoothLeService != null)
										send_Data(MyUtil.getUserInfo());
									sendCodeCount++;
								} else {
									Toast.makeText(KitchenScaleActivity.this, getString(R.string.user_data_error), Toast.LENGTH_LONG).show();
									//Toast.makeText(KitchenScaleActivity.this, getString(R.string.user_data_error), Toast.LENGTH_LONG).show();
								}
							} else if (readMessage.equals(UtilConstants.ERROR_CODE_TEST)) {
								Toast.makeText(KitchenScaleActivity.this, getString(R.string.scale_measurement_error), Toast.LENGTH_SHORT).show();
							}
							/* 判断称类型是否错误 */
							if (readMessage.startsWith(UtilConstants.KITCHEN_SCALE)) {
								dueDate(readMessage,0);
							}else  {
								if (readMessage.length() > 31){
									/* 跳转到制定的秤界面 */
									if (readMessage.startsWith(UtilConstants.BATHROOM_SCALE)) {
										/* 人体秤 */
										UtilConstants.CURRENT_SCALE = UtilConstants.BATHROOM_SCALE;
										UtilConstants.CURRENT_USER.setScaleType(UtilConstants.CURRENT_SCALE);
									} else if (readMessage.startsWith(UtilConstants.BABY_SCALE)) {
										/* 婴儿秤 */
										UtilConstants.CURRENT_SCALE = UtilConstants.BABY_SCALE;
										UtilConstants.CURRENT_USER.setScaleType(UtilConstants.CURRENT_SCALE);
									}else if(readMessage.startsWith(UtilConstants.BODY_SCALE)){
										/*脂肪秤*/
										UtilConstants.CURRENT_SCALE=UtilConstants.BODY_SCALE;
										UtilConstants.CURRENT_USER.setScaleType(UtilConstants.CURRENT_SCALE);
									}
									UtilConstants.CURRENT_USER.setScaleType(UtilConstants.CURRENT_SCALE);
									/* 保存测量数据 */
									RecordDao.dueDate(recordService, readMessage);
									handler.sendEmptyMessage(UtilConstants.scaleChangeMessage);
								}else if (readMessage.equals(UtilConstants.ERROR_CODE_GETDATE)) {
									openErrorDiolg("2");
								}
							}
						}
					}
				} catch (Exception e) {
					Log.e(TAG, "接受数据解析异常==>"+e.getMessage());
				}
			}
		}
	};

	/** 处理数据 */
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
			if (recod != null && null != recod.getScaleType() && recod.getScaleType().equalsIgnoreCase(UtilConstants.CURRENT_SCALE)) {
				if(TextUtils.isEmpty(food_name.getText())){
					selectNutrient = null;
				}else{
					if(null!=selectNutrient)recod.setRphoto(selectNutrient.getNutrientDesc());
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
	
	/**
	 * 搜索完后，保存数据
	 */
	public void saveSearchDate(){
	}
	
	/**选择了营养类型**/
	@Override
	public void natureSelectComplete(NutrientBo nutrient) {
		if(null!=nutrient){
			//重绘界面参数
			food_name.setText(nutrient.getNutrientDesc());
			selectNutrient = nutrient;
			if(null!=curRecord){
				float calcium =  0.0f;
				if(!TextUtils.isEmpty(nutrient.getNutrientVitB6()) && Tool.isDigitsOnly(nutrient.getNutrientVitB6())){
					calcium =  Float.parseFloat(nutrient.getNutrientVitB6())*curRecord.getRweight()*0.01f;//钙
				}
				float protein =  0.0f;
				if(!TextUtils.isEmpty(nutrient.getNutrientFiberTD()) && Tool.isDigitsOnly(nutrient.getNutrientFiberTD())){
					protein = Float.parseFloat(nutrient.getNutrientFiberTD())*curRecord.getRweight()*0.01f;//蛋白质
				}
				float kcal = 0.0f;
				if(!TextUtils.isEmpty(nutrient.getNutrientAsh()) && Tool.isDigitsOnly(nutrient.getNutrientAsh())){
					kcal =  Float.parseFloat(nutrient.getNutrientAsh())*curRecord.getRweight()*0.01f;//卡路里
				}
				float water =  0.0f;
				if(!TextUtils.isEmpty(nutrient.getNutrientCalcium()) && Tool.isDigitsOnly(nutrient.getNutrientCalcium())){
					water =  Float.parseFloat(nutrient.getNutrientCalcium())*curRecord.getRweight()*0.01f;//碳水化合物
				}
				float fat =  0.0f;
				if(!TextUtils.isEmpty(nutrient.getNutrientSugarTot()) && Tool.isDigitsOnly(nutrient.getNutrientSugarTot())){
					fat =  Float.parseFloat(nutrient.getNutrientSugarTot())*curRecord.getRweight()*0.01f;//脂肪
				}
				float fibre =  0.0f;
				if(!TextUtils.isEmpty(nutrient.getNutrientIron()) && Tool.isDigitsOnly(nutrient.getNutrientIron())){
					fibre =  Float.parseFloat(nutrient.getNutrientIron())*curRecord.getRweight()*0.01f;//纤维
				}
				float cholesterol =  0.0f;
				if(!TextUtils.isEmpty(nutrient.getNutrientMagnesium()) && Tool.isDigitsOnly(nutrient.getNutrientMagnesium())){
					cholesterol =  Float.parseFloat(nutrient.getNutrientMagnesium())*curRecord.getRweight()*0.01f;//胆固醇
				}
				float vitaminC =  0.0f;
				if(!TextUtils.isEmpty(nutrient.getNutrientPotassium()) && Tool.isDigitsOnly(nutrient.getNutrientPotassium())){
					vitaminC =  Float.parseFloat(nutrient.getNutrientPotassium())*curRecord.getRweight()*0.01f;//维生素C
				}

				bodywater_tv.setText(UtilTooth.keep2Point(kcal) + "kcal"); //卡路里
				bodyfat_tv.setText(UtilTooth.keep2Point(protein) + "g");//蛋白质
				bone_tv.setText(UtilTooth.keep2Point(fat) + "g"); //脂肪
				musle_tv.setText(UtilTooth.keep2Point(water) + "g");//碳水化合物
				visal_tv.setText(UtilTooth.keep2Point(fibre) + "g");//纤维
				bmr_tv.setText(UtilTooth.keep2Point(cholesterol) + "mg");//胆固醇
				bmi_tv.setText(UtilTooth.keep2Point(vitaminC) + "mg");//维生素C
				physicage_tv.setText(UtilTooth.keep2Point(calcium) + "mg");//钙
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
		System.out.println("BT开始扫描...");
		// Register for broadcasts when a device is discovered
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(mReceiver, filter);

		// Register for broadcasts when discovery has finished
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(mReceiver, filter);

		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		mBtAdapter.startDiscovery();
	}

	/** 停止扫描 */
	public void stopDiscovery() {
		mBtAdapter.cancelDiscovery();
		KitchenScaleActivity.this.unregisterReceiver(mReceiver);
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
