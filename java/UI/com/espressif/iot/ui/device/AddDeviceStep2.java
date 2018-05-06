package com.espressif.iot.ui.device;

import com.espressif.iot.R;
import com.espressif.iot.base.api.EspBaseApiUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class AddDeviceStep2 extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_device);

        String ssid = EspBaseApiUtil.getWifiConnectedSsid();
        if (ssid == null)
        {
            ssid = "";
        }
        TextView TV_ssid = (TextView) findViewById(R.id.tv_ssid);
        TV_ssid.setText(ssid);

        SharedPreferences sharedPreferences = getSharedPreferences("wifiInfo", Context.MODE_PRIVATE);
        String wifiSSID = sharedPreferences.getString("wifiSSID", "");
        final String wifiPWD = sharedPreferences.getString("wifiPWD", "");

        final TextView TV_pwd = (TextView) findViewById(R.id.tv_pwd);
        if (wifiSSID.equals(ssid)) {
            TV_pwd.setText(wifiPWD);
        }

        final SharedPreferences.Editor sharedPreferences_edit = sharedPreferences.edit();
        Button bt_next = (Button) findViewById(R.id.bt_confirm);
        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivityForResult(new Intent(AddDeviceStep1.this, AddDeviceStep2.class), 0x13);

               /* if (wifiPWD.length() < 8) {
                    new AlertDialog.Builder(this)
                            .setTitle("wifi password")
                            .setMessage("passwd is too short")
                            .setNegativeButton("comfirm")
                            .create()
                            .show();
                    return;
                }*/

                CheckBox save_wifi = (CheckBox) findViewById(R.id.cb_save);
                if (save_wifi.isChecked()) {
                    sharedPreferences_edit.putString("wifiSSID", EspBaseApiUtil.getWifiConnectedSsid());
                    sharedPreferences_edit.putString("wifiPWD", String.format("%s", TV_pwd.getText()));
                    sharedPreferences_edit.apply();
                }
                startActivity(new Intent(AddDeviceStep2.this, AddDeviceStep3.class));
            }
        });

        ImageView IV_back = (ImageView) findViewById(R.id.add_device_back);
        IV_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
	}
}
