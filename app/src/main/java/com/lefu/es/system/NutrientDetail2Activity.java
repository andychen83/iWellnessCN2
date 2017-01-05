package com.lefu.es.system;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.lefu.es.adapter.NutrientDetailAdapter;
import com.lefu.es.entity.NutrientAtrrBo;
import com.lefu.es.entity.NutrientBo;
import com.lefu.iwellness.newes.cn.system.R;

/**
 * 测量营养参数详细界面
 * 
 * @author Leon 2015-11-19
 */
@SuppressLint("SimpleDateFormat")
public class NutrientDetail2Activity extends Activity implements android.view.View.OnClickListener {
	private TextView back_tv, list_textview;

	private ListView detailist_contains;

	private NutrientBo nutrientBo = null;
	
	private NutrientDetailAdapter nutrientAdapter = null;

	public static Intent creatIntent(Context context, NutrientBo nutrientBo) {
		Intent intent = new Intent(context, NutrientDetail2Activity.class);
		intent.putExtra("nutrient", nutrientBo);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nutrient_detail);

		nutrientBo = (NutrientBo)getIntent().getSerializableExtra("nutrient");

		initView();
		
		initDate(nutrientBo);
	}

	
	private void initDate(NutrientBo nutrientBo2) {
		if(null!=nutrientBo2){
			List<NutrientAtrrBo> ls= NutrientAtrrBo.formatNutrientAtrrBo(nutrientBo2);
			nutrientAdapter = new NutrientDetailAdapter(NutrientDetail2Activity.this, ls,nutrientBo2.getFoodWeight());
			detailist_contains.setAdapter(nutrientAdapter);
		}
		
	}

	private void initView() {

		back_tv = (TextView) this.findViewById(R.id.back_textView);

		list_textview = (TextView) this.findViewById(R.id.list_textview);

		detailist_contains = (ListView) this.findViewById(R.id.detailist_contains);

		back_tv.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back_tv :
				finish();
				break;
		}
	}

}
