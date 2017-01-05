package com.lefu.es.util;

import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
/**
 * SharedPreferences工具类
 * @author Leon
 * 2015-11-19
 */
public class SharedPreferencesUtil {
	public Context context;
	private String TAG = "SharedPreferencesUtil";

	public SharedPreferencesUtil(Context con) {
		super();
		this.context = con;
	}

	/**保存*/
	@SuppressWarnings("rawtypes")
	public boolean saveSharedPreferences(String fileName, int mode, Map<String, Object> map) {
		boolean result = true;
		SharedPreferences sharedPreferences = null;
		if (null != fileName && !"".equals(fileName)) {
			try {
				switch (mode) {
					case Context.MODE_APPEND :
						sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_APPEND);
						break;

					default :
						sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
						break;
				}
				if (null != sharedPreferences && null != map && map.size() > 0) {
					SharedPreferences.Editor editor = sharedPreferences.edit();
					Iterator iter = map.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry entry = (Map.Entry) iter.next();
						String key = entry.getKey().toString();
						Object val = entry.getValue();

						if (val instanceof Integer) {
							editor.putInt(key, (Integer) val);
						} else if (val instanceof String) {
							editor.putString(key, (String) val);
						} else if (val instanceof Float) {
							editor.putFloat(key, (Float) val);
						} else if (val instanceof Long) {
							editor.putLong(key, (Long) val);
						} else if (val instanceof Boolean) {
							editor.putBoolean(key, (Boolean) val);
						} else {
							editor.putString(key, (String) val);
						}
					}
					editor.commit();
					Log.i(TAG, "文件内容添加成功：" + fileName);
				}
			} catch (Exception e) {
				Log.i(TAG, "文件内容添加失败：" + e.getMessage());
				result = false;
			}

		} else {
			Log.i(TAG, "文件内容添加失败-->文件名不存在");
			result = false;
		}
		return result;
	}

	/**编辑*/
	public boolean editSharedPreferences(String fileName, String key, Object val) {
		boolean result = true;
		SharedPreferences sharedPreferences = null;
		if (null != fileName && !"".equals(fileName)) {
			try {
				sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				if (val instanceof Integer) {
					editor.putInt(key, (Integer) val);
				} else if (val instanceof String) {
					editor.putString(key, (String) val);
				} else if (val instanceof Float) {
					editor.putFloat(key, (Float) val);
				} else if (val instanceof Long) {
					editor.putLong(key, (Long) val);
				} else if (val instanceof Boolean) {
					editor.putBoolean(key, (Boolean) val);
				} else {
					editor.putString(key, (String) val);
				}
				editor.commit();
				Log.i(TAG, "文件内容修改成功：" + fileName);
			} catch (Exception e) {
				result = false;
				Log.i(TAG, "文件内容修改失败：" + fileName);
			}
		} else {
			Log.i(TAG, "文件内容修改失败-->文件名不存在");
			result = false;
		}
		return result;
	}

	/**读取*/
	public Object readbackUp(String fileName, String key, Object retValueType) {
		try {
			SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
			if (retValueType instanceof Integer) {
				return sharedPreferences.getInt(key, 0);
			} else if (retValueType instanceof String) {
				return sharedPreferences.getString(key, "");
			} else if (retValueType instanceof Float) {
				return sharedPreferences.getFloat(key, 0.0f);
			} else if (retValueType instanceof Long) {
				return sharedPreferences.getLong(key, 0l);
			} else if (retValueType instanceof Boolean) {
				return sharedPreferences.getBoolean(key, false);
			} else {
				return sharedPreferences.getString(key, "");
			}

		} catch (Exception e) {
			Log.e(TAG, "获取接点信息失败:" + e.getMessage());
			return null;
		}
	}
	
}
