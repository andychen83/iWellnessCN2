package com.lefu.es.constant;

import java.util.HashSet;
import java.util.Set;

import com.lefu.es.service.BluetoothChatService;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.widget.ArrayAdapter;

/**
 * 蓝牙工具
 * @author Leon
 * 2015-11-17
 */
public class BluetoolUtil {
	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;


	public static final String ELECTRONIC_SCALE="Electronic Scale";
	
	public static final String HEALTH_SCALE="Health Scale";
	
	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// Intent request codes
	public static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
	public static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
	public static final int REQUEST_ENABLE_BT = 3;

	public static BluetoothDevice remoteDevice = null;
	// Name of the connected device
	public static String mConnectedDeviceName = null;
	// Array adapter for the conversation thread
	public static ArrayAdapter<String> mConversationArrayAdapter;
	// String buffer for outgoing messages
	public static StringBuffer mOutStringBuffer;
	// Local Bluetooth adapter
	public static BluetoothAdapter mBluetoothAdapter = null;
	// Member object for the chat services
	public static BluetoothChatService mChatService = null;

	public static BluetoothDevice lastDevice = null;

	public static Set<BluetoothDevice> devices = new HashSet<BluetoothDevice>();

	public static boolean bleflag = false;

	public static String mDeviceAddress;
	
}
