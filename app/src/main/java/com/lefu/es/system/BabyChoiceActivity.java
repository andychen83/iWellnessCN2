package com.lefu.es.system;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.lefu.es.adapter.BabyGalleryAdapter;
import com.lefu.es.entity.UserModel;
import com.lefu.es.service.UserService;
import com.lefu.iwellness.newes.cn.system.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BabyChoiceActivity extends Activity {

    @Bind(R.id.id_recyclerview)
     RecyclerView mRecyclerView;

     BabyGalleryAdapter mAdapter;
     List<UserModel> mDatas;

    private UserService uservice;

    public static Intent creatIntent(Context context){
        Intent intent = new Intent(context,BabyChoiceActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_baby_choice);
        ButterKnife.bind(this);
        uservice = new UserService(this);

        //设置布局管理器
        GridLayoutManager layoutManager=new GridLayoutManager(this,3);
        mRecyclerView.setLayoutManager(layoutManager);

        initAdapter();
    }


    public void initAdapter(){
        try {
            mDatas = uservice.getAllBabys();
            if(null==mDatas){
                mDatas = new ArrayList<>();
            }
            //设置适配器
            mAdapter = new BabyGalleryAdapter(this, mDatas);
            mAdapter.setOnItemClickLitener(new BabyGalleryAdapter.OnItemClickLitener()
            {
                @Override
                public void onItemClick(View view, int position)
                {
                    UserModel baby = mDatas.get(position);
                    startActivity(BabyScaleNewActivity.creatIntent(BabyChoiceActivity.this,baby));
                    BabyChoiceActivity.this.finish();
                }
            });
            mRecyclerView.setAdapter(mAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.addBtn)
    public  void addUser(){
        Intent intent = new Intent();
        intent.setClass(BabyChoiceActivity.this, BabyAddActivity.class);
        BabyChoiceActivity.this.startActivity(intent);
        this.finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

}
