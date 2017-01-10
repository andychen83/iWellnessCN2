package com.lefu.es.system;

import android.app.Activity;
import android.app.Dialog;
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

import com.lefu.iwellness.newes.cn.system.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*婴儿秤
* */
public class BabyScaleNewActivity extends Activity implements View.OnClickListener {

    private RelativeLayout set;
    private RelativeLayout up_scale;
    private Dialog dialog;
    View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_scale_new);
        set = (RelativeLayout) findViewById(R.id.set);
        up_scale = (RelativeLayout) findViewById(R.id.up_scale);
        set.setOnClickListener(this);
        up_scale.setOnClickListener(this);


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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set:
                startActivity(BabySetScaleActivity.creatIntent(BabyScaleNewActivity.this));
                break;
            case R.id.up_scale:
                dialog.show();
                break;
            default:
                break;
        }
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
}