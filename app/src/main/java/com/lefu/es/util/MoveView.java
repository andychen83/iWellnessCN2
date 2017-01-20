package com.lefu.es.util;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lefu.es.constant.UtilConstants;
import com.lefu.iwellness.newes.cn.system.R;

import static com.lefu.iwellness.newes.cn.system.R.id.bft_biaoz;
import static com.lefu.iwellness.newes.cn.system.R.id.textView1;
import static com.lefu.iwellness.newes.cn.system.R.id.textView2;
import static com.lefu.iwellness.newes.cn.system.R.id.textView3;

/**
 * 控制笑脸在水平上的偏移
 * Created by lenovo on 2017/1/11.
 */

public class MoveView {


    /**
     * * 体重部分
     *
     * @param activity  当前Activity
     * @param view      笑脸图标父级
     * @param imageView 笑脸图标
     * @param textView1 从左到右第一个分割线
     * @param textView2 从左到右第二个分割线
     * @param textView3 是否标准
     * @param gender    性别：0男，1女
     * @param height    身高
     * @param weight    体重
     */
    public static void weight(Activity activity, View view, ImageView imageView, TextView textView1, TextView textView2, TextView textView3, int gender, float height, float weight,String danwei) {
        // 标准体重
        float standard_weight;
        // 从左到右第一个分割线
        float critical_point1;
        // 从左到右第二个分割线
        float critical_point2;
        // 终点分割线
        float critical_point3;
        // 笑脸偏移量
        int deviation = 0;
        // 屏幕宽度
        int screenwidth = activity.getWindowManager().getDefaultDisplay().getWidth();
        if (gender == 1) {
            standard_weight = (height - 80) * 0.7f;
        } else {
            standard_weight = (height - 70) * 0.6f;
        }
        critical_point1 = standard_weight - standard_weight * 0.1f;
        critical_point2 = standard_weight + standard_weight * 0.1f;
        critical_point3 = standard_weight + standard_weight * 0.2f;

        if (weight < critical_point1) {
            deviation = (int) (screenwidth * 0.25 * (weight / critical_point1));
            imageView.setImageDrawable(activity.getDrawable(R.drawable.sunface));
            textView3.setText("偏低");
            textView3.setBackground(activity.getDrawable(R.drawable.orange_bg));
        } else if (weight >= critical_point1 && weight < critical_point2) {
            imageView.setImageDrawable(activity.getDrawable(R.drawable.sunface));
            deviation = (int) (screenwidth * 0.25 + (weight - critical_point1) / (critical_point2 - critical_point1) * screenwidth * 0.5);
            textView3.setText("标准");
            textView3.setBackground(activity.getDrawable(R.drawable.grade_bg));
        } else if (weight >= critical_point2) {
            imageView.setImageDrawable(activity.getDrawable(R.drawable.sunface));
            deviation = (int) (screenwidth * 0.75 + (weight - critical_point2) / critical_point3 * screenwidth * 0.25);
            textView3.setText("偏高");
            textView3.setBackground(activity.getDrawable(R.drawable.red_color_bg));
        }
        if (danwei.equals(UtilConstants.UNIT_LB) || danwei.equals(UtilConstants.UNIT_FATLB) || danwei.equals(UtilConstants.UNIT_ST)){
            textView1.setText(UtilTooth.kgToLB_ForFatScale(critical_point1) + "lb");
            textView2.setText(UtilTooth.kgToLB_ForFatScale(critical_point2) + "lb");
        }else{
            textView1.setText(MyUtil.onePoint(critical_point1) + "kg");
            textView2.setText(MyUtil.onePoint(critical_point2) + "kg");
        }

        int margin = 0;
        if(weight!=0){
            margin = deviation - 50;
            if (margin > screenwidth - 140) {
                margin = screenwidth - 140;
            }
        }
        view.setPadding(margin, 0, 0, 0);
    }

    public static String weightString(int gender, float height, float weight) {
        // 标准体重
        float standard_weight;
        // 从左到右第一个分割线
        float critical_point1;
        // 从左到右第二个分割线
        float critical_point2;
        // 终点分割线
        //float critical_point3;

        if (gender == 1) {
            standard_weight = (height - 80) * 0.7f;
        } else {
            standard_weight = (height - 70) * 0.6f;
        }
        critical_point1 = standard_weight - standard_weight * 0.1f;
        critical_point2 = standard_weight + standard_weight * 0.1f;
        //critical_point3 = standard_weight + standard_weight * 0.2f;

        if (weight < critical_point1) {
            return "偏低";
        } else if (weight >= critical_point1 && weight < critical_point2) {
            return "标准";
        } else {
            return"偏高";
        }

    }

    /**
     * 水分率
     *
     * @param activity     当前Activity
     * @param view         笑脸图标父级
     * @param imageView    笑脸图标
     * @param textView1    从左到右第一个分割线
     * @param textView2    从左到右第二个分割线
     * @param textView3    是否标准
     * @param gender       性别：0男，1女
     * @param waterPercent 当前用户水分率
     */
    public static void moisture(Activity activity, View view, ImageView imageView, TextView textView1, TextView textView2, TextView textView3, int gender, float waterPercent) {
        // 从左到右第一个分割线
        double critical_point1;
        // 从左到右第二个分割线
        double critical_point2;
        // 终点分割线
        double critical_point3 = 100;
        // 笑脸偏移量
        int deviation = 0;
        // 屏幕宽度
        int screenwidth = activity.getWindowManager().getDefaultDisplay().getWidth();

        if (gender == 1) {
            // 男
            critical_point1 = 55;
            critical_point2 = 65;
        } else {
            // 女
            critical_point1 = 45;
            critical_point2 = 60;
        }
        if (waterPercent <= critical_point1) {
            deviation = (int) (screenwidth * 0.25 * (waterPercent / critical_point1));
            imageView.setImageDrawable(activity.getDrawable(R.drawable.sunface));
            textView3.setText("不足");
            textView3.setBackground(activity.getDrawable(R.drawable.red_color_bg));
        } else if (waterPercent > critical_point1 && waterPercent <= critical_point2) {
            imageView.setImageDrawable(activity.getDrawable(R.drawable.sunface));
            deviation = (int) (screenwidth * 0.25 + (waterPercent - critical_point1) / (critical_point2 - critical_point1) * screenwidth * 0.5);
            textView3.setText("标准");
            textView3.setBackground(activity.getDrawable(R.drawable.green_light_color_bg));
        } else if (waterPercent > critical_point2) {
            imageView.setImageDrawable(activity.getDrawable(R.drawable.sunface));
            deviation = (int) (screenwidth * 0.75 + (waterPercent - critical_point2) / critical_point3 * screenwidth * 0.25);
            textView3.setText("优");
            textView3.setBackground(activity.getDrawable(R.drawable.grade_bg));
        }
        textView1.setText(MyUtil.onePoint(critical_point1) + "%");
        textView2.setText(MyUtil.onePoint(critical_point2) + "%");
        int margin = 0;
        if(waterPercent!=0){
            margin = deviation - 50;
            if (margin > screenwidth - 140) {
                margin = screenwidth - 140;
            }
        }

        view.setPadding(margin, 0, 0, 0);
    }

    public static String moistureString(int gender, float waterPercent) {
        // 从左到右第一个分割线
        double critical_point1;
        // 从左到右第二个分割线
        double critical_point2;
        // 终点分割线
        double critical_point3 = 100;
        if (gender == 1) {
            // 男
            critical_point1 = 55;
            critical_point2 = 65;
        } else {
            // 女
            critical_point1 = 45;
            critical_point2 = 60;
        }
        if (waterPercent <= critical_point1) {
            return "不足";
        } else if (waterPercent > critical_point1 && waterPercent <= critical_point2) {
            return "标准";
        } else {
            return "优";
        }

    }


    /**
     * 脂肪率
     *
     * @param activity   当前Activity
     * @param view       笑脸图标父级
     * @param imageView  笑脸图标
     * @param textView1  从左到右第一个分割线
     * @param textView2  从左到右第二个分割线
     * @param textView3  从左到右第三个分割线
     * @param textView4  从左到右第四个分割线
     * @param bft_biaoz  是否标准
     * @param gender     性别0男，1女
     * @param age        年龄
     * @param fatPercent 当前人的脂肪率
     */
    public static void bft(Activity activity, View view, ImageView imageView, TextView textView1, TextView textView2,
                           TextView textView3, TextView textView4, TextView bft_biaoz, int gender, int age, float fatPercent) {
        // 从左到右第一个分割线
        double critical_point1 = 0;
        // 从左到右第二个分割线
        double critical_point2 = 0;
        // 从左到右第三个分割线
        double critical_point3 = 0;
        // 从左到右第四个分割线
        double critical_point4 = 0;
        // 终点分割线
        double critical_point5 = 100;
        // 笑脸偏移量
        int deviation = 0;
        // 屏幕宽度
        int screenwidth = activity.getWindowManager().getDefaultDisplay().getWidth();

        if (gender == 1) {
            // 男
            if (age < 40) {
                critical_point1 = 10;
                critical_point2 = 16;
                critical_point3 = 21;
                critical_point4 = 26;
            } else if (age >= 40 && age < 60) {
                critical_point1 = 11;
                critical_point2 = 17;
                critical_point3 = 22;
                critical_point4 = 27;
            } else if (age >= 60) {
                critical_point1 = 13;
                critical_point2 = 19;
                critical_point3 = 24;
                critical_point4 = 29;
            }

        } else {
            // 女
            if (age < 40) {
                critical_point1 = 20;
                critical_point2 = 27;
                critical_point3 = 34;
                critical_point4 = 39;
            } else if (age >= 40 && age < 60) {
                critical_point1 = 21;
                critical_point2 = 28;
                critical_point3 = 35;
                critical_point4 = 40;
            } else if (age >= 60) {
                critical_point1 = 22;
                critical_point2 = 29;
                critical_point3 = 36;
                critical_point4 = 41;
            }
        }

        if (fatPercent <= critical_point1) {
            deviation = (int) (screenwidth * 0.2 * (fatPercent / critical_point1));
            imageView.setImageDrawable(activity.getDrawable(R.drawable.sunface));
            bft_biaoz.setText("瘦");
            bft_biaoz.setBackground(activity.getDrawable(R.drawable.green_light_color_bg));
        } else if (fatPercent > critical_point1 && fatPercent <= critical_point2) {
            imageView.setImageDrawable(activity.getDrawable(R.drawable.sunface));
            deviation = (int) (screenwidth * 0.2 + (fatPercent - critical_point1) / (critical_point2 - critical_point1) * screenwidth * 0.2);
            bft_biaoz.setText("偏瘦");
            bft_biaoz.setBackground(activity.getDrawable(R.drawable.green_light_color_bg));
        } else if (fatPercent > critical_point2 && fatPercent <= critical_point3) {
            imageView.setImageDrawable(activity.getDrawable(R.drawable.sunface));
            deviation = (int) (screenwidth * 0.4 + (fatPercent - critical_point2) / (critical_point3 - critical_point2) * screenwidth * 0.2);
            bft_biaoz.setText("标准");
            bft_biaoz.setBackground(activity.getDrawable(R.drawable.grade_bg));
        } else if (fatPercent > critical_point3 && fatPercent <= critical_point4) {
            imageView.setImageDrawable(activity.getDrawable(R.drawable.sunface));
            deviation = (int) (screenwidth * 0.6 + (fatPercent - critical_point3) / (critical_point4 - critical_point3) * screenwidth * 0.2);
            bft_biaoz.setText("偏胖");
            bft_biaoz.setBackground(activity.getDrawable(R.drawable.red_color_bg));
        } else if (fatPercent > critical_point4) {
            imageView.setImageDrawable(activity.getDrawable(R.drawable.sunface));
            deviation = (int) (screenwidth * 0.8 + (fatPercent - critical_point4) / critical_point5 * screenwidth * 0.2);
            bft_biaoz.setText("胖");
            bft_biaoz.setBackground(activity.getDrawable(R.drawable.red_color_bg));
        }
        textView1.setText(MyUtil.onePoint(critical_point1) + "%");
        textView2.setText(MyUtil.onePoint(critical_point2) + "%");
        textView3.setText(MyUtil.onePoint(critical_point3) + "%");
        textView4.setText(MyUtil.onePoint(critical_point4) + "%");
        int margin = 0;
        if(fatPercent!=0){
            margin = deviation - 50;
            if (margin > screenwidth - 100) {
                margin = screenwidth - 100;
            }
        }
        view.setPadding(margin, 0, 0, 0);
    }

    public static String bftString(int gender, int age, float fatPercent) {
        // 从左到右第一个分割线
        double critical_point1 = 0;
        // 从左到右第二个分割线
        double critical_point2 = 0;
        // 从左到右第三个分割线
        double critical_point3 = 0;
        // 从左到右第四个分割线
        double critical_point4 = 0;
        // 终点分割线
        double critical_point5 = 100;

        if (gender == 1) {
            // 男
            if (age < 40) {
                critical_point1 = 10;
                critical_point2 = 16;
                critical_point3 = 21;
                critical_point4 = 26;
            } else if (age >= 40 && age < 60) {
                critical_point1 = 11;
                critical_point2 = 17;
                critical_point3 = 22;
                critical_point4 = 27;
            } else if (age >= 60) {
                critical_point1 = 13;
                critical_point2 = 19;
                critical_point3 = 24;
                critical_point4 = 29;
            }

        } else {
            // 女
            if (age < 40) {
                critical_point1 = 20;
                critical_point2 = 27;
                critical_point3 = 34;
                critical_point4 = 39;
            } else if (age >= 40 && age < 60) {
                critical_point1 = 21;
                critical_point2 = 28;
                critical_point3 = 35;
                critical_point4 = 40;
            } else if (age >= 60) {
                critical_point1 = 22;
                critical_point2 = 29;
                critical_point3 = 36;
                critical_point4 = 41;
            }
        }

        if (fatPercent <= critical_point1) {
            return "瘦";
        } else if (fatPercent > critical_point1 && fatPercent <= critical_point2) {
            return "偏瘦";
        } else if (fatPercent > critical_point2 && fatPercent <= critical_point3) {
            return "标准";
        } else if (fatPercent > critical_point3 && fatPercent <= critical_point4) {
            return "偏胖";
        } else {
            return "胖";
        }
    }


    /**
     * 骨量
     *
     * @param activity  当前Activity
     * @param view      笑脸图标父级
     * @param imageView 笑脸图标
     * @param textView1 从左到右第一个分割线
     * @param textView2 从左到右第二个分割线
     * @param textView3 是否标准
     * @param bone      当前人的骨量
     */
    public static void bone(Activity activity, View view, ImageView imageView, TextView textView1, TextView textView2, TextView textView3,int gender,float weight, float bone,String danwei) {
        // 从左到右第一个分割线
        float critical_point1 = 2.4f;
        // 从左到右第二个分割线
        float critical_point2 = 2.6f;
        // 终点分割线
        float critical_point3 = 3.2f;
        // 笑脸偏移量
        int deviation = 0;
        if (gender == 1) {
            // 男
            if (weight < 60) {
                critical_point1 = 2.4f;
                critical_point2 = 2.6f;
                critical_point3 = 3.2f;
            } else if (weight >= 60 && weight <= 75) {
                critical_point1 = 2.8f;
                critical_point2 = 3.0f;
                critical_point3 = 3.2f;
            } else if (weight > 75) {
                critical_point1 = 3.1f;
                critical_point2 = 3.3f;
                critical_point3 = 3.4f;
            }

        } else {
            // 女
            if (weight < 45) {
                critical_point1 = 1.7f;
                critical_point2 = 1.9f;
                critical_point3 = 2.5f;
            } else if (weight >= 45 && weight <= 60) {
                critical_point1 = 2.1f;
                critical_point2 = 2.3f;
                critical_point3 = 2.5f;
            } else if (weight > 60) {
                critical_point1 = 2.4f;
                critical_point2 = 2.6f;
                critical_point3 = 3.0f;
            }
        }
        // 屏幕宽度
        int screenwidth = activity.getWindowManager().getDefaultDisplay().getWidth();

        if (bone <= critical_point1) {
            deviation = (int) (screenwidth * 0.25 * (bone / critical_point1));
            imageView.setImageDrawable(activity.getDrawable(R.drawable.sunface));
            textView3.setText("不足");
            textView3.setBackground(activity.getDrawable(R.drawable.red_color_bg));
        } else if (bone > critical_point1 && bone <= critical_point2) {
            imageView.setImageDrawable(activity.getDrawable(R.drawable.sunface));
            deviation = (int) (screenwidth * 0.25 + (bone - critical_point1) / (critical_point2 - critical_point1) * screenwidth * 0.5);
            textView3.setText("标准");
            textView3.setBackground(activity.getDrawable(R.drawable.green_light_color_bg));
        } else if (bone > critical_point2) {
            imageView.setImageDrawable(activity.getDrawable(R.drawable.sunface));
            deviation = (int) (screenwidth * 0.75 + (bone - critical_point2) / critical_point3 * screenwidth * 0.25);
            textView3.setText("优");
            textView3.setBackground(activity.getDrawable(R.drawable.grade_bg));
        }
        if (danwei.equals(UtilConstants.UNIT_LB) || danwei.equals(UtilConstants.UNIT_FATLB) || danwei.equals(UtilConstants.UNIT_ST)){
            textView1.setText(UtilTooth.kgToLB_ForFatScale(critical_point1) + "lb");
            textView2.setText(UtilTooth.kgToLB_ForFatScale(critical_point2) + "lb");
        }else{
            textView1.setText(MyUtil.onePoint(critical_point1) + "kg");
            textView2.setText(MyUtil.onePoint(critical_point2) + "kg");
        }
        int margin = 0;
        if(bone!=0){
            margin = deviation - 50;
            if (margin > screenwidth - 140) {
                margin = screenwidth - 140;
            }
        }
        view.setPadding(margin, 0, 0, 0);
    }

    public static String boneString(float bone) {
        // 从左到右第一个分割线
        float critical_point1 = 2.4f;
        // 从左到右第二个分割线
        float critical_point2 = 2.6f;
        // 终点分割线
        float critical_point3 = 1.0f;

        if (bone <= critical_point1) {
            return "不足";
        } else if (bone > critical_point1 && bone <= critical_point2) {
            return "标准";
        } else{
            return "优";
        }

    }


    /**
     * BMI
     *
     * @param activity  当前Activity
     * @param view      笑脸图标父级
     * @param imageView 笑脸图标
     * @param textView1 从左到右第一个分割线
     * @param textView2 从左到右第二个分割线
     * @param textView3 从左到右第三个分割线
     * @param bft_biaoz 是否标准
     * @param bmi       当前人的BMI值
     */
    public static void bmi(Activity activity, View view, ImageView imageView, TextView textView1, TextView textView2, TextView textView3, TextView bft_biaoz, float bmi) {
        // 从左到右第一个分割线
        double critical_point1 = 18.5;
        // 从左到右第二个分割线
        double critical_point2 = 24.0;
        // 从左到右第三个分割线
        double critical_point3 = 28.0;
        // 终点分割线
        double critical_point5 = 22;
        // 笑脸偏移量
        int deviation = 0;
        // 屏幕宽度
        int screenwidth = activity.getWindowManager().getDefaultDisplay().getWidth();


        if (bmi <= critical_point1) {
            deviation = (int) (screenwidth * 0.25 * (bmi / critical_point1));
            imageView.setImageDrawable(activity.getDrawable(R.drawable.sunface));
            bft_biaoz.setText("偏瘦");
            bft_biaoz.setBackground(activity.getDrawable(R.drawable.green_light_color_bg));
        } else if (bmi > critical_point1 && bmi <= critical_point2) {
            imageView.setImageDrawable(activity.getDrawable(R.drawable.sunface));
            deviation = (int) (screenwidth * 0.25 + (bmi - critical_point1) / (critical_point2 - critical_point1) * screenwidth * 0.25);
            bft_biaoz.setText("标准");
            bft_biaoz.setBackground(activity.getDrawable(R.drawable.grade_bg));
        } else if (bmi > critical_point2 && bmi <= critical_point3) {
            imageView.setImageDrawable(activity.getDrawable(R.drawable.sunface));
            deviation = (int) (screenwidth * 0.5 + (bmi - critical_point2) / (critical_point3 - critical_point2) * screenwidth * 0.25);
            bft_biaoz.setText("偏胖");
            bft_biaoz.setBackground(activity.getDrawable(R.drawable.red_color_bg));
        } else if (bmi > critical_point3) {
            imageView.setImageDrawable(activity.getDrawable(R.drawable.sunface));
            deviation = (int) (screenwidth * 0.75 + (bmi - critical_point3) / critical_point5 * screenwidth * 0.25);
            bft_biaoz.setText("胖");
            bft_biaoz.setBackground(activity.getDrawable(R.drawable.red_color_bg));
        }
        textView1.setText(MyUtil.onePoint(critical_point1) + "");
        textView2.setText(MyUtil.onePoint(critical_point2) + "");
        textView3.setText(MyUtil.onePoint(critical_point3) + "");
        int margin = 0;
        if(bmi!=0){
            margin = deviation - 50;
            if (margin > screenwidth - 100) {
                margin = screenwidth - 100;
            }
        }

        view.setPadding(margin, 0, 0, 0);
    }

    public static String bmiString(float bmi) {
        // 从左到右第一个分割线
        double critical_point1 = 18.5;
        // 从左到右第二个分割线
        double critical_point2 = 24.0;
        // 从左到右第三个分割线
        double critical_point3 = 28.0;
        // 终点分割线
        double critical_point5 = 22;

        if (bmi <= critical_point1) {
           return "偏瘦";
        } else if (bmi > critical_point1 && bmi <= critical_point2) {
            return"标准";
        } else if (bmi > critical_point2 && bmi <= critical_point3) {
            return"偏胖";
        } else{
            return"胖";
        }
    }


    /**
     * 内脏脂肪指数
     *
     * @param activity
     * @param view
     * @param imageView
     * @param textView1
     * @param textView2
     * @param textView3
     * @param visceral
     */
    public static void visceralFat(Activity activity, View view, ImageView imageView, TextView textView1, TextView textView2, TextView textView3, float visceral) {
        // 从左到右第一个分割线
        double critical_point1 = 9.0;
        // 从左到右第二个分割线
        double critical_point2 = 14.0;
        // 终点分割线
        double critical_point3 = 16;
        // 笑脸偏移量
        int deviation = 0;
        // 屏幕宽度
        int screenwidth = activity.getWindowManager().getDefaultDisplay().getWidth();

        if (visceral <= critical_point1) {
            deviation = (int) (screenwidth * 0.4 * (visceral / critical_point1));
            imageView.setImageDrawable(activity.getDrawable(R.drawable.sunface));
            textView3.setText("标准");
            textView3.setBackground(activity.getDrawable(R.drawable.grade_bg));
        } else if (visceral > critical_point1 && visceral <= critical_point2) {
            imageView.setImageDrawable(activity.getDrawable(R.drawable.sunface));
            deviation = (int) (screenwidth * 0.4 + (visceral - critical_point1) / (critical_point2 - critical_point1) * screenwidth * 0.4);
            textView3.setText("警惕");
            textView3.setBackground(activity.getDrawable(R.drawable.orange_bg));
        } else if (visceral > critical_point2) {
            imageView.setImageDrawable(activity.getDrawable(R.drawable.sunface));
            deviation = (int) (screenwidth * 0.8 + (visceral - critical_point2) / critical_point3 * screenwidth * 0.2);
            textView3.setText("危险");
            textView3.setBackground(activity.getDrawable(R.drawable.red_color_bg));
        }
        textView1.setText(MyUtil.onePoint(critical_point1) + "");
        textView2.setText(MyUtil.onePoint(critical_point2) + "");
        int margin = 0;
        if(visceral!=0){
            margin = deviation - 80;
            if (margin > screenwidth - 140) {
                margin = screenwidth - 140;
            }
        }
        view.setPadding(margin, 0, 0, 0);
    }

    public static String visceralFatString(float bone) {
        // 从左到右第一个分割线
        double critical_point1 = 9.0;
        // 从左到右第二个分割线
        double critical_point2 = 14.0;
        // 终点分割线
        double critical_point3 = 16;

        if (bone <= critical_point1) {
            return "标准";
        } else if (bone > critical_point1 && bone <= critical_point2) {
            return"警惕";
        } else {
            return"危险";
        }
    }

    /**
     * BMR基础代谢率
     *
     * @param activity  当前Activity
     * @param view      笑脸图标父级
     * @param imageView 笑脸图标
     * @param textView1 从左到右第一个分割线
     * @param textView3 是否标准
     * @param bmr       当前人的BMr值
     */
    public static void bmr(Activity activity, View view, ImageView imageView, TextView textView1, TextView textView3,int gender,int age,float weight, float bmr) {
        // 从左到右第一个分割线
        float critical_point1 = 1261;
        // 终点分割线
        float critical_point3 = 1261;
        // 笑脸偏移量
        int deviation = 0;
        // 屏幕宽度
        int screenwidth = activity.getWindowManager().getDefaultDisplay().getWidth();

        if(age<=29){
            if(gender==1){
                critical_point1 = UtilTooth.keep1Point3(weight * 24.0f);
                critical_point3 = critical_point1;
            }else {
                critical_point1 = UtilTooth.keep1Point3(weight * 23.6f);
                critical_point3 = critical_point1;
            }
        }else if(age>=30 && age<=49){
            if(gender==1){
                critical_point1 = UtilTooth.keep1Point3(weight * 22.3f);
                critical_point3 = critical_point1;
            }else {
                critical_point1 = UtilTooth.keep1Point3(weight * 21.7f);
                critical_point3 = critical_point1;
            }
        }else{
            if(gender==1){
                critical_point1 = UtilTooth.keep1Point3(weight * 21.5f);
                critical_point3 = critical_point1;
            }else {
                critical_point1 = UtilTooth.keep1Point3(weight * 20.7f);
                critical_point3 = critical_point1;
            }
        }

        if (bmr <= critical_point1) {
            deviation = (int) (screenwidth * 0.5 * (bmr / critical_point1));
            imageView.setImageDrawable(activity.getDrawable(R.drawable.sunface));
            textView3.setText("偏低");
            textView3.setBackground(activity.getDrawable(R.drawable.red_color_bg));
        } else if (bmr > critical_point1) {
            imageView.setImageDrawable(activity.getDrawable(R.drawable.sunface));
            deviation = (int) (screenwidth * 0.5 + (bmr - critical_point1) / critical_point3 * screenwidth * 0.5);
            textView3.setText("优");
            textView3.setBackground(activity.getDrawable(R.drawable.grade_bg));
        }
        textView1.setText(MyUtil.onePoint(critical_point1) + "Kcal");
        int margin = 0;
        if(bmr!=0){
            margin = deviation - 70;
            if (margin > screenwidth - 140) {
                margin = screenwidth - 140;
            }
        }
        view.setPadding(margin, 0, 0, 0);
    }

    public static String bmrString(int gender,int age,float weight, float bmr) {
        // 从左到右第一个分割线
        float critical_point1 = 1261;
        // 终点分割线
        float critical_point3 = 1261;

        if(age<=29){
            if(gender==1){
                critical_point1 = UtilTooth.keep1Point3(weight * 24.0f);
                critical_point3 = critical_point1;
            }else {
                critical_point1 = UtilTooth.keep1Point3(weight * 23.6f);
                critical_point3 = critical_point1;
            }
        }else if(age>=30 && age<=49){
            if(gender==1){
                critical_point1 = UtilTooth.keep1Point3(weight * 22.3f);
                critical_point3 = critical_point1;
            }else {
                critical_point1 = UtilTooth.keep1Point3(weight * 21.7f);
                critical_point3 = critical_point1;
            }
        }else{
            if(gender==1){
                critical_point1 = UtilTooth.keep1Point3(weight * 21.5f);
                critical_point3 = critical_point1;
            }else {
                critical_point1 = UtilTooth.keep1Point3(weight * 20.7f);
                critical_point3 = critical_point1;
            }
        }

        if (bmr <= critical_point1) {
            return "偏低";
        } else {
            return "优";
        }
    }

    public static void muscle(Activity activity, View view, ImageView imageView, TextView textView1, TextView textView2, TextView textView3,int gender, float height, float muscle,String danwei) {
        // 从左到右第一个分割线
        float critical_point1 = 1;
        // 从左到右第二个分割线
        float critical_point2 = 1;
        // 终点分割线
        float critical_point3 = 20;
        // 笑脸偏移量
        int deviation = 0;
        // 屏幕宽度
        int screenwidth = activity.getWindowManager().getDefaultDisplay().getWidth();
        if (gender == 1) {
            // 男
            if (height < 160) {
                critical_point1 = 38.5f;
                critical_point2 = 46.5f;
                //critical_point3 = 21;

            } else if (height >= 160 && height <= 170) {
                critical_point1 = 44f;
                critical_point2 = 52.4f;
                //critical_point3 = 22;

            } else {
                critical_point1 = 49.4f;
                critical_point2 = 59.4f;
               // critical_point3 = 24;

            }

        } else {
            // 女
            if (height < 150) {
                critical_point1 = 29.1f;
                critical_point2 = 34.7f;
                //critical_point3 = 21;

            } else if (height >= 150 && height <= 160) {
                critical_point1 = 32.9f;
                critical_point2 = 37.5f;
               // critical_point3 = 22;

            } else {
                critical_point1 = 36.5f;
                critical_point2 = 42.5f;
                //critical_point3 = 24;

            }
        }

        if (muscle <= critical_point1) {
            deviation = (int) (screenwidth * 0.25 * (muscle / critical_point1));
            imageView.setImageDrawable(activity.getDrawable(R.drawable.sunface));
            textView3.setText("不足");
            textView3.setBackground(activity.getDrawable(R.drawable.red_color_bg));
        } else if (muscle > critical_point1 && muscle <= critical_point2) {
            imageView.setImageDrawable(activity.getDrawable(R.drawable.sunface));
            deviation = (int) (screenwidth * 0.25 + (muscle - critical_point1) / (critical_point2 - critical_point1) * screenwidth * 0.5);
            textView3.setText("标准");
            textView3.setBackground(activity.getDrawable(R.drawable.green_light_color_bg));
        } else if (muscle > critical_point2) {
            imageView.setImageDrawable(activity.getDrawable(R.drawable.sunface));
            deviation = (int) (screenwidth * 0.75 + (muscle - critical_point2) / critical_point3 * screenwidth * 0.25);
            textView3.setText("优");
            textView3.setBackground(activity.getDrawable(R.drawable.grade_bg));
        }
        if (danwei.equals(UtilConstants.UNIT_LB) || danwei.equals(UtilConstants.UNIT_FATLB) || danwei.equals(UtilConstants.UNIT_ST)){
            textView1.setText(UtilTooth.kgToLB_ForFatScale(critical_point1) + "lb");
            textView2.setText(UtilTooth.kgToLB_ForFatScale(critical_point2) + "lb");
        }else{
            textView1.setText(MyUtil.onePoint(critical_point1) + "kg");
            textView2.setText(MyUtil.onePoint(critical_point2) + "kg");
        }
        int margin = 0;
        if(muscle!=0){
            margin = deviation - 40;
            if (margin > screenwidth - 140) {
                margin = screenwidth - 140;
            }
        }

        view.setPadding(margin, 0, 0, 0);
    }

    public static String muscleString(int gender, float height, float muscle) {
        // 从左到右第一个分割线
        float critical_point1 = 1;
        // 从左到右第二个分割线
        float critical_point2 = 1;
        // 终点分割线
        float critical_point3 = 20;
        if (gender == 1) {
            // 男
            if (height < 160) {
                critical_point1 = 38.5f;
                critical_point2 = 46.5f;
                //critical_point3 = 21;

            } else if (height >= 160 && height <= 170) {
                critical_point1 = 44f;
                critical_point2 = 52.4f;
                //critical_point3 = 22;

            } else {
                critical_point1 = 49.4f;
                critical_point2 = 59.4f;
                // critical_point3 = 24;

            }

        } else {
            // 女
            if (height < 150) {
                critical_point1 = 29.1f;
                critical_point2 = 34.7f;
                //critical_point3 = 21;

            } else if (height >= 150 && height <= 160) {
                critical_point1 = 32.9f;
                critical_point2 = 37.5f;
                // critical_point3 = 22;

            } else {
                critical_point1 = 36.5f;
                critical_point2 = 42.5f;
                //critical_point3 = 24;

            }
        }

        if (muscle <= critical_point1) {
            return "不足";
        } else if (muscle > critical_point1 && muscle <= critical_point2) {
            return "标准";
        } else {
            return "优";
        }

    }

}
