package com.lefu.es.system;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lefu.iwellness.newes.cn.system.R;
//婴儿秤设置
public class BabySetScaleActivity extends AppCompatActivity {

    public static Intent creatIntent(Context context){
        Intent intent = new Intent(context,BabySetScaleActivity.class);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_set_scale);
    }
}
