package com.lefu.es.system;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.TimeChart.DateChangeCallback;
import org.achartengine.chart.XYChart;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.greenrobot.eventbus.EventBus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lefu.es.adapter.RecordDetailAdaptor;
import com.lefu.es.cache.CacheHelper;
import com.lefu.es.constant.UtilConstants;
import com.lefu.es.constant.imageUtil;
import com.lefu.es.entity.Records;
import com.lefu.es.entity.UserModel;
import com.lefu.es.event.NoRecordsEvent;
import com.lefu.es.service.ExitApplication;
import com.lefu.es.service.RecordService;
import com.lefu.es.util.Image;
import com.lefu.es.util.MyUtil;
import com.lefu.es.util.SharedPreferencesUtil;
import com.lefu.es.util.StringUtils;
import com.lefu.es.util.UtilTooth;
import com.lefu.es.view.GuideView;
import com.lefu.es.view.guideview.HighLightGuideView;
import com.lefu.es.view.guideview.HighLightGuideView.OnDismissListener;
import com.lefu.iwellness.newes.cn.system.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.lefu.iwellness.newes.cn.system.R.drawable.baby;

/**
 * 测量记录列表
 * 
 * @author Leon 2015-11-19
 */
@SuppressLint("SimpleDateFormat")
public class RecordListActivity extends Activity implements android.view.View.OnClickListener, DateChangeCallback {
	private static final String TAG = "RecordListActivity";
	
	private TextView back_tv;
	private TextView graph_tv;
	private TextView list_tv;
	private TextView username_tv;

	private LinearLayout linebg;
	private LinearLayout charcontainer;
	private LinearLayout delist;

	private ImageView deleteImg;
	private ImageView delallImg;
	private SimpleDraweeView headImage;

	private ImageView shareImage;

	private LinearLayout chartContainer;
	private Records lastRecod;

	private int type = 0;
	private int recordid = 0;
	private GraphicalView mChart;
	private Handler handler;
	private int selectedPosition = -1;
	private int dateType = 5;

	private RecordDetailAdaptor recordAdaptor;
	private ListView lv;

	private RecordService recordService;

	@Bind(R.id.body_menu_ly)
	LinearLayout menuLy;

	@Bind(R.id.weight_menu)
	RadioButton weightMenu;

	@Bind(R.id.water_menu)
	RadioButton waterMenu;

	@Bind(R.id.fat_menu)
	RadioButton fatMenu;

	@Bind(R.id.bone_menu)
	RadioButton boneMenu;

	@Bind(R.id.bmi_menu)
	RadioButton bmiMenu;

	@Bind(R.id.visfat_menu)
	RadioButton visfatMenu;

	@Bind(R.id.bmr_menu)
	RadioButton bmrMenu;

	@Bind(R.id.muscial_menu)
	RadioButton muscialMenu;

	protected UserModel user = null; //选择的婴儿


	/**
	 * 构建实例
	 * @param context
	 * @param user
	 * @return
	 */
	public static Intent creatIntent(Context context,UserModel user){
		Intent intent = new Intent(context,RecordListActivity.class);
		intent.putExtra("user",user);
		return intent;
	}
	
	private void showTipMask() {
		HighLightGuideView.builder(this)
		.setText(getString(R.string.click_see_list))
		.addNoHighLightGuidView(R.drawable.ic_ok).addHighLightGuidView(list_tv, 0, 1, HighLightGuideView.VIEWSTYLE_CIRCLE).setTouchOutsideDismiss(false).setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				if (null == UtilConstants.su) {
					UtilConstants.su = new SharedPreferencesUtil(RecordListActivity.this);
				}
				
				UtilConstants.su.editSharedPreferences("lefuconfig", "first_install_detail", "1");
				UtilConstants.FIRST_INSTALL_DETAIL = "1";
				
			}
		}).show();
	}

	private void showTipMask2() {
		HighLightGuideView.builder(this)
		.setText(getString(R.string.click_see_share))
		.addNoHighLightGuidView(R.drawable.ic_ok).addHighLightGuidView(shareImage, 0,1,HighLightGuideView.VIEWSTYLE_CIRCLE).setTouchOutsideDismiss(false).setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				if (null == UtilConstants.su) {
					UtilConstants.su = new SharedPreferencesUtil(RecordListActivity.this);
				}
				UtilConstants.su.editSharedPreferences("lefuconfig", "first_install_share", "1");
				UtilConstants.FIRST_INSTALL_SHARE = "1";
				
			}
		}).show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_new);
		ButterKnife.bind(this);
		Serializable serializable = getIntent().getSerializableExtra("baby");
		if(null==serializable){
			Toast.makeText(RecordListActivity.this, getString(R.string.choice_a_user), Toast.LENGTH_LONG).show();
			finish();
		}else{
			user = (UserModel)serializable;
			//只有脂肪秤才显示
			if(UtilConstants.CURRENT_SCALE.equals(UtilConstants.BODY_SCALE)){
				menuLy.setVisibility(View.VISIBLE);
			}else{
				menuLy.setVisibility(View.GONE);
			}
			handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					switch (msg.what) {
						case 1 :
							break;
						case 2 :
							if (lv != null && recordAdaptor != null && recordAdaptor.selectedPosition >= 0) {
								lv.setSelection(recordAdaptor.selectedPosition);
							}
							break;
					}
					super.handleMessage(msg);
				}

			};
			type = UtilConstants.WEIGHT_SINGLE;
			recordid = 0;
			initView();
		}
	}

	private void initView() {
		recordService = new RecordService(this);
		username_tv = (TextView) this.findViewById(R.id.user_name_tv);
		headImage = (SimpleDraweeView) this.findViewById(R.id.reviseHead);
		if (null != user) {
			username_tv.setText(user.getUserName());
			if (null != user.getPer_photo() && !"".equals(user.getPer_photo()) && !user.getPer_photo().equals("null")) {
				headImage.setImageURI(Uri.fromFile(new File(user.getPer_photo())));
			}
		}
		back_tv = (TextView) this.findViewById(R.id.back_textView);
		back_tv.setOnClickListener((android.view.View.OnClickListener) imgOnClickListener);
		graph_tv = (TextView) this.findViewById(R.id.graph_textview);
		list_tv = (TextView) this.findViewById(R.id.list_textview);
		graph_tv.setOnClickListener((android.view.View.OnClickListener) imgOnClickListener);
		list_tv.setOnClickListener((android.view.View.OnClickListener) imgOnClickListener);
		linebg = (LinearLayout) this.findViewById(R.id.line_bg);
		charcontainer = (LinearLayout) this.findViewById(R.id.chart_container);
		delist = (LinearLayout) this.findViewById(R.id.rl_delist_top);

		deleteImg = (ImageView) this.findViewById(R.id.del_img_btn);
		delallImg = (ImageView) this.findViewById(R.id.delall_img_btn);
		shareImage = (ImageView) this.findViewById(R.id.share_img);
		deleteImg.setOnClickListener((android.view.View.OnClickListener) imgOnClickListener);
		delallImg.setOnClickListener((android.view.View.OnClickListener) imgOnClickListener);
		shareImage.setOnClickListener((android.view.View.OnClickListener) imgOnClickListener);

		lv = (ListView) findViewById(R.id.detailist_contains);
		lv.setOnItemClickListener(itemClickListener);
		lv.setSelector(R.drawable.listview_bg);

		chartContainer = (LinearLayout) findViewById(R.id.chart_container);
		loadData();
		
		if (TextUtils.isEmpty(UtilConstants.FIRST_INSTALL_DETAIL) && null!=CacheHelper.recordListDesc && CacheHelper.recordListDesc.size() == 1) {
			showTipMask();
		}
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


	private OnItemClickListener itemClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v, int arg2, long arg3) {
			selectedPosition = arg2;
			ListView listV = (ListView) parent;
			lastRecod = (Records) listV.getItemAtPosition(arg2);
			mChart.setSeriesSelection(getchartSelectIndex(lastRecod.getId()));
			mChart.invalidate();
			recordAdaptor.setSelectedPosition(arg2);
			recordAdaptor.notifyDataSetInvalidated();

			handler.sendEmptyMessage(1);
			Intent intent = new Intent();
			intent.setClass(RecordListActivity.this, RecordListItemActivity.class);
			Bundle mBundle = new Bundle();
			mBundle.putSerializable("record", lastRecod);
			intent.putExtras(mBundle);
			startActivity(intent);
		}
	};

	private int getchartSelectIndex(int id) {
		int i = 0;
		if (null != CacheHelper.recordListDesc && CacheHelper.recordListDesc.size() > 0) {
			for (Records re : CacheHelper.recordListDesc) {
				if (re.getId() == id) {
					break;
				}
				i++;
			}
		}
		return i;
	}

	@OnClick(R.id.weight_menu)
	public void weightMenuClick(){
		type = UtilConstants.WEIGHT_SINGLE;
		recordid = 0;
		initChart();
	}

	@OnClick(R.id.water_menu)
	public void waterMenuClick(){
		type = UtilConstants.BODYWATER_SINGLE;
		recordid = 0;
		initChart();
	}

	@OnClick(R.id.fat_menu)
	public void fatMenuClick(){
		type = UtilConstants.WEIGHT_SINGLE;
		recordid = 0;
		initChart();
	}

	@OnClick(R.id.bone_menu)
	public void boneMenuClick(){
		type = UtilConstants.BONE_SINGLE;
		recordid = 0;
		initChart();
	}

	@OnClick(R.id.bmi_menu)
	public void bmiMenuClick(){
		type = UtilConstants.BMI_SINGLE;
		recordid = 0;
		initChart();
	}

	@OnClick(R.id.visfat_menu)
	public void visfatMenuClick(){
		type = UtilConstants.VISCALEFAT_SINGLE;
		recordid = 0;
		initChart();
	}

	@OnClick(R.id.bmr_menu)
	public void bmrMenuClick(){
		type = UtilConstants.BMR_SINGLE;
		recordid = 0;
		initChart();
	}

	@OnClick(R.id.muscial_menu)
	public void muscialMenuClick(){
		type = UtilConstants.MUSCALE_SINGLE;
		recordid = 0;
		initChart();
	}
	
	private void loadData() {
		try {
			if (null != user) {
				//[Records [scaleType=cf, ugroup=P1, recordTime=2016-09-20 00:02:55, compareRecord=-33.0, rweight=27.0, rbmi=0.0, rbone=1.0, rbodyfat=5.0, rmuscle=24.4, rbodywater=85.0, rvisceralfat=1.0, rbmr=1457.0, level=null, sex=null, sweight=null, sbmi=0, sbone=null, sbodyfat=null, smuscle=null, sbodywater=null, svisceralfat=null, sbmr=null, sHeight=null, sAge=null], Records [scaleType=cf, ugroup=P1, recordTime=2016-09-19 23:33:06, compareRecord=60.0, rweight=60.0, rbmi=20.8, rbone=2.8, rbodyfat=13.2, rmuscle=47.6, rbodywater=58.2, rvisceralfat=2.0, rbmr=1516.0, level=null, sex=null, sweight=null, sbmi=0, sbone=null, sbodyfat=null, smuscle=null, sbodywater=null, svisceralfat=null, sbmr=null, sHeight=null, sAge=null]]
				CacheHelper.recordListDesc = this.recordService.getAllDatasByScaleAndIDDesc(UtilConstants.CURRENT_SCALE, user.getId(), 167f);
				CacheHelper.recordList = this.recordService.getAllDatasByScaleAndIDAsc(UtilConstants.CURRENT_SCALE, user.getId(), 167f);
				initChart();
			}
		} catch (Exception e) {
			Log.e(TAG,"图标加载失败"+e.getMessage());
		}
	}


	private void initChart(){
		try {
				if (null != CacheHelper.recordList) {
					//[Records [scaleType=cf, ugroup=P1, recordTime=2016-09-20 00:02:55, compareRecord=-33.0, rweight=27.0, rbmi=9.3, rbone=1.0, rbodyfat=5.0, rmuscle=24.4, rbodywater=85.0, rvisceralfat=1.0, rbmr=1457.0, level=null, sex=null, sweight=null, sbmi=0, sbone=null, sbodyfat=null, smuscle=null, sbodywater=null, svisceralfat=null, sbmr=null, sHeight=null, sAge=null], Records [scaleType=cf, ugroup=P1, recordTime=2016-09-19 23:33:06, compareRecord=60.0, rweight=60.0, rbmi=20.8, rbone=2.8, rbodyfat=13.2, rmuscle=47.6, rbodywater=58.2, rvisceralfat=2.0, rbmr=1516.0, level=null, sex=null, sweight=null, sbmi=0, sbone=null, sbodyfat=null, smuscle=null, sbodywater=null, svisceralfat=null, sbmr=null, sHeight=null, sAge=null]]
					Date[] dt = new Date[CacheHelper.recordList.size()];
					double[] views = new double[CacheHelper.recordList.size()];
					Records recor = null;
					intiListView(CacheHelper.recordListDesc);
					if (null != CacheHelper.recordListDesc && CacheHelper.recordListDesc.size() > 0) {
						lastRecod = CacheHelper.recordListDesc.get(0);
					}
					for (int i = 0; i < CacheHelper.recordList.size(); i++) {
						recor = CacheHelper.recordList.get(i);
						Log.i(TAG, "record time: "+String.valueOf(recor.getRecordTime()));
						dt[i] = UtilTooth.stringToTime(recor.getRecordTime());
						if (type == UtilConstants.WEIGHT_SINGLE) {
							UtilConstants.isWeight = true;
						} else {
							UtilConstants.isWeight = false;
						}


						if (type == UtilConstants.WEIGHT_SINGLE) {

							if (recor.getScaleType().contains(UtilConstants.BABY_SCALE)) {
								views[i] = UtilTooth.myround2(recor.getRweight());
							} else {
								views[i] = recor.getRweight();
							}
						} else if (type == UtilConstants.BMI_SINGLE) {
							views[i] = recor.getRbmi();
						} else if (type == UtilConstants.BMR_SINGLE) {
							views[i] = UtilTooth.myround(recor.getRbmr());
						} else if (type == UtilConstants.BODYFAT_SINGLE) {
							views[i] = UtilTooth.myround(recor.getRbodyfat());
						} else if (type == UtilConstants.BODYWATER_SINGLE) {
							views[i] = UtilTooth.myround(recor.getRbodywater());
						} else if (type == UtilConstants.BONE_SINGLE) {
							if (user.getDanwei().equals(UtilConstants.UNIT_KG)) {
								views[i] = UtilTooth.myround(recor.getRbone());
							} else if (user.getDanwei().equals(UtilConstants.UNIT_LB) || user.getDanwei().equals(UtilConstants.UNIT_FATLB)) {
								views[i] = Double.parseDouble(UtilTooth.kgToLB(recor.getRbone()));
							} else if (user.getDanwei().equals(UtilConstants.UNIT_ST)) {
								views[i] = Double.parseDouble(UtilTooth.kgToLB(recor.getRbone()));
							} else {
								views[i] = UtilTooth.myround(recor.getRbone());
							}
						} else if (type == UtilConstants.MUSCALE_SINGLE) {
//					views[i] = UtilTooth.myround(recor.getRmuscle());
							if (user.getDanwei().equals(UtilConstants.UNIT_KG)) {
								views[i] = UtilTooth.myround(recor.getRmuscle());
							} else if (user.getDanwei().equals(UtilConstants.UNIT_LB) || user.getDanwei().equals(UtilConstants.UNIT_FATLB)) {
								views[i] = Double.parseDouble(UtilTooth.kgToLB(recor.getRmuscle()));
							} else if (user.getDanwei().equals(UtilConstants.UNIT_ST)) {
								views[i] = Double.parseDouble(UtilTooth.kgToLB(recor.getRmuscle()));
							} else {
								views[i] = UtilTooth.myround(recor.getRmuscle());
							}
						} else if (type == UtilConstants.VISCALEFAT_SINGLE) {
							views[i] = UtilTooth.myround(recor.getRvisceralfat());
						}

					}
					openChart(dt, views);
				}else{
					//
					chartContainer.removeAllViews();

					intiListView(null);
				}
		} catch (Exception e) {
			Log.e(TAG,"图标加载失败"+e.getMessage());
		}
	}



	private void intiListView(List<Records> list2) {
		if (null != list2 && list2.size() > 0) {
			recordAdaptor = new RecordDetailAdaptor(getApplicationContext(), list2, lv, R.layout.listview_item);
//			if (selectedPosition >= list2.size()) {
//				selectedPosition = list2.size() - 1;
//			}
			selectedPosition = 0;
			int i = 0;
			for (Records records : list2) {
				if (records.getId() == recordid) {
					selectedPosition = i;
					break;
				}
				//i++;
			}
			selectedPosition = i;
			recordAdaptor.setSelectedPosition(selectedPosition);
			lv.setAdapter(recordAdaptor);
			handler.sendEmptyMessage(2);
		} else {
			list2 = new ArrayList<Records>();
			selectedPosition = -1;
			recordAdaptor = new RecordDetailAdaptor(getApplicationContext(), list2, lv, R.layout.listview_item);
			lv.setAdapter(recordAdaptor);
		}
	}

	private void openChart(Date[] dt, double[] views) {
		if (null != dt && null != views) {
			TimeSeries viewsSeries = new TimeSeries("Views");
			double min = 500000, max = 0;
			long maxDate = 0;
			long minDate = 8888888888888888l;
			int weightType = 1;

			String scaleT = UtilConstants.CURRENT_SCALE;
			UtilConstants.CHOICE_KG = user.getDanwei();
			if (UtilConstants.CHOICE_KG.equals(UtilConstants.UNIT_KG)) {
				weightType = 1;
			} else if (UtilConstants.CHOICE_KG.equals(UtilConstants.UNIT_LB)) {
				if (scaleT.equals(UtilConstants.BODY_SCALE) || scaleT.equals(UtilConstants.BATHROOM_SCALE)) {
					weightType = 2;
				} else {
					weightType = 5;
				}
			} else if (UtilConstants.CHOICE_KG.equals(UtilConstants.UNIT_ST)) {
				if (scaleT.equals(UtilConstants.BODY_SCALE)) {
					weightType = 3;

				} else if (scaleT.equals(UtilConstants.BATHROOM_SCALE)) {
					weightType = 4;
				} else {
					weightType = 5;
				}
			} else if (UtilConstants.CHOICE_KG.equals(UtilConstants.UNIT_FATLB)) {
				if (scaleT.equals(UtilConstants.BODY_SCALE) || scaleT.equals(UtilConstants.BATHROOM_SCALE)) {
					weightType = 2;
				} else {
					weightType = 5;
				}
			}
			Log.i(TAG, "weight unit: "+String.valueOf(weightType));
			for (int i = 0; i < dt.length; i++) {
				long tempDate = dt[i].getTime();
				if (tempDate > maxDate) {
					maxDate = tempDate;
				}
				if (tempDate < minDate) {
					minDate = tempDate;
				}
				double vlu = UtilTooth.round(views[i], 2);
				float it = (float) views[i];
				if (UtilConstants.isWeight) {
					switch (weightType) {
						case 1 :
							if (UtilConstants.CURRENT_SCALE.equals(UtilConstants.BABY_SCALE)) {
								vlu = UtilTooth.round(views[i], 2);
							}
							break;
						case 2 :
							vlu = UtilTooth.round(UtilTooth.kgToLB_ForFatScale2(vlu), 2);
							break;
						case 3 :
							vlu = UtilTooth.kgToStLb_F((float) vlu);
							break;

						case 4 :
							vlu = UtilTooth.kgToStLb_B((float) vlu);
							break;
						case 5 :
							vlu = UtilTooth.lbToLBOZ_F(it);
							break;
					}
				}
				if (vlu > 0) {
					viewsSeries.add(dt[i], vlu);
					if (vlu > max) {
						max = vlu;
					}
					if (vlu < min) {
						min = vlu;
					}
				}
				
				Log.i(TAG, "vlu: "+vlu);
			}
			XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
			dataset.addSeries(viewsSeries);
			XYMultipleSeriesRenderer renderer = getDemoRenderer();
			if (min - 10 >= 0) {
				min = min - 10;
			} else {
				min = 0;
			}
			renderer.setYAxisMin(min);
			renderer.setYAxisMax(max + 10);

			long curTime = maxDate;
			double xMin = Double.parseDouble((curTime - (60 * 60 * 24 * 365)) + "");
			double xMax = Double.parseDouble((curTime + (60 * 60 * 24 * 365)) + "");
			renderer.setXAxisMin(xMin);
			renderer.setXAxisMax(xMax);
			
			switch (type) {
				case UtilConstants.WEIGHT_SINGLE :
					if (UtilConstants.UNIT_KG.equals(UtilConstants.CHOICE_KG)) {
						renderer.setYTitle(this.getText(R.string.Weightkg_cloun).toString());
					} else if (UtilConstants.UNIT_LB.equals(UtilConstants.CHOICE_KG)) {
						renderer.setYTitle(this.getText(R.string.Weightlb_cloun).toString());
					} else if (UtilConstants.UNIT_ST.equals(UtilConstants.CHOICE_KG)) {
						renderer.setYTitle(this.getText(R.string.Weightlb_cloun).toString());
					}else if (UtilConstants.UNIT_FATLB.equals(UtilConstants.CHOICE_KG)) {
						if(UtilConstants.CURRENT_SCALE.equals(UtilConstants.BABY_SCALE)){
							renderer.setYTitle(this.getText(R.string.Weightlboz_cloun).toString());
						}else{
							renderer.setYTitle(this.getText(R.string.Weightlb_cloun).toString());
						}
					}else {
					renderer.setYTitle(this.getText(R.string.Weightkg_cloun).toString());
				      }
					break;
				case UtilConstants.BMI_SINGLE :
					renderer.setYTitle(this.getText(R.string.bmi_cloun).toString());
					break;
				case UtilConstants.BMR_SINGLE :
					renderer.setYTitle(this.getText(R.string.bmr_cloun).toString());
					break;
				case UtilConstants.BODYFAT_SINGLE :
					renderer.setYTitle(this.getText(R.string.bodyfat_cloun).toString());
					break;
				case UtilConstants.BODYWATER_SINGLE :
					renderer.setYTitle("   " + this.getText(R.string.bodywater_cloun).toString());
					break;
				case UtilConstants.BONE_SINGLE :
					if (UtilConstants.CHOICE_KG.equals(UtilConstants.UNIT_KG)) {
						renderer.setYTitle(this.getText(R.string.bonekg_cloun).toString());
					} else if (UtilConstants.CHOICE_KG.equals(UtilConstants.UNIT_LB) || UtilConstants.CHOICE_KG.equals(UtilConstants.UNIT_FATLB)) {
						renderer.setYTitle(this.getText(R.string.bonelb_cloun).toString());
					}else if (UtilConstants.CHOICE_KG.equals(UtilConstants.UNIT_ST)) {
						renderer.setYTitle(this.getText(R.string.bonestlb_cloun).toString());
					} 
					
					break;
				case UtilConstants.MUSCALE_SINGLE :
					if (UtilConstants.CHOICE_KG.equals(UtilConstants.UNIT_KG)) {
						renderer.setYTitle(this.getText(R.string.musclekg_cloun).toString());
					} else if (UtilConstants.CHOICE_KG.equals(UtilConstants.UNIT_LB) || UtilConstants.CHOICE_KG.equals(UtilConstants.UNIT_FATLB)) {
						renderer.setYTitle(this.getText(R.string.musclelb_cloun).toString());
					}else if (UtilConstants.CHOICE_KG.equals(UtilConstants.UNIT_ST)) {
						renderer.setYTitle(this.getText(R.string.musclestlb_cloun).toString());
					}
					break;
				case UtilConstants.VISCALEFAT_SINGLE :
					renderer.setYTitle(this.getText(R.string.visceral_cloun).toString());
					break;
			}

			mChart = (GraphicalView) ChartFactory.getTimeChartView(getBaseContext(), UtilConstants.isWeight, dataset, renderer, null, this);
			if (null != CacheHelper.recordListDesc) {
				int i = 0;
				for (Records records : CacheHelper.recordListDesc) {
					if (records.getId() == recordid) {
						selectedPosition = i;
						break;
					}
					i++;
				}
				selectedPosition = i;
			} else {
				selectedPosition = dt.length - 1;
			}
			mChart.setSeriesSelection(selectedPosition);
			mChart.setSoundEffectsEnabled(false);
			mChart.invalidate();
			XYChart.weightType = weightType;

			mChart.setBackgroundColor(Color.TRANSPARENT);

			mChart.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					chartClick();
				}
			});

			chartContainer.removeAllViewsInLayout();
			chartContainer.addView(mChart);

			handler.sendEmptyMessage(1);

		}
	}

	private void chartClick() {
		Format formatter = new SimpleDateFormat("dd-MMM-yyyy");
		SeriesSelection seriesSelection = mChart.getCurrentSeriesAndPoint();
		if (seriesSelection != null) {
			mChart.invalidate();
			int pointIndex = seriesSelection.getPointIndex();
			if (CacheHelper.recordListDesc != null && pointIndex >= 0 && CacheHelper.recordListDesc.size() > pointIndex) {
				lastRecod = CacheHelper.recordListDesc.get(pointIndex);
				selectedPosition = getSelectIndex(lastRecod.getId());
				handler.sendEmptyMessage(1);
			}
		}
	}

	@SuppressWarnings("deprecation")
	private XYMultipleSeriesRenderer getDemoRenderer() {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);
		renderer.setPointSize(6.5f);
		renderer.setXLabels(4);
		renderer.setXTitle(this.getText(R.string.hour).toString());
		renderer.setXLabelsAlign(Align.LEFT);
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setZoomEnabled(true, false);
		renderer.setPanEnabled(true, false);
		renderer.setAxesColor(Color.LTGRAY);
		renderer.setLabelsColor(Color.WHITE);
		renderer.setApplyBackgroundColor(true);
		renderer.setMarginsColor(getResources().getColor(R.color.graph_margin));
		renderer.setMargins(new int[]{0, 30, 10, 0});
		renderer.setClickEnabled(true);
		renderer.setSelectableBuffer(40);
		renderer.setShowLegend(false);
		renderer.setBackgroundColor(getResources().getColor(R.color.graph_bg));

		XYSeriesRenderer r = new XYSeriesRenderer();
		r.setColor(getResources().getColor(R.color.graph_line));
		r.setLineWidth(5);
		r.setPointStyle(PointStyle.CIRCLE);
		r.setFillBelowLine(true);
		r.setFillBelowLineColor(Color.TRANSPARENT);
		r.setFillPoints(true);
		r.setDisplayChartValues(true);
		r.setChartValuesTextSize(20);
		renderer.addSeriesRenderer(r);

		return renderer;
	}

	private int getSelectIndex(int id) {
		int i = 0;
		if (null != CacheHelper.recordList && CacheHelper.recordList.size() > 0) {
			for (Records re : CacheHelper.recordList) {
				if (re.getId() == id) {
					break;
				}
				i++;
			}
		}
		return i;
	}

	android.view.View.OnClickListener imgOnClickListener = new android.view.View.OnClickListener() {
		@SuppressWarnings("deprecation")
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.back_textView :
					RecordListActivity.this.finish();
					break;

				case R.id.graph_textview :
					linebg.setBackgroundDrawable(getResources().getDrawable(R.drawable.line_graph));
					delist.setVisibility(View.GONE);
					lv.setVisibility(View.GONE);
					charcontainer.setVisibility(View.VISIBLE);
					menuLy.setVisibility(View.VISIBLE);
					break;

				case R.id.list_textview :
					linebg.setBackgroundDrawable(getResources().getDrawable(R.drawable.line_list));
					delist.setVisibility(View.VISIBLE);
					lv.setVisibility(View.VISIBLE);
					charcontainer.setVisibility(View.GONE);
					menuLy.setVisibility(View.GONE);
//					if (TextUtils.isEmpty(UtilConstants.FIRST_INSTALL_SHARE)) {
//						showTipMask2();
//					}
					
					if (TextUtils.isEmpty(UtilConstants.FIRST_INSTALL_SHARE) && null!=CacheHelper.recordListDesc && CacheHelper.recordListDesc.size() == 1) {
						showTipMask2();
					}
					break;
				case R.id.del_img_btn :
					deleteImg.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_deleted));
					delallImg.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_delete_all));
					if (selectedPosition == -1) {
						Toast.makeText(RecordListActivity.this, getString(R.string.select_record), Toast.LENGTH_LONG);
						return;
					}
					dialog(getString(R.string.deleted_waring), v.getId());
					break;
				case R.id.delall_img_btn :
					deleteImg.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_delete));
					delallImg.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_delete_alld));
					dialog(getString(R.string.deleteall_waring), v.getId());
					break;
				case R.id.share_img :
					if (null != lastRecod) {
						StringBuffer str = new StringBuffer();
					
						//婴儿秤，分享数据只需显示体重和BMI，其他数据去掉
						if(UtilConstants.CURRENT_SCALE.equals(UtilConstants.BABY_SCALE)){
							if (null != user) {
								str.append(user.getUserName());
								str.append("\n");
							}

							str.append(getString(R.string.export_time) + StringUtils.getDateShareString(lastRecod.getRecordTime(), 6));
							str.append("\n");
							
							if (user.getDanwei().equals(UtilConstants.UNIT_KG)) {
								str.append(getString(R.string.export_weight) + lastRecod.getRweight() + "");
								str.append(getText(R.string.kg_danwei));
							} else if (user.getDanwei().equals(UtilConstants.UNIT_LB) || user.getDanwei().equals(UtilConstants.UNIT_ST)) {
								str.append(getString(R.string.export_weight) + UtilTooth.kgToLB_ForFatScale(Math.abs(Float.parseFloat(lastRecod.getRweight() + ""))));
								str.append(getText(R.string.lb_danwei));
							}else if (user.getDanwei().equals(UtilConstants.UNIT_FATLB)) {
								str.append(getString(R.string.export_weight) + UtilTooth.lbozToString(lastRecod.getRweight()));
								//str.append(getText(R.string.lboz_danwei));
							}
							str.append("\n");
							
							float bmi = UtilTooth.countBMI2(lastRecod.getRweight(), (user.getBheigth() / 100));
							bmi = UtilTooth.myround(bmi);

							str.append(getString(R.string.export_BMI) + bmi + "\n");
							
						}else{
						if (null != user) {
							str.append(user.getUserName());
							str.append("\n");
						}

						str.append(getString(R.string.export_time) + StringUtils.getDateShareString(lastRecod.getRecordTime(), 6));
						str.append("\n");

						if (user.getDanwei().equals(UtilConstants.UNIT_KG)) {
							str.append(getString(R.string.export_weight) + lastRecod.getRweight() + "");
							str.append(getText(R.string.kg_danwei));
						} else if (user.getDanwei().equals(UtilConstants.UNIT_LB) || user.getDanwei().equals(UtilConstants.UNIT_ST)) {
							str.append(getString(R.string.export_weight) + UtilTooth.kgToLB_ForFatScale(Math.abs(Float.parseFloat(lastRecod.getRweight() + ""))));
							str.append(getText(R.string.lb_danwei));
						}else if (user.getDanwei().equals(UtilConstants.UNIT_FATLB)) {
							str.append(getString(R.string.export_weight) + UtilTooth.kgToLB_ForFatScale(Math.abs(Float.parseFloat(lastRecod.getRweight() + ""))));
							str.append(getText(R.string.lb_danwei));
						}
						str.append("\n");

						if (UtilConstants.CURRENT_SCALE.equals(UtilConstants.BATHROOM_SCALE)) {

						} else {
							str.append(getString(R.string.export_body_Water) + lastRecod.getRbodywater() + "%\n");
							str.append(getString(R.string.export_body_Fat) + lastRecod.getRbodyfat() + "%\n");
							if (user.getDanwei().equals(UtilConstants.UNIT_KG)) {
								str.append(getString(R.string.export_bone) + lastRecod.getRbone() + "");
								str.append(getText(R.string.kg_danwei) + "\n");
							} else if (user.getDanwei().equals(UtilConstants.UNIT_LB) || user.getDanwei().equals(UtilConstants.UNIT_ST)) {
								str.append(getString(R.string.export_bone) + UtilTooth.kgToLB(Math.abs(Float.parseFloat(lastRecod.getRbone() + ""))));
								str.append(getText(R.string.lb_danwei) + "\n");
							}else if (user.getDanwei().equals(UtilConstants.UNIT_FATLB)) {
								str.append(getString(R.string.export_bone) + UtilTooth.kgToLB(Math.abs(Float.parseFloat(lastRecod.getRbone() + ""))));
								if(UtilConstants.CURRENT_SCALE.equals(UtilConstants.BABY_SCALE)){
									str.append(getText(R.string.lboz_danwei) + "\n");
								}else{
									str.append(getText(R.string.lb_danwei) + "\n");
								}
							} else {
								str.append(getString(R.string.export_bone) + lastRecod.getRbone());
								str.append(getText(R.string.kg_danwei) + "\n");
							}
						}
						float bmi = UtilTooth.countBMI2(lastRecod.getRweight(), (user.getBheigth() / 100));
						bmi = UtilTooth.myround(bmi);

						str.append(getString(R.string.export_BMI) + bmi + "\n");
						if (UtilConstants.CURRENT_SCALE.equals(UtilConstants.BATHROOM_SCALE)) {

						} else {
							str.append(getString(R.string.export_visceral_fat) + lastRecod.getRvisceralfat() + "\n");
							str.append(getString(R.string.export_BMR) + lastRecod.getRbmr() + " kcal\n");
							if (user.getDanwei().equals(UtilConstants.UNIT_KG)) {
								str.append(getString(R.string.export_muscle_mass) + lastRecod.getRmuscle() + "");
								str.append(getText(R.string.kg_danwei) + "\n");
							} else if (user.getDanwei().equals(UtilConstants.UNIT_LB) || user.getDanwei().equals(UtilConstants.UNIT_ST)) {
								str.append(getString(R.string.export_muscle_mass) + UtilTooth.kgToLB(Math.abs(Float.parseFloat(lastRecod.getRmuscle() + ""))));
								str.append(getText(R.string.lb_danwei) + "\n");
							} else if (user.getDanwei().equals(UtilConstants.UNIT_FATLB)) {
								str.append(getString(R.string.export_muscle_mass) + UtilTooth.kgToLB(Math.abs(Float.parseFloat(lastRecod.getRmuscle() + ""))));
								if(UtilConstants.CURRENT_SCALE.equals(UtilConstants.BABY_SCALE)){
									str.append(getText(R.string.lboz_danwei) + "\n");
								}else{
									str.append(getText(R.string.lb_danwei) + "\n");
								}
							} else {
								str.append(getString(R.string.export_muscle_mass) + lastRecod.getRmuscle() + "");
								str.append(getText(R.string.kg_danwei) + "\n");
							}
						}
						}

						Intent sendIntent = new Intent();
						sendIntent.setAction(Intent.ACTION_SEND);

						sendIntent.putExtra(Intent.EXTRA_TEXT, str.toString());
						sendIntent.setType("text/plain");

						startActivity(sendIntent);
					} else {
						Toast.makeText(RecordListActivity.this, getString(R.string.select_record), Toast.LENGTH_SHORT).show();
					}
					break;
			}
		}
	};

	protected void dialog(String title, final int id) {
		AlertDialog.Builder builder = new Builder(RecordListActivity.this);
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
						case R.id.del_img_btn :
							if (null != lastRecod && null != user) {
								recordService.delete(lastRecod);
								CacheHelper.recordListDesc = recordService.getAllDatasByScaleAndIDDesc(UtilConstants.CURRENT_SCALE, user.getId(), user.getBheigth());
								if (null != CacheHelper.recordListDesc && CacheHelper.recordListDesc.size() > 0) {
									lastRecod = CacheHelper.recordListDesc.get(0);
									recordid = lastRecod.getId();
								}else{
									EventBus.getDefault().post(new NoRecordsEvent());
								}
								loadData();
							}

							break;
						case R.id.delall_img_btn :
							if (null != user) {
								recordService.deleteByUseridAndScale(user.getId() + "", UtilConstants.CURRENT_SCALE);
								CacheHelper.recordListDesc = recordService.getAllDatasByScaleAndIDDesc(UtilConstants.CURRENT_SCALE, user.getId(), user.getBheigth());

								lastRecod = null;
								recordid = -1;
								loadData();
								EventBus.getDefault().post(new NoRecordsEvent());
							}

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
	public void dateChangeCallback(int level) {
		Date curDate = null;
		DateFormat format;
		try {
			curDate = UtilTooth.stringToTime(lastRecod.getRecordTime());
		} catch (Exception e) {
		}

		switch (level) {
			case 0 :
				GraphicalView.mZoomInEnable = false;
				break;
			case 1 :
				GraphicalView.mZoomInEnable = true;
				GraphicalView.mZoomOutEnable = true;
				break;

			case 2 :
				if (curDate != null) {
					dateType = 5;
				}
				break;
			case 3 :
				if (curDate != null) {
					dateType = 5;
				}
				break;
			case 4 :
				GraphicalView.mZoomInEnable = true;
				GraphicalView.mZoomOutEnable = true;
				if (curDate != null) {
					dateType = 5;
				}
				break;
			case 5 :
				GraphicalView.mZoomOutEnable = false;
				if (curDate != null) {
					dateType = 5;
				}
				break;
		}
	}

	@Override
	public void onClick(View v) {
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ButterKnife.unbind(this);
	}
}
