package com.lefu.es.util;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

/**
 * 控制笑脸在水平上的偏移
 * Created by lenovo on 2017/1/11.
 */

public class MoveView {


    /**
     * * 体重部分
     *
     * @param activity  当前Activity
     * @param view      笑脸图标
     * @param textView1 从左到右第一个分割线
     * @param textView2 从左到右第二个分割线
     * @param gender    性别：0男，1女
     * @param height    身高
     * @param weight    体重
     */
    public static void weight(Activity activity, View view, TextView textView1, TextView textView2, int gender, int height, int weight) {
        // 标准体重
        double standard_weight;
        // 从左到右第一个分割线
        double critical_point1;
        // 从左到右第二个分割线
        double critical_point2;
        // 笑脸偏移量
        int deviation = 0;
        // 屏幕宽度
        int screenwidth = activity.getWindowManager().getDefaultDisplay().getWidth();
        if (gender == 0) {
            standard_weight = (height - 80) * 0.7;
        } else {
            standard_weight = (height - 70) * 0.6;
        }
        critical_point1 = standard_weight - standard_weight * 0.1;
        critical_point2 = standard_weight + standard_weight * 0.1;
        if (weight < critical_point1) {
            deviation = (int) (screenwidth * 0.25 * (weight / critical_point1));
        } else if (weight >= critical_point1 && weight < critical_point2) {
            deviation = (int) (screenwidth * 0.25 + (weight - critical_point1) / (critical_point2 - critical_point1) * screenwidth * 0.5);
        } else if (weight >= critical_point2) {
            deviation = (int) (screenwidth * 0.75 + (weight - critical_point2) / standard_weight * 0.1 * screenwidth * 0.25);
        }
        textView1.setText(String.valueOf(MyUtil.onePoint(critical_point1)));
        textView2.setText(String.valueOf(MyUtil.onePoint(critical_point2)));

        view.setPadding(deviation-10, 0, 0, 0);
    }
}
