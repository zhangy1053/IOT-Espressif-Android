package com.espressif.iot.net;

import org.json.JSONObject;

import com.espressif.iot.log.XLogger;
import com.espressif.iot.net.AsyncRequest.RequestListener;
import com.espressif.iot.ui.configure.Constant;
import android.content.Context;
import android.text.TextUtils;

public class HttpManager {
	
	private volatile static HttpManager instances;
	
	private AsyncRequest mAsyncRequest;
	
	private HttpManager() {
		mAsyncRequest = new AsyncRequest();
	}

	public static HttpManager getInstances() {
		if(instances == null){
			synchronized (HttpManager.class) {
				if(instances == null){
					instances = new HttpManager();
				}
			}
		}
		return instances;
	}
	
	public void requestSmsCode(final Context context, String message, final HttpManagerInterface listener){
		try {
            
			mAsyncRequest.requestMessage(Constant.REGISTER, "", new RequestListener() {
				
				@Override
				public void onComplete(String response) {
					if(TextUtils.isEmpty(response)){
						listener.onRequestResult(HttpManagerInterface.REQUEST_ERROR, "");
						return;
					}
					listener.onRequestResult(HttpManagerInterface.REQUEST_OK, response);
				}
			});
		} catch (Exception e) {
			listener.onRequestResult(HttpManagerInterface.REQUEST_ERROR, "");
		}
	}
	
	public void requestRegister(final Context context, String message, final HttpManagerInterface listener){
		
		try {
			JSONObject msg = new JSONObject(message);
            JSONObject register = new JSONObject();
            register.put("name_space", "AccountManagement");
            register.put("name", "AddUser");
            register.put("userName", msg.optString("userName"));
            register.put("passwd", msg.optString("passwd"));
            register.put("userPhone", msg.optString("userPhone"));
            register.put("userEmail", msg.optString("userEmail"));
            register.put("RegisterCode", msg.optString("RegisterCode"));
            
            XLogger.d("Register:" + register.toString());
            
			mAsyncRequest.requestMessage(Constant.REGISTER, register.toString(), new RequestListener() {
				
				@Override
				public void onComplete(String response) {
					XLogger.d(response);
					if(TextUtils.isEmpty(response)){
						listener.onRequestResult(HttpManagerInterface.REQUEST_ERROR, "");
						return;
					}
					listener.onRequestResult(HttpManagerInterface.REQUEST_OK, response);
				}
			});
		} catch (Exception e) {
			listener.onRequestResult(HttpManagerInterface.REQUEST_ERROR, "");
		}
	}
	
	public void requestEmailCode(final Context context, String email, final HttpManagerInterface listener){
		
		try {
            JSONObject requestCode = new JSONObject();
            requestCode.put("name_space", "AccountManagement");
            requestCode.put("name", "RequestRegisterCode");
            requestCode.put("userAccount", email);
            
            XLogger.d("RequestCode:" + requestCode.toString());
            
			mAsyncRequest.requestMessage(Constant.REGISTER, requestCode.toString(), new RequestListener() {
				
				@Override
				public void onComplete(String response) {
					XLogger.d(response);
					if(TextUtils.isEmpty(response)){
						listener.onRequestResult(HttpManagerInterface.REQUEST_ERROR, "");
						return;
					}
					listener.onRequestResult(HttpManagerInterface.REQUEST_OK, response);
				}
			});
		} catch (Exception e) {
			listener.onRequestResult(HttpManagerInterface.REQUEST_ERROR, "");
		}
	}
	
	public void requestLogin(final Context context, String message, final HttpManagerInterface listener){
		
		try {
			JSONObject msg = new JSONObject(message);
            JSONObject loginObj = new JSONObject();
            loginObj.put("name_space", "AccountManagement");
            loginObj.put("name", "Login");
            loginObj.put("loginName", msg.optString("loginName"));
            loginObj.put("passwd", msg.optString("passwd"));
            
            XLogger.d("Login:" + loginObj.toString());
            
			mAsyncRequest.requestMessage(Constant.REGISTER, loginObj.toString(), new RequestListener() {
				
				@Override
				public void onComplete(String response) {
					XLogger.d(response);
					if(TextUtils.isEmpty(response)){
						listener.onRequestResult(HttpManagerInterface.REQUEST_ERROR, "");
						return;
					}
					listener.onRequestResult(HttpManagerInterface.REQUEST_OK, response);
				}
			});
		} catch (Exception e) {
			listener.onRequestResult(HttpManagerInterface.REQUEST_ERROR, "");
		}
	}
}
