/**
 * Copyright (C) 2009 - 2013 SC 4ViewSoft SRL
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.achartengine.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;

import com.lefu.es.constant.UtilConstants;

import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * The time chart rendering class.
 */
public class TimeChart extends LineChart {
    /** The constant to identify this chart type. */
    public static final String TYPE = "Time";
    /** The number of milliseconds in a day. */
    public static final long DAY = 24 * 60 * 60 * 1000;
    /** The date format pattern to be used in formatting the X axis labels. */
    private String mDateFormat;
    /** The starting point for labels. */
    private Double mStartPoint;
    private double[] panLs;

    private DateChangeCallback mDateChangeCallback;
    private Handler handler;

    TimeChart() {
    }

    /**
     * Builds a new time chart instance.
     *
     * @param dataset the multiple series dataset
     * @param renderer the multiple series renderer
     */
    public TimeChart(Context context, boolean isWeight, XYMultipleSeriesDataset dataset, XYMultipleSeriesRenderer renderer, DateChangeCallback callback) {
        super(context,isWeight,dataset, renderer);
        mDateChangeCallback=callback;
        handler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mDateChangeCallback.dateChangeCallback(msg.what);
            }

        };
        mDateChangeCallback=callback;
        panLs = renderer.getPanLimits();
    }

    /**
     * Returns the date format pattern to be used for formatting the X axis
     * labels.
     *
     * @return the date format pattern for the X axis labels
     */
    public String getDateFormat() {
        return mDateFormat;
    }

    /**
     * Sets the date format pattern to be used for formatting the X axis labels.
     *
     * @param format the date format pattern for the X axis labels. If null, an
     *          appropriate default format will be used.
     */
    public void setDateFormat(String format) {
        mDateFormat = format;
    }

    /**
     * The graphical representation of the labels on the X axis.
     *
     * @param xLabels the X labels values
     * @param xTextLabelLocations the X text label locations
     * @param canvas the canvas to paint to
     * @param paint the paint to be used for drawing
     * @param left the left value of the labels area
     * @param top the top value of the labels area
     * @param bottom the bottom value of the labels area
     * @param xPixelsPerUnit the amount of pixels per one unit in the chart labels
     * @param minX the minimum value on the X axis in the chart
     * @param maxX the maximum value on the X axis in the chart
     */
    @Override
    protected void drawXLabels(List<Double> xLabels, Double[] xTextLabelLocations, Canvas canvas,
                               Paint paint, int left, int top, int bottom, double xPixelsPerUnit, double minX, double maxX) {
        int length = xLabels.size();
        if (length > 0) {
            boolean showLabels = mRenderer.isShowLabels();
            boolean showGridY = mRenderer.isShowGridY();
            double dS=xLabels.get(0);

            double dE=xLabels.get(0);
            if(xLabels.size()>1){
                dE=xLabels.get(1);
            }

            // Log.e("test", ">>>>>>>>"+dS+" - "+dE);
            DateFormat format = getDateFormat(dS, dE);
            for (int i = 0; i < length; i++) {
                long label = Math.round(xLabels.get(i));
                float xLabel = (float) (left + xPixelsPerUnit * (label - minX));
                if (showLabels) {
                    paint.setColor(mRenderer.getXLabelsColor());
                    canvas.drawLine(xLabel, bottom, xLabel, bottom + mRenderer.getLabelsTextSize() / 3, paint);
                    if(i==2){
                        if(mRenderer.getXTitle().equals("Year")){
                            label =Math.round(xLabels.get(1))+DAY*365;
                        }
                    }if(length==2&&i==1){
                        if(mRenderer.getXTitle().equals("Year")){
                            label =Math.round(xLabels.get(0))+DAY*365;
                        }
                    }

                    drawText(canvas, format.format(new Date(label)), xLabel,
                            bottom + mRenderer.getLabelsTextSize() * 4 / 3 + mRenderer.getXLabelsPadding(), paint, mRenderer.getXLabelsAngle());
                }
                if (showGridY) {
                    paint.setColor(mRenderer.getGridColor(0));
                    canvas.drawLine(xLabel, bottom, xLabel, top, paint);
                }
            }
        }
        drawXTextLabels(xTextLabelLocations, canvas, paint, true, left, top, bottom, xPixelsPerUnit,
                minX, maxX);
    }

    /**
     * Returns the date format pattern to be used, based on the date range.
     *
     * @param start the start date in milliseconds
     * @param end the end date in milliseconds
     * @return the date format
     */
    private DateFormat getDateFormat(double start, double end) {

        if (mDateFormat != null) {
            SimpleDateFormat format = null;
            try {
                format = new SimpleDateFormat(mDateFormat);
                return format;
            } catch (Exception e) {
                // do nothing here
            }
        }

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        double diff = end - start;

   /* if (diff > DAY && diff < 5 * DAY) {
      format = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT);
    } else if (diff < DAY) {
      format = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT);
    }else if(diff > 30){
    	format = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.FULL, SimpleDateFormat.SHORT);
    }*/
        //yyyy-mm-dd HH:MM

        //Log.e("test", ":"+panLs[0]+"_"+panLs[1]+"_"+panLs[2]+"_"+panLs[3]);
        double[] panL=panLs;
        if(diff<=(1000*60*15)){
            handler.sendEmptyMessage(0);
            Locale local = Locale.getDefault();
            if(local.getCountry().equals(Locale.TAIWAN.getCountry()) || local.getCountry().equals(Locale.CHINA.getCountry()) || UtilConstants.SELECT_LANGUAGE == 2){
                mRenderer.setXTitle(" 時 ");
            }else{
                mRenderer.setXTitle("Hour");
            }
            //format = new SimpleDateFormat("HH:mm");
            format = new SimpleDateFormat("HH");
        }else if(diff<DAY *0.5){
            handler.sendEmptyMessage(1);
    	/*panL[0]=panL[0]-(1000*60*60*12);
    	panL[1]=panL[0]+(1000*60*60*12);
    	getRenderer().setPanLimits(panL);*/
            Locale local = Locale.getDefault();
            if(local.getCountry().equals(Locale.TAIWAN.getCountry()) || local.getCountry().equals(Locale.CHINA.getCountry()) || UtilConstants.SELECT_LANGUAGE == 2){
                mRenderer.setXTitle(" 時 ");
            }else{
                mRenderer.setXTitle("Hour");
            }
            //format = new SimpleDateFormat("HH:mm");
            format = new SimpleDateFormat("HH");
            //format = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT);
        }else if(diff < DAY){
            handler.sendEmptyMessage(2);
    	/*panL[0]=panL[0]-(1000*60*60*24);
    	panL[1]=panL[0]+(1000*60*60*24);
    	getRenderer().setPanLimits(panL);*/
            Locale local = Locale.getDefault();
            if(local.getCountry().equals(Locale.TAIWAN.getCountry()) || local.getCountry().equals(Locale.CHINA.getCountry()) || UtilConstants.SELECT_LANGUAGE == 2){
                mRenderer.setXTitle(" 時 ");
            }else{
                mRenderer.setXTitle("Hour");
            }
            //format = new SimpleDateFormat("HH:mm");
            format = new SimpleDateFormat("HH");
            //format = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT);
        }else if(diff < 30 * DAY){
            handler.sendEmptyMessage(3);
    	/*panL[0]=panL[0]-(1000*60*60*24*5);
    	panL[1]=panL[0]+(1000*60*60*24*5);
    	getRenderer().setPanLimits(panL);*/
            Locale local = Locale.getDefault();
            if(local.getCountry().equals(Locale.TAIWAN.getCountry()) || local.getCountry().equals(Locale.CHINA.getCountry()) || UtilConstants.SELECT_LANGUAGE == 2){
                mRenderer.setXTitle(" 日 ");
            }else{
                mRenderer.setXTitle("DAY");
            }
            format = new SimpleDateFormat("dd");
            //format = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT);
        }else if(diff < 365 * DAY ){
            handler.sendEmptyMessage(4);
    	/*panL[0]=panL[0]-(1000*60*60*24*30);
    	panL[1]=panL[0]+(1000*60*60*24*30);
    	getRenderer().setPanLimits(panL);*/
            Locale local = Locale.getDefault();
            if(local.getCountry().equals(Locale.TAIWAN.getCountry()) || local.getCountry().equals(Locale.CHINA.getCountry()) || UtilConstants.SELECT_LANGUAGE == 2){
                mRenderer.setXTitle(" 月 ");
            }else{
                mRenderer.setXTitle("Month");
            }
            format = new SimpleDateFormat("MM");
            //format = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT);
        }else if(diff < 400 * DAY ){
            handler.sendEmptyMessage(4);
    	/*panL[0]=panL[0]-(1000*60*60*24*30);
    	panL[1]=panL[0]+(1000*60*60*24*30);
    	getRenderer().setPanLimits(panL);*/
            Locale local = Locale.getDefault();
            if(local.getCountry().equals(Locale.TAIWAN.getCountry()) || local.getCountry().equals(Locale.CHINA.getCountry()) || UtilConstants.SELECT_LANGUAGE == 2){
                mRenderer.setXTitle(" 月 ");
            }else{
                mRenderer.setXTitle("Month");
            }
            format = new SimpleDateFormat("MM");
            //format = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT);
        }else{
            handler.sendEmptyMessage(5);
            Locale local = Locale.getDefault();
            if(local.getCountry().equals(Locale.TAIWAN.getCountry()) || local.getCountry().equals(Locale.CHINA.getCountry()) || UtilConstants.SELECT_LANGUAGE == 2){
                mRenderer.setXTitle(" 年 ");
            }else{
                mRenderer.setXTitle("Year");
            }

            format = new SimpleDateFormat("yyyy");
            //format = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT);
        }



        return format;
    }

    /**
     * Returns the chart type identifier.
     *
     * @return the chart type
     */
    public String getChartType() {
        return TYPE;
    }

    @Override
    protected List<Double> getXLabels(double min, double max, int count) {
        final List<Double> result = new ArrayList<Double>();
        if (!mRenderer.isXRoundedLabels()) {
            if (mDataset.getSeriesCount() > 0) {
                XYSeries series = mDataset.getSeriesAt(0);
                int length = series.getItemCount();
                int intervalLength = 0;
                int startIndex = -1;
                for (int i = 0; i < length; i++) {
                    double value = series.getX(i);
                    if (min <= value && value <= max) {
                        intervalLength++;
                        if (startIndex < 0) {
                            startIndex = i;
                        }
                    }
                }
                if (intervalLength < count) {
                    for (int i = startIndex; i < startIndex + intervalLength; i++) {
                        //Log.e("test", "# add 1");
                        result.add(series.getX(i));
                    }
                } else {
                    float step = (float) intervalLength / count;
                    int intervalCount = 0;
                    for (int i = 0; i < length && intervalCount < count; i++) {
                        double value = series.getX(Math.round(i * step));
                        if (min <= value && value <= max) {
                            //Log.e("test", "# add 2");
                            result.add(value);
                            intervalCount++;
                        }
                    }
                }
                // Log.e("test", "$ 1");
                return result;
            } else {
                return super.getXLabels(min, max, count);
            }
        }
        if (mStartPoint == null) {
            mStartPoint = min - (min % DAY) + DAY + new Date(Math.round(min)).getTimezoneOffset() * 60
                    * 1000;
        }
        if (count > 25) {
            count = 25;
        }


        final double cycleMath = (max - min) / count;
        if (cycleMath <= 0) {
            //Log.e("test", "$ 2");
            return result;
        }
        double cycle = DAY;

        if (cycleMath <= DAY) {
            boolean isR=false;
            while (cycleMath < cycle / 2) {
                if(cycle<=1000*60*45){
                    cycle= cycle / 3;
                }else{
                    cycle = cycle / 2;
                }
            }

        } else {
            while (cycleMath > cycle) {
                if(cycle <= DAY * 30){
                    cycle = cycle * 2;
                }else{
                    cycle = cycle * 2;
                }
            }
        }

        //Log.e("test", "$$ " +cycle);
        double val = mStartPoint - Math.floor((mStartPoint - min) / cycle) * cycle;
        int i = 0;
        while (val < max && i++ <= count) {
            // Log.e("test", "# add 3");
            result.add(val);
            val += cycle;
        }
        // Log.e("test", "$ 3");
        return result;
    }

    public interface DateChangeCallback {
        public void dateChangeCallback(int level);
    }

}
