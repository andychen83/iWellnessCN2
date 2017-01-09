package com.lefu.es.system;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.lefu.iwellness.newes.cn.system.R;

import butterknife.ButterKnife;

public class BodyFatNewActivity extends BaseBleActivity implements View.OnClickListener {

    private RelativeLayout set;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_fat_new);
        ButterKnife.bind(this);
        set = (RelativeLayout) findViewById(R.id.set);
        set.setOnClickListener(this);
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
