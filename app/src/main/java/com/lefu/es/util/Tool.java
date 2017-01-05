package com.lefu.es.util;


import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.os.Environment;

import com.lefu.es.constant.UtilConstants;

public class Tool {
	
	public static boolean isDigitsOnly(String str){
		Boolean strResult = str.matches("-?[0-9]+.*[0-9]*");
	    
	    return strResult;
	}
	
	/**检查参数*/
	public static boolean checkParameter(String str) {
		int flag = 0;
		flag += str.indexOf("'") + 1;
		flag += str.indexOf("\"") + 1;
		flag += str.indexOf(";") + 1;
		flag += str.indexOf("1=1") + 1;
		flag += str.indexOf("|") + 1;
		flag += str.indexOf("<") + 1;
		flag += str.indexOf(">") + 1;
		flag += str.indexOf("-") + 1;
		if (flag != 0) {
			return false;
		} else {
			return true;
		}
	}
	
	/**根据生日获取年龄*/
	public static int getAgeByBirthday(Date birthday) {
		if(null==birthday){return 0;}
		Calendar cal = Calendar.getInstance();

		if (cal.before(birthday)) {
			throw new IllegalArgumentException("The birthDay is before Now.It's unbelievable!");
		}

		int yearNow = cal.get(Calendar.YEAR);
		int monthNow = cal.get(Calendar.MONTH) + 1;
		int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

		cal.setTime(birthday);
		int yearBirth = cal.get(Calendar.YEAR);
		int monthBirth = cal.get(Calendar.MONTH) + 1;
		int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

		int age = yearNow - yearBirth;

		if(monthBirth>monthNow || (monthBirth==monthNow && dayOfMonthBirth>dayOfMonthNow)){
			age--;
		}else if((monthBirth==monthNow && dayOfMonthBirth<dayOfMonthNow) || monthBirth< monthNow){
			age++;
		}

		return age;
	}
	
	/**字符串转date*/
	@SuppressLint("SimpleDateFormat")
	public static Date StringToDate(String dateStr,String formatStr){
		SimpleDateFormat dd=new SimpleDateFormat(formatStr);
		Date date=null;
		try {
			date = dd.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**判断是否有SD卡*/
	public static boolean isHasSdcard() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**获取图片随机文件名*/
	@SuppressLint("SimpleDateFormat")
	public static String randomImage() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
		StringBuffer sb = new StringBuffer();
		sb.append(UtilConstants.SDCARD_ROOT_PATH);
		sb.append(UtilConstants.APPLICATION_PATH);
		sb.append(UtilConstants.IMAGE_CACHE_PATH);
		sb.append("/" + dateFormat.format(date) + ".jpg");
		return sb.toString();
	}
	
	/**
     * 创建目录
     *
     * @param path
     */
    public static void createDirs(File path) {
        if (path != null && !path.exists()) {
            path.mkdirs();
        }
    }
    
    public static void close(Writer writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (Exception e) {
                //LogUtils.i(TAG, "close exception: " + e.toString());
            }
        }
    }
    
    public static void close(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (Exception e) {
                //LogUtils.i(TAG, "close exception: " + e.toString());
            }
        }
    }
}
