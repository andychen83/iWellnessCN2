package com.lefu.es.entity;
/**
 * 蓝牙设备
 * @author lfl
 */
public class BlueDevice {
	private String uuid;
	private String deviceName;
	
	public BlueDevice(){}
	
	public BlueDevice(String uuid,String deviceName){
		this.uuid = uuid;
		this.deviceName = deviceName;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	
}
