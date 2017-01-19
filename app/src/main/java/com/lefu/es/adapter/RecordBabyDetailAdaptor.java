package com.lefu.es.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lefu.es.constant.UtilConstants;
import com.lefu.es.entity.Records;
import com.lefu.es.entity.UserModel;
import com.lefu.es.util.StringUtils;
import com.lefu.es.util.UtilTooth;
import com.lefu.es.view.MyTextView3;
import com.lefu.iwellness.newes.cn.system.R;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 抱嬰记录详细适配器
 * 
 * @author Leon 2015-11-17
 */
public class RecordBabyDetailAdaptor extends BaseAdapter {
	Locale local = Locale.getDefault();
	private List<Records> users;
	private int resource;
	public int selectedPosition = -1;
	public float selectedPositionY = 0;
	private int weightType = 1;
	String scaleT = "";
	ListView listView;
	Context cont;
	UserModel userModel = null;
	public RecordBabyDetailAdaptor(Context cont, List<Records> users, ListView lv, int resource,UserModel user) {
		this.listView = lv;
		this.users = users;
		this.resource = resource;
		this.userModel = user;
		inflater = (LayoutInflater) cont.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.cont = cont;
		Log.v("tag", "lsw:" + UtilConstants.CHOICE_KG);
		if (users != null && users.size() > 0) {
			Records item = users.get(0);
			scaleT = item.getScaleType();
			if (userModel.getDanwei().equals(UtilConstants.UNIT_LB) || userModel.getDanwei().equals(UtilConstants.UNIT_FATLB) || userModel.getDanwei().equals(UtilConstants.UNIT_ST)) {
				weightType = 5;
			} else {
				weightType = 1;
			}
			
		}
	}

	@Override
	public int getCount() {
		return users.size();
	}

	@Override
	public Object getItem(int arg0) {
		return users.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	public void setSelectedPosition(int position) {
		selectedPosition = position;
	}
	@Override
	public View getView(int posintion, View convertView, ViewGroup viewGroup) {
		TextView groupView = null;
		MyTextView3 nameView = null;
		TextView bmiView = null;
		LinearLayout rlayout = null;
		ImageView photoView = null;
		if (null == convertView) {
			convertView = inflater.inflate(resource, null);
			groupView = (TextView) convertView.findViewById(R.id.textView_date);
			nameView = (MyTextView3) convertView.findViewById(R.id.textView_weight);
			bmiView = (TextView) convertView.findViewById(R.id.textView_bmi);
			rlayout = (LinearLayout) convertView.findViewById(R.id.recorditem_title);
			photoView = (ImageView) convertView.findViewById(R.id.iv_photo);
			ViewCache cache = new ViewCache();
			cache.groupView = groupView;
			cache.nameView = nameView;
			cache.bmiView = bmiView;
			cache.rlayout = rlayout;
			cache.photoView = photoView;
			convertView.setTag(cache);
		} else {
			ViewCache cache = (ViewCache) convertView.getTag();
			groupView = cache.groupView;
			nameView = cache.nameView;
			bmiView = cache.bmiView;
			rlayout = cache.rlayout;
			photoView = cache.photoView;
		}
		Records user = users.get(posintion);
		if (null == user.getRecordTime() || "".equals(user.getRecordTime().trim())) {
			user.setRecordTime("No Date");
		}
		if (selectedPosition == posintion) {
			groupView.setSelected(true);
			nameView.setPressed(true);
			bmiView.setPressed(true);
			groupView.setTextColor(cont.getResources().getColor(R.color.white));
			nameView.setTextWhiteColor();
			bmiView.setTextColor(cont.getResources().getColor(R.color.white));
			rlayout.setBackgroundDrawable(cont.getResources().getDrawable(R.drawable.list_item_bg));
		} else {
			groupView.setSelected(false);
			nameView.setPressed(false);
			bmiView.setPressed(false);
			groupView.setTextColor(cont.getResources().getColor(R.color.black));
			nameView.setTexBlackColor();
			bmiView.setTextColor(cont.getResources().getColor(R.color.black));
			rlayout.setBackgroundColor(Color.WHITE);
		}
		Date iDate = UtilTooth.stringToTime(user.getRecordTime());
		if (iDate != null) {
			
			groupView.setText(StringUtils.getDateString(iDate, 5));
		}

		if (user.getRphoto() != null && user.getRphoto().length() > 3) {
			photoView.setVisibility(View.VISIBLE);
		} else {
			photoView.setVisibility(View.INVISIBLE);
		}
		String mWeight = "";

		switch (weightType) {
			case 1 :
				mWeight = UtilTooth.round(user.getRweight(), 2) + "";

				if (selectedPosition != posintion)
					nameView.setTexts(mWeight + cont.getText(R.string.kg_danwei), null);
					
				else {
					nameView.setTexts(mWeight + cont.getText(R.string.kg_danwei), null, true);
				}
				break;

			case 5 :
				nameView.setTextWhiteColor();
				String text = UtilTooth.lbozToString(user.getRweight());
				if (selectedPosition != posintion)
					nameView.setTexts(text, null);
				else {
					nameView.setTexts(text, null, true);
				}

				break;

				
		}
		float bmi = UtilTooth.countBMI2(user.getRweight(), (UtilConstants.CURRENT_USER.getBheigth() / 100));
		bmiView.setText(UtilTooth.myround(bmi)+"");
		
		return convertView;
	}

	private final class ViewCache {
		public TextView groupView;
		public MyTextView3 nameView;
		public TextView bmiView;
		public LinearLayout rlayout;
		public ImageView photoView;
	}

	private LayoutInflater inflater;

}
