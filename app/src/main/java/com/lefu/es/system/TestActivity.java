package com.lefu.es.system;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lefu.es.util.MoveView;
import com.lefu.iwellness.newes.cn.system.R;

public class TestActivity extends AppCompatActivity {
    /*体重
    * ---------*/
    private ImageView face_img_weight;
    private LinearLayout face_img_weight_ll;
    private TextView weight_critical_point1;
    private TextView weight_critical_point2;
    private AppCompatTextView biaoz;

    /*水分率
    *-----------------
     */
    private ImageView face_img_moisture;
    private LinearLayout face_img_moisture_ll;
    private TextView moistrue_critical_point1;
    private TextView moistrue_critical_point2;
    private AppCompatTextView biaoz_moistrue;

    /*脂肪率
    *-----------------
     */
    private ImageView face_img_bft;
    private LinearLayout face_img_bft_ll;
    private TextView bft_critical_point1;
    private TextView bft_critical_point2;
    private TextView bft_critical_point3;
    private TextView bft_critical_point4;
    private AppCompatTextView bft_biaoz;

    /*骨量
    *-----------------
     */
    private ImageView face_img_bone;
    private LinearLayout face_img_bone_ll;
    private TextView bone_critical_point1;
    private TextView bone_critical_point2;
    private AppCompatTextView bone_biaoz;

    /*BMI
    *-----------------
     */
    private ImageView face_img_bmi;
    private LinearLayout face_img_bmi_ll;
    private TextView bmi_critical_point1;
    private TextView bmi_critical_point2;
    private TextView bmi_critical_point3;
    private AppCompatTextView bmi_biaoz;

    /*内脏脂肪指数
    *-----------------
     */
    private ImageView face_img_visceral;
    private LinearLayout face_img_visceral_ll;
    private TextView visceral_critical_point1;
    private TextView visceral_critical_point2;
    private AppCompatTextView visceral_biaoz;

    /*BMR基础代谢率
    *-----------------
     */
    private ImageView face_img_bmr;
    private LinearLayout face_img_bmr_ll;
    private TextView bmr_critical_point1;
    private TextView bmr_critical_point2;
    private AppCompatTextView bmr_biaoz;

    /*肌肉率
    *-----------------
     */
    private ImageView face_img_muscle;
    private LinearLayout face_img_muscle_ll;
    private TextView muscle_critical_point1;
    private TextView muscle_critical_point2;
    private AppCompatTextView muscle_biaoz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        face_img_weight_ll = (LinearLayout) findViewById(R.id.face_img_weight_ll);
        weight_critical_point1 = (TextView) findViewById(R.id.weight_critical_point1);
        weight_critical_point2 = (TextView) findViewById(R.id.weight_critical_point2);
        biaoz = (AppCompatTextView) findViewById(R.id.biaoz);
        face_img_weight = (ImageView) findViewById(R.id.face_img_weight);

        // 体重
        MoveView.weight(TestActivity.this,face_img_weight_ll,face_img_weight,weight_critical_point1,weight_critical_point2,biaoz,0,170,199);

        face_img_moisture_ll = (LinearLayout) findViewById(R.id.face_img_moisture_ll);
        moistrue_critical_point1 = (TextView) findViewById(R.id.moistrue_critical_point1);
        moistrue_critical_point2 = (TextView) findViewById(R.id.moistrue_critical_point2);
        biaoz_moistrue = (AppCompatTextView) findViewById(R.id.biaoz_moistrue);
        face_img_moisture = (ImageView) findViewById(R.id.face_img_moisture);
        // 水分率
        MoveView.moisture(TestActivity.this,face_img_moisture_ll,face_img_moisture,moistrue_critical_point1,moistrue_critical_point2,biaoz_moistrue,0,59);

        face_img_bft_ll = (LinearLayout) findViewById(R.id.face_img_bft_ll);
        bft_critical_point1 = (TextView) findViewById(R.id.bft_critical_point1);
        bft_critical_point2 = (TextView) findViewById(R.id.bft_critical_point2);
        bft_critical_point3 = (TextView) findViewById(R.id.bft_critical_point3);
        bft_critical_point4 = (TextView) findViewById(R.id.bft_critical_point4);
        bft_biaoz = (AppCompatTextView) findViewById(R.id.bft_biaoz);
        face_img_bft = (ImageView) findViewById(R.id.face_img_bft);
        // 脂肪率
        MoveView.bft(TestActivity.this,face_img_bft_ll,face_img_bft,bft_critical_point1,bft_critical_point2,bft_critical_point3,bft_critical_point4,bft_biaoz,1,28,10);


        face_img_bone_ll = (LinearLayout) findViewById(R.id.face_img_bone_ll);
        bone_critical_point1 = (TextView) findViewById(R.id.bone_critical_point1);
        bone_critical_point2 = (TextView) findViewById(R.id.bone_critical_point2);
        bone_biaoz = (AppCompatTextView) findViewById(R.id.bone_biaoz);
        face_img_bone = (ImageView) findViewById(R.id.face_img_bone);
        // 骨量
        MoveView.bone(TestActivity.this,face_img_bone_ll,face_img_bone,bone_critical_point1,bone_critical_point2,bone_biaoz,2.7);


        face_img_bmi_ll = (LinearLayout) findViewById(R.id.face_img_bmi_ll);
        bmi_critical_point1 = (TextView) findViewById(R.id.bmi_critical_point1);
        bmi_critical_point2 = (TextView) findViewById(R.id.bmi_critical_point2);
        bmi_critical_point3 = (TextView) findViewById(R.id.bmi_critical_point3);
        bmi_biaoz = (AppCompatTextView) findViewById(R.id.bmi_biaoz);
        face_img_bmi = (ImageView) findViewById(R.id.face_img_bmi);
        // BMI
        MoveView.bmi(TestActivity.this,face_img_bmi_ll,face_img_bmi,bmi_critical_point1,bmi_critical_point2,bmi_critical_point3,bmi_biaoz,18.5);


        face_img_visceral_ll = (LinearLayout) findViewById(R.id.face_img_visceral_ll);
        visceral_critical_point1 = (TextView) findViewById(R.id.visceral_critical_point1);
        visceral_critical_point2 = (TextView) findViewById(R.id.visceral_critical_point2);
        visceral_biaoz = (AppCompatTextView) findViewById(R.id.visceral_biaoz);
        face_img_visceral = (ImageView) findViewById(R.id.face_img_visceral);
        // 内脏脂肪指数
        MoveView.visceralFat(TestActivity.this,face_img_visceral_ll,face_img_visceral,visceral_critical_point1,visceral_critical_point2,visceral_biaoz,8);


        face_img_bmr_ll = (LinearLayout) findViewById(R.id.face_img_bmr_ll);
        bmr_critical_point1 = (TextView) findViewById(R.id.bmr_critical_point1);
        bmr_biaoz = (AppCompatTextView) findViewById(R.id.bmr_biaoz);
        face_img_bmr = (ImageView) findViewById(R.id.face_img_bmr);
        // BMR 基础代谢率
        MoveView.bmr(TestActivity.this,face_img_bmr_ll,face_img_bmr,bmr_critical_point1,bmr_biaoz,1562);


        face_img_muscle_ll = (LinearLayout) findViewById(R.id.face_img_muscle_ll);
        muscle_critical_point1 = (TextView) findViewById(R.id.muscle_critical_point1);
        muscle_critical_point2 = (TextView) findViewById(R.id.muscle_critical_point2);
        muscle_biaoz = (AppCompatTextView) findViewById(R.id.muscle_biaoz);
        face_img_muscle = (ImageView) findViewById(R.id.face_img_muscle);
        // 肌肉率
        MoveView.muscle(TestActivity.this,face_img_muscle_ll,face_img_muscle,muscle_critical_point1,muscle_critical_point2,muscle_biaoz,90);



    }
}
