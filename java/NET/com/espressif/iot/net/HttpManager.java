package com.espressif.iot.net;

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
	
}
