package com.espressif.iot.net;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import com.espressif.iot.log.XLogger;

import android.text.TextUtils;

public class HttpNet {

	public static final String GET = "GET";
	public static final String POST = "POST";
	public static final String UTF_8 = "utf-8";

	public static final String HOSTNAME = "";

	public String doHttpPost(String url, String entry, HashMap<String, String> headerParams) {

		HttpURLConnection connection = null;
		try {
			URL mUrl = new URL(url);
			connection = (HttpURLConnection) mUrl.openConnection();
			connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
			connection.setRequestMethod(POST);
			connection.setReadTimeout(30 * 1000);
			connection.setConnectTimeout(10 * 1000);
			connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
			connection.addRequestProperty("Connection", "close");
            if(headerParams != null){
                for(String key:headerParams.keySet()){
                	connection.setRequestProperty(key, headerParams.get(key));
                }
            }

			if (connection instanceof HttpsURLConnection) {
				HttpsURLConnection connection2 = (HttpsURLConnection) connection;
				connection2.setHostnameVerifier(new HostnameVerifier() {
					
					public boolean verify(String arg0, SSLSession arg1) {
						if (HOSTNAME.equals(arg0)) {
							return true;
						}
						return false;
					}
				});
			}
			connection.connect();
			
			if(!TextUtils.isEmpty(entry)){
	            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
	            dos.write(entry.getBytes());
	            dos.flush();
	            dos.close();
            }
			
            String outputStr = null;
			if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
				outputStr = streamToString(connection.getInputStream());
			}
			
			return outputStr;
		} catch (Exception e) {
            XLogger.e("doHttpsPost:" + e.getMessage());
			return null;
		} finally {
			if (connection != null) {
				connection.disconnect();
				connection = null;
			}
		}
	}
	
	private String streamToString(InputStream is) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            baos.close();
            is.close();
            byte[] byteArray = baos.toByteArray();
            return new String(byteArray);
        } catch (Exception e) {
        	XLogger.e("streamToString:" + e.getMessage());
            return null;
        }
    }
}