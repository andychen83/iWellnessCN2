package com.lefu.es.system;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lefu.es.ble.BlueSingleton;
import com.lefu.es.blenew.bean.BluetoothLeDevice1;
import com.lefu.es.blenew.constant.BluetoolUtil1;
import com.lefu.es.blenew.helper.BleHelper1;
import com.lefu.es.blenew.service.BluetoothLeService1;
import com.lefu.es.cache.CacheHelper;
import com.lefu.es.constant.AppData;
import com.lefu.es.constant.BluetoolUtil;
import com.lefu.es.constant.UtilConstants;
import com.lefu.es.db.RecordDao;
import com.lefu.es.entity.NutrientBo;
import com.lefu.es.entity.Records;
import com.lefu.es.entity.UserModel;
import com.lefu.es.service.ExitApplication;
import com.lefu.es.service.TimeService;
import com.lefu.es.service.UserService;
import com.lefu.es.util.MoveView;
import com.lefu.es.util.MyUtil;
import com.lefu.es.util.SharedPreferencesUtil;
import com.lefu.es.util.StringUtils;
import com.lefu.es.util.ToastUtils;
import com.lefu.es.util.UtilTooth;
import com.lefu.es.view.MyTextView2;
import com.lefu.es.view.MyTextView5;
import com.lefu.iwellness.newes.cn.system.R;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.lefu.iwellness.newes.cn.system.R.id.compare_tv;
import static com.lefu.iwellness.newes.cn.system.R.style.dialog;

/*
抱婴模式秤
* */
public class BabyScaleNewActivity extends BaseBleActivity {

    @Bind(R.id.setting_menu)
     RelativeLayout set;

    @Bind(R.id.harmbaby_menu)
     RelativeLayout up_scale;

    @Bind(R.id.back_ly)
    LinearLayout backBtn;

    @Bind(R.id.bluetooth_status)
    AppCompatTextView bluetoothStatusTx;

    @Bind(R.id.weith_value_tx)
    MyTextView5 weithValueTx;

    @Bind(R.id.user_name)
    AppCompatTextView userNameTx;

    @Bind(R.id.user_header)
    SimpleDraweeView userHeadImg;

    @Bind(R.id.targe_tx)
    TextView targetTx;

    @Bind(R.id.compare_last_tx)
    MyTextView2 compare_tv;

    @Bind(R.id.weith_status)
    TextView weithStatus;

    @Bind(R.id.unti_tv)
    TextView unit_tv;

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

    private   boolean isOpenBabyScale = false;//是否是上秤模式

    protected UserModel babyUser = null; //选择的婴儿


    /**
     * 构建实例
     * @param context
     * @param baby
     * @return
     */
    public static Intent creatIntent(Context context,UserModel baby){
        Intent intent = new Intent(context,BabyScaleNewActivity.class);
        intent.putExtra("baby",baby);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_scale_new);
        ButterKnife.bind(this);

        Serializable serializable = getIntent().getSerializableExtra("baby");
        if(null==serializable){
            Toast.makeText(BabyScaleNewActivity.this, getString(R.string.choice_a_baby), Toast.LENGTH_LONG).show();
            finish();
        }else{
            babyUser = (UserModel)serializable;
            initView(babyUser);

            Toast.makeText(BabyScaleNewActivity.this, getString(R.string.click_onscale_waring), Toast.LENGTH_LONG).show();
        }

    }

    private void initView(UserModel babyUser) {
        if(null!=babyUser){
            if(!TextUtils.isEmpty(babyUser.getPer_photo())){
                userHeadImg.setImageURI(Uri.fromFile(new File(babyUser.getPer_photo())));
            }
            userNameTx.setText(babyUser.getUserName());
            targetTx.setText(UtilTooth.keep1Point3(babyUser.getTargweight())+"");
            //初始化界面参数
            initViewData(babyUser);
        }
    }

    protected  Records lastRecord = null;
    public void initViewData(final UserModel babyUser){
        if(null!=babyUser){
            //获取最后一次测量记录
            Thread thread=new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try {
                        lastRecord = recordService.findLastRecords(babyUser.getId());
                        if(null!=lastRecord){
                            Message message=initHandler.obtainMessage(1);
                            message.obj=lastRecord;
                            initHandler.sendMessage(message);
                        }else{
                            Message message=initHandler.obtainMessage(2);
                            initHandler.sendMessage(message);
                        }
                    } catch (Exception e) {
                        Log.e(TAG,"初始化抱婴界面失败："+e.getMessage());
                    }
                }
            });
            thread.start();
        }
    }

    /**
     * 初始化页面
     */
    Handler initHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case 1: //初始化界面
                    Records lastRecord = (Records)msg.obj;
                    if(null!=lastRecord){
                        localData(lastRecord,babyUser);
                        initBodyBar(babyUser,lastRecord);
                    }
                    break;
                case 2:
                    localData(null,babyUser);
                    initBodyBar(babyUser,null);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    

    /**
     * 初始化 圆圈参数
     * @param
     */
    private  void localData(Records record,UserModel user){
        if(null==user)return;
        if(null==record){
            compare_tv.setTexts("0.0", null);
        }else{
            if (user.getDanwei().equals(UtilConstants.UNIT_LB) || user.getDanwei().equals(UtilConstants.UNIT_FATLB) || user.getDanwei().equals(UtilConstants.UNIT_ST)) {
                weithValueTx.setTexts(UtilTooth.lbozToString(record.getRweight()), null);
                if (null != unit_tv) {
                    unit_tv.setText(this.getText(R.string.lb_danwei));
                }
            } else {
                weithValueTx.setTexts(UtilTooth.keep1Point(record.getRweight()), null);
                if (null != unit_tv) {
                    unit_tv.setText(this.getText(R.string.kg_danwei));
                }
            }
            String sex = user.getSex();
            if(TextUtils.isEmpty(sex) || "null".equalsIgnoreCase(sex))sex = "1";
            int gender = Integer.parseInt(sex);
            weithStatus.setText(MoveView.weightString(gender,user.getBheigth(),record.getRweight()));

            if (user.getDanwei().equals(UtilConstants.UNIT_KG)) {
                if (null == record.getCompareRecord() || "".equals(record.getCompareRecord())) {
                    compare_tv.setTexts("0.0", null);
                    compare_tv.setTexts("0.0 " + this.getText(R.string.kg_danwei), null);
                } else {
                    BigDecimal b = new BigDecimal(Double.parseDouble(record.getCompareRecord()));
                    float cr = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                    if (cr > 0) {
                        compare_tv.setTexts("↑" + UtilTooth.myroundString3(Math.abs(cr) + "") + this.getText(R.string.kg_danwei), null);
                    } else if (cr < 0) {
                        compare_tv.setTexts("↓" + UtilTooth.myroundString3(Math.abs(cr) + "") + this.getText(R.string.kg_danwei), null);
                    } else {
                        compare_tv.setTexts(UtilTooth.myroundString3(record.getCompareRecord() + "") + this.getText(R.string.kg_danwei), null);
                    }
                }
            } else if (user.getDanwei().equals(UtilConstants.UNIT_LB) || user.getDanwei().equals(UtilConstants.UNIT_FATLB) || user.getDanwei().equals(UtilConstants.UNIT_ST)) {
                if (null == record.getCompareRecord() || "".equals(record.getCompareRecord().trim())) {
                    record.setCompareRecord("0");
                    compare_tv.setTexts("0.0 " + " " + this.getText(R.string.lb_danwei), null);
                } else {
                    float cr = Float.parseFloat(record.getCompareRecord());
                    if (cr > 0) {
                        compare_tv.setTexts("↑" + UtilTooth.lbozToString(record.getRweight()), null);
                    } else if (cr < 0) {
                        compare_tv.setTexts("↓" + UtilTooth.lbozToString(record.getRweight()), null);
                    } else {
                        compare_tv.setTexts("0.0" + " " + this.getText(R.string.lboz_danwei), null);
                    }
                }
            } else {
                if (null == record.getCompareRecord() || "".equals(record.getCompareRecord())) {
                    compare_tv.setTexts("0.0", null);
                    compare_tv.setTexts("0.0 " + this.getText(R.string.kg_danwei), null);
                } else {
                    BigDecimal b = new BigDecimal(Double.parseDouble(record.getCompareRecord()));
                    float cr = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                    if (cr > 0) {
                        compare_tv.setTexts("↑" + UtilTooth.myroundString3(Math.abs(cr) + "") + this.getText(R.string.kg_danwei), null);
                    } else if (cr < 0) {
                        compare_tv.setTexts("↓" + UtilTooth.myroundString3(Math.abs(cr) + "") + this.getText(R.string.kg_danwei), null);
                    } else {
                        compare_tv.setTexts(UtilTooth.myroundString3(record.getCompareRecord() + "") + this.getText(R.string.kg_danwei), null);
                    }
                }
            }
        }
    }

    /**
     * 初始化界面所有的进度条
     * @param record
     */
    public void initBodyBar(UserModel user, Records record){
        if(null!=record && null!=user){
            // BMI
            bmiIndex.setText(UtilTooth.keep1Point(record.getRbmi()));
            MoveView.bmi(BabyScaleNewActivity.this,face_img_bmi_ll,face_img_bmi,bmi_critical_point1,bmi_critical_point2,bmi_critical_point3,bmi_biaoz,record.getRbmi());

        }else{
            bmiIndex.setText("0");
            MoveView.bmi(BabyScaleNewActivity.this,face_img_bmi_ll,face_img_bmi,bmi_critical_point1,bmi_critical_point2,bmi_critical_point3,bmi_biaoz,0);
        }
    }

    @OnClick(R.id.user_header)
    public void  userHeaderClick(){
        startActivityForResult(UserBabyListActivity.creatIntent(BabyScaleNewActivity.this,babyUser.getId()),102);
    }

    /**
     * 返回事件
     */
    @OnClick(R.id.back_ly)
    public void backClick(){
        this.finish();
    }

    @OnClick(R.id.history_menu)
    public void  historyMenuClick(){
        startActivityForResult(RecordListActivity.creatIntent(BabyScaleNewActivity.this,babyUser),0);
    }

    @OnClick(R.id.harmbaby_menu)
    public void  upScaleClick(){
        this.isOpenBabyScale = true;
        receiveRecod = null;
        ToastUtils.ToastCenter(BabyScaleNewActivity.this, getString(R.string.adult_onscale_waring));

        //发送人体参数
        discoverBleService();
    }

    @OnClick(R.id.setting_menu)
    public void setMenuClick(){
        startActivity(BodyFatScaleSetActivity.creatIntent(BabyScaleNewActivity.this,babyUser));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 102) {
            Bundle loginBundle = data.getExtras();
            if(null!=loginBundle){
                Serializable serializable = loginBundle.getSerializable("user");
                if(null!=serializable){
                    UserModel userModel = (UserModel)serializable;
                    initView(userModel);
                }
            }
        }else if (resultCode == 103) {
            Bundle loginBundle = data.getExtras();
            if(null!=loginBundle){
                Serializable serializable = loginBundle.getSerializable("user");
                if(null!=serializable){
                    UserModel user = (UserModel) serializable;
                    //替换当前页面最后的测量记录
                    babyUser = user;
                    initView(babyUser);
                    //通知界面更新
                    Message message=initHandler.obtainMessage(1);
                    message.obj=lastRecord;
                    initHandler.sendMessage(message);
                    //保存记录
                    RecordDao.handHarmBabyData(recordService,lastRecord,babyUser);
                    //重置
                    receiveRecod = null;
                    //lastRecord = null;
                    isOpenBabyScale = false;
                }
            }
        }
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
       // ToastUtils.ToastCenter(BabyScaleNewActivity.this, getString(R.string.scale_paired_success));
        //发送人体参数
        if(null!= mDeviceName && (mDeviceName.toLowerCase().startsWith("heal")
                || mDeviceName.toLowerCase().startsWith("yu"))){

            try {
                if(null!=mBluetoothLeService){
                    final BluetoothGattCharacteristic characteristic = mBluetoothLeService.getCharacteristicNew(mBluetoothLeService.getSupportedGattServices(), "2a9c");
                    mBluetoothLeService.setCharacteristicIndaicate(characteristic, true); //开始监听通道
                    //发送用户组数据
                    String unit = "00";
                    if (babyUser.getDanwei().equals(UtilConstants.UNIT_ST)) {
                        unit = "02";
                    } else if (babyUser.getDanwei().equals(UtilConstants.UNIT_LB)) {
                        unit = "01";
                    } else {
                        unit = "00";
                    }
                    // 获取用户组
                    String p = babyUser.getGroup().replace("P", "0");
                    // 获取 校验位
                    String xor = Integer.toHexString(StringUtils.hexToTen("fd") ^ StringUtils.hexToTen("37")^ StringUtils.hexToTen(unit) ^ StringUtils.hexToTen(p));
                    Log.e(TAG, "发送新称数据：" + "fd37"+unit + p + "000000000000" + xor);
                    // 发送数据
                   if(isOpenBabyScale) BleHelper1.getInstance().sendDateToScale(mBluetoothLeService,"fd37"+unit + p + "000000000000" + xor);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            if(isOpenBabyScale) BleHelper1.getInstance().sendDateToScale(mBluetoothLeService, MyUtil.getUserInfo());
        }
    }

    @Override
    public void reveiveBleData(String readMessage) {

        System.out.println("检测读取到数据：" + readMessage);
        if(TextUtils.isEmpty(readMessage)) return;
        if(!isOpenBabyScale) {
            //Toast.makeText(BabyScaleNewActivity.this, getString(R.string.open_harmbaby_scale), Toast.LENGTH_SHORT).show();
            return;
        }
        //测脂错误
        if (readMessage.equals(UtilConstants.ERROR_CODE)) {
            if(babyUser.getDanwei().equals(UtilConstants.UNIT_ST) || babyUser.getDanwei().equals(UtilConstants.UNIT_LB)){
                Toast.makeText(BabyScaleNewActivity.this, getString(R.string.user_data_error), Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(BabyScaleNewActivity.this, getString(R.string.user_data_error_lb), Toast.LENGTH_LONG).show();
            }
            return;
        } else if (readMessage.equals(UtilConstants.ERROR_CODE_TEST)) {
            Toast.makeText(BabyScaleNewActivity.this, getString(R.string.scale_measurement_error), Toast.LENGTH_LONG).show();
            return;
        }
        //处理不同类型的秤
        boolean newScale = false;
        try {
            if ((readMessage.startsWith("0306"))) {//阿里秤
                newScale = true;
                UtilConstants.CURRENT_SCALE = UtilConstants.BODY_SCALE;
            }else{
                newScale = false;
            }
            if(null!=mDeviceName && mDeviceName.toLowerCase().startsWith(UtilConstants.DLscaleName)){ //新的DL Scale
                //CF 88 13 00 14 00 00 00 00 00 40
                if(RecordDao.isLockData(readMessage)){
                    if ((System.currentTimeMillis()- UtilConstants.receiveDataTime>1000)) {
                        UtilConstants.receiveDataTime = System.currentTimeMillis();
                        dueDate(readMessage,3);
                    }
                }else{
                    dueDate(readMessage,2);
                }
            }else{
                /**判断是不是两次连续的数据*/
                if (readMessage.length() > 31 && (System.currentTimeMillis()- UtilConstants.receiveDataTime>1000)) {
                    UtilConstants.receiveDataTime=System.currentTimeMillis();
                    if(newScale){
                        dueDate(readMessage,1);
                    }else{
                        if (readMessage.equals(UtilConstants.ERROR_CODE)) {
                            if(babyUser.getDanwei().equals(UtilConstants.UNIT_ST) || babyUser.getDanwei().equals(UtilConstants.UNIT_LB)){
                                Toast.makeText(BabyScaleNewActivity.this, getString(R.string.user_data_error), Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(BabyScaleNewActivity.this, getString(R.string.user_data_error_lb), Toast.LENGTH_LONG).show();
                            }
                            return;
                        } else if (readMessage.equals(UtilConstants.ERROR_CODE_TEST)) {
                            Toast.makeText(BabyScaleNewActivity.this, getString(R.string.scale_measurement_error), Toast.LENGTH_LONG).show();
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
        initBodyBar(babyUser,records);
    }

    /**
     * 数据处理
     * @param readMessage
     * @param i
     */
    private void dueDate(String readMessage, int i) {
        Records records = null;
        if(0==i){//旧秤
            records = MyUtil.parseMeaageForBaby(this.recordService, readMessage);
        }else if(1==i){//阿里秤
            records = MyUtil.parseZuKangMeaage(this.recordService, readMessage,babyUser);
        }else if(2==i){//新称过程数据
            MyUtil.setProcessWeightData(readMessage,weithValueTx);
        }else if(3==i){//新秤锁定数据
            records = MyUtil.parseDLScaleMeaage(this.recordService, readMessage,babyUser);
        }
        Message msg1 = handler.obtainMessage(0);
        msg1.obj = records;
        handler.sendMessage(msg1);
    }



    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0 :
                    Records data  = (Records)msg.obj;
                    if(isOpenBabyScale && null!=data && data.getRweight()>0){
                        if(null==receiveRecod){
                            receiveRecod = data;
                            ToastUtils.ToastCenter(BabyScaleNewActivity.this, getString(R.string.harm_baby_onscale_waring));
                            return ;
                        }else{
                            float weight  = data.getRweight()-receiveRecod.getRweight();
                            //ToastUtils.ToastCenter(BabyScaleNewActivity.this, "接收到抱着婴儿测量的数据了****:"+weight);
                            if(weight>0){
                                //保存 婴体重
                                try {
                                    AppData.hasCheckData=true;
                                    if (!BluetoolUtil.bleflag)
                                        TimeService.setIsdoing(true);
                                    else
                                        BlueSingleton.setIsdoing(true);

                                    data.setRweight(weight);
                                    data.setSweight(String.valueOf(weight));
                                    data.setScaleType(UtilConstants.BABY_SCALE);
                                    //更新界面
                                    if(null!=lastRecord){
                                        float compare = data.getRweight() - lastRecord.getRweight();
                                        data.setCompareRecord((UtilTooth.myround(compare)) + "");
                                        Log.e(TAG, "婴儿车前后重量相差:"+ compare);
                                        if(Math.abs(compare)>=0.2){
                                            //替换当前页面最后的测量记录
                                            lastRecord = data;
                                            askForSaveExceptionData();
                                            return ;
                                        }
                                    }else{
                                        data.setCompareRecord((UtilTooth.myround(weight)) + "");
                                    }
                                    //替换当前页面最后的测量记录
                                    lastRecord = data;
                                    //通知界面更新
                                    Message message=initHandler.obtainMessage(1);
                                    message.obj=data;
                                    initHandler.sendMessage(message);
                                    //保存记录
                                    RecordDao.handHarmBabyData(recordService,data,babyUser);

                                    //重置
                                    receiveRecod = null;
                                    //lastRecord = null;
                                    isOpenBabyScale = false;
                                } catch (Exception e) {
                                    Log.e(TAG, "保存用户测量数据异常"+e.getMessage());
                                }
                            }else{
                                //测量有误，抱婴后还比之前轻了
                                Toast.makeText(BabyScaleNewActivity.this, getString(R.string.mesure_error_harmbaby), Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    break;

            }
        }

    };


    /**
     * 询问异常数据是否保存
     */
    protected void askForSaveExceptionData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BabyScaleNewActivity.this);
        builder.setMessage(getString(R.string.receive_data_waring));
        builder.setNegativeButton(R.string.cancle_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //启动 选择
                startActivityForResult(BabyChoiceForDataActivity.creatIntent(BabyScaleNewActivity.this,receiveRecod),103);
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(R.string.ok_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //通知界面更新
                Message message=initHandler.obtainMessage(1);
                message.obj=lastRecord;
                initHandler.sendMessage(message);
                //保存记录
                RecordDao.handHarmBabyData(recordService,lastRecord,babyUser);

                //重置
                receiveRecod = null;
                //lastRecord = null;
                isOpenBabyScale = false;
                dialog.dismiss();

            }
        });
        builder.create().show();
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

}