package com.lefu.es.ble;

import java.io.Serializable;

import com.lefu.es.constant.BluetoolUtil;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
/**
 * 蓝牙单例
 * 
 * @author Leon 2015-11-17
 */
public class BlueSingleton implements Serializable {
	private static final long serialVersionUID = -7953127527312783591L;
	/* 单例 */
	private static BlueSingleton uniqueInstance = null;
	/* 是否已经连接 */
	private boolean mConnected = false;
	/* 是否在扫描中 */
	private boolean mScanning = false;
	/* 是否在处理中 */
	static boolean isdoings = false;
	/* 是否已经发现蓝牙设备 */
	public boolean isSearch = false;
	/* 是否已经退出BLE扫描 */
	public boolean isExit = false;

	private BluetoothAdapter mBluetoothAdapter;
	private Activity activity;
	private ServiceConnection mServiceConnection;
	
	private boolean mHasBind;

	private BlueSingleton() {
	}

	/** 获取单例 */
	public static BlueSingleton getInstance(Handler h) {
		if (uniqueInstance == null) {
			uniqueInstance = new BlueSingleton();
		}
		return uniqueInstance;
	}

	/** 开启蓝牙扫描 */
	public void scanLeDevice(final boolean enable, Activity activity, ServiceConnection mServiceConnection) {
		try {
			this.activity = activity;
			this.mServiceConnection = mServiceConnection;
			final BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothAdapter == null) {
				mBluetoothAdapter = bluetoothManager.getAdapter();
			}
			if (enable) {
				isSearch = false;
				mScanning = true;
				ScanThread scanThread = new ScanThread();
				scanThread.start();
			} else {
				mScanning = false;
				if (mBluetoothAdapter == null) {
					mBluetoothAdapter = bluetoothManager.getAdapter();
				}
				mBluetoothAdapter.stopLeScan(mLeScanCallback);
			}
		} catch (Exception e) {
			
		}
	}

	/** 蓝牙扫描线程 */
	class ScanThread extends Thread {
		public ScanThread() {
		}

		@Override
		public void run() {
			super.run();
			while (!isExit && !getSearchState()) {
				Log.e("bluesingleton", "ScanThread...");
				if(mBluetoothAdapter==null)return;
				try {
					if (mBluetoothAdapter != null)mBluetoothAdapter.startLeScan(mLeScanCallback);
					Thread.sleep(7000);
					if (!getSearchState()) {
						mScanning = false;
						if (mBluetoothAdapter != null)mBluetoothAdapter.stopLeScan(mLeScanCallback);
					}
				} catch (Exception e) {
					Log.e("BlueSingleton", "MyThread::"+e.getMessage());
				}
				
			}
		}
	}

	/** 蓝牙扫描回调 */
	public BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
			Runnable rn = new Runnable() {
				@SuppressWarnings("static-access")
				@Override
				public void run() {
					try {
						final BleAdvertisedData badata = BleUtil.parseAdertisedData(scanRecord);
						String deviceName = device.getName();
						if (deviceName == null) {
							deviceName = badata.getName();
						}
						if (TextUtils.isEmpty(deviceName)) {
							Log.e("===BlueSingleton=== ", "没有发现蓝牙名字");
							
						}else{
							Log.e("===BlueSingleton=== ", "蓝牙名字为==>"+deviceName);
						}
						if (device != null && deviceName != null && deviceName.indexOf("Scale") >= 0) {
							System.out.println("发现BLE称=" + deviceName + "[" + device.getAddress() + "]");
							/* 标识已经找到设备 */
							isSearch = true;
							/* 停止蓝牙扫描 */
							if (null!=mBluetoothAdapter) {
								mBluetoothAdapter.stopLeScan(mLeScanCallback);
								mScanning = false;
							}
							/* 记录蓝牙地址 */
							BluetoolUtil.mDeviceAddress = device.getAddress();
							BluetoolUtil.mConnectedDeviceName = deviceName;
							/* 启动连接服务 */
							try {
								if(mHasBind){
									activity.getApplicationContext().unbindService(mServiceConnection);
									mHasBind = false;
								}
							} catch (Exception e) {
								// TODO: handle exception
							}
							
							activity.getApplicationContext().bindService(new Intent(activity, BluetoothLeService.class), mServiceConnection, activity.BIND_AUTO_CREATE);
							mHasBind = true;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			rn.run();
		}
	};

	/** 扫描过程中退出 */
	public void linkout() {
		if (mScanning && !mConnected) {
			scanLeDevice(false, activity, mServiceConnection);
		}
	}

	public boolean getSearchState() {
		return isSearch;
	}

	public boolean getmScanning() {
		return mScanning;
	}
	public void setmScanning(boolean b) {
		mScanning = b;
	}
	public boolean getmConnected() {
		return mConnected;
	}
	public void setmConnected(boolean b) {
		mConnected = b;
	}
	public void setmServiceConnection(ServiceConnection mServiceConnection) {
		this.mServiceConnection = mServiceConnection;
	}

	public static synchronized boolean isIsdoing() {
		return isdoings;
	}

	public static synchronized void setIsdoing(boolean isdoing) {
		isdoings = isdoing;
	}
}
