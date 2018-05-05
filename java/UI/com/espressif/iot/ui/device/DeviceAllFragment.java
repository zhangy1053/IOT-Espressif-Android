package com.espressif.iot.ui.device;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.espressif.iot.R;
import com.espressif.iot.account.AccountManager;
import com.espressif.iot.log.XLogger;
import com.espressif.iot.net.HttpManager;
import com.espressif.iot.net.HttpManagerInterface;
import com.espressif.iot.ui.configure.Constant;
import com.espressif.iot.ui.device.DeviceListAdapter.ListClickListener;
import com.espressif.iot.util.ToastUtils;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class DeviceAllFragment extends Fragment{
	
	private ListView mDeviceListView;
	private DeviceListAdapter mAdapter;
	
	private static List<DeviceInfoBean> mDeviceList = new ArrayList<DeviceInfoBean>();
	
	private TextView mTips;
	
	private LocalBroadcastManager mBroadcastManager;
	
	private Handler uiHandler = new Handler(Looper.getMainLooper());
	
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		registerReceiver();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.device_all_fragment, container, false);
        mDeviceListView = (ListView) view.findViewById(R.id.device_list);
        mTips = (TextView) view.findViewById(R.id.device_all_tips);
        mTips.setVisibility(View.GONE);
        
        getDeviceData();
        mAdapter = new DeviceListAdapter(getActivity(), mDeviceList);
        mAdapter.setListClickListener(listClickListener);
        mDeviceListView.setAdapter(mAdapter);
		return view;
	}
	
	private ListClickListener listClickListener = new ListClickListener() {
		
		@Override
		public void onSwitchClick(int position) {
			openDevice(position);
		}
		
		@Override
		public void onDeleteClick(int position) {
			
		}
		
		@Override
		public void onClockClick(int position) {
		
		}

		@Override
		public void onWifiClick(int position) {
			
		}
	};
	
	private void openDevice(final int position){
		try {
			JSONObject info = new JSONObject();
			info.put("name_space", "Alexa.PowerController");
			info.put("deviceId", mDeviceList.get(position).getDevice_id());
			info.put("token", "token");
			if (mDeviceList.get(position).getDevice_status().equals("OFF")) {
			    info.put("name", "TurnOn");
			}
			else {
			    info.put("name", "TurnOff");
            }

			HttpManager.getInstances().requestDeviceCtrl(getActivity(), info.toString(),  new HttpManagerInterface() {
				
				@Override
				public void onRequestResult(int flag, final String msg) {
					if(flag == HttpManagerInterface.REQUEST_OK){
					    uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject content = new JSONObject(msg);
                                    JSONArray prop = content.optJSONArray("properties");
                                    for(int i=0; i<prop.length(); i++){
                                        JSONObject status = prop.optJSONObject(i);
                                        String namespace = status.optString("name_space");
                                        if("Alexa.PowerController".equals(namespace)){
                                            String value = status.optString("value");
                                            XLogger.d("Alexa.PowerController:" + value);
                                            mDeviceList.get(position).setDevice_status(value);
                                        }else if("Alexa.EndpointHealth".equals(namespace)){
                                            JSONObject connectivity_value = status.optJSONObject("value");
                                            String value = connectivity_value.optString("value");
                                            XLogger.d("connectivity:" + value);
                                            mDeviceList.get(position).setDevice_connectivity_status(value);
                                        }
                                    }

                                    mAdapter.notifyDataSetChanged();
                                } catch (Exception e) {

                                }
                            }
                        });
					}
				}
			});
		} catch (Exception e) {

		}
	}
	
	public void getDeviceData(){

        try {
            JSONObject device = new JSONObject();
            device.put("token", "token");
            device.put("userId", AccountManager.getInstance().getUserInfo(getActivity()).getUserId());
            
            HttpManager.getInstances().requestDeviceList(getActivity(), device.toString(), new HttpManagerInterface() {
    			
    			@Override
    			public void onRequestResult(int flag, final String msg) {
    				if(flag == HttpManagerInterface.REQUEST_OK){
							try {
								mDeviceList.clear();
								JSONObject content = new JSONObject(msg);
								if(content.has("devices")) {
									JSONArray deviceList = new JSONArray(content.optString("devices"));
									if (deviceList != null && deviceList.length() > 0) {
										for (int i = 0; i < deviceList.length(); i++) {
											JSONObject device = deviceList.getJSONObject(i);
											DeviceInfoBean deviceInfoBean = new DeviceInfoBean();
											deviceInfoBean.setDevice_id(device.optString("deviceId"));
											deviceInfoBean.setDevice_ip("");
											deviceInfoBean.setDevice_name(device.optString("firendlyName"));
											deviceInfoBean.setDevice_status(device.optString("manufacturerName"));
											deviceInfoBean.setDevice_connectivity_status("UNREARCHABLE");
											mDeviceList.add(deviceInfoBean);
										}
									}
									quaryDeviceStatus(getActivity(), mDeviceList);
								}
							}catch(Exception e){

							}
						}
    			}
    		});
		} catch (Exception e) {

		}
	}

	private void quaryDeviceStatus(Context context, List<DeviceInfoBean> deviceList){
		try{
			for(final DeviceInfoBean deviceInfoBean:deviceList){
				JSONObject device = new JSONObject();
				device.put("token", "token");
				device.put("deviceId", deviceInfoBean.getDevice_id());

				HttpManager.getInstances().requestDeviceStatus(context, device.toString(), new HttpManagerInterface() {
					@Override
					public void onRequestResult(int flag, final String msg) {
						if(flag == HttpManagerInterface.REQUEST_OK){
							uiHandler.post(new Runnable() {
								@Override
								public void run() {
									try {
										JSONObject content = new JSONObject(msg);
										JSONArray prop = content.optJSONArray("properties");
										for(int i=0; i<prop.length(); i++){
											JSONObject status = prop.optJSONObject(i);
											String namespace = status.optString("name_space");
											if("Alexa.PowerController".equals(namespace)){
                                                String value = status.optString("value");
                                                XLogger.d("Alexa.PowerController:" + value);
												deviceInfoBean.setDevice_status(value);
											}else if("Alexa.EndpointHealth".equals(namespace)){
                                                JSONObject connectivity_value = status.optJSONObject("value");
                                                String value = connectivity_value.optString("value");
                                                XLogger.d("connectivity:" + value);
                                                deviceInfoBean.setDevice_connectivity_status(value);
											}
										}
										mAdapter.notifyDataSetChanged();
									}catch (Exception e){

									}
								}
							});
						}else{

						}
					}
				});
			}
		}catch (Exception e){

		}
	}

	@Override
	public void onResume() {
		super.onResume();
		quaryDeviceStatus(getActivity(), mDeviceList);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mBroadcastManager.unregisterReceiver(deviceAddReceiver);
	}
	
	/**
	 * 注册广播接收器
	 */
	private void registerReceiver() {
		mBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
	    IntentFilter intentFilter = new IntentFilter();
	    intentFilter.addAction(Constant.DEVICE_ADD_COMPLETE);
	    mBroadcastManager.registerReceiver(deviceAddReceiver, intentFilter);
	}

	private BroadcastReceiver deviceAddReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	if(intent == null){
	    		return;
	    	}
	        final String bssid = intent.getStringExtra("bssid");
	        final String ip = intent.getStringExtra("ip");
	        uiHandler.post(new Runnable() {
				
				@Override
				public void run() {
			        DeviceInfoBean deviceInfoBean = new DeviceInfoBean();
			        deviceInfoBean.setDevice_id(bssid);
			        deviceInfoBean.setDevice_ip(ip);
			        deviceInfoBean.setDevice_name(bssid);
			        deviceInfoBean.setDevice_status(ip);
			        mDeviceList.add(deviceInfoBean);
			        mAdapter.notifyDataSetChanged();
				}
			});
	    }
	};
}
