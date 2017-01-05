package com.lefu.es.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.lefu.es.constant.BTConstant;
import com.lefu.es.constant.BluetoolUtil;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 蓝牙chat
 */
public class BluetoothChatService {
	private static final String TAG = "BluetoothChatService";
	private static final boolean D = true;

	// Name for the SDP record when creating server socket
	private static final String NAME_SECURE = "BluetoothChatSecure";

	// Member fields
	private final BluetoothAdapter mAdapter;
	private final Handler mHandler;
	private AcceptThread mSecureAcceptThread;
	private AcceptThread mInsecureAcceptThread;
	private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;
	public static int mState;

	public BluetoothChatService(Context context, Handler handler) {
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mState = BTConstant.STATE_NONE;
		mHandler = handler;
	}

	/**设置状态变化*/
	private synchronized void setState(int state) {
		mState = state;
		if (null != mHandler){
			mHandler.obtainMessage(BluetoolUtil.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
		}
	}

	/**获取当前连接状态*/
	public synchronized int getState() {
		return mState;
	}

	/**开始*/
	public synchronized void start() {
		if (D)
			Log.d(TAG, "start");
		try {
			// Cancel any thread attempting to make a connection
			if (mConnectThread != null) {
				mConnectThread.cancel();
				mConnectThread = null;
			}

			// Cancel any thread currently running a connection
			if (mConnectedThread != null) {
				mConnectedThread.cancel();
				mConnectedThread = null;
			}

			setState(BTConstant.STATE_LISTEN);

			// Start the thread to listen on a BluetoothServerSocket
			if (mSecureAcceptThread == null) {
				mSecureAcceptThread = new AcceptThread(true);
				mSecureAcceptThread.start();
			}
			if (mInsecureAcceptThread == null) {
				mInsecureAcceptThread = new AcceptThread(false);
				mInsecureAcceptThread.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**连接终端（安全非安全模式）*/
	public synchronized void connect(BluetoothDevice device, boolean secure) {
		if (D)
			Log.d(TAG, "connect to: " + device);
		try {
			// Cancel any thread attempting to make a connection
			if (mState == BTConstant.STATE_CONNECTING) {
				if (mConnectThread != null) {
					mConnectThread.cancel();
					mConnectThread = null;
				}
			}

			// Cancel any thread currently running a connection
			if (mConnectedThread != null) {
				mConnectedThread.cancel();
				mConnectedThread = null;
			}

			// Start the thread to connect with the given device
			mConnectThread = new ConnectThread(device, secure);
			mConnectThread.start();
			setState(BTConstant.STATE_CONNECTING);
		} catch (Exception e) {
			connectionFailed();
		}
	}
	
	/**连接终端*/
	public boolean connectDevice(BluetoothDevice device) {
		if (device == null) {
			return false;
		}
		try {
			BluetoothSocket bs = device.createRfcommSocketToServiceRecord(BTConstant.MY_UUID_INSECURE);
			if (bs != null) {
				bs.connect();
				bs.close();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		connectionFailed();

		return false;
	}

	/**连接成功*/
	public synchronized void connected(BluetoothSocket socket, BluetoothDevice device, final String socketType) {
		if (D)
			Log.d(TAG, "connected, Socket Type:" + socketType);
		try {

			// Cancel the thread that completed the connection
			if (mConnectThread != null) {
				mConnectThread.cancel();
				mConnectThread = null;
			}

			// Cancel any thread currently running a connection
			if (mConnectedThread != null) {
				mConnectedThread.cancel();
				mConnectedThread = null;
			}

			// Cancel the accept thread because we only want to connect to one device
			if (mSecureAcceptThread != null) {
				mSecureAcceptThread.cancel();
				mSecureAcceptThread = null;
			}
			if (mInsecureAcceptThread != null) {
				mInsecureAcceptThread.cancel();
				mInsecureAcceptThread = null;
			}

			// Start the thread to manage the connection and perform transmissions
			mConnectedThread = new ConnectedThread(socket, socketType);
			mConnectedThread.start();

			// Send the name of the connected device back to the UI Activity
			Message msg = mHandler.obtainMessage(BluetoolUtil.MESSAGE_DEVICE_NAME);
			Bundle bundle = new Bundle();
			bundle.putString(BluetoolUtil.DEVICE_NAME, device.getName());
			msg.setData(bundle);
			mHandler.sendMessage(msg);

			setState(BTConstant.STATE_CONNECTED);
		} catch (Exception e) {
			connectionFailed();
		}
	}

	/**发送数据给终端*/
	public boolean write(byte[] out) {
		try {
			// Create temporary object
			ConnectedThread r;
			// Synchronize a copy of the ConnectedThread
			synchronized (this) {
				if (mState != BTConstant.STATE_CONNECTED)
					return false;
				r = mConnectedThread;
			}
			// Perform the write unsynchronized
			r.write(out);
		} catch (Exception e) {
		}
		return true;
	}

	/**连接失败*/
	private void connectionFailed() {
		// Send a failure message back to the Activity
		Message msg = mHandler.obtainMessage(BluetoolUtil.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(BluetoolUtil.TOAST, "Unable to connect device");
		msg.setData(bundle);
		mHandler.sendMessage(msg);

		// Start the service over to restart listening mode
		BluetoothChatService.this.start();
	}

	/**连接丢失*/
	private void connectionLost() {
		// Send a failure message back to the Activity
		Message msg = mHandler.obtainMessage(BluetoolUtil.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(BluetoolUtil.TOAST, "Device connection was lost");
		msg.setData(bundle);
		mHandler.sendMessage(msg);

		// Start the service over to restart listening mode
		BluetoothChatService.this.start();
	}

	/**监听线程*/
	private class AcceptThread extends Thread {
		// The local server socket
		private final BluetoothServerSocket mmServerSocket;
		private String mSocketType;

		public AcceptThread(boolean secure) {
			secure = true;
			BluetoothServerSocket tmp = null;
			mSocketType = secure ? "Secure" : "Insecure";

			// Create a new listening server socket
			try {
				if (secure && mAdapter != null && mAdapter.isEnabled()) {
					tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE, BTConstant.MY_UUID_SECURE);
				}
			} catch (IOException e) {
				Log.e(TAG, "Socket Type: " + mSocketType + "listen() failed", e);
			}
			mmServerSocket = tmp;
		}

		/**取消*/
		public void cancel() {
			try {
				if (null != mmServerSocket)
					mmServerSocket.close();
			} catch (Exception e) {
				Log.e(TAG, "Socket Type" + mSocketType + "close() of server failed", e);
			}
		}
	}

	/**
	 * This thread runs while attempting to make an outgoing connection with a
	 * device. It runs straight through; the connection either succeeds or fails.
	 */
	private class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;
		private String mSocketType;

		public ConnectThread(BluetoothDevice device, boolean secure) {
			secure = true;
			mmDevice = device;
			BluetoothSocket tmp = null;
			mSocketType = secure ? "Secure" : "Insecure";

			try {
				if (secure) {
					tmp = device.createRfcommSocketToServiceRecord(BTConstant.MY_UUID_SECURE);
				}
			} catch (Exception e) {
				Log.e(TAG, "Socket Type: " + mSocketType + "create() failed", e);
			}
			mmSocket = tmp;
		}

		public void run() {
			Log.i(TAG, "BEGIN mConnectThread SocketType:" + mSocketType);
			setName("ConnectThread" + mSocketType);

			try {
				if (null != mAdapter)
					mAdapter.cancelDiscovery();
				if (null != mmSocket)
					mmSocket.connect();
			} catch (IOException e) {
				try {
					Log.e(TAG, "" + mmDevice.getName());
					if (null != mmSocket)
						mmSocket.close();
				} catch (IOException e2) {
					Log.e(TAG, "unable to close() " + mSocketType + " socket during connection failure", e2);
				}
				connectionFailed();
				return;
			}

			synchronized (BluetoothChatService.this) {
				mConnectThread = null;
			}

			connected(mmSocket, mmDevice, mSocketType);
		}

		public void cancel() {
			try {
				if (null != mmSocket)
					mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect " + mSocketType + " socket failed", e);
			}
		}
	}

	/**
	 * This thread runs during a connection with a remote device. It handles all
	 * incoming and outgoing transmissions.
	 */
	private class ConnectedThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

		public ConnectedThread(BluetoothSocket socket, String socketType) {
			Log.d(TAG, "create ConnectedThread: " + socketType);
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
				Log.e(TAG, "temp sockets not created", e);
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run() {
			Log.i(TAG, "BEGIN mConnectedThread");
			byte[] buffer = new byte[1024];
			int bytes;

			while (true) {
				try {
					if (null != mmInStream) {
						bytes = mmInStream.read(buffer);
						if (null != mHandler)
							mHandler.obtainMessage(BluetoolUtil.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
					}
				} catch (IOException e) {
					Log.e(TAG, "disconnected", e);
					connectionLost();
					try {
						if (null != mmInStream)
							mmInStream.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
					try {
						if (null != mmOutStream)
							mmOutStream.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
					try {
						if (null != mmSocket)
							mmSocket.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
					BluetoothChatService.this.start();
					break;
				}
			}
		}

		/**写入*/
		public void write(byte[] buffer) {
			try {
				if (null != mmOutStream) {
					mmOutStream.write(buffer);
					if (null != mHandler)
						mHandler.obtainMessage(BluetoolUtil.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
				}
			} catch (IOException e) {
				Log.e(TAG, "Exception during write", e);
			}
		}

		/**取消*/
		public void cancel() {
			try {
				if (null != mmSocket){
					mmSocket.close();
				}
			} catch (IOException e) {
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}
	}
}
