package com.lefu.es.util;

import android.content.Context;
import android.util.DisplayMetrics;
/**
 * 显示工具类
 * @author Leon
 * 2015-11-19
 */
public class DisplayUtil {
	/** dp转px */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/** px转dp */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	/**获取屏幕宽度*/
	public static int getWidth(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();
		return dm.widthPixels;
	}
}
