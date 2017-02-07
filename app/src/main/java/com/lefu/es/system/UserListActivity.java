package com.lefu.es.system;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lefu.es.adapter.UserlistviewAdapter;
import com.lefu.es.constant.AppData;
import com.lefu.es.constant.BluetoothTools;
import com.lefu.es.constant.UtilConstants;
import com.lefu.es.entity.UserModel;
import com.lefu.es.service.ExitApplication;
import com.lefu.es.service.UserService;
import com.lefu.iwellness.newes.cn.system.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.R.attr.tag;

/**
 * 用户列表界面
 * @author Leon
 * 2015-11-19
 *
 */
@SuppressLint("HandlerLeak")
public class UserListActivity extends Activity {
	private static String TAG = UserListActivity.class.getSimpleName();

	private UserlistviewAdapter userAdapter;
	private ListView brithListview;

	private ImageView addphoto_imageView;

	Intent serveIntent;

	private boolean isEdit = false;
	private TextView editText;

	private TextView backText;
	private UserService uservice;
	public List<UserModel> users = new ArrayList<UserModel>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_edit_list);
		ButterKnife.bind(this);
		viewInit();

		//ExitApplication.getInstance().addActivity(this);
	}

	public void viewInit() {
		addphoto_imageView = (ImageView) findViewById(R.id.addphoto_imageView);
		//addphoto_imageView.setOnClickListener(imgOnClickListener);
		brithListview = (ListView) this.findViewById(R.id.user_listview);
		brithListview.setOnItemClickListener(onItemClickListener);
		editText = (TextView) findViewById(R.id.useredit_textView);
		editText.setOnClickListener(imgOnClickListener);
		backText = (TextView) findViewById(R.id.back_tv);
		backText.setOnClickListener(imgOnClickListener);
		dataInit();
		userAdapter = new UserlistviewAdapter(getApplicationContext(), R.layout.user_list_item, users);
		brithListview.setAdapter(userAdapter);
	}

	public void dataInit() {
		try {
			if (null == uservice) {
				uservice = new UserService(this);
			}
			users.clear();
			users = this.uservice.getAllUserByScaleType();
		} catch (Exception e) {
			users = new ArrayList<>();
			Log.e(TAG,"用户列表程序失败"+e.getMessage());
		}
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(broadcastReceiver);
		super.onDestroy();
		ButterKnife.unbind(this);
	}

	@Override
	protected void onStart() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothTools.ACTION_NO_USER);
		registerReceiver(broadcastReceiver, intentFilter);
		super.onStart();
	}

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothTools.ACTION_NO_USER.equals(action)) {
				toAddUser();
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
	}


	@OnClick(R.id.addphoto_imageView)
	public void saveClick(){
		toAddUser();
	}

	OnClickListener imgOnClickListener = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.addphoto_imageView :
					//toAddUser();
					break;
				case R.id.useredit_textView :
					if (!isEdit) {
						isEdit = true;
					} else {
						isEdit = false;
					}
					userAdapter.setEdit(isEdit);
					userAdapter.notifyDataSetChanged();
					break;
				case R.id.back_tv :
					checkUser();
					break;
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			checkUser();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_HOME) {
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**检查是否还存在用户*/
	private void checkUser(){
		if (null == userAdapter.getItem(0)) {
			toAddUser();
		} else {
			if (null == UtilConstants.CURRENT_USER||AppData.isCheckScale) {
				Toast.makeText(UserListActivity.this, getString(R.string.user_select_need), Toast.LENGTH_SHORT).show();
			} else {
				UserListActivity.this.finish();
			}
		}
	}
	
	/**跳转到添加用户界面*/
	private void toAddUser(){
		//ExitApplication.getInstance().exit(UserListActivity.this);
		UserListActivity.this.startActivity(new Intent(UserListActivity.this, UserAddActivity.class));
	}

	OnItemClickListener onItemClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			UtilConstants.CURRENT_USER = userAdapter.users.get(position);
			UtilConstants.CURRENT_SCALE=UtilConstants.CURRENT_USER.getScaleType();
			UtilConstants.SELECT_USER=UtilConstants.CURRENT_USER.getId();
			System.out.println("当前用户称类型："+UtilConstants.CURRENT_SCALE);
			if (null != UtilConstants.CURRENT_USER && null != UtilConstants.CURRENT_USER.getDanwei() && !"".equals(UtilConstants.CURRENT_USER.getDanwei())) {
				if (null != UtilConstants.su) {
					UtilConstants.su.editSharedPreferences("lefuconfig", "unit", UtilConstants.CURRENT_USER.getDanwei());
					UtilConstants.su.editSharedPreferences("lefuconfig", "user", UtilConstants.CURRENT_USER.getId());
				}
			}
				
			UtilConstants.CHOICE_KG = (UtilConstants.CURRENT_USER.getScaleType() == null || !"".equals(UtilConstants.CURRENT_USER.getScaleType())) ? UtilConstants.UNIT_KG : UtilConstants.CURRENT_USER.getScaleType();
			
			//ExitApplication.getInstance().exit(UserListActivity.this);
			UserListActivity.this.startActivity(new Intent(UserListActivity.this, LoadingActivity.class));
			UserListActivity.this.finish();
		}

	};

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			userAdapter.notifyDataSetChanged();
		}
	};
}
