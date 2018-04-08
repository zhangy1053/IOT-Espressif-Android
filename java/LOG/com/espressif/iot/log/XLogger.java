package com.espressif.iot.log;

import com.espressif.iot.base.application.EspApplication;
import android.util.Log;

public class XLogger {
	
	private static final String TAG = "SmartCon";
	
	public static final boolean DEBUG = true;
	
	public static void i(String msg) {
		if (DEBUG) {
			Log.i(TAG, EspApplication.version + " " + msg);
		}
	}
	
	public static void d(String msg) {
		if (DEBUG) {
			Log.i(TAG, EspApplication.version + " " + msg);
		}
	}

	public static void e(String msg) {
		if (DEBUG) {
			Log.e(TAG, EspApplication.version +  " " + msg);
		}
	}
	
	public static void exception(Exception e) {
		if (DEBUG) {
			e.printStackTrace();
		}
	}
}
