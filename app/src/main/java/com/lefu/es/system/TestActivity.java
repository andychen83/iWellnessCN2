package com.lefu.es.system;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lefu.es.util.MoveView;
import com.lefu.iwellness.newes.cn.system.R;

public class TestActivity extends AppCompatActivity {
    private ImageView face_img_weight;
    private LinearLayout face_img_weight_ll;
    private TextView weight_critical_point1;
    private TextView weight_critical_point2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        face_img_weight_ll = (LinearLayout) findViewById(R.id.face_img_weight_ll);
        weight_critical_point1 = (TextView) findViewById(R.id.weight_critical_point1);
        weight_critical_point2 = (TextView) findViewById(R.id.weight_critical_point2);

//        face_img_weight_ll.setPadding(800,0,0,0);
        //(Activity activity, View view, TextView textView1, TextView textView2, int gender, int height, int weight)
        MoveView.weight(TestActivity.this,face_img_weight_ll,weight_critical_point1,weight_critical_point2,0,170,56);
    }
}
