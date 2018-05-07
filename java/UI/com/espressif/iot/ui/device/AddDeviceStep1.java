package com.espressif.iot.ui.device;

import com.espressif.iot.R;
import com.espressif.iot.ui.main.EspMainActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AddDeviceStep1 extends Activity{

	private AnimationDrawable animationDrawable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_state_confirm_pair);
		ImageView animationIV = (ImageView) findViewById(R.id.tv_state_confirm_light);
		animationDrawable = (AnimationDrawable) animationIV.getDrawable();
		animationDrawable.start();

        TextView TV_next = (TextView) findViewById(R.id.tv_confirm_pair_state_next);
        TV_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(AddDeviceStep1.this, AddDeviceStep3.class), 0x13);
                //startActivity(new Intent(AddDeviceStep1.this, AddDeviceStep3.class));
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

    @Override
    protected void onResume() {
        super.onResume();
        animationDrawable.start();
    }

    @Override
    protected void onPause() {
        animationDrawable.stop();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        animationDrawable.stop();
        super.onDestroy();
    }
}
