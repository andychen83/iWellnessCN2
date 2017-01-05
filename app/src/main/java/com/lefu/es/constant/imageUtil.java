package com.lefu.es.constant;

import java.io.FileNotFoundException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
/**
 * 图片工具
 * 
 * @author Leon 2015-11-17
 */
public class imageUtil {

	public static Bitmap getBitmapfromPath(String path) {
		Bitmap bm = null;
		if (null != path && !"".equals(path)) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 2;
			bm = BitmapFactory.decodeFile(path, options);
		}
		return bm;
	}

	/***
	 * 从指定目录获取图片
	 * @param path
	 * @return
	 */
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
