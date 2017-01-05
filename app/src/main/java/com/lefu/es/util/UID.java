package com.lefu.es.util;

import android.annotation.SuppressLint;
import java.util.Date;

@SuppressLint({"SimpleDateFormat", "DefaultLocale"})
public class UID {
	 private static Date date = new Date();
	 private static StringBuilder buf = new StringBuilder();
	 private static int seq = 0;
	 private static final int ROTATION = 99999;
	  
	 public static synchronized String next(){
	    if (seq > ROTATION) seq = 0;
	    buf.delete(0, buf.length());
	    date.setTime(System.currentTimeMillis());
	    return String.format("%1$tY%1$tm%1$td%1$tk%1$tM%1$tS", date);
	  }
	  
	  public static String getRegisterID(){
		  java.text.SimpleDateFormat sdf=new java.text.SimpleDateFormat("yyyyMMddHHmmssSSS");
		  return "register"+sdf.format(new Date());
	  }
	  
	  public static String geUserID(){
		  java.text.SimpleDateFormat sdf=new java.text.SimpleDateFormat("yyyyMMddHHmmssSSS");
		  return "user"+sdf.format(new Date());
	  }
	  
	  public static String getImage(){
		  java.text.SimpleDateFormat sdf=new java.text.SimpleDateFormat("yyyyMMddHHmmssSSS");
		  return sdf.format(new Date())+"_photo.jpg";
	  }
}
