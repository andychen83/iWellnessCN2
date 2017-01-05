package com.lefu.es.db;

import android.content.Context;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
	private static final String name = "lefu.db";
	private static final int version = 5;
	public DBOpenHelper(Context context) {
		super(context, name, null, version);
	}

	/**
	 * 创建数据库
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		/*用户表*/
		db.execSQL("DROP TABLE IF EXISTS user");
		/*用户测量记录表*/
		db.execSQL("DROP TABLE IF EXISTS userrecord");

		db.execSQL("CREATE TABLE IF NOT EXISTS user (id integer primary key autoincrement, username varchar(30),ugroup varchar(10),"
				+ "sex varchar(2),level varchar(10),bheigth real, ageyear INTEGER,agemonth INTEGER,number INTEGER,scaletype varchar(10),uniqueid varchar(10),birth varchar(20),per_photo varchar(200),targweight real ,danwei varchar(10))");

		db.execSQL("CREATE TABLE IF NOT EXISTS userrecord (id integer primary key autoincrement, useid INTEGER,scaleType varchar(10) " + ",ugroup varchar(10),recordtime DATETIME DEFAULT CURRENT_TIMESTAMP,comparerecord varchar(50),rweight real,rbmi real,rbone real"
				+ ",rbodyfat real,rmuscle real,rbodywater real,rvisceralfat real,rbmr real,photo varchar(200),bodyage real)");

	}

	/**
	 * 更新
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		onCreate(db);
	}
}
