package com.lefu.es.view;


import com.lefu.es.util.UtilTooth;
import com.lefu.iwellness.newes.cn.system.R;

import android.content.Context;
import android.graphics.Color;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public class MyBar extends LinearLayout {

	public MyBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		//ϵͳ�汾
		int sysVersion = Integer.parseInt(VERSION.SDK);
		Log.e("test", "ϵͳ�汾��"+sysVersion);
		
		setOrientation(LinearLayout.HORIZONTAL);
		setWeightSum(UtilTooth.BAR_WEIGHT_SUM);
		
		View vThin = new View(context);
		//vThin.setBackgroundColor(Color.parseColor("#0068b7"));
		
		if(sysVersion>=15){
			vThin.setBackgroundResource(R.drawable.mybar_shap_left);
		}else{
			vThin.setBackgroundResource(R.drawable.mybar_shap_left_low);
		}
		LayoutParams lpThin = new LayoutParams(0, -1,UtilTooth.BAR_WEIGHT_THIN);
		vThin.setLayoutParams(lpThin);
		addView(vThin);
		
		View vHealthy = new View(context);
		vHealthy.setBackgroundColor(Color.parseColor("#54b32c"));
		LayoutParams lpHealthy = new LayoutParams(0, -1,UtilTooth.BAR_WEIGHT_HEALTHY);
		vHealthy.setLayoutParams(lpHealthy);
		addView(vHealthy);
		
		View vFat = new View(context);
		vFat.setBackgroundColor(Color.parseColor("#f7af00"));
		LayoutParams lpFat = new LayoutParams(0, -1,UtilTooth.BAR_WEIGHT_FAT);
		vFat.setLayoutParams(lpFat);
		addView(vFat);
		
		View vOverWeight = new View(context);
		//vOverWeight.setBackgroundColor(Color.parseColor("#ea2531"));
		if(sysVersion>=15){
			vOverWeight.setBackgroundResource(R.drawable.mybar_shap_right);
		}else{
			vOverWeight.setBackgroundResource(R.drawable.mybar_shap_right_low);
		}
		LayoutParams lpOverWeight = new LayoutParams(0, -1,UtilTooth.BAR_WEIGHT_OVERWEIGHT);
		vOverWeight.setLayoutParams(lpOverWeight);
		addView(vOverWeight);
		
	}

}
