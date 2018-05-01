package com.espressif.iot.ui.configure;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;
import com.espressif.iot.R;
import com.espressif.iot.account.AccountManager;
import com.espressif.iot.base.api.EspBaseApiUtil;
import com.espressif.iot.base.application.EspApplication;
import com.espressif.iot.db.IOTApDBManager;
import com.espressif.iot.esptouch.EspWifiAdminSimple;
import com.espressif.iot.esptouch.EsptouchTask;
import com.espressif.iot.esptouch.IEsptouchListener;
import com.espressif.iot.esptouch.IEsptouchResult;
import com.espressif.iot.esptouch.IEsptouchTask;
import com.espressif.iot.esptouch.util.EspAES;
import com.espressif.iot.log.XLogger;
import com.espressif.iot.net.HttpManager;
import com.espressif.iot.net.HttpManagerInterface;
import com.espressif.iot.object.db.IApDB;
import com.espressif.iot.ui.main.EspActivityAbs;
import com.espressif.iot.user.IEspUser;
import com.espressif.iot.user.builder.BEspUser;
import com.espressif.iot.util.BSSIDUtil;
import com.espressif.iot.util.EspStrings;
import com.espressif.iot.util.ToastUtils;
import com.google.zxing.qrcode.ui.ShareCaptureActivity;

public class DeviceEspTouchActivity extends EspActivityAbs implements OnCheckedChangeListener, OnClickListener,
    OnMenuItemClickListener
{
    private IOTApDBManager mIOTApDBManager;
    
    private WifiManager mWifiManager;
    private LocalBroadcastManager mBraodcastManager;
    
    private static final String ESPTOUCH_VERSION = "v0.3.4.3";
    
    private TextView mSsidTV;
    private EditText mPasswordEdT;
    private CheckBox mShowPwdCB;
    private CheckBox mIsHideSsidCB;
    private CheckBox mActivateCB;
    private CheckBox mMultipleDevicesCB;
    private Button mConfirmBtn;
    private TextView mWifiHintTV;
    private TextView mEspTouchVersionTV;
    private View mEsptouchContentView;
    private TextView mEsptouchContentCountTV;
    private Button mEsptouchContentDoneBtn;
    
    private IEspUser mUser;
    
    private static final int POPUPMENU_ID_GET_SHARE = 1;
    private static final int POPUPMENU_ID_SOFTAP_CONFIGURE = 2;
    
    private List<String> mEsptouchDeviceBssidList = new ArrayList<String>();
    private AtomicInteger mEsptouchDeivceRegisterCount = new AtomicInteger();
    private List<String> mRegisteredDeviceBssidList = new ArrayList<String>();
    private List<String> mRegisteredDeviceNameList = new ArrayList<String>();
    
    private static final int REQUEST_SOFTAP_CONFIGURE = 10;
    private static final int REQUEST_GET_SHARED = 12;
    
    private EspWifiAdminSimple mWifiAdmin;
    
    private IEsptouchResult mIEsptouchResult;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.esp_esptouch);
        
        mWifiAdmin = new EspWifiAdminSimple(this);
        mIOTApDBManager = IOTApDBManager.getInstance();
        mWifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        mUser = BEspUser.getBuilder().getInstance();
        
        String ssid = EspBaseApiUtil.getWifiConnectedSsid();
        if (ssid == null)
        {
            ssid = "";
        }
        mSsidTV = (TextView)findViewById(R.id.ssid);
        mSsidTV.setText(getString(R.string.esp_esptouch_ssid, ssid));
        
        final String bssid = getConnectionBssid();
        mPasswordEdT = (EditText)findViewById(R.id.password);
        String password = getCurrentWifiPassword(bssid);
        mPasswordEdT.setText(password);
        
        mShowPwdCB = (CheckBox)findViewById(R.id.show_password);
        mShowPwdCB.setOnCheckedChangeListener(this);
        
        mIsHideSsidCB = (CheckBox)findViewById(R.id.is_hidden);
        mActivateCB = (CheckBox)findViewById(R.id.activate);
        mMultipleDevicesCB = (CheckBox)findViewById(R.id.multiple_devices);
        
        if (!mUser.isLogin())
        {
            mActivateCB.setChecked(false);
            mActivateCB.setVisibility(View.GONE);
        }
        
        boolean isWifiConnected = EspBaseApiUtil.isWifiConnected();
        mConfirmBtn = (Button)findViewById(R.id.comfirm_btn);
        mConfirmBtn.setOnClickListener(this);
        mConfirmBtn.setEnabled(isWifiConnected);
        
        mWifiHintTV = (TextView)findViewById(R.id.wifi_connect_hint);
        mWifiHintTV.setVisibility(isWifiConnected ? View.GONE : View.VISIBLE);
        
        mEspTouchVersionTV = (TextView)findViewById(R.id.esptouch_version);
        mEspTouchVersionTV.setText(getString(R.string.esp_esptouch_version, ESPTOUCH_VERSION));
        
        setTitle(R.string.esp_configure_add);
        setTitleRightIcon(R.drawable.esp_icon_menu_moreoverflow);
        
        mEsptouchContentView = View.inflate(this, R.layout.esptouch_dialog_content, null);
        mEsptouchContentCountTV = (TextView)mEsptouchContentView.findViewById(R.id.esptouch_tv_count);
        mEsptouchContentDoneBtn = (Button)mEsptouchContentView.findViewById(R.id.esptouch_btn_done);
        
        // register Receiver
        mBraodcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter filter = new IntentFilter(EspStrings.Action.ESPTOUCH_CONTACTING_SERVER);
        filter.addAction(EspStrings.Action.ESPTOUCH_DEVICE_FOUND);
        filter.addAction(EspStrings.Action.ESPTOUCH_DEVICE_REGISTERED);
        filter.addAction(EspStrings.Action.ESPTOUCH_REGISTERING_DEVICES);
        mBraodcastManager.registerReceiver(mEsptouchReceiver, filter);
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();
        
        IntentFilter wifiFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mWifiReceiver, wifiFilter);
    }
    
    @Override
    protected void onPause()
    {
        super.onPause();
        
        unregisterReceiver(mWifiReceiver);
    }
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mBraodcastManager.unregisterReceiver(mEsptouchReceiver);
    }
    
    @Override
    protected void onTitleRightIconClick(View rightIcon)
    {
        PopupMenu popupMenu = new PopupMenu(this, rightIcon);
        Menu menu = popupMenu.getMenu();
        menu.add(Menu.NONE, POPUPMENU_ID_GET_SHARE, 0, R.string.esp_esptouch_menu_get_share);
        menu.add(Menu.NONE, POPUPMENU_ID_SOFTAP_CONFIGURE, 0, R.string.esp_esptouch_menu_softap_configure);
//        menu.add(Menu.NONE, POPUPMENU_ID_BROWSER_CONFIGURE, 0, R.string.esp_esptouch_menu_browser_configure);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }
    
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        if (buttonView == mShowPwdCB)
        {
            if (isChecked)
            {
                mPasswordEdT.setInputType(InputType_PASSWORD_VISIBLE);
            }
            else
            {
                mPasswordEdT.setInputType(InputType_PASSWORD_NORMAL);
            }
        }
    }

    @Override
    public void onClick(View v)
    {
        if (v == mConfirmBtn)
        {
            doEspTouch();
        }
    }
    
    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
        switch(item.getItemId())
        {
            case POPUPMENU_ID_GET_SHARE:
                startActivityForResult(new Intent(this, ShareCaptureActivity.class), REQUEST_GET_SHARED);
                return true;
            case POPUPMENU_ID_SOFTAP_CONFIGURE:
                startActivityForResult(new Intent(this, DeviceSoftAPConfigureActivity.class), REQUEST_SOFTAP_CONFIGURE);
                return true;
        }
        return false;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode) {
            case REQUEST_SOFTAP_CONFIGURE:
                if (resultCode == RESULT_OK) {
                    finish();
                }
                break;
            case REQUEST_GET_SHARED:
                if (resultCode == RESULT_OK) {
                    finish();
                }
                break;
        }
    }
    
    private String getConnectionBssid()
    {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        if (wifiInfo != null)
        {
            return wifiInfo.getBSSID();
        }
        
        return null;
    }
    
    private String getCurrentWifiPassword(String currentBssid)
    {
        List<IApDB> apDBList = mIOTApDBManager.getAllApDBList();
        for (IApDB ap : apDBList)
        {
            if (ap.getBssid().equals(currentBssid))
            {
                return ap.getPassword();
            }
        }
        
        return "";
    }
    
    private BroadcastReceiver mWifiReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            boolean isWifiConnected = EspBaseApiUtil.isWifiConnected();
            mConfirmBtn.setEnabled(isWifiConnected);
            mWifiHintTV.setVisibility(isWifiConnected ? View.GONE : View.VISIBLE);
            
            String ssid = EspBaseApiUtil.getWifiConnectedSsid();
            String password = "";
            if (ssid == null)
            {
                ssid = "";
            }
            else
            {
                String bssid = getConnectionBssid();
                password = getCurrentWifiPassword(bssid);
            }
            mSsidTV.setText(getString(R.string.esp_esptouch_ssid, ssid));
            mPasswordEdT.setText(password);
        }
        
    };
    
    private BroadcastReceiver mEsptouchReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();
            final Resources resources = getResources();
            String text = null;
            if(action.equals(EspStrings.Action.ESPTOUCH_DEVICE_FOUND))
            {
                int quantity = mEsptouchDeviceBssidList.size();
                text = resources.getQuantityString(R.plurals.esp_esptouch_device_found, quantity, quantity);
                mEsptouchContentCountTV.setText(text);
            }
            else if (action.equals(EspStrings.Action.ESPTOUCH_CONTACTING_SERVER))
            {
                // when contacting server, make done button gone
                mEsptouchContentDoneBtn.setVisibility(View.GONE);
                text = resources.getString(R.string.esp_esptouch_contacting_server);
                mEsptouchContentCountTV.setText(text);
            }
            else if(action.equals(EspStrings.Action.ESPTOUCH_REGISTERING_DEVICES))
            {
                text = resources.getString(R.string.esp_esptouch_registering_devices);
                mEsptouchContentCountTV.setText(text);
            }
            else if(action.equals(EspStrings.Action.ESPTOUCH_DEVICE_REGISTERED))
            {
                String deviceBssid = intent.getStringExtra(EspStrings.Key.DEVICE_BSSID);
                mRegisteredDeviceBssidList.add(deviceBssid);
                String deviceName = intent.getStringExtra(EspStrings.Key.DEVICE_NAME);
                mRegisteredDeviceNameList.add(deviceName);
                int quantity = mEsptouchDeivceRegisterCount.incrementAndGet();
                text = resources.getQuantityString(R.plurals.esp_esptouch_device_registered, quantity, quantity);
                mEsptouchContentCountTV.setText(text);
            }
        }
        
    };
    
    private IEsptouchListener mEsptouchListener = new IEsptouchListener()
    {
        
        @Override
        public void onEsptouchResultAdded(final IEsptouchResult result)
        {
            runOnUiThread(new Runnable()
            {
                
                @Override
                public void run()
                {
                    String bssid = BSSIDUtil.restoreBSSID(result.getBssid());
                    mEsptouchDeviceBssidList.add(bssid);
                    Context context = EspApplication.sharedInstance().getApplicationContext();
                    LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context);
                    Intent intent = new Intent(EspStrings.Action.ESPTOUCH_DEVICE_FOUND);
                    broadcastManager.sendBroadcast(intent);
                }
                
            });
        }
    };
    
    private List<String> filterConfigureDeviceNameList()
    {
        List<String> deviceNameList = new ArrayList<String>();
        
        for (int i = 0; i < mRegisteredDeviceBssidList.size(); ++i)
        {
            String deviceBssid = mRegisteredDeviceBssidList.get(i);
            if (mEsptouchDeviceBssidList.contains(deviceBssid))
            {
                String deviceName = mRegisteredDeviceNameList.get(i);
                deviceNameList.add(deviceName);
                mEsptouchDeviceBssidList.remove(deviceBssid);
                break;
            }
        }
        
        return deviceNameList;
    }
    
    private void doEspTouch()
    {
        mRegisteredDeviceBssidList.clear();
        mRegisteredDeviceNameList.clear();
        mEsptouchDeivceRegisterCount.set(0);
        mEsptouchDeviceBssidList.clear();
        
        String apSsid = EspBaseApiUtil.getWifiConnectedSsid();
        String apPassword = mPasswordEdT.getText().toString();
        String apBssid = mWifiAdmin.getWifiConnectedBssid();
        String taskResultCountStr = "1";

        XLogger.d("mBtnConfirm is clicked, mEdtApSsid = " + apSsid
                    + ", " + " mEdtApPassword = " + apPassword);
        if(mTask != null) {
            mTask.cancelEsptouch();
        }
        mTask = new EsptouchAsyncTask();
        mTask.execute(apSsid, apBssid, apPassword, taskResultCountStr);
    }
    
    
    
    private void esptouchOver()
    {
        setResult(Activity.RESULT_OK);
        finish();
    }
    
    private EsptouchAsyncTask mTask;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }

            if(action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
                NetworkInfo ni = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (ni != null && !ni.isConnected()) {
                    if (mTask != null) {
                        mTask.cancelEsptouch();
                        mTask = null;
                        new AlertDialog.Builder(DeviceEspTouchActivity.this)
                                .setMessage("Wifi disconnected or changed")
                                .setNegativeButton(android.R.string.cancel, null)
                                .show();
                    }
                }
            }
        }
    };

    private void deviceRegister(String info){
        try {
            JSONObject infoObj = new JSONObject(info);

        	JSONObject addDevice = new JSONObject();
        	addDevice.put("deviceId", mIEsptouchResult.getBssid());
        	addDevice.put("deviceType", infoObj.optString("deviceType"));
        	addDevice.put("firendlyName", infoObj.optString("deviceType") + " " + mIEsptouchResult.getBssid().substring(mIEsptouchResult.getBssid().length() - 2));
        	addDevice.put("manufacturerName", infoObj.optString("manufacturerName"));
        	addDevice.put("userId", AccountManager.getInstance().getUserInfo(this).getUserId());

        	HttpManager.getInstances().requestDeviceRegister(this, addDevice.toString(), new HttpManagerInterface() {

    			@Override
    			public void onRequestResult(int flag, String msg) {
    				if(flag == HttpManagerInterface.REQUEST_OK){
    					try {
        					JSONObject content = new JSONObject(msg);
        					JSONObject result = new JSONObject(content.optString("result"));
        					if("OK".equals(result.optString("code"))){
        						ToastUtils.showToast(DeviceEspTouchActivity.this, "设备注册成功");
        					}

						} catch (Exception e) {
							ToastUtils.showToast(DeviceEspTouchActivity.this, "设备注册失败");
						}
    				}else{
    					ToastUtils.showToast(DeviceEspTouchActivity.this, "设备注册失败");
    				}
    			}
    		});
        } catch (Exception e) {

        }
    }

    
    private void onEsptoucResultAddedPerform(final IEsptouchResult result) {
    	XLogger.d("onEsptoucResultAddedPerform");
    	
    	try {
        	mIEsptouchResult = result;

            getDeviceInfo();

		} catch (Exception e) {

		}

//        runOnUiThread(new Runnable() {
//
//            @Override
//            public void run() {
//            	mIEsptouchResult = result;
//                String text = result.getBssid() + " is connected to the wifi";
//                Toast.makeText(DeviceEspTouchActivity.this, text,
//                        Toast.LENGTH_LONG).show();
//            }
//
//        });
    }
    
    private Socket socket; 
    
    private void getDeviceInfo(){
    	
    	XLogger.d("getDeviceInfo");
    	
    	new Thread(new Runnable() {  
            
            @Override  
            public void run() {  
                try {
                    XLogger.d("IP:" + mIEsptouchResult.getInetAddress().getHostAddress());

                    socket = new Socket(mIEsptouchResult.getInetAddress().getHostAddress(), 55555);
                    InputStream is = socket.getInputStream();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int i=-1;
                    while(  (i=is.read()) !=-1  ){
                        baos.write(i);
                    }
                    String result = baos.toString();

                    XLogger.d("getUserInfo result=" + result);
                    is.close();
                    socket.close();

                    deviceRegister(result);
                      
                } catch (Exception e) {  
                    e.printStackTrace();  
                }
            }  
        }).start(); 
    }
    
    private void toastEspTouchResult(boolean suc)
    {
        int msgRes = suc ? R.string.esp_configure_result_success : R.string.esp_configure_result_failed;
        Toast.makeText(this, msgRes, Toast.LENGTH_LONG).show();
    }
    
    private IEsptouchListener myListener = new IEsptouchListener() {

        @Override
        public void onEsptouchResultAdded(final IEsptouchResult result) {
            onEsptoucResultAddedPerform(result);
        }
    };
    
    private class EsptouchAsyncTask extends AsyncTask<String, Void, List<IEsptouchResult>> {

        // without the lock, if the user tap confirm and cancel quickly enough,
        // the bug will arise. the reason is follows:
        // 0. task is starting created, but not finished
        // 1. the task is cancel for the task hasn't been created, it do nothing
        // 2. task is created
        // 3. Oops, the task should be cancelled, but it is running
        private final Object mLock = new Object();
        private ProgressDialog mProgressDialog;
        private IEsptouchTask mEsptouchTask;

        public void cancelEsptouch() {
            cancel(true);
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            if (mEsptouchTask != null) {
                mEsptouchTask.interrupt();
            }
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(DeviceEspTouchActivity.this);
            mProgressDialog
                    .setMessage("Esptouch is configuring, please wait for a moment...");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    synchronized (mLock) {
                        XLogger.d("progress dialog is canceled");
                        if (mEsptouchTask != null) {
                            mEsptouchTask.interrupt();
                        }
                    }
                }
            });
            mProgressDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                    "Waiting...", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        	XLogger.d("progress dialog is ok");
                        	Intent intent = new Intent(Constant.DEVICE_ADD_COMPLETE);
                            intent.putExtra("bssid", mIEsptouchResult.getBssid());
                            intent.putExtra("ip", mIEsptouchResult.getInetAddress().getHostAddress());
                            LocalBroadcastManager.getInstance(DeviceEspTouchActivity.this).sendBroadcast(intent);

                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    });
            mProgressDialog.show();
            mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                    .setEnabled(false);
        }

        @Override
        protected List<IEsptouchResult> doInBackground(String... params) {
            int taskResultCount = -1;
            synchronized (mLock) {
                // !!!NOTICE
                String apSsid = mWifiAdmin.getWifiConnectedSsidAscii(params[0]);
                String apBssid = params[1];
                String apPassword = params[2];
                String taskResultCountStr = params[3];
                taskResultCount = Integer.parseInt(taskResultCountStr);
                boolean useAes = false;
                if (useAes) {
                    byte[] secretKey = "1234567890123456".getBytes(); // TODO modify your own key
                    EspAES aes = new EspAES(secretKey);
                    mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, aes, DeviceEspTouchActivity.this);
                } else {
                    mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, null, DeviceEspTouchActivity.this);
                }
                mEsptouchTask.setEsptouchListener(myListener);
            }
            List<IEsptouchResult> resultList = mEsptouchTask.executeForResults(taskResultCount);
            return resultList;
        }

        @Override
        protected void onPostExecute(List<IEsptouchResult> result) {
            mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                    .setEnabled(true);
            mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(
                    "OK");
            if (result == null) {
                mProgressDialog.setMessage("Create Esptouch task failed, the esptouch port could be used by other thread");
                return;
            }

            IEsptouchResult firstResult = result.get(0);
            // check whether the task is cancelled and no results received
            if (!firstResult.isCancelled()) {
                int count = 0;
                // max results to be displayed, if it is more than maxDisplayCount,
                // just show the count of redundant ones
                final int maxDisplayCount = 5;
                // the task received some results including cancelled while
                // executing before receiving enough results
                if (firstResult.isSuc()) {
                    StringBuilder sb = new StringBuilder();
                    for (IEsptouchResult resultInList : result) {
                        sb.append("Esptouch success, bssid = "
                                + resultInList.getBssid()
                                + ",InetAddress = "
                                + resultInList.getInetAddress()
                                .getHostAddress() + "\n");
                        count++;
                        if (count >= maxDisplayCount) {
                            break;
                        }
                    }
                    if (count < result.size()) {
                        sb.append("\nthere's " + (result.size() - count)
                                + " more result(s) without showing\n");
                    }
                    mProgressDialog.setMessage(sb.toString());
                } else {
                    mProgressDialog.setMessage("Esptouch fail");
                }
            }
        }
    }
}
