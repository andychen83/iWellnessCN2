package com.lefu.es.db;

import java.util.Date;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.holtek.libHTBodyfat.HTBodyfatGeneral;
import com.holtek.libHTBodyfat.HTDataType;
import com.lefu.es.application.IwellnessApplication;
import com.lefu.es.constant.UtilConstants;
import com.lefu.es.entity.NutrientBo;
import com.lefu.es.entity.Records;
import com.lefu.es.entity.UserModel;
import com.lefu.es.service.RecordService;
import com.lefu.es.service.UserService;
import com.lefu.es.util.MyUtil;
import com.lefu.es.util.StringUtils;
import com.lefu.es.util.Tool;
import com.lefu.es.util.UtilTooth;

/**
 * 测量记录
 * @author lfl
 */
public class RecordDao {
	private static final String TAG = "RecordDao";
	private static Records lastRecod;
	/**处理数据*/
	public static void dueDate(RecordService recordService,String readMessage) {
		Records recod = MyUtil.parseMeaage(recordService, readMessage);
		handleData(recordService,recod,readMessage);
	}

	public static void handleData2(RecordService recordService,Records recod) {
		uservice = new UserService(IwellnessApplication.app);
		try {
			//UserModel user = UtilConstants.CURRENT_USER;
//				UserModel um = new UserModel();
//				um.setId(user.getId());
//				um.setSex(recod.getSex());
//				um.setBheigth(Float.parseFloat(recod.getsHeight()));
//				um.setLevel(recod.getLevel());
//				um.setAgeYear(Integer.parseInt(recod.getsAge()));
//				um.setDanwei(user.getDanwei());
			recod.setUseId(UtilConstants.CURRENT_USER.getId());
			recod.setUgroup(UtilConstants.CURRENT_USER.getGroup());
			recod.setRbmr(StringUtils.isNumber(recod.getSbmr()) == true ? (Float.parseFloat(recod.getSbmr()) / 1) : 0);
			recod.setRbodyfat(StringUtils.isNumber(recod.getSbodyfat()) == true ? Float.parseFloat(recod.getSbodyfat()) : 0);
			recod.setRbodywater(StringUtils.isNumber(recod.getSbodywater()) == true ? Float.parseFloat(recod.getSbodywater()) : 0);
			recod.setRbone(StringUtils.isNumber(recod.getSbone()) == true ? Float.parseFloat(recod.getSbone()) : 0);
			recod.setRmuscle(StringUtils.isNumber(recod.getSmuscle()) == true ? Float.parseFloat(recod.getSmuscle()) : 0);
			recod.setRvisceralfat(StringUtils.isNumber(recod.getSvisceralfat()) == true ? (Float.parseFloat(recod.getSvisceralfat()) / 1) : 0);
			recod.setRweight(StringUtils.isNumber(recod.getSweight()) == true ? Float.parseFloat(recod.getSweight()) : 0);
			recod.setRecordTime(UtilTooth.dateTimeChange(new Date()));
			if (UtilConstants.BABY_SCALE.equals(recod.getScaleType())) {
				recod.setRweight(UtilTooth.myround2((float) (recod.getRweight() * 0.1)));
			} else {
				recod.setRweight(UtilTooth.myround((float) (recod.getRweight())));
			}
			if (StringUtils.isNumber(recod.getSbmi())) {
				recod.setRbmi(UtilTooth.myround(UtilTooth.countBMI2(recod.getRweight(), (Float.parseFloat(recod.getsHeight())) / 100)));
			}
			if (StringUtils.isNumber(recod.getsHeight()) == true && !"0".equals(recod.getsHeight())) {
				recod.setSbmi(String.valueOf(recod.getRbmi()));
			}
//				if (StringUtils.isNumber(recod.getSbmi())) {
//					recod.setRbmi(UtilTooth.myround(UtilTooth.countBMI2(recod.getRweight(), (Float.parseFloat(recod.getsHeight())) / 100)));
//				}
			try {
				uservice.update(UtilConstants.CURRENT_USER);

				Records lastRecod = recordService.findLastRecordsByScaleTypeAndUser(recod.getScaleType(), String.valueOf(UtilConstants.CURRENT_USER.getId()));
				if (null != lastRecod) {
					recod.setCompareRecord((UtilTooth.myround(recod.getRweight() - lastRecod.getRweight())) + "");
				} else {
					recod.setCompareRecord("0.0");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			recordService.save(recod);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**处理数据*/
	public static void handleData(RecordService recordService,Records recod,String readMessage){
		uservice = new UserService(IwellnessApplication.app);
		if (recod.getScaleType().equalsIgnoreCase(UtilConstants.BODY_SCALE)) {
			try {
				//UserModel user = UtilConstants.CURRENT_USER;
//				UserModel um = new UserModel();
//				um.setId(user.getId());
//				um.setSex(recod.getSex());
//				um.setBheigth(Float.parseFloat(recod.getsHeight()));
//				um.setLevel(recod.getLevel());
//				um.setAgeYear(Integer.parseInt(recod.getsAge()));
//				um.setDanwei(user.getDanwei());
				recod.setUseId(UtilConstants.CURRENT_USER.getId());
				recod.setUgroup(UtilConstants.CURRENT_USER.getGroup());
				recod.setRbmr(StringUtils.isNumber(recod.getSbmr()) == true ? (Float.parseFloat(recod.getSbmr()) / 1) : 0);
				recod.setRbodyfat(StringUtils.isNumber(recod.getSbodyfat()) == true ? Float.parseFloat(recod.getSbodyfat()) : 0);
				recod.setRbodywater(StringUtils.isNumber(recod.getSbodywater()) == true ? Float.parseFloat(recod.getSbodywater()) : 0);
				recod.setRbone(StringUtils.isNumber(recod.getSbone()) == true ? Float.parseFloat(recod.getSbone()) : 0);
				recod.setRmuscle(StringUtils.isNumber(recod.getSmuscle()) == true ? Float.parseFloat(recod.getSmuscle()) : 0);
				recod.setRvisceralfat(StringUtils.isNumber(recod.getSvisceralfat()) == true ? (Float.parseFloat(recod.getSvisceralfat()) / 1) : 0);
				recod.setRweight(StringUtils.isNumber(recod.getSweight()) == true ? Float.parseFloat(recod.getSweight()) : 0);
				recod.setRecordTime(UtilTooth.dateTimeChange(new Date()));
				if (UtilConstants.BABY_SCALE.equals(recod.getScaleType())) {
					recod.setRweight(UtilTooth.myround2((float) (recod.getRweight() * 0.1)));
				} else {
					recod.setRweight(UtilTooth.myround((float) (recod.getRweight())));
				}
				if (StringUtils.isNumber(recod.getSbmi())) {
					recod.setRbmi(UtilTooth.myround(UtilTooth.countBMI2(recod.getRweight(), (Float.parseFloat(recod.getsHeight())) / 100)));
				}
				if (StringUtils.isNumber(recod.getsHeight()) == true && !"0".equals(recod.getsHeight())) {
					recod.setSbmi(String.valueOf(recod.getRbmi()));
				}
//				if (StringUtils.isNumber(recod.getSbmi())) {
//					recod.setRbmi(UtilTooth.myround(UtilTooth.countBMI2(recod.getRweight(), (Float.parseFloat(recod.getsHeight())) / 100)));
//				}
				try {
					uservice.update(UtilConstants.CURRENT_USER);
					
					Records lastRecod = recordService.findLastRecordsByScaleTypeAndUser(recod.getScaleType(), String.valueOf(UtilConstants.CURRENT_USER.getId()));
					if (null != lastRecod) {
						recod.setCompareRecord((UtilTooth.myround(recod.getRweight() - lastRecod.getRweight())) + "");
					} else {
						recod.setCompareRecord("0.0");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				recordService.save(recod);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			//UserModel user = UtilConstants.CURRENT_USER;
			recod = new Records();
			recod.setUseId(UtilConstants.CURRENT_USER.getId());
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
			recod.setSweight((StringUtils.hexToTen(readMessage.substring(8, 12)) * 0.1) + "");
			recod.setSbodyfat((StringUtils.hexToTen(readMessage.substring(12, 16)) * 0.1) + "");
			recod.setSbone((StringUtils.hexToTen(readMessage.substring(16, 18)) * 0.1) + "");
			recod.setSmuscle((StringUtils.hexToTen(readMessage.substring(18, 22)) * 0.1) + "");
			recod.setSvisceralfat(StringUtils.hexToTen(readMessage.substring(22, 24)) + "");
			recod.setSbodywater((StringUtils.hexToTen(readMessage.substring(24, 28)) * 0.1) + "");
			recod.setSbmr((StringUtils.hexToTen(readMessage.substring(28, 32)) * 1) + "");

			recod.setRbmr(StringUtils.isNumber(recod.getSbmr()) == true ? (Float.parseFloat(recod.getSbmr()) / 1) : 0);
			recod.setRbodyfat(StringUtils.isNumber(recod.getSbodyfat()) == true ? Float.parseFloat(recod.getSbodyfat()) : 0);
			recod.setRbodywater(StringUtils.isNumber(recod.getSbodywater()) == true ? Float.parseFloat(recod.getSbodywater()) : 0);
			recod.setRbone(StringUtils.isNumber(recod.getSbone()) == true ? Float.parseFloat(recod.getSbone()) : 0);
			recod.setRmuscle(StringUtils.isNumber(recod.getSmuscle()) == true ? Float.parseFloat(recod.getSmuscle()) : 0);
			recod.setRvisceralfat(StringUtils.isNumber(recod.getSvisceralfat()) == true ? (Float.parseFloat(recod.getSvisceralfat()) / 1) : 0);
			recod.setRweight(StringUtils.isNumber(recod.getSweight()) == true ? Float.parseFloat(recod.getSweight()) : 0);
			recod.setRecordTime(UtilTooth.dateTimeChange(new Date()));
			if (UtilConstants.BABY_SCALE.equals(recod.getScaleType())) {
				recod.setRweight(UtilTooth.myround2((float) (recod.getRweight() * 0.1)));
			} else {
				recod.setRweight(UtilTooth.myround((float) (recod.getRweight())));
			}
			if (StringUtils.isNumber(recod.getsHeight()) == true && !"0".equals(recod.getsHeight())) {
				recod.setSbmi(UtilTooth.countBMI(recod.getRweight(), (Float.parseFloat(recod.getsHeight())) / 100));
			}
			if (StringUtils.isNumber(recod.getSbmi())) {
				recod.setRbmi(UtilTooth.myround(UtilTooth.countBMI2(recod.getRweight(), (Float.parseFloat(recod.getsHeight())) / 100)));
			}
			if (StringUtils.isNumber(recod.getsHeight()) == true && !"0".equals(recod.getsHeight())) {
				recod.setSbmi(String.valueOf(recod.getRbmi()));
			}
			try {
				uservice.update(UtilConstants.CURRENT_USER);
				
				Records lastRecod = recordService.findLastRecordsByScaleTypeAndUser(recod.getScaleType(), String.valueOf(UtilConstants.CURRENT_USER.getId()));
				if (null != lastRecod) {
					recod.setCompareRecord((UtilTooth.myround(recod.getRweight() - lastRecod.getRweight())) + "");
				} else {
					recod.setCompareRecord("0.0");
				}
				recordService.save(recod);
			} catch (Exception e) {
				Toast.makeText(IwellnessApplication.app, "save data failed:"+e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}
	}
	
	private static UserService uservice;
	public static void dueKitchenDate(RecordService recordService,String readMessage,NutrientBo nutrient) {
		Records recod = MyUtil.parseMeaage(recordService, readMessage);
		handleKitchenData(recordService,recod,nutrient);
	}

	public static void dueKitchenDate2(RecordService recordService,Records recod,NutrientBo nutrient) {
		handleKitchenData(recordService,recod,nutrient);
	}

	public static Records handleKitchenData(RecordService recordService,Records recod,NutrientBo nutrient){
		if(null!=recod && null!=recordService){
			uservice = new UserService(IwellnessApplication.app);
			UserModel user = UtilConstants.CURRENT_USER;
			if(null!=user){
				if(recod.getUnitType()==0){
					user.setDanwei(UtilConstants.UNIT_G);
				}else if(recod.getUnitType()==1){
					user.setDanwei(UtilConstants.UNIT_LB);
				}else if(recod.getUnitType()==2){
					user.setDanwei(UtilConstants.UNIT_ML);
				}else if(recod.getUnitType()==3){
					user.setDanwei(UtilConstants.UNIT_FATLB);
				}else if(recod.getUnitType()==4){
					user.setDanwei(UtilConstants.UNIT_ML2);
				}
				recod.setUseId(user.getId());
				recod.setUgroup(user.getGroup());
			}
			recod.setScaleType(UtilConstants.KITCHEN_SCALE);
			recod.setRecordTime(UtilTooth.dateTimeChange(new Date()));
			if(null!=nutrient){
				float calcium =  0.0f;
				if(!TextUtils.isEmpty(nutrient.getNutrientVitB6()) && Tool.isDigitsOnly(nutrient.getNutrientVitB6())){
					calcium =  Float.parseFloat(nutrient.getNutrientVitB6())*recod.getRweight()*0.01f;//钙
				}
				float protein =  0.0f;
				if(!TextUtils.isEmpty(nutrient.getNutrientFiberTD()) && Tool.isDigitsOnly(nutrient.getNutrientFiberTD())){
					protein = Float.parseFloat(nutrient.getNutrientFiberTD())*recod.getRweight()*0.01f;//蛋白质
				}
				float kcal = 0.0f;
				if(!TextUtils.isEmpty(nutrient.getNutrientAsh()) && Tool.isDigitsOnly(nutrient.getNutrientAsh())){
					kcal =  Float.parseFloat(nutrient.getNutrientAsh())*recod.getRweight()*0.01f;//卡路里
				}
				float water =  0.0f;
				if(!TextUtils.isEmpty(nutrient.getNutrientCalcium()) && Tool.isDigitsOnly(nutrient.getNutrientCalcium())){
					water =  Float.parseFloat(nutrient.getNutrientCalcium())*recod.getRweight()*0.01f;//碳水化合物
				}
				float fat =  0.0f;
				if(!TextUtils.isEmpty(nutrient.getNutrientSugarTot()) && Tool.isDigitsOnly(nutrient.getNutrientSugarTot())){
					fat =  Float.parseFloat(nutrient.getNutrientSugarTot())*recod.getRweight()*0.01f;//脂肪
				}
				float fibre =  0.0f;
				if(!TextUtils.isEmpty(nutrient.getNutrientIron()) && Tool.isDigitsOnly(nutrient.getNutrientIron())){
					fibre =  Float.parseFloat(nutrient.getNutrientIron())*recod.getRweight()*0.01f;//纤维
				}
				float cholesterol =  0.0f;
				if(!TextUtils.isEmpty(nutrient.getNutrientMagnesium()) && Tool.isDigitsOnly(nutrient.getNutrientMagnesium())){
					cholesterol =  Float.parseFloat(nutrient.getNutrientMagnesium())*recod.getRweight()*0.01f;//胆固醇
				}
				float vitaminC =  0.0f;
				if(!TextUtils.isEmpty(nutrient.getNutrientPotassium()) && Tool.isDigitsOnly(nutrient.getNutrientPotassium())){
					vitaminC =  Float.parseFloat(nutrient.getNutrientPotassium())*recod.getRweight()*0.01f;//维生素C
				}

				recod.setRphoto(nutrient.getNutrientDesc());
				recod.setRbodywater(kcal) ; //卡路里
				recod.setRbodyfat(protein);//蛋白质
				recod.setRbone(fat); //脂肪
				recod.setRmuscle(water);//碳水化合物
				recod.setRvisceralfat(fibre) ;//纤维
				recod.setRbmi(cholesterol);//胆固醇
				recod.setRbmr(vitaminC) ;//维生素C
				recod.setBodyAge(calcium);//钙
			}else{
				recod.setRbodywater(0.0f) ; //卡路里
				recod.setRbodyfat(0.0f);//蛋白质
				recod.setRbone(0.0f); //脂肪
				recod.setRmuscle(0.0f);//碳水化合物
				recod.setRvisceralfat(0.0f) ;//纤维
				recod.setRbmi(0.0f);//胆固醇
				recod.setRbmr(0.0f) ;//维生素C
				recod.setBodyAge(0.0f);//钙
			}

			try {
				recordService.save(recod);

				if(null!=user && null!=uservice){
					uservice.update(user);
				}

			} catch (Exception e) {
			}
		}

		return recod;
	}
	
	public static Records parseZuKangMeaage(RecordService recordService ,String readMessage,UserModel user) {
		Log.e("test", "解析数据：" + readMessage);
		Records recod = null;
		if(TextUtils.isEmpty(readMessage) && readMessage.length()<35){
			return null;
		}
		int age = 0;
        double height = 0;
        double weight = 0;
        int impedance = 0;
        int sex = 0;
        int level = 0;
        

		recod = new Records();
		recod.setUseId(user.getId());
		recod.setScaleType(user.getScaleType());
		recod.setUgroup(user.getGroup());
		recod.setLevel(user.getLevel());
		//String unit = readMessage.substring(16, 18);
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
		
		Log.e(TAG, "输入参数==>体重："+weight+"  身高:"+height+"  性别:"+sex+"  年龄:"+age+"  类型:"+level+"  阻抗:"+impedance);   
		
		Log.e(TAG, "计算结果==>" + bodyfat.getBodyfatParameters()+"====阻抗系数++>"+impedance);   
	    
	    try {
	    	if(bodyfat.getBodyfatParameters() == HTDataType.ErrorNone){
		        //正常计算
	    		recod.setRbmi(UtilTooth.keep1Point3(bodyfat.BMI));
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
				Log.e(TAG, "阻抗:" + bodyfat.ZTwoLegs +
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
			recordService.save(recod);
		} catch (Exception e) {
			Log.e(TAG, "解析数据异常==>" + e.getMessage());   
		}
		return recod;
	}

	/**
	 * 是否是锁定数据
	 * @param readMessage
	 * @return
     */
	public static boolean isLockData(String readMessage){
		if(TextUtils.isEmpty(readMessage) || readMessage.length()<22 ){
			return false;
		}
		String stats =  readMessage.substring(18, 20);
		if("00".equals(stats)){
			Log.e(TAG,readMessage +"   锁定数据");
			return true;
		}else{
			Log.e(TAG,readMessage +"   过程数据");
			return false;
		}
	}

	public static boolean checkData(String readMessage){
		if(!TextUtils.isEmpty(readMessage) && (readMessage.startsWith(UtilConstants.BODY_SCALE) || readMessage.startsWith(UtilConstants.BATHROOM_SCALE))){
			return true;
		}else{
			return false;
		}

	}

	/**
	 * DL Scale秤信息
	 * @param recordService
	 * @param readMessage
	 * @param user
     * @return CF 88 13 00 14 00 00 00 00 00 40
     */
	public static Records parseDLScaleMeaage(RecordService recordService ,String readMessage,UserModel user) {
		Log.e("test", "解析数据：" + readMessage);
		Records recod = null;
		if(TextUtils.isEmpty(readMessage) && readMessage.length()<22){
			return null;
		}
		int age = 0;
		double height = 0;
		double weight = 0;
		int impedance = 0;
		int sex = 0;
		int level = 0;


		recod = new Records();
		recod.setUseId(user.getId());
		recod.setUgroup(user.getGroup());
		recod.setLevel(user.getLevel());

		String scaleType = readMessage.substring(0,2);
		impedance = (int)(StringUtils.hexToTen(readMessage.substring(4,6)+readMessage.substring(2,4))*0.1f);
		//String unit = readMessage.substring(16, 18);
		weight = StringUtils.hexToTen(readMessage.substring(8, 10)+readMessage.substring(6, 8))*0.01d;
		height = user.getBheigth();
		if(!TextUtils.isEmpty(user.getSex()))sex=Integer.parseInt(user.getSex());
		if(!TextUtils.isEmpty(user.getLevel()))level=Integer.parseInt(user.getLevel());
        String unit = readMessage.substring(16, 18);
		age = user.getAgeYear();

		recod.setScaleType(scaleType);
		recod.setSex(sex+"");
		recod.setsAge(age + "");
		recod.setsHeight(String.valueOf(height));
		recod.setSweight(weight+ "");
		recod.setRweight((float)weight);
		recod.setRecordTime(UtilTooth.dateTimeChange(new Date()));

		if (unit.equals("00")) {//kg
			recod.setUnitType(0);
		} else if (unit.equals("01")) {//lb
			recod.setUnitType(1);
		} else if (unit.equals("02")) {//st
			recod.setUnitType(2);
		} else if (unit.equals("03")) {//斤
			recod.setUnitType(3);
		} else if (unit.equals("04")) {//g
			recod.setUnitType(4);
		}else if (unit.equals("05")) {//lb:oz
			recod.setUnitType(5);
		}else if (unit.equals("06")) {//oz
			recod.setUnitType(6);
		}else if (unit.equals("07")) {//ml(water)
			recod.setUnitType(7);
		}else if (unit.equals("08")) {//ml(milk)
			recod.setUnitType(8);
		}else {
			recod.setUnitType(0);
		}
		try {
			if(readMessage.startsWith(UtilConstants.BATHROOM_SCALE)){
				recod.setRbmi(UtilTooth.myround(UtilTooth.countBMI2(recod.getRweight(), (Float.parseFloat(recod.getsHeight())) / 100)));
				recod.setSbmi(UtilTooth.onePoint(recod.getRbmi()));
			}else if(readMessage.startsWith(UtilConstants.BODY_SCALE)){
				HTBodyfatGeneral bodyfat = new HTBodyfatGeneral(weight,height,sex, age, level, impedance);

				Log.e(TAG, "输入参数==>体重："+weight+"  身高:"+height+"  性别:"+sex+"  年龄:"+age+"  类型:"+level+"  阻抗:"+impedance);

				Log.e(TAG, "计算结果==>" + bodyfat.getBodyfatParameters()+"====阻抗系数++>"+impedance);

				if(bodyfat.getBodyfatParameters() == HTDataType.ErrorNone){
					//正常计算
					recod.setRbmi(UtilTooth.keep1Point3(bodyfat.BMI));
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
					Log.e(TAG, "阻抗:" + bodyfat.ZTwoLegs +
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
			recordService.save(recod);
		} catch (Exception e) {
			Log.e(TAG, "解析数据异常==>" + e.getMessage());
		}
		return recod;
	}


	/**
	 * 保存 婴儿数据
	 * @param recordService
	 * @param recod
	 * @param baby
     */
	public static void  handHarmBabyData(RecordService recordService,Records recod,UserModel baby){
		if(null!=recod && null!=baby){
			try {
				recod.setUseId(baby.getId());
				recod.setUgroup(baby.getGroup());
				Records lastRecod = recordService.findLastRecordsByUID(baby.getId());
				if (null != lastRecod) {
					recod.setCompareRecord((UtilTooth.myround(recod.getRweight() - lastRecod.getRweight())) + "");
				} else {
					recod.setCompareRecord("0.0");
				}
				recordService.save(recod);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	
}
