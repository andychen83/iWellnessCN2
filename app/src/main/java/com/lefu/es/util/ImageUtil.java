package com.lefu.es.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
/**
 * 图片工具类
 * @author Leon
 * 2015-11-19
 */
public class ImageUtil {
	private static final String TAG = "ImageUtil";

	public static Uri getImageContentUri(Context context, File imageFile) {
		String filePath = imageFile.getAbsolutePath();
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Images.Media._ID },
				MediaStore.Images.Media.DATA + "=? ",
				new String[] { filePath }, null);

		if (cursor != null && cursor.moveToFirst()) {
			int id = cursor.getInt(cursor
					.getColumnIndex(MediaStore.MediaColumns._ID));
			Uri baseUri = Uri.parse("content://media/external/images/media");
			return Uri.withAppendedPath(baseUri, "" + id);
		} else {
			if (imageFile.exists()) {
				ContentValues values = new ContentValues();
				values.put(MediaStore.Images.Media.DATA, filePath);
				return context.getContentResolver().insert(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			} else {
				return null;
			}
		}
	}

	public static String saveBitmap(String savePath, String picName, Bitmap bm) {
		String photoUrl = "";
		if (null == savePath || null == picName || null == bm) {
			return photoUrl;
		}
		File file = new File(savePath);
		if (!file.exists()) {// 如果目录不存在就创建目录
			file.mkdirs();
		}

		try {
			FileOutputStream out = new FileOutputStream(savePath + picName);
			bm.compress(Bitmap.CompressFormat.PNG, 90, out);
			photoUrl = savePath + picName;
			out.flush();
			out.close();
			Log.i(TAG, "已经保存");
		} catch (FileNotFoundException e) {
			Log.i(TAG, "保存失败:" + e.getMessage());
			photoUrl = "";
		} catch (IOException e) {
			Log.i(TAG, "保存失败:" + e.getMessage());
			photoUrl = "";
		}
		return photoUrl;
	}

	public Bitmap getBitmapTodifferencePath(String path, Context context) {
		if (path.length() < 7) {
			return null;
		}
		String str = path.substring(0, 7);
		if ("content".equals(str)) {
			try {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;

				BitmapFactory.decodeStream(context.getContentResolver().openInputStream(Uri.parse(path)), null, options);

				int height = options.outHeight;

				if (options.outWidth > 100) {
					options.inSampleSize = options.outWidth / 100 + 1 + 1;
					options.outWidth = 100;
					height = options.outHeight / options.inSampleSize;
					options.outHeight = height;
				}
				options.inJustDecodeBounds = false;
				options.inPurgeable = true;
				options.inInputShareable = true;

				return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(Uri.parse(path)), null, options);

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, options);

			int height = options.outHeight;
			if (options.outWidth > 100) {
				options.inSampleSize = options.outWidth / 100 + 1 + 1;
				options.outWidth = 100;
				height = options.outHeight / options.inSampleSize;
				options.outHeight = height;
			}
			options.inJustDecodeBounds = false;
			options.inPurgeable = true;
			options.inInputShareable = true;
			return BitmapFactory.decodeFile(path, options);
		}

		return null;
	}

}
