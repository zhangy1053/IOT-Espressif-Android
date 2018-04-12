package com.espressif.iot.crypt;

import java.math.BigInteger;
import java.net.URLDecoder;
import android.text.TextUtils;
import com.espressif.iot.log.XLogger;

public class CryptHelper {

	public static String makeaes(String content, int type, int key) {
		if(TextUtils.isEmpty(content)){
			return "";
		}

		if (type == 0) {
			if (key == 0) {
				return AESUtils.encryptAndHex(content, "");
			}
		} else if (type == 1) {
			if (key == 0) {
				return AESUtils.decryptAnd2String(content, "");
			}
		}
		
		return "";
	}
	
	public static String make3des(String content, int type, String key) {
		String seckey = "";
		if(TextUtils.isEmpty(content)) {
			XLogger.e("make3des error !!!");
			return "";
		}
		
		if(key.equals("")){
			seckey = "";
		}else{
			XLogger.e("no encrypt key found !!!");
			return "";
		}
		
		if(type == 0){
			return CryptUtil.encryptBy3DesAndBase64(content, seckey);
		}else{
			return CryptUtil.decryptBy3DesAndBase64(content, seckey);
		}
	}
	
	public static String makebase62(String content) {
		if(TextUtils.isEmpty(content)){
			return "";
		}
		
		return Base62.toBase62(new BigInteger(content));
	}
	
	public static String ParseSecConent(String content, String key){
		
		if(TextUtils.isEmpty(content)){
			return "";
		}
		
		try{
			return URLDecoder.decode(CryptUtil.decryptBy3DesAndBase64(content, key), "utf-8");
		}catch(Exception e){
			return "";
		}
	}
}
