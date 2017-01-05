package com.lefu.es.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lefu.es.entity.NutrientAtrrBo;
import com.lefu.es.util.Tool;
import com.lefu.es.util.UtilTooth;
import com.lefu.iwellness.newes.cn.system.R;

public class NutrientDetailAdapter extends BaseAdapter {
	
	private List<NutrientAtrrBo> atrrArray;
    private Context          context;
    private float            mWeight;
    
    public NutrientDetailAdapter(Context _context, List<NutrientAtrrBo> arr,float mWeight) {
    	this.atrrArray = arr;
        this.context = _context;
        this.mWeight = mWeight;
    }

	@Override
	public int getCount() {

		 if (null == atrrArray || atrrArray.size() == 0) {
	            return 0;
	        }
	        return atrrArray.size();
	}

	@Override
	public Object getItem(int position) {
		 if (null == atrrArray || atrrArray.size() == 0) {
	            return null;
	        }
	        return atrrArray.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder h = null;
        if (convertView == null) {
            h = new ViewHolder();
            convertView = View.inflate(context, R.layout.naturin_listview_item, null);

            h.atrrName = (TextView) convertView.findViewById(R.id.name_tv);

            h.atrrValue = (TextView) convertView.findViewById(R.id.value_tv);
            convertView.setTag(h);
        } else {
            h = (ViewHolder) convertView.getTag();
        }
        h.atrrName.setText(atrrArray.get(position).getAttrName());
        if(!TextUtils.isEmpty(atrrArray.get(position).getAttrValue()) && Tool.isDigitsOnly(atrrArray.get(position).getAttrValue())){
			float water =  Float.parseFloat(atrrArray.get(position).getAttrValue())*mWeight*0.01f;
			h.atrrValue.setText(UtilTooth.keep2Point(water)+"");
		}else{
			h.atrrValue.setText("0.0");
		}
        return convertView;
	}
	
	 class ViewHolder {
	        TextView  atrrName;
	        TextView  atrrValue;
	    }

}
