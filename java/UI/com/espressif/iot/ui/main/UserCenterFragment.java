package com.espressif.iot.ui.main;

import com.espressif.iot.R;
import com.espressif.iot.account.AccountManager;
import com.espressif.iot.log.XLogger;

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
    private TextView mUserPhoneTxt;
    
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
		XLogger.d("userCenter view create");
		
        View view = inflater.inflate(R.layout.user_center, container, false);

        mUserNameTxt = (TextView) view.findViewById(R.id.user_fragment_nickname);
        mUserPhoneTxt = (TextView) view.findViewById(R.id.user_fragment_phonenumber);
        
        setUserData();
		return view;
	}
	
	private void setUserData(){
		mUserNameTxt.setText(AccountManager.getInstance().getUserInfo(getActivity()).getUserName());
		mUserPhoneTxt.setText(AccountManager.getInstance().getUserInfo(getActivity()).getUserPhone());
	}
}
