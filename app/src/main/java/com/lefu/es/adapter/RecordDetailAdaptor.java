package com.lefu.es.adapter;

import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import com.lefu.es.util.StringUtils;
import com.lefu.es.util.UtilTooth;
import com.lefu.es.view.MyTextView3;
import com.lefu.iwellness.newes.cn.system.R;

/**
 * 记录详细适配器
 * 
 * @author Leon 2015-11-17
 */
public class RecordDetailAdaptor extends BaseAdapter {
	Locale local = Locale.getDefault();
	private List<Records> users;
	private int resource;
	public int selectedPosition = -1;
	public float selectedPositionY = 0;
	private int weightType = 1;
	String scaleT = "";
	ListView listView;
	Context cont;
	public RecordDetailAdaptor(Context cont, List<Records> users, ListView lv, int resource) {
		this.listView = lv;
		this.users = users;
		this.resource = resource;
		inflater = (LayoutInflater) cont.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.cont = cont;
		Log.v("tag", "lsw:" + UtilConstants.CHOICE_KG);
		if (users != null && users.size() > 0) {
			Records item = users.get(0);
			scaleT = item.getScaleType();
			if (scaleT.equals(UtilConstants.KITCHEN_SCALE)) {
				if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_LB)) {
					weightType = 6;
				}else if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_FATLB)) {
					weightType = 5;
				}else if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_KG)) {
					weightType = 1;
				}else if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_ML)) {
					weightType = 7;
				}else if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_ML2)) {
					weightType = 8;
				}else{
					weightType = 1;
				}
			}else{
				if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_KG)) {
					weightType = 1;
				} else if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_LB)) {
					//修改婴儿称列表单位
					if (scaleT.equals(UtilConstants.BODY_SCALE) || scaleT.equals(UtilConstants.BATHROOM_SCALE)) {
						weightType = 2;
					} else if(scaleT.equals(UtilConstants.BABY_SCALE)){
						weightType = 5;
					}
				} else if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_ST)) {
					//修改婴儿称列表单位
					if (scaleT.equals(UtilConstants.BODY_SCALE)) {
						weightType = 3;

					} else if (scaleT.equals(UtilConstants.BATHROOM_SCALE)) {
						weightType = 4;
					}else if(scaleT.equals(UtilConstants.BABY_SCALE)){
						weightType = 5;
					} 
					
				} else if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_FATLB)) {
					if (scaleT.equals(UtilConstants.BODY_SCALE) || scaleT.equals(UtilConstants.BATHROOM_SCALE)) {
						weightType = 2;
					} else if(scaleT.equals(UtilConstants.BABY_SCALE)){
						weightType = 5;
					}
				}else{
					weightType = 1;
				}
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
				if (scaleT.equals(UtilConstants.BABY_SCALE)) {
					mWeight = UtilTooth.round(user.getRweight(), 2) + "";
				} else {
					mWeight = user.getRweight() + "";
				}

				if (selectedPosition != posintion)
					if(UtilConstants.KITCHEN_SCALE.equals(UtilConstants.CURRENT_SCALE)){
						nameView.setTexts(mWeight + cont.getText(R.string.g_danwei), null);
					}else{
						nameView.setTexts(mWeight + cont.getText(R.string.kg_danwei), null);
					}
					
				else {
					if(UtilConstants.KITCHEN_SCALE.equals(UtilConstants.CURRENT_SCALE)){
						nameView.setTexts(mWeight + cont.getText(R.string.g_danwei), null, true);
					}else{
						nameView.setTexts(mWeight + cont.getText(R.string.kg_danwei), null, true);
					}
					
				}
				break;
			case 2 :
				mWeight=UtilTooth.onePoint(UtilTooth.kgToLB_ForFatScale3(user.getRweight()));
				if (selectedPosition != posintion)
					nameView.setTexts(mWeight + cont.getText(R.string.lb_danwei), null);
				else {
					nameView.setTexts(mWeight + cont.getText(R.string.lb_danwei), null, true);
				}
				break;
			case 3 :
				String[] fatTemp = UtilTooth.kgToStLbForScaleFat2(user.getRweight());
				if (fatTemp[1] != null && fatTemp[1].indexOf("/") > 0) {
					if (selectedPosition != posintion)
						nameView.setTexts(fatTemp[0], fatTemp[1]);
					else {
						nameView.setTexts(fatTemp[0], fatTemp[1], true);
					}
				} else {
					if (selectedPosition != posintion)
						nameView.setTexts(fatTemp[0] + cont.getText(R.string.stlb_danwei), null);
					else {
						nameView.setTexts(fatTemp[0] + cont.getText(R.string.stlb_danwei), null, true);
					}
				}
				break;
			case 4 :
				mWeight = UtilTooth.kgToStLb(user.getRweight());
				if (selectedPosition != posintion)
					nameView.setTexts(mWeight + cont.getText(R.string.stlb_danwei), null);
				else {
					nameView.setTexts(mWeight + cont.getText(R.string.stlb_danwei), null, true);
				}
				break;
			case 5 :
				nameView.setTextWhiteColor();
				if(UtilConstants.KITCHEN_SCALE.equals(UtilConstants.CURRENT_SCALE)){
					mWeight = UtilTooth.kgToFloz(user.getRweight());
					if (selectedPosition != posintion)
						nameView.setTexts(mWeight+ cont.getText(R.string.oz_danwei2), null);
					else {
						nameView.setTexts(mWeight+ cont.getText(R.string.oz_danwei2), null, true);
					}
				}else if(UtilConstants.BABY_SCALE.equals(UtilConstants.CURRENT_SCALE)){
					String text = UtilTooth.lbozToString(user.getRweight());
					if (selectedPosition != posintion)
						nameView.setTexts(text, null);
					else {
						nameView.setTexts(text, null, true);
					}
				}else{
					mWeight = UtilTooth.kgToLbForScaleBaby(user.getRweight());
					if (null != mWeight && mWeight.indexOf(":") > 0) {
						mWeight = mWeight.substring(0, mWeight.indexOf(":")) + cont.getText(R.string.lb_danwei) + ":" + mWeight.substring(mWeight.indexOf(":") + 1, mWeight.length()) + cont.getText(R.string.oz_danwei);
						if (selectedPosition != posintion)
							nameView.setTexts(mWeight, null);
						else {
							nameView.setTexts(mWeight, null, true);
						}
					} else {
						if (selectedPosition != posintion)
							nameView.setTexts(mWeight, null);
						else {
							nameView.setTexts(mWeight, null, true);
						}
					}
				}
				

				break;
			case 6 :
				mWeight = UtilTooth.kgToLBoz(user.getRweight());
				if (selectedPosition != posintion)
					nameView.setTexts(mWeight + cont.getText(R.string.lboz_danwei), null);
				else {
					nameView.setTexts(mWeight + cont.getText(R.string.lboz_danwei), null, true);
				}
				break;
			case 7 :
				mWeight = user.getRweight() + "";
				if (selectedPosition != posintion)
					nameView.setTexts(mWeight + cont.getText(R.string.ml_danwei), null);
					
				else {
					nameView.setTexts(mWeight + cont.getText(R.string.ml_danwei), null, true);
				}
				break;
			case 8 :
				mWeight = UtilTooth.kgToML(user.getRweight()) + "";
				if (selectedPosition != posintion)
					nameView.setTexts(mWeight + cont.getText(R.string.ml_danwei2), null);
					
				else {
					nameView.setTexts(mWeight + cont.getText(R.string.ml_danwei2), null, true);
				}
				break;
				
			case 9 :
				mWeight = UtilTooth.kgToLB_new(user.getRweight()) + "";
				if (selectedPosition != posintion)
					nameView.setTexts(mWeight + cont.getText(R.string.lb_danwei), null);
					
				else {
					nameView.setTexts(mWeight + cont.getText(R.string.lb_danwei), null, true);
				}
				break;
				
		}
		if(UtilConstants.KITCHEN_SCALE.equals(UtilConstants.CURRENT_SCALE)){
			if(TextUtils.isEmpty(user.getRphoto())){
				bmiView.setText("");
			}else{
				bmiView.setText(user.getRphoto());
			}
		}else{
			float bmi = UtilTooth.countBMI2(user.getRweight(), (UtilConstants.CURRENT_USER.getBheigth() / 100));
			bmiView.setText(UtilTooth.myround(bmi)+"");
		}
		
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
