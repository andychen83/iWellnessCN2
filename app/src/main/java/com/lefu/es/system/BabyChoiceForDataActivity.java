package com.lefu.es.system;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;

import com.lefu.es.adapter.AdultGalleryAdapter;
import com.lefu.es.adapter.BabyGalleryAdapter;
import com.lefu.es.entity.Records;
import com.lefu.es.entity.UserModel;
import com.lefu.es.service.UserService;
import com.lefu.iwellness.newes.cn.system.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BabyChoiceForDataActivity extends Activity {

    @Bind(R.id.id_recyclerview)
    RecyclerView mRecyclerView;

    BabyGalleryAdapter mAdapter;
    List<UserModel> mDatas;

    private UserService uservice;

    Records records = null;

    public static Intent creatIntent(Context context, Records records){
        Intent intent = new Intent(context,BabyChoiceForDataActivity.class);
        intent.putExtra("record",records);
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
        Serializable serializable = getIntent().getSerializableExtra("record");
        if(null!=serializable){
            records = (Records)serializable;
        }
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
                    Intent intent = new Intent();
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable("user", baby);
                    intent.putExtras(mBundle);
                    setResult(103,intent);
                    BabyChoiceForDataActivity.this.finish();
                }
            });
            mRecyclerView.setAdapter(mAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.addBtn)
    public  void addUser(){
        startActivityForResult(BabyAddActivity.creatIntent(BabyChoiceForDataActivity.this),101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 101) {
            Bundle loginBundle = data.getExtras();
            if(null!=loginBundle){
                Serializable serializable = loginBundle.getSerializable("user");
                if(null!=serializable){
                    UserModel user = (UserModel) serializable;
                    Intent intent = new Intent();
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable("user", user);
                    intent.putExtras(mBundle);
                    setResult(103,intent);
                    BabyChoiceForDataActivity.this.finish();
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

}
