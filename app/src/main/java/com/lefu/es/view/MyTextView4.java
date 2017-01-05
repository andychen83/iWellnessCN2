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
 * 自定义TextView4
 * @author Leon
 * 2015-11-19
 */
public class MyTextView4 extends LinearLayout {
	private Context context;
	private String tLeft;
	private String tRight;
	TextView tvST;

	public MyTextView4(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	public MyTextView4(Context context) {
		super(context);
		this.context = context;
	}

	public void setTexts(String textLeft, String textRight) {
		tLeft = textLeft;
		tRight = textRight;
		init();
	}

	public void init() {
		removeAllViewsInLayout();
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
		setOrientation(LinearLayout.HORIZONTAL);
		setGravity(Gravity.CENTER_VERTICAL);

		tvST = new TextView(context);
		tvST.setLayoutParams(params);
		tvST.setTextColor(Color.BLACK);
		tvST.setTextSize(14);
		if ("0:0".equals(tLeft)) {
			tLeft = "0:0 " + getContext().getText(R.string.stlb_danwei).toString();
			tLeft = "0:0 " ;
		}
		tvST.setText(tLeft);
		if (tLeft != null) {
			if (tLeft.equals(getContext().getString(R.string.nodata_waring))) {
				tvST.setTextSize(14);
			}
		}
		if (LoadingActivity.isPad) {
			tvST.setTextSize(20);
		}

		addView(tvST);

		if (tRight != null && tRight.indexOf("/") > 0) {
			String[] sT = tRight.split("/");
			int iSmall = Integer.parseInt(sT[0]);
			int iBig = Integer.parseInt(sT[1]);
			LinearLayout llRight1 = new LinearLayout(context);
			llRight1.setOrientation(LinearLayout.HORIZONTAL);
			LinearLayout llRight = new LinearLayout(context);
			params.leftMargin = 20;
			llRight.setLayoutParams(params);

			llRight.setOrientation(LinearLayout.VERTICAL);

			TextView tvSmall = new TextView(context);
			tvSmall.setTextSize(7);
			if (LoadingActivity.isPad) {
				tvSmall.setTextSize(14);
			}
			tvSmall.setTextColor(Color.BLACK);
			TextPaint paint = tvSmall.getPaint();
			paint.setFakeBoldText(true);
			tvSmall.setGravity(Gravity.CENTER_HORIZONTAL);
			tvSmall.setText(iSmall + "");

			View tvSplit = new View(context);
			tvSplit.setLayoutParams(new LayoutParams(20, 1));
			tvSplit.setBackgroundColor(Color.BLACK);

			TextView tvBig = new TextView(context);
			tvBig.setGravity(Gravity.CENTER_HORIZONTAL);
			tvBig.setTextColor(Color.BLACK);
			TextPaint paint2 = tvBig.getPaint();
			paint2.setFakeBoldText(true);

			TextView danwei = new TextView(context);
			danwei.setGravity(Gravity.CENTER_HORIZONTAL);
			danwei.setTextColor(Color.BLACK);
			danwei.setTextSize(9);
			if (LoadingActivity.isPad) {
				danwei.setTextSize(20);
			}

			tvBig.setTextSize(7);

			if (LoadingActivity.isPad) {
				tvBig.setTextSize(14);
			}
			tvBig.setText(iBig + "");
			//danwei.setText(getContext().getText(R.string.stlb_danwei));
			llRight.addView(tvSmall);
			llRight.addView(tvSplit);
			llRight.addView(tvBig);
			llRight1.addView(llRight);
			llRight1.addView(danwei);

			addView(llRight1);
		}
	}

}
