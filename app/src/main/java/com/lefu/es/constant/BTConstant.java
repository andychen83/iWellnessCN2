package com.lefu.es.constant;

import java.util.UUID;

/**
 * BT常量
 * @author Leon
 * 2015-12-3
 * 
 */
public class BTConstant {
	// Unique UUID for this application
	public static final UUID MY_UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	public static final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	// Constants that indicate the current connection state
	public static final int STATE_NONE = 0; // we're doing nothing
	public static final int STATE_LISTEN = 1; // now listening for incoming
	public static final int STATE_CONNECTING = 2; // now initiating an outgoing
	public static final int STATE_CONNECTED = 3; // now connected to a remote

}
