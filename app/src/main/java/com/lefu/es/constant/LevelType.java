package com.lefu.es.constant;

import com.lefu.iwellness.newes.cn.system.R;

/**
 * 等级
 * @author 
 * Leon 2015-11-17
 * 
 */
public enum LevelType {
	ORDINARY("Ordinary", R.string.ordinary), AMAEUR("Amaeur", R.string.amaeur), PROFESSIONAL("Professional", R.string.professional);

	private LevelType(String value, int id) {
		this.id = id;
		this.value = value;
	}
	private int id;
	private String value;
	public int getId() {
		return id;
	}
	public String getValue() {
		return value;
	}

}
