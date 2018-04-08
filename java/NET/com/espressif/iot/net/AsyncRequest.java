package com.espressif.iot.net;

import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.espressif.iot.log.XLogger;

public class AsyncRequest {

	private volatile static AsyncRequest instance;
	
	private ExecutorService mExecutorService;

	private HttpNet mHttpNet;

	public AsyncRequest() {
		mExecutorService = Executors.newFixedThreadPool(5);
		mHttpNet = new HttpNet();
	}
	
	public static AsyncRequest getInstance(){
		if(instance == null){
			synchronized (AsyncRequest.class) {
				if(instance == null){
					instance = new AsyncRequest();
				}
			}
		}
		return instance;
	}
	
	//带请求头的http/https请求，如果没有请求头，传null即可
	public void requestMessage(final String url, final String message, final HashMap<String, String> header, final RequestListener listener) {	
		mExecutorService.submit(new Runnable() {
			
			@Override
			public void run() {
				try {
					String is = mHttpNet.doHttpPost(url, message, header);
					listener.onComplete(is);
				} catch (Exception e) {
					XLogger.e(e.getMessage());
				}
			}
		});
	}

	public static interface RequestListener {
		public void onComplete(String response);
	}
	
	public static interface RequestListenerStream{
		public void onComplete(InputStream response);
	}
}
