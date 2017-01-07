package com.lefu.es.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.lefu.es.system.AutoBLEActivity;
import com.lefu.iwellness.newes.cn.system.R;

/**
 * 作者: Administrator on 2017/1/7.
 * 作用: 提示信息
 */

public class ToastUtils {

    public static void  ToastCenter(Context context,String msg){
        Toast tost = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        tost.setGravity(Gravity.CENTER, 0, 0);
        tost.show();
    }
}
