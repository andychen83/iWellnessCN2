package com.lefu.es.system;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.lefu.es.constant.UtilConstants;
import com.lefu.es.entity.Records;
import com.lefu.es.service.ExitApplication;
import com.lefu.es.util.UtilTooth;
import com.lefu.es.view.MyTextView4;
import com.lefu.iwellness.newes.cn.system.R;

/**
 * 测量记录详细
 * @author Leon
 * 2015-11-19
 */
public class RecordListItemActivity extends Activity implements OnClickListener {
	private Records record = null;
	TextView tvdetail_weight_title,tvdetail_muscle_title,tvdetail_bone_title = null;
	MyTextView4 tvdetail_weight = null;
	TextView tvdetail_bone = null;
	TextView tvdetail_bodyfat = null;
	TextView tvdetail_muscle = null;

	TextView tvdetail_bodywater = null;
	TextView tvdetail_visceral = null;
	TextView tvdetail_bmi = null;
	TextView tvdetail_bmr = null;
	TextView tvdetail_phsicalage = null;
	TextView tvdetail_bodyfat_title = null;
	TextView tvdetail_bodywater_title = null;
	TextView tvdetail_visceral_title = null;
	TextView tvdetail_bmi_title = null;
	TextView tvdetail_bmr_title = null;
	TextView tvdetail_phsicalage_title = null;
	TextView tv_name_title = null;
	
	private TableRow row_phsicalage = null;

	ImageView chaImage = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if(UtilConstants.BABY_SCALE.equals(UtilConstants.CURRENT_SCALE) || UtilConstants.BATHROOM_SCALE.equals(UtilConstants.CURRENT_SCALE)){
			setContentView(R.layout.activity_detaillistitem2);
		}else{
			setContentView(R.layout.activity_detaillistitem);
		}
		
		initResourceRefs();
		record = (Records) getIntent().getSerializableExtra("record");
		creatView(record);
	}

	private void initResourceRefs() {
		chaImage = (ImageView) this.findViewById(R.id.cha_imageview);
		tvdetail_weight_title = (TextView) this.findViewById(R.id.tvdetail_weight_title);
		tvdetail_weight = (MyTextView4) this.findViewById(R.id.tvdetail_weight);
		tvdetail_bmr = (TextView) this.findViewById(R.id.tvdetail_bmr);
		tvdetail_bmr_title = (TextView) this.findViewById(R.id.tvdetail_bmr_title);
		tvdetail_bone = (TextView) this.findViewById(R.id.tvdetail_bone);
		tvdetail_bodyfat = (TextView) this.findViewById(R.id.tvdetail_bodyfat);
		tvdetail_bodyfat_title = (TextView) this.findViewById(R.id.tvdetail_bodyfat_title);
		tvdetail_muscle = (TextView) this.findViewById(R.id.tvdetail_muscle);
		tvdetail_phsicalage = (TextView) this.findViewById(R.id.tvdetail_phsicalage);
		tvdetail_phsicalage_title = (TextView) this.findViewById(R.id.tvdetail_phsicalage_title);
		row_phsicalage = (TableRow) this.findViewById(R.id.row_phsicalage);
		
		tvdetail_bone_title = (TextView) this.findViewById(R.id.tvdetail_bone_title);
		tvdetail_muscle_title = (TextView) this.findViewById(R.id.tvdetail_muscle_title);
		
		tvdetail_bodywater = (TextView) this.findViewById(R.id.tvdetail_bodywater);
		tvdetail_bodywater_title = (TextView) this.findViewById(R.id.tvdetail_bodywater_title);
		
		tvdetail_visceral = (TextView) this
				.findViewById(R.id.tvdetail_visceral);
		tvdetail_visceral_title = (TextView) this
				.findViewById(R.id.tvdetail_visceral_title);
		
		tvdetail_bmi = (TextView) this.findViewById(R.id.tvdetail_bmi);
		tvdetail_bmi_title = (TextView) this.findViewById(R.id.tvdetail_bmi_title);
		
		tv_name_title = (TextView) this.findViewById(R.id.tv_name_title);

		chaImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				RecordListItemActivity.this.finish();
			}
		});
	}

	public void creatView(Records record) {
		if (null != record) {
			if(UtilConstants.KITCHEN_SCALE.equals(UtilConstants.CURRENT_SCALE)){
				tv_name_title.setText(record.getRphoto());
				if(null!=UtilConstants.CURRENT_USER){
					if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_LB)) {
						tvdetail_weight_title.setText(this.getText(R.string.Weightlboz_cloun).toString());
						if (null != tvdetail_weight)
							tvdetail_weight.setTexts(UtilTooth.kgToLBoz(record.getRweight()) + "",null);
					}else if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_FATLB)) {
						tvdetail_weight_title.setText(this.getText(R.string.Weightfloz_cloun).toString());
						if (null != tvdetail_weight)
							tvdetail_weight.setTexts(UtilTooth.kgToFloz(record.getRweight()) + "",null);
					}else if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_G)) {
						tvdetail_weight_title.setText(this.getText(R.string.Weightg_cloun).toString());
						if (null != tvdetail_weight)
							tvdetail_weight.setTexts(UtilTooth.keep2Point(record.getRweight()) + "",null);
					}else if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_ML)) {
						tvdetail_weight_title.setText(this.getText(R.string.Weightml_cloun).toString());
						if (null != tvdetail_weight)
							tvdetail_weight.setTexts(UtilTooth.keep2Point(record.getRweight()) + "",null);
					}else if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_ML2)) {
						tvdetail_weight_title.setText(this.getText(R.string.Weightml2_cloun).toString());
						if (null != tvdetail_weight)
							tvdetail_weight.setTexts(UtilTooth.kgToML(record.getRweight()) + "",null);
					}else{
						tvdetail_weight_title.setText(this.getText(R.string.Weightlboz_cloun).toString());
						if (null != tvdetail_weight)
							tvdetail_weight.setTexts(UtilTooth.kgToLBoz(record.getRweight()) + "",null);
					}
				}else{
					tvdetail_weight_title.setText(this.getText(R.string.Weightg_cloun).toString());
					if (null != tvdetail_weight)
						tvdetail_weight.setTexts(UtilTooth.keep2Point(record.getRweight()) + "",null);
				}
				
				
//				if (UtilConstants.CHOICE_KG.equals(UtilConstants.UNIT_LB)) {
//					tvdetail_weight_title.setText(this.getText(R.string.Weightlboz_cloun).toString());
//					if (null != tvdetail_weight)
//						tvdetail_weight.setTexts(UtilTooth.kgToLBoz(record.getRweight()*0.001f) + "",null);
//				} else if (UtilConstants.CHOICE_KG.equals(UtilConstants.UNIT_ST)) {
//					tvdetail_weight_title.setText(this.getText(R.string.Weightstlb_cloun).toString());
//					if (null != tvdetail_weight)
//						tvdetail_weight.setTexts(UtilTooth.kgToStLb_B(record.getRweight()*0.001f) + "",null);
//					String[] fatTemp=UtilTooth.kgToStLbForScaleFat2(record.getRweight()*0.001f);
//					if(fatTemp[1]!=null && fatTemp[1].indexOf("/")>0){
//						tvdetail_weight.setTexts(fatTemp[0],fatTemp[1]);
//					}else{
//						tvdetail_weight.setTexts(fatTemp[0]+getText(R.string.stlb_danwei),null);
//					}
//				}else if(UtilConstants.CHOICE_KG.equals(UtilConstants.UNIT_FATLB)){
//					tvdetail_weight_title.setText(this.getText(R.string.Weightfloz_cloun).toString());
//					if (null != tvdetail_weight)
//						tvdetail_weight.setTexts(UtilTooth.kgToFloz(record.getRweight()) + "",null);
//				}else if(UtilConstants.CHOICE_KG.equals(UtilConstants.UNIT_ML)){
//					tvdetail_weight_title.setText(this.getText(R.string.Weightml_cloun).toString());
//					if (null != tvdetail_weight)
//						tvdetail_weight.setTexts(UtilTooth.keep2Point(record.getRweight()) + "",null);
//				}else{
//					if (UtilConstants.CHOICE_KG.equals(UtilConstants.UNIT_KG)) {
//						tvdetail_weight_title.setText(this.getText(R.string.Weightg_cloun).toString());
//						if (null != tvdetail_weight)
//							tvdetail_weight.setTexts(UtilTooth.keep2Point(record.getRweight()) + "",null);
//					} 
//				}
				
				tvdetail_bone_title.setText(this.getText(R.string.energ_cloun).toString());
				tvdetail_bone.setText(UtilTooth.keep2Point(record.getRbodywater()) + "");
				
				tvdetail_bodyfat_title.setText(this.getText(R.string.Proteing_cloun).toString());
				if (null != tvdetail_bodyfat)tvdetail_bodyfat.setText(UtilTooth.keep2Point(record.getRbodyfat()) + "");
				
				
				tvdetail_muscle_title.setText(this.getText(R.string.Lipidg_cloun).toString());
				tvdetail_muscle.setText(UtilTooth.keep2Point(record.getRbone()) + "");
				
				tvdetail_bodywater_title.setText(this.getText(R.string.Calciumg_cloun).toString());
				if (null != tvdetail_bodywater)
					tvdetail_bodywater.setText(UtilTooth.keep2Point(record.getRmuscle()) + "");
				
				tvdetail_visceral_title.setText(this.getText(R.string.Fiberg_cloun).toString());
				if (null != tvdetail_visceral)
					tvdetail_visceral.setText(UtilTooth.keep2Point(record.getRvisceralfat()) + "");
				
				tvdetail_bmi_title.setText(this.getText(R.string.Cholesterolg_cloun).toString());
				if (null != tvdetail_bmi)
					tvdetail_bmi.setText(UtilTooth.keep2Point(record.getRbmi()) + "");
				
				tvdetail_bmr_title.setText(this.getText(R.string.Vitaming_cloun).toString());
				if (null != tvdetail_bmr)
					tvdetail_bmr.setText(UtilTooth.keep2Point(record.getRbmr()) + "");
				
				tvdetail_phsicalage_title.setText(this.getText(R.string.Carbohydrateg_cloun).toString());
				tvdetail_phsicalage.setText(UtilTooth.keep2Point(record.getBodyAge())+"");
				
				
				
				
				
			}else{
				tv_name_title.setText("");
				if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_KG)) {
					tvdetail_weight_title.setText(this.getText(R.string.Weightkg_cloun).toString());
					if (null != tvdetail_weight)
						tvdetail_weight.setTexts(record.getRweight() + "",null);
				} else if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_LB)) {
					tvdetail_weight_title.setText(this.getText(R.string.Weightlb_cloun).toString());
					if (null != tvdetail_weight)
						tvdetail_weight.setTexts(UtilTooth.kgToLB_ForFatScale(record.getRweight()) + "",null);
				}else if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_ST)) {
					tvdetail_weight_title.setText(this.getText(R.string.Weightstlb_cloun).toString());
					if (null != tvdetail_weight)
						tvdetail_weight.setTexts(UtilTooth.kgToStLb_B(record.getRweight()) + "",null);
					String[] fatTemp=UtilTooth.kgToStLbForScaleFat2(record.getRweight());
					if(fatTemp[1]!=null && fatTemp[1].indexOf("/")>0){
						tvdetail_weight.setTexts(fatTemp[0],fatTemp[1]);
					}else{
						tvdetail_weight.setTexts(fatTemp[0]+getText(R.string.stlb_danwei),null);
					}
				}else if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_FATLB)) {
					if(UtilConstants.CURRENT_SCALE.equals(UtilConstants.BABY_SCALE)){
						tvdetail_weight_title.setText(this.getText(R.string.Weightlboz_cloun).toString());
						if (null != tvdetail_weight)
							tvdetail_weight.setTexts(UtilTooth.lbozToString(record.getRweight()),null);
					}else{
						tvdetail_weight_title.setText(this.getText(R.string.Weightlb_cloun).toString());
						if (null != tvdetail_weight)
							tvdetail_weight.setTexts(UtilTooth.kgToLB_ForFatScale(record.getRweight()) + "",null);
					}

				}else{
					tvdetail_weight_title.setText(this.getText(R.string.Weightkg_cloun).toString());
					if (null != tvdetail_weight)
						tvdetail_weight.setTexts(record.getRweight() + "",null);
				}
				
				if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_KG)) {
					tvdetail_bone_title.setText(this.getText(R.string.bonekg_cloun).toString());
					tvdetail_muscle_title.setText(this.getText(R.string.musclekg_cloun).toString());
					tvdetail_bone.setText(record.getRbone()+"");
					tvdetail_muscle.setText(record.getRmuscle() + "");
				}else if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_LB)
						|| UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_FATLB)) {
					tvdetail_bone_title.setText(this.getText(R.string.bonelb_cloun).toString());
					tvdetail_muscle_title.setText(this.getText(R.string.musclelb_cloun).toString());
					tvdetail_bone.setText(UtilTooth.kgToLB(record.getRbone()) + "");
					tvdetail_muscle.setText(UtilTooth.kgToLB(record.getRmuscle()) + "");
				} else if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_ST)) {
					tvdetail_bone_title.setText(this.getText(R.string.bonestlb_cloun).toString());
					tvdetail_muscle_title.setText(this.getText(R.string.musclestlb_cloun).toString());
					tvdetail_bone.setText(UtilTooth.kgToLB(record.getRbone()) + "");
					tvdetail_muscle.setText(UtilTooth.kgToLB(record.getRmuscle()) + "");
				}else{
					tvdetail_bone_title.setText(this.getText(R.string.bonekg_cloun).toString());
					tvdetail_muscle_title.setText(this.getText(R.string.musclekg_cloun).toString());
					tvdetail_bone.setText(record.getRbone()+"");
					tvdetail_muscle.setText(record.getRmuscle() + "");
				}
				if(record.getBodyAge()>0){
					tvdetail_phsicalage.setText(UtilTooth.keep0Point(record.getBodyAge()));
				}else{
					row_phsicalage.setVisibility(View.GONE);
				}
				if (null != tvdetail_bmr)
					tvdetail_bmr.setText(record.getRbmr() + "");
				
				if (null != tvdetail_bodyfat)
					tvdetail_bodyfat.setText(record.getRbodyfat() + "");
				
				if (null != tvdetail_bodywater)
					tvdetail_bodywater.setText(record.getRbodywater() + "");
				if (null != tvdetail_visceral)
					tvdetail_visceral.setText(record.getRvisceralfat() + "");
				if (null != tvdetail_bmi){
					//String bmi = "";
					float bmi = UtilTooth.countBMI2(record.getRweight(), (UtilConstants.CURRENT_USER.getBheigth() / 100));
                    tvdetail_bmi.setText(UtilTooth.myround(bmi)+"");
	            }
			}
			
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			this.finish();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_HOME) {
			if (null != UtilConstants.serveIntent)
				stopService(UtilConstants.serveIntent);
			NotificationManager notificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
			notificationManager.cancel(0);
			if (null != LoadingActivity.mainActivty)
				LoadingActivity.mainActivty.finish();
			this.finish();
			ExitApplication.getInstance().exit(this);
		}
		return super.onKeyDown(keyCode, event);
	}

}
