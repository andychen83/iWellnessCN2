package com.lefu.es.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lefu.es.entity.NutrientBo;
import com.lefu.iwellness.newes.cn.system.R;

public class NutrientInfoAdapter extends BaseAdapter {
	
	private List<NutrientBo> atrrArray;
    private Context          context;
    
    public NutrientInfoAdapter(Context _context, List<NutrientBo> arr) {
    	this.atrrArray = arr;
        this.context = _context;
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
	
	public void appendList(List<NutrientBo> ls) {
        if (!atrrArray.containsAll(ls) && ls != null && ls.size() > 0) {
        	atrrArray.addAll(ls);
        	notifyDataSetChanged();
        }
        
    }
	
	public void clearList(List<NutrientBo> ls) {
		if(null!=atrrArray){
			atrrArray.clear();
	    	atrrArray.addAll(ls);
	        notifyDataSetChanged();
		}
    	
    }


    public void clear() {
    	atrrArray.clear();
        notifyDataSetChanged();
    }

   
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder h = null;
        if (convertView == null) {
            h = new ViewHolder();
            convertView = View.inflate(context, R.layout.kitchen_info_header, null);

            h.atrrName = (TextView) convertView.findViewById(R.id.excel_name);

            h.excel_Carbohydrt = (TextView) convertView.findViewById(R.id.excel_Carbohydrt);
            h.excel_Energ = (TextView) convertView.findViewById(R.id.excel_Energ);
            h.excel_Protein = (TextView) convertView.findViewById(R.id.excel_Protein);
            h.excel_Lipid = (TextView) convertView.findViewById(R.id.excel_Lipid);
            h.excel_VitC = (TextView) convertView.findViewById(R.id.excel_VitC);
            h.excel_Fiber = (TextView) convertView.findViewById(R.id.excel_Fiber);
            h.excel_Cholesterol = (TextView) convertView.findViewById(R.id.excel_Cholesterol);
            h.excel_Calcium = (TextView) convertView.findViewById(R.id.excel_Calcium);
            convertView.setTag(h);
        } else {
            h = (ViewHolder) convertView.getTag();
        }
        h.atrrName.setText(atrrArray.get(position).getNutrientDesc());
        h.excel_Carbohydrt.setText(atrrArray.get(position).getNutrientCarbohydrt());
        h.excel_Energ.setText(atrrArray.get(position).getNutrientEnerg());
        h.excel_Protein.setText(atrrArray.get(position).getNutrientProtein());
        h.excel_Lipid.setText(atrrArray.get(position).getNutrientLipidTot());
        h.excel_VitC.setText(atrrArray.get(position).getNutrientVitc());
        h.excel_Fiber.setText(atrrArray.get(position).getNutrientFiberTD());
        h.excel_Cholesterol.setText(atrrArray.get(position).getNutrientCholestrl());
        h.excel_Calcium.setText(atrrArray.get(position).getNutrientCalcium());
        
        return convertView;
	}
	
	 class ViewHolder {
	        TextView  atrrName;
	        TextView  excel_Carbohydrt;
	        TextView  excel_Energ;
	        TextView  excel_Protein;
	        TextView  excel_Lipid;
	        TextView  excel_VitC;
	        TextView  excel_Fiber;
	        TextView  excel_Cholesterol;
	        TextView  excel_Calcium;
	    }

}
