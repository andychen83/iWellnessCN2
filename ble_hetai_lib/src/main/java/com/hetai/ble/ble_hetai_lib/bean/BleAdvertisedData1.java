package com.hetai.ble.ble_hetai_lib.bean;

import java.util.List;
import java.util.UUID;

public class BleAdvertisedData1 {
	private List<UUID> mUuids;
	private String mName;
	public BleAdvertisedData1(List<UUID> uuids, String name) {
		mUuids = uuids;
		mName = name;
	}

	public List<UUID> getUuids() {
		return mUuids;
	}

	public String getName() {
		return mName;
	}
}