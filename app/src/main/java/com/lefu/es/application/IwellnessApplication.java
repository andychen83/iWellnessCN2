package com.lefu.es.application;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.lefu.es.cache.CacheHelper;
import com.lefu.es.db.DatabaseCreatOrUpdateHelper;

/**
 * 作者: Administrator on 2016/12/13.
 * 作用:
 */

public class IwellnessApplication extends MultiDexApplication{
    public static IwellnessApplication app = null;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
		/*异常捕捉*/
//		CrashHandler crashHandler = CrashHandler.getInstance();
//		crashHandler.init(getApplicationContext());
        Fresco.initialize(getApplicationContext());
        new IwellnessApplication.initAsyncTask().execute();//初始化营养参数
    }

    /**
     * 加载基础数据
     */
    private void initCacheDates() {
        // 基础数据
        if(null== CacheHelper.nutrientList || CacheHelper.nutrientList.size()==0){
            SQLiteDatabase database = DatabaseCreatOrUpdateHelper.createOrUpdateDB(this);
            CacheHelper.cacheAllNutrientsDate(this, database);
            if (null != database) {
                database.close();
                database = null;
            }
        }
    }

    class initAsyncTask extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... arg0) {
            initCacheDates();
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            Log.i("Application*****", "缓存初始化完成");
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

    }
}
