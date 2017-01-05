package com.lefu.es.ble;

import java.util.List;
import java.util.UUID;

import com.lefu.es.constant.BLEConstant;
import com.lefu.es.util.StringUtils;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * BLE服务
 * @author Leon 
 * 2015-11-17
 */
@SuppressLint("NewApi")
public class BluetoothLeService extends Service {
	private final static String TAG = BluetoothLeService.class.getSimpleName();
	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private String mBluetoothDeviceAddress;
	private BluetoothGatt mBluetoothGatt;

	/**GATT回调*/
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				/*连接成功*/
				broadcastUpdate(BLEConstant.ACTION_GATT_CONNECTED);
				/*发现服务*/
				if(null!=mBluetoothGatt)mBluetoothGatt.discoverServices();
			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				/*断开连接*/
				broadcastUpdate(BLEConstant.ACTION_GATT_DISCONNECTED);
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				/*发现服务*/
				broadcastUpdate(BLEConstant.ACTION_GATT_SERVICES_DISCOVERED);
			}
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				/*读到数据*/
				broadcastUpdate(BLEConstant.ACTION_DATA_AVAILABLE, characteristic);
				/*释放连接*/
				disconnect();
			}
		}

		@Override
		public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
			broadcastUpdate(BLEConstant.ACTION_DATA_AVAILABLE, characteristic);
		}

		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
		}

		public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
		};

	};

	/**发送单纯广播*/
	private void broadcastUpdate(final String action) {
		final Intent intent = new Intent(action);
		sendBroadcast(intent);
	}

	/**发送数据广播*/
	private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
		final Intent intent = new Intent(action);
		final byte[] data = characteristic.getValue();
		intent.putExtra(BLEConstant.EXTRA_DATA, StringUtils.bytes2HexString(data));
		sendBroadcast(intent);
	}

	public class LocalBinder extends Binder {
		public BluetoothLeService getService() {
			return BluetoothLeService.this;
		}
	}
	
	

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind");
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG, "onUnbind");
		close();
		return super.onUnbind(intent);
	}

	private final IBinder mBinder = new LocalBinder();
	
	/**判断是否初始化*/
	public boolean initialize() {
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				Log.e(TAG, "Unable to initialize BluetoothManager.");
				return false;
			}
		}

		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
			return false;
		}

		return true;
	}

	/**连接*/
	public boolean connect(final String address) {
		if (mBluetoothAdapter == null || address == null) {
			return false;
		}

		if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress) && mBluetoothGatt != null) {
			if (mBluetoothGatt.connect()) {
				return true;
			} else {
				return false;
			}
		}

		final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		if (device == null) {
			return false;
		}
		mBluetoothGatt = device.connectGatt(this, true, mGattCallback);
		mBluetoothDeviceAddress = address;
		return true;
	}

	/**断开连接*/
	public void disconnect() {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.disconnect();
	}

	/**关闭*/
	public void close() {
		if (mBluetoothGatt != null) {
			mBluetoothGatt.close();
			mBluetoothGatt = null;
		}
	}
	
	/**
	 * Request a read on a given {@code BluetoothGattCharacteristic}. The read
	 * result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
	 * callback.
	 * 
	 * @param characteristic
	 *            The characteristic to read from.
	 */
	public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			System.out.println("return");
			return;
		}
		mBluetoothGatt.readCharacteristic(characteristic);
	}

	/**传递数据给终端*/
	public void wirteCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter != null && mBluetoothGatt != null) {
			mBluetoothGatt.writeCharacteristic(characteristic);
		}
	}

	/**设置当指定characteristic值变化时，发出notify通知*/
	public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
		if (mBluetoothAdapter != null && mBluetoothGatt != null) {
			mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
			BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
			if (descriptor != null) {
				descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
				mBluetoothGatt.writeDescriptor(descriptor);
			}
		}
	}
	
	/**设置当指定characteristic值变化时，发出indicate通知*/
	public void setCharacteristicIndaicate(BluetoothGattCharacteristic characteristic, boolean enabled) {
		if (mBluetoothGatt != null && null!=characteristic) {
			mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
			BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
			if (descriptor != null) {
				descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
				mBluetoothGatt.writeDescriptor(descriptor);
			}
		}
	}


	/**获取支持的服务*/
	public List<BluetoothGattService> getSupportedGattServices() {
		if (mBluetoothGatt == null){
			return null;
		}

		return mBluetoothGatt.getServices();
	}

	/**获取支持的Characteristic*/
	public BluetoothGattCharacteristic getCharacteristic(List<BluetoothGattService> gattServices, String s) {
		if (gattServices == null)
			return null;
		for (BluetoothGattService gattService : gattServices) {
			String suuid = gattService.getUuid().toString();
			if (suuid.substring(4, 8).endsWith("fff0")) {
				return gattService.getCharacteristic(UUID.fromString("0000" + s + "-0000-1000-8000-00805f9b34fb"));
			}
		}

		return null;
	}
	
	public BluetoothGattCharacteristic getCharacteristicNew(List<BluetoothGattService> gattServices, String s) {
		if (gattServices == null)
			return null;
		for (BluetoothGattService gattService : gattServices) {
			String suuid = gattService.getUuid().toString();
			if (suuid.substring(4, 8).endsWith("181b")) {
				return gattService.getCharacteristic(UUID.fromString("0000" + s + "-0000-1000-8000-00805f9b34fb"));
			}
		}

		return null;
	}
	
	/**注册GATT状态变化广播*/
	public static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BLEConstant.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BLEConstant.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BLEConstant.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BLEConstant.ACTION_DATA_AVAILABLE);
		return intentFilter;
	}
}
