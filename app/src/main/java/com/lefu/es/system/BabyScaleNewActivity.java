package com.lefu.es.system;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.lefu.iwellness.newes.cn.system.R;

/*婴儿秤
* */
public class BabyScaleNewActivity extends Activity implements View.OnClickListener {

    private RelativeLayout set;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_scale_new);
        set = (RelativeLayout) findViewById(R.id.set);
        set.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set:
                startActivity(BabySetScaleActivity.creatIntent(BabyScaleNewActivity.this));
                break;
            default:
                break;
        }
    }
}
