package com.lefu.es.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.lefu.es.db.DBOpenHelper;
import com.lefu.es.entity.Records;
import com.lefu.es.util.UtilTooth;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
/**
 * 记录数据库操作
 * @author Leon
 * 2015-11-17
 *
 */
public class RecordService {
	private DBOpenHelper dbHelper;
	private SQLiteDatabase dbs;
	private Context context;

	public RecordService(Context context) {
		this.dbHelper = new DBOpenHelper(context);
		this.context = context;
	}

	public void closeDB() {
		if (null != this.dbHelper) {
			dbHelper.close();
			dbHelper = null;
		}
	}

	/**保存记录*/
	public void save(Records pe) {
		try {
			if (pe != null && pe.getUseId() > 0) {
				dbs = dbHelper.getWritableDatabase();
				if (pe.getRecordTime() != null && pe.getRecordTime().length() > 0) {
					dbs.execSQL("insert into userrecord(useid,scaleType,ugroup,recordtime,comparerecord,rweight, rbmi, rbone, rbodyfat, rmuscle, rbodywater, rvisceralfat, rbmr,bodyage,photo) " + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
							new Object[]{pe.getUseId(), pe.getScaleType(), pe.getUgroup(), pe.getRecordTime(), pe.getCompareRecord(), pe.getRweight(), pe.getRbmi(),
									pe.getRbone(), pe.getRbodyfat(), pe.getRmuscle(), pe.getRbodywater(), pe.getRvisceralfat(), pe.getRbmr(), pe.getBodyAge(),pe.getRphoto()});
				} else {
					dbs.execSQL("insert into userrecord(useid,scaleType,ugroup,comparerecord,rweight, rbmi, rbone, rbodyfat, rmuscle, rbodywater, rvisceralfat, rbmr,bodyage,photo) " + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{pe.getUseId(), pe.getScaleType(), pe.getUgroup(),
							pe.getCompareRecord(), pe.getRweight(), pe.getRbmi(), pe.getRbone(), pe.getRbodyfat(),
							pe.getRmuscle(), pe.getRbodywater(), pe.getRvisceralfat(), pe.getRbmr(), pe.getBodyAge(),pe.getRphoto()});

				}
				dbs.close();
			}
		} catch (Exception e) {
			Log.e("RecordService", "save Record failed" + e.getMessage());
		}
	}

	/**删除记录*/
	public void delete(Records pe) throws Exception {
		dbs = dbHelper.getWritableDatabase();
		dbs.execSQL("delete from userrecord where id=?", new Object[]{pe.getId()});

		deletePhoto(pe.getRphoto());
	}

	/**删除图片*/
	public void deletePhoto(String path) {
		if (path != null && path.length() > 3) {
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
		}
	}

	/**更新图片*/
	public void updatePhoto(int rID, String photoName) {
		dbs = dbHelper.getWritableDatabase();
		dbs.beginTransaction();
		dbs.execSQL("update  userrecord set photo=? where id=? ", new Object[]{photoName, rID});
		dbs.setTransactionSuccessful();
		dbs.endTransaction();
	}

	/**更新记录*/
	public void update(Records pe) throws Exception {
		dbs = dbHelper.getWritableDatabase();
		dbs.beginTransaction();
		dbs.execSQL("update  userrecord set useid=?,scaleType=?,ugroup=?,recordtime=now(),comparerecord=?,rweight=?, rbmi=?, rbone=?, " + "rbodyfat=?, rmuscle=?, rbodywater=?, rvisceralfat=?, rbmr=?,bodyage=? where id=? ",
				new Object[]{pe.getUseId(), pe.getScaleType(), pe.getUgroup(), pe.getRweight(), pe.getRbmi(), pe.getRbone(), pe.getRbodyfat(), pe.getRmuscle(), pe.getRbodywater(), pe.getRvisceralfat(), pe.getRbmr(), pe.getBodyAge(), pe.getId()});
		dbs.setTransactionSuccessful();
		dbs.endTransaction();
	}

	/**根据id查询记录*/
	public Records find(int id) throws Exception {
		dbs = dbHelper.getReadableDatabase();
		String sql = "select useid,scaleType,ugroup,datetime(recordtime,'localtime'),comparerecord,rweight, rbmi, rbone, rbodyfat, rmuscle, rbodywater," + " rvisceralfat, rbmr,photo,bodyage from userrecord where id=? order by recordtime desc  ";
		Cursor cursor = dbs.rawQuery(sql, new String[]{String.valueOf(id)});
		Records pes = null;
		if (cursor.moveToFirst()) {
			int useid = cursor.getInt(cursor.getColumnIndex("useid"));
			String scaletype = cursor.getString(cursor.getColumnIndex("scaleType"));
			String ugroup = cursor.getString(cursor.getColumnIndex("ugroup"));
			String recordtime = cursor.getString(cursor.getColumnIndex("datetime(recordtime,'localtime')"));
			String comparerecord = cursor.getString(cursor.getColumnIndex("comparerecord"));

			float rweight = cursor.getFloat(cursor.getColumnIndex("rweight"));
			float rbmi = cursor.getFloat(cursor.getColumnIndex("rbmi"));
			float rbone = cursor.getFloat(cursor.getColumnIndex("rbone"));
			float rbodyfat = cursor.getFloat(cursor.getColumnIndex("rbodyfat"));
			float rmuscle = cursor.getFloat(cursor.getColumnIndex("rmuscle"));
			float rbodywater = cursor.getFloat(cursor.getColumnIndex("rbodywater"));
			float rvisceralfat = cursor.getFloat(cursor.getColumnIndex("rvisceralfat"));
			float rbmr = cursor.getFloat(cursor.getColumnIndex("rbmr"));

			float bodyage = cursor.getFloat(cursor.getColumnIndex("bodyage"));

			pes = new Records(id, useid, scaletype, ugroup, recordtime, comparerecord, rweight, rbmi, rbone, rbodyfat, rmuscle, rbodywater, rvisceralfat, rbmr, bodyage);

			String photo = cursor.getString(cursor.getColumnIndex("photo"));
			if (photo != null && photo.length() > 3) {
				pes.setRphoto(photo);
			}
		}
		cursor.close();
		dbs.close();
		return pes;
	}

	/**根据用户id获取最新一条记录*/
	public Records findLastRecords(int useid,String scale) throws Exception {
		dbs = dbHelper.getReadableDatabase();
		Cursor cursor = dbs.rawQuery("select id, useid,scaleType,ugroup,datetime(recordtime,'localtime'),comparerecord,rweight, rbmi, rbone, rbodyfat, rmuscle, rbodywater,"
				+ " rvisceralfat, rbmr,photo,max(datetime(recordtime,'localtime')),bodyage from userrecord where useid = ? and scaletype=?  group by scaletype order by recordtime desc", new String[]{String.valueOf(useid),scale});
		Records pes = null;
		if (cursor.moveToFirst()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String scaletype = cursor.getString(cursor.getColumnIndex("scaleType"));
			String ugroup = cursor.getString(cursor.getColumnIndex("ugroup"));
			String recordtime = cursor.getString(cursor.getColumnIndex("datetime(recordtime,'localtime')"));
			String comparerecord = cursor.getString(cursor.getColumnIndex("comparerecord"));

			float rweight = cursor.getFloat(cursor.getColumnIndex("rweight"));
			float rbmi = cursor.getFloat(cursor.getColumnIndex("rbmi"));
			float rbone = cursor.getFloat(cursor.getColumnIndex("rbone"));
			float rbodyfat = cursor.getFloat(cursor.getColumnIndex("rbodyfat"));
			float rmuscle = cursor.getFloat(cursor.getColumnIndex("rmuscle"));
			float rbodywater = cursor.getFloat(cursor.getColumnIndex("rbodywater"));
			float rvisceralfat = cursor.getFloat(cursor.getColumnIndex("rvisceralfat"));
			float rbmr = cursor.getFloat(cursor.getColumnIndex("rbmr"));
			float bodyage = cursor.getFloat(cursor.getColumnIndex("bodyage"));
			pes = new Records(id, useid, scaletype, ugroup, recordtime, comparerecord, rweight, rbmi, rbone, rbodyfat, rmuscle, rbodywater, rvisceralfat, rbmr, bodyage);
			String photo = cursor.getString(cursor.getColumnIndex("photo"));
			if (photo != null && photo.length() > 3) {
				pes.setRphoto(photo);
			}
		}
		cursor.close();
		dbs.close();
		return pes;
	}

	/**根据用户id获取最新一条记录*/
	public Records findLastRecords(int useid) throws Exception {
		dbs = dbHelper.getReadableDatabase();
		Cursor cursor = dbs.rawQuery("select id, useid,scaleType,ugroup,datetime(recordtime,'localtime'),comparerecord,rweight, rbmi, rbone, rbodyfat, rmuscle, rbodywater,"
				+ " rvisceralfat, rbmr,photo,max(datetime(recordtime,'localtime')),bodyage from userrecord where useid = ?  group by scaletype order by recordtime desc", new String[]{String.valueOf(useid)});
		Records pes = null;
		if (cursor.moveToFirst()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String scaletype = cursor.getString(cursor.getColumnIndex("scaleType"));
			String ugroup = cursor.getString(cursor.getColumnIndex("ugroup"));
			String recordtime = cursor.getString(cursor.getColumnIndex("datetime(recordtime,'localtime')"));
			String comparerecord = cursor.getString(cursor.getColumnIndex("comparerecord"));

			float rweight = cursor.getFloat(cursor.getColumnIndex("rweight"));
			float rbmi = cursor.getFloat(cursor.getColumnIndex("rbmi"));
			float rbone = cursor.getFloat(cursor.getColumnIndex("rbone"));
			float rbodyfat = cursor.getFloat(cursor.getColumnIndex("rbodyfat"));
			float rmuscle = cursor.getFloat(cursor.getColumnIndex("rmuscle"));
			float rbodywater = cursor.getFloat(cursor.getColumnIndex("rbodywater"));
			float rvisceralfat = cursor.getFloat(cursor.getColumnIndex("rvisceralfat"));
			float rbmr = cursor.getFloat(cursor.getColumnIndex("rbmr"));
			float bodyage = cursor.getFloat(cursor.getColumnIndex("bodyage"));
			pes = new Records(id, useid, scaletype, ugroup, recordtime, comparerecord, rweight, rbmi, rbone, rbodyfat, rmuscle, rbodywater, rvisceralfat, rbmr, bodyage);
			String photo = cursor.getString(cursor.getColumnIndex("photo"));
			if (photo != null && photo.length() > 3) {
				pes.setRphoto(photo);
			}
		}
		cursor.close();
		dbs.close();
		return pes;
	}

	/**根据称类型和组获取最新一条记录*/
	public Records findLastRecordsByScaleType(String scaleType, String sgroup) throws Exception {
		dbs = dbHelper.getReadableDatabase();
		String sql = "select *,max(datetime(recordtime,'localtime')) md from userrecord group by useid having scaleType=? and ugroup=? order by md desc";
		Cursor cursor = dbs.rawQuery(sql, new String[]{scaleType, sgroup});
		Records pes = null;
		if (cursor.moveToFirst()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			int useid = cursor.getInt(cursor.getColumnIndex("useid"));
			String scaletype = cursor.getString(cursor.getColumnIndex("scaleType"));
			String ugroup = cursor.getString(cursor.getColumnIndex("ugroup"));
			String recordtime = cursor.getString(cursor.getColumnIndex("md"));
			String comparerecord = cursor.getString(cursor.getColumnIndex("comparerecord"));

			float rweight = cursor.getFloat(cursor.getColumnIndex("rweight"));
			float rbmi = cursor.getFloat(cursor.getColumnIndex("rbmi"));
			float rbone = cursor.getFloat(cursor.getColumnIndex("rbone"));
			float rbodyfat = cursor.getFloat(cursor.getColumnIndex("rbodyfat"));
			float rmuscle = cursor.getFloat(cursor.getColumnIndex("rmuscle"));
			float rbodywater = cursor.getFloat(cursor.getColumnIndex("rbodywater"));
			float rvisceralfat = cursor.getFloat(cursor.getColumnIndex("rvisceralfat"));
			float rbmr = cursor.getFloat(cursor.getColumnIndex("rbmr"));
			pes = new Records(id, useid, scaletype, ugroup, recordtime, comparerecord, rweight, rbmi, rbone, rbodyfat, rmuscle, rbodywater, rvisceralfat, rbmr);
			String photo = cursor.getString(cursor.getColumnIndex("photo"));
			if (photo != null && photo.length() > 3) {
				pes.setRphoto(photo);
			}
		}
		cursor.close();
		dbs.close();
		return pes;
	}

	/**根据称类型和用户获取最新一条记录*/
	public Records findLastRecordsByScaleTypeAndUser(String scaleType, String uid) throws Exception {
		dbs = dbHelper.getReadableDatabase();
		String sql = "select *,max(datetime(recordtime,'localtime')) md from userrecord where scaleType=? and useid=? order by md desc";
		Cursor cursor = dbs.rawQuery(sql, new String[]{scaleType, uid});
		Records pes = null;
		if (cursor.moveToFirst()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			int useid = cursor.getInt(cursor.getColumnIndex("useid"));
			String scaletype = cursor.getString(cursor.getColumnIndex("scaleType"));
			String ugroup = cursor.getString(cursor.getColumnIndex("ugroup"));
			String recordtime = cursor.getString(cursor.getColumnIndex("md"));
			String comparerecord = cursor.getString(cursor.getColumnIndex("comparerecord"));

			float rweight = cursor.getFloat(cursor.getColumnIndex("rweight"));
			float rbmi = cursor.getFloat(cursor.getColumnIndex("rbmi"));
			float rbone = cursor.getFloat(cursor.getColumnIndex("rbone"));
			float rbodyfat = cursor.getFloat(cursor.getColumnIndex("rbodyfat"));
			float rmuscle = cursor.getFloat(cursor.getColumnIndex("rmuscle"));
			float rbodywater = cursor.getFloat(cursor.getColumnIndex("rbodywater"));
			float rvisceralfat = cursor.getInt(cursor.getColumnIndex("rvisceralfat"));
			float rbmr = cursor.getFloat(cursor.getColumnIndex("rbmr"));
			float bodyage = cursor.getFloat(cursor.getColumnIndex("bodyage"));
			pes = new Records(id, useid, scaletype, ugroup, recordtime, comparerecord, rweight, rbmi, rbone, rbodyfat, rmuscle, rbodywater, rvisceralfat, rbmr, bodyage);
			String photo = cursor.getString(cursor.getColumnIndex("photo"));
			if (photo != null && photo.length() > 3) {
				pes.setRphoto(photo);
			}
		}
		cursor.close();
		dbs.close();
		return pes;
	}

	/**根据称类型获取最新两条记录*/
	public List<Records> findLastTowRecordsByScaleType(String scaleType, String sgroup) throws Exception {
		dbs = dbHelper.getReadableDatabase();
		String sql = "select * from userrecord where scaleType=? and ugroup=? order by recordtime desc limit 0,5";
		Cursor cursor = dbs.rawQuery(sql, new String[]{scaleType, sgroup});
		List<Records> pes = new ArrayList<Records>();
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			int useid = cursor.getInt(cursor.getColumnIndex("useid"));
			String scaletype = cursor.getString(cursor.getColumnIndex("scaleType"));
			String ugroup = cursor.getString(cursor.getColumnIndex("ugroup"));
			String recordtime = cursor.getString(cursor.getColumnIndex("recordtime"));
			String comparerecord = cursor.getString(cursor.getColumnIndex("comparerecord"));
			float rweight = cursor.getFloat(cursor.getColumnIndex("rweight"));
			float rbmi = cursor.getFloat(cursor.getColumnIndex("rbmi"));
			float rbone = cursor.getFloat(cursor.getColumnIndex("rbone"));
			float rbodyfat = cursor.getFloat(cursor.getColumnIndex("rbodyfat"));
			float rmuscle = cursor.getFloat(cursor.getColumnIndex("rmuscle"));
			float rbodywater = cursor.getFloat(cursor.getColumnIndex("rbodywater"));
			float rvisceralfat = cursor.getFloat(cursor.getColumnIndex("rvisceralfat"));
			float rbmr = cursor.getFloat(cursor.getColumnIndex("rbmr"));
			float bodyage = cursor.getFloat(cursor.getColumnIndex("bodyage"));

			Records rs = new Records(id, useid, scaletype, ugroup, recordtime, comparerecord, rweight, rbmi, rbone, rbodyfat, rmuscle, rbodywater, rvisceralfat, rbmr, bodyage);
			String photo = cursor.getString(cursor.getColumnIndex("photo"));
			if (photo != null && photo.length() > 3) {
				rs.setRphoto(photo);
			}

			pes.add(rs);

		}
		cursor.close();
		dbs.close();

		List<Records> list = new ArrayList<Records>();
		for (int i = pes.size() - 1; i >= 0; i--) {
			list.add(pes.get(i));
		}
		return list;

	}

	/**根据称类型获取最新一条记录*/
	public List<Records> findLastRecordsByScaleType2(String scaleType) throws Exception {
		dbs = dbHelper.getReadableDatabase();
		String sql = "select *,max(datetime(recordtime,'localtime')) md from userrecord group by useid having scaleType=?  order by ugroup asc,recordtime desc";
		Cursor cursor = dbs.rawQuery(sql, new String[]{scaleType});
		List<Records> pes = new ArrayList<Records>();
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			int useid = cursor.getInt(cursor.getColumnIndex("useid"));
			String scaletype = cursor.getString(cursor.getColumnIndex("scaleType"));
			String ugroup = cursor.getString(cursor.getColumnIndex("ugroup"));
			String recordtime = cursor.getString(cursor.getColumnIndex("md"));
			String comparerecord = cursor.getString(cursor.getColumnIndex("comparerecord"));

			float rweight = cursor.getFloat(cursor.getColumnIndex("rweight"));
			float rbmi = cursor.getFloat(cursor.getColumnIndex("rbmi"));
			float rbone = cursor.getFloat(cursor.getColumnIndex("rbone"));
			float rbodyfat = cursor.getFloat(cursor.getColumnIndex("rbodyfat"));
			float rmuscle = cursor.getFloat(cursor.getColumnIndex("rmuscle"));
			float rbodywater = cursor.getFloat(cursor.getColumnIndex("rbodywater"));
			float rvisceralfat = cursor.getFloat(cursor.getColumnIndex("rvisceralfat"));
			float rbmr = cursor.getFloat(cursor.getColumnIndex("rbmr"));
			float bodyage = cursor.getFloat(cursor.getColumnIndex("bodyage"));
			Records rs = new Records(id, useid, scaletype, ugroup, recordtime, comparerecord, rweight, rbmi, rbone, rbodyfat, rmuscle, rbodywater, rvisceralfat, rbmr, bodyage);

			String photo = cursor.getString(cursor.getColumnIndex("photo"));
			if (photo != null && photo.length() > 3) {
				rs.setRphoto(photo);
			}

			pes.add(rs);
		}
		cursor.close();
		dbs.close();
		return pes;
	}

	/**根据id获取最新一条记录*/
	public Records findLastRecordsByUID(int userId) throws Exception {
		dbs = dbHelper.getReadableDatabase();
		Records rs = null;
		String sql = "select *,max(datetime(recordtime,'localtime')) md from userrecord where useid =? order by md desc";
		Cursor cursor = dbs.rawQuery(sql, new String[]{String.valueOf(userId)});
		if (cursor.moveToFirst()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			int useid = cursor.getInt(cursor.getColumnIndex("useid"));
			String scaletype = cursor.getString(cursor.getColumnIndex("scaleType"));
			String ugroup = cursor.getString(cursor.getColumnIndex("ugroup"));
			String recordtime = cursor.getString(cursor.getColumnIndex("md"));
			String comparerecord = cursor.getString(cursor.getColumnIndex("comparerecord"));

			float rweight = cursor.getFloat(cursor.getColumnIndex("rweight"));
			float rbmi = cursor.getFloat(cursor.getColumnIndex("rbmi"));
			float rbone = cursor.getFloat(cursor.getColumnIndex("rbone"));
			float rbodyfat = cursor.getFloat(cursor.getColumnIndex("rbodyfat"));
			float rmuscle = cursor.getFloat(cursor.getColumnIndex("rmuscle"));
			float rbodywater = cursor.getFloat(cursor.getColumnIndex("rbodywater"));
			float rvisceralfat = cursor.getFloat(cursor.getColumnIndex("rvisceralfat"));
			float rbmr = cursor.getFloat(cursor.getColumnIndex("rbmr"));
			float bodyage = cursor.getFloat(cursor.getColumnIndex("bodyage"));
			rs = new Records(id, useid, scaletype, ugroup, recordtime, comparerecord, rweight, rbmi, rbone, rbodyfat, rmuscle, rbodywater, rvisceralfat, rbmr, bodyage);
			String photo = cursor.getString(cursor.getColumnIndex("photo"));
			if (photo != null && photo.length() > 3) {
				rs.setRphoto(photo);
			}
		}
		cursor.close();
		dbs.close();
		return rs;
	}

	/**获取每组用户的最后一条记录*/
	public List<Records> findLastRecords() throws Exception {
		dbs = dbHelper.getReadableDatabase();
		Cursor cursor = dbs.rawQuery("select id, useid,scaleType,ugroup,recordtime,datetime(recordtime,'localtime'),comparerecord,rweight, rbmi, rbone, rbodyfat, "
				+ "rmuscle, rbodywater, rvisceralfat, rbmr,photo,bodyage,recordtime from userrecord where 1=?  group by scaletype,ugroup order by recordtime desc ", new String[]{String.valueOf(1)});

		List<Records> pes = new ArrayList<Records>();
		if (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			int useid = cursor.getInt(cursor.getColumnIndex("useid"));
			String scaletype = cursor.getString(cursor.getColumnIndex("scaleType"));
			String ugroup = cursor.getString(cursor.getColumnIndex("ugroup"));
			String recordtime = cursor.getString(cursor.getColumnIndex("datetime(recordtime,'localtime')"));
			String comparerecord = cursor.getString(cursor.getColumnIndex("comparerecord"));

			float rweight = cursor.getFloat(cursor.getColumnIndex("rweight"));
			float rbmi = cursor.getFloat(cursor.getColumnIndex("rbmi"));
			float rbone = cursor.getFloat(cursor.getColumnIndex("rbone"));
			float rbodyfat = cursor.getFloat(cursor.getColumnIndex("rbodyfat"));
			float rmuscle = cursor.getFloat(cursor.getColumnIndex("rmuscle"));
			float rbodywater = cursor.getFloat(cursor.getColumnIndex("rbodywater"));
			float rvisceralfat = cursor.getFloat(cursor.getColumnIndex("rvisceralfat"));
			float rbmr = cursor.getFloat(cursor.getColumnIndex("rbmr"));
			float bodyage = cursor.getFloat(cursor.getColumnIndex("bodyage"));
			Records rs = new Records(id, useid, scaletype, ugroup, recordtime, comparerecord, rweight, rbmi, rbone, rbodyfat, rmuscle, rbodywater, rvisceralfat, rbmr, bodyage);

			String photo = cursor.getString(cursor.getColumnIndex("photo"));
			if (photo != null && photo.length() > 3) {
				rs.setRphoto(photo);
			}
			pes.add(rs);
		}
		cursor.close();
		dbs.close();
		return pes;
	}

	/**根据用户id获取总数*/
	public Long getCount(int useid) throws Exception {
		dbs = dbHelper.getReadableDatabase();
		Cursor cursor = dbs.rawQuery("select count(*) from userrecord where useid=?", new String[]{String.valueOf(useid)});
		cursor.moveToFirst();
		Long result = cursor.getLong(0);
		cursor.close();
		return result;
	}

	/**获取分页数据*/
	public List<Records> getScrollData(int offset, int maxResult, int useid) throws Exception {
		dbs = dbHelper.getReadableDatabase();
		String sql = "select id, useid,scaleType,ugroup,recordtime,datetime(recordtime,'localtime'),comparerecord,rweight, rbmi, rbone, rbodyfat, rmuscle, rbodywater, rvisceralfat, rbmr,bodyage " + " from userrecord where useid=? order by recordtime desc  limit ?,?";
		Cursor cursor = dbs.rawQuery(sql, new String[]{String.valueOf(useid), String.valueOf(offset), String.valueOf(maxResult)});
		List<Records> pes = new ArrayList<Records>();
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String scaletype = cursor.getString(cursor.getColumnIndex("scaleType"));
			String ugroup = cursor.getString(cursor.getColumnIndex("ugroup"));
			String recordtime = cursor.getString(cursor.getColumnIndex("datetime(recordtime,'localtime')"));
			String comparerecord = cursor.getString(cursor.getColumnIndex("comparerecord"));

			float rweight = cursor.getFloat(cursor.getColumnIndex("rweight"));
			float rbmi = cursor.getFloat(cursor.getColumnIndex("rbmi"));
			float rbone = cursor.getFloat(cursor.getColumnIndex("rbone"));
			float rbodyfat = cursor.getFloat(cursor.getColumnIndex("rbodyfat"));
			float rmuscle = cursor.getFloat(cursor.getColumnIndex("rmuscle"));
			float rbodywater = cursor.getFloat(cursor.getColumnIndex("rbodywater"));
			float rvisceralfat = cursor.getFloat(cursor.getColumnIndex("rvisceralfat"));
			float rbmr = cursor.getFloat(cursor.getColumnIndex("rbmr"));
			float bodyage = cursor.getFloat(cursor.getColumnIndex("bodyage"));
			pes.add(new Records(id, useid, scaletype, ugroup, recordtime, comparerecord, rweight, rbmi, rbone, rbodyfat, rmuscle, rbodywater, rvisceralfat, rbmr, bodyage));
		}
		cursor.close();
		dbs.close();
		return pes;
	}

	/**获取所有数据*/
	public List<Records> getAllDatas() throws Exception {
		dbs = dbHelper.getReadableDatabase();
		String sql = "select *,max(datetime(recordtime,'localtime')) md from userrecord group by ugroup having scaleType=? order by md desc ";
		Cursor cursor = dbs.rawQuery(sql, new String[]{"ce"});
		List<Records> pes = new ArrayList<Records>();
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			int useid = cursor.getInt(cursor.getColumnIndex("useid"));
			String scaletype = cursor.getString(cursor.getColumnIndex("scaleType"));
			String ugroup = cursor.getString(cursor.getColumnIndex("ugroup"));
			String recordtime = cursor.getString(cursor.getColumnIndex("md"));
			String comparerecord = cursor.getString(cursor.getColumnIndex("comparerecord"));

			float rweight = cursor.getFloat(cursor.getColumnIndex("rweight"));
			float rbmi = cursor.getFloat(cursor.getColumnIndex("rbmi"));
			float rbone = cursor.getFloat(cursor.getColumnIndex("rbone"));
			float rbodyfat = cursor.getFloat(cursor.getColumnIndex("rbodyfat"));
			float rmuscle = cursor.getFloat(cursor.getColumnIndex("rmuscle"));
			float rbodywater = cursor.getFloat(cursor.getColumnIndex("rbodywater"));
			float rvisceralfat = cursor.getFloat(cursor.getColumnIndex("rvisceralfat"));
			float rbmr = cursor.getFloat(cursor.getColumnIndex("rbmr"));
			float bodyage = cursor.getFloat(cursor.getColumnIndex("bodyage"));

			Records rs = new Records(id, useid, scaletype, ugroup, recordtime, comparerecord, rweight, rbmi, rbone, rbodyfat, rmuscle, rbodywater, rvisceralfat, rbmr, bodyage);

			String photo = cursor.getString(cursor.getColumnIndex("photo"));
			if (photo != null && photo.length() > 3) {
				rs.setRphoto(photo);
			}

			pes.add(rs);
		}
		cursor.close();
		dbs.close();
		return pes;
	}

	/**根据称类型和组查询*/
	public List<Records> getAllDatasByScaleAndGroup(String scale, String group, float userHeight) throws Exception {
		dbs = dbHelper.getReadableDatabase();
		String sql = "select *,datetime(recordtime,'localtime') md from userrecord where scaleType=? and ugroup=? order by recordtime desc ";
		Cursor cursor = dbs.rawQuery(sql, new String[]{scale, group});
		List<Records> pes = new ArrayList<Records>();
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			int useid = cursor.getInt(cursor.getColumnIndex("useid"));
			String scaletype = cursor.getString(cursor.getColumnIndex("scaleType"));
			String ugroup = cursor.getString(cursor.getColumnIndex("ugroup"));
			String recordtime = cursor.getString(cursor.getColumnIndex("md"));
			String comparerecord = cursor.getString(cursor.getColumnIndex("comparerecord"));

			float rweight = cursor.getFloat(cursor.getColumnIndex("rweight"));
			float rbmi = UtilTooth.countBMI2(rweight, (userHeight / 100));
			rbmi = UtilTooth.myround(rbmi);
			float rbone = cursor.getFloat(cursor.getColumnIndex("rbone"));
			float rbodyfat = cursor.getFloat(cursor.getColumnIndex("rbodyfat"));
			float rmuscle = cursor.getFloat(cursor.getColumnIndex("rmuscle"));
			float rbodywater = cursor.getFloat(cursor.getColumnIndex("rbodywater"));
			float rvisceralfat = cursor.getFloat(cursor.getColumnIndex("rvisceralfat"));
			float rbmr = cursor.getFloat(cursor.getColumnIndex("rbmr"));
			float bodyage = cursor.getFloat(cursor.getColumnIndex("bodyage"));
			Records rs = new Records(id, useid, scaletype, ugroup, recordtime, comparerecord, rweight, rbmi, rbone, rbodyfat, rmuscle, rbodywater, rvisceralfat, rbmr, bodyage);

			String photo = cursor.getString(cursor.getColumnIndex("photo"));
			if (photo != null && photo.length() > 3) {
				rs.setRphoto(photo);
			}

			pes.add(rs);
		}
		cursor.close();
		dbs.close();
		return pes;
	}

	/**根据称类型和组查询所有数据*/
	public List<Records> getAllDatasByScaleAndGroupDesc(String scale, String group, float userHeight) throws Exception {
		dbs = dbHelper.getReadableDatabase();
		String sql = "select *,datetime(recordtime,'localtime') md from userrecord where scaleType=? and ugroup=? order by recordtime asc ";
		Cursor cursor = dbs.rawQuery(sql, new String[]{scale, group});
		List<Records> pes = new ArrayList<Records>();
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			int useid = cursor.getInt(cursor.getColumnIndex("useid"));
			String scaletype = cursor.getString(cursor.getColumnIndex("scaleType"));
			String ugroup = cursor.getString(cursor.getColumnIndex("ugroup"));
			String recordtime = cursor.getString(cursor.getColumnIndex("md"));
			String comparerecord = cursor.getString(cursor.getColumnIndex("comparerecord"));

			float rweight = cursor.getFloat(cursor.getColumnIndex("rweight"));
			float rbmi = UtilTooth.countBMI2(rweight, (userHeight / 100));
			rbmi = UtilTooth.myround(rbmi);
			float rbone = cursor.getFloat(cursor.getColumnIndex("rbone"));
			float rbodyfat = cursor.getFloat(cursor.getColumnIndex("rbodyfat"));
			float rmuscle = cursor.getFloat(cursor.getColumnIndex("rmuscle"));
			float rbodywater = cursor.getFloat(cursor.getColumnIndex("rbodywater"));
			float rvisceralfat = cursor.getFloat(cursor.getColumnIndex("rvisceralfat"));
			float rbmr = cursor.getFloat(cursor.getColumnIndex("rbmr"));
			float bodyage = cursor.getFloat(cursor.getColumnIndex("bodyage"));
			Records rs = new Records(id, useid, scaletype, ugroup, recordtime, comparerecord, rweight, rbmi, rbone, rbodyfat, rmuscle, rbodywater, rvisceralfat, rbmr, bodyage);

			String photo = cursor.getString(cursor.getColumnIndex("photo"));
			if (photo != null && photo.length() > 3) {
				rs.setRphoto(photo);
			}

			pes.add(rs);
		}
		cursor.close();
		dbs.close();
		return pes;
	}

	/**根据秤类型和用户id查询*/
	public List<Records> getAllDatasByScaleAndIDDesc(String scale, int uid, float userHeight) throws Exception {
		dbs = dbHelper.getReadableDatabase();
		String sql = "select * from userrecord where scaleType=? and useid=? order by recordtime desc ";
		Cursor cursor = dbs.rawQuery(sql, new String[]{scale, String.valueOf(uid)});
		List<Records> pes = new ArrayList<Records>();
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			int useid = cursor.getInt(cursor.getColumnIndex("useid"));
			String scaletype = cursor.getString(cursor.getColumnIndex("scaleType"));
			String ugroup = cursor.getString(cursor.getColumnIndex("ugroup"));
			String recordtime = cursor.getString(cursor.getColumnIndex("recordtime"));
			String comparerecord = cursor.getString(cursor.getColumnIndex("comparerecord"));

			float rweight = cursor.getFloat(cursor.getColumnIndex("rweight"));
			float rbmi = cursor.getFloat(cursor.getColumnIndex("rbmi"));
			float rbone = cursor.getFloat(cursor.getColumnIndex("rbone"));
			float rbodyfat = cursor.getFloat(cursor.getColumnIndex("rbodyfat"));
			float rmuscle = cursor.getFloat(cursor.getColumnIndex("rmuscle"));
			float rbodywater = cursor.getFloat(cursor.getColumnIndex("rbodywater"));
			float rvisceralfat = cursor.getFloat(cursor.getColumnIndex("rvisceralfat"));
			float rbmr = cursor.getFloat(cursor.getColumnIndex("rbmr"));
			float bodyage = cursor.getFloat(cursor.getColumnIndex("bodyage"));
			Records rs = new Records(id, useid, scaletype, ugroup, recordtime, comparerecord, rweight, rbmi, rbone, rbodyfat, rmuscle, rbodywater, rvisceralfat, rbmr, bodyage);

			String photo = cursor.getString(cursor.getColumnIndex("photo"));
			if (photo != null) {
				rs.setRphoto(photo);
			}

			pes.add(rs);
		}
		cursor.close();
		dbs.close();
		return pes;
	}


	/**
	 * 给抱婴使用
	 * @param
	 * @param uid
	 * @param
	 * @return
     * @throws Exception
     */
	public List<Records> getAllDatasByScaleAndIDDescForHarmBaby(int uid) throws Exception {
		dbs = dbHelper.getReadableDatabase();
		String sql = "select * from userrecord where useid=? order by recordtime desc ";
		Cursor cursor = dbs.rawQuery(sql, new String[]{ String.valueOf(uid)});
		List<Records> pes = new ArrayList<Records>();
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			int useid = cursor.getInt(cursor.getColumnIndex("useid"));
			String scaletype = cursor.getString(cursor.getColumnIndex("scaleType"));
			String ugroup = cursor.getString(cursor.getColumnIndex("ugroup"));
			String recordtime = cursor.getString(cursor.getColumnIndex("recordtime"));
			String comparerecord = cursor.getString(cursor.getColumnIndex("comparerecord"));

			float rweight = cursor.getFloat(cursor.getColumnIndex("rweight"));
			float rbmi = cursor.getFloat(cursor.getColumnIndex("rbmi"));
			float rbone = cursor.getFloat(cursor.getColumnIndex("rbone"));
			float rbodyfat = cursor.getFloat(cursor.getColumnIndex("rbodyfat"));
			float rmuscle = cursor.getFloat(cursor.getColumnIndex("rmuscle"));
			float rbodywater = cursor.getFloat(cursor.getColumnIndex("rbodywater"));
			float rvisceralfat = cursor.getFloat(cursor.getColumnIndex("rvisceralfat"));
			float rbmr = cursor.getFloat(cursor.getColumnIndex("rbmr"));
			float bodyage = cursor.getFloat(cursor.getColumnIndex("bodyage"));
			Records rs = new Records(id, useid, scaletype, ugroup, recordtime, comparerecord, rweight, rbmi, rbone, rbodyfat, rmuscle, rbodywater, rvisceralfat, rbmr, bodyage);

			String photo = cursor.getString(cursor.getColumnIndex("photo"));
			if (photo != null) {
				rs.setRphoto(photo);
			}

			pes.add(rs);
		}
		cursor.close();
		dbs.close();
		return pes;
	}
	
	

	/**根据秤类型和用户id查询*/
	public List<Records> getAllDatasByScaleAndIDAsc(String scale, int uid, float userHeight) throws Exception {
		dbs = dbHelper.getReadableDatabase();
//		String sql = "select *,datetime(recordtime,'localtime') md from userrecord where scaleType=? and useid=? order by recordtime asc ";
		String sql = "select * from userrecord where scaleType=? and useid=? order by recordtime asc ";
		Cursor cursor = dbs.rawQuery(sql, new String[]{scale, String.valueOf(uid)});
		List<Records> pes = new ArrayList<Records>();
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			int useid = cursor.getInt(cursor.getColumnIndex("useid"));
			String scaletype = cursor.getString(cursor.getColumnIndex("scaleType"));
			String ugroup = cursor.getString(cursor.getColumnIndex("ugroup"));
//			String recordtime = cursor.getString(cursor.getColumnIndex("md"));
			String recordtime = cursor.getString(cursor.getColumnIndex("recordtime"));
			String comparerecord = cursor.getString(cursor.getColumnIndex("comparerecord"));

			float rweight = cursor.getFloat(cursor.getColumnIndex("rweight"));
			float rbmi = cursor.getFloat(cursor.getColumnIndex("rbmi"));
			float rbone = cursor.getFloat(cursor.getColumnIndex("rbone"));
			float rbodyfat = cursor.getFloat(cursor.getColumnIndex("rbodyfat"));
			float rmuscle = cursor.getFloat(cursor.getColumnIndex("rmuscle"));
			float rbodywater = cursor.getFloat(cursor.getColumnIndex("rbodywater"));
			float rvisceralfat = cursor.getFloat(cursor.getColumnIndex("rvisceralfat"));
			float rbmr = cursor.getFloat(cursor.getColumnIndex("rbmr"));
			float bodyage = cursor.getFloat(cursor.getColumnIndex("bodyage"));
			Records rs = new Records(id, useid, scaletype, ugroup, recordtime, comparerecord, rweight, rbmi, rbone, rbodyfat, rmuscle, rbodywater, rvisceralfat, rbmr, bodyage);

			String photo = cursor.getString(cursor.getColumnIndex("photo"));
			if (photo != null && photo.length() > 3) {
				rs.setRphoto(photo);
			}

			pes.add(rs);
		}
		cursor.close();
		dbs.close();
		return pes;
	}

	/**根据秤类型和用户id查询*/
	public List<Records> getAllDatasByScaleAndIDAsForHarmBaby(int uid) throws Exception {
		dbs = dbHelper.getReadableDatabase();
//		String sql = "select *,datetime(recordtime,'localtime') md from userrecord where scaleType=? and useid=? order by recordtime asc ";
		String sql = "select * from userrecord where scaleType=? and useid=? order by recordtime asc ";
		Cursor cursor = dbs.rawQuery(sql, new String[]{ String.valueOf(uid)});
		List<Records> pes = new ArrayList<Records>();
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			int useid = cursor.getInt(cursor.getColumnIndex("useid"));
			String scaletype = cursor.getString(cursor.getColumnIndex("scaleType"));
			String ugroup = cursor.getString(cursor.getColumnIndex("ugroup"));
//			String recordtime = cursor.getString(cursor.getColumnIndex("md"));
			String recordtime = cursor.getString(cursor.getColumnIndex("recordtime"));
			String comparerecord = cursor.getString(cursor.getColumnIndex("comparerecord"));

			float rweight = cursor.getFloat(cursor.getColumnIndex("rweight"));
			float rbmi = cursor.getFloat(cursor.getColumnIndex("rbmi"));
			float rbone = cursor.getFloat(cursor.getColumnIndex("rbone"));
			float rbodyfat = cursor.getFloat(cursor.getColumnIndex("rbodyfat"));
			float rmuscle = cursor.getFloat(cursor.getColumnIndex("rmuscle"));
			float rbodywater = cursor.getFloat(cursor.getColumnIndex("rbodywater"));
			float rvisceralfat = cursor.getFloat(cursor.getColumnIndex("rvisceralfat"));
			float rbmr = cursor.getFloat(cursor.getColumnIndex("rbmr"));
			float bodyage = cursor.getFloat(cursor.getColumnIndex("bodyage"));
			Records rs = new Records(id, useid, scaletype, ugroup, recordtime, comparerecord, rweight, rbmi, rbone, rbodyfat, rmuscle, rbodywater, rvisceralfat, rbmr, bodyage);

			String photo = cursor.getString(cursor.getColumnIndex("photo"));
			if (photo != null && photo.length() > 3) {
				rs.setRphoto(photo);
			}

			pes.add(rs);
		}
		cursor.close();
		dbs.close();
		return pes;
	}

	/**根据用户id和秤类型删除所有测量记录*/
	public void deleteByUseridAndScale(String userid, String scaleKind) {
		try {
			List<Records> list = getAllDatasByScaleAndGroup(scaleKind, userid, 100);
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Records r = list.get(i);
					deletePhoto(r.getRphoto());
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		dbs = dbHelper.getWritableDatabase();
		dbs.beginTransaction();
		dbs.execSQL("delete from userrecord where useid=? and scaleType=?", new Object[]{userid, scaleKind});
		dbs.setTransactionSuccessful();
		dbs.endTransaction();
		dbs.close();
	}

	/**根据用户id和秤类型删除所有测量记录*/
	public void deleteByUseridAndScaleForHarmBaby(String userid) {

		dbs = dbHelper.getWritableDatabase();
		dbs.beginTransaction();
		dbs.execSQL("delete from userrecord where useid=? ", new Object[]{userid});
		dbs.setTransactionSuccessful();
		dbs.endTransaction();
		dbs.close();
	}
	
	
	public List<Records> findRecordsByScaleTypeAndFoodNameAndKg(String scaleType,String foodName,float weight) throws Exception {
		dbs = dbHelper.getReadableDatabase();
		String sql = "select *,max(datetime(recordtime,'localtime')) md from userrecord group by useid having scaleType=? and photo=? and rweight=?  order by recordtime desc";
		Cursor cursor = dbs.rawQuery(sql, new String[]{scaleType,foodName,String.valueOf(weight)});
		List<Records> pes = new ArrayList<Records>();
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			int useid = cursor.getInt(cursor.getColumnIndex("useid"));
			String scaletype = cursor.getString(cursor.getColumnIndex("scaleType"));
			String ugroup = cursor.getString(cursor.getColumnIndex("ugroup"));
			String recordtime = cursor.getString(cursor.getColumnIndex("md"));
			String comparerecord = cursor.getString(cursor.getColumnIndex("comparerecord"));

			float rweight = cursor.getFloat(cursor.getColumnIndex("rweight"));
			float rbmi = cursor.getFloat(cursor.getColumnIndex("rbmi"));
			float rbone = cursor.getFloat(cursor.getColumnIndex("rbone"));
			float rbodyfat = cursor.getFloat(cursor.getColumnIndex("rbodyfat"));
			float rmuscle = cursor.getFloat(cursor.getColumnIndex("rmuscle"));
			float rbodywater = cursor.getFloat(cursor.getColumnIndex("rbodywater"));
			float rvisceralfat = cursor.getFloat(cursor.getColumnIndex("rvisceralfat"));
			float rbmr = cursor.getFloat(cursor.getColumnIndex("rbmr"));
			float bodyage = cursor.getFloat(cursor.getColumnIndex("bodyage"));
			Records rs = new Records(id, useid, scaletype, ugroup, recordtime, comparerecord, rweight, rbmi, rbone, rbodyfat, rmuscle, rbodywater, rvisceralfat, rbmr, bodyage);

			String photo = cursor.getString(cursor.getColumnIndex("photo"));
			if (photo != null && photo.length() > 3) {
				rs.setRphoto(photo);
			}

			pes.add(rs);
		}
		cursor.close();
		dbs.close();
		return pes;
	}

}
