package com.example.hyojin.myapplication.activity.setting;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.hyojin.myapplication.R;
import com.example.hyojin.myapplication.app.AppConfig;
import com.example.hyojin.myapplication.app.AppController;
import com.example.hyojin.myapplication.gcm.QuickstartPreferences;
import com.example.hyojin.myapplication.gcm.RegisterationIntentService;
import com.example.hyojin.myapplication.helper.SQLiteHandler;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AlramSettingActivity extends Activity {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST=9000;
    private static final String TAG="Alram Setting Activity";
    private ProgressDialog pDialog;
    private SQLiteHandler db;


    private Button mRegistrationButton;
    private ProgressBar mRegistrationProgressBar;
    private BroadcastReceiver mRegistrationBrodcastReceiver;
    private TextView mInformationTextView;
    SharedPreferences setting;
    boolean alram;
    String name, gcm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alram_setting);

        registBroadcastReceiver();
        setLocalBroadcastManager(AlramSettingActivity.this);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SqLite database handler
        db = new SQLiteHandler(AlramSettingActivity.this);

        HashMap<String, String> user = db.getUserDetails();
        name = user.get("name");




        mInformationTextView=(TextView)findViewById(R.id.informationTextView);
        mRegistrationProgressBar=(ProgressBar)findViewById(R.id.registrationProgressBar);
        mRegistrationProgressBar.setVisibility(ProgressBar.GONE);


        mRegistrationButton=(Button)findViewById(R.id.registerationButton);
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        alram=setting.getBoolean("alram",false);

        mRegistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AlramSettingActivity.this, "알람 상태"+setting.getBoolean("alram",false), Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor edit = setting.edit();
                edit.putBoolean("alram",!alram);
                alram=!alram;
                edit.commit();
                if(setting.getBoolean("alram",false)){
                    Toast.makeText(AlramSettingActivity.this, "알람 울림으로 설정"+setting.getBoolean("alram",false), Toast.LENGTH_SHORT).show();
                    mRegistrationButton.setText("알람 울림");
                    gcm="onn";
                }else{
                    Toast.makeText(AlramSettingActivity.this, "알람 안울림으로 설정"+setting.getBoolean("alram",false), Toast.LENGTH_SHORT).show();
                    mRegistrationButton.setText("알람 안울림");
                    gcm="";
                }
                getInstanceIdToken();


            }
        });

    }

    private boolean checkPlayServices(){
        int resultCode= GooglePlayServicesUtil.isGooglePlayServicesAvailable(AlramSettingActivity.this);
        if(resultCode!= ConnectionResult.SUCCESS){
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)){
                GooglePlayServicesUtil.getErrorDialog(resultCode,AlramSettingActivity.this,PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }else {
                Log.i(TAG, "This device is not supproted");
            }
            return false;
        }
        return true;
    }

    public void getInstanceIdToken(){
        if(checkPlayServices()){
            Intent intent = new Intent(AlramSettingActivity.this,RegisterationIntentService.class);
            AlramSettingActivity.this.startService(intent);
        }else{
            Toast.makeText(AlramSettingActivity.this, "no in regis service", Toast.LENGTH_SHORT).show();
        }
    }

    private void setLocalBroadcastManager(Context context){
        LocalBroadcastManager.getInstance(context).registerReceiver(mRegistrationBrodcastReceiver,new IntentFilter(QuickstartPreferences.REGISTRATION_READY));
        LocalBroadcastManager.getInstance(context).registerReceiver(mRegistrationBrodcastReceiver,new IntentFilter(QuickstartPreferences.REGISTRATION_GENERATING));
        LocalBroadcastManager.getInstance(context).registerReceiver(mRegistrationBrodcastReceiver, new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    private void deleteLocalBoardcastManager(Context context){
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mRegistrationBrodcastReceiver);
    }

    public void registBroadcastReceiver(){
        mRegistrationBrodcastReceiver= new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(action.equals(QuickstartPreferences.REGISTRATION_READY)){
                    mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                    mInformationTextView.setVisibility(View.GONE);
                }else if(action.equals(QuickstartPreferences.REGISTRATION_GENERATING)){
                    mRegistrationProgressBar.setVisibility(ProgressBar.VISIBLE);
                    mInformationTextView.setVisibility(View.VISIBLE);
                    //mInformationTextView.setText("알람 설정 중");
                }else if(action.equals(QuickstartPreferences.REGISTRATION_COMPLETE)){
                    mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                    String token=intent.getStringExtra("token");
                    tokenUp(token,name,gcm);
                    //mInformationTextView.setText(token);
                }
            }
        };
    }

    //토큰 업데이트
    private void tokenUp(final String token, final String name, final String gcm){
        // Tag used to cancel the request
        String tag_string_req = "req_UpdateToken";

        pDialog.setMessage("Update Token ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_TOKEN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Update Token: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response.substring(response.indexOf("{"),response.lastIndexOf("}")+1));
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                        //토큰업데이트
                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(AlramSettingActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Update Scrap List: " + error.getMessage());
                Toast.makeText(AlramSettingActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("user_name", name);
                params.put("gcm",gcm);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }




}
