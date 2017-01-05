package com.lefu.es.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {


	/**
     * 日期long格式化为yyyy/MM/dd HH:mm
     */
    public static String formatDate(long date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return dateFormat.format(new Date(date));
    }
  
    public static String getYear(long date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy");
        return dateFormat.format(new Date(date));
    }
    
    public static String getMonth(long date) {
        DateFormat dateFormat = new SimpleDateFormat("MM");
        return dateFormat.format(new Date(date));
    }
    
    public static String getDay(long date) {
        DateFormat dateFormat = new SimpleDateFormat("dd");
        return dateFormat.format(new Date(date));
    }
    
    public static String getHour(long date) {
        DateFormat dateFormat = new SimpleDateFormat("hh");
        return dateFormat.format(new Date(date));
    }
    
    public static String getMinute(long date) {
        DateFormat dateFormat = new SimpleDateFormat("mm");
        return dateFormat.format(new Date(date));
    }
    
    public static String getSecond(long date) {
        DateFormat dateFormat = new SimpleDateFormat("ss");
        return dateFormat.format(new Date(date));
    }
}
