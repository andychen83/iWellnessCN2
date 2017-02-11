package com.lefu.es.entity;

import java.io.Serializable;

/**
 * 记录
 * @author lfl
 */
public class Records implements Serializable  {
	private static final long serialVersionUID = 1781102348190850847L;
	private int id;//id
	private int useId;//用户Id
	private String scaleType;//CF=֬脂肪称；CE=人体称；CB=婴儿称；CA=厨房秤
	private String ugroup;//用户组
	private String recordTime;//测量时间
	private String compareRecord; //对比测量记录
	private float rweight;//体重kg
	private float rbmi=0; //BMI
	private float rbone; // bone kg
	private float rbodyfat; // body fat 脂肪%
	private float rmuscle; // Muscale Mass %
	private float rbodywater; // Body Water 水分%
	private float rvisceralfat; // Visceral Fat
	private float rbmr; //BMR Kcal
	private String level;//等级
	private String sex;//性别
	private String sweight;//体重kg
	private String sbmi="0"; //BMI
	private String sbone; // bone kg
	private String sbodyfat; // body fat %
	private String smuscle; // Muscale Mass %
	private String sbodywater; // Body Water 身体水分%
	private String svisceralfat; // Visceral Fat
	private String sbmr; //BMR Kcal
	private String sHeight; //身高
	private String sAge; //年例
	private float bodyAge;//身体年龄
	private String rPhoto;//头像
	private int unitType;// 00-g,01=ml,02=lb,04-ml
	public boolean isNull=false;

	private String receiveMsg ;

	private UserModel user;

	public Records(){}

	public Records(int useId,String scaleType,String ugroup,String compareRecord,
			float rweight,float rbmi,float rbone,float rbodyfat, float rmuscle,float rbodywater,float rvisceralfat,float rbmr){
		this.useId = useId;
		this.compareRecord = compareRecord;
		this.scaleType = scaleType;
		this.ugroup = ugroup;

		this.rbmi = rbmi;
		this.rbmr = rbmr;
		this.rbodyfat = rbodyfat;
		this.rbodywater = rbodywater;
		this.rbone = rbone;
		this.rmuscle = rmuscle;
		this.rvisceralfat = rvisceralfat;
		this.rweight = rweight;
	}

	public Records(int id,int useId,String scaleType,String ugroup,String recordTime,String compareRecord,
			float rweight,float rbmi,float rbone,float rbodyfat, float rmuscle,float rbodywater,float rvisceralfat,float rbmr,float bodyAge){
		this.id = id;
		this.recordTime = recordTime;
		this.useId = useId;
		this.compareRecord = compareRecord;
		this.scaleType = scaleType;
		this.ugroup = ugroup;

		this.rbmi = rbmi;
		this.rbmr = rbmr;
		this.rbodyfat = rbodyfat;
		this.rbodywater = rbodywater;
		this.rbone = rbone;
		this.rmuscle = rmuscle;
		this.rvisceralfat = rvisceralfat;
		this.rweight = rweight;
		this.bodyAge = bodyAge;
	}
	public Records(int id,int useId,String scaleType,String ugroup,String recordTime,String compareRecord,
			float rweight,float rbmi,float rbone,float rbodyfat, float rmuscle,float rbodywater,float rvisceralfat,float rbmr){
		this.id = id;
		this.recordTime = recordTime;
		this.useId = useId;
		this.compareRecord = compareRecord;
		this.scaleType = scaleType;
		this.ugroup = ugroup;

		this.rbmi = rbmi;
		this.rbmr = rbmr;
		this.rbodyfat = rbodyfat;
		this.rbodywater = rbodywater;
		this.rbone = rbone;
		this.rmuscle = rmuscle;
		this.rvisceralfat = rvisceralfat;
		this.rweight = rweight;
	}
	public Records(int useId,String scaleType,String ugroup){
		isNull = true;
		this.useId = useId;
		this.scaleType = scaleType;
		this.ugroup = ugroup;
	}

	public Records(int useId,String scaleType,String ugroup,String recordTime,String compareRecord,
			float rweight,float rbmi,float rbone,float rbodyfat, float rmuscle,float rbodywater,float rvisceralfat,float rbmr){
		this.recordTime = recordTime;
		this.useId = useId;
		this.compareRecord = compareRecord;
		this.scaleType = scaleType;
		this.ugroup = ugroup;

		this.rbmi = rbmi;
		this.rbmr = rbmr;
		this.rbodyfat = rbodyfat;
		this.rbodywater = rbodywater;
		this.rbone = rbone;
		this.rmuscle = rmuscle;
		this.rvisceralfat = rvisceralfat;
		this.rweight = rweight;
	}

	public Records(int useId,String scaleType,String ugroup,String recordTime,String compareRecord,
			float rweight,float rbmi,float rbone,float rbodyfat, float rmuscle,float rbodywater,float rvisceralfat,float rbmr,float bodyAge){
		this.recordTime = recordTime;
		this.useId = useId;
		this.compareRecord = compareRecord;
		this.scaleType = scaleType;
		this.ugroup = ugroup;

		this.rbmi = rbmi;
		this.rbmr = rbmr;
		this.rbodyfat = rbodyfat;
		this.rbodywater = rbodywater;
		this.rbone = rbone;
		this.rmuscle = rmuscle;
		this.rvisceralfat = rvisceralfat;
		this.rweight = rweight;
		this.bodyAge = bodyAge;
	}

	public String getReceiveMsg() {
		return receiveMsg;
	}

	public void setReceiveMsg(String receiveMsg) {
		this.receiveMsg = receiveMsg;
	}

	public int getUnitType() {
		return unitType;
	}

	public void setUnitType(int unitType) {
		this.unitType = unitType;
	}

	public String getsAge() {
		return sAge;
	}

	public void setsAge(String sAge) {
		this.sAge = sAge;
	}

	public String getsHeight() {
		return sHeight;
	}

	public void setsHeight(String sHeight) {
		this.sHeight = sHeight;
	}

	public String getSweight() {
		return sweight;
	}

	public void setSweight(String sweight) {
		this.sweight = sweight;
	}

	public String getSbmi() {
		return sbmi;
	}

	public void setSbmi(String sbmi) {
		this.sbmi = sbmi;
	}

	public String getSbone() {
		return sbone;
	}

	public void setSbone(String sbone) {
		this.sbone = sbone;
	}

	public String getSbodyfat() {
		return sbodyfat;
	}

	public void setSbodyfat(String sbodyfat) {
		this.sbodyfat = sbodyfat;
	}

	public String getSmuscle() {
		return smuscle;
	}

	public void setSmuscle(String smuscle) {
		this.smuscle = smuscle;
	}

	public String getSbodywater() {
		return sbodywater;
	}

	public void setSbodywater(String sbodywater) {
		this.sbodywater = sbodywater;
	}

	public String getSvisceralfat() {
		return svisceralfat;
	}

	public void setSvisceralfat(String svisceralfat) {
		this.svisceralfat = svisceralfat;
	}

	public String getSbmr() {
		return sbmr;
	}

	public void setSbmr(String sbmr) {
		this.sbmr = sbmr;
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

	public String getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(String recordTime) {
		this.recordTime = recordTime;
	}

	public String getCompareRecord() {
		return compareRecord;
	}



	public float getBodyAge() {
		return bodyAge;
	}

	public void setBodyAge(float bodyAge) {
		this.bodyAge = bodyAge;
	}

	public void setCompareRecord(String compareRecord) {
		this.compareRecord = compareRecord;
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

	public UserModel getUser() {
		return user;
	}

	public void setUser(UserModel user) {
		this.user = user;
	}

	public String getRphoto() {
		return rPhoto;
	}

	public void setRphoto(String rPhoto) {
		this.rPhoto = rPhoto;
	}

	@Override
	public String toString() {
		return "Records [scaleType=" + scaleType + ", ugroup=" + ugroup
				+ ", recordTime=" + recordTime + ", compareRecord="
				+ compareRecord + ", rweight=" + rweight + ", rbmi=" + rbmi
				+ ", rbone=" + rbone + ", rbodyfat=" + rbodyfat + ", rmuscle="
				+ rmuscle + ", rbodywater=" + rbodywater + ", rvisceralfat="
				+ rvisceralfat + ", rbmr=" + rbmr + ", level=" + level
				+ ", sex=" + sex + ", sweight=" + sweight + ", sbmi=" + sbmi
				+ ", sbone=" + sbone + ", sbodyfat=" + sbodyfat + ", smuscle="
				+ smuscle + ", sbodywater=" + sbodywater + ", svisceralfat="
				+ svisceralfat + ", sbmr=" + sbmr + ", sHeight=" + sHeight
				+ ", sAge=" + sAge + "]";
	}

}
