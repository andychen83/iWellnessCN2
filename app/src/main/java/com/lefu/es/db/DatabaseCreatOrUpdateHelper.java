package com.lefu.es.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.lefu.es.util.LogUtils;
import com.lefu.iwellness.newes.cn.system.R;

public class DatabaseCreatOrUpdateHelper {
    private static String    TAG         = DatabaseCreatOrUpdateHelper.class.getSimpleName();
    private static final String BASE_DATABASE_NAME = "basedb.db";
	public static final String PACKAGE_NAME = "com.lefu.iwellness.newes.cn.system";
	public static final String DATABASE_PAGE_PATH      = "/data" + Environment.getDataDirectory().getAbsolutePath() + "/"+ PACKAGE_NAME+"/databases";
	public static final String BASE_DATABASE_PATH      = DATABASE_PAGE_PATH+"/"+BASE_DATABASE_NAME; // 在手机里存放数据库的位置,自定义位置有点麻烦
    private static final int BUFFER_SIZE = 400000;

    public static SQLiteDatabase createOrUpdateDB(Context context) {
        SQLiteDatabase db = null;
        File f = new File(BASE_DATABASE_PATH);
        if (!f.exists()) {
            File f1 = new File(DATABASE_PAGE_PATH);
            f1.mkdirs();

            try {
                // 创建导入基础数据
                importDateBase(context);
                db = SQLiteDatabase.openOrCreateDatabase(BASE_DATABASE_PATH, null);
            } catch (FileNotFoundException e) {
                LogUtils.e(TAG, "onCreate DB failed:" + e.getMessage());
            } catch (IOException e) {
                LogUtils.e(TAG, "onCreate DB failed:" + e.getMessage());
            }
        } else {
            LogUtils.e(TAG, "f.exists()");
            try {
                // 初始化基础数据库
                context.deleteDatabase(BASE_DATABASE_PATH);
                importDateBase(context);
                db = SQLiteDatabase.openOrCreateDatabase(BASE_DATABASE_PATH, null);
            } catch (FileNotFoundException e) {
                LogUtils.e(TAG, "onCreate DB failed:" + e.getMessage());
            } catch (IOException e) {
                LogUtils.e(TAG, "onCreate DB failed:" + e.getMessage());
            }

        }
        return db;
    }

    public static void importDateBase(Context context) throws IOException {
        // 初始化基础数据库
        InputStream is = context.getResources().openRawResource(R.raw.basedb);// 欲导入的数据库
        FileOutputStream fos = new FileOutputStream(BASE_DATABASE_PATH);
        byte[] buffer = new byte[BUFFER_SIZE];
        int count = 0;
        while ((count = is.read(buffer)) > 0) {
            fos.write(buffer, 0, count);
        }
        fos.close();
        is.close();
    }
}
