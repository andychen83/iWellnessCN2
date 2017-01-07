package com.lefu.es.blenew.bean;

import java.io.Serializable;

/**
 * 测量记录
 * @author andy
 */
public class Records1 implements Serializable  {
	private static final long serialVersionUID = 1781102348190850847L;
	private int id;//id
	private int useId;//用户Id
	private String scaleType;//CF=֬脂肪称；CE=人体称；CB=婴儿称；CA=厨房秤
	private String ugroup;//用户组
	private String recordTime;//测量时间
	private String compareRecord; //对比测量记录
	private float rweight;//体重kg
	private float rbmi; //BMI
	private float rbone; // bone kg
	private float rbodyfat; // body fat 脂肪%
	private float rmuscle; // Muscale Mass %
	private float rbodywater; // Body Water 水分%
	private float rvisceralfat; // Visceral Fat
	private float rbmr; //BMR Kcal
	private String level;//等级
	private String sex;//性别
	private float bodyAge;//身体年龄
	private int unitType;// 00-g,01=ml,02=lb,04-ml
	private boolean isEffective;//测量结果是否有效

	public boolean isEffective() {
		return isEffective;
	}

	public void setEffective(boolean effective) {
		isEffective = effective;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUseId() {
		return useId;
	}

	public void setUseId(int useId) {
		this.useId = useId;
	}

	public String getScaleType() {
		return scaleType;
	}

	public void setScaleType(String scaleType) {
		this.scaleType = scaleType;
	}

	public String getUgroup() {
		return ugroup;
	}

	public void setUgroup(String ugroup) {
		this.ugroup = ugroup;
	}

	public String getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(String recordTime) {
		this.recordTime = recordTime;
	}

	public String getCompareRecord() {
		return compareRecord;
	}

	public void setCompareRecord(String compareRecord) {
		this.compareRecord = compareRecord;
	}

	public float getRweight() {
		return rweight;
	}

	public void setRweight(float rweight) {
		this.rweight = rweight;
	}

	public float getRbmi() {
		return rbmi;
	}

	public void setRbmi(float rbmi) {
		this.rbmi = rbmi;
	}

	public float getRbone() {
		return rbone;
	}

	public void setRbone(float rbone) {
		this.rbone = rbone;
	}

	public float getRbodyfat() {
		return rbodyfat;
	}

	public void setRbodyfat(float rbodyfat) {
		this.rbodyfat = rbodyfat;
	}

	public float getRmuscle() {
		return rmuscle;
	}

	public void setRmuscle(float rmuscle) {
		this.rmuscle = rmuscle;
	}

	public float getRbodywater() {
		return rbodywater;
	}

	public void setRbodywater(float rbodywater) {
		this.rbodywater = rbodywater;
	}

	public float getRvisceralfat() {
		return rvisceralfat;
	}

	public void setRvisceralfat(float rvisceralfat) {
		this.rvisceralfat = rvisceralfat;
	}

	public float getRbmr() {
		return rbmr;
	}

	public void setRbmr(float rbmr) {
		this.rbmr = rbmr;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public float getBodyAge() {
		return bodyAge;
	}

	public void setBodyAge(float bodyAge) {
		this.bodyAge = bodyAge;
	}

	public int getUnitType() {
		return unitType;
	}

	public void setUnitType(int unitType) {
		this.unitType = unitType;
	}
}
