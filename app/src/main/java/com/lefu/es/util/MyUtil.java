package com.lefu.es.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.text.TextUtils;
import android.util.Log;

import com.holtek.libHTBodyfat.HTBodyfatGeneral;
import com.holtek.libHTBodyfat.HTDataType;
import com.lefu.es.constant.UtilConstants;
import com.lefu.es.entity.Records;
import com.lefu.es.entity.UserModel;
import com.lefu.es.service.RecordService;

/**
 * 工具类
 * 
 * @author Leon 2015-11-19
 */
public class MyUtil {
	private static final String TAG = "MyUtil";
	private static Records lastRecod;

	public static String getCurrentDate(){
		StringBuffer sb = new StringBuffer();
		long date = System.currentTimeMillis();
		sb.append("f1");
		sb.append(Integer.toHexString(Integer.parseInt(DateUtil.getYear(date).substring(0,2))));
		int year2 = Integer.parseInt(DateUtil.getYear(date).substring(2,4));
		if(year2<10){
			sb.append("0");
		}
		sb.append(Integer.toHexString(year2));
		
		int month = Integer.parseInt(DateUtil.getMonth(date));
		if(month<10){
			sb.append("0");
		}
		sb.append(Integer.toHexString(month));
		int day = Integer.parseInt(DateUtil.getDay(date));
		if(day<10){
			sb.append("0");
		}
		sb.append(Integer.toHexString(day));
		int hour = Integer.parseInt(DateUtil.getHour(date));
		if(hour<10){
			sb.append("0");
		}
		sb.append(Integer.toHexString(hour));
		int minute = Integer.parseInt(DateUtil.getMinute(date));
		if(minute<10){
			sb.append("0");
		}
		sb.append(Integer.toHexString(minute));
		int second = Integer.parseInt(DateUtil.getSecond(date));
		if(second<10){
			sb.append("0");
		}
		sb.append(Integer.toHexString(second));
		System.out.println("sync current date: "+sb.toString());
//		sb.append(Integer.toHexString(20));
//		sb.append(Integer.toHexString(16));
//		sb.append(Integer.toHexString(03));
//		sb.append(Integer.toHexString(01));
//		sb.append(Integer.toHexString(12));
//		sb.append(Integer.toHexString(37));
//		sb.append(Integer.toHexString(48));
		return sb.toString();
	}
    
	/** 获取下发信息 */
	public static String getUserInfo() {
		UserModel userModel = UtilConstants.CURRENT_USER;
		if (userModel == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		sb.append(userModel.getGroup().replace("P", "0"));
		sb.append("0").append(userModel.getSex());
		sb.append("0").append(userModel.getLevel());
		int height = (int) userModel.getBheigth();
		if (height > 15) {
			sb.append(Integer.toHexString(height));
		} else {
			sb.append("0" + Integer.toHexString(height));
		}

		int age = userModel.getAgeYear();
		if (age > 15) {
			sb.append(Integer.toHexString(age));
		} else {
			sb.append("0" + Integer.toHexString(age));
		}

		if (userModel.getDanwei().equals(UtilConstants.UNIT_KG)) {
			sb.append("01");
		} else if (userModel.getDanwei().equals(UtilConstants.UNIT_LB)) {
			sb.append("02");
		} else if (userModel.getDanwei().equals(UtilConstants.UNIT_ST)) {
			sb.append("04");
		} else if (userModel.getDanwei().equals(UtilConstants.UNIT_FATLB)) {
			sb.append("02");
		} else {
			sb.append("01");
		}

		String bcc = StringUtils.getBCC(StringUtils.hexStringToByteArray(sb.toString()));
		String out = "FE" + sb.toString() + bcc;
		return out;
	}

	/** 解析报文 */
	public static Records parseMeaage(RecordService recordService, String readMessage) {
		Records recod = new Records();
		try {
			recod.setScaleType(readMessage.substring(0, 2));
			recod.setUgroup("P" + StringUtils.hexToTen(readMessage.substring(3, 4)) + "");
			recod.setLevel(StringUtils.hexToTen(readMessage.substring(2, 3)) + "");
			String biary = StringUtils.hexToBirary(readMessage.substring(4, 6));
			if (biary.length() < 8) {
				for (int i = biary.length(); i < 8; i++) {
					biary = "0" + biary;
				}
			}
			

			recod.setSex(biary.substring(0, 1));
			recod.setsAge(StringUtils.binaryToTen(biary.substring(1)) + "");
			recod.setsHeight(StringUtils.hexToTen(readMessage.substring(6, 8)) + "");
			int weight = StringUtils.hexToTen(readMessage.substring(8, 12));
			recod.setSweight((weight * 0.1) + "");
			String unit = readMessage.substring(12, 14);
			recod.setSbodyfat((StringUtils.hexToTen(readMessage.substring(12, 16)) * 0.1) + "");
			recod.setSbone((StringUtils.hexToTen(readMessage.substring(16, 18)) * 0.1) + "");
			//recod.setSbone(getBone(StringUtils.hexToTen(readMessage.substring(16, 18)), StringUtils.hexToTen(readMessage.substring(8, 12))));
			recod.setSmuscle((StringUtils.hexToTen(readMessage.substring(18, 22)) * 0.1) + "");
			recod.setSvisceralfat(StringUtils.hexToTen(readMessage.substring(22, 24)) + "");
			recod.setSbodywater((StringUtils.hexToTen(readMessage.substring(24, 28)) * 0.1) + "");
			recod.setSbmr((StringUtils.hexToTen(readMessage.substring(28, 32)) * 1) + "");

			if (readMessage.length() > 32)
				recod.setBodyAge(StringUtils.hexToTen(readMessage.substring(32, 34)) * 1);

			recod.setRbmr(StringUtils.isNumber(recod.getSbmr()) == true ? (Integer.parseInt(recod.getSbmr()) / 1) : 0);
			recod.setRbodyfat(StringUtils.isNumber(recod.getSbodyfat()) == true ? Float.parseFloat(recod.getSbodyfat()) : 0);
			recod.setRbodywater(StringUtils.isNumber(recod.getSbodywater()) == true ? Float.parseFloat(recod.getSbodywater()) : 0);
			recod.setRbone(StringUtils.isNumber(recod.getSbone()) == true ? Float.parseFloat(recod.getSbone()) : 0);
			recod.setRmuscle(StringUtils.isNumber(recod.getSmuscle()) == true ? Float.parseFloat(recod.getSmuscle()) : 0);
			recod.setRvisceralfat(StringUtils.isNumber(recod.getSvisceralfat()) == true ? (Integer.parseInt(recod.getSvisceralfat()) / 1) : 0);
			recod.setRweight(StringUtils.isNumber(recod.getSweight()) == true ? Float.parseFloat(recod.getSweight()) : 0);
			if (UtilConstants.BABY_SCALE.equals(recod.getScaleType())) {
				if(unit.equals("01")){
					if(null!=UtilConstants.CURRENT_USER)UtilConstants.CURRENT_USER.setDanwei(UtilConstants.UNIT_FATLB);
				}else {
					if(null!=UtilConstants.CURRENT_USER)UtilConstants.CURRENT_USER.setDanwei(UtilConstants.UNIT_KG);
				} 
				recod.setUnitType(StringUtils.hexToTen(unit));
				recod.setRweight(UtilTooth.myround2((float) (recod.getRweight() * 0.1)));
			} else if (UtilConstants.KITCHEN_SCALE.equals(recod.getScaleType())) { //单位是g
				recod.setSweight(weight + "");
				recod.setRweight(Float.parseFloat(recod.getSweight()));
				recod.setUnitType(StringUtils.hexToTen(readMessage.substring(12, 14)));
			} else {
				recod.setRweight(UtilTooth.myround((float) (recod.getRweight() * 0.1)));
			}
			if (UtilConstants.KITCHEN_SCALE.equals(recod.getScaleType())) {
				recod.setSbmi("0.0");
				recod.setRbmi(0.0f);
			}else{
				if (StringUtils.isNumber(recod.getsHeight()) == true && !"0".equals(recod.getsHeight())) {
					recod.setSbmi(UtilTooth.countBMI(recod.getRweight(), (Float.parseFloat(recod.getsHeight())) / 100));
				}
				if (StringUtils.isNumber(recod.getSbmi())) {
					recod.setRbmi(UtilTooth.myround(Float.parseFloat(recod.getSbmi())));
				}
			}
			if (!UtilConstants.KITCHEN_SCALE.equals(recod.getScaleType())) {
				lastRecod = recordService.findLastRecordsByScaleType(recod.getScaleType(), recod.getUgroup());
			}
			if (null != lastRecod) {
				recod.setCompareRecord((UtilTooth.myround(recod.getRweight() - lastRecod.getRweight())) + "");
			} else {
				recod.setCompareRecord("0.0");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recod;
	}
	
	public static Records parseZuKangMeaage(RecordService recordService ,String readMessage,UserModel user) {
		Log.e("test", "解析数据：" + readMessage);
		Records recod = null;
		if(TextUtils.isEmpty(readMessage) && readMessage.length()<40){
			return null;
		}
		int age = 0;
        double height = 0;
        double weight = 0;
        int impedance = 0;
        int sex = 0;
        int level = 0;

		try {
			recod = new Records();
			recod.setUseId(user.getId());
			recod.setScaleType(user.getScaleType());
			recod.setUgroup(user.getGroup());
			recod.setLevel(user.getLevel());

			weight = StringUtils.hexToTen(readMessage.substring(24, 26)+readMessage.substring(22, 24))*0.01d;
			height = user.getBheigth();
			if(!TextUtils.isEmpty(user.getSex()))sex=Integer.parseInt(user.getSex());
			if(!TextUtils.isEmpty(user.getLevel()))level=Integer.parseInt(user.getLevel());
			impedance = StringUtils.hexToTen(readMessage.substring(34, 36)+readMessage.substring(32, 34) + readMessage.substring(30, 32));
			age = user.getAgeYear();
			
			recod.setSex(sex+"");
			recod.setsAge(age + "");
			recod.setsHeight(String.valueOf(height));
			recod.setSweight(weight+ "");
			recod.setRweight((float)weight);
			recod.setRecordTime(UtilTooth.dateTimeChange(new Date()));
			HTBodyfatGeneral bodyfat = new HTBodyfatGeneral(weight,height,sex, age, level, impedance);
			
			//Log.e(TAG, "输入参数==>体重："+weight+"  身高:"+height+"  性别:"+sex+"  年龄:"+age+"  类型:"+level+"  阻抗:"+impedance);   
			
			//Log.e(TAG, "计算结果==>" + bodyfat.getBodyfatParameters()+"====阻抗系数++>"+impedance);   
	    	if(bodyfat.getBodyfatParameters() == HTDataType.ErrorNone){
		        //正常计算
	    		recod.setRbmi(UtilTooth.keep1Point3(bodyfat.BMI*0.1f));
		    	recod.setRbmr((int)bodyfat.BMR);
				recod.setRbodyfat(UtilTooth.keep1Point3(bodyfat.bodyfatPercentage));
				recod.setRbodywater(UtilTooth.keep1Point3(bodyfat.waterPercentage));
				recod.setRbone(UtilTooth.keep1Point3(bodyfat.boneKg));
				recod.setRmuscle(UtilTooth.keep1Point3(bodyfat.muscleKg));
				recod.setRvisceralfat((int) bodyfat.VFAL);
				
				recod.setSbmi(UtilTooth.onePoint(recod.getRbmi()));
				recod.setSbodyfat(UtilTooth.onePoint(recod.getRbodyfat()));
				recod.setSbone(UtilTooth.onePoint(recod.getRbone()));
				recod.setSmuscle(UtilTooth.onePoint(recod.getRmuscle()));
				recod.setSvisceralfat(UtilTooth.onePoint(recod.getRvisceralfat()));
				recod.setSbodywater(UtilTooth.onePoint(recod.getRbodywater()));
				recod.setSbmr(UtilTooth.onePoint(recod.getRbmr()));
				float bmi = UtilTooth.countBMI2(recod.getRweight(), (user.getBheigth() / 100));
				recod.setBodyAge(UtilTooth.getPhysicAge(bmi,user.getAgeYear()));
				Log.i(TAG, "阻抗:" + bodyfat.ZTwoLegs +
                        "Ω  BMI:" + String.format("%.1f",bodyfat.BMI) +
                        "  BMR:" + (int) bodyfat.BMR +
                        "  内脏脂肪:" + (int) bodyfat.VFAL +
                        "  骨量:" + String.format("%.1fkg",bodyfat.boneKg) +
                        "  脂肪率:" +String.format("%.1f%%",bodyfat.bodyfatPercentage) +
                        "  水分:" + String.format("%.1f%%",bodyfat.waterPercentage) +
                        "  肌肉:" + String.format("%.1fkg",bodyfat.muscleKg) + "\r\n");
		    }else {
		    	recod.setRbmi(UtilTooth.myround(UtilTooth.countBMI2(recod.getRweight(), (Float.parseFloat(recod.getsHeight())) / 100)));
		    	recod.setSbmi(UtilTooth.onePoint(recod.getRbmi()));
		    	Log.e(TAG, "输入数据有误==>" + bodyfat.toString());   
		    }
			lastRecod = recordService.findLastRecordsByScaleType(
					recod.getScaleType(), recod.getUgroup());
			if (null != lastRecod) {
				recod.setCompareRecord((UtilTooth.myround(recod
						.getRweight() - lastRecod.getRweight()))
						+ "");
			} else {
				recod.setCompareRecord("0.0");
			}
			//recordService.save(recod);
		} catch (Exception e) {
			Log.e(TAG, "解析数据异常==>" + e.getMessage());   
		}
		return recod;
	}

	/** 获取骨骼 */
	private static String getBone(int bone, int weight) {
		double bo = ((bone * 0.1) / (weight * 0.1) * 100);
		bo = bo * 0.01 * (weight * 0.1);
		return UtilTooth.myroundString(bo + "");
	}
	
	
}
