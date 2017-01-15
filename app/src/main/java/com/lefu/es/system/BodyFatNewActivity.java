package com.lefu.es.system;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lefu.es.blenew.helper.BleHelper1;
import com.lefu.es.constant.UtilConstants;
import com.lefu.es.db.RecordDao;
import com.lefu.es.entity.Records;
import com.lefu.es.entity.UserModel;
import com.lefu.es.service.ExitApplication;
import com.lefu.es.service.UserService;
import com.lefu.es.util.MoveView;
import com.lefu.es.util.MyUtil;
import com.lefu.es.util.SharedPreferencesUtil;
import com.lefu.es.util.StringUtils;
import com.lefu.es.util.ToastUtils;
import com.lefu.es.util.UtilTooth;
import com.lefu.es.view.MyTextView;
import com.lefu.es.view.MyTextView5;
import com.lefu.iwellness.newes.cn.system.R;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.R.attr.data;
import static com.lefu.iwellness.newes.cn.system.R.id.textView1;
import static com.lefu.iwellness.newes.cn.system.R.id.textView2;

public class BodyFatNewActivity extends BaseBleActivity {

    @Bind(R.id.setting_menu)
    RelativeLayout set;

    @Bind(R.id.bluetooth_status)
    TextView bluetoothStatusTx;

    @Bind(R.id.weith_value_tx)
    MyTextView5 weithValueTx;

    @Bind(R.id.weith_status)
    TextView weithStatus;

    @Bind(R.id.user_name)
    TextView userNameTx;

    @Bind(R.id.bmi_value_tx)
    TextView bmTx;

    @Bind(R.id.bmi_status)
    TextView bmiStatus;

    @Bind(R.id.visal_value_tx)
    TextView visalTx;

    @Bind(R.id.visal_status)
    TextView visalStatus;

    @Bind(R.id.user_header)
    SimpleDraweeView userHeadImg;

    @Bind(R.id.unti_tv)
    TextView unit_tv;

    private UserService uservice;

    //private Dialog dialog;
    View view;

    /*体重
   * ---------*/
    @Bind(R.id.face_img_weight)
    ImageView face_img_weight;
    @Bind(R.id.face_img_weight_ll)
     LinearLayout face_img_weight_ll;
    @Bind(R.id.weight_critical_point1)
     TextView weight_critical_point1;
    @Bind(R.id.weight_critical_point2)
     TextView weight_critical_point2;
    @Bind(R.id.biaoz)
     AppCompatTextView biaoz;
    @Bind(R.id.weight_index_tx)
    AppCompatTextView weightIndex;

    @Bind(R.id.status_bar2)
    RelativeLayout status_bar2;
    @Bind(R.id.weight_jiantou)
    AppCompatImageView weight_jiantou;

    /*水分率
    *-----------------
     */
    @Bind(R.id.face_img_moisture)
     ImageView face_img_moisture;
    @Bind(R.id.face_img_moisture_ll)
     LinearLayout face_img_moisture_ll;
    @Bind(R.id.moistrue_critical_point1)
     TextView moistrue_critical_point1;
    @Bind(R.id.moistrue_critical_point2)
     TextView moistrue_critical_point2;
    @Bind(R.id.biaoz_moistrue)
     AppCompatTextView biaoz_moistrue;
    @Bind(R.id.water_index_tx)
    AppCompatTextView waterIndex;
    @Bind(R.id.status_bar_moisture)
    RelativeLayout status_bar_moisture;

    @Bind(R.id.water_jiantou)
    AppCompatImageView water_jiantou;
    /*脂肪率
    *-----------------
     */
    @Bind(R.id.face_img_bft)
     ImageView face_img_bft;
    @Bind(R.id.face_img_bft_ll)
     LinearLayout face_img_bft_ll;
    @Bind(R.id.bft_critical_point1)
     TextView bft_critical_point1;
    @Bind(R.id.bft_critical_point2)
     TextView bft_critical_point2;
    @Bind(R.id.bft_critical_point3)
     TextView bft_critical_point3;
    @Bind(R.id.bft_critical_point4)
     TextView bft_critical_point4;
    @Bind(R.id.bft_biaoz)
     AppCompatTextView bft_biaoz;
    @Bind(R.id.fat_index_tx)
    AppCompatTextView fatIndex;
    @Bind(R.id.status_bar_bft)
    RelativeLayout status_bar_bft;

    @Bind(R.id.fat_jiantou)
    AppCompatImageView fat_jiantou;
    /*骨量
    *-----------------
     */
    @Bind(R.id.face_img_bone)
     ImageView face_img_bone;
    @Bind(R.id.face_img_bone_ll)
     LinearLayout face_img_bone_ll;
    @Bind(R.id.bone_critical_point1)
     TextView bone_critical_point1;
    @Bind(R.id.bone_critical_point2)
     TextView bone_critical_point2;
    @Bind(R.id.bone_biaoz)
     AppCompatTextView bone_biaoz;
    @Bind(R.id.bone_index_tx)
    AppCompatTextView boneIndex;
    @Bind(R.id.status_bar_bone)
    RelativeLayout status_bar_bone;
    @Bind(R.id.bone_jiantou)
    AppCompatImageView bone_jiantou;
    /*BMI
    *-----------------
     */
    @Bind(R.id.face_img_bmi)
     ImageView face_img_bmi;
    @Bind(R.id.face_img_bmi_ll)
     LinearLayout face_img_bmi_ll;
    @Bind(R.id.bmi_critical_point1)
     TextView bmi_critical_point1;
    @Bind(R.id.bmi_critical_point2)
     TextView bmi_critical_point2;
    @Bind(R.id.bmi_critical_point3)
     TextView bmi_critical_point3;
    @Bind(R.id.bmi_biaoz)
     AppCompatTextView bmi_biaoz;
    @Bind(R.id.bmi_index_tx)
    AppCompatTextView bmiIndex;
    @Bind(R.id.status_bar_bmi)
    RelativeLayout status_bar_bmi;
    @Bind(R.id.bmi_jiantou)
    AppCompatImageView bmi_jiantou;
    /*内脏脂肪指数
    *-----------------
     */
    @Bind(R.id.face_img_visceral)
     ImageView face_img_visceral;
    @Bind(R.id.face_img_visceral_ll)
     LinearLayout face_img_visceral_ll;
    @Bind(R.id.visceral_critical_point1)
     TextView visceral_critical_point1;
    @Bind(R.id.visceral_critical_point2)
     TextView visceral_critical_point2;
    @Bind(R.id.visceral_biaoz)
     AppCompatTextView visceral_biaoz;
    @Bind(R.id.visalfat_index_tx)
    AppCompatTextView visalfatIndex;
    @Bind(R.id.status_bar_visalfat)
    RelativeLayout status_bar_visalfat;
    @Bind(R.id.visalfat_jiantou)
    AppCompatImageView visalfat_jiantou;
    /*BMR基础代谢率
    *-----------------
     */
    @Bind(R.id.face_img_bmr)
     ImageView face_img_bmr;
    @Bind(R.id.face_img_bmr_ll)
     LinearLayout face_img_bmr_ll;
    @Bind(R.id.bmr_critical_point1)
     TextView bmr_critical_point1;
    @Bind(R.id.bmr_biaoz)
     AppCompatTextView bmr_biaoz;
    @Bind(R.id.bmr_index_tx)
    AppCompatTextView bmrIndex;
    @Bind(R.id.status_bar_bmr)
    RelativeLayout status_bar_bmr;
    @Bind(R.id.bmr_jiantou)
    AppCompatImageView bmr_jiantou;
    /*肌肉率
    *-----------------
     */
    @Bind(R.id.face_img_muscle)
     ImageView face_img_muscle;
    @Bind(R.id.face_img_muscle_ll)
     LinearLayout face_img_muscle_ll;
    @Bind(R.id.muscle_critical_point1)
     TextView muscle_critical_point1;
    @Bind(R.id.muscle_critical_point2)
     TextView muscle_critical_point2;
    @Bind(R.id.muscle_biaoz)
     AppCompatTextView muscle_biaoz;
    @Bind(R.id.muscal_index_tx)
    AppCompatTextView muscalIndex;
    @Bind(R.id.status_bar_muscial)
    RelativeLayout status_bar_muscial;
    @Bind(R.id.muscal_jiantou)
    AppCompatImageView muscal_jiantou;
    /*身体年龄
    *-----------------
     */
    @Bind(R.id.age_biaoz)
    AppCompatTextView age_biaoz;
    @Bind(R.id.age_index_tx)
    AppCompatTextView ageIndex;


    public static int SELCET_USER=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_fat_new);
        ButterKnife.bind(this);
        uservice = new UserService(this);

        initView();
    }


    /**
     * 初始化界面参数
     */
    private void initView() {
        if(null!=UtilConstants.CURRENT_USER){
            try {
                userNameTx.setText(UtilConstants.CURRENT_USER.getUserName());
                if(!TextUtils.isEmpty(UtilConstants.CURRENT_USER.getPer_photo())){
                    userHeadImg.setImageURI(Uri.fromFile(new File(UtilConstants.CURRENT_USER.getPer_photo())));
                }
                Records lastRecords = recordService.findLastRecords(UtilConstants.CURRENT_USER.getId(),"cf");
                if(null!=lastRecords){
                    localData(lastRecords,UtilConstants.CURRENT_USER);
                    initBodyBar(UtilConstants.CURRENT_USER,lastRecords);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == SELCET_USER) {
            Bundle loginBundle = data.getExtras();
            if(null!=loginBundle){
                Serializable serializable = loginBundle.getSerializable("user");
                if(null!=serializable){
                    UserModel user = (UserModel) serializable;
                    //保存用户
                    UtilConstants.CURRENT_USER = user;
                    if(user.getId()!=UtilConstants.CURRENT_USER.getId()){
                        RecordDao.handleData2(recordService, receiveRecod);
                        initView();
                    }else{
                        RecordDao.handleData2(recordService, receiveRecod);
                    }
                }
            }
        }
    }

    /**
     * 初始化 圆圈参数
     * @param
     */
    private  void localData(Records record,UserModel user){
        if(null==user || null==record)return;
        if (user.getDanwei().equals(UtilConstants.UNIT_ST)) {
            if (UtilConstants.CURRENT_SCALE.equals(UtilConstants.BODY_SCALE)) {
                String[] tempS = UtilTooth.kgToStLbForScaleFat2(record.getRweight());

                weithValueTx.setTexts(tempS[0], tempS[1]);
                if (null != unit_tv) {
                    unit_tv.setText(this.getText(R.string.stlb_danwei));
                }
            } else {
                weithValueTx.setTexts(UtilTooth.kgToStLb(record.getRweight()), null);
                if (null != unit_tv) {
                    unit_tv.setText(this.getText(R.string.stlb_danwei));
                }
            }
        } else if (user.getDanwei().equals(UtilConstants.UNIT_LB) || user.getDanwei().equals(UtilConstants.UNIT_FATLB)) {
            weithValueTx.setTexts(UtilTooth.kgToLB_ForFatScale(record.getRweight()), null);
            if (null != unit_tv) {
                unit_tv.setText(this.getText(R.string.lb_danwei));
            }
        } else {
            weithValueTx.setTexts(record.getRweight() + "", null);
            if (null != unit_tv) {
                unit_tv.setText(this.getText(R.string.kg_danwei));
            }
        }
        String sex = user.getSex();
        if(TextUtils.isEmpty(sex) || "null".equalsIgnoreCase(sex))sex = "1";
        int gender = Integer.parseInt(sex);
        weithStatus.setText(MoveView.weightString(gender,user.getBheigth(),record.getRweight()));
        bmTx.setText(String.valueOf(record.getRbmi()));
        visalTx.setText(UtilTooth.keep1Point(record.getRbodyfat())+"%");
        bmiStatus.setText(MoveView.bmiString(record.getRbmi()));
        visalStatus.setText(MoveView.bftString(gender,user.getAgeYear(),record.getRbodyfat()));
    }

    /**
     * 初始化界面所有的进度条
     * @param record
     */
    public void initBodyBar(UserModel user, Records record){
        if(null!=record && null!=user){
            String sex = user.getSex();
            if(TextUtils.isEmpty(sex) || "null".equalsIgnoreCase(sex))sex = "1";
            int gender = Integer.parseInt(sex);
            // 体重
            if (user.getDanwei().equals(UtilConstants.UNIT_LB) || user.getDanwei().equals(UtilConstants.UNIT_FATLB) || user.getDanwei().equals(UtilConstants.UNIT_ST)){
                weightIndex.setText(UtilTooth.kgToLB_ForFatScale(record.getRweight()) + "lb");
            }else{
                weightIndex.setText(UtilTooth.keep1Point(record.getRweight())+ "kg");
            }

            MoveView.weight(BodyFatNewActivity.this,face_img_weight_ll,face_img_weight,weight_critical_point1,weight_critical_point2,biaoz,gender,user.getBheigth(),record.getRweight(),user.getDanwei());
            // 水分率
            waterIndex.setText(UtilTooth.keep1Point(record.getRbodywater())+"%");
            MoveView.moisture(BodyFatNewActivity.this,face_img_moisture_ll,face_img_moisture,moistrue_critical_point1,moistrue_critical_point2,biaoz_moistrue,gender,record.getRbodywater());
            // 脂肪率
            fatIndex.setText(UtilTooth.keep1Point(record.getRbodyfat())+"%");
            MoveView.bft(BodyFatNewActivity.this,face_img_bft_ll,face_img_bft,bft_critical_point1,bft_critical_point2,bft_critical_point3,bft_critical_point4,bft_biaoz,gender,user.getAgeYear(),record.getRbodyfat());
            // 骨量
            if (user.getDanwei().equals(UtilConstants.UNIT_LB) || user.getDanwei().equals(UtilConstants.UNIT_FATLB) || user.getDanwei().equals(UtilConstants.UNIT_ST)){
                boneIndex.setText(UtilTooth.kgToLB_ForFatScale(record.getRbone()) + "lb");
            }else{
                boneIndex.setText(UtilTooth.keep1Point(record.getRbone())+ "kg");
            }
            MoveView.bone(BodyFatNewActivity.this,face_img_bone_ll,face_img_bone,bone_critical_point1,bone_critical_point2,bone_biaoz,record.getRbone(),user.getDanwei());
            // BMI
            bmiIndex.setText(UtilTooth.keep1Point(record.getRbmi()));
            MoveView.bmi(BodyFatNewActivity.this,face_img_bmi_ll,face_img_bmi,bmi_critical_point1,bmi_critical_point2,bmi_critical_point3,bmi_biaoz,record.getRbmi());
            // 内脏脂肪指数
            visalfatIndex.setText(UtilTooth.keep1Point(record.getRvisceralfat()));
            MoveView.visceralFat(BodyFatNewActivity.this,face_img_visceral_ll,face_img_visceral,visceral_critical_point1,visceral_critical_point2,visceral_biaoz,record.getRvisceralfat());
            // BMR 基础代谢率
            bmrIndex.setText(UtilTooth.keep1Point(record.getRbmr())+"kcal");
            MoveView.bmr(BodyFatNewActivity.this,face_img_bmr_ll,face_img_bmr,bmr_critical_point1,bmr_biaoz,gender,user.getAgeYear(),record.getRweight(),record.getRbmr());
            // 肌肉含量
            float muscal = UtilTooth.keep1Point3(record.getRmuscle());
            if (user.getDanwei().equals(UtilConstants.UNIT_LB) || user.getDanwei().equals(UtilConstants.UNIT_FATLB) || user.getDanwei().equals(UtilConstants.UNIT_ST)){
                muscalIndex.setText(UtilTooth.kgToLB_ForFatScale(muscal) + "lb");
            }else{
                muscalIndex.setText(UtilTooth.keep1Point(muscal)+ "kg");
            }
            MoveView.muscle(BodyFatNewActivity.this,face_img_muscle_ll,face_img_muscle,muscle_critical_point1,muscle_critical_point2,muscle_biaoz,gender,user.getBheigth(),muscal,user.getDanwei());
            //身体年龄
            ageIndex.setText(UtilTooth.keep0Point(record.getBodyAge()));
        }
    }

    List<UserModel> babys = null;
    @OnClick(R.id.harmbaby_menu)
    public void harmBabyMenuClick(){
        try {
            if(null==babys || babys.size()==0) babys=uservice.getAllBabys();

            if(null==babys || babys.size()==0){
                //添加一个用户组
                startActivity(BabyAddActivity.creatIntent(BodyFatNewActivity.this));
            }else{
                //弹出选择用户组
                startActivity(BabyChoiceActivity.creatIntent(BodyFatNewActivity.this));
                if(null!=mBluetoothLeService){
                    mScanner.stopScane();
                    mBluetoothLeService.disconnect();
                }
            }
        } catch (Exception e) {
           Log.e(TAG,"脂肪秤页面点击抱婴按钮异常==>"+e.getMessage());
        }
    }



    @OnClick(R.id.setting_menu)
    public void setMenuClick(){
        startActivity(SettingActivity.creatIntent(BodyFatNewActivity.this));
    }

    @OnClick(R.id.history_menu)
    public void  historyMenuClick(){
        if(null==UtilConstants.CURRENT_USER){
            //调到用户列表选择页面
            Intent intent1 = new Intent();
            intent1.setClass(BodyFatNewActivity.this, UserListActivity.class);
            BodyFatNewActivity.this.startActivity(intent1);
        }else{
            startActivityForResult(RecordListActivity.creatIntent(BodyFatNewActivity.this,UtilConstants.CURRENT_USER),0);
        }
    }

    @OnClick(R.id.user_header)
    public void  userHeaderClick(){
        //调到用户列表选择页面
        Intent intent1 = new Intent();
        intent1.setClass(BodyFatNewActivity.this, UserListActivity.class);
        BodyFatNewActivity.this.startActivity(intent1);
    }

    @Override
    public void updateConnectionState(int resourceId) {
        switch (resourceId){
            case R.string.disconnected:
                bluetoothStatusTx.setText(getResources().getText(R.string.connect_state_not_connected));
                break;

            case R.string.connected:
                bluetoothStatusTx.setText(getResources().getText(R.string.connect_state_connected));
                break;
        }
    }

    @Override
    public void discoverBleService() {
        ToastUtils.ToastCenter(BodyFatNewActivity.this, getString(R.string.scale_paired_success));
        //发送人体参数
        if(null!= mDeviceName && (mDeviceName.toLowerCase().startsWith("heal")
                || mDeviceName.toLowerCase().startsWith("yu"))){

            try {
                if(null!=mBluetoothLeService){
                    final BluetoothGattCharacteristic characteristic = mBluetoothLeService.getCharacteristicNew(mBluetoothLeService.getSupportedGattServices(), "2a9c");
                    mBluetoothLeService.setCharacteristicIndaicate(characteristic, true); //开始监听通道
                    //发送用户组数据
                    String unit = "00";
                    if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_ST)) {
                        unit = "02";
                    } else if (UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_LB)) {
                        unit = "01";
                    } else {
                        unit = "00";
                    }
                    // 获取用户组
                    String p = UtilConstants.CURRENT_USER.getGroup().replace("P", "0");
                    // 获取 校验位
                    String xor = Integer.toHexString(StringUtils.hexToTen("fd") ^ StringUtils.hexToTen("37")^ StringUtils.hexToTen(unit) ^ StringUtils.hexToTen(p));
                    Log.e(TAG, "发送新称数据：" + "fd37"+unit + p + "000000000000" + xor);
                    // 发送数据
                    BleHelper1.getInstance().sendDateToScale(mBluetoothLeService,"fd37"+unit + p + "000000000000" + xor);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            BleHelper1.getInstance().sendDateToScale(mBluetoothLeService, MyUtil.getUserInfo());
        }
    }

    @Override
    public void reveiveBleData(String readMessage) {
        System.out.println("检测读取到数据：" + readMessage);
        if(!mActivty)return ;
        if(TextUtils.isEmpty(readMessage)) return;
        //测脂错误
        if (readMessage.equals(UtilConstants.ERROR_CODE)) {
            if(UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_ST) || UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_LB)){
                Toast.makeText(BodyFatNewActivity.this, getString(R.string.user_data_error), Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(BodyFatNewActivity.this, getString(R.string.user_data_error_lb), Toast.LENGTH_LONG).show();
            }
            return;
        } else if (readMessage.equals(UtilConstants.ERROR_CODE_TEST)) {
            Toast.makeText(BodyFatNewActivity.this, getString(R.string.scale_measurement_error), Toast.LENGTH_LONG).show();
            return;
        }
        //秤和人体参数不匹配
        if (readMessage.startsWith(UtilConstants.BABY_SCALE)) {
            if (UtilConstants.CURRENT_USER.getAgeYear() < 1 || UtilConstants.CURRENT_USER.getBheigth()<30) {
                if(UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_KG)){
                    Toast.makeText(BodyFatNewActivity.this, getString(R.string.age_error_5), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(BodyFatNewActivity.this, getString(R.string.age_error_7), Toast.LENGTH_SHORT).show();
                }

                return;
            }
        }else{
            if (UtilConstants.CURRENT_USER.getAgeYear() < 10 || UtilConstants.CURRENT_USER.getBheigth()<100) {
                if(UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_KG)){
                    Toast.makeText(BodyFatNewActivity.this, getString(R.string.age_error_4), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(BodyFatNewActivity.this, getString(R.string.age_error_6), Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
        //处理不同类型的秤

        boolean newScale = false;
        try {
            if ((readMessage.startsWith("0306"))) {//阿里秤
                newScale = true;
                UtilConstants.CURRENT_SCALE = UtilConstants.BODY_SCALE;
            }else{
                newScale = false;


                /** 称类型判断 */
                String choice_scale = "";
                if ((readMessage.toLowerCase().startsWith(UtilConstants.BODY_SCALE))) {
                    choice_scale = UtilConstants.BODY_SCALE;
                }else if ((readMessage.toLowerCase().startsWith(UtilConstants.BATHROOM_SCALE))) {
                    choice_scale = UtilConstants.BATHROOM_SCALE;
                }else if ((readMessage.toLowerCase().startsWith(UtilConstants.BABY_SCALE))) {
                    choice_scale = UtilConstants.BABY_SCALE;
                }else if ((readMessage.toLowerCase().startsWith(UtilConstants.KITCHEN_SCALE))) {
                    choice_scale = UtilConstants.KITCHEN_SCALE;
                }
                UtilConstants.CURRENT_SCALE = choice_scale;
            }
            if(null!=mDeviceName && mDeviceName.toLowerCase().startsWith(UtilConstants.DLscaleName)){ //新的DL Scale
                //CF 88 13 00 14 00 00 00 00 00 40
                if(RecordDao.isLockData(readMessage)){
                    if ((System.currentTimeMillis()- UtilConstants.receiveDataTime>1000) && null==receiveDataDialog) {
                        UtilConstants.receiveDataTime = System.currentTimeMillis();
                        dueDate(readMessage,3);
                    }
                }else{
                    dueDate(readMessage,2);
                }
            }else{
                /**判断是不是两次连续的数据*/
                if (readMessage.length() > 31 && (System.currentTimeMillis()- UtilConstants.receiveDataTime>1000) && null==receiveDataDialog) {
                    UtilConstants.receiveDataTime=System.currentTimeMillis();

                    if(newScale){
							/*脂肪秤*/
                        UtilConstants.CURRENT_SCALE=UtilConstants.BODY_SCALE;
                        UtilConstants.CURRENT_USER.setScaleType(UtilConstants.CURRENT_SCALE);
                        dueDate(readMessage,1);
                    }else{
                        if (readMessage.equals(UtilConstants.ERROR_CODE)) {
                            if(UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_ST) || UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_LB)){
                                Toast.makeText(BodyFatNewActivity.this, getString(R.string.user_data_error), Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(BodyFatNewActivity.this, getString(R.string.user_data_error_lb), Toast.LENGTH_LONG).show();
                            }
                            return;
                        } else if (readMessage.equals(UtilConstants.ERROR_CODE_TEST)) {
                            Toast.makeText(BodyFatNewActivity.this, getString(R.string.scale_measurement_error), Toast.LENGTH_LONG).show();
                            return;
                        }
							/*显示称类型错误*/
                        if (!readMessage.startsWith(UtilConstants.BATHROOM_SCALE)&&readMessage.length()>31) {
								/*跳转到制定的秤界面*/
                            if(readMessage.startsWith(UtilConstants.BODY_SCALE)){
									/*脂肪秤*/
                                UtilConstants.CURRENT_SCALE=UtilConstants.BODY_SCALE;
                                UtilConstants.CURRENT_USER.setScaleType(UtilConstants.CURRENT_SCALE);
									/*保存测量数据*/
                                RecordDao.dueDate(recordService, readMessage);
                            }else if(readMessage.startsWith(UtilConstants.BABY_SCALE)){
									/*婴儿秤*/
                                UtilConstants.CURRENT_SCALE=UtilConstants.BABY_SCALE;
                                UtilConstants.CURRENT_USER.setScaleType(UtilConstants.CURRENT_SCALE);
									/*保存测量数据*/
                                RecordDao.dueDate(recordService, readMessage);
                            }else if (readMessage.startsWith(UtilConstants.KITCHEN_SCALE)) {
									/* 厨房秤 */
                                UtilConstants.CURRENT_SCALE = UtilConstants.KITCHEN_SCALE;
                                UtilConstants.CURRENT_USER.setScaleType(UtilConstants.CURRENT_SCALE);
									/* 保存测量数据 */
                                RecordDao.dueKitchenDate(recordService, readMessage, null);
                            }
                            handler.sendEmptyMessage(UtilConstants.scaleChangeMessage);

                            return;
                        }
                        if (readMessage.equals(UtilConstants.ERROR_CODE_GETDATE)) {
                            openErrorDiolg("2");
                            return;
                        }
                        if ((readMessage.startsWith("c") && readMessage.length() > 31)) {
                            dueDate(readMessage,0);
                        }
                    }
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "解析数据异常"+e.getMessage());
        }
    }

    @Override
    protected  void saveDataCallBack(Records records){
        if(records.getScaleType().startsWith(UtilConstants.BATHROOM_SCALE)){
									/*人体秤*/
            UtilConstants.CURRENT_SCALE=UtilConstants.BATHROOM_SCALE;
            UtilConstants.CURRENT_USER.setScaleType(UtilConstants.CURRENT_SCALE);
            handler.sendEmptyMessage(UtilConstants.scaleChangeMessage);
        }else if(records.getScaleType().startsWith(UtilConstants.BABY_SCALE)){
									/*婴儿秤*/
            UtilConstants.CURRENT_SCALE=UtilConstants.BABY_SCALE;
            UtilConstants.CURRENT_USER.setScaleType(UtilConstants.CURRENT_SCALE);
									/*保存测量数据*/
            handler.sendEmptyMessage(UtilConstants.scaleChangeMessage);
        }else if (records.getScaleType().startsWith(UtilConstants.KITCHEN_SCALE)) {
									/* 厨房秤 */
            UtilConstants.CURRENT_SCALE = UtilConstants.KITCHEN_SCALE;
            UtilConstants.CURRENT_USER.setScaleType(UtilConstants.CURRENT_SCALE);
            handler.sendEmptyMessage(UtilConstants.scaleChangeMessage);
        }else{
            localData(records,UtilConstants.CURRENT_USER);
            initBodyBar(UtilConstants.CURRENT_USER,records);
        }
    }

    /**
     * 数据处理
     * @param readMessage
     * @param i
     */
    private void dueDate(String readMessage, int i) {
        if(0==i){//旧秤
            receiveRecod = MyUtil.parseMeaage(this.recordService, readMessage);
            Message msg1 = handler.obtainMessage(0);
            msg1.obj = receiveRecod;
            handler.sendMessage(msg1);
        }else if(1==i){//阿里秤
            receiveRecod = MyUtil.parseZuKangMeaage(this.recordService, readMessage,UtilConstants.CURRENT_USER);
            Message msg1 = handler.obtainMessage(0);
            msg1.obj = receiveRecod;
            handler.sendMessage(msg1);
        }else if(2==i){//新称过程数据
            float weight = MyUtil.getWeightData(readMessage);
            weithValueTx.setTexts(UtilTooth.keep1Point(weight),null);
        }else if(3==i){//新秤锁定数据
            receiveRecod = MyUtil.parseDLScaleMeaage(this.recordService, readMessage,UtilConstants.CURRENT_USER);
            Message msg1 = handler.obtainMessage(0);
            msg1.obj = receiveRecod;
            handler.sendMessage(msg1);
        }
    }



    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0 :
                    Records data  = (Records)msg.obj;
                    if(null!=data){
                        weithValueTx.setTexts(UtilTooth.keep1Point(data.getRweight()),null);
                        playSound();
                        showReceiveDataDialog();
                    }
                    break;
                case 1 :

                    break;
                case 5 :

                    break;
                case UtilConstants.scaleChangeMessage :
					/*保存秤类型*/
                    if(UtilConstants.su==null){
                        UtilConstants.su=new SharedPreferencesUtil(LoadingActivity.mainActivty);
                    }
                    UtilConstants.su.editSharedPreferences("lefuconfig", "scale", UtilConstants.CURRENT_SCALE);
					/*保存用户信息*/
                    try {
                        uservice.update(UtilConstants.CURRENT_USER);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
					/*跳转*/
                    ExitApplication.getInstance().exit(BodyFatNewActivity.this);
                    Intent intent = new Intent();
                    intent.setClass(BodyFatNewActivity.this, LoadingActivity.class);
                    BodyFatNewActivity.this.startActivity(intent);
                    break;

            }
        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }


    @OnClick(R.id.weight_jiantou)
    public void menuWeightOpenClick(){
        if(status_bar2.getVisibility()==View.VISIBLE){
            status_bar2.setVisibility(View.GONE);
            weight_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }else{
            status_bar2.setVisibility(View.VISIBLE);
            weight_jiantou.setBackground(getDrawable(R.drawable.up_arrow));
        }

        if(status_bar_muscial.getVisibility()==View.VISIBLE){
            status_bar_muscial.setVisibility(View.GONE);
            muscal_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_visalfat.getVisibility()==View.VISIBLE){
            status_bar_visalfat.setVisibility(View.GONE);
            visalfat_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_bmi.getVisibility()==View.VISIBLE){
            status_bar_bmi.setVisibility(View.GONE);
            bmi_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_bone.getVisibility()==View.VISIBLE){
            status_bar_bone.setVisibility(View.GONE);
            bone_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_bft.getVisibility()==View.VISIBLE){
            status_bar_bft.setVisibility(View.GONE);
            fat_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_moisture.getVisibility()==View.VISIBLE){
            status_bar_moisture.setVisibility(View.GONE);
            water_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_bmr.getVisibility()==View.VISIBLE){
            status_bar_bmr.setVisibility(View.GONE);
            bmr_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }
    }

    @OnClick(R.id.muscal_jiantou)
    public void menuMuscalOpenClick(){
        if(status_bar_muscial.getVisibility()==View.VISIBLE){
            status_bar_muscial.setVisibility(View.GONE);
            muscal_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }else{
            status_bar_muscial.setVisibility(View.VISIBLE);
            muscal_jiantou.setBackground(getDrawable(R.drawable.up_arrow));
        }

        if(status_bar2.getVisibility()==View.VISIBLE){
            status_bar2.setVisibility(View.GONE);
            weight_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_visalfat.getVisibility()==View.VISIBLE){
            status_bar_visalfat.setVisibility(View.GONE);
            visalfat_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_bmi.getVisibility()==View.VISIBLE){
            status_bar_bmi.setVisibility(View.GONE);
            bmi_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_bone.getVisibility()==View.VISIBLE){
            status_bar_bone.setVisibility(View.GONE);
            bone_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_bft.getVisibility()==View.VISIBLE){
            status_bar_bft.setVisibility(View.GONE);
            fat_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_moisture.getVisibility()==View.VISIBLE){
            status_bar_moisture.setVisibility(View.GONE);
            water_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_bmr.getVisibility()==View.VISIBLE){
            status_bar_bmr.setVisibility(View.GONE);
            bmr_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }
    }

    @OnClick(R.id.visalfat_jiantou)
    public void menuVisalfatOpenClick(){
        if(status_bar_visalfat.getVisibility()==View.VISIBLE){
            status_bar_visalfat.setVisibility(View.GONE);
            visalfat_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }else{
            status_bar_visalfat.setVisibility(View.VISIBLE);
            visalfat_jiantou.setBackground(getDrawable(R.drawable.up_arrow));
        }

        if(status_bar_muscial.getVisibility()==View.VISIBLE){
            status_bar_muscial.setVisibility(View.GONE);
            muscal_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar2.getVisibility()==View.VISIBLE){
            status_bar2.setVisibility(View.GONE);
            weight_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_bmi.getVisibility()==View.VISIBLE){
            status_bar_bmi.setVisibility(View.GONE);
            bmi_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_bone.getVisibility()==View.VISIBLE){
            status_bar_bone.setVisibility(View.GONE);
            bone_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_bft.getVisibility()==View.VISIBLE){
            status_bar_bft.setVisibility(View.GONE);
            fat_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_moisture.getVisibility()==View.VISIBLE){
            status_bar_moisture.setVisibility(View.GONE);
            water_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_bmr.getVisibility()==View.VISIBLE){
            status_bar_bmr.setVisibility(View.GONE);
            bmr_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }
    }

    @OnClick(R.id.bmi_jiantou)
    public void menuBmiOpenClick(){
        if(status_bar_bmi.getVisibility()==View.VISIBLE){
            status_bar_bmi.setVisibility(View.GONE);
            bmi_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }else{
            status_bar_bmi.setVisibility(View.VISIBLE);
            bmi_jiantou.setBackground(getDrawable(R.drawable.up_arrow));
        }

        if(status_bar_muscial.getVisibility()==View.VISIBLE){
            status_bar_muscial.setVisibility(View.GONE);
            muscal_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_visalfat.getVisibility()==View.VISIBLE){
            status_bar_visalfat.setVisibility(View.GONE);
            visalfat_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar2.getVisibility()==View.VISIBLE){
            status_bar2.setVisibility(View.GONE);
            weight_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_bone.getVisibility()==View.VISIBLE){
            status_bar_bone.setVisibility(View.GONE);
            bone_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_bft.getVisibility()==View.VISIBLE){
            status_bar_bft.setVisibility(View.GONE);
            fat_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_moisture.getVisibility()==View.VISIBLE){
            status_bar_moisture.setVisibility(View.GONE);
            water_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_bmr.getVisibility()==View.VISIBLE){
            status_bar_bmr.setVisibility(View.GONE);
            bmr_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }
    }

    @OnClick(R.id.bone_jiantou)
    public void menuBoneOpenClick(){
        if(status_bar_bone.getVisibility()==View.VISIBLE){
            status_bar_bone.setVisibility(View.GONE);
            bone_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }else{
            status_bar_bone.setVisibility(View.VISIBLE);
            bone_jiantou.setBackground(getDrawable(R.drawable.up_arrow));
        }

        if(status_bar_muscial.getVisibility()==View.VISIBLE){
            status_bar_muscial.setVisibility(View.GONE);
            muscal_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_visalfat.getVisibility()==View.VISIBLE){
            status_bar_visalfat.setVisibility(View.GONE);
            visalfat_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_bmi.getVisibility()==View.VISIBLE){
            status_bar_bmi.setVisibility(View.GONE);
            bmi_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar2.getVisibility()==View.VISIBLE){
            status_bar2.setVisibility(View.GONE);
            weight_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_bft.getVisibility()==View.VISIBLE){
            status_bar_bft.setVisibility(View.GONE);
            fat_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_moisture.getVisibility()==View.VISIBLE){
            status_bar_moisture.setVisibility(View.GONE);
            water_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_bmr.getVisibility()==View.VISIBLE){
            status_bar_bmr.setVisibility(View.GONE);
            bmr_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }
    }

    @OnClick(R.id.fat_jiantou)
    public void menuFatOpenClick(){
        if(status_bar_bft.getVisibility()==View.VISIBLE){
            status_bar_bft.setVisibility(View.GONE);
            fat_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }else{
            status_bar_bft.setVisibility(View.VISIBLE);
            fat_jiantou.setBackground(getDrawable(R.drawable.up_arrow));
        }

        if(status_bar_muscial.getVisibility()==View.VISIBLE){
            status_bar_muscial.setVisibility(View.GONE);
            muscal_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_visalfat.getVisibility()==View.VISIBLE){
            status_bar_visalfat.setVisibility(View.GONE);
            visalfat_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_bmi.getVisibility()==View.VISIBLE){
            status_bar_bmi.setVisibility(View.GONE);
            bmi_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_bone.getVisibility()==View.VISIBLE){
            status_bar_bone.setVisibility(View.GONE);
            bone_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar2.getVisibility()==View.VISIBLE){
            status_bar2.setVisibility(View.GONE);
            weight_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_moisture.getVisibility()==View.VISIBLE){
            status_bar_moisture.setVisibility(View.GONE);
            water_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_bmr.getVisibility()==View.VISIBLE){
            status_bar_bmr.setVisibility(View.GONE);
            bmr_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }
    }

    @OnClick(R.id.water_jiantou)
    public void menuWaterOpenClick(){
        if(status_bar_moisture.getVisibility()==View.VISIBLE){
            status_bar_moisture.setVisibility(View.GONE);
            water_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }else{
            status_bar_moisture.setVisibility(View.VISIBLE);
            water_jiantou.setBackground(getDrawable(R.drawable.up_arrow));
        }

        if(status_bar_muscial.getVisibility()==View.VISIBLE){
            status_bar_muscial.setVisibility(View.GONE);
            muscal_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_visalfat.getVisibility()==View.VISIBLE){
            status_bar_visalfat.setVisibility(View.GONE);
            visalfat_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_bmi.getVisibility()==View.VISIBLE){
            status_bar_bmi.setVisibility(View.GONE);
            bmi_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_bone.getVisibility()==View.VISIBLE){
            status_bar_bone.setVisibility(View.GONE);
            bone_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_bft.getVisibility()==View.VISIBLE){
            status_bar_bft.setVisibility(View.GONE);
            fat_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar2.getVisibility()==View.VISIBLE){
            status_bar2.setVisibility(View.GONE);
            weight_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_bmr.getVisibility()==View.VISIBLE){
            status_bar_bmr.setVisibility(View.GONE);
            bmr_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }
    }

    @OnClick(R.id.bmr_jiantou)
    public void menuBmrOpenClick(){
        if(status_bar_bmr.getVisibility()==View.VISIBLE){
            status_bar_bmr.setVisibility(View.GONE);
            bmr_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }else{
            status_bar_bmr.setVisibility(View.VISIBLE);
            bmr_jiantou.setBackground(getDrawable(R.drawable.up_arrow));
        }

        if(status_bar_muscial.getVisibility()==View.VISIBLE){
            status_bar_muscial.setVisibility(View.GONE);
            muscal_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_visalfat.getVisibility()==View.VISIBLE){
            status_bar_visalfat.setVisibility(View.GONE);
            visalfat_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_bmi.getVisibility()==View.VISIBLE){
            status_bar_bmi.setVisibility(View.GONE);
            bmi_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_bone.getVisibility()==View.VISIBLE){
            status_bar_bone.setVisibility(View.GONE);
            bone_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_bft.getVisibility()==View.VISIBLE){
            status_bar_bft.setVisibility(View.GONE);
            fat_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar_moisture.getVisibility()==View.VISIBLE){
            status_bar_moisture.setVisibility(View.GONE);
            water_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }

        if(status_bar2.getVisibility()==View.VISIBLE){
            status_bar2.setVisibility(View.GONE);
            weight_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }
    }

}
