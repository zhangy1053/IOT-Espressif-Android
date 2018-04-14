package com.espressif.iot.ui.main;

import com.espressif.iot.R;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class UserCenterFragment extends Fragment{
	
    private EspMainActivity mActivity;
    
    private TextView mUserNameTxt;
    
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        
        mActivity = (EspMainActivity)activity;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_center, container, false);

		return view;
	}
}
