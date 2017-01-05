package com.lefu.es.entity;

/**
 * 用户分组
 * @author Leon
 * 2015-11-17
 */
public class UserGroupModel {
	public static UserGroupModel group0 = null;
	public static UserGroupModel group1 = null;
	public static UserGroupModel group2 = null;
	public static UserGroupModel group3 = null;
	public static UserGroupModel group4 = null;
	public static UserGroupModel group5 = null;
	public static UserGroupModel group6 = null;
	public static UserGroupModel group7 = null;
	public static UserGroupModel group8 = null;
	
	private int id;
	private String groupNumber;
	private String groupName;
	
	public UserGroupModel(){}
	
	public UserGroupModel(String groupNumber,String groupName){
		this.groupName = groupName;
		this.groupNumber = groupNumber;
	}
	
	static{
		 group0 = new UserGroupModel("P0","P0");
		 group1 = new UserGroupModel("P1","P1");
		 group2 = new UserGroupModel("P2","P2");
		 group3 = new UserGroupModel("P3","P3");
		 group4 = new UserGroupModel("P4","P4");
		 group5 = new UserGroupModel("P5","P5");
		 group6 = new UserGroupModel("P6","P6");
		 group7 = new UserGroupModel("P7","P7");
		 group8 = new UserGroupModel("P8","P8");
	}

	public String getGroupNumber() {
		return groupNumber;
	}

	public void setGroupNumber(String groupNumber) {
		this.groupNumber = groupNumber;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	

}
