package com.lefu.es.system;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;
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

import com.lefu.es.ble.BlueSingleton;
import com.lefu.es.blenew.helper.BleHelper1;
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
import com.lefu.es.util.MyUtil;
import com.lefu.es.util.SharedPreferencesUtil;
import com.lefu.es.util.StringUtils;
import com.lefu.es.util.ToastUtils;
import com.lefu.iwellness.newes.cn.system.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    AppCompatTextView weithValueTx;

    @Bind(R.id.user_name)
    AppCompatTextView userNameTx;

    private   boolean isOpenBabyScale = false;//是否是上秤模式

    protected UserModel babyUser = null; //选择的婴儿



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
        }

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
        Intent intent = new Intent();
        intent.setClass(BabyScaleNewActivity.this, RecordListActivity.class);
        intent.putExtra("type", UtilConstants.WEIGHT_SINGLE);
        intent.putExtra("id", 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityForResult(intent, 0);
    }

    @OnClick(R.id.harmbaby_menu)
    public void  upScaleClick(){
        this.isOpenBabyScale = true;
    }

    @OnClick(R.id.setting_menu)
    public void setMenuClick(){
        startActivity(BodyFatScaleSetActivity.creatIntent(BabyScaleNewActivity.this));
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
        ToastUtils.ToastCenter(BabyScaleNewActivity.this, getString(R.string.scale_paired_success));
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
        if(TextUtils.isEmpty(readMessage)) return;
        if(!isOpenBabyScale) {
            Toast.makeText(BabyScaleNewActivity.this, getString(R.string.open_harmbaby_scale), Toast.LENGTH_LONG).show();
            return;
        }
        //测脂错误
        if (readMessage.equals(UtilConstants.ERROR_CODE)) {
            if(UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_ST) || UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_LB)){
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
                            if(UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_ST) || UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_LB)){
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

    /**
     * 数据处理
     * @param readMessage
     * @param i
     */
    private void dueDate(String readMessage, int i) {
        if(0==i){//旧秤
            if(null==receiveRecod){
                receiveRecod = MyUtil.parseMeaageForBaby(this.recordService, readMessage);
            }else{
                secondRecod = MyUtil.parseMeaageForBaby(this.recordService, readMessage);
            }
        }else if(1==i){//阿里秤
            if(null==receiveRecod){
                receiveRecod = MyUtil.parseZuKangMeaage(this.recordService, readMessage,babyUser);
            }else{
                secondRecod = MyUtil.parseZuKangMeaage(this.recordService, readMessage,babyUser);
            }
        }else if(2==i){//新称过程数据
            float weight = MyUtil.getWeightData(readMessage);
            weithValueTx.setText(String.valueOf(weight));
        }else if(3==i){//新秤锁定数据
            if(null==receiveRecod){
                receiveRecod = MyUtil.parseDLScaleMeaage(this.recordService, readMessage,babyUser);
            }else{
                secondRecod = MyUtil.parseDLScaleMeaage(this.recordService, readMessage,babyUser);
            }

        }
        Message msg1 = handler.obtainMessage(0);
        handler.sendMessage(msg1);
    }

    /**
     * 锁定数据显示
     * @param data
     */
    private  void localData(Records data){
        weithValueTx.setText(String.valueOf(data.getRweight()));
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0 :
                    Records data  = (Records)msg.obj;
                    if(null!=receiveRecod && null!=secondRecod){
                      float weight  = secondRecod.getRweight()-receiveRecod.getRweight();
                      if(weight>0){
                          //保存 婴体重
                          try {
                              AppData.hasCheckData=true;
                              if (!BluetoolUtil.bleflag)
                                  TimeService.setIsdoing(true);
                              else
                                  BlueSingleton.setIsdoing(true);

                              secondRecod.setRweight(weight);
                              secondRecod.setSweight(String.valueOf(weight));
                              RecordDao.handHarmBabyData(recordService,secondRecod,babyUser);

                          } catch (Exception e) {
                              Log.e(TAG, "保存用户测量数据异常"+e.getMessage());
                          }
                      }else{
                          //测量有误，抱婴后还比之前轻了
                          Toast.makeText(BabyScaleNewActivity.this, getString(R.string.mesure_error_harmbaby), Toast.LENGTH_LONG).show();
                      }
                    }
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