package com.lefu.es.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lefu.es.constant.BluetoothTools;
import com.lefu.es.constant.UtilConstants;
import com.lefu.es.db.DBOpenHelper;
import com.lefu.es.entity.UserModel;
import com.lefu.es.service.UserService;
import com.lefu.iwellness.newes.cn.system.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 抱婴列表适配器
 * @author Leon
 * 2015-11-17
 */
public class BabylistviewAdapter extends BaseAdapter {
	public List<UserModel> users = new ArrayList<UserModel>();
	private int resource;
	private int selectedPosition = -1;
	private UserService uservice;
	private Context cont;
	private boolean isEdit;
	private Bitmap bitmap;
	private UserModel user;
	public BabylistviewAdapter(Context cont, int resource, List<UserModel> users) {
		this.cont = cont;
		this.resource = resource;
		this.users = users;
		inflater = (LayoutInflater) cont.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void clearALL(List<UserModel> ls){
		users.clear();
		users.addAll(ls);
		notifyDataSetChanged();
	}

	public void setEdit(boolean isEdit) {
		this.isEdit = isEdit;
	}

	@Override
	public int getCount() {
		if (null == users || 0 == users.size()) {
			return 0;
		}
		return users.size();
	}

	@Override
	public Object getItem(int arg0) {
		if (null == users || 0 == users.size()) {
			return null;
		}
		return users.get(arg0);
	}

	public int getItemID(int index) {
		return users.get(index).getId();
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	public void setSelectedPosition(int position) {
		selectedPosition = position;
	}

	@Override
	public View getView(final int posintion, View convertView, ViewGroup viewGroup) {
		ViewCache cache = new ViewCache();
		convertView = inflater.inflate(resource, null);
		cache.photo = (ImageView) convertView.findViewById(R.id.reviseHead);
		cache.nameView = (TextView) convertView.findViewById(R.id.username_textView);
		cache.deletimg = (ImageView) convertView.findViewById(R.id.delted_imageView);

		user = users.get(posintion);
		if (null != user) {
			bitmap = null;
			cache.nameView.setText(user.getUserName());
			if (null != user.getPer_photo() && !"".equals(user.getPer_photo())) {
				cache.photo.setImageURI(Uri.fromFile(new File(UtilConstants.CURRENT_USER.getPer_photo())));
				//cache.photo.setImageBitmap(bitmap);
			}
		}
		if (!isEdit) {
			cache.deletimg.setVisibility(View.GONE);
		} else {
			cache.deletimg.setVisibility(View.VISIBLE);
		}
		cache.deletimg.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					if (null == uservice) {
						uservice = new UserService(cont);
					}
					int uid = getItemID(posintion);
					uservice.delete(uid);
					users.remove(posintion);
					notifyDataSetChanged();

					Intent startService = new Intent(BluetoothTools.ACTION_NO_USER);
					startService.putExtra("babyId",uid);
					cont.sendBroadcast(startService);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		return convertView;
	}

	private final class ViewCache {
		public ImageView photo;
		public TextView nameView;
		public ImageView deletimg;
	}

	private LayoutInflater inflater;

}
