package com.lefu.es.system;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.lefu.es.constant.UtilConstants;
import com.lefu.es.entity.Records;
import com.lefu.es.entity.UserModel;
import com.lefu.es.service.ExitApplication;
import com.lefu.es.util.MoveView;
import com.lefu.es.util.UtilTooth;
import com.lefu.es.view.MyTextView4;
import com.lefu.iwellness.newes.cn.system.R;

import java.io.Serializable;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 抱婴 测量记录详细
 * @author Leon
 * 2015-11-19
 */
public class RecordListBabyItemActivity extends Activity implements OnClickListener {
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

	@Bind(R.id.tvdetail_weight_status)
	TextView tvdetail_weight_status ;
	@Bind(R.id.tvdetail_bone_status)
	TextView tvdetail_bone_status ;
	@Bind(R.id.tvdetail_bodyfat_status)
	TextView tvdetail_bodyfat_status ;
	@Bind(R.id.tvdetail_muscle_status)
	TextView tvdetail_muscle_status ;
	@Bind(R.id.tvdetail_bodywater_status)
	TextView tvdetail_bodywater_status ;
	@Bind(R.id.tvdetail_visceral_status)
	TextView tvdetail_visceral_status ;
	@Bind(R.id.tvdetail_bmi_status)
	TextView tvdetail_bmi_status ;
	@Bind(R.id.tvdetail_bmr_status)
	TextView tvdetail_bmr_status ;
	@Bind(R.id.tvdetail_phsicalage_status)
	TextView tvdetail_phsicalage_status ;


	private TableRow row_phsicalage = null;

	ImageView chaImage = null;

	protected UserModel user = null;

	public static Intent creatIntent(Context context, UserModel user,Records record){
		Intent intent = new Intent(context,RecordListBabyItemActivity.class);
		intent.putExtra("user",user);
		intent.putExtra("record",record);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Serializable serializable = getIntent().getSerializableExtra("user");
		if(null==serializable){
			user = UtilConstants.CURRENT_USER;
		}else {
			user = (UserModel) serializable;
		}
		Serializable serializable2 = getIntent().getSerializableExtra("record");
		if(null==serializable2){
			Toast.makeText(RecordListBabyItemActivity.this, getString(R.string.choice_a_user), Toast.LENGTH_LONG).show();
			finish();
		}else {
			record = (Records) serializable2;
		}


		if(UtilConstants.BABY_SCALE.equals(record.getScaleType()) || UtilConstants.BATHROOM_SCALE.equals(record.getScaleType())){
			setContentView(R.layout.activity_detaillistitem2);
		}else{
			setContentView(R.layout.activity_detaillistitem);
		}
		ButterKnife.bind(this);
		initResourceRefs();
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
				RecordListBabyItemActivity.this.finish();
			}
		});
	}

	public void creatView(Records record) {
		if (null != record) {

			tv_name_title.setText("");
			if (user.getDanwei().equals(UtilConstants.UNIT_KG)) {
				tvdetail_weight_title.setText(this.getText(R.string.Weightkg_cloun).toString());
				if (null != tvdetail_weight)
					tvdetail_weight.setTexts(UtilTooth.keep1Point(record.getRweight()),null);
			} else if (user.getDanwei().equals(UtilConstants.UNIT_LB) || user.getDanwei().equals(UtilConstants.UNIT_ST) || user.getDanwei().equals(UtilConstants.UNIT_FATLB)) {
				tvdetail_weight_title.setText(this.getText(R.string.Weightlb_cloun).toString());
				if (null != tvdetail_weight)
					tvdetail_weight.setTexts(UtilTooth.kgToLB_ForFatScale(record.getRweight()) + "",null);
			}


			if (null != tvdetail_bmi){
				tvdetail_bmi.setText(UtilTooth.myround(record.getRbmi())+"");
			}
			countBodyParam(record,user);
		}
	}

	/**
	 * 计算人体参数各个标准
	 */
	private void countBodyParam(Records record, UserModel user){
		if(null!=record && null!=user){
			String sex = user.getSex();
			if(TextUtils.isEmpty(sex) || "null".equalsIgnoreCase(sex))sex = "1";
			int gender = Integer.parseInt(sex);

			// 体重
			tvdetail_weight_status.setText(MoveView.weightString(gender,user.getBheigth(),record.getRweight()));
			// 水分率
			tvdetail_bodywater_status.setText(MoveView.moistureString(gender,record.getRbodywater()));

			// 脂肪率
			tvdetail_bodyfat_status.setText(MoveView.bftString(gender,user.getAgeYear(),record.getRbodyfat()));

			// 骨量
			tvdetail_bone_status.setText(MoveView.boneString(record.getRbone()));

			// BMI
			tvdetail_bmi_status.setText(MoveView.bmiString(record.getRbmi()));

			// 内脏脂肪指数
			tvdetail_visceral_status.setText(MoveView.visceralFatString(record.getRvisceralfat()));

			// BMR 基础代谢率
			tvdetail_bmr_status.setText(MoveView.bmrString(gender,user.getAgeYear(),record.getRweight(),record.getRbmr()));

			// 肌肉含量
			float muscal = UtilTooth.keep1Point3(record.getRmuscle());

			tvdetail_muscle_status.setText(MoveView.muscleString(gender,user.getBheigth(),muscal));

			//身体年龄

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
			NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.cancel(0);
			if (null != LoadingActivity.mainActivty)
				LoadingActivity.mainActivty.finish();
			this.finish();
			ExitApplication.getInstance().exit(this);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ButterKnife.unbind(this);
	}

}
