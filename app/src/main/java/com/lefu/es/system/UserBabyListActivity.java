package com.lefu.es.system;

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
import com.lefu.es.db.RecordDao;
import com.lefu.es.entity.UserModel;
import com.lefu.es.service.ExitApplication;
import com.lefu.es.service.UserService;
import com.lefu.es.util.ToastUtils;
import com.lefu.iwellness.newes.cn.system.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 抱婴用户列表界面
 * @author Leon
 * 2015-11-19
 *
 */
@SuppressLint("HandlerLeak")
public class UserBabyListActivity extends Activity {
	private static String TAG = UserBabyListActivity.class.getSimpleName();

	private UserlistviewAdapter userAdapter;
	private ListView brithListview;

	private ImageView addphoto_imageView;

	Intent serveIntent;

	private boolean isEdit = false;
	private TextView editText;

	private TextView backText;
	private UserService uservice;
	public List<UserModel> users = new ArrayList<UserModel>();

	int babyId = 0;//当前抱婴的id

	public static Intent creatIntent(Context context,int selectId){
		Intent intent = new Intent(context,UserBabyListActivity.class);
		intent.putExtra("babyid",selectId);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_edit_list);
		babyId = getIntent().getIntExtra("babyid",0);
		viewInit();
		ExitApplication.getInstance().addActivity(this);
	}

	public void viewInit() {
		addphoto_imageView = (ImageView) findViewById(R.id.addphoto_imageView);
		addphoto_imageView.setOnClickListener(imgOnClickListener);
		brithListview = (ListView) this.findViewById(R.id.user_listview);
		brithListview.setOnItemClickListener(onItemClickListener);
		editText = (TextView) findViewById(R.id.useredit_textView);
		editText.setOnClickListener(imgOnClickListener);
		backText = (TextView) findViewById(R.id.back_tv);
		backText.setOnClickListener(imgOnClickListener);
		dataInit();
		userAdapter = new UserlistviewAdapter(getApplicationContext(), R.layout.harmbaby_list_item, users);
		brithListview.setAdapter(userAdapter);
	}

	public void dataInit() {
		try {
			if (null == uservice) {
				uservice = new UserService(this);
			}
			users.clear();
			users = this.uservice.getAllBabys();
		} catch (Exception e) {
			users = new ArrayList<>();
			Log.e(TAG,"用户列表程序失败"+e.getMessage());
		}
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(broadcastReceiver);
		super.onDestroy();
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
				int uid = intent.getIntExtra("babyId",0);
				if(0!=uid && uid==babyId){
					babyId = 0;
				}
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
	}



	OnClickListener imgOnClickListener = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.addphoto_imageView :
					toAddUser();
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
					UserBabyListActivity.this.finish();
					break;
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if(babyId==0){
				ToastUtils.ToastCenter(UserBabyListActivity.this,"请选择个婴儿");
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_HOME) {
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**跳转到添加用户界面*/
	private void toAddUser(){
		startActivityForResult(BabyAddActivity.creatIntent(UserBabyListActivity.this),101);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 101) {
			Bundle loginBundle = data.getExtras();
			if(null!=loginBundle){
				Serializable serializable = loginBundle.getSerializable("user");
				if(null!=serializable){
					try {
						if (null == uservice) {
							uservice = new UserService(this);
						}
						users.clear();
						users = this.uservice.getAllBabys();

					} catch (Exception e) {
						users = new ArrayList<>();
						Log.e(TAG,"用户列表程序失败"+e.getMessage());
					}
					userAdapter.clearALL(users);
				}
			}
		}
	}

	OnItemClickListener onItemClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			UserModel userModel = userAdapter.users.get(position);
			Intent intent = new Intent();
			Bundle mBundle = new Bundle();
			mBundle.putSerializable("user", userModel);
			intent.putExtras(mBundle);
			setResult(102,intent);
			finish();
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
