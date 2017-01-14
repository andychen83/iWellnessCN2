package com.lefu.es.system;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lefu.es.cache.CacheHelper;
import com.lefu.es.constant.UtilConstants;
import com.lefu.es.entity.Records;
import com.lefu.es.entity.UserModel;
import com.lefu.es.util.FileUtils;
import com.lefu.es.util.SharedPreferencesUtil;
import com.lefu.es.util.UtilTooth;
import com.lefu.iwellness.newes.cn.system.R;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.lefu.iwellness.newes.cn.system.R.id.home_img_btn;

/*
* 脂肪秤设置界面
* */
public class BodyFatScaleSetActivity extends AppCompatActivity {
    protected UserModel babyUser = null; //选择的婴儿

    @Bind(R.id.user_header)
    SimpleDraweeView userHeadImg;

    @Bind(R.id.user_name)
    TextView userNameTx;

    public static Intent creatIntent(Context context,UserModel baby){
        Intent intent = new Intent(context,BodyFatScaleSetActivity.class);
        intent.putExtra("baby",baby);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_fat_scale_set);
        ButterKnife.bind(this);
        fileutil = new FileUtils(this);
        Serializable serializable = getIntent().getSerializableExtra("baby");
        if(null==serializable){
            Toast.makeText(BodyFatScaleSetActivity.this, getString(R.string.choice_a_baby), Toast.LENGTH_LONG).show();
            finish();
        }else{
            babyUser = (UserModel)serializable;
            initView(babyUser);
        }
    }

    private void initView(UserModel babyUser) {
        if(null!=babyUser){
            if(!TextUtils.isEmpty(babyUser.getPer_photo())){
                userHeadImg.setImageURI(Uri.fromFile(new File(babyUser.getPer_photo())));
            }
            userNameTx.setText(babyUser.getUserName());


        }
    }

    @OnClick(R.id.info_layout)
    public void infoLyClick(){
        startActivityForResult(UserEditActivity.creatIntent(BodyFatScaleSetActivity.this,babyUser),0);
    }

    @OnClick(R.id.save_layout)
    public void saveLyClick(){
        showSaveasDialog();
    }

    @OnClick(R.id.about_layout)
    public void aboutLyClick(){
        Intent intent = new Intent();
        intent.setClass(BodyFatScaleSetActivity.this, HelpActivity.class);
        intent.putExtra("IsHelp", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @OnClick(R.id.linke_contract)
    public void linkLyClick(){
        String subject = getResources().getString(R.string.email_subject)+"("+getResources().getString(R.string.app_name)+")";
        //String myCc = "cc";
        String mybody = " ";

        Uri uri = Uri.parse("mailto:info@lefu.cc");
        //String[] email = {"3802**92@qq.com"};
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        //intent.putExtra(Intent.EXTRA_CC, email); // 抄送人
        intent.putExtra(Intent.EXTRA_SUBJECT, subject); // 主题
        intent.putExtra(Intent.EXTRA_TEXT, mybody); // 正文
        startActivity(Intent.createChooser(intent, subject));
    }

    @OnClick(home_img_btn)
    public void homeBtnClick(){
        this.finish();
    }

    private AlertDialog savesAlertDialog;
    private Button txt_btn, close_btn;
    private FileUtils fileutil = null;
    /** 显示保存Dialog */
    private void showSaveasDialog() {
        // 初始化自定义布局参数
        LayoutInflater layoutInflater = getLayoutInflater();
        // 为了能在下面的OnClickListener中获取布局上组件的数据，必须定义为final类型.
        View customLayout = layoutInflater.inflate(R.layout.savesa, (ViewGroup) findViewById(R.id.customDialog));

        txt_btn = (Button) customLayout.findViewById(R.id.txt_button);
        close_btn = (Button) customLayout.findViewById(R.id.close_button);

        txt_btn.setOnClickListener(imgOnClickListener);
        close_btn.setOnClickListener(imgOnClickListener);

        savesAlertDialog = new AlertDialog.Builder(this).setView(customLayout).show();

        Window window = savesAlertDialog.getWindow();
        window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.mystyle); // 添加动画
    }

    View.OnClickListener imgOnClickListener = new View.OnClickListener() {
        @SuppressWarnings("deprecation")
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.txt_button :
                    if (!fileutil.hasSD()) {
                        Toast.makeText(BodyFatScaleSetActivity.this, getString(R.string.setting_noSDCard), Toast.LENGTH_SHORT).show();
                        savesAlertDialog.dismiss();
                        return;
                    }
                    if (null != CacheHelper.recordListDesc && 0 != CacheHelper.recordListDesc.size()) {
                        try {
                            fileutil.createSDFile("myrecords.txt");
                            String str = "\n";
                            for (Records curRecord : CacheHelper.recordListDesc) {
                                str += curRecord.getRecordTime()+","+curRecord.getRphoto() + " Weight:" + curRecord.getRweight() + "g,Carbohydrate:" + curRecord.getRbodywater() + "kcal,Energ:"
                                        + curRecord.getRbodyfat() + "g,Protein:"+ curRecord.getRbone() + "g,Fiber:" + curRecord.getRvisceralfat()  + "g,Lipid:" + curRecord.getRmuscle() + "g,Cholesterol:"
                                        + curRecord.getRbmi() + "g，Calcium:" + curRecord.getBodyAge() + "mg,Vitamin C"+ curRecord.getRbmr() + "mg \n";
                            }
                            fileutil.writeSDFile(str, "myrecords.txt");
                            Toast.makeText(BodyFatScaleSetActivity.this, getString(R.string.setting_export_txt_succ), Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            Toast.makeText(BodyFatScaleSetActivity.this, getString(R.string.setting_export_createFile_fail), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(BodyFatScaleSetActivity.this, getString(R.string.setting_export_noRecord), Toast.LENGTH_SHORT).show();
                    }
                    savesAlertDialog.dismiss();
                    break;
                case R.id.close_button :
                    savesAlertDialog.dismiss();
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

}
