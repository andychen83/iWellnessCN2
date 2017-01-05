package com.lefu.es.adapter;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
/*
 * 我的页面
 */
public class MyPageAdapter extends PagerAdapter {
	private ArrayList<View> listViews;

	public MyPageAdapter(ArrayList<View> listViews) {
		this.listViews = listViews;
	}

	public void setListViews(ArrayList<View> listViews) {
		if (this.listViews != null && this.listViews.size() > 0) {
			this.listViews.clear();
		}
		this.listViews = listViews;
	}

	@Override
	public int getCount() {
		if (listViews == null) {
			return 0;
		}
		return listViews.size();
	}

	@Override
	public void destroyItem(View view, int position, Object arg2) {
		if (listViews == null || listViews.size() <= position) {
			return;
		}
		if (listViews.get(position) != null) {
			((ViewPager) view).removeView(listViews.get(position));
		}
	}

	@Override
	public void finishUpdate(View arg0) {
	}

	@Override
	public Object instantiateItem(View arg0, int arg1) {
		try {
			if (null != listViews && listViews.size() > 0 && arg1 < listViews.size())
				((ViewPager) arg0).addView(listViews.get(arg1), 0);
		} catch (Exception e) {
			Log.e("zhou", "exception��" + e.getMessage());
		}
		if (null != listViews && listViews.size() > 0 && arg1 < listViews.size())
			return listViews.get(arg1);
		else {
			return null;
		}
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

}
