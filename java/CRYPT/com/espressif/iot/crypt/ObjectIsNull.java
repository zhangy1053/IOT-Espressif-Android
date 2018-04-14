/*
 * Copyright © 2013，the original authors or Tianyuan DIC Computer Co., Ltd.
 *
 * Please see the tydic success stories and it solutions 
 *
 *      http://www.tydic.com/Default.aspx
 *
 * Statement: This document's code after sufficiently has not permitted does not have 
 * any way dissemination and the change, once discovered violates the stipulation, will
 * receive the criminal sanction.
 * Address: 3/F,T3 Building, South 7th Road, South Area, Hi-tech Industrial park, Shenzhen, P.R.C.
 * Email: webmaster@tydic.com　
 * Tel: +86 755 26745688 
 */

package com.espressif.iot.crypt;

import java.util.Date;
import java.util.List;

/**
 * 
 * @author Sun Liang {@link sunliang@tydic.com}
 * @version pntWoGame 2013年11月5日 上午11:00:03
 * @since 1.0
 **/
public class ObjectIsNull {
	/**
	 * 如果为空,返回true;如果为非空,返回false
	 * 
	 * @param obj
	 *            基本对象
	 * @return boolean
	 */
	public static boolean check(Object obj) {
		/* 为空 */
		if (obj == null) {
			return true;
		}
		/* String */
		if (obj instanceof String) {
			return checkString((String) obj);
		}
		/* Integer */
		if (obj instanceof Integer) {
			return checkInteger((Integer) obj);
		}
		/* Long */
		if (obj instanceof Long) {
			return checkLong((Long) obj);
		}
		/* Double */
		if (obj instanceof Double) {
			return checkDouble((Double) obj);
		}
		/* Date */
		if (obj instanceof Date) {
			return checkDate((Date) obj);
		}
		/* List */
		if (obj instanceof List) {
			return checkList((List<?>) obj);
		}
		/* String[] */
		if (obj instanceof String[]) {
			return checkDate((String[]) obj);
		}

		return false;
	}

	private static boolean checkDate(String[] strings) {
		if (strings.length <= 0) {
			return true;
		}
		return false;
	}

	private static boolean checkList(List<?> list) {
		if (list.size() <= 0) {
			return true;
		}
		return false;
	}

	private static boolean checkDate(Date date) {
		if (date == null) {
			return true;
		}
		return false;
	}

	private static boolean checkDouble(Double double1) {
		if (double1.doubleValue() == 0) {
			return true;
		}
		return false;
	}

	private static boolean checkLong(Long long1) {
		if (long1.longValue() == 0 || long1.longValue() == -1L) {
			return true;
		}
		return false;
	}

	private static boolean checkInteger(Integer integer) {
		if (integer.intValue() == 0 || integer.intValue() == -1) {
			return true;
		}
		return false;
	}

	private static boolean checkString(String string) {
		if (string.trim().length() <= 0 || "null".equalsIgnoreCase(string)) {
			return true;
		}
		return false;
	}
}
