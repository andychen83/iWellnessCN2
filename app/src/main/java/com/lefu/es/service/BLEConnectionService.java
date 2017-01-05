package com.lefu.es.service;

import com.lefu.es.ble.BlueSingleton;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.IBinder;
/**
 * BLE连接服务
 * @author Leon 
 * 2015-11-17
 */
public class BLEConnectionService extends Service {
	BlueSingleton blueSingleton;
	BluetoothAdapter adapter;
	boolean isScan = false;

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		blueSingleton = BlueSingleton.getInstance(null);
		adapter = BluetoothAdapter.getDefaultAdapter();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		myThread.start();
		return null;
	}

	Thread myThread = new Thread() {
		@Override
		public void run() {
			super.run();
			isScan = blueSingleton.getmScanning();
			while (true) {
				if (!isScan) {
					adapter.startLeScan(blueSingleton.mLeScanCallback);
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	};
}
