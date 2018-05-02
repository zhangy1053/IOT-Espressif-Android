package com.espressif.iot.ui.device;

import java.util.List;

import org.json.JSONObject;

import com.espressif.iot.R;
import com.espressif.iot.log.XLogger;
import com.espressif.iot.net.HttpManager;
import com.espressif.iot.net.HttpManagerInterface;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import net.tsz.afinal.FinalActivity;

public class DeviceListAdapter extends BaseAdapter{

	private Context mContext;
	private List<DeviceInfoBean> mDeviceInfoList;
	private ListClickListener mListClickListener;
	
	public DeviceListAdapter(Context context, List<DeviceInfoBean> deviceInfoList) {
		mContext = context;
		mDeviceInfoList = deviceInfoList;
	}
	
	public void setListClickListener(ListClickListener listClickListener){
		mListClickListener = listClickListener;
	}
	
	public interface ListClickListener{
		void onSwitchClick(int position);
		void onDeleteClick(int position);
		void onClockClick(int position);
	}
	
	@Override
	public int getCount() {
		return mDeviceInfoList.size();
	}

	@Override
	public Object getItem(int position) {
		if(position < mDeviceInfoList.size() && position >= 0){
			return mDeviceInfoList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	class ViewHolder{
		LinearLayout listview_ll;
		ImageView switch_img;
		ImageView wifi_img;
		ImageView clock_img;
		ImageView delete_img;
		TextView device_name;
		TextView device_status;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder mHolder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.device_item, null);
			mHolder = new ViewHolder();
			mHolder.switch_img = (ImageView) convertView.findViewById(R.id.device_switch);
			mHolder.wifi_img = (ImageView) convertView.findViewById(R.id.device_wifi);
			mHolder.clock_img = (ImageView) convertView.findViewById(R.id.device_clock);
			//mHolder.delete_img = (ImageView) convertView.findViewById(R.id.device_delete);
			mHolder.device_name = (TextView) convertView.findViewById(R.id.device_name);
			mHolder.device_status = (TextView) convertView.findViewById(R.id.device_status);
			
			convertView.setTag(mHolder);
		}else{
			mHolder = (ViewHolder) convertView.getTag();
		}
		
		mHolder.device_name.setText(mDeviceInfoList.get(position).getDevice_name());
		mHolder.device_status.setText(mDeviceInfoList.get(position).getDevice_status());

		if (mDeviceInfoList.get(position).getDevice_connectivity_status() != null &&
		    mDeviceInfoList.get(position).getDevice_connectivity_status().equals("OK")) {
			mHolder.wifi_img.setImageResource(R.drawable.device_online_wifi);
		}
		else {
			mHolder.wifi_img.setImageResource(R.drawable.device_offline_wifi);
		}

		if (mDeviceInfoList.get(position).getDevice_status() != null &&
				mDeviceInfoList.get(position).getDevice_status().equals("ON")) {
			mHolder.switch_img.setImageResource(R.drawable.device_on);
		}
		else {
			mHolder.switch_img.setImageResource(R.drawable.device_offline);
		}

		mHolder.switch_img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mListClickListener.onSwitchClick(position);
			}
		});
		
		return convertView;
	}
}
