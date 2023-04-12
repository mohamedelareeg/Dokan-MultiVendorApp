package com.mohaa.eldokan.services;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mohaa.eldokan.R;


public class FCM_Messaging_Service_Admin extends FirebaseMessagingService{

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
            if (remoteMessage.getFrom().equals("/topics/" + "promotional_messages")) {
                String click_Action = remoteMessage.getNotification().getClickAction();

                    displayNotifcation(remoteMessage.getData().get("title"), remoteMessage.getData().get("content-text"),
                            click_Action);
            }
    }

    private void displayNotifcation(String title, String contentText , String click_Action){
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.mipmap.ic_logo)
                .setAutoCancel(true)
                .setLights(Color.RED, 1000, 300)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSound(alarmSound)
                .setContentTitle(title)
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(click_Action);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(getString(R.string.default_notification_channel_id),
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        int mNotificationID = (int)System.currentTimeMillis();
        notificationManager.notify(mNotificationID, mBuilder.build());
    }
}