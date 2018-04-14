package com.espressif.iot.account;

import org.json.JSONObject;

import com.espressif.iot.util.SharedPrefUtils;
import android.content.Context;
import android.text.TextUtils;

public class AccountManager {
	
	private static volatile AccountManager instance;
	
	private UserInfo mUserInfo;
	
	private AccountManager(){
		mUserInfo = new UserInfo();
	}
	
	public static AccountManager getInstance(){
		if(instance == null){
			synchronized (AccountManager.class) {
				if(instance == null){
					instance = new AccountManager();
				}
			}
		}
		return instance;
	}
	
	public void saveUserInfo(Context context, String userInfo){
		if(TextUtils.isEmpty(userInfo)){
			return;
		}
		mUserInfo = strtoUserInfo(userInfo);
		SharedPrefUtils.saveUserInfo(context, mUserInfo.toString());
	}
	
	public UserInfo getUserInfo(Context context){
		if(mUserInfo == null){
			mUserInfo = strtoUserInfo(SharedPrefUtils.getUserInfo(context));
		}
		return mUserInfo;
	}
	
	public boolean isLogin(Context context){
		if(getUserInfo(context) != null){
			return true;
		}else{
			return false;
		}
	}
	
	private UserInfo strtoUserInfo(String user){
		UserInfo userInfo = new UserInfo();
		try {
			JSONObject userObj = new JSONObject(user);
			userInfo.setUserId(userObj.optString("userId"));
			userInfo.setUserName(userObj.optString("userName"));
			userInfo.setUserPhone(userObj.optString("userPhone"));
			userInfo.setUserEmail(userObj.optString("userEmail"));
		} catch (Exception e) {

		}
		return userInfo;
	}
}
