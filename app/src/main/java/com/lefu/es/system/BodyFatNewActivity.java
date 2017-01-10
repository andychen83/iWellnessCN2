package com.lefu.es.system;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lefu.es.blenew.helper.BleHelper1;
import com.lefu.es.constant.UtilConstants;
import com.lefu.es.db.RecordDao;
import com.lefu.es.entity.Records;
import com.lefu.es.service.ExitApplication;
import com.lefu.es.service.UserService;
import com.lefu.es.util.MyUtil;
import com.lefu.es.util.SharedPreferencesUtil;
import com.lefu.es.util.StringUtils;
import com.lefu.es.util.ToastUtils;
import com.lefu.iwellness.newes.cn.system.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BodyFatNewActivity extends BaseBleActivity implements View.OnClickListener {

    private RelativeLayout set;

    @Bind(R.id.bluetooth_status)
    AppCompatTextView bluetoothStatusTx;

    @Bind(R.id.weith_value_tx)
    AppCompatTextView weithValueTx;


    private UserService uservice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_fat_new);
        ButterKnife.bind(this);
        set = (RelativeLayout) findViewById(R.id.set);
        set.setOnClickListener(this);

        uservice = new UserService(this);
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
            if(null!=mDeviceName && mDeviceName.toLowerCase().startsWith("dl")){ //新的DL Scale
                //CF 88 13 00 14 00 00 00 00 00 40
                if(RecordDao.isLockData(readMessage)){
                    dueDate(readMessage,2);
                }else{
                    dueDate(readMessage,3);
                }
            }else{
                /**判断是不是两次连续的数据*/
                if (readMessage.length() > 31 && (System.currentTimeMillis()- UtilConstants.receiveDataTime>1000)) {
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

            weithValueTx.setText(String.valueOf(weight));
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set:
                startActivity(BodyFatScaleSetActivity.creatIntent(BodyFatNewActivity.this));
                break;
            default:
                break;
        }

    }
}
