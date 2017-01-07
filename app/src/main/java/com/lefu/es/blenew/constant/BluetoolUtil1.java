package com.lefu.es.blenew.constant;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.widget.ArrayAdapter;

import java.util.HashSet;
import java.util.Set;

/**
 * 蓝牙工具
 * @author andy
 * 2016-10-26
 */
public class BluetoolUtil1 {
	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;


	public static final int FOUND_DEVICE = 101;
	public static final int RECEIVE_DATA = 102;
	public static final int CLEARALL_DATA = 103;
	public static final int DESCIVE_SERVICE = 104;
	public static final int DESCIVE_CONNECTED = 105;
	public static final int DESCIVE_DISCONNECT = 106;

	public static final String ELECTRONIC_SCALE="Electronic Scale";
	
	public static final String HEALTH_SCALE="Health Scale";

	public static final String DL_SCALE="DL Scale";
	
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

	public static BluetoothDevice lastDevice = null;

	public static Set<BluetoothDevice> devices = new HashSet<BluetoothDevice>();

	public static boolean bleflag = false;

	public static String mDeviceAddress;
	
}
