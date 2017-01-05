package com.lefu.es.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
/**
 * 图片
 * @author Leon
 * 2015-11-19
 */
public class Image {
	public static final int ASPECT_FREE = 1;
	public static final int ASPECT_SQUARE = 2;
	private static int width = 0;
	private static int height = 0;
	private static int RESULT_PICTURE = 2;
	static File tempImage = null;

	public static void getImage(Activity activity, int requestCode) {
		Intent toPhoto = new Intent(Intent.ACTION_GET_CONTENT);
		toPhoto.setType("image/*");
		activity.startActivityForResult(Intent.createChooser(toPhoto, "ѡ��ͼƬ"),
				requestCode);
	}

	public static void getImageFromCamere(Activity activity, String imagePath,
			int requestCode) {
		tempImage = new File(imagePath);//
		Intent toCamere = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		toCamere.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempImage));
		activity.startActivityForResult(toCamere, requestCode);
	}

	public static void getImageFromCamere(Context context) {
		Intent toCamere = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		context.startActivity(toCamere);
	}

	public static void doCropImage(Activity activity, String imagePath,
			int imagex, int imagey, int aspectMode) {
		if (imagex != 0 && imagey != 0) {
			width = imagex;
			height = imagey;
		}
		tempImage = new File(imagePath);
		Intent intent = getCropImageIntent(aspectMode);
		activity.startActivityForResult(Intent.createChooser(intent, "����"),
				RESULT_PICTURE);
	}

	public static boolean file2file(String source, String target) {
		

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(source);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(target);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (fis != null && fos != null) {
			byte[] buff = new byte[100 * 1024];
			int readed = -1;
			try {
				while ((readed = fis.read(buff)) > 0)
					fos.write(buff, 0, readed);
				fis.close();
				fos.close();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if(fis==null){
		}else if(fos==null){
			File file = new File(target);
			if(!file.exists()){
				try {
					file.createNewFile();
					//!!
					file2file(source, target);
				} catch (IOException e) {
				}
			}
		}
		return false;
	}

	public static Intent getCropImageIntent(int aspectMode) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		try {
			intent.setDataAndType(Uri.fromFile(tempImage), "image/*");
			intent.putExtra("crop", "true");
			if (aspectMode == ASPECT_SQUARE) {
				intent.putExtra("aspectX", 1);
				intent.putExtra("aspectY", 1);
			}
			if (width != 0 && height != 0) {
				intent.putExtra("outputX", width);
				intent.putExtra("outputY", height);
			}
			intent.putExtra("output", Uri.fromFile(tempImage));
			intent.putExtra("outputFormat", "JPEG");
			intent.putExtra("return-data", true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return intent;
	}

	public static Bitmap drawableToBitmap(Drawable drawable)
	{
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		Bitmap bitmap = Bitmap.createBitmap(width, height, config);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;
	}

	public static Drawable zoomDrawable(Drawable drawable, int w, int h) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap oldbmp = drawableToBitmap(drawable);
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) w / width);
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
				matrix, true);
		return new BitmapDrawable(newbmp);
	}

	public static Bitmap file2Bitmap(String f, int scale) {
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = scale;
			FileInputStream fis = new FileInputStream(f);
			bitmap = BitmapFactory.decodeStream(fis, null, options);
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	public static boolean bitmap2File(Bitmap bmp, String path) {
		CompressFormat format = Bitmap.CompressFormat.JPEG;
		int quality = 100;
		OutputStream stream = null;
		try {
			stream = new FileOutputStream(path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return bmp.compress(format, quality, stream);
	}
	
	/**
	 * 获取圆角位图的方法
	 * @param bitmap 需要转化成圆角的位图
	 * @param pixels 圆角的度数，数值越大，圆角越大
	 * @return 处理后的圆角位图
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}
	
}
