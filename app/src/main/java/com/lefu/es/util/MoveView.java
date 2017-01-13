package com.lefu.es.util;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lefu.iwellness.newes.cn.system.R;

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
    public static void weight(Activity activity, View view, ImageView imageView, TextView textView1, TextView textView2, TextView textView3, int gender, int height, double weight) {
        // 标准体重
        double standard_weight;
        // 从左到右第一个分割线
        double critical_point1;
        // 从左到右第二个分割线
        double critical_point2;
        // 终点分割线
        double critical_point3;
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
        critical_point3 = standard_weight + standard_weight * 0.2;

        if (weight < critical_point1) {
            deviation = (int) (screenwidth * 0.25 * (weight / critical_point1));
            imageView.setImageDrawable(activity.getDrawable(R.drawable.ag_icon));
            textView3.setText("偏低");
            textView3.setBackground(activity.getDrawable(R.drawable.orange_bg));
        } else if (weight >= critical_point1 && weight < critical_point2) {
            imageView.setImageDrawable(activity.getDrawable(R.drawable.hp_icon));
            deviation = (int) (screenwidth * 0.25 + (weight - critical_point1) / (critical_point2 - critical_point1) * screenwidth * 0.5);
            textView3.setText("标准");
            textView3.setBackground(activity.getDrawable(R.drawable.grade_bg));
        } else if (weight >= critical_point2) {
            imageView.setImageDrawable(activity.getDrawable(R.drawable.ag_icon));
            deviation = (int) (screenwidth * 0.75 + (weight - critical_point2) / critical_point3 * screenwidth * 0.25);
            textView3.setText("偏高");
            textView3.setBackground(activity.getDrawable(R.drawable.red_color_bg));
        }
        textView1.setText(MyUtil.onePoint(critical_point1) + "kg");
        textView2.setText(MyUtil.onePoint(critical_point2) + "kg");
        int margin = deviation - 50;
        if (margin > screenwidth - 140) {
            margin = screenwidth - 140;
        }
        view.setPadding(margin, 0, 0, 0);
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
    public static void moisture(Activity activity, View view, ImageView imageView, TextView textView1, TextView textView2, TextView textView3, int gender, double waterPercent) {
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

        if (gender == 0) {
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
            imageView.setImageDrawable(activity.getDrawable(R.drawable.ag_icon));
            textView3.setText("不足");
            textView3.setBackground(activity.getDrawable(R.drawable.red_color_bg));
        } else if (waterPercent > critical_point1 && waterPercent <= critical_point2) {
            imageView.setImageDrawable(activity.getDrawable(R.drawable.hp_icon));
            deviation = (int) (screenwidth * 0.25 + (waterPercent - critical_point1) / (critical_point2 - critical_point1) * screenwidth * 0.5);
            textView3.setText("标准");
            textView3.setBackground(activity.getDrawable(R.drawable.green_light_color_bg));
        } else if (waterPercent > critical_point2) {
            imageView.setImageDrawable(activity.getDrawable(R.drawable.hp_icon));
            deviation = (int) (screenwidth * 0.75 + (waterPercent - critical_point2) / critical_point3 * screenwidth * 0.25);
            textView3.setText("优");
            textView3.setBackground(activity.getDrawable(R.drawable.grade_bg));
        }
        textView1.setText(MyUtil.onePoint(critical_point1) + "%");
        textView2.setText(MyUtil.onePoint(critical_point2) + "%");
        int margin = deviation - 50;
        if (margin > screenwidth - 140) {
            margin = screenwidth - 140;
        }
        view.setPadding(margin, 0, 0, 0);
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
                           TextView textView3, TextView textView4, TextView bft_biaoz, int gender, int age, double fatPercent) {
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

        if (gender == 0) {
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
            imageView.setImageDrawable(activity.getDrawable(R.drawable.hp_icon));
            bft_biaoz.setText("瘦");
            bft_biaoz.setBackground(activity.getDrawable(R.drawable.green_light_color_bg));
        } else if (fatPercent > critical_point1 && fatPercent <= critical_point2) {
            imageView.setImageDrawable(activity.getDrawable(R.drawable.hp_icon));
            deviation = (int) (screenwidth * 0.2 + (fatPercent - critical_point1) / (critical_point2 - critical_point1) * screenwidth * 0.2);
            bft_biaoz.setText("偏瘦");
            bft_biaoz.setBackground(activity.getDrawable(R.drawable.green_light_color_bg));
        } else if (fatPercent > critical_point2 && fatPercent <= critical_point3) {
            imageView.setImageDrawable(activity.getDrawable(R.drawable.hp_icon));
            deviation = (int) (screenwidth * 0.4 + (fatPercent - critical_point2) / (critical_point3 - critical_point2) * screenwidth * 0.2);
            bft_biaoz.setText("标准");
            bft_biaoz.setBackground(activity.getDrawable(R.drawable.grade_bg));
        } else if (fatPercent > critical_point3 && fatPercent <= critical_point4) {
            imageView.setImageDrawable(activity.getDrawable(R.drawable.ag_icon));
            deviation = (int) (screenwidth * 0.6 + (fatPercent - critical_point3) / (critical_point4 - critical_point3) * screenwidth * 0.2);
            bft_biaoz.setText("偏胖");
            bft_biaoz.setBackground(activity.getDrawable(R.drawable.red_color_bg));
        } else if (fatPercent > critical_point4) {
            imageView.setImageDrawable(activity.getDrawable(R.drawable.ag_icon));
            deviation = (int) (screenwidth * 0.8 + (fatPercent - critical_point4) / critical_point5 * screenwidth * 0.2);
            bft_biaoz.setText("胖");
            bft_biaoz.setBackground(activity.getDrawable(R.drawable.red_color_bg));
        }
        textView1.setText(MyUtil.onePoint(critical_point1) + "%");
        textView2.setText(MyUtil.onePoint(critical_point2) + "%");
        textView3.setText(MyUtil.onePoint(critical_point3) + "%");
        textView4.setText(MyUtil.onePoint(critical_point4) + "%");
        int margin = deviation - 50;
        if (margin > screenwidth - 100) {
            margin = screenwidth - 100;
        }
        view.setPadding(margin, 0, 0, 0);
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
    public static void bone(Activity activity, View view, ImageView imageView, TextView textView1, TextView textView2, TextView textView3, double bone) {
        // 从左到右第一个分割线
        double critical_point1 = 2.4;
        // 从左到右第二个分割线
        double critical_point2 = 2.6;
        // 终点分割线
        double critical_point3 = 1.0;
        // 笑脸偏移量
        int deviation = 0;
        // 屏幕宽度
        int screenwidth = activity.getWindowManager().getDefaultDisplay().getWidth();

        if (bone <= critical_point1) {
            deviation = (int) (screenwidth * 0.25 * (bone / critical_point1));
            imageView.setImageDrawable(activity.getDrawable(R.drawable.ag_icon));
            textView3.setText("不足");
            textView3.setBackground(activity.getDrawable(R.drawable.red_color_bg));
        } else if (bone > critical_point1 && bone <= critical_point2) {
            imageView.setImageDrawable(activity.getDrawable(R.drawable.hp_icon));
            deviation = (int) (screenwidth * 0.25 + (bone - critical_point1) / (critical_point2 - critical_point1) * screenwidth * 0.5);
            textView3.setText("标准");
            textView3.setBackground(activity.getDrawable(R.drawable.green_light_color_bg));
        } else if (bone > critical_point2) {
            imageView.setImageDrawable(activity.getDrawable(R.drawable.hp_icon));
            deviation = (int) (screenwidth * 0.75 + (bone - critical_point2) / critical_point3 * screenwidth * 0.25);
            textView3.setText("优");
            textView3.setBackground(activity.getDrawable(R.drawable.grade_bg));
        }
        textView1.setText(MyUtil.onePoint(critical_point1) + "kg");
        textView2.setText(MyUtil.onePoint(critical_point2) + "kg");
        int margin = deviation - 50;
        if (margin > screenwidth - 140) {
            margin = screenwidth - 140;
        }
        view.setPadding(margin, 0, 0, 0);
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
    public static void bmi(Activity activity, View view, ImageView imageView, TextView textView1, TextView textView2, TextView textView3, TextView bft_biaoz, double bmi) {
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
            imageView.setImageDrawable(activity.getDrawable(R.drawable.hp_icon));
            bft_biaoz.setText("偏瘦");
            bft_biaoz.setBackground(activity.getDrawable(R.drawable.green_light_color_bg));
        } else if (bmi > critical_point1 && bmi <= critical_point2) {
            imageView.setImageDrawable(activity.getDrawable(R.drawable.hp_icon));
            deviation = (int) (screenwidth * 0.25 + (bmi - critical_point1) / (critical_point2 - critical_point1) * screenwidth * 0.25);
            bft_biaoz.setText("健康");
            bft_biaoz.setBackground(activity.getDrawable(R.drawable.grade_bg));
        } else if (bmi > critical_point2 && bmi <= critical_point3) {
            imageView.setImageDrawable(activity.getDrawable(R.drawable.ag_icon));
            deviation = (int) (screenwidth * 0.5 + (bmi - critical_point2) / (critical_point3 - critical_point2) * screenwidth * 0.25);
            bft_biaoz.setText("偏胖");
            bft_biaoz.setBackground(activity.getDrawable(R.drawable.red_color_bg));
        } else if (bmi > critical_point3) {
            imageView.setImageDrawable(activity.getDrawable(R.drawable.ag_icon));
            deviation = (int) (screenwidth * 0.75 + (bmi - critical_point3) / critical_point5 * screenwidth * 0.25);
            bft_biaoz.setText("胖");
            bft_biaoz.setBackground(activity.getDrawable(R.drawable.red_color_bg));
        }
        textView1.setText(MyUtil.onePoint(critical_point1) + "");
        textView2.setText(MyUtil.onePoint(critical_point2) + "");
        textView3.setText(MyUtil.onePoint(critical_point3) + "");
        int margin = deviation - 50;
        if (margin > screenwidth - 100) {
            margin = screenwidth - 100;
        }
        view.setPadding(margin, 0, 0, 0);
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
     * @param bone
     */
    public static void visceralFat(Activity activity, View view, ImageView imageView, TextView textView1, TextView textView2, TextView textView3, double bone) {
        // 从左到右第一个分割线
        double critical_point1 = 8.0;
        // 从左到右第二个分割线
        double critical_point2 = 14.0;
        // 终点分割线
        double critical_point3 = 16;
        // 笑脸偏移量
        int deviation = 0;
        // 屏幕宽度
        int screenwidth = activity.getWindowManager().getDefaultDisplay().getWidth();

        if (bone <= critical_point1) {
            deviation = (int) (screenwidth * 0.4 * (bone / critical_point1));
            imageView.setImageDrawable(activity.getDrawable(R.drawable.hp_icon));
            textView3.setText("标准");
            textView3.setBackground(activity.getDrawable(R.drawable.grade_bg));
        } else if (bone > critical_point1 && bone <= critical_point2) {
            imageView.setImageDrawable(activity.getDrawable(R.drawable.ag_icon));
            deviation = (int) (screenwidth * 0.4 + (bone - critical_point1) / (critical_point2 - critical_point1) * screenwidth * 0.4);
            textView3.setText("警惕");
            textView3.setBackground(activity.getDrawable(R.drawable.orange_bg));
        } else if (bone > critical_point2) {
            imageView.setImageDrawable(activity.getDrawable(R.drawable.ag_icon));
            deviation = (int) (screenwidth * 0.8 + (bone - critical_point2) / critical_point3 * screenwidth * 0.2);
            textView3.setText("危险");
            textView3.setBackground(activity.getDrawable(R.drawable.red_color_bg));
        }
        textView1.setText(MyUtil.onePoint(critical_point1) + "");
        textView2.setText(MyUtil.onePoint(critical_point2) + "");
        int margin = deviation - 80;
        if (margin > screenwidth - 140) {
            margin = screenwidth - 140;
        }
        view.setPadding(margin, 0, 0, 0);
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
    public static void bmr(Activity activity, View view, ImageView imageView, TextView textView1, TextView textView3, double bmr) {
        // 从左到右第一个分割线
        double critical_point1 = 1261;
        // 终点分割线
        double critical_point3 = 1261;
        // 笑脸偏移量
        int deviation = 0;
        // 屏幕宽度
        int screenwidth = activity.getWindowManager().getDefaultDisplay().getWidth();

        if (bmr <= critical_point1) {
            deviation = (int) (screenwidth * 0.5 * (bmr / critical_point1));
            imageView.setImageDrawable(activity.getDrawable(R.drawable.ag_icon));
            textView3.setText("偏低");
            textView3.setBackground(activity.getDrawable(R.drawable.red_color_bg));
        } else if (bmr > critical_point1) {
            imageView.setImageDrawable(activity.getDrawable(R.drawable.hp_icon));
            deviation = (int) (screenwidth * 0.5 + (bmr - critical_point1) / critical_point3 * screenwidth * 0.5);
            textView3.setText("优");
            textView3.setBackground(activity.getDrawable(R.drawable.grade_bg));
        }
        textView1.setText(MyUtil.onePoint(critical_point1) + "Kcal");
        int margin = deviation - 70;
        if (margin > screenwidth - 140) {
            margin = screenwidth - 140;
        }
        view.setPadding(margin, 0, 0, 0);
    }

    public static void muscle(Activity activity, View view, ImageView imageView, TextView textView1, TextView textView2, TextView textView3, double muscle) {
        // 从左到右第一个分割线
        double critical_point1 = 72;
        // 从左到右第二个分割线
        double critical_point2 = 82;
        // 终点分割线
        double critical_point3 = 18;
        // 笑脸偏移量
        int deviation = 0;
        // 屏幕宽度
        int screenwidth = activity.getWindowManager().getDefaultDisplay().getWidth();

        if (muscle <= critical_point1) {
            deviation = (int) (screenwidth * 0.25 * (muscle / critical_point1));
            imageView.setImageDrawable(activity.getDrawable(R.drawable.ag_icon));
            textView3.setText("不足");
            textView3.setBackground(activity.getDrawable(R.drawable.red_color_bg));
        } else if (muscle > critical_point1 && muscle <= critical_point2) {
            imageView.setImageDrawable(activity.getDrawable(R.drawable.hp_icon));
            deviation = (int) (screenwidth * 0.25 + (muscle - critical_point1) / (critical_point2 - critical_point1) * screenwidth * 0.5);
            textView3.setText("标准");
            textView3.setBackground(activity.getDrawable(R.drawable.green_light_color_bg));
        } else if (muscle > critical_point2) {
            imageView.setImageDrawable(activity.getDrawable(R.drawable.hp_icon));
            deviation = (int) (screenwidth * 0.75 + (muscle - critical_point2) / critical_point3 * screenwidth * 0.25);
            textView3.setText("优");
            textView3.setBackground(activity.getDrawable(R.drawable.grade_bg));
        }
        textView1.setText(MyUtil.onePoint(critical_point1) + "%");
        textView2.setText(MyUtil.onePoint(critical_point2) + "%");
        int margin = deviation - 40;
        if (margin > screenwidth - 140) {
            margin = screenwidth - 140;
        }
        view.setPadding(margin, 0, 0, 0);
    }

}
