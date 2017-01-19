package com.lefu.es.system;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lefu.es.ble.BlueSingleton;
import com.lefu.es.blenew.constant.BluetoolUtil1;
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
import com.lefu.es.service.RecordService;
import com.lefu.es.service.TimeService;
import com.lefu.es.service.UserService;
import com.lefu.es.util.MoveView;
import com.lefu.es.util.MyUtil;
import com.lefu.es.util.SharedPreferencesUtil;
import com.lefu.es.util.StringUtils;
import com.lefu.es.util.ToastUtils;
import com.lefu.es.util.UtilTooth;
import com.lefu.es.view.MyTextView5;
import com.lefu.iwellness.newes.cn.system.R;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.lefu.iwellness.newes.cn.system.R.id.cancle_datacbtn;
import static com.lefu.iwellness.newes.cn.system.R.id.save_databtn;

/*人体秤
* */
public class BodyScaleNewActivity extends BaseBleActivity {


    @Bind(R.id.bluetooth_status)
    AppCompatTextView bluetoothStatusTx;

    @Bind(R.id.user_header)
    SimpleDraweeView userHeadImg;

    @Bind(R.id.user_name)
    AppCompatTextView userNameTx;

    @Bind(R.id.weith_value_tx)
    MyTextView5 weithValueTx;

    @Bind(R.id.weith_status)
    TextView weithStatus;

    @Bind(R.id.unti_tv)
    TextView unit_tv;

    private UserService uservice;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_scale_new);
        ButterKnife.bind(this);
        uservice = new UserService(this);

        initView();
    }



    private void initView() {
        UtilConstants.SELECT_SCALE = UtilConstants.BATHROOM_SCALE;
        if(null!=UtilConstants.CURRENT_USER){
            userNameTx.setText(UtilConstants.CURRENT_USER.getUserName());
            if(!TextUtils.isEmpty(UtilConstants.CURRENT_USER.getPer_photo())){
                userHeadImg.setImageURI(Uri.fromFile(new File(UtilConstants.CURRENT_USER.getPer_photo())));
            }
            try {
                Records lastRecords = recordService.findLastRecords(UtilConstants.CURRENT_USER.getId(),"ce");
                if(null!=lastRecords){
                    localData(lastRecords,UtilConstants.CURRENT_USER);
                    initBodyBar(UtilConstants.CURRENT_USER,lastRecords);
                }
            } catch (Exception e) {
                e.printStackTrace();
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
            MoveView.weight(BodyScaleNewActivity.this,face_img_weight_ll,face_img_weight,weight_critical_point1,weight_critical_point2,biaoz,gender,user.getBheigth(),record.getRweight(),user.getDanwei());

            // BMI
            bmiIndex.setText(UtilTooth.keep1Point(record.getRbmi()));
            MoveView.bmi(BodyScaleNewActivity.this,face_img_bmi_ll,face_img_bmi,bmi_critical_point1,bmi_critical_point2,bmi_critical_point3,bmi_biaoz,record.getRbmi());

        }
    }

    List<UserModel> babys = null;
    @OnClick(R.id.harmbaby_menu)
    public void harmBabyMenuClick(){
        try {
            if(null==babys || babys.size()==0) babys=uservice.getAllBabys();

            if(null==babys || babys.size()==0){
                //添加一个用户组
                startActivity(BabyAddActivity.creatIntent(BodyScaleNewActivity.this,null));
            }else{
                //弹出选择用户组
                startActivity(BabyChoiceActivity.creatIntent(BodyScaleNewActivity.this));
            }
        } catch (Exception e) {
            Log.e(TAG,"脂肪秤页面点击抱婴按钮异常==>"+e.getMessage());
        }
    }

    @OnClick(R.id.setting_menu)
    public void setMenuClick(){
        startActivity(SettingActivity.creatIntent(BodyScaleNewActivity.this));
    }

    @OnClick(R.id.history_menu)
    public void  historyMenuClick(){
        if(null==UtilConstants.CURRENT_USER){
            //调到用户列表选择页面
            Intent intent1 = new Intent();
            intent1.setClass(BodyScaleNewActivity.this, UserListActivity.class);
            BodyScaleNewActivity.this.startActivity(intent1);
        }else{
            startActivityForResult(RecordListActivity.creatIntent(BodyScaleNewActivity.this,UtilConstants.CURRENT_USER),0);
        }
    }

    @OnClick(R.id.user_header)
    public void  userHeaderClick(){
        //调到用户列表选择页面
        Intent intent1 = new Intent();
        intent1.setClass(BodyScaleNewActivity.this, UserListActivity.class);
        BodyScaleNewActivity.this.startActivity(intent1);
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
        ToastUtils.ToastCenter(BodyScaleNewActivity.this, getString(R.string.scale_paired_success));
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
        if(TextUtils.isEmpty(readMessage) || readMessage.length()<10 || !mActivty) return;
        //测脂错误
        if (readMessage.equals(UtilConstants.ERROR_CODE)) {
            if(UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_ST) || UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_LB)){
                Toast.makeText(BodyScaleNewActivity.this, getString(R.string.user_data_error), Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(BodyScaleNewActivity.this, getString(R.string.user_data_error_lb), Toast.LENGTH_LONG).show();
            }
            return;
        } else if (readMessage.equals(UtilConstants.ERROR_CODE_TEST)) {
            Toast.makeText(BodyScaleNewActivity.this, getString(R.string.scale_measurement_error), Toast.LENGTH_LONG).show();
            return;
        }
        //秤和人体参数不匹配
        if (readMessage.startsWith(UtilConstants.BABY_SCALE)) {
            if (UtilConstants.CURRENT_USER.getAgeYear() < 1 || UtilConstants.CURRENT_USER.getBheigth()<30) {
                if(UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_KG)){
                    Toast.makeText(BodyScaleNewActivity.this, getString(R.string.age_error_5), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(BodyScaleNewActivity.this, getString(R.string.age_error_7), Toast.LENGTH_SHORT).show();
                }

                return;
            }
        }else{
            if (UtilConstants.CURRENT_USER.getAgeYear() < 10 || UtilConstants.CURRENT_USER.getBheigth()<100) {
                if(UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_KG)){
                    Toast.makeText(BodyScaleNewActivity.this, getString(R.string.age_error_4), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(BodyScaleNewActivity.this, getString(R.string.age_error_6), Toast.LENGTH_SHORT).show();
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
                    if ((System.currentTimeMillis()- UtilConstants.receiveDataTime>1500) && null==receiveDataDialog) {
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
							/*脂肪秤*/
                        UtilConstants.CURRENT_SCALE=UtilConstants.BODY_SCALE;
                        UtilConstants.CURRENT_USER.setScaleType(UtilConstants.CURRENT_SCALE);
                        dueDate(readMessage,1);
                    }else{
                        if (readMessage.equals(UtilConstants.ERROR_CODE)) {
                            if(UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_ST) || UtilConstants.CURRENT_USER.getDanwei().equals(UtilConstants.UNIT_LB)){
                                Toast.makeText(BodyScaleNewActivity.this, getString(R.string.user_data_error), Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(BodyScaleNewActivity.this, getString(R.string.user_data_error_lb), Toast.LENGTH_LONG).show();
                            }
                            return;
                        } else if (readMessage.equals(UtilConstants.ERROR_CODE_TEST)) {
                            Toast.makeText(BodyScaleNewActivity.this, getString(R.string.scale_measurement_error), Toast.LENGTH_LONG).show();
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
        if(records.getScaleType().startsWith(UtilConstants.BODY_SCALE)){
									/*脂肪秤*/
            UtilConstants.CURRENT_SCALE=UtilConstants.BODY_SCALE;
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
            MyUtil.setProcessWeightData(readMessage,weithValueTx);
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
                        weithValueTx.setTexts(UtilTooth.keep1Point(data.getRweight()),null);
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
                    ExitApplication.getInstance().exit(BodyScaleNewActivity.this);
                    Intent intent = new Intent();
                    intent.setClass(BodyScaleNewActivity.this, LoadingActivity.class);
                    BodyScaleNewActivity.this.startActivity(intent);
                    break;

            }
        }

    };

    protected Dialog receiveDataDialog;
    private Button cancleBtn,saveBtn;
    /**
     * 接收到数据提示
     */
    protected void showReceiveDataDialog() {
        // 初始化自定义布局参数
        LayoutInflater layoutInflater = getLayoutInflater();
        // 为了能在下面的OnClickListener中获取布局上组件的数据，必须定义为final类型.
        View customLayout = layoutInflater.inflate(R.layout.activity_receive_alert, (ViewGroup) findViewById(R.id.receiveDataDialog));

        cancleBtn = (Button) customLayout.findViewById(R.id.cancle_datacbtn);
        saveBtn = (Button) customLayout.findViewById(save_databtn);

        cancleBtn.setOnClickListener(imgOnClickListener);
        saveBtn.setOnClickListener(imgOnClickListener);

        receiveDataDialog = new Dialog(this,R.style.dialog);
        receiveDataDialog.setContentView(customLayout);
        receiveDataDialog .show();

        Window window = receiveDataDialog.getWindow();
        window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.mystyle); // 添加动画
    }

    View.OnClickListener imgOnClickListener = new View.OnClickListener() {
        @SuppressWarnings("deprecation")
        public void onClick(View v) {
            switch (v.getId()) {
                case cancle_datacbtn:
                    if(null!=receiveDataDialog)receiveDataDialog.dismiss();
                    receiveDataDialog = null;
                    break;
                case save_databtn:
                    try {
                        AppData.hasCheckData=true;
                        if (!BluetoolUtil.bleflag)
                            TimeService.setIsdoing(true);
                        else
                            BlueSingleton.setIsdoing(true);
                        if (null != receiveRecod && null != receiveRecod.getScaleType()) {
                            float compare = 0;
                            if(!TextUtils.isEmpty(receiveRecod.getCompareRecord()) && !"null".equals(receiveRecod.getCompareRecord())){
                                compare = Float.parseFloat(receiveRecod.getCompareRecord());
                            }
                            if(Math.abs(compare)>=3){
                                askForSaveExceptionData();
                                //dialogForBodyScale(getString(R.string.receive_data_waring), v.getId());
                                if(null!=receiveDataDialog)receiveDataDialog.dismiss();
                                receiveDataDialog = null;
                            }else{
                                if (UtilConstants.KITCHEN_SCALE.equals(UtilConstants.CURRENT_SCALE)) {
                                    NutrientBo nutrient = null;
                                    if(!TextUtils.isEmpty(receiveRecod.getRphoto())){
                                        nutrient = CacheHelper.queryNutrientsByName(receiveRecod.getRphoto());
                                    }
                                    RecordDao.dueKitchenDate2(recordService, receiveRecod,nutrient);
                                } else {
                                    RecordDao.handleData2(recordService, receiveRecod);
                                }
                                saveDataCallBack(receiveRecod);
                                if (!BluetoolUtil.bleflag){
                                    TimeService.setIsdoing(false);
                                }else{
                                    BlueSingleton.setIsdoing(false);
                                }
                                if(null!=receiveDataDialog)receiveDataDialog.dismiss();
                                receiveDataDialog = null;
                                receiveRecod = null;
                            }
                        }

                    } catch (Exception e) {
                        Log.e(TAG, "保存用户测量数据异常"+e.getMessage());
                    }
                    break;
            }
        }
    };

    /**
     * 询问异常数据是否保存
     */
    protected void askForSaveExceptionData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BodyScaleNewActivity.this);
        builder.setMessage(getString(R.string.receive_data_waring));
        builder.setNegativeButton(R.string.cancle_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //启动成人用户选择
                startActivityForResult(UserChoiceActivity.creatIntent(BodyScaleNewActivity.this,receiveRecod),103);
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(R.string.ok_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RecordDao.handleData2(recordService, receiveRecod);
                initView();
                dialog.dismiss();
                receiveRecod = null;
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 103) {
            Bundle loginBundle = data.getExtras();
            if(null!=loginBundle){
                Serializable serializable = loginBundle.getSerializable("user");
                if(null!=serializable){
                    UserModel user = (UserModel) serializable;
                    //保存用户
                    UtilConstants.CURRENT_USER = user;
                    UtilConstants.SELECT_USER=UtilConstants.CURRENT_USER.getId();
                    System.out.println("当前用户称类型："+UtilConstants.CURRENT_SCALE);
                    if (null != UtilConstants.CURRENT_USER && null != UtilConstants.CURRENT_USER.getDanwei() && !"".equals(UtilConstants.CURRENT_USER.getDanwei())) {
                        if (null != UtilConstants.su) {
                            UtilConstants.su.editSharedPreferences("lefuconfig", "unit", UtilConstants.CURRENT_USER.getDanwei());
                            UtilConstants.su.editSharedPreferences("lefuconfig", "user", UtilConstants.CURRENT_USER.getId());
                        }
                    }
                    RecordDao.handleData2(recordService, receiveRecod);
                    initView();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.weight_jiantou)
    public void menuWeightOpenClick(){
        if(status_bar2.getVisibility()== View.VISIBLE){
            status_bar2.setVisibility(View.GONE);
            weight_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }else{
            status_bar2.setVisibility(View.VISIBLE);
            weight_jiantou.setBackground(getDrawable(R.drawable.up_arrow));
        }

        if(status_bar_bmi.getVisibility()==View.VISIBLE){
            status_bar_bmi.setVisibility(View.GONE);
            bmi_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
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

        if(status_bar2.getVisibility()==View.VISIBLE){
            status_bar2.setVisibility(View.GONE);
            weight_jiantou.setBackground(getDrawable(R.drawable.down_arrow));
        }
    }
}
