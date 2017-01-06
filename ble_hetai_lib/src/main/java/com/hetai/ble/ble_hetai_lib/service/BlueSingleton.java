package com.hetai.ble.ble_hetai_lib.service;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import com.hetai.ble.ble_hetai_lib.bean.BleAdvertisedData;
import com.hetai.ble.ble_hetai_lib.constant.BluetoolUtil;
import com.hetai.ble.ble_hetai_lib.utils.BleUtil;
import java.io.Serializable;

/**
 * 蓝牙单例
 * 
 * @author andy 2016-10-26
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
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
	
	private BluetoothLeScanner scanner;

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
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					ScanThread2 scanThread2 = new ScanThread2();
					scanThread2.start();
				}else{
					ScanThread scanThread = new ScanThread();
					scanThread.start();
				}
				
			} else {
				mScanning = false;
				if (mBluetoothAdapter == null) {
					mBluetoothAdapter = bluetoothManager.getAdapter();
				}
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					if(null==scanner)scanner = mBluetoothAdapter.getBluetoothLeScanner();
					scanner.stopScan(scanCallback);
				}else{
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
				}
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
				if (mBluetoothAdapter == null) {
					return;
				}
				mBluetoothAdapter.startLeScan(mLeScanCallback);
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (!getSearchState()) {
					mScanning = false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
				}
			}
		}
	}
	
	
	class ScanThread2 extends Thread {
		public ScanThread2() {
		}

		@TargetApi(Build.VERSION_CODES.LOLLIPOP)
		@Override
		public void run() {
			super.run();
			while (!isExit && !getSearchState()) {
				Log.e("bluesingleton", "ScanThread...");
				if (mBluetoothAdapter == null) {
					return;
				}
				if(null==scanner)scanner = mBluetoothAdapter.getBluetoothLeScanner();
				scanner.startScan(scanCallback);
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (!getSearchState()) {
					mScanning = false;
					scanner.stopScan(scanCallback);
				}
			}
		}
	}
	
	private ScanCallback scanCallback = new ScanCallback() {
	    @Override
	    public void onScanResult(int callbackType, ScanResult result) {

	        System.out.println(result.getRssi() + " " + result.getDevice().getName() );
	        final BluetoothDevice device = result.getDevice();
	        if (device != null ) {
	        	String deviceName = device.getName();
				if (deviceName != null && deviceName.indexOf("Scale") >= 0) {
					System.out.println("发现BLE称=" + deviceName + "[" + device.getAddress() + "]");
					/* 标识已经找到设备 */
					isSearch = true;
					/* 停止蓝牙扫描 */
					if (mScanning) {
						scanner.stopScan(scanCallback);
						mScanning = false;
					}
					/* 记录蓝牙地址 */
					BluetoolUtil.mDeviceAddress = device.getAddress();
					BluetoolUtil.mConnectedDeviceName = deviceName;
					/* 启动连接服务 */
					if(mHasBind){
						activity.getApplicationContext().unbindService(mServiceConnection);
						mHasBind = false;
					}
					activity.getApplicationContext().bindService(new Intent(activity, BluetoothLeService.class), mServiceConnection, activity.BIND_AUTO_CREATE);
					mHasBind = true;
				}
	        }
			
	        super.onScanResult(callbackType, result);

	    }

	    @Override
	    public void onScanFailed(int errorCode) {
	        System.out.println("Error code " + errorCode );

	        super.onScanFailed(errorCode);

	    }
	};

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

						if (device != null && deviceName != null && deviceName.indexOf("Scale") >= 0) {
							System.out.println("发现BLE称=" + deviceName + "[" + device.getAddress() + "]");
							/* 标识已经找到设备 */
							isSearch = true;
							/* 停止蓝牙扫描 */
							if (mScanning) {
								mBluetoothAdapter.stopLeScan(mLeScanCallback);
								mScanning = false;
							}
							/* 记录蓝牙地址 */
							BluetoolUtil.mDeviceAddress = device.getAddress();
							BluetoolUtil.mConnectedDeviceName = deviceName;
							/* 启动连接服务 */
							if(mHasBind){
								activity.getApplicationContext().unbindService(mServiceConnection);
								mHasBind = false;
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
