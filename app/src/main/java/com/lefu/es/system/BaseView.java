package com.lefu.es.system;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
/**
 * 基础视图
 * @author Leon
 * 2015-11-19
 */
public class BaseView extends RelativeLayout {
	public Context context;

	public BaseView(Context context) {
		super(context);
		this.context = context;
	}

	public BaseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public BaseView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}
}
