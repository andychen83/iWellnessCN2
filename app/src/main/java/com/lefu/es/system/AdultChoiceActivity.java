package com.lefu.es.system;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.lefu.es.adapter.BabyGalleryAdapter;
import com.lefu.es.entity.UserModel;
import com.lefu.es.service.UserService;
import com.lefu.iwellness.newes.cn.system.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AdultChoiceActivity extends Activity {

    @Bind(R.id.id_recyclerview)
     RecyclerView mRecyclerView;

    @Bind(R.id.addBtn)
    TextView addBtn;

     BabyGalleryAdapter mAdapter;
     List<UserModel> mDatas;

    private UserService uservice;

    public static Intent creatIntent(Context context){
        Intent intent = new Intent(context,AdultChoiceActivity.class);
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
        addBtn.setVisibility(View.GONE);
        initAdapter();
    }


    public void initAdapter(){
        try {
            mDatas = uservice.getAllUserByScaleType();
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
                    Intent intent=new Intent();
                    Bundle bundle=new  Bundle();
                    bundle.putSerializable("user",baby);
                    intent.putExtras(bundle);
                    setResult(BodyFatNewActivity.SELCET_USER,intent);
                    AdultChoiceActivity.this.finish();
                }
            });
            mRecyclerView.setAdapter(mAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @OnClick(R.id.addBtn)
//    public  void addUser(){
//        startActivity(BabyAddActivity.creatIntent(getApplicationContext()));
//        this.finish();
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

}
