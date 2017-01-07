package com.lefu.es.system;

import android.app.Activity;
import android.os.Bundle;

import com.lefu.iwellness.newes.cn.system.R;

public class BodyFatNewActivity extends BaseBleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_fat_new);
    }

    @Override
    public void updateConnectionState(int resourceId) {
        
    }

    @Override
    public void discoverBleService() {

    }

    @Override
    public void reveiveBleData(String data) {

    }
}
