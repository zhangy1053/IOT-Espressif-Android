package com.espressif.iot.ui.device;

import com.espressif.iot.R;

import android.app.Activity;
import android.os.Bundle;

public class DeviceSettingActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dimming_lamp_detail_layout);
	}
}
