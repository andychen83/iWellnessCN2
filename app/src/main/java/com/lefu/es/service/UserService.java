package com.lefu.es.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.lefu.es.db.DBOpenHelper;
import com.lefu.es.entity.UserModel;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 用户
 * @author Leon
 * 2015-11-19
 *
 */
public class UserService {
	private DBOpenHelper dbHelper;
	private SQLiteDatabase dbs;
	List<String>  gros = new ArrayList<String>(Arrays.asList("P1","P2","P3","P4","P5","P6","P7","P8","P9")); 
	
	public void closeDB(){
		if(null!=this.dbHelper){
			dbHelper.close();  
			dbHelper = null;
		}
	}
	
	public UserService(Context context) {
		this.dbHelper = new DBOpenHelper(context);
	}
	
	/**保存用户信息*/
	public int save(UserModel pe) throws Exception {
		int strid = 0;
		dbs = dbHelper.getWritableDatabase();
		dbs.execSQL("insert into user(username,ugroup,sex,level,bheigth,ageyear,agemonth,number,scaletype,uniqueid,birth,per_photo,targweight,danwei) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)", 
				new Object[]{pe.getUserName(),pe.getGroup(),pe.getSex(),pe.getLevel(),pe.getBheigth(),pe.getAgeYear(),pe.getAgeMonth(),pe.getNumber(),pe.getScaleType(),pe.getUniqueID(),pe.getBirth(),pe.getPer_photo(),pe.getTargweight(),pe.getDanwei()});
		Cursor cursor = dbs.rawQuery("select last_insert_rowid() from user", null);

		if (cursor.moveToFirst()) strid = cursor.getInt(0);
		Log.i("testAuto", strid + "");
		dbs.close();
		return strid;
	}
	
	/**获取最大分组数*/
	public int getMaxGroup(){
		dbs = dbHelper.getReadableDatabase();
		String sql="select max(number) from user";
		Cursor cursor = dbs.rawQuery(sql, new String[]{});
		int num = -1;
		try {
			if (null!=cursor && cursor.moveToFirst()) {
				 num = cursor.getInt(cursor.getColumnIndex("max(number)"));
			}
		} catch (Exception e) {
		}
		cursor.close();
		dbs.close(); 
		return num;
	}
	
	/**根据称类型获取分组*/
	public String getAddUserGroup(String scale){
		String up = "P0";
		try {
			List<String> glist = getAllUserGroupByScaleType(scale);
			for (String string : gros) {
				if(glist.contains(string)){
					continue;
				}else{
					up = string;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return up;
	}
	
	/**根据称类型获取用户组*/
	public List<String> getAllUserGroupByScaleType(String scale)throws Exception {
		dbs = dbHelper.getReadableDatabase();
		String sql="select * from user order where ugroup<>? by ugroup ";
		Cursor cursor = dbs.rawQuery(sql,new String[]{"P999"});
		List<String> pes = new ArrayList<String>();
		while (cursor.moveToNext()) {
			String group = cursor.getString(cursor.getColumnIndex("ugroup"));
			pes.add(group);
		}
		cursor.close();
		dbs.close(); 
		return pes;
	}
	
	/**查询所有用户*/
	public List<UserModel> getAllUserByScaleType()throws Exception {
		dbs = dbHelper.getReadableDatabase();
		String sql="select * from user where ugroup<>?";
		Cursor cursor = dbs.rawQuery(sql,new String[]{"P999"});
		List<UserModel> pes = new ArrayList<UserModel>();
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String name = cursor.getString(cursor.getColumnIndex("username"));
			String group = cursor.getString(cursor.getColumnIndex("ugroup"));
			String sex = cursor.getString(cursor.getColumnIndex("sex"));
			String level = cursor.getString(cursor.getColumnIndex("level"));
			float bheigth = cursor.getFloat(cursor.getColumnIndex("bheigth"));
			int ageyear = cursor.getInt(cursor.getColumnIndex("ageyear"));
			int agemonth = cursor.getInt(cursor.getColumnIndex("agemonth"));
			int number = cursor.getInt(cursor.getColumnIndex("number"));
			String uId = cursor.getString(cursor.getColumnIndex("uniqueid"));
			String birth = cursor.getString(cursor.getColumnIndex("birth"));
			String photo = cursor.getString(cursor.getColumnIndex("per_photo"));
			float targweight = cursor.getFloat(cursor.getColumnIndex("targweight"));
			String  danwei = cursor.getString(cursor.getColumnIndex("danwei"));
			String  scaletype = cursor.getString(cursor.getColumnIndex("scaletype"));
			pes.add(new UserModel(id,name,group,sex,level,bheigth,ageyear,agemonth,number,scaletype,uId,birth,photo,targweight,danwei));
		}
		cursor.close();
		dbs.close(); 
		return pes;
	}

	/**
	 * 查询所有婴儿数据
	 * @return  用户组P999为 抱婴
	 * @throws Exception
     */
	public List<UserModel> getAllBabys()throws Exception {
		dbs = dbHelper.getReadableDatabase();
		String sql="select * from user where ugroup=?";
		Cursor cursor = dbs.rawQuery(sql,new String[]{"P999"});
		List<UserModel> pes = new ArrayList<UserModel>();
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String name = cursor.getString(cursor.getColumnIndex("username"));
			String group = cursor.getString(cursor.getColumnIndex("ugroup"));
			String sex = cursor.getString(cursor.getColumnIndex("sex"));
			String level = cursor.getString(cursor.getColumnIndex("level"));
			float bheigth = cursor.getFloat(cursor.getColumnIndex("bheigth"));
			int ageyear = cursor.getInt(cursor.getColumnIndex("ageyear"));
			int agemonth = cursor.getInt(cursor.getColumnIndex("agemonth"));
			int number = cursor.getInt(cursor.getColumnIndex("number"));
			String uId = cursor.getString(cursor.getColumnIndex("uniqueid"));
			String birth = cursor.getString(cursor.getColumnIndex("birth"));
			String photo = cursor.getString(cursor.getColumnIndex("per_photo"));
			float targweight = cursor.getFloat(cursor.getColumnIndex("targweight"));
			String  danwei = cursor.getString(cursor.getColumnIndex("danwei"));
			String  scaletype = cursor.getString(cursor.getColumnIndex("scaletype"));
			pes.add(new UserModel(id,name,group,sex,level,bheigth,ageyear,agemonth,number,scaletype,uId,birth,photo,targweight,danwei));
		}
		cursor.close();
		dbs.close();
		return pes;
	}
	
	/**删除用户*/
	public void delete(int id) throws Exception {
		dbs = dbHelper.getWritableDatabase();
		dbs.execSQL("delete from user where id=?", new Object[]{id});
		dbs.close(); 
	}

	/**更新用户信息*/
	public void update(UserModel pe) throws Exception {
		dbs = dbHelper.getWritableDatabase();
		dbs.beginTransaction();
		dbs.execSQL("update  user set username=?,ugroup=?,sex=?,level=?,bheigth=?,ageyear=?,agemonth=?,number=?,scaletype=?,uniqueid=?,birth=?,per_photo=?,targweight=?,danwei=? where id=? ", 
				new Object[]{pe.getUserName(),pe.getGroup(),pe.getSex(),pe.getLevel(),pe.getBheigth(),pe.getAgeYear(),pe.getAgeMonth(),
				pe.getNumber(),pe.getScaleType(),pe.getUniqueID(),pe.getBirth(),pe.getPer_photo(),pe.getTargweight(),pe.getDanwei(),pe.getId()});
		dbs.setTransactionSuccessful();
		dbs.endTransaction();
	}
	
	/**更新用户信息2*/
	public void update2(UserModel pe) throws Exception {
		dbs = dbHelper.getWritableDatabase();
		dbs.beginTransaction();
		dbs.execSQL("update  user set sex=?,level=?,bheigth=?,ageyear=? ,uniqueid=? ,targweight=?,danwei=? where id=? ", 
				new Object[]{pe.getSex(),pe.getLevel(),pe.getBheigth(),pe.getAgeYear(),pe.getUniqueID(),pe.getTargweight(),pe.getDanwei(),pe.getId()});
		dbs.setTransactionSuccessful();
		dbs.endTransaction();
	}

	/**根据id查找用户*/
	public UserModel find(int id) throws Exception {
		dbs = dbHelper.getReadableDatabase();
		Cursor cursor = dbs.rawQuery("select * from user where id=?", new String[]{String.valueOf(id)});
		UserModel pes = null;
		if(null!=cursor && cursor.moveToFirst()) {
			String name = cursor.getString(cursor.getColumnIndex("username"));
			String group = cursor.getString(cursor.getColumnIndex("ugroup"));
			String sex = cursor.getString(cursor.getColumnIndex("sex"));
			String level = cursor.getString(cursor.getColumnIndex("level"));
			float bheigth = cursor.getFloat(cursor.getColumnIndex("bheigth"));
			int ageyear = cursor.getInt(cursor.getColumnIndex("ageyear"));
			int agemonth = cursor.getInt(cursor.getColumnIndex("agemonth"));
			int number = cursor.getInt(cursor.getColumnIndex("number"));
			String scaletype = cursor.getString(cursor.getColumnIndex("scaletype"));
			String uId = cursor.getString(cursor.getColumnIndex("uniqueid"));
			String birth = cursor.getString(cursor.getColumnIndex("birth"));
			String photo = cursor.getString(cursor.getColumnIndex("per_photo"));
			float targweight = cursor.getFloat(cursor.getColumnIndex("targweight"));
			String  danwei = cursor.getString(cursor.getColumnIndex("danwei"));
			pes = new UserModel(id,name,group,sex,level,bheigth,ageyear,agemonth,number,scaletype,uId,birth,photo,targweight,danwei);
		}
		cursor.close();
		dbs.close(); 
		return pes;
	}
	
	/**根据称的类型和组号查询用户 */
	public UserModel findUserByGupandScale(String ugroup,String scale) throws Exception {
		dbs = dbHelper.getReadableDatabase();
		Cursor cursor = dbs.rawQuery("select * from user where ugroup=? and scaletype=? and ugroup<>?", new String[]{ugroup,scale,"P999"});
		UserModel pes = null;
		if(cursor.moveToFirst()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String name = cursor.getString(cursor.getColumnIndex("username"));
			String group = cursor.getString(cursor.getColumnIndex("ugroup"));
			String sex = cursor.getString(cursor.getColumnIndex("sex"));
			String level = cursor.getString(cursor.getColumnIndex("level"));
			float bheigth = cursor.getFloat(cursor.getColumnIndex("bheigth"));
			int ageyear = cursor.getInt(cursor.getColumnIndex("ageyear"));
			int agemonth = cursor.getInt(cursor.getColumnIndex("agemonth"));
			int number = cursor.getInt(cursor.getColumnIndex("number"));
			String scaletype = cursor.getString(cursor.getColumnIndex("scaletype"));
			String uId = cursor.getString(cursor.getColumnIndex("uniqueid"));
			String birth = cursor.getString(cursor.getColumnIndex("birth"));
			String photo = cursor.getString(cursor.getColumnIndex("per_photo"));
			float targweight = cursor.getFloat(cursor.getColumnIndex("targweight"));
			String  danwei = cursor.getString(cursor.getColumnIndex("danwei"));
			pes = new UserModel(id,name,group,sex,level,bheigth,ageyear,agemonth,number,scaletype,uId,birth,photo,targweight,danwei);
		}
		cursor.close();
		dbs.close(); 
		return pes;
	}
	
	/**获取用户数量*/
	public Long getCount()throws Exception{
		dbs = dbHelper.getReadableDatabase();
		Cursor cursor = dbs.rawQuery("select count(*) from user where ugroup<>?", new String[]{"P999"});
		cursor.moveToFirst();
		Long result = cursor.getLong(0);
		cursor.close();
		return result;
	}
	
	/**获取最大的用户id*/
	public int maxid()throws Exception{
		dbs = dbHelper.getReadableDatabase();
		Cursor cursor = dbs.rawQuery("select max(id) from user ", null);
		cursor.moveToFirst();
		int result = cursor.getInt(0);
		cursor.close();
		return result;
	}
	
	/**分页查找用户数据*/
	public List<UserModel> getScrollData(int offset, int maxResult)throws Exception {
		dbs = dbHelper.getReadableDatabase();
		String sql="select * from user where ugroup<>? limit ?,? ";
		Cursor cursor = dbs.rawQuery(sql, new String[]{String.valueOf(offset),String.valueOf(maxResult),"P999"});
		List<UserModel> pes = new ArrayList<UserModel>();
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String name = cursor.getString(cursor.getColumnIndex("username"));
			String group = cursor.getString(cursor.getColumnIndex("ugroup"));
			String sex = cursor.getString(cursor.getColumnIndex("sex"));
			String level = cursor.getString(cursor.getColumnIndex("level"));
			float bheigth = cursor.getFloat(cursor.getColumnIndex("bheigth"));
			int ageyear = cursor.getInt(cursor.getColumnIndex("ageyear"));
			int agemonth = cursor.getInt(cursor.getColumnIndex("agemonth"));
			int number = cursor.getInt(cursor.getColumnIndex("number"));
			String scaletype = cursor.getString(cursor.getColumnIndex("scaletype"));
			String birth = cursor.getString(cursor.getColumnIndex("birth"));
			String photo = cursor.getString(cursor.getColumnIndex("per_photo"));
			float targweight = cursor.getFloat(cursor.getColumnIndex("targweight"));
			String  danwei = cursor.getString(cursor.getColumnIndex("danwei"));
			pes.add(new UserModel(id,name,group,sex,level,bheigth,ageyear,agemonth,number,scaletype,"",birth,photo,targweight,danwei));
		}
		cursor.close();
		dbs.close(); 
		return pes;
	}
	
	/**获取所有用户数据*/
	public List<UserModel> getAllDatas()throws Exception {
		dbs = dbHelper.getReadableDatabase();
		String sql="select * from user where ugroup<>? ORDER BY id DESC limit 0,1 ";
		Cursor cursor = dbs.rawQuery(sql, new String[]{"P999"});
		List<UserModel> pes = new ArrayList<UserModel>();
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String name = cursor.getString(cursor.getColumnIndex("username"));
			String group = cursor.getString(cursor.getColumnIndex("ugroup"));
			String sex = cursor.getString(cursor.getColumnIndex("sex"));
			String level = cursor.getString(cursor.getColumnIndex("level"));
			float bheigth = cursor.getFloat(cursor.getColumnIndex("bheigth"));
			int ageyear = cursor.getInt(cursor.getColumnIndex("ageyear"));
			int agemonth = cursor.getInt(cursor.getColumnIndex("agemonth"));
			int number = cursor.getInt(cursor.getColumnIndex("number"));
			String scaletype = cursor.getString(cursor.getColumnIndex("scaletype"));
			String uId = cursor.getString(cursor.getColumnIndex("uniqueid"));
			String birth = cursor.getString(cursor.getColumnIndex("birth"));
			String photo = cursor.getString(cursor.getColumnIndex("per_photo"));
			float targweight = cursor.getFloat(cursor.getColumnIndex("targweight"));
			String  danwei = cursor.getString(cursor.getColumnIndex("danwei"));
			pes.add(new UserModel(id,name,group,sex,level,bheigth,ageyear,agemonth,number,scaletype,uId,birth,photo,targweight,danwei));
		}
		cursor.close();
		dbs.close(); 
		return pes;
	}
}
