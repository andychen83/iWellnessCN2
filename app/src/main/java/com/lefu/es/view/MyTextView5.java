package com.lefu.es.view;

import android.content.Context;
import android.graphics.Color;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lefu.es.system.LoadingActivity;
import com.lefu.iwellness.newes.cn.system.R;

/**
 * 自定义TextView
 * @author Leon
 * 2015-11-19
 */
public class MyTextView5 extends LinearLayout {
	private Context context;
	private String tLeft;
	private String tRight;
	TextView tvST;

	public MyTextView5(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;

		init();
	}

	public MyTextView5(Context context) {
		super(context);
		this.context = context;
	}

	public void setTexts(String textLeft,String textRight){
		tLeft=textLeft;
	    tRight = textRight;
	    init();
		
	}
	
	public void init(){
		removeAllViewsInLayout();
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
		setOrientation(LinearLayout.HORIZONTAL);
		setGravity(Gravity.CENTER);
		
		tvST = new TextView(context);
		tvST.setLayoutParams(params);
		tvST.setTextColor(getResources().getColor(R.color.main_text_color));
		if(LoadingActivity.isPad){
			if(LoadingActivity.isV){
				tvST.setTextSize(120);
			}else{
				tvST.setTextSize(80);
			}
		}else{
			tvST.setTextSize(30);
		}
		tvST.setText(tLeft);
		if(tLeft!=null && tLeft.equals(getContext().getString(R.string.nodata_waring))){
			if(LoadingActivity.isPad){
				tvST.setTextSize(30);
			}else{
				tvST.setTextSize(30);
			}
		}
		
		addView(tvST);
		
		if(tRight!=null && tRight.indexOf("/")>0){
			String[] sT=tRight.split("/");
			int iSmall=Integer.parseInt(sT[0]);
			int iBig=Integer.parseInt(sT[1]);
			LinearLayout llRight = new LinearLayout(context);
			params.leftMargin=20;
			llRight.setLayoutParams(params);
			
			llRight.setOrientation(LinearLayout.VERTICAL);
			
			TextView tvSmall = new TextView(context);
			tvSmall.setTextSize(18);
			tvSmall.setTextColor(getResources().getColor(R.color.main_text_color));
			TextPaint paint = tvSmall.getPaint();  
			paint.setFakeBoldText(true);  
			tvSmall.setGravity(Gravity.CENTER_HORIZONTAL);
			tvSmall.setText(iSmall+"");
			
			View tvSplit = new View(context);
			tvSplit.setLayoutParams(new LayoutParams(50, 4));
			tvSplit.setBackgroundColor(getResources().getColor(R.color.main_text_color));
			
			TextView tvBig = new TextView(context);
			tvBig.setGravity(Gravity.CENTER_HORIZONTAL);
			tvBig.setTextColor(getResources().getColor(R.color.main_text_color));
			TextPaint paint2 = tvBig.getPaint();  
			paint2.setFakeBoldText(true);  
			tvBig.setTextSize(18);
			tvBig.setText(iBig+"");
			
			
			if(LoadingActivity.isPad){
				if(LoadingActivity.isV){
					tvSplit.setLayoutParams(new LayoutParams(80, 4));
					tvSmall.setTextSize(75);
					tvBig.setTextSize(75);
				}else{
					tvSplit.setLayoutParams(new LayoutParams(40, 4));
					tvSmall.setTextSize(30);
					tvBig.setTextSize(30);
				}
			}
			llRight.addView(tvSmall);
			llRight.addView(tvSplit);
			llRight.addView(tvBig);
			
			addView(llRight);
		}
	}

}
