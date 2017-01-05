package com.lefu.es.entity;

import java.io.Serializable;
/**
 * 用户
 * @author Leon
 * 2015-11-17
 */
public class UserModel implements Serializable {
	private static final long serialVersionUID = -8295238475381724571L;
	private int id;//id
	private String userName;//用户名
	private String group; //分组
	private String scaleType;   //称类型
	private String sex; //性别
	private String level ; //等级
	private float bheigth=0.0f; //身高
	private int ageYear = 0;  //年龄年份
	private int ageMonth = 0; //年例月份
	private int number;//0-9 //
	private String uniqueID;//
	private String birth;//生日
	private String per_photo;//头像
	private float targweight;//目标体重
	private String danwei="kg";//单位
	
	public UserModel(){}
	
	public UserModel(String userName,String group,String sex,String level,float bheigth,int ageYear,int ageMonth,int number,String scaleType,String uId){
		this.userName = userName;
		this.group = group;
		this.sex = sex;
		this.level = level;
		this.bheigth = bheigth;
		this.ageYear = ageYear;
		this.ageMonth = ageMonth;
		this.number = number;
		this.scaleType = scaleType;
		this.uniqueID = uId;
	}
	
	public UserModel(String userName,String group,String sex,String level,float bheigth,int ageYear,int ageMonth,int number,String scaleType,String uId,String birth){
		this.userName = userName;
		this.group = group;
		this.sex = sex;
		this.level = level;
		this.bheigth = bheigth;
		this.ageYear = ageYear;
		this.ageMonth = ageMonth;
		this.number = number;
		this.scaleType = scaleType;
		this.uniqueID = uId;
		this.birth = birth;
	}
	
	public UserModel(int id,String userName,String group,String sex,String level,float bheigth,int ageYear,int ageMonth,int number,String scaleType,String uId){
		this.id = id;
		this.userName = userName;
		this.group = group;
		this.sex = sex;
		this.level = level;
		this.bheigth = bheigth;
		this.ageYear = ageYear;
		this.ageMonth = ageMonth;
		this.number = number;
		this.scaleType = scaleType;
		this.uniqueID = uId;
		
	}
	
	public UserModel(int id,String userName,String group,String sex,String level,float bheigth,int ageYear,
			int ageMonth,int number,String scaleType,String uId,String birth){
		this.id = id;
		this.userName = userName;
		this.group = group;
		this.sex = sex;
		this.level = level;
		this.bheigth = bheigth;
		this.ageYear = ageYear;
		this.ageMonth = ageMonth;
		this.number = number;
		this.scaleType = scaleType;
		this.uniqueID = uId;
		this.birth = birth;
	}
	
	public UserModel(int id,String userName,String group,String sex,String level,float bheigth,int ageYear,
			int ageMonth,int number,String scaleType,String uId,String birth,String photo){
		this.id = id;
		this.userName = userName;
		this.group = group;
		this.sex = sex;
		this.level = level;
		this.bheigth = bheigth;
		this.ageYear = ageYear;
		this.ageMonth = ageMonth;
		this.number = number;
		this.scaleType = scaleType;
		this.uniqueID = uId;
		this.birth = birth;
		this.per_photo = photo;
	}
	
	public UserModel(String userName,String group,String sex,String level,float bheigth,int ageYear,int ageMonth,int number,String scaleType){
		this.userName = userName;
		this.group = group;
		this.sex = sex;
		this.level = level;
		this.bheigth = bheigth;
		this.ageYear = ageYear;
		this.ageMonth = ageMonth;
		this.number = number;
		this.scaleType = scaleType;
	}
	
	public UserModel(int id,String userName,String group,String sex,String level,float bheigth,int ageYear,
			int ageMonth,int number,String scaleType,String uId,String birth,String photo,float targweight){
		this.id = id;
		this.userName = userName;
		this.group = group;
		this.sex = sex;
		this.level = level;
		this.bheigth = bheigth;
		this.ageYear = ageYear;
		this.ageMonth = ageMonth;
		this.number = number;
		this.scaleType = scaleType;
		this.uniqueID = uId;
		this.birth = birth;
		this.per_photo = photo;
		this.targweight = targweight;
	}
	
	public UserModel(int id,String userName,String group,String sex,String level,float bheigth,int ageYear,
			int ageMonth,int number,String scaleType,String uId,String birth,String photo,float targweight,String danwei){
		this.id = id;
		this.userName = userName;
		this.group = group;
		this.sex = sex;
		this.level = level;
		this.bheigth = bheigth;
		this.ageYear = ageYear;
		this.ageMonth = ageMonth;
		this.number = number;
		this.scaleType = scaleType;
		this.uniqueID = uId;
		this.birth = birth;
		this.per_photo = photo;
		this.targweight = targweight;
		this.danwei = danwei;
	}
	
	

	public float getTargweight() {
		return targweight;
	}

	public void setTargweight(float targweight) {
		this.targweight = targweight;
	}

	public String getUniqueID() {
		if(uniqueID!=null && uniqueID.length()>0){
			return uniqueID;
		}else{
			return "";
		}
	}

	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public float getBheigth() {
		return bheigth;
	}

	public void setBheigth(float bheigth) {
		this.bheigth = bheigth;
	}

	public int getAgeYear() {
		return ageYear;
	}

	public void setAgeYear(int ageYear) {
		this.ageYear = ageYear;
	}

	public int getAgeMonth() {
		return ageMonth;
	}

	public void setAgeMonth(int ageMonth) {
		this.ageMonth = ageMonth;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getScaleType() {
		return scaleType;
	}

	public void setScaleType(String scaleType) {
		this.scaleType = scaleType;
	}

	public String getBirth() {
		return birth;
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}

	public String getPer_photo() {
		return per_photo;
	}

	public void setPer_photo(String per_photo) {
		this.per_photo = per_photo;
	}

	public String getDanwei() {
		return danwei;
	}

	public void setDanwei(String danwei) {
		this.danwei = danwei;
	}
	
}
