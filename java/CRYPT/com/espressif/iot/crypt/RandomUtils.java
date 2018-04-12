package com.espressif.iot.crypt;

import java.math.BigInteger;
import java.text.DecimalFormat;

public class RandomUtils {

	public static String random(int length) {
		double temp = Math.random();
		for (int i = 0; i < length; i++) {
			temp = temp * 10;
		}
		DecimalFormat df = new DecimalFormat("0.00");
		String num = "" + df.format(temp);
		num = num.substring(0, num.indexOf("."));
		String encode = Base62.toBase62(new BigInteger(num));
		return encode;
	}

}
