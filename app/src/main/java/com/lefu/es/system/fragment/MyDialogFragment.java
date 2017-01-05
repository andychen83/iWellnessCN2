package com.lefu.es.system.fragment;

import java.io.Serializable;
import java.util.ArrayList;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.lefu.es.entity.NutrientBo;
import com.lefu.iwellness.newes.cn.system.R;

public class MyDialogFragment extends DialogFragment implements OnItemClickListener {
	
	ArrayList<NutrientBo> nutrientList = null;
	
	ListView mylist;
	
	public static MyDialogFragment newInstance(ArrayList<NutrientBo> nutrientList) {
	    MyDialogFragment f = new MyDialogFragment();

	    // Supply num input as an argument.
	    Bundle extras = new Bundle();
	    extras.putSerializable("nutrientList", (Serializable)nutrientList);
	    
	    f.setArguments(extras);

	    return f;
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		final DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

		final WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
		layoutParams.width = dm.widthPixels;
		layoutParams.height = getResources().getDimensionPixelOffset(R.dimen.dialog_four_items);
		layoutParams.gravity = Gravity.TOP;
		getDialog().getWindow().setAttributes(layoutParams);
	}
	
	
	
	public interface NatureSelectListener  
    {  
        void natureSelectComplete(NutrientBo nutrient);  
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    nutrientList = (ArrayList<NutrientBo>)getArguments().getSerializable("nutrientList");
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_fragment_kitchen, null, false);
		mylist = (ListView) view.findViewById(R.id.list);
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(null!=nutrientList){
			final int size =  nutrientList.size();
			ArrayList<String> nutrientNames = new ArrayList<String>();
			for (NutrientBo nutrientBo : nutrientList) {
				nutrientNames.add(nutrientBo.getNutrientDesc());
			}
			String[] listitems = (String[])nutrientNames.toArray(new String[size]);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listitems);
			mylist.setAdapter(adapter);
			mylist.setOnItemClickListener(this);
		}
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		dismiss();
		NatureSelectListener listener = (NatureSelectListener) getActivity();  
         listener.natureSelectComplete(nutrientList.get(position));  
	}

}
