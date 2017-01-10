package com.lefu.es.system;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.lefu.es.constant.UtilConstants;
import com.lefu.iwellness.newes.cn.system.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*婴儿秤
* */
public class BabyScaleNewActivity extends Activity {

    @Bind(R.id.setting_menu)
     RelativeLayout set;

    @Bind(R.id.harmbaby_menu)
     RelativeLayout up_scale;

    private Dialog dialog;
    View view;

    public static Intent creatIntent(Context context){
        Intent intent = new Intent(context,BabyScaleNewActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_scale_new);
        ButterKnife.bind(this);

        view = LayoutInflater.from(BabyScaleNewActivity.this).inflate(R.layout.baby_dialog_gridview, null);
        dialog = new Dialog(BabyScaleNewActivity.this);
        dialog.setContentView(view);
        dialog.setTitle("请选择");
        GridView gridview = (GridView) view.findViewById(R.id.gview);
        BabyGirdViewAdpter adpter = new BabyGirdViewAdpter(getLayoutInflater(),getData());
        gridview.setAdapter(adpter);

        // 添加点击事件
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Toast.makeText(getApplicationContext(), "你选择了：" + getData().get(arg2).getName(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    @OnClick(R.id.history_menu)
    public void  historyMenuClick(){
        Intent intent = new Intent();
        intent.setClass(BabyScaleNewActivity.this, RecordListActivity.class);
        intent.putExtra("type", UtilConstants.WEIGHT_SINGLE);
        intent.putExtra("id", 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityForResult(intent, 0);
    }

    @OnClick(R.id.setting_menu)
    public void setMenuClick(){
        startActivity(BodyFatScaleSetActivity.creatIntent(BabyScaleNewActivity.this));
    }


    /**
     * 将数据ArrayList中
     *
     * @return
     */
    private ArrayList<Baby> getData() {
        ArrayList<Baby> items = new ArrayList<Baby>();
        for (int i = 0; i < 7; i++) {
            Baby baby = new Baby();
            baby.setName("baby"+i);
            items.add(baby);
        }
        return items;
    }

    class Baby{
        public String headUrl;
        public String name;
        public void setHeadUrl(String headUrl){
            this.headUrl = headUrl;
        }
        public String getHeadUrl(){
            return headUrl;
        }
        public void setName(String name){
            this.name = name;
        }
        public String getName(){
            return name;
        }
    }

    class BabyGirdViewAdpter extends BaseAdapter{
        private LayoutInflater inflater;
        private List<Baby> list;

        public BabyGirdViewAdpter(LayoutInflater inflater, ArrayList<Baby> list) {
            this.inflater = inflater;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Baby baby =list.get(position);
            View view;
            ViewHolder viewHolder;
            if(convertView==null){
                view=inflater.inflate(R.layout.selet_baby, null);
                viewHolder=new ViewHolder();
                viewHolder.image=(ImageView) view.findViewById(R.id.imageview);
                viewHolder.name=(TextView) view.findViewById(R.id.textview);
                view.setTag(viewHolder);
            }else{
                view=convertView;
                viewHolder=(ViewHolder) view.getTag();
            }
//            viewHolder.image.setImageResource();
            viewHolder.image.setImageDrawable(getDrawable(R.drawable.baby));
            viewHolder.name.setText(baby.getName());
            return view;
        }

    }
    class ViewHolder{
        ImageView image;
        TextView name;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

}