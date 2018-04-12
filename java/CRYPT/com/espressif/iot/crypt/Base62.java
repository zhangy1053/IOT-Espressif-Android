package com.espressif.iot.crypt;

import java.math.BigInteger;

/**
 * 对数字做base94,注意只能是正整数哈.
 */
public class Base62 {
	/**
	 * 剔除&符号,由于&作为分隔符
	 */
	public static final String baseAscII = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	public static final BigInteger baseNum = BigInteger.valueOf(baseAscII.length());

	/**
	 * Base62 编码
	 * 
	 * @param value
	 *            待转换成base94的数字
	 */
	public static String toBase62(BigInteger value) {
		
		assert value.compareTo(BigInteger.ZERO) >= 0 : "toBase94 值必须大于等于0";
		
		StringBuilder result = new StringBuilder();
		
		while (value.compareTo(BigInteger.ZERO) > 0) {
			BigInteger[] bis = value.divideAndRemainder(baseNum);
			result.append(baseAscII.charAt(bis[1].intValue()));
			value = bis[0];
		}
		
		return result.reverse().toString();
	}

	/**
	 * Base62 解码
	 * 
	 * @param value
	 *            待解码的base94的字符串
	 */
	public static BigInteger fromBase62(String value) {
		assert null != value : "不是一个有效的Base94字符串: null";
		
		BigInteger result = BigInteger.ZERO;
		
		for (char c : value.toCharArray()) {
			int i = baseAscII.indexOf(c);
			assert i >= 0 : "不是一个有效的Base94字符串:" + value;
			result = result.multiply(baseNum).add(BigInteger.valueOf(i));
		}
		
		return result;
	}
}
