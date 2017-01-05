package com.lefu.es.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.lefu.es.entity.BlueDevice;
import com.lefu.es.entity.NutrientBo;
import com.lefu.es.entity.Records;
import com.lefu.es.entity.UserModel;
import com.lefu.es.service.RecordService;
import com.lefu.es.service.UserService;
/**
 * 缓存
 *
 * @author Leon 2015-11-17
 */
public class CacheHelper {
	private static final String TAG = "CacheHelper";
	public static List<UserModel> userList = new ArrayList<UserModel>();
	public static List<Records> recordList = new ArrayList<Records>();
	public static List<Records> recordListDesc = new ArrayList<Records>();
	public static List<Records> recordLastList = new ArrayList<Records>();

	public static List<BlueDevice> devicesList = new ArrayList<BlueDevice>();

	public static List<NutrientBo> nutrientList = new ArrayList<NutrientBo>();
	public static List<String> nutrientNameList = new ArrayList<String>();

	public static List<String> nutrientTempNameList = new ArrayList<String>();
	public static List<NutrientBo> nutrientTempList = new ArrayList<NutrientBo>();

	private static UserService userService;
	private static RecordService recordService;

	public static void cacheAllNutrientsDate(Context context, SQLiteDatabase db) {
		if (null != db) {
			queryAllTempNutrients(context, db);
			queryAllNutrients(context, db);

		}
	}

	/**
	 * 根据名称精准查找
	 * @param name
	 * @return
	 */
	public static NutrientBo queryNutrientsByName(String name){
		NutrientBo nutrient  = null;
		if(!TextUtils.isEmpty(name) && null!=CacheHelper.nutrientList && CacheHelper.nutrientList.size()>0){
			for (NutrientBo nutrientBo : CacheHelper.nutrientList) {
				if(!TextUtils.isEmpty(nutrientBo.getNutrientDesc()) && nutrientBo.getNutrientDesc().equals(name)){
					nutrient = nutrientBo;
					break;
				}
			}
		}
		return nutrient;
	}



	/**
	 * 模糊搜索
	 * @param name
	 * @return
	 */
	public static List<NutrientBo> queryAllNutrientsByName(String name){
		List<NutrientBo> nutrientList  = null;
		if(null!=CacheHelper.nutrientList && CacheHelper.nutrientList.size()>0){
			nutrientList = new ArrayList<NutrientBo>();
			for (NutrientBo nutrientBo : CacheHelper.nutrientList) {
				if(!TextUtils.isEmpty(nutrientBo.getNutrientDesc()) && nutrientBo.getNutrientDesc().contains(name)){
					nutrientList.add(nutrientBo);
				}
			}
		}
		return nutrientList;
	}


	/**
	 * 分页
	 * @param pager
	 * @return
	 */
	public static List<NutrientBo> queryAllNutrientsByPager(int pager){
		if(null!=CacheHelper.nutrientList && CacheHelper.nutrientList.size()>0){
			pager = pager -1;
			if(pager<0){
				pager = 0;
			}
			int start = pager * 30;
			int end = start + 30;
			if(end>=CacheHelper.nutrientList.size()-1){
				end = CacheHelper.nutrientList.size();
			}
			return CacheHelper.nutrientList.subList(start, end);
		}
		return null;
	}

	private static void queryAllTempNutrients(Context context, SQLiteDatabase db) {
		try {
			if (null != db) {
				String sql = "select * from base_na limit 0,100";
				Cursor cursor = db.rawQuery(sql, null);
				NutrientBo pdomain = null;
				while (cursor.moveToNext()) {
					pdomain = new NutrientBo();
					//pdomain.setId(cursor.getInt(cursor.getColumnIndex("_id")));
					pdomain.setNutrientNo(cursor.getString(cursor.getColumnIndex("nutrientNo")));
					pdomain.setNutrientDesc(cursor.getString(cursor.getColumnIndex("nutrientDesc")));
					pdomain.setNutrientWater(cursor.getString(cursor.getColumnIndex("nutrientWater")));
					pdomain.setNutrientEnerg(cursor.getString(cursor.getColumnIndex("nutrientEnerg")));
					pdomain.setNutrientProtein(cursor.getString(cursor.getColumnIndex("nutrientProtein")));
					pdomain.setNutrientLipidTot(cursor.getString(cursor.getColumnIndex("nutrientLipidTot")));
					pdomain.setNutrientAsh(cursor.getString(cursor.getColumnIndex("nutrientAsh")));
					pdomain.setNutrientCarbohydrt(cursor.getString(cursor.getColumnIndex("nutrientCarbohydrt")));
					pdomain.setNutrientFiberTD(cursor.getString(cursor.getColumnIndex("nutrientFiberTD")));
					pdomain.setNutrientSugarTot(cursor.getString(cursor.getColumnIndex("nutrientSugarTot")));
					pdomain.setNutrientCalcium(cursor.getString(cursor.getColumnIndex("nutrientCalcium")));
					pdomain.setNutrientIron(cursor.getString(cursor.getColumnIndex("nutrientIron")));
					pdomain.setNutrientMagnesium(cursor.getString(cursor.getColumnIndex("nutrientMagnesium")));
					pdomain.setNutrientPhosphorus(cursor.getString(cursor.getColumnIndex("nutrientPhosphorus")));
					pdomain.setNutrientPotassium(cursor.getString(cursor.getColumnIndex("nutrientPotassium")));
					pdomain.setNutrientSodium(cursor.getString(cursor.getColumnIndex("nutrientSodium")));
					pdomain.setNutrientZinc(cursor.getString(cursor.getColumnIndex("nutrientZinc")));
					pdomain.setNutrientCopper(cursor.getString(cursor.getColumnIndex("nutrientCopper")));
					pdomain.setNutrientManganese(cursor.getString(cursor.getColumnIndex("nutrientManganese")));
					pdomain.setNutrientSelenium(cursor.getString(cursor.getColumnIndex("nutrientSelenium")));
					pdomain.setNutrientVitc(cursor.getString(cursor.getColumnIndex("nutrientVitc")));
					pdomain.setNutrientThiamin(cursor.getString(cursor.getColumnIndex("nutrientThiamin")));
					pdomain.setNutrientRiboflavin(cursor.getString(cursor.getColumnIndex("nutrientRiboflavin")));
					pdomain.setNutrientNiacin(cursor.getString(cursor.getColumnIndex("nutrientNiacin")));
					pdomain.setNutrientPantoAcid(cursor.getString(cursor.getColumnIndex("nutrientPantoAcid")));
					pdomain.setNutrientVitB6(cursor.getString(cursor.getColumnIndex("nutrientVitB6")));
					pdomain.setNutrientFolateTot(cursor.getString(cursor.getColumnIndex("nutrientFolateTot")));
					pdomain.setNutrientFolicAcid(cursor.getString(cursor.getColumnIndex("nutrientFolicAcid")));
					pdomain.setNutrientFoodFolate(cursor.getString(cursor.getColumnIndex("nutrientFoodFolate")));
					pdomain.setNutrientFolateDfe(cursor.getString(cursor.getColumnIndex("nutrientFolateDfe")));
					pdomain.setNutrientCholineTot(cursor.getString(cursor.getColumnIndex("nutrientCholineTot")));
					pdomain.setNutrientVitB12(cursor.getString(cursor.getColumnIndex("nutrientVitB12")));
					pdomain.setNutrientVitAiu(cursor.getString(cursor.getColumnIndex("nutrientVitAiu")));
					pdomain.setNutrientVitArae(cursor.getString(cursor.getColumnIndex("nutrientVitArae")));
					pdomain.setNutrientRetinol(cursor.getString(cursor.getColumnIndex("nutrientRetinol")));

					pdomain.setNutrientAlphaCarot("0.0");
					pdomain.setNutrientBetaCarot("0.0");
					pdomain.setNutrientBetaCrypt("0.0");
					pdomain.setNutrientLycopene("0.0");
					pdomain.setNutrientLutZea("0.0");
					pdomain.setNutrientVite("0.0");
					pdomain.setNutrientVitd("0.0");
					pdomain.setNutrientVitDiu("0.0");
					pdomain.setNutrientVitK("0.0");
					pdomain.setNutrientFaSat("0.0");
					pdomain.setNutrientFaMono("0.0");
					pdomain.setNutrientFaPoly("0.0");
					pdomain.setNutrientCholestrl("0.0");
					pdomain.setNutrientGmWt1("0.0");
					pdomain.setNutrientGmWtDesc1("0.0");
					pdomain.setNutrientGmWt2("0.0");
					pdomain.setNutrientGmWtDesc2("0.0");
					pdomain.setNutrientRefusePct("0.0");

					if(!TextUtils.isEmpty(pdomain.getNutrientDesc())){
						CacheHelper.nutrientTempNameList.add(pdomain.getNutrientDesc());
						CacheHelper.nutrientTempList.add(pdomain);
					}
				}
				cursor.close();
				Log.e("queryAllTempNutrients*****", "获取到营养临时缓存数据共: "+CacheHelper.nutrientTempList.size());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void queryAllNutrients(Context context, SQLiteDatabase db) {
		try {
			if (null != db) {
				String sql = "select * from base_na";
				Cursor cursor = db.rawQuery(sql, null);
				NutrientBo pdomain = null;
				while (cursor.moveToNext()) {
					pdomain = new NutrientBo();
					//pdomain.setId(cursor.getInt(cursor.getColumnIndex("_id")));
					pdomain.setNutrientNo(cursor.getString(cursor.getColumnIndex("nutrientNo")));
					pdomain.setNutrientDesc(cursor.getString(cursor.getColumnIndex("nutrientDesc")));
					pdomain.setNutrientWater(cursor.getString(cursor.getColumnIndex("nutrientWater")));
					pdomain.setNutrientEnerg(cursor.getString(cursor.getColumnIndex("nutrientEnerg")));
					pdomain.setNutrientProtein(cursor.getString(cursor.getColumnIndex("nutrientProtein")));
					pdomain.setNutrientLipidTot(cursor.getString(cursor.getColumnIndex("nutrientLipidTot")));
					pdomain.setNutrientAsh(cursor.getString(cursor.getColumnIndex("nutrientAsh")));
					pdomain.setNutrientCarbohydrt(cursor.getString(cursor.getColumnIndex("nutrientCarbohydrt")));
					pdomain.setNutrientFiberTD(cursor.getString(cursor.getColumnIndex("nutrientFiberTD")));
					pdomain.setNutrientSugarTot(cursor.getString(cursor.getColumnIndex("nutrientSugarTot")));
					pdomain.setNutrientCalcium(cursor.getString(cursor.getColumnIndex("nutrientCalcium")));
					pdomain.setNutrientIron(cursor.getString(cursor.getColumnIndex("nutrientIron")));
					pdomain.setNutrientMagnesium(cursor.getString(cursor.getColumnIndex("nutrientMagnesium")));
					pdomain.setNutrientPhosphorus(cursor.getString(cursor.getColumnIndex("nutrientPhosphorus")));
					pdomain.setNutrientPotassium(cursor.getString(cursor.getColumnIndex("nutrientPotassium")));
					pdomain.setNutrientSodium(cursor.getString(cursor.getColumnIndex("nutrientSodium")));
					pdomain.setNutrientZinc(cursor.getString(cursor.getColumnIndex("nutrientZinc")));
					pdomain.setNutrientCopper(cursor.getString(cursor.getColumnIndex("nutrientCopper")));
					pdomain.setNutrientManganese(cursor.getString(cursor.getColumnIndex("nutrientManganese")));
					pdomain.setNutrientSelenium(cursor.getString(cursor.getColumnIndex("nutrientSelenium")));
					pdomain.setNutrientVitc(cursor.getString(cursor.getColumnIndex("nutrientVitc")));
					pdomain.setNutrientThiamin(cursor.getString(cursor.getColumnIndex("nutrientThiamin")));
					pdomain.setNutrientRiboflavin(cursor.getString(cursor.getColumnIndex("nutrientRiboflavin")));
					pdomain.setNutrientNiacin(cursor.getString(cursor.getColumnIndex("nutrientNiacin")));
					pdomain.setNutrientPantoAcid(cursor.getString(cursor.getColumnIndex("nutrientPantoAcid")));
					pdomain.setNutrientVitB6(cursor.getString(cursor.getColumnIndex("nutrientVitB6")));
					pdomain.setNutrientFolateTot(cursor.getString(cursor.getColumnIndex("nutrientFolateTot")));
					pdomain.setNutrientFolicAcid(cursor.getString(cursor.getColumnIndex("nutrientFolicAcid")));
					pdomain.setNutrientFoodFolate(cursor.getString(cursor.getColumnIndex("nutrientFoodFolate")));
					pdomain.setNutrientFolateDfe(cursor.getString(cursor.getColumnIndex("nutrientFolateDfe")));
					pdomain.setNutrientCholineTot(cursor.getString(cursor.getColumnIndex("nutrientCholineTot")));
					pdomain.setNutrientVitB12(cursor.getString(cursor.getColumnIndex("nutrientVitB12")));
					pdomain.setNutrientVitAiu(cursor.getString(cursor.getColumnIndex("nutrientVitAiu")));
					pdomain.setNutrientVitArae(cursor.getString(cursor.getColumnIndex("nutrientVitArae")));
					pdomain.setNutrientRetinol(cursor.getString(cursor.getColumnIndex("nutrientRetinol")));

					pdomain.setNutrientAlphaCarot("0.0");
					pdomain.setNutrientBetaCarot("0.0");
					pdomain.setNutrientBetaCrypt("0.0");
					pdomain.setNutrientLycopene("0.0");
					pdomain.setNutrientLutZea("0.0");
					pdomain.setNutrientVite("0.0");
					pdomain.setNutrientVitd("0.0");
					pdomain.setNutrientVitDiu("0.0");
					pdomain.setNutrientVitK("0.0");
					pdomain.setNutrientFaSat("0.0");
					pdomain.setNutrientFaMono("0.0");
					pdomain.setNutrientFaPoly("0.0");
					pdomain.setNutrientCholestrl("0.0");
					pdomain.setNutrientGmWt1("0.0");
					pdomain.setNutrientGmWtDesc1("0.0");
					pdomain.setNutrientGmWt2("0.0");
					pdomain.setNutrientGmWtDesc2("0.0");
					pdomain.setNutrientRefusePct("0.0");

					if(!TextUtils.isEmpty(pdomain.getNutrientDesc())){
						CacheHelper.nutrientNameList.add(pdomain.getNutrientDesc());
						CacheHelper.nutrientList.add(pdomain);
					}
				}
				cursor.close();
				Log.e("queryAllNutrients*****", "获取到营养缓存数据共: "+CacheHelper.nutrientList.size());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 根据名称查找营养成分集合对象
	 * @param name
	 * @return
	 */
	public static ArrayList<NutrientBo> findNutrientByName(String name){
		if(TextUtils.isEmpty(name) || null==CacheHelper.nutrientList || CacheHelper.nutrientList.size()==0){
			return null;
		}
		ArrayList<NutrientBo> nutrientList = new ArrayList<NutrientBo>();
		for (NutrientBo bo : CacheHelper.nutrientList) {
			if(!TextUtils.isEmpty(bo.getNutrientDesc()) && bo.getNutrientDesc().contains(name)){
				nutrientList.add(bo);
			}
		}
		return nutrientList;
	}

	public static Records getRecordLast(int id) {
		Records record = null;
		if (null != recordListDesc && recordListDesc.size() > 0) {
			for (Records records : recordListDesc) {
				if (records.getId() == id) {
					record = records;
					break;
				}
			}
		}
		return record;
	}

	public static void main(String[] arges) {
		List<String> recordList = new ArrayList<String>();
		recordList.add("P6");
		recordList.add("P1");
		recordList.add("P3");
		recordList.add("P5");
		recordList.add("P4");
		recordList.add("P2");

		Collections.sort(recordList);
		for (String object : recordList) {
			System.out.println(object);
		}
	}

	/**
	 * 初始化缓存
	 */
	public static void intiCaches() {
		if (null == userList) {
			userList = new ArrayList<UserModel>();
		} else {
			userList.clear();
		}
		if (null == recordList) {
			recordList = new ArrayList<Records>();
		} else {
			recordList.clear();
		}
		if (null == recordLastList) {
			recordLastList = new ArrayList<Records>();
		} else {
			recordLastList.clear();
		}
		if (null == devicesList) {
			devicesList = new ArrayList<BlueDevice>();
		} else {
			devicesList.clear();
		}
		try {
			userList = userService.getAllDatas();
			recordList = recordService.getAllDatas();
			recordLastList = recordService.findLastRecords();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 根据用id查找
	 *
	 * @param id
	 * @return
	 */
	public static UserModel getUserById(int id) {
		if (null != userList && userList.size() > 0 && id != 0) {
			for (UserModel user : userList) {
				if (user.getId() == id) {
					return user;
				}
			}
		}
		return null;
	}

	/**
	 * 按照用户称类型和组查询
	 *
	 * @param group
	 * @param scaleType
	 * @return
	 */
	public static UserModel getUserByGroandScaleType(String group, String scaleType) {
		if (null != userList && userList.size() > 0 && null != group && null != scaleType) {
			for (UserModel user : userList) {
				if (group.equals(user.getGroup()) && scaleType.equals(user.getScaleType())) {
					return user;
				}
			}
		}
		return null;
	}

	/**
	 * 更新用户信息
	 *
	 * @param ue
	 * @return
	 */
	public static boolean updateUserById(UserModel ue) {
		if (null != userList && userList.size() > 0 && null != ue) {
			for (UserModel user : userList) {
				if (user.getId() == ue.getId()) {
					userList.remove(user);
					userList.add(ue);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 根据称类型获取最后测量记录
	 *
	 * @param scaleType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Records> getRecordLastListByScaleType(String scaleType) {
		List<Records> recordList = null;
		if (null != recordLastList && recordLastList.size() > 0) {
			recordList = new ArrayList<Records>();
			for (Records records : recordList) {
				if (records.getScaleType().equals(scaleType)) {
					recordList.add(records);
				}
			}
			Collections.sort(recordList, new CacheHelper.SortByName());
		}
		return recordList;
	}

	@SuppressWarnings("rawtypes")
	static class SortByName implements Comparator {
		public int compare(Object o1, Object o2) {
			Records s1 = (Records) o1;
			Records s2 = (Records) o2;
			return s1.getUgroup().compareTo(s2.getUgroup());
		}
	}

}
