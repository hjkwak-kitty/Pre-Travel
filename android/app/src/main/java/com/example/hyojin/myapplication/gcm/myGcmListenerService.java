package com.example.hyojin.myapplication.gcm;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by Hyojin on 2016-07-13.
 */

public class myGcmListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data){
        super.onMessageReceived(from,data);
        String title = data.getString("title");
        String message = data.getString("message");
        String is_notifi= data.getString("notification_type");
        if(is_notifi.equals("true")){
            myNotification cnb= new myNotification(getApplicationContext());
            cnb.sendNotification(title,message,myNotification.BY_GCM);
        }
  }
}
