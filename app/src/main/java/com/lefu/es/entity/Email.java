package com.lefu.es.entity;
/**
 * 邮件
 * @author lfl
 */
public class Email {
	private int id;
	private String name;
	private String email;
	
	public Email(String eName,String eEmail){
		this.name = eName;
		this.email = eEmail;
	}
	
	public Email(int eId,String eName,String eEmail){
		this.id= eId;
		this.name = eName;
		this.email = eEmail;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
