package com.espressif.iot.crypt;

public class Keys {
	public final static String des_net = "!wostoresecret!#$%107482";
	public final static String aes_pay = "f3e402cc01e640ff";
	public final static String aes_inf = "446a27578df8da84";
	public final static String pay_cp = "2f0cb27bdc0a4624a2e693fc9beca6e2";

	public static String get_des_net() {
		return Keys.des_net;
	}
	
	public static String get_aes_pay() {
		return Keys.aes_pay;
	}

	public static String get_aes_inf() {
		return Keys.aes_inf;
	}

	public static String get_pay_cp() {
		return Keys.pay_cp;
	}
}
