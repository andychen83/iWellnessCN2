package com.lefu.es.util;

import android.annotation.SuppressLint;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 * @author Leon
 * 2015-11-19
 */
@SuppressLint({"SimpleDateFormat", "DefaultLocale"})
public class StringUtils {
	
	/**判断是否是数字*/
	public static boolean isNumber(String number) {
		if (null == number || "".equals(number)) {
			return false;
		}
		int index = number.indexOf(".");
		if (index < 0) {
			return StringUtils.isNumeric(number);
		} else {
			String num1 = number.substring(0, index);
			String num2 = number.substring(index + 1);

			return StringUtils.isNumeric(num1) && StringUtils.isNumeric(num2);
		}
	}

	/**异或校验*/
	public static int getSubChecknum(byte[] srcbyte) {
		int num = (srcbyte[0] & 0xff);
		for (int i = 1; i <= srcbyte.length - 1; i++) {
			num = num ^ srcbyte[i];
		}
		return num;
	}
	
	
	public static String getBCC(byte[] data) {
		String ret = "";
		byte BCC[] = new byte[1];
		for (int i = 0; i < data.length; i++) {
			BCC[0] = (byte) (BCC[0] ^ data[i]);
		}
		String hex = Integer.toHexString(BCC[0] & 0xFF);
		if (hex.length() == 1) {
			hex = '0' + hex;
		}
		ret += hex.toUpperCase();
		return ret;
	}

	/**计算校验和*/
	public static String calcCheckSum(String msg) {
		byte[] arr = msg.getBytes();
		byte[] res = new byte[4];

		for (int i = 0; i < arr.length; i += 4) {
			res[0] ^= arr[i];
			res[1] ^= arr[i + 1];
			res[2] ^= arr[i + 2];
			res[3] ^= arr[i + 3];
		}

		res[0] = (byte) ~res[0];
		res[1] = (byte) ~res[1];
		res[2] = (byte) ~res[2];
		res[3] = (byte) ~res[3];

		String resStr = "";

		for (int i = 0; i < 4; i++) {
			resStr = resStr + byte2hex(res[i]);
		}

		return resStr;
	}

	/**将单字节转成16进制*/
	public static String byte2hex(byte b) {
		StringBuffer buf = new StringBuffer();
		char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
		int high = ((b & 0xf0) >> 4);
		int low = (b & 0x0f);
		buf.append(hexChars[high]);
		buf.append(hexChars[low]);

		return buf.toString();
	}

	/**16进制字符串转字节数组*/
	public static byte[] hexStringToByteArray(String digits) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			for (int i = 0; i < digits.length(); i += 2) {
				char c1 = digits.charAt(i);
				if ((i + 1) >= digits.length()) {
					throw new IllegalArgumentException("hexUtil.odd");
				}
				char c2 = digits.charAt(i + 1);
				byte b = 0;
				if ((c1 >= '0') && (c1 <= '9'))
					b += ((c1 - '0') * 16);
				else if ((c1 >= 'a') && (c1 <= 'f'))
					b += ((c1 - 'a' + 10) * 16);
				else if ((c1 >= 'A') && (c1 <= 'F'))
					b += ((c1 - 'A' + 10) * 16);
				else
					throw new IllegalArgumentException("hexUtil.bad");

				if ((c2 >= '0') && (c2 <= '9'))
					b += (c2 - '0');
				else if ((c2 >= 'a') && (c2 <= 'f'))
					b += (c2 - 'a' + 10);
				else if ((c2 >= 'A') && (c2 <= 'F'))
					b += (c2 - 'A' + 10);
				else
					throw new IllegalArgumentException("hexUtil.bad");
				baos.write(b);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return (baos.toByteArray());
	}

	/**字节数组转16进制字符串*/
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder();
		if (src == null || src.length <= 0) {
			return null;
		}
		char[] buffer = new char[2];
		for (int i = 0; i < src.length; i++) {
			buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
			buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
			stringBuilder.append(buffer);
		}
		return stringBuilder.toString().toUpperCase();
	}


	/**判断是否是数字*/
	public static boolean isNumeric(String str) {
		if (null == str || "".equals(str)) {
			return false;
		}
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	/**字节数组转字符串*/
	public static String bytes2HexString(byte[] b) {
		String ret = "";
		for (int i = 0; i < b.length; i++) {

			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex;
		}

		return ret;
	}

	/**16进制转10进制*/
	public static int hexToTen(String hex) {
		if (null == hex || (null != hex && "".equals(hex))) {
			return 0;
		}
		return Integer.valueOf(hex, 16);
	}

	/**16进制转2进制*/
	public static String hexToBirary(String hex) {
		if (null == hex || (null != hex && "".equals(hex))) {
			return null;
		}
		return Integer.toBinaryString(Integer.valueOf(hex, 16));
	}

	/**10进制转2进制*/
	public static String tenToBinary(int i) {
		return Integer.toBinaryString(i);
	}

	/**2进制转10进制*/
	public static int binaryToTen(String bianry) {
		if (null == bianry || (null != bianry && "".equals(bianry))) {
			return 0;
		}
		return Integer.valueOf(bianry, 2);
	}

	@SuppressWarnings("deprecation")
	public static String getDateShareString(String dates, int type) {
		StringBuffer sb = new StringBuffer();
		try {
			if (null != dates && !"".equals(dates)) {

				SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = df2.parse(dates);
				int week = date.getDay();
				int month = date.getMonth() + 1;

				sb.append(date.getHours() < 10 ? "0" + date.getHours() : date.getHours());
				sb.append(":" + (date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes()));
				sb.append(":" + (date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds()));
				sb.append(", " + weekArray[week]);
				sb.append("," + (date.getMonth() + 1));
				sb.append("/ " + date.getDate());
				sb.append("/" + (date.getYear() + 1900));

			}
		} catch (Exception e) {
		}
		return sb.toString();
	}
	public static final String[] weekArrayzh = new String[]{"周日","周一","周二","周三","周四","周五","周六"};
	public static final String[] monthArrayzh = new String[]{"1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月"};
	@SuppressWarnings("deprecation")
	public static String getDateStringZh(Date date, int type) {
		StringBuffer sb = new StringBuffer();

		int week = date.getDay();
		int month = date.getMonth() + 1;

		switch (type) {
			case 1 :
				sb.append(date.getHours() < 10 ? "0" + date.getHours() : date.getHours());
				sb.append(":" + (date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes()));
				sb.append(", " + weekArrayzh[week]);
				sb.append(", " + getDateStr(date.getDate()) + " " + monthArrayzh[month - 1]);
				sb.append(", " + (date.getYear() + 1900));
				break;

			case 2 :
				sb.append(weekArrayzh[week]);
				sb.append(", " + getDateStr(date.getDate()) + " " + monthArrayzh[month - 1]);
				sb.append(", " + (date.getYear() + 1900));
				break;
			case 3 :
				sb.append(monthArrayzh[month - 1]);
				sb.append(", " + (date.getYear() + 1900));
				break;
			case 4 :
				sb.append((date.getYear() + 1900));
				break;
			case 5 :
				sb.append(date.getHours() < 10 ? "0" + date.getHours() : date.getHours());
				sb.append(":" + (date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes()));
				sb.append(", " + weekArrayzh[week]);
				sb.append("," + (date.getMonth() + 1));
				sb.append("/ " + date.getDate());
				sb.append("/" + (date.getYear() + 1900));
				break;
			case 6 :
				sb.append(date.getHours() < 10 ? "0" + date.getHours() : date.getHours());
				sb.append(":" + (date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes()));
				sb.append(":" + (date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds()));
				sb.append(", " + weekArrayzh[week]);
				sb.append("," + (date.getMonth() + 1));
				sb.append("/ " + date.getDate());
				sb.append("/" + (date.getYear() + 1900));
				break;
		}
		return sb.toString();

	}
	public static final String[] weekArray = new String[]{"周日","周一","周二","周三","周四","周五","周六"};
	public static final String[] monthArray = new String[]{"1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月"};
	//public static final String[] weekArray = new String[]{"Sun","Mon","Tues","Wed","Thur","Fri","Sat"};
	//public static final String[] monthArray = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
	@SuppressWarnings("deprecation")
	public static String getDateString(Date date, int type) {
		StringBuffer sb = new StringBuffer();

		int week = date.getDay();
		int month = date.getMonth() + 1;

		switch (type) {
			case 1 :
				sb.append(date.getHours() < 10 ? "0" + date.getHours() : date.getHours());
				sb.append(":" + (date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes()));
				sb.append(", " + weekArray[week]);
				sb.append(", " + getDateStr(date.getDate()) + " " + monthArray[month - 1]);
				sb.append(", " + (date.getYear() + 1900));
				break;

			case 2 :
				sb.append(weekArray[week]);
				sb.append(", " + getDateStr(date.getDate()) + " " + monthArray[month - 1]);
				sb.append(", " + (date.getYear() + 1900));
				break;
			case 3 :
				sb.append(monthArray[month - 1]);
				sb.append(", " + (date.getYear() + 1900));
				break;
			case 4 :
				sb.append((date.getYear() + 1900));
				break;
			case 5 :
				sb.append(date.getHours() < 10 ? "0" + date.getHours() : date.getHours());
				sb.append(":" + (date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes()));
				sb.append(", " + weekArray[week]);
				sb.append("," + (date.getMonth() + 1));
				sb.append("/ " + date.getDate());
				sb.append("/" + (date.getYear() + 1900));
				break;
			case 6 :
				sb.append(date.getHours() < 10 ? "0" + date.getHours() : date.getHours());
				sb.append(":" + (date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes()));
				sb.append(":" + (date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds()));
				sb.append(", " + weekArray[week]);
				sb.append("," + (date.getMonth() + 1));
				sb.append("/ " + date.getDate());
				sb.append("/" + (date.getYear() + 1900));
				break;
		}
		return sb.toString();

	}

	private static String getDateStr(int day) {
		switch (day) {
			case 1 :
			case 21 :
			case 31 :
				return day + "st";
			case 2 :
			case 22 :
				return day + "nd";
			case 3 :
			case 23 :
				return day + "rd";
			case 4 :
			case 5 :
			case 6 :
			case 7 :
			case 8 :
			case 9 :
			case 10 :
			case 11 :
			case 12 :
			case 13 :
			case 14 :
			case 15 :
			case 16 :
			case 17 :
			case 18 :
			case 19 :
			case 20 :
			case 24 :
			case 25 :
			case 26 :
			case 27 :
			case 28 :
			case 29 :
			case 30 :
				return day + "th";

		}
		return day + "";
	}

}
