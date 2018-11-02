package com.example.hyojin.myapplication.gcm;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by Hyojin on 2016-07-13.
 */
public class myInstanceIDListnerService extends InstanceIDListenerService {
    private static final String TAG = "MyInstanceIDLS";

    @Override
    public void onTokenRefresh(){
        Intent intent= new Intent(this,RegisterationIntentService.class);
        startService(intent);
    }
}
