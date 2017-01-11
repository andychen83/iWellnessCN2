package com.lefu.es.system;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lefu.iwellness.newes.cn.system.R;

public class TestActivity extends AppCompatActivity {
    private ImageView face_img_weight;
    private LinearLayout face_img_weight_ll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
//        face_img_weight = (ImageView) findViewById(R.id.face_img_weight);
//        face_img_weight.setPadding(50,0,0,0);
        face_img_weight_ll = (LinearLayout) findViewById(R.id.face_img_weight_ll);
        face_img_weight_ll.setPadding(800,0,0,0);
    }
}
