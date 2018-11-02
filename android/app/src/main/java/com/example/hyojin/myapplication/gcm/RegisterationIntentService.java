package com.example.hyojin.myapplication.gcm;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.example.hyojin.myapplication.R;


import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * Created by Hyojin on 2016-07-13.
 */
public class RegisterationIntentService extends IntentService{
    private static final String TAG ="RegistrationIntentService";

    public RegisterationIntentService(){
        super(TAG);
    }
    @SuppressLint("LongLogTag")
    @Override
    protected void onHandleIntent(Intent intent) {
        //GCM인스턴스 아이디의 토큰을 가져오는 작업이 시작되면 LocalBoardcast로 GENERATING 액션을 알려 ProgressBar가 동작하게 한다..
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(QuickstartPreferences.REGISTRATION_GENERATING));

        Log.d(TAG, "in Registerservice"); //GCM을 위한 instance ID가져옴

        InstanceID instanceID = InstanceID.getInstance(this);
        String token=null;
        try {
            synchronized (TAG){
                String default_senderId= getString(R.string.gcm_defaultSenderId);
                String scope = GoogleCloudMessaging.INSTANCE_ID_SCOPE;
                token = instanceID.getToken(default_senderId,scope,null);

                Log.i(TAG,"GCM Registration Token: "+token);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token",token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }
}
