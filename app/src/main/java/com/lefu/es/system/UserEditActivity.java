package com.lefu.es.system;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lefu.es.constant.ActivityVolues;
import com.lefu.es.constant.AppData;
import com.lefu.es.constant.UtilConstants;
import com.lefu.es.constant.imageUtil;
import com.lefu.es.entity.UserModel;
import com.lefu.es.service.ExitApplication;
import com.lefu.es.service.UserService;
import com.lefu.es.util.Image;
import com.lefu.es.util.ImageUtil;
import com.lefu.es.util.SharedPreferencesUtil;
import com.lefu.es.util.Tool;
import com.lefu.es.util.UID;
import com.lefu.es.util.UtilTooth;
import com.lefu.es.wheelview.ScreenInfo;
import com.lefu.es.wheelview.WheelMain;
import com.lefu.iwellness.newes.cn.system.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import static com.lefu.iwellness.newes.cn.system.R.drawable.baby;

/**
 * 编辑用户信息
 * @author lfl
 */
public class UserEditActivity extends AppCompatActivity {
	private Button maleBtn;
	private Button femaleBtn;

	private Button ordinaryBtn;
	private Button amatuerBtn;
	private Button professBtn;

	private String userType;

	private ImageView imageCancel;
	private ImageView imageSave;

	private EditText nameET;
	private EditText heightET, heightET2;
	private TextView ageET;
	private EditText monthET;
	private EditText idET;

	private RadioGroup group;
	private TextView heng_tv;
	private TextView danwei_tv;

	private RadioGroup unitgroup;
	private RadioButton kgrb, strb, lbrb;

	private boolean isKG = true;
	private UserService uservice;
	private SharedPreferencesUtil su = new SharedPreferencesUtil(this);

	private LinearLayout taget_layout = null;
	private EditText target_edittv = null;
	private EditText target_edittv2 = null;
	private TextView heng_tv2;
	private TextView target_danwei_tv;

	/** 修改头像 */
	private SimpleDraweeView ib_upphoto;
	/** 修改头像自定义Dialog中的按钮 **/
	private Button rb_dialog[] = new Button[3];
	private int RadioButtonID[] = {R.id.rb_setPhoto1, R.id.rb_setPhoto2, R.id.rb_setPhoto3};
	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
	private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
	private static final int PHOTO_REQUEST_CUT = 3;// 结果
	// 创建一个以当前时间为名称的文件

	String path = Environment.getExternalStorageDirectory() + "/brithPhoto/";

	String photoImg = "";

	/** 通过centerIndex来决定采用那种存储方式 **/
	private int centerIndex;

	// 获得编辑权限
	SharedPreferences.Editor editor;

	/* 头像名称 */
	private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
	private File tempFile;
	private Bitmap bitmap;
	
	/*点击选择年龄弹窗*/
	private long ageClickTime=System.currentTimeMillis();
	
    private RadioButton sex_male_checkbox,sex_female_checkbox;
	
	private RadioButton ordinary_btn_checkbox,amateur_btn_checkbox,profess_btn_checkbox;

	// 拍照临时图片
	private String mTempPhotoPath;
	// 剪切后图像文件
	private Uri mDestinationUri;

	protected UserModel user = null; //需要编辑的用户

	public static Intent creatIntent(Context context, UserModel user){
		Intent intent = new Intent(context,UserEditActivity.class);
		intent.putExtra("user",user);
		return intent;
	}
	
	private void showAlertDailog(String title){
        new com.lefu.es.view.AlertDialog(UserEditActivity.this).builder()
                .setTitle(getResources().getString(R.string.waring_title))
                .setMsg(title)
                .setPositiveButton(getResources().getString(R.string.ok_btn), new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
    }



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Serializable serializable = getIntent().getSerializableExtra("user");
		if(null!=serializable){
			user = (UserModel)serializable;
		}else{
			user = UtilConstants.CURRENT_USER;
		}
		if("P999".equals(user.getGroup())){
			setContentView(R.layout.activity_baby_edit);
		}else{
			setContentView(R.layout.activity_user_edit);
		}

		mDestinationUri = Uri.fromFile(new File(this.getCacheDir(), "cropImage.jpeg"));
		mTempPhotoPath = Environment.getExternalStorageDirectory() + File.separator + "photo.jpeg";
		SharedPreferences sharedPreferences = getSharedPreferences(ActivityVolues.shape_name, MODE_PRIVATE);
		editor = sharedPreferences.edit();
		Intent center = getIntent();
		centerIndex = center.getIntExtra("center", -100);

		ExitApplication.getInstance().addActivity(this);

		/*判断用户数据是否被清空*/
		UtilConstants.su = new SharedPreferencesUtil(UserEditActivity.this);
		/*新检测添加*/
		if(UtilConstants.CURRENT_USER==null){
			AppData.isCheckScale=true;
			UtilConstants.CURRENT_USER= JSONObject.parseObject((String) UtilConstants.su.readbackUp("lefuconfig", "addUser", ""),UserModel.class);
		}

		initView();
	}

	@SuppressWarnings("deprecation")
	private void initView() {
		uservice = new UserService(this);
		nameET = (EditText) findViewById(R.id.add_brith_name);
		heightET = (EditText) findViewById(R.id.add_brith_height1);
		heightET2 = (EditText) findViewById(R.id.add_brith_height2);
		ageET = (TextView) findViewById(R.id.add_brith_day);
		taget_layout = (LinearLayout) this.findViewById(R.id.taget_layout);
		target_edittv = (EditText) this.findViewById(R.id.target_edittv);
		target_edittv2 = (EditText) this.findViewById(R.id.target_edittv2);

		sex_male_checkbox = (RadioButton)this.findViewById(R.id.sex_male_checkbox);
		sex_female_checkbox = (RadioButton)this.findViewById(R.id.sex_female_checkbox);

		ordinary_btn_checkbox = (RadioButton) findViewById(R.id.ordinary_btn_checkbox);
        amateur_btn_checkbox = (RadioButton) findViewById(R.id.amateur_btn_checkbox);
        profess_btn_checkbox = (RadioButton) findViewById(R.id.profess_btn_checkbox);

		if (UtilConstants.BATHROOM_SCALE.equals(UtilConstants.CURRENT_SCALE)) {
			taget_layout.setVisibility(View.VISIBLE);
		}
		heng_tv = (TextView) this.findViewById(R.id.heng);
		heng_tv2 = (TextView) this.findViewById(R.id.kgheng);
		danwei_tv = (TextView) this.findViewById(R.id.add_height_danwei);
		target_danwei_tv = (TextView) this.findViewById(R.id.add_taeget_danwei);
		unitgroup = (RadioGroup) this.findViewById(R.id.unit_radioGourp);
		kgrb = (RadioButton) this.findViewById(R.id.add_unit_kg);
		strb = (RadioButton) this.findViewById(R.id.add_unit_ST);
		lbrb = (RadioButton) this.findViewById(R.id.add_unit_lb);
		kgrb.setOnClickListener(kgOnClickListener);
		strb.setOnClickListener(kgOnClickListener);
		lbrb.setOnClickListener(kgOnClickListener);

		maleBtn = (Button) findViewById(R.id.sex_male);
		femaleBtn = (Button) findViewById(R.id.sex_female);
		ordinaryBtn = (Button) findViewById(R.id.ordinary_btn);
		amatuerBtn = (Button) findViewById(R.id.amateur_btn);
		professBtn = (Button) findViewById(R.id.profess_btn);
		maleBtn.setOnClickListener(sexOnClickListener);
		femaleBtn.setOnClickListener(sexOnClickListener);
		ordinaryBtn.setOnClickListener(leverOnClickListener);
		amatuerBtn.setOnClickListener(leverOnClickListener);
		professBtn.setOnClickListener(leverOnClickListener);
		imageCancel = (ImageView) findViewById(R.id.userCancel);
		imageSave = (ImageView) findViewById(R.id.userSave);
		imageCancel.setOnClickListener(imgOnClickListener);
		imageSave.setOnClickListener(imgOnClickListener);
		//ageET.setOnClickListener(imgOnClickListener);

		ib_upphoto = (SimpleDraweeView) this.findViewById(R.id.reviseHead);
		ib_upphoto.setOnClickListener(photoClickListener);

		UtilConstants.CHOICE_KG = UtilConstants.UNIT_KG;
		if (null != user) {
			UtilConstants.CHOICE_KG = user.getDanwei();
			if(UtilConstants.CHOICE_KG.equals(UtilConstants.UNIT_LB) || UtilConstants.CHOICE_KG.equals(UtilConstants.UNIT_ST) || UtilConstants.CHOICE_KG.equals(UtilConstants.UNIT_FATLB)){

			}else{
				UtilConstants.CHOICE_KG = UtilConstants.UNIT_KG;
			}
			this.nameET.setText(user.getUserName());
			if (null != user.getSex() && user.getSex().equals("0")) {
				femaleBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.womend));
			} else {
				maleBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.mand));
			}
			if (null != user.getPer_photo() && !"".equals(user.getPer_photo()) && !user.getPer_photo().equals("null")) {
				photoImg = user.getPer_photo();
				ib_upphoto.setImageURI(Uri.fromFile(new File(user.getPer_photo())));
			}
			if (UtilConstants.CHOICE_KG.equals(UtilConstants.UNIT_ST)) {
				isKG = false;
				strb.setChecked(true);
				heightET2.setVisibility(View.VISIBLE);
				heng_tv.setVisibility(View.VISIBLE);
				danwei_tv.setText(this.getText(R.string.ftin_danwei));

				target_edittv2.setVisibility(View.VISIBLE);
				heng_tv2.setVisibility(View.VISIBLE);
				target_danwei_tv.setText(this.getText(R.string.lb_danwei));
			} else if (UtilConstants.CHOICE_KG.equals(UtilConstants.UNIT_LB) || UtilConstants.CHOICE_KG.equals(UtilConstants.UNIT_FATLB)) {
				isKG = false;
				lbrb.setChecked(true);
				heightET2.setVisibility(View.VISIBLE);
				heng_tv.setVisibility(View.VISIBLE);
				danwei_tv.setText(this.getText(R.string.ftin_danwei));

				target_edittv2.setVisibility(View.VISIBLE);
				heng_tv2.setVisibility(View.VISIBLE);
				target_danwei_tv.setText(this.getText(R.string.lb_danwei));
			} else {
				kgrb.setChecked(true);
				heightET2.setVisibility(View.GONE);
				heng_tv.setVisibility(View.GONE);
				danwei_tv.setText(this.getText(R.string.cm_danwei));

				target_edittv2.setVisibility(View.GONE);
				heng_tv2.setVisibility(View.GONE);
				target_danwei_tv.setText(this.getText(R.string.kg_danwei));
			}

			if (null != user) {
				this.org_hei = user.getBheigth();
				this.org_hei1 = UtilTooth.cm2FT_INArray(user.getBheigth())[0];
				this.org_hei2 = UtilTooth.cm2FT_INArray(user.getBheigth())[0];
			}
			this.targ_old = user.getTargweight();
			this.targ_new = this.targ_old;
			if (isKG) {
				heightET.setText((int) user.getBheigth() + "");

				target_edittv.setText(UtilTooth.toOnePonit(user.getTargweight())  + "");
			} else {
				heightET.setText(UtilTooth.cm2FT_INArray(user.getBheigth())[0]);
				heightET2.setText(UtilTooth.cm2FT_INArray(user.getBheigth())[1]);

				target_edittv2.setVisibility(View.GONE);
				heng_tv2.setVisibility(View.GONE);
				String wei = target_edittv.getText().toString().trim();
				if (null == wei || "".equals(wei)) {
					wei = "0";
				}
				float ftin = UtilTooth.kgToLB_target(user.getTargweight());
				target_edittv.setText(UtilTooth.onePoint(ftin) + "");
			}

			ageET.setText(user.getBirth());

			if (user.getSex().equals("1")) {
				maleBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.mand));
				femaleBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.women));
				sex = "1";
				sex_male_checkbox.setChecked(true);
				sex_female_checkbox.setChecked(false);
			} else {
				maleBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.man));
				femaleBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.womend));
				sex = "0";
				sex_male_checkbox.setChecked(false);
				sex_female_checkbox.setChecked(true);
			}

			if ("1".equals(user.getLevel())) {
				userType = "1";
				this.amatuerBtn.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.pyeyud));
				 ordinary_btn_checkbox.setChecked(false);
	                amateur_btn_checkbox.setChecked(true);
	                profess_btn_checkbox.setChecked(false);
			} else if ("2".equals(user.getLevel())) {
				userType = "2";
				this.professBtn.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.pyundongd));
				 ordinary_btn_checkbox.setChecked(false);
                 amateur_btn_checkbox.setChecked(false);
                 profess_btn_checkbox.setChecked(true);
			} else {
				userType = "0";
				this.ordinaryBtn.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.pcommd));
				 ordinary_btn_checkbox.setChecked(true);
                 amateur_btn_checkbox.setChecked(false);
                 profess_btn_checkbox.setChecked(false);
			}
		}

        ageET.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				/*增加时延，防止快速点击两次*/
				if((System.currentTimeMillis()-ageClickTime)>1000){
					showDateTimePicker();
				}
				ageClickTime=System.currentTimeMillis();
				return false;
			}
		});
        sex_male_checkbox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sex = "1";
				maleBtn.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.mand));
				femaleBtn.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.women));

				sex_male_checkbox.setChecked(true);
				sex_female_checkbox.setChecked(false);

			}
		});

		sex_female_checkbox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sex = "0";
				maleBtn.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.man));
				femaleBtn.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.womend));
				sex_male_checkbox.setChecked(false);
				sex_female_checkbox.setChecked(true);
			}
		});

        ordinary_btn_checkbox.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                userType = "0";
                ordinaryBtn.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.pcommd));
                amatuerBtn.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.pyeyu));
                professBtn.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.pyundong));


                ordinary_btn_checkbox.setChecked(true);
                amateur_btn_checkbox.setChecked(false);
                profess_btn_checkbox.setChecked(false);
                showAlertDailog(getResources().getString(R.string.putong_waring_title));
            }
        });

       amateur_btn_checkbox.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                userType = "1";
                ordinaryBtn.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.pcomm));
                amatuerBtn.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.pyeyud));
                professBtn.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.pyundong));

                ordinary_btn_checkbox.setChecked(false);
                amateur_btn_checkbox.setChecked(true);
                profess_btn_checkbox.setChecked(false);
                showAlertDailog(getResources().getString(R.string.yeyu_waring_title));
            }
        });


        profess_btn_checkbox.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                userType = "2";
                ordinaryBtn.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.pcomm));
                amatuerBtn.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.pyeyu));
                professBtn.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.pyundongd));

                ordinary_btn_checkbox.setChecked(false);
                amateur_btn_checkbox.setChecked(false);
                profess_btn_checkbox.setChecked(true);
                showAlertDailog(getResources().getString(R.string.zhuanye_waring_title));
            }
        });
	}

	OnClickListener photoClickListener = new OnClickListener() {
		public void onClick(View v) {
			int id = v.getId();
			if (id == R.id.reviseHead) {
				showSetPhotoDialog();
			} else if (id == R.id.rb_setPhoto1) {
				alertDialog.dismiss();
				camera();
			} else if (id == R.id.rb_setPhoto2) {
				alertDialog.dismiss();
				gallery();
			} else if (id == R.id.rb_setPhoto3) {
				alertDialog.dismiss();
			}
		}
	};

	protected static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
	protected static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 102;
	protected static final int REQUEST_CAMERA_PERMISSION = 103;
	/* 从相册获取 */
	public void gallery() {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ){
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
					!= PackageManager.PERMISSION_GRANTED) {
				requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
						getString(R.string.permission_read_storage_rationale),
						REQUEST_STORAGE_READ_ACCESS_PERMISSION);
			} else {
//			if(null!=alertDialog)alertDialog.dismiss();
//			Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
//			// 如果限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
//			pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//			startActivityForResult(pickIntent, PHOTO_REQUEST_GALLERY);
				EasyImage.openGallery(this, 0, true);
			}
		}else{
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setType("image/*");
			startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
		}
	}

	/* 从相机获取 */
	public void camera() {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ){
			if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
					!= PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
					!= PackageManager.PERMISSION_GRANTED)) {
				if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
						!= PackageManager.PERMISSION_GRANTED){
					requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
							getString(R.string.permission_write_storage_rationale),
							REQUEST_STORAGE_WRITE_ACCESS_PERMISSION);
				}else if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
						!= PackageManager.PERMISSION_GRANTED){
					requestPermission(Manifest.permission.CAMERA,
							getString(R.string.permission_carame),
							REQUEST_CAMERA_PERMISSION);
				}

			} else {
				if(null!=alertDialog)alertDialog.dismiss();
				EasyImage.openCamera(UserEditActivity.this, 0);
//			Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//			//下面这句指定调用相机拍照后的照片存储的路径
//			//takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mTempPhotoPath)));
//			tempFile = new File(Environment.getExternalStorageDirectory(), PHOTO_FILE_NAME);
//			if (!tempFile.getParentFile().exists()) {
//				tempFile.getParentFile().mkdirs();
//			}
//			Uri photoURI = FileProvider.getUriForFile(UserAddActivity.this,"com.lefu.iwellness.newes.system.provider", tempFile);
//			takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);//将拍的照片保存到photoURI
//			takeIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
//			takeIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//			startActivityForResult(takeIntent, PHOTO_REQUEST_TAKEPHOTO);
			}

		}else{
			Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
			// 判断存储卡是否可以用，可用进行存储
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), PHOTO_FILE_NAME)));
			startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
		}

	}

	/**
	 * 请求权限
	 *
	 * 如果权限被拒绝过，则提示用户需要权限
	 */
	protected void requestPermission(final String permission, String rationale, final int requestCode) {
		if (shouldShowRequestPermissionRationale(permission)) {
			showAlertDialog(getString(R.string.permission_title_rationale), rationale,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							requestPermissions(new String[]{permission}, requestCode);
						}
					}, getString(R.string.ok_btn), null, getString(R.string.cancle_btn));
		} else {
			requestPermissions(new String[]{permission}, requestCode);
		}
	}
	private AlertDialog mAlertDialog;
	/**
	 * 显示指定标题和信息的对话框
	 *
	 * @param title                         - 标题
	 * @param message                       - 信息
	 * @param onPositiveButtonClickListener - 肯定按钮监听
	 * @param positiveText                  - 肯定按钮信息
	 * @param onNegativeButtonClickListener - 否定按钮监听
	 * @param negativeText                  - 否定按钮信息
	 */
	protected void showAlertDialog(@Nullable String title, @Nullable String message,
								   @Nullable DialogInterface.OnClickListener onPositiveButtonClickListener,
								   @NonNull String positiveText,
								   @Nullable DialogInterface.OnClickListener onNegativeButtonClickListener,
								   @NonNull String negativeText) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton(positiveText, onPositiveButtonClickListener);
		builder.setNegativeButton(negativeText, onNegativeButtonClickListener);
		mAlertDialog = builder.show();
	}

	/**
	 * Callback received when a permissions request has been completed.
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case REQUEST_STORAGE_READ_ACCESS_PERMISSION:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					gallery();
				}
				break;
			case REQUEST_STORAGE_WRITE_ACCESS_PERMISSION:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					camera();
				}
				break;
			case REQUEST_CAMERA_PERMISSION:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					camera();
				}
				break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	private boolean hasSdcard() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ){
			EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
				@Override
				public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
					//Some error handling
				}

				@Override
				public void onImagesPicked(List<File> imageFiles, EasyImage.ImageSource source, int type) {
					onPhotosReturned(imageFiles);
				}

				@Override
				public void onCanceled(EasyImage.ImageSource source, int type) {
					//Cancel handling, you might wanna remove taken photo if it was canceled
					if (source == EasyImage.ImageSource.CAMERA) {
						File photoFile = EasyImage.lastlyTakenButCanceledPhoto(UserEditActivity.this);
						if (photoFile != null) photoFile.delete();
					}
				}
			});
		}else{
			switch (requestCode) {

				case PHOTO_REQUEST_TAKEPHOTO :
					if (hasSdcard()) {
						tempFile = new File(Environment.getExternalStorageDirectory(), PHOTO_FILE_NAME);
						crop(Uri.fromFile(tempFile));
					} else {
						Toast.makeText(UserEditActivity.this, getString(R.string.setting_noSDCard), Toast.LENGTH_LONG).show();
					}
					break;

				case PHOTO_REQUEST_GALLERY :
					if (data != null) {
						// 得到图片的全路径
						Uri uri = data.getData();
						crop(uri);
					}
					break;

				case PHOTO_REQUEST_CUT :
					try {
						bitmap = data.getParcelableExtra("data");
						bitmap = Image.toRoundCorner(bitmap, 8);
						ib_upphoto.setImageBitmap(bitmap);
						photoImg = ImageUtil.saveBitmap(UtilConstants.USER_HEADER_CACHE_PATH, UID.getImage(), bitmap);

					} catch (Exception e) {
						e.printStackTrace();
					}
					break;

			}
		}

	}

	private void onPhotosReturned(List<File> photos) {
		if(null!=photos && photos.size()>0){
			Picasso.with(UserEditActivity.this)
					.load(photos.get(0))
					.fit()
					.centerCrop()
					.into(ib_upphoto);
			photoImg = photos.get(0).getAbsolutePath();
		}
	}

	@Override
	protected void onDestroy() {
		// Clear any configuration that was done!
		EasyImage.clearConfiguration(this);
		super.onDestroy();
	}

	/**剪切图片*/
	private void crop(Uri uri) {
		// 裁剪图片意图
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// 裁剪框的比例，1：1
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// 裁剪后输出图片的尺寸大小
		intent.putExtra("outputX", 100);
		intent.putExtra("outputY", 100);
		// 图片格式
		intent.putExtra("outputFormat", "png");
		intent.putExtra("noFaceDetection", true);// 取消人脸识别
		intent.putExtra("return-data", true);// true:不返回uri，false：返回uri
		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	private AlertDialog alertDialog;

	/**显示设置头像的Dialog*/
	private void showSetPhotoDialog() {
		// 初始化自定义布局参数
		LayoutInflater layoutInflater = getLayoutInflater();
		// 为了能在下面的OnClickListener中获取布局上组件的数据，必须定义为final类型.
		View customLayout = layoutInflater.inflate(R.layout.showsetphototdialog, (ViewGroup) findViewById(R.id.customDialog));

		for (int i = 0; i < rb_dialog.length; i++) {
			rb_dialog[i] = (Button) customLayout.findViewById(RadioButtonID[i]);
			rb_dialog[i].setOnClickListener(photoClickListener);
		}

		alertDialog = new AlertDialog.Builder(this).setView(customLayout).show();

		Window window = alertDialog.getWindow();
		window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
		window.setWindowAnimations(R.style.mystyle); // 添加动画
	}

	private float org_hei = 0.0f;
	private String org_hei1 = "0";
	private String org_hei2 = "0";
	private float targ_old = 0.0f;
	private float targ_new = 0.0f;
	private float targetweight = 0;
	private float targetweightLB = 0;
	OnClickListener kgOnClickListener = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
				case (R.id.add_unit_kg) :
					heightET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
					if (heightET2.getVisibility() == View.VISIBLE) {
						heightET2.setVisibility(View.GONE);
						heng_tv.setVisibility(View.GONE);
						String h1 = heightET.getText().toString();
						if ("".equals(h1.trim()) || "0.0".equals(h1.trim()))
							h1 = "0";
						String h2 = heightET2.getText().toString();
						if ("".equals(h2.trim()))
							h2 = "0";
						if (h1.equals(org_hei1) && h2.equals(org_hei2)) {
							heightET.setText((int)org_hei + "");
						} else {
							org_hei1 = h1;
							org_hei2 = h2;
							String[] ftin = {h1, h2};
							int cm = UtilTooth.ft_in2CMArray(ftin);
							heightET.setText(cm + "");
							org_hei = cm;
						}
					}
					if (UtilConstants.CHOICE_KG.equals(UtilConstants.UNIT_LB) || UtilConstants.CHOICE_KG.equals(UtilConstants.UNIT_ST)) {
						target_edittv2.setVisibility(View.GONE);
						heng_tv2.setVisibility(View.GONE);
						String h1 = target_edittv.getText().toString();
						if (null == h1 || "".equals(h1.trim()))
							h1 = "0";
						String kg = "0";
						if ("0".equals(h1)) {
							kg = "0";
						} else {
							targetweight = UtilTooth.lbToKg_target(Float.parseFloat(h1));
							kg = UtilTooth.onePoint(targetweight);
						}
						target_edittv.setText(kg);
					}

					isKG = true;
					UtilConstants.CHOICE_KG = UtilConstants.UNIT_KG;
					danwei_tv.setText("cm");
					target_danwei_tv.setText(UtilConstants.UNIT_KG);
					break;
				case (R.id.add_unit_ST) :
					heightET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
					if (heightET2.getVisibility() == View.GONE) {
						heightET2.setVisibility(View.VISIBLE);
						heng_tv.setVisibility(View.VISIBLE);
						if (!"".equals(heightET.getText().toString())) {
							org_hei = Float.parseFloat(heightET.getText().toString().trim());
							String[] ftin = UtilTooth.cm2FT_INArray(Float.parseFloat(heightET.getText().toString()));
							if (null != ftin) {
								org_hei1 = ftin[0];
								org_hei2 = ftin[1];
								heightET.setText(ftin[0]);
								heightET2.setText(ftin[1]);
							}
						}

					}
					if (UtilConstants.CHOICE_KG.equals(UtilConstants.UNIT_LB)) {
						target_edittv2.setVisibility(View.GONE);
						heng_tv2.setVisibility(View.GONE);
					} else if (UtilConstants.CHOICE_KG.equals(UtilConstants.UNIT_KG)) {
						target_edittv2.setVisibility(View.GONE);
						heng_tv2.setVisibility(View.GONE);
						String h1 = target_edittv.getText().toString();
						if (null == h1 || "".equals(h1.trim()))
							h1 = "0";
						String kg = "0";
						if ("0".equals(h1)) {
							kg = "0";
						} else {
							targetweightLB = UtilTooth.kgToLB_target(Float.parseFloat(h1));
							kg = UtilTooth.onePoint(targetweightLB);
						}
						target_edittv.setText(kg);
						target_edittv2.setText("0");
					}
					isKG = false;
					UtilConstants.CHOICE_KG = UtilConstants.UNIT_ST;
					danwei_tv.setText("ft:in");
					target_danwei_tv.setText(UtilConstants.UNIT_LB);
					break;
				case (R.id.add_unit_lb) :
					heightET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
					if (heightET2.getVisibility() == View.GONE) {
						heightET2.setVisibility(View.VISIBLE);
						heng_tv.setVisibility(View.VISIBLE);
						if (!"".equals(heightET.getText().toString())) {
							org_hei = Float.parseFloat(heightET.getText().toString().trim());
							String[] ftin = UtilTooth.cm2FT_INArray(Float.parseFloat(heightET.getText().toString()));
							if (null != ftin) {
								org_hei1 = ftin[0];
								org_hei2 = ftin[1];
								heightET.setText(ftin[0]);
								heightET2.setText(ftin[1]);
							}
						}
					}
					if (UtilConstants.CHOICE_KG.equals(UtilConstants.UNIT_ST)) {
						target_edittv2.setVisibility(View.GONE);
						heng_tv2.setVisibility(View.GONE);
					} else if (UtilConstants.CHOICE_KG.equals(UtilConstants.UNIT_KG)) {
						target_edittv2.setVisibility(View.GONE);
						heng_tv2.setVisibility(View.GONE);
						String h1 = target_edittv.getText().toString();
						if (null == h1 || "".equals(h1.trim()))
							h1 = "0";
						String kg = "0";
						if ("0".equals(h1)) {
							kg = "0";
						} else {
							targetweightLB = UtilTooth.kgToLB_target(Float.parseFloat(h1));
							kg = UtilTooth.onePoint(targetweightLB);
						}
						target_edittv.setText(kg);
						target_edittv2.setText("0");
					}
					isKG = false;
					UtilConstants.CHOICE_KG = UtilConstants.UNIT_LB;
					danwei_tv.setText("ft:in");
					target_danwei_tv.setText(UtilConstants.UNIT_LB);
					break;
			}
		}
	};

	OnClickListener sexOnClickListener = new OnClickListener() {
		@SuppressWarnings("deprecation")
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.sex_male :
					sex = "1";
					maleBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.mand));
					femaleBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.women));
					sex_male_checkbox.setChecked(true);
					sex_female_checkbox.setChecked(false);
					break;
				case R.id.sex_female :
					sex = "0";
					maleBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.man));
					femaleBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.womend));
					sex_male_checkbox.setChecked(false);
					sex_female_checkbox.setChecked(true);
					break;
			}
		}
	};

	OnClickListener leverOnClickListener = new OnClickListener() {
		@SuppressWarnings("deprecation")
		public void onClick(View v) {
			switch (v.getId()) {

				case (R.id.ordinary_btn) :
					userType = "0";
					ordinaryBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.pcommd));
					amatuerBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.pyeyu));
					professBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.pyundong));
					 ordinary_btn_checkbox.setChecked(true);
	                    amateur_btn_checkbox.setChecked(false);
	                    profess_btn_checkbox.setChecked(false);
	                    showAlertDailog(getResources().getString(R.string.putong_waring_title));
					break;

				case (R.id.amateur_btn) :
					userType = "1";
					ordinaryBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.pcomm));
					amatuerBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.pyeyud));
					professBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.pyundong));
					  ordinary_btn_checkbox.setChecked(false);
	                    amateur_btn_checkbox.setChecked(true);
	                    profess_btn_checkbox.setChecked(false);
	                    showAlertDailog(getResources().getString(R.string.yeyu_waring_title));
					break;
				case (R.id.profess_btn) :
					userType = "2";
					ordinaryBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.pcomm));
					amatuerBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.pyeyu));
					professBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.pyundongd));
					 ordinary_btn_checkbox.setChecked(false);
	                    amateur_btn_checkbox.setChecked(false);
	                    profess_btn_checkbox.setChecked(true);
	                    showAlertDailog(getResources().getString(R.string.zhuanye_waring_title));
					break;
			}
		}
	};

	String sex = "1";
	OnClickListener imgOnClickListener = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
				case (R.id.userCancel) :
					exit();
					break;
				case (R.id.userSave) :
					saveUser();
					break;
				case (R.id.add_brith_day) :
					/*增加时延，防止快速点击两次*/
					if((System.currentTimeMillis()-ageClickTime)>1000){
						showDateTimePicker();
					}
					ageClickTime=System.currentTimeMillis();
					break;

			}
		}
	};

	/**退出*/
	private void exit(){
		UserEditActivity.this.finish();
		if (AppData.isCheckScale) {
			/* 是否存在用户 */
			try {
				if (uservice.getCount() > 0) {
					UserEditActivity.this.startActivity(new Intent(UserEditActivity.this, UserListActivity.class));
				}else{
					/* 结束程序 */
					ExitApplication.getInstance().exit(UserEditActivity.this);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**判断*/
	private void saveUser() {
		String sName = nameET.getText().toString();
		if (null == sName || "".equals(sName.trim())) {
			Toast.makeText(this, getString(R.string.name_error), Toast.LENGTH_SHORT).show();
			return;
		}
		String ageday = ageET.getText().toString();
		if (null == ageday || "".equals(ageday.trim())) {
			Toast.makeText(this, getString(R.string.age_error), Toast.LENGTH_SHORT).show();
			return;
		}

		String year = ageday.substring(ageday.lastIndexOf("-") + 1);
		String mm = ageday.substring(0, ageday.lastIndexOf("-"));
		ageday = year + "-" + mm;
		int age = Tool.getAgeByBirthday(Tool.StringToDate(ageday, "yyyy-MM-dd"));
		if (age < 1 || age > 99) {
			Toast.makeText(this, getString(R.string.age_error_2), Toast.LENGTH_SHORT).show();
			return;
		}


		if (isKG) {
			String height1 = heightET.getText().toString();
			if (null == height1 || "".equals(height1.trim())) {
				Toast.makeText(this, getString(R.string.height_error), Toast.LENGTH_LONG)
						.show();
				return;
			}

			if (age >= 10) {
				if(Float.parseFloat(height1) < 100 || Float.parseFloat(height1) >220){
					Toast.makeText(this, getString(R.string.height_error_1), Toast.LENGTH_LONG)
							.show();
					return;
				}

			}else{
				if(Float.parseFloat(height1) < 30 || Float.parseFloat(height1) >220){
					Toast.makeText(this, getString(R.string.height_error_3), Toast.LENGTH_LONG)
							.show();
					return;
				}
			}
		} else {
			// heightET.setText(UtilTooth.myround(UtilTooth.cm2foot(user2.getBheigth()))+"");
			String h1 = heightET.getText().toString();
			if ("".equals(h1.trim()))
				h1 = "0";
			String h2 = heightET2.getText().toString();
			if ("".equals(h2.trim()))
				h2 = "0";
			int h11 = (int) Float.parseFloat(h1);
			int h22 = (int) Float.parseFloat(h2);
			if (age >= 10){
				if ((h11 < 3) || (h11 == 3 && h22 < 3) || (h11 > 7) || (h11 == 7 && h22 > 3)) {
					Toast.makeText(this, getString(R.string.height_error_2), Toast.LENGTH_LONG)
							.show();
					return;
				}
			}else{
				if ((h11 < 1) || (h11 > 7) || (h11 == 7 && h22 > 3)) {
					Toast.makeText(this, getString(R.string.height_error_4), Toast.LENGTH_LONG)
							.show();
					return;
				}
			}
		}
		if (null == userType || "".equals(userType.trim())) {
			Toast.makeText(this, getString(R.string.user_info_level_need), Toast.LENGTH_SHORT).show();
			return;
		}

		Message msg = saveHandler.obtainMessage(SAVE_ATION);
		saveHandler.sendMessage(msg);
	}

	public static final int SAVE_ATION = 1;
	private final Handler saveHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case SAVE_ATION :
					if (null != user) {
						/* 判断是否是识别界面进入 */
						UserModel user=creatUserModel();
						if (!AppData.isCheckScale) {
							/* 保存用户信息 */
							try {
								if(UtilConstants.CURRENT_SCALE.equals(UtilConstants.KITCHEN_SCALE)){
									user.setDanwei(UtilConstants.UNIT_KG);
								}
								uservice.update(user);
								user = uservice.find(user.getId());
								UtilConstants.SELECT_USER = user.getId();
							} catch (Exception e) {
								e.printStackTrace();
							}
							Intent intent=new Intent();
							Bundle bundle=new Bundle();
							bundle.putSerializable("user",user);
							intent.putExtras(bundle);
							setResult(RESULT_OK, intent);
							UserEditActivity.this.finish();
						} else {
							/*跳转到指定的扫描界面*/
							int currentapiVersion = Build.VERSION.SDK_INT;
							Intent intent1 = new Intent();
							intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							if (currentapiVersion < 18) {
								intent1.setClass(UserEditActivity.this, AutoBTActivity.class);
							} else {
								intent1.setClass(UserEditActivity.this, AutoBLEActivity.class);
							}
							UserEditActivity.this.startActivity(intent1);
							finish();
						}
					}
					break;
			}
		}
	};

	/** 创建用户 */
	public UserModel creatUserModel() {
		if (null != user) {
			user.setUserName(nameET.getText().toString());
			user.setLevel(userType);
			user.setScaleType(UtilConstants.CURRENT_SCALE);
			user.setDanwei(UtilConstants.CHOICE_KG);
			if(UtilConstants.CURRENT_SCALE.equals(UtilConstants.BABY_SCALE)){
				if(UtilConstants.CHOICE_KG.equals(UtilConstants.UNIT_LB) || UtilConstants.CHOICE_KG.equals(UtilConstants.UNIT_ST) || UtilConstants.CHOICE_KG.equals(UtilConstants.UNIT_FATLB)){
					user.setDanwei(UtilConstants.UNIT_FATLB);
				}else{
					user.setDanwei(UtilConstants.UNIT_KG);
				}
			} 
			user.setPer_photo(photoImg);
			user.setSex(sex);
			String hei = this.heightET.getText().toString().trim();
			String hei2 = this.heightET2.getText().toString().trim();
			if (null == hei || "".equals(hei)) {
				hei = "0";
			}
			if (null == hei2 || "".equals(hei2)) {
				hei2 = "0";
			}
			String tage = this.target_edittv.getText().toString().trim();
			String tage2 = this.target_edittv2.getText().toString().trim();
			if (null == tage || "".equals(tage)) {
				tage = "0";
			}
			if (null == tage2 || "".equals(tage2)) {
				tage2 = "0";
			}
			String age = this.ageET.getText().toString().trim();
			if (null == age || "".equals(age)) {
				age = "0";
			}
			if (null != monthET) {
				String mont = monthET.getText().toString();
				if ("".equals(mont.trim())) {
					user.setAgeMonth(0);
				} else {
					user.setAgeMonth(Integer.parseInt(mont));
				}
			}
			if (null != target_edittv) {
				String wei = target_edittv.getText().toString().trim();
				if (null == wei || "".equals(wei)) {
					wei = "0";
				}
				float tg = Float.parseFloat(wei);
				if (tg < 1)
					tg = 0f;
				if (UtilConstants.CHOICE_KG.equals(UtilConstants.UNIT_KG)) {
					user.setTargweight(tg);
				} else {
					user.setTargweight(UtilTooth.lbToKg_target(tg));
				}
			}
			if (isKG) {
				user.setBheigth(Float.parseFloat(hei));
			} else {
				user.setBheigth(UtilTooth.ft_in2CMArray(new String[]{hei, hei2}));
			}
			if (isKG) {
				user.setBheigth(Float.parseFloat(hei));
			} else {
				user.setBheigth(UtilTooth.ft_in2CMArray(new String[]{hei, hei2}));
			}
			su.editSharedPreferences("lefuconfig", "unit", UtilConstants.CHOICE_KG);
			user.setBirth(age);
			if (null != age && !"".equals(age)) {
				if (age.lastIndexOf("-") > 0) {
					String year = age.substring(age.lastIndexOf("-") + 1);
					String mm = age.substring(0, age.lastIndexOf("-"));
					age = year + "-" + mm;
				}
			}
			user.setAgeYear(Tool.getAgeByBirthday(Tool.StringToDate(age, "yyyy-MM-dd")));
		}

		return user;
	}

	private WheelMain wheelMain;
	/*** 自定义Dialog */
	private AlertDialog dialog;
	/** 是否成功保存 **/
	private boolean isOK;

	/** 时间滚动器*/
	public void showDateTimePicker() {
		LayoutInflater inflater = LayoutInflater.from(UserEditActivity.this);
		View timepickerview = inflater.inflate(R.layout.datewheelpick, null);
		timepickerview.setMinimumWidth(getWindowManager().getDefaultDisplay().getWidth());
		ScreenInfo screenInfo = new ScreenInfo(UserEditActivity.this);
		wheelMain = new WheelMain(timepickerview);
		wheelMain.screenheight = screenInfo.getHeight();
		Calendar calendar = Calendar.getInstance();
		// calendar.setTime(dateFormat.parse(time));设置指定时间
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		wheelMain.setTime(year, month, day);
		dialog = new AlertDialog.Builder(this).setView(timepickerview).show();

		Window window = dialog.getWindow();
		window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
		window.setWindowAnimations(R.style.mystyle); // 添加动画

		Button btn = (Button) timepickerview.findViewById(R.id.btn_datetime_sure);
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				isOK = true;
				ageET.setText(wheelMain.getTime());
				dialog.dismiss();
			}
		});

	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			exit();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_HOME) {
		}
		return super.onKeyDown(keyCode, event);
	}

}
