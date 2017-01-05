package com.lefu.es.view.guideview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lefu.iwellness.newes.cn.system.R;

import java.util.ArrayList;
import java.util.List;



/**
 * * des:“应用新功能”的用户指引view
 * Created by jaydenxiao
 * on 2016.08.11:59
 */
public class HighLightGuideView extends View {
    //高亮类型：矩形、圆形、椭圆形
    public static final int VIEWSTYLE_RECT = 0;
    public static final int VIEWSTYLE_CIRCLE = 1;
    public static final int VIEWSTYLE_OVAL = 2;
    //画笔类型，圆滑、默认
    public static final int MASKBLURSTYLE_SOLID = 0;
    public static final int MASKBLURSTYLE_NORMAL = 1;

    private View rootView;//activity的contentview,是FrameLayout
    private Bitmap jtUpLeft, jtUpRight, jtDownRight, jtDownLeft;// 指示箭头
    private Bitmap fgBitmap;// 前景
    private Canvas mCanvas;// 绘制蒙版层的画布
    private Paint mPaint;// 绘制蒙版层画笔
    private int screenW, screenH;// 屏幕宽高
    private static final int margin = 40;
    private int radius;//圆半径
    private OnDismissListener onDismissListener;//关闭监听
    private Activity activity;

    /*******************可配置属性*****************************/
    private boolean touchOutsideCancel = true;//外部点击是否可关闭
//    private int highLightStyle = VIEWSTYLE_RECT;//高亮类型默认圆形
    public int maskblurstyle = MASKBLURSTYLE_SOLID;//画笔类型默认
    private ArrayList<Bitmap> tipBitmaps;//显示图片
    private ArrayList<View> targetViews;//高亮目标view
    private int maskColor = 0x99000000;// 蒙版层颜色
    private int borderWitdh = 10;
    private int highLisghtPadding = 0;// 高亮控件padding
    private int okBtnX, okBtnY;
    private String mText;
    
    //高亮view的显示半径
    private List<Float> targetRadius;
    private List<Integer> targetStyle;


    private HighLightGuideView(Activity activity) {
        super(activity);
        this.activity=activity;
        // 计算参数
        cal(activity);
        // 初始化对象
        init(activity);
    }

    public static HighLightGuideView builder(Activity activity) {
        return new HighLightGuideView(activity);
    }

    /**
     * 计算参数
     *
     * @param context 上下文环境引用
     */
    private void cal(Context context) {
        // 获取屏幕尺寸数组
        int[] screenSize = UiUtil.getScreenSize((Activity) context);
        // 获取屏幕宽高
        screenW = screenSize[0];
        screenH = screenSize[1];
    }

    /**
     * 初始化对象
     */
    private void init(Context context) {
        tipBitmaps = new ArrayList<Bitmap>();
        targetViews = new ArrayList<View>();
        targetRadius = new ArrayList<Float>();
        targetStyle = new ArrayList<Integer>();
        rootView = ((Activity) getContext()).findViewById(android.R.id.content);
        // 实例化画笔并开启其抗锯齿和抗抖动
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        // 设置画笔透明度为0是关键！
        mPaint.setARGB(0, 255, 0, 0);
        // 设置混合模式为DST_IN
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        BlurMaskFilter.Blur blurStyle = null;
        switch (maskblurstyle) {
            case MASKBLURSTYLE_SOLID:
                blurStyle = BlurMaskFilter.Blur.SOLID;
                break;
            case MASKBLURSTYLE_NORMAL:
                blurStyle = BlurMaskFilter.Blur.NORMAL;
                break;
        }
        mPaint.setMaskFilter(new BlurMaskFilter(15, blurStyle));
        // 生成前景图Bitmap
        fgBitmap = Bitmap.createBitmap(screenW, screenH, Bitmap.Config.ARGB_4444);
        // 将其注入画布
        mCanvas = new Canvas(fgBitmap);
        // 绘制前景画布颜色
        mCanvas.drawColor(maskColor);
        // 实例化箭头图片
        jtDownRight = BitmapFactory.decodeResource(getResources(), R.drawable.hl_down_right);
        jtDownLeft = BitmapFactory.decodeResource(getResources(), R.drawable.hl_down_left);
        jtUpLeft = BitmapFactory.decodeResource(getResources(), R.drawable.hl_up_left);
        jtUpRight = BitmapFactory.decodeResource(getResources(), R.drawable.hl_up_right);
    }

    @SuppressLint("ResourceAsColor")
	@Override
    protected void onDraw(Canvas canvas) {
        if (targetViews == null && tipBitmaps == null)
            return;
        // 绘制前景
        canvas.drawBitmap(fgBitmap, 0, 0, null);
        //有高亮控件
        if (targetViews.size() > 0 && tipBitmaps.size() > 0) {
            for (int i = 0; i < targetViews.size(); i++) {
                //高亮控件宽高
                int vWidth = targetViews.get(i).getWidth();
                int vHeight = targetViews.get(i).getHeight();
                
                float radiusDiff = targetRadius.get(i);
                int highLightStyle = targetStyle.get(i);
                
                //获取获取高亮控件坐标
                int left = 0;
                int top = 0;
                int right = 0;
                int bottom = 0;
                try {
                    Rect rtLocation =ViewUtils.getLocationInView(((ViewGroup)activity.findViewById(Window.ID_ANDROID_CONTENT)).getChildAt(0),targetViews.get(i));
                    left = rtLocation.left;
                    top = rtLocation.top;
                    right = rtLocation.right;
                    bottom = rtLocation.bottom;
                    Log.d("statusheightssleft",left+"");
                    Log.d("statusheightsstop",top+"");
                    Log.d("statusheightbottom",right+"");
                    Log.d("statusheightsbottom",bottom+"");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                //绘制高亮形状
                switch (highLightStyle) {
                    case VIEWSTYLE_OVAL:
                        RectF rectf = new RectF(left-highLisghtPadding-radiusDiff, top-highLisghtPadding, right+highLisghtPadding+radiusDiff, bottom+highLisghtPadding);  
                        mCanvas.drawOval(rectf, mPaint);
                        break;
                    case VIEWSTYLE_RECT:
                        RectF rect = new RectF(left - borderWitdh-highLisghtPadding, top - borderWitdh-highLisghtPadding, right + borderWitdh+highLisghtPadding, bottom + borderWitdh+highLisghtPadding);
                        mCanvas.drawRoundRect(rect, 20, 20, mPaint);
                        break;
                    case VIEWSTYLE_CIRCLE:
                    default:
                        radius = vWidth > vHeight ? vWidth / 2 +highLisghtPadding/2: vHeight / 2+ highLisghtPadding/2;
                        if (radius < 50) {
                            radius = 60;
                        }
                        mCanvas.drawCircle(left + vWidth / 2, top + vHeight / 2, radius*radiusDiff, mPaint);
                        break;

                }
                //绘制箭头和提示图
//                if (bottom < screenH / 2 || (screenH / 2 - top > bottom - screenH / 2)) {// 偏上
//                    int jtTop = highLightStyle == VIEWSTYLE_CIRCLE ? bottom +highLisghtPadding + margin+radius/3 : bottom +highLisghtPadding+ margin;
//                    if (right < screenW / 2 || (screenW / 2 - left > right - screenW / 2)) {//偏左
//                        //canvas.drawBitmap(jtUpLeft, left + vWidth / 2, jtTop, null);
//                        if (tipBitmaps.get(i) != null) {
//                        	
//                            //canvas.drawBitmap(tipBitmaps.get(i), screenW/2-tipBitmaps.get(i).getWidth()/2, screenH - tipBitmaps.get(i).getHeight()-200, null);
//                            
//                        }
//                    } else {
//                        //canvas.drawBitmap(jtUpRight, left + vWidth / 2 - 100 - margin, jtTop, null);
//                        if (tipBitmaps.get(i) != null) {
//                            canvas.drawBitmap(tipBitmaps.get(i), left + vWidth / 2 - 100 - tipBitmaps.get(i).getWidth() / 2, jtTop + jtUpRight.getHeight(), null);
//                        }
//                    }
//                } else {
//                    int jtTop = highLightStyle == VIEWSTYLE_CIRCLE ? top - jtDownLeft.getHeight()-radius/3 -highLisghtPadding- margin : top - jtDownLeft.getHeight() - margin-highLisghtPadding;
//                    if (right < screenW / 2 || (screenW / 2 - left > right - screenW / 2)) {
//                        //canvas.drawBitmap(jtDownLeft, left + vWidth / 2, jtTop, null);
//                        if (tipBitmaps.get(i) != null) {
//                            canvas.drawBitmap(tipBitmaps.get(i), left + vWidth / 2, jtTop - tipBitmaps.get(i).getHeight(), null);
//                        }
//                    } else {
//                        //canvas.drawBitmap(jtDownRight, left + vWidth / 2 - 100 - margin, jtTop, null);
//                        if (tipBitmaps.get(i) != null) {
//                            canvas.drawBitmap(tipBitmaps.get(i), left + vWidth / 2 - 100 - tipBitmaps.get(i).getWidth() / 2 - margin, jtTop - tipBitmaps.get(i).getHeight(), null);
//                        }
//                    }
//                }
                
            }
        }
        //无高亮控件，只是显示提示图片（仅能提示一张）
        if (tipBitmaps.size() > 0) {
        	okBtnX = (screenW - tipBitmaps.get(0).getWidth()) / 2;
            okBtnY = screenH - tipBitmaps.get(0).getHeight()-400;
            canvas.drawBitmap(tipBitmaps.get(0), okBtnX, okBtnY, null);
        }
        
        if(mText!=null){
            Paint paint = new Paint();
            paint.setTextSize(60);
            paint.setColor(Color.parseColor("#ffffff"));
            Bitmap bitmap = Bitmap.createBitmap(screenW, screenH, Bitmap.Config.RGB_565);
            //bitmap.eraseColor(Color.argb(0,0,0,0)); 
            Canvas canvas1 = new Canvas(fgBitmap);
            // 绘制前景画布颜色
            canvas1.drawColor(0x00ffffff);
            int width = (int) paint.measureText(mText);
            canvas1.drawText(mText, (screenW-width)/2, okBtnY-200, paint);
        }
    }

 
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP://
            	if(event.getX()>okBtnX && event.getX()<okBtnX+tipBitmaps.get(0).getWidth()
            			&&event.getY()>okBtnY && event.getY()<okBtnY + tipBitmaps.get(0).getHeight()){
            		this.setVisibility(View.GONE);
                    //移除view
                    if (rootView != null) {
                        ((ViewGroup) rootView).removeView(this);
                    }
                    //返回监听
                    if (this.onDismissListener != null) {
                        onDismissListener.onDismiss();
                    }
                    return true;
            	}
                if (touchOutsideCancel) {
                    this.setVisibility(View.GONE);
                    //移除view
                    if (rootView != null) {
                        ((ViewGroup) rootView).removeView(this);
                    }
                    //返回监听
                    if (this.onDismissListener != null) {
                        onDismissListener.onDismiss();
                    }
                    return true;
                }
                break;
        }
        return true;
    }

    public interface OnDismissListener {
        public void onDismiss();
    }

    /********************builder模式设置属性******************************/

    /**
     * 绘制前景画布颜色
     *
     * @param bgColor
     */
    public HighLightGuideView setMaskColor(int bgColor) {
        try {
            this.maskColor = getResources().getColor(bgColor);
            // 重新绘制前景画布
            mCanvas.drawColor(maskColor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 设置高亮显示类型
     *
     * @param style
     */
//    public HighLightGuideView setHighLightStyle(int style) {
//        this.highLightStyle = style;
//        return this;
//    }
    

    /**
     * 设置高亮画笔类型
     *
     * @param maskblurstyle
     */
    public HighLightGuideView setMaskblurstyle(int maskblurstyle) {
        this.maskblurstyle = maskblurstyle;
        return this;
    }

    /**
     * 设置需要高亮的View和提示的图片
     * @param targetView
     * @param res
     * @param radiusDiff :如果是圆，radiusDiff是倍数关系，如果是椭圆或者矩形:是加减关系,
     * @param style
     * @return
     */
    public HighLightGuideView addHighLightGuidView(View targetView, int res,float radiusDiff, int style) {
        try {
            targetViews.add(targetView);
            tipBitmaps.add(BitmapFactory.decodeResource(getResources(), res));
            targetRadius.add(radiusDiff);
            targetStyle.add(style);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }
    
    /**
     * 设置不需要高亮的View，只是提示图片
     *
     * @param res
     */
    public HighLightGuideView addNoHighLightGuidView(int res) {
        try {
            tipBitmaps.add(BitmapFactory.decodeResource(getResources(), res));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 设置外部是否关闭，默认关闭
     *
     * @param cancel
     */
    public HighLightGuideView setTouchOutsideDismiss(boolean cancel) {
        this.touchOutsideCancel = cancel;
        return this;
    }

    /**
     * 设置额外的边框宽度
     *
     * @param borderWidth
     */
    public HighLightGuideView setBorderWidth(int borderWidth) {
        this.borderWitdh = borderWidth;
        return this;
    }

    /**
     * 设置状态栏高度 默认是减去了一个状态栏高度 如果主题设置android:windowTranslucentStatus=true
     * 需要设置状态栏高度为0
     *
     * @param highLisghtPadding
     */
    public HighLightGuideView setHighLisghtPadding(int highLisghtPadding) {
        this.highLisghtPadding = highLisghtPadding;
        return this;
    }

    /**
     * 设置关闭监听
     *
     * @param listener
     */
    public HighLightGuideView setOnDismissListener(OnDismissListener listener) {
        this.onDismissListener = listener;
        return this;
    }

    /**
     * 清空画布
     */
    public HighLightGuideView clearBg() {
        if (mCanvas != null) {
            Paint paint = new Paint();
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            mCanvas.drawPaint(paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        }
        // 将其注入画布
        mCanvas = new Canvas(fgBitmap);
        // 绘制前景画布
        mCanvas.drawColor(maskColor);
        return this;
    }

    /**
     * 显示
     */
    public void show() {
        if (rootView != null) {
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            ((ViewGroup) rootView).addView(this, ((ViewGroup) rootView).getChildCount(), lp);
        }
    }
    
    public HighLightGuideView setText(String text){
    	this.mText = text;
    	return this;
    }

}
