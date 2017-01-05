package com.lefu.es.util;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;


/**
 * BT工具类
 * @author lfl
 * 
 */
public class BTUtils {

	/**蓝牙可见*/
	public static void enableDiscoverable(Context context) {
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
		context.startActivity(discoverableIntent);
	}

}
