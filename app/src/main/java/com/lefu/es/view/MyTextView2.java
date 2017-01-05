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
 * 自定义TextView2
 * @author Leon
 * 2015-11-19
 */
public class MyTextView2 extends LinearLayout {
	private Context context;
	private String tLeft;
	private String tRight;
	TextView tvST;

	public MyTextView2(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;

		init();
	}

	public MyTextView2(Context context) {
		super(context);
		this.context = context;
	}

	public void setTexts(String textLeft, String textRight) {
		tLeft = textLeft;
		tRight = textRight;
		init();
	}

	public void setTexts(String textLeft, String textRight, String stlb) {
		tLeft = textLeft;
		tRight = textRight;
		init(stlb);
	}

	public void init(String stlb) {
		boolean isadd = true;
		removeAllViewsInLayout();
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
		setOrientation(LinearLayout.HORIZONTAL);
		setGravity(Gravity.CENTER_VERTICAL);

		tvST = new TextView(context);
		tvST.setLayoutParams(params);
		tvST.setTextColor(Color.DKGRAY);
		tvST.setTextSize(12);
		tvST.setSingleLine(true);
		if (null == stlb || "".equals(stlb)) {
			isadd = false;
		}
		if ("0.0".equals(tLeft)) {
			tLeft = "0.0 " + getContext().getText(R.string.stlb_danwei).toString();
			isadd = false;
		}

		if (tLeft != null && tLeft.equals(getContext().getString(R.string.nodata_waring))) {
			isadd = false;
			tvST.setTextSize(12);
		}

		if (LoadingActivity.isPad) {
			tvST.setTextSize(20);
		}
		addView(tvST);

		if (tRight != null && tRight.indexOf("/") > 0) {
			isadd = false;
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
				tvSmall.setTextSize(12);
			}
			tvSmall.setTextColor(Color.DKGRAY);
			TextPaint paint = tvSmall.getPaint();
			paint.setFakeBoldText(true);
			tvSmall.setGravity(Gravity.CENTER_HORIZONTAL);
			tvSmall.setText(iSmall + "");

			View tvSplit = new View(context);
			tvSplit.setLayoutParams(new LayoutParams(20, 1));
			tvSplit.setBackgroundColor(Color.WHITE);

			TextView tvBig = new TextView(context);
			tvBig.setGravity(Gravity.CENTER_HORIZONTAL);
			tvBig.setTextColor(Color.DKGRAY);
			TextPaint paint2 = tvBig.getPaint();
			paint2.setFakeBoldText(true);
			tvBig.setTextSize(7);
			if (LoadingActivity.isPad) {
				tvBig.setTextSize(12);
			}
			tvBig.setText(iBig + " ");

			TextView danwei = new TextView(context);
			danwei.setGravity(Gravity.CENTER_VERTICAL);
			danwei.setTextColor(Color.DKGRAY);
			danwei.setTextSize(9);
			if (LoadingActivity.isPad) {
				danwei.setTextSize(20);
			}
			danwei.setText(" " + getContext().getText(R.string.stlb_danwei).toString());

			llRight.addView(tvSmall);
			llRight.addView(tvSplit);
			llRight.addView(tvBig);

			llRight1.addView(llRight);
			llRight1.addView(danwei);

			addView(llRight1);
		}

		if (isadd) {
			tvST.setText(tLeft + getContext().getText(R.string.stlb_danwei));
		} else {
			tvST.setText(tLeft);
		}

	}

	public void init() {
		removeAllViewsInLayout();
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
		setOrientation(LinearLayout.HORIZONTAL);
		setGravity(Gravity.CENTER_VERTICAL);

		tvST = new TextView(context);
		tvST.setLayoutParams(params);
		tvST.setTextColor(Color.DKGRAY);
		tvST.setTextSize(12);
		tvST.setSingleLine(true);
		if ("0.0".equals(tLeft)) {
			tLeft = "0.0 " + getContext().getText(R.string.stlb_danwei).toString();;
		}
		tvST.setText(tLeft);
		if (tLeft != null && tLeft.equals(getContext().getString(R.string.nodata_waring))) {
			tvST.setTextSize(12);
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
				tvSmall.setTextSize(12);
			}
			tvSmall.setTextColor(Color.DKGRAY);
			TextPaint paint = tvSmall.getPaint();
			paint.setFakeBoldText(true);
			tvSmall.setGravity(Gravity.CENTER_HORIZONTAL);
			tvSmall.setText(iSmall + "");

			View tvSplit = new View(context);
			tvSplit.setLayoutParams(new LayoutParams(20, 1));
			tvSplit.setBackgroundColor(Color.DKGRAY);

			TextView tvBig = new TextView(context);
			tvBig.setGravity(Gravity.CENTER_HORIZONTAL);
			tvBig.setTextColor(Color.DKGRAY);
			TextPaint paint2 = tvBig.getPaint();
			paint2.setFakeBoldText(true);
			tvBig.setTextSize(7);
			if (LoadingActivity.isPad) {
				tvBig.setTextSize(12);
			}
			tvBig.setText(iBig + " ");

			TextView danwei = new TextView(context);
			danwei.setGravity(Gravity.CENTER_VERTICAL);
			danwei.setTextColor(Color.DKGRAY);
			danwei.setTextSize(9);
			if (LoadingActivity.isPad) {
				danwei.setTextSize(20);
			}
			danwei.setText(" " + getContext().getText(R.string.stlb_danwei).toString());

			llRight.addView(tvSmall);
			llRight.addView(tvSplit);
			llRight.addView(tvBig);

			llRight1.addView(llRight);
			llRight1.addView(danwei);

			addView(llRight1);
		}

	}

}
