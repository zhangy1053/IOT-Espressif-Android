package com.espressif.iot.net;

public interface HttpManagerInterface {

	public static final int REQUEST_OK = 1;
	public static final int REQUEST_ERROR = 2;

	void onRequestResult(int flag, String msg);
}
