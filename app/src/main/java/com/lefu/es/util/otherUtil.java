package com.lefu.es.util;
/**
 * 其他工具类
 * @author Leon
 * 2015-11-19
 */
public class otherUtil {
	public static String[] getYera() {
		String str[] = new String[107];

		int index1 = 1942;
		for (int i = 0; i < str.length; i++) {
			str[i] = cyclicalm(index1 - 1900 + 36) + "年(" + index1 + ")";
			index1++;
		}
		return str;
	}

	public static String cyclicalm(int num) {
		final String[] Gan = new String[]{"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
		final String[] Zhi = new String[]{"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
		return (Gan[num % 10] + Zhi[num % 12]);
	}

	public static boolean isGregorianLeapYear(int year) {
		boolean isLeap = false;
		if (year % 4 == 0)
			isLeap = true;
		if (year % 100 == 0)
			isLeap = false;
		if (year % 400 == 0)
			isLeap = true;
		return isLeap;
	}

}
