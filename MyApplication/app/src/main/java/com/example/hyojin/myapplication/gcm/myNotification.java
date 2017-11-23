package com.example.hyojin.myapplication.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.example.hyojin.myapplication.R;
import com.example.hyojin.myapplication.activity.Fragment.MainActivity;

/**
 * Created by Hyojin on 2016-07-13.
 */
public class myNotification {
    private final String TAG="HyojinNotification";

    public static final int BY_GCM=1;
    public static final int BY_REVIEW_REGI=2;

    private Context mContext;

    private final String default_title = "Noti";
    public myNotification(Context context){
        this.mContext=context;
    }

    public void sendNotification(int type){
        switch (type){
            case  BY_REVIEW_REGI:
                inside_sendNotification(default_title,"기본알람!!",type);
                break;
        }
    }
    public void sendNotification(String title, String message, int type){
        inside_sendNotification(title,message,type);
    }

    private void inside_sendNotification(String title, String message, int type){
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0/*Request code*/, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.noti)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager=(NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(type/*ID of notification */, notificationBuilder.build());
    }
}

