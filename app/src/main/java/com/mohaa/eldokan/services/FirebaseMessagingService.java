package com.mohaa.eldokan.services;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.Utils.ProductsUI;
import com.mohaa.eldokan.models.OrdersState;


import java.util.List;
/**
 * Created by Mohamed El Sayed
 */
public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private List<OrdersState> notificationList;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String click_Action = remoteMessage.getNotification().getClickAction();
        String dataOrderID = remoteMessage.getData().get("order_id");
        String messageTitle = remoteMessage.getNotification().getTitle();
        String messageBody = remoteMessage.getNotification().getBody();
        // ========================== Update NotificationFragment ==========================
        /*
        notificationList = UserWiazrd.getInstance().getTempUser().getNotificationList();
        Notification notification = new Notification();
        notification.setType(type);
        notification.setForward(forward);
        notification.setPost(post);
        notification.setFrom(dataFrom);
        notification.setMessage(message);
        notificationList.add(notification);
        UserWiazrd.getInstance().getTempUser().setNotificationList(notificationList);
        */
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.mipmap.ic_logo)
                .setAutoCancel(true)
                .setLights(Color.RED, 1000, 300)
                .setDefaults(android.app.Notification.DEFAULT_VIBRATE)
                .setSound(alarmSound)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        Intent intent = new Intent(click_Action);
        intent.putExtra("blog_post_id",dataOrderID);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);


        int mNotificationID = (int)System.currentTimeMillis();
        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(getString(R.string.default_notification_channel_id),
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        manager.notify(mNotificationID,mBuilder.build());

    }
}