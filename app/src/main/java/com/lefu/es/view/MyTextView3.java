package com.lefu.es.view;

import com.lefu.es.system.LoadingActivity;
import com.lefu.iwellness.newes.cn.system.R;

import android.content.Context;
import android.graphics.Color;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 自定义TextView3
 * @author Leon
 * 2015-11-19
 */
public class MyTextView3 extends LinearLayout {
	private Context context;
	private String tLeft;
	private String tRight;
	TextView tvST;
	TextView tvSmall;
	TextView tvBig;
	TextView danwei;
	
	
	public MyTextView3(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}
	
	public MyTextView3(Context context) {
		super(context);
		this.context = context;
	}


	public void setTexts(String textLeft,String textRight){
		tLeft=textLeft;
	    tRight = textRight;
	    init();
		
	}
	
	public void setTexts(String textLeft,String textRight,boolean white){
		tLeft=textLeft;
	    tRight = textRight;
	    init(white);
	}
	
	public void setTextWhiteColor(){
		if(null!=tvST)tvST.setTextColor(context.getResources().getColor( R.color.white));
		if(null!=tvSmall){
			tvSmall.setTextColor(context.getResources().getColor( R.color.white));
		}
		if(null!=tvBig){
			tvBig.setTextColor(context.getResources().getColor( R.color.white));
		}
		if(null!=danwei){
			danwei.setTextColor(context.getResources().getColor( R.color.white));
		}
	}
	
	public void setTexBlackColor(){
		if(null!=tvST)tvST.setTextColor(context.getResources().getColor( R.color.black));
		if(null!=tvSmall){
			tvSmall.setTextColor(context.getResources().getColor( R.color.black));
		}
		if(null!=tvBig){
			tvBig.setTextColor(context.getResources().getColor( R.color.black));
		}
		if(null!=danwei){
			danwei.setTextColor(context.getResources().getColor( R.color.black));
		}
	}
	
	public void init(boolean white){
		removeAllViewsInLayout();
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
		setOrientation(LinearLayout.HORIZONTAL);
		setGravity(Gravity.CENTER_VERTICAL);
		
		tvST = new TextView(context);
		tvST.setLayoutParams(params);
		tvST.setTextColor(Color.WHITE);
		tvST.setTextSize(14);
		if("0:0".equals(tLeft)){
			tLeft="0:0 "+getContext().getText(R.string.stlb_danwei).toString();
		}
		tvST.setText(tLeft);
		if(tLeft!=null){
			if(tLeft.equals(getContext().getString(R.string.nodata_waring))){
				tvST.setTextSize(20);
			}
		}
		if(LoadingActivity.isPad){
			tvST.setTextSize(30);
		}
		
		addView(tvST);
		
		if(tRight!=null && tRight.indexOf("/")>0){
			String[] sT=tRight.split("/");
			int iSmall=Integer.parseInt(sT[0]);
			int iBig=Integer.parseInt(sT[1]);
			LinearLayout llRight1 = new LinearLayout(context);
			llRight1.setOrientation(LinearLayout.HORIZONTAL);
			LinearLayout llRight = new LinearLayout(context);
			params.leftMargin=20;
			llRight.setLayoutParams(params);
			
			llRight.setOrientation(LinearLayout.VERTICAL);
			
		    tvSmall = new TextView(context);
			tvSmall.setTextSize(8);
			if(LoadingActivity.isPad){
				tvSmall.setTextSize(14);
			}
			tvSmall.setTextColor(Color.WHITE);
			TextPaint paint = tvSmall.getPaint();  
			paint.setFakeBoldText(true);  
			tvSmall.setGravity(Gravity.CENTER_HORIZONTAL);
			tvSmall.setText(iSmall+"");
			
			View tvSplit = new View(context);
			tvSplit.setLayoutParams(new LayoutParams(20, 1));
			tvSplit.setBackgroundColor(Color.WHITE);
			
			tvBig = new TextView(context);
			tvBig.setGravity(Gravity.CENTER_HORIZONTAL);
			tvBig.setTextColor(Color.WHITE);
			TextPaint paint2 = tvBig.getPaint();  
			paint2.setFakeBoldText(true); 
			
			danwei = new TextView(context);
			danwei.setGravity(Gravity.CENTER_HORIZONTAL);
			danwei.setTextColor(Color.WHITE);
			danwei.setTextSize(10);
			if(LoadingActivity.isPad){
				danwei.setTextSize(20);
			}
			
			tvBig.setTextSize(8);

			if(LoadingActivity.isPad){
				tvBig.setTextSize(20);
			}
			tvBig.setText(iBig+"");
			danwei.setText(getContext().getText(R.string.stlb_danwei));
			llRight.addView(tvSmall);
			llRight.addView(tvSplit);
			llRight.addView(tvBig);
			llRight1.addView(llRight);
			llRight1.addView(danwei);
			
			addView(llRight1);
			
		}else{
			
		}

		
	}
	
	public void init(){
		removeAllViewsInLayout();
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
		setOrientation(LinearLayout.HORIZONTAL);
		setGravity(Gravity.CENTER_VERTICAL);
		
		tvST = new TextView(context);
		tvST.setLayoutParams(params);
		tvST.setTextColor(Color.BLACK);
		tvST.setTextSize(14);
		if("0:0".equals(tLeft)){
			tLeft="0:0 "+getContext().getText(R.string.stlb_danwei).toString();
		}
		tvST.setText(tLeft);
		if(tLeft!=null){
			if(tLeft.equals(getContext().getString(R.string.nodata_waring))){
				tvST.setTextSize(20);
			}
		}
		if(LoadingActivity.isPad){
			tvST.setTextSize(30);
		}
		
		addView(tvST);
		
		if(tRight!=null && tRight.indexOf("/")>0){
			String[] sT=tRight.split("/");
			int iSmall=Integer.parseInt(sT[0]);
			int iBig=Integer.parseInt(sT[1]);
			LinearLayout llRight1 = new LinearLayout(context);
			llRight1.setOrientation(LinearLayout.HORIZONTAL);
			LinearLayout llRight = new LinearLayout(context);
			params.leftMargin=20;
			llRight.setLayoutParams(params);
			
			llRight.setOrientation(LinearLayout.VERTICAL);
			
		    tvSmall = new TextView(context);
			tvSmall.setTextSize(8);
			if(LoadingActivity.isPad){
				tvSmall.setTextSize(14);
			}
			tvSmall.setTextColor(Color.BLACK);
			TextPaint paint = tvSmall.getPaint();  
			paint.setFakeBoldText(true);  
			tvSmall.setGravity(Gravity.CENTER_HORIZONTAL);
			tvSmall.setText(iSmall+"");
			
			View tvSplit = new View(context);
			tvSplit.setLayoutParams(new LayoutParams(20, 1));
			tvSplit.setBackgroundColor(Color.BLACK);
			
			tvBig = new TextView(context);
			tvBig.setGravity(Gravity.CENTER_HORIZONTAL);
			tvBig.setTextColor(Color.BLACK);
			TextPaint paint2 = tvBig.getPaint();  
			paint2.setFakeBoldText(true); 
			
			danwei = new TextView(context);
			danwei.setGravity(Gravity.CENTER_HORIZONTAL);
			danwei.setTextColor(Color.BLACK);
			danwei.setTextSize(10);
			if(LoadingActivity.isPad){
				danwei.setTextSize(20);
			}
			
			tvBig.setTextSize(8);

			if(LoadingActivity.isPad){
				tvBig.setTextSize(20);
			}
			tvBig.setText(iBig+"");
			danwei.setText(getContext().getText(R.string.stlb_danwei));
			llRight.addView(tvSmall);
			llRight.addView(tvSplit);
			llRight.addView(tvBig);
			llRight1.addView(llRight);
			llRight1.addView(danwei);
			
			addView(llRight1);
		}
	}

}
