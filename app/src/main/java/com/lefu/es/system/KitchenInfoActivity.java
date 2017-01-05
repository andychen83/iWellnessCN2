package com.lefu.es.system;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.lefu.es.adapter.SearchAdapter;
import com.lefu.es.cache.CacheHelper;
import com.lefu.es.entity.NutrientBo;
import com.lefu.es.system.fragment.SearchDialogFragment.NatureSelectListener;
import com.lefu.es.util.Tool;
import com.lefu.es.util.UtilTooth;
import com.lefu.es.view.kmpautotextview.KMPAutoComplTextView;
import com.lefu.es.view.spinner.HintAdapter;
import com.lefu.es.view.spinner.HintSpinner;
import com.lefu.iwellness.newes.cn.system.R;
/**
 * Info信息界面
 * @author Leon
 * 2015-11-21
 */
public class KitchenInfoActivity extends Activity implements NatureSelectListener {
	//private XListView xListView;
	//private NutrientInfoAdapter mAdapter;
	private NutrientBo selectNutrient = null;

	private TextView Water_excel_title;
	private TextView Energ_excel_title;
	private TextView Protein_excel_title;
	private TextView Lipid_excel_title;
	private TextView Ash_excel_title;

	private TextView Carbohydrt_excel_title;
	private TextView Fiber_excel_title;
	private TextView Sugar_excel_title;
	private TextView Calcium_excel_title;
	private TextView Iron_excel_title;

	private TextView Magnesium_excel_title;
	private TextView Phosphorus_excel_title;
	private TextView Potassium_excel_title;
	private TextView Sodium_excel_title;
	private TextView Zinc_excel_title;

	private TextView Copper_excel_title;
	private TextView Manganese_excel_title;
	private TextView Selenium_excel_title;
	private TextView Vit_C_excel_title;
	private TextView Thiamin_excel_title;

	private TextView Riboflavin_excel_title;
	private TextView Niacin_excel_title;
	private TextView Panto_Acid_excel_title;
	private TextView Vit_B6_excel_title;
	private TextView Folate_Tot_excel_title;

	private TextView Folic_Acid_excel_title;
	private TextView Food_Folate_excel_title;
	private TextView Folate_DFE_excel_title;
	private TextView Choline_Tot_excel_title;
	private TextView Vit_B12_excel_title;

	private TextView Vit_A_IU_excel_title;
	private TextView Vit_A_RAE_excel_title;
	private TextView Retinol_excel_title;
	private TextView Alpha_Carot_excel_title;
	private TextView Beta_Carot_excel_title;

	private TextView Beta_Crypt_excel_title;
	private TextView Lycopene_excel_title;
	private TextView LutZea_excel_title;
	private TextView Vit_E_excel_title;
	private TextView Vit_D_excel_title;

	private TextView Vit_D_IU_excel_title;
	private TextView Vit_K_excel_title;
	private TextView FA_Sat_excel_title;
	private TextView FA_Mono_excel_title;
	private TextView FA_Poly_excel_title;

	private TextView Cholesterol_excel_title;
	private TextView GmWt_1_excel_title;
	private TextView GmWt_2_excel_title;
	private TextView Refuse_Pct_excel_title;

	private TextView jiujing_vol_ml_excel_title;
	private TextView Energ_kj_excel_title;
	private TextView Food_excel_title;

	private AutoCompleteTextView search_et;
	private AutoCompleteTextView weight_et;
	private TextView search_btn;
	private TextView btn_mback;
	int currentPage = 0;

	private HintSpinner<String> defaultHintSpinner;
	private List<String> defaults;

	private String currentUnit = null;

	public SearchAdapter searchSdapter = null;//

	@SuppressWarnings("unused")
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_kitchen_info2);

		initView();


	}

	private void initView() {
		currentUnit = getResources().getString(R.string.g_danwei);

		search_et = (AutoCompleteTextView)findViewById(R.id.search_et);
		weight_et = (AutoCompleteTextView)findViewById(R.id.weight_et);
		search_btn = (TextView)findViewById(R.id.search_btn);
		btn_mback = (TextView)findViewById(R.id.btn_mback);
		search_btn.setOnClickListener(btnOnClickListener);
		btn_mback.setOnClickListener(btnOnClickListener);

		Food_excel_title= (TextView)findViewById(R.id.Food_excel_title);

		Water_excel_title = (TextView)findViewById(R.id.Water_excel_title);
		Energ_excel_title = (TextView)findViewById(R.id.Energ_excel_title);
		Protein_excel_title = (TextView)findViewById(R.id.Protein_excel_title);
		Lipid_excel_title = (TextView)findViewById(R.id.Lipid_excel_title);
		Ash_excel_title = (TextView)findViewById(R.id.Ash_excel_title);

		jiujing_vol_ml_excel_title = (TextView)findViewById(R.id.jiujing_vol_ml_excel_title);
		Energ_kj_excel_title = (TextView)findViewById(R.id.Energ_kj_excel_title);

		Carbohydrt_excel_title = (TextView)findViewById(R.id.Carbohydrt_excel_title);
		Fiber_excel_title = (TextView)findViewById(R.id.Fiber_excel_title);
		Sugar_excel_title = (TextView)findViewById(R.id.Sugar_excel_title);
		Calcium_excel_title = (TextView)findViewById(R.id.Calcium_excel_title);
		Iron_excel_title = (TextView)findViewById(R.id.Iron_excel_title);

		Magnesium_excel_title = (TextView)findViewById(R.id.Magnesium_excel_title);
		Phosphorus_excel_title = (TextView)findViewById(R.id.Phosphorus_excel_title);
		Potassium_excel_title = (TextView)findViewById(R.id.Potassium_excel_title);
		Sodium_excel_title = (TextView)findViewById(R.id.Sodium_excel_title);
		Zinc_excel_title = (TextView)findViewById(R.id.Zinc_excel_title);

		Copper_excel_title = (TextView)findViewById(R.id.Copper_excel_title);
		Manganese_excel_title = (TextView)findViewById(R.id.Manganese_excel_title);
		Selenium_excel_title = (TextView)findViewById(R.id.Selenium_excel_title);
		Vit_C_excel_title = (TextView)findViewById(R.id.Vit_C_excel_title);
		Thiamin_excel_title = (TextView)findViewById(R.id.Thiamin_excel_title);

		Riboflavin_excel_title = (TextView)findViewById(R.id.Riboflavin_excel_title);
		Niacin_excel_title = (TextView)findViewById(R.id.Niacin_excel_title);
		Panto_Acid_excel_title = (TextView)findViewById(R.id.Panto_Acid_excel_title);
		Vit_B6_excel_title = (TextView)findViewById(R.id.Vit_B6_excel_title);
		Folate_Tot_excel_title = (TextView)findViewById(R.id.Folate_Tot_excel_title);

		Folic_Acid_excel_title = (TextView)findViewById(R.id.Folic_Acid_excel_title);
		Food_Folate_excel_title = (TextView)findViewById(R.id.Food_Folate_excel_title);
		Folate_DFE_excel_title = (TextView)findViewById(R.id.Folate_DFE_excel_title);
		Choline_Tot_excel_title = (TextView)findViewById(R.id.Choline_Tot_excel_title);
		Vit_B12_excel_title = (TextView)findViewById(R.id.Vit_B12_excel_title);

		Vit_A_IU_excel_title = (TextView)findViewById(R.id.Vit_A_IU_excel_title);
		Vit_A_RAE_excel_title = (TextView)findViewById(R.id.Vit_A_RAE_excel_title);
		Retinol_excel_title = (TextView)findViewById(R.id.Retinol_excel_title);
		Alpha_Carot_excel_title = (TextView)findViewById(R.id.Alpha_Carot_excel_title);
		Beta_Carot_excel_title = (TextView)findViewById(R.id.Beta_Carot_excel_title);

		Beta_Crypt_excel_title = (TextView)findViewById(R.id.Beta_Crypt_excel_title);
		Lycopene_excel_title = (TextView)findViewById(R.id.Lycopene_excel_title);
		LutZea_excel_title = (TextView)findViewById(R.id.LutZea_excel_title);
		Vit_E_excel_title = (TextView)findViewById(R.id.Vit_E_excel_title);
		Vit_D_excel_title = (TextView)findViewById(R.id.Vit_D_excel_title);

		Vit_D_IU_excel_title = (TextView)findViewById(R.id.Vit_D_IU_excel_title);
		Vit_K_excel_title = (TextView)findViewById(R.id.Vit_K_excel_title);
		FA_Sat_excel_title = (TextView)findViewById(R.id.FA_Sat_excel_title);
		FA_Mono_excel_title = (TextView)findViewById(R.id.FA_Mono_excel_title);
		FA_Poly_excel_title = (TextView)findViewById(R.id.FA_Poly_excel_title);

		Cholesterol_excel_title = (TextView)findViewById(R.id.Cholesterol_excel_title);
		GmWt_1_excel_title = (TextView)findViewById(R.id.GmWt_1_excel_title);
		GmWt_2_excel_title = (TextView)findViewById(R.id.GmWt_2_excel_title);
		Refuse_Pct_excel_title = (TextView)findViewById(R.id.Refuse_Pct_excel_title);

//		if(null!=CacheHelper.nutrientTempNameList && CacheHelper.nutrientTempNameList.size()>0)search_et.setDatas(CacheHelper.nutrientTempNameList);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				try {
					if(null!=CacheHelper.nutrientNameList && CacheHelper.nutrientNameList.size()>0){
						initSearchViews();
					}

				} catch (Exception e) {
					//Log.e(TAG+"11", e.getMessage());
				}

			}
		}, 5000);
//		//lastWeight = 100f;
//        search_et.setOnPopupItemClickListener(new KMPAutoComplTextView.OnPopupItemClickListener() {
//            @Override
//            public void onPopupItemClick(CharSequence charSequence) {
//                //查找
//            	if(TextUtils.isEmpty(charSequence)){
//					return ;
//				}
//				NutrientBo nutrient = CacheHelper.queryNutrientsByName(charSequence.toString());
//				if(null==nutrient){
//					Toast.makeText(KitchenInfoActivity.this, "No Data had bean found", Toast.LENGTH_SHORT).show();
//					return ;
//				}
//				search_et.setText(nutrient.getNutrientDesc());
//				selectNutrient = nutrient;
//				setViewDate(nutrient, 0f);
//            }
//        });

		defaults = new ArrayList<String>();
		defaults.add(getResources().getString(R.string.g_danwei));
		defaults.add(getResources().getString(R.string.lboz_danwei));
		defaults.add(getResources().getString(R.string.ml_danwei));
		defaults.add(getResources().getString(R.string.oz_danwei2));
		defaults.add(getResources().getString(R.string.ml_danwei2));
		final Spinner defaultSpinner = (Spinner) findViewById(R.id.spinner1);

		defaultHintSpinner = new HintSpinner<String>(
				defaultSpinner,
				// Default layout - You don't need to pass in any layout id, just your hint text and
				// your list data
				new HintAdapter<String>(this, R.string.default_spinner_hint, defaults),
				new HintSpinner.Callback<String>() {
					@Override
					public void onItemSelected(int position, String itemAtPosition) {
						// Here you handle the on item selected event (this skips the hint selected
						// event)
						showSelectedItem(itemAtPosition);
					}
				});

		defaultHintSpinner.init();

		weight_et.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String str = s.toString().trim();
				if(!TextUtils.isEmpty(str)){
					if(Tool.isDigitsOnly(str)){
						try {
							if(null!=currentUnit){
								if(needChage){
									if(currentUnit.equals(getResources().getString(R.string.oz_danwei2))){
										lastWeight =UtilTooth.lbTog(Float.parseFloat(weight_et.getText().toString()));
									}else if(currentUnit.equals(getResources().getString(R.string.lboz_danwei))){
										lastWeight = UtilTooth.ozTog(Float.parseFloat(weight_et.getText().toString()));
									}else if(currentUnit.equals(getResources().getString(R.string.ml_danwei2))){
										lastWeight = UtilTooth.kgToML(Float.parseFloat(weight_et.getText().toString()));
									}else{
										lastWeight =  UtilTooth.gTog(Float.parseFloat(weight_et.getText().toString()));
									}
									if(null!=selectNutrient)setViewDate(selectNutrient, lastWeight);
									needChage = true;
								}
							}
						} catch (Exception e) {
							if(needChage){
								lastWeight = Float.parseFloat(str);
								if(null!=selectNutrient)setViewDate(selectNutrient, lastWeight);
								needChage = true;
							}
						}

					}
				}else{
					if(needChage){
						if(null!=selectNutrient)setViewDate(selectNutrient, 0f);
						needChage = true;
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {


			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}

		});

		//lastWeight = 100f;
	}

	private void initSearchViews() {
		search_et.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
									int position, long id) {
				// TODO Auto-generated method stub
				try {
					String str = search_et.getText().toString();
					if(TextUtils.isEmpty(str)){
						return ;
					}
					NutrientBo nutrient = CacheHelper.queryNutrientsByName(str);
					if(null==nutrient){
						Toast.makeText(KitchenInfoActivity.this, "没有搜索到数据", Toast.LENGTH_SHORT).show();
						return ;
					}
					search_et.setText(nutrient.getNutrientDesc());
					selectNutrient = nutrient;
					setViewDate(nutrient, 0f);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

		});

		search_et.setThreshold(1);

		searchSdapter = new SearchAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,(String[])CacheHelper.nutrientNameList.toArray(new String[0]),SearchAdapter.ALL);//速度优先
		search_et.setAdapter(searchSdapter);//
	}


	private float lastWeight = 0f;
	private boolean needChage = true;
	private void showSelectedItem(String itemAtPosition) {
		if(TextUtils.isEmpty(itemAtPosition)) return;
		if(itemAtPosition.equals(currentUnit)){
			return;
		}else{
			if(lastWeight>0){
				needChage = false;
				if(itemAtPosition.equals(getResources().getString(R.string.oz_danwei2))){
					weight_et.setText(UtilTooth.kgToFloz(lastWeight));
				}else if(itemAtPosition.equals(getResources().getString(R.string.lboz_danwei))){
					weight_et.setText(UtilTooth.kgToLBoz(lastWeight));
				}else if(itemAtPosition.equals(getResources().getString(R.string.ml_danwei2))){
					weight_et.setText(UtilTooth.gToMl2(lastWeight));
				}else{
					weight_et.setText(UtilTooth.gTogS(lastWeight));
				}
			}
			currentUnit = itemAtPosition;
		}
	}


	android.view.View.OnClickListener btnOnClickListener = new android.view.View.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.search_btn :
					/* 跳转到用户列表界面 */
					if(TextUtils.isEmpty(search_et.getText().toString())){
						Toast.makeText(KitchenInfoActivity.this, "Please input something", Toast.LENGTH_SHORT).show();
						return ;
					}
					NutrientBo nutrient = CacheHelper.queryNutrientsByName(search_et.getText().toString());
					if(null==nutrient){
						Toast.makeText(KitchenInfoActivity.this, "没有搜索到数据", Toast.LENGTH_SHORT).show();
						return ;
					}

					search_et.setText(nutrient.getNutrientDesc());
					selectNutrient = nutrient;
					setViewDate(nutrient, 0f);
					break;

				case R.id.btn_mback:
					finish();
					break;
			}
		}
	};


	private void setViewDate(NutrientBo nutrientBo,float weight){
		if(null!=nutrientBo){

			if(!TextUtils.isEmpty(nutrientBo.getNutrientWater())){
				if(weight==0){
					Food_excel_title.setText(nutrientBo.getNutrientWater());
				}else{
					Food_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientWater())*weight*0.01f));
				}
			}else{
				Food_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientEnerg())){
				if(weight==0){
					Water_excel_title.setText(nutrientBo.getNutrientEnerg());
				}else{
					Water_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientEnerg())*weight*0.01f));
				}
			}else{
				Water_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientProtein())){
				if(weight==0){
					jiujing_vol_ml_excel_title.setText(nutrientBo.getNutrientProtein());
				}else{
					jiujing_vol_ml_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientProtein())*weight*0.01f));
				}
			}else{
				jiujing_vol_ml_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientLipidTot())){
				if(weight==0){
					Energ_excel_title.setText(nutrientBo.getNutrientLipidTot());
				}else{
					Energ_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientLipidTot())*weight*0.01f));
				}
			}else{
				Energ_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientAsh())){
				if(weight==0){
					Protein_excel_title.setText(nutrientBo.getNutrientAsh());
				}else{
					Protein_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientAsh())*weight*0.01f));
				}
			}else{
				Protein_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientCarbohydrt())){
				if(weight==0){
					Energ_kj_excel_title.setText(nutrientBo.getNutrientCarbohydrt());
				}else{
					Energ_kj_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientCarbohydrt())*weight*0.01f));
				}
			}else{
				Energ_kj_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientFiberTD())){
				if(weight==0){
					Lipid_excel_title.setText(nutrientBo.getNutrientFiberTD());
				}else{
					Lipid_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientFiberTD())*weight*0.01f));
				}
			}else{
				Lipid_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientSugarTot())){
				if(weight==0){
					Ash_excel_title.setText(nutrientBo.getNutrientSugarTot());
				}else{
					Ash_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientSugarTot())*weight*0.01f));
				}
			}else{
				Ash_excel_title.setText("0.0");
			}
			if(!TextUtils.isEmpty(nutrientBo.getNutrientCalcium())){
				if(weight==0){
					Carbohydrt_excel_title.setText(nutrientBo.getNutrientCalcium());
				}else{
					Carbohydrt_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientCalcium())*weight*0.01f));
				}
			}else{
				Carbohydrt_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientIron())){
				if(weight==0){
					Fiber_excel_title.setText(nutrientBo.getNutrientIron());
				}else{
					Fiber_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientIron())*weight*0.01f));
				}
			}else{
				Fiber_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientMagnesium())){
				if(weight==0){
					Sugar_excel_title.setText(nutrientBo.getNutrientMagnesium());
				}else{
					Sugar_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientMagnesium())*weight*0.01f));
				}
			}else{
				Sugar_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientPhosphorus())){//纖維
				if(weight==0){
					Calcium_excel_title.setText(nutrientBo.getNutrientPhosphorus());
				}else{
					Calcium_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientPhosphorus())*weight*0.01f));
				}
			}else{
				Calcium_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientPotassium())){
				if(weight==0){
					Iron_excel_title.setText(nutrientBo.getNutrientPotassium());
				}else{
					Iron_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientPotassium())*weight*0.01f));
				}
			}else{
				Iron_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientSodium())){//鈣
				if(weight==0){
					Magnesium_excel_title.setText(nutrientBo.getNutrientSodium());
				}else{
					Magnesium_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientSodium())*weight*0.01f));
				}
			}else{
				Magnesium_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientZinc())){
				if(weight==0){
					Phosphorus_excel_title.setText(nutrientBo.getNutrientZinc());
				}else{
					Phosphorus_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientZinc())*weight*0.01f));
				}
			}else{
				Phosphorus_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientCopper())){
				if(weight==0){
					Potassium_excel_title.setText(nutrientBo.getNutrientCopper());
				}else{
					Potassium_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientCopper())*weight*0.01f));
				}
			}else{
				Potassium_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientManganese())){//磷
				if(weight==0){
					Sodium_excel_title.setText(nutrientBo.getNutrientManganese());
				}else{
					Sodium_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientManganese())*weight*0.01f));
				}
			}else{
				Sodium_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientSelenium())){
				if(weight==0){
					Zinc_excel_title.setText(nutrientBo.getNutrientSelenium());
				}else{
					Zinc_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientSelenium())*weight*0.01f));
				}
			}else{
				Zinc_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientVitc())){//納
				if(weight==0){
					Copper_excel_title.setText(nutrientBo.getNutrientVitc());
				}else{
					Copper_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientVitc())*weight*0.01f));
				}
			}else{
				Copper_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientThiamin())){
				if(weight==0){
					Manganese_excel_title.setText(nutrientBo.getNutrientThiamin());
				}else{
					Manganese_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientThiamin())*weight*0.01f));
				}
			}else{
				Manganese_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientRiboflavin())){//銅
				if(weight==0){
					Selenium_excel_title.setText(nutrientBo.getNutrientRiboflavin());
				}else{
					Selenium_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientRiboflavin())*weight*0.01f));
				}
			}else{
				Selenium_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientNiacin())){
				if(weight==0){
					Vit_C_excel_title.setText(nutrientBo.getNutrientNiacin());
				}else{
					Vit_C_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientNiacin())*weight*0.01f));
				}
			}else{
				Vit_C_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientPantoAcid())){//銫
				if(weight==0){
					Thiamin_excel_title.setText(nutrientBo.getNutrientPantoAcid());
				}else{
					Thiamin_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientPantoAcid())*weight*0.01f));
				}
			}else{
				Thiamin_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientVitB6())){
				if(weight==0){
					Riboflavin_excel_title.setText(nutrientBo.getNutrientVitB6());
				}else{
					Riboflavin_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientVitB6())*weight*0.01f));
				}
			}else{
				Riboflavin_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientFolateTot())){//硫胺素
				if(weight==0){
					Niacin_excel_title.setText(nutrientBo.getNutrientFolateTot());
				}else{
					Niacin_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientFolateTot())*weight*0.01f));
				}
			}else{
				Niacin_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientFolicAcid())){//核黄素
				if(weight==0){
					Panto_Acid_excel_title.setText(nutrientBo.getNutrientFolicAcid());
				}else{
					Panto_Acid_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientFolicAcid())*weight*0.01f));
				}
			}else{
				Panto_Acid_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientFoodFolate())){
				if(weight==0){
					Vit_B6_excel_title.setText(nutrientBo.getNutrientFoodFolate());
				}else{
					Vit_B6_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientFoodFolate())*weight*0.01f));
				}
			}else{
				Vit_B6_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientFolateDfe())){
				if(weight==0){
					Folate_Tot_excel_title.setText(nutrientBo.getNutrientFolateDfe());
				}else{
					Folate_Tot_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientFolateDfe())*weight*0.01f));
				}
			}else{
				Folate_Tot_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientCholineTot())){
				if(weight==0){
					Folic_Acid_excel_title.setText(nutrientBo.getNutrientCholineTot());
				}else{
					Folic_Acid_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientCholineTot())*weight*0.01f));
				}
			}else{
				Folic_Acid_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientVitB12())){
				if(weight==0){
					Food_Folate_excel_title.setText(nutrientBo.getNutrientVitB12());
				}else{
					Food_Folate_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientVitB12())*weight*0.01f));
				}
			}else{
				Food_Folate_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientVitAiu())){
				if(weight==0){
					Folate_DFE_excel_title.setText(nutrientBo.getNutrientVitAiu());
				}else{
					Folate_DFE_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientVitAiu())*weight*0.01f));
				}
			}else{
				Folate_DFE_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientVitArae())){
				if(weight==0){
					Choline_Tot_excel_title.setText(nutrientBo.getNutrientVitArae());
				}else{
					Choline_Tot_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientVitArae())*weight*0.01f));
				}
			}else{
				Choline_Tot_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientRetinol())){
				if(weight==0){
					Vit_B12_excel_title.setText(nutrientBo.getNutrientRetinol());
				}else{
					Vit_B12_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientRetinol())*weight*0.01f));
				}
			}else{
				Vit_B12_excel_title.setText("0.0");
			}




//
//			if(!TextUtils.isEmpty(nutrientBo.getNutrientVitB12())){
//				if(weight==0){
//					Vit_B12_excel_title.setText(nutrientBo.getNutrientVitB12());
//				}else{
//					Vit_B12_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientVitB12())*weight*0.01f));
//				}
//			}else{
//				Vit_B12_excel_title.setText("0.0");
//			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientVitAiu())){
				if(weight==0){
					Vit_A_IU_excel_title.setText(nutrientBo.getNutrientVitAiu());
				}else{
					Vit_A_IU_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientVitAiu())*weight*0.01f));
				}
			}else{
				Vit_A_IU_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientVitArae())){
				if(weight==0){
					Vit_A_RAE_excel_title.setText(nutrientBo.getNutrientVitArae());
				}else{
					Vit_A_RAE_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientVitArae())*weight*0.01f));
				}
			}else{
				Vit_A_RAE_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientRetinol())){
				if(weight==0){
					Retinol_excel_title.setText(nutrientBo.getNutrientRetinol());
				}else{
					Retinol_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientRetinol())*weight*0.01f));
				}
			}else{
				Retinol_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientAlphaCarot())){
				if(weight==0){
					Alpha_Carot_excel_title.setText(nutrientBo.getNutrientAlphaCarot());
				}else{
					Alpha_Carot_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientAlphaCarot())*weight*0.01f));
				}
			}else{
				Alpha_Carot_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientBetaCarot())){
				if(weight==0){
					Beta_Carot_excel_title.setText(nutrientBo.getNutrientBetaCarot());
				}else{
					Beta_Carot_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientBetaCarot())*weight*0.01f));
				}
			}else{
				Beta_Carot_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientBetaCrypt())){
				if(weight==0){
					Beta_Crypt_excel_title.setText(nutrientBo.getNutrientBetaCrypt());
				}else{
					Beta_Crypt_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientBetaCrypt())*weight*0.01f));
				}
			}else{
				Beta_Crypt_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientLycopene())){
				if(weight==0){
					Lycopene_excel_title.setText(nutrientBo.getNutrientLycopene());
				}else{
					Lycopene_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientLycopene())*weight*0.01f));
				}
			}else{
				Lycopene_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientLutZea())){
				if(weight==0){
					LutZea_excel_title.setText(nutrientBo.getNutrientLutZea());
				}else{
					LutZea_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientLutZea())*weight*0.01f));
				}
			}else{
				LutZea_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientVite())){
				if(weight==0){
					Vit_E_excel_title.setText(nutrientBo.getNutrientVite());
				}else{
					Vit_E_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientVite())*weight*0.01f));
				}
			}else{
				Vit_E_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientVitd())){
				if(weight==0){
					Vit_D_excel_title.setText(nutrientBo.getNutrientVitd());
				}else{
					Vit_D_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientVitd())*weight*0.01f));
				}
			}else{
				Vit_D_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientVitDiu())){
				if(weight==0){
					Vit_D_IU_excel_title.setText(nutrientBo.getNutrientVitDiu());
				}else{
					Vit_D_IU_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientVitDiu())*weight*0.01f));
				}
			}else{
				Vit_D_IU_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientVitK())){
				if(weight==0){
					Vit_K_excel_title.setText(nutrientBo.getNutrientVitK());
				}else{
					Vit_K_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientVitK())*weight*0.01f));
				}
			}else{
				Vit_K_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientFaSat())){
				if(weight==0){
					FA_Sat_excel_title.setText(nutrientBo.getNutrientFaSat());
				}else{
					FA_Sat_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientFaSat())*weight*0.01f));
				}
			}else{
				FA_Sat_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientFaMono())){
				if(weight==0){
					FA_Mono_excel_title.setText(nutrientBo.getNutrientFaMono());
				}else{
					FA_Mono_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientFaMono())*weight*0.01f));
				}
			}else{
				FA_Mono_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientFaPoly())){
				if(weight==0){
					FA_Poly_excel_title.setText(nutrientBo.getNutrientFaPoly());
				}else{
					FA_Poly_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientFaPoly())*weight*0.01f));
				}
			}else{
				FA_Poly_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientCholestrl())){
				if(weight==0){
					Cholesterol_excel_title.setText(nutrientBo.getNutrientCholestrl());
				}else{
					Cholesterol_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientCholestrl())*weight*0.01f));
				}
			}else{
				Cholesterol_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientGmWt1())){
				if(weight==0){
					GmWt_1_excel_title.setText(nutrientBo.getNutrientGmWt1());
				}else{
					GmWt_1_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientGmWt1())*weight*0.01f));
				}
			}else{
				GmWt_1_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientGmWt2())){
				if(weight==0){
					GmWt_2_excel_title.setText(nutrientBo.getNutrientGmWt2());
				}else{
					GmWt_2_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientGmWt2())*weight*0.01f));
				}
			}else{
				GmWt_2_excel_title.setText("0.0");
			}

			if(!TextUtils.isEmpty(nutrientBo.getNutrientRefusePct())){
				if(weight==0){
					Refuse_Pct_excel_title.setText(nutrientBo.getNutrientRefusePct());
				}else{
					Refuse_Pct_excel_title.setText(UtilTooth.keep2Point(Float.parseFloat(nutrientBo.getNutrientRefusePct())*weight*0.01f));
				}
			}else{
				Refuse_Pct_excel_title.setText("0.0");
			}


		}
	}

	public void onClickBack(View v) {
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			NotificationManager notificationManager = (NotificationManager) this.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
			notificationManager.cancel(0);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void natureSelectComplete(NutrientBo nutrient) {
		if(null!=nutrient){
			//重绘界面参数
			search_et.setText(nutrient.getNutrientDesc());
			selectNutrient = nutrient;
			setViewDate(selectNutrient, 0f);
		}
	}

}
