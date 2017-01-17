package com.lefu.es.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lefu.es.entity.UserModel;
import com.lefu.iwellness.newes.cn.system.R;

import java.io.File;
import java.util.List;

/**
 * 作者: Administrator on 2017/1/12.
 * 作用:
 */

public class AdultGalleryAdapter extends  RecyclerView.Adapter<AdultGalleryAdapter.ViewHolder> {
    private Context context;

    public interface OnItemClickLitener
    {
        void onItemClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    private LayoutInflater mInflater;
    private List<UserModel> mDatas;

    public AdultGalleryAdapter(Context context, List<UserModel> datats)
    {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        mDatas = datats;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public ViewHolder(View arg0)
        {
            super(arg0);
        }

        SimpleDraweeView mImg;
        TextView mTxt;
    }

    @Override
    public int getItemCount()
    {
        return mDatas.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View view = mInflater.inflate(R.layout.item_adult_select,
                viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.mImg = (SimpleDraweeView) view
                .findViewById(R.id.id_index_gallery_item_image);
        viewHolder.mTxt = (TextView) view
                .findViewById(R.id.id_index_gallery_item_text);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i)
    {
        if(TextUtils.isEmpty(mDatas.get(i).getPer_photo())){

        }else{
            //加载本地图片
            viewHolder.mImg.setImageURI(Uri.fromFile(new File(mDatas.get(i).getPer_photo())));
        }
        //viewHolder.mImg.setImageResource(mDatas.get(i));
        viewHolder.mTxt.setText(mDatas.get(i).getUserName());
        //如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null)
        {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mOnItemClickLitener.onItemClick(viewHolder.itemView, i);
                }
            });

        }

    }
}
