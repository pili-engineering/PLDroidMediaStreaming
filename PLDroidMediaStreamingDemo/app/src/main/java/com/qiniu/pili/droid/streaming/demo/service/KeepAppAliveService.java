package com.qiniu.pili.droid.streaming.demo.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.qiniu.pili.droid.streaming.demo.R;

public class KeepAppAliveService extends Service {
    private NotificationManager mNotificationManager;
    private String mNotificationId = "keepAppAliveId";
    private String mNotificationName = "Keep app alive";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(mNotificationId, mNotificationName, NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
        }
        startForeground(1, getNotification());
    }

    private Notification getNotification() {
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.qiniu_logo)
                .setContentTitle("七牛推流")
                .setContentText("后台运行中");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(mNotificationId);
        }
        return builder.build();
    }
}
