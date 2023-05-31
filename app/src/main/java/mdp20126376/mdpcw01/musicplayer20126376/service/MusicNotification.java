package mdp20126376.mdpcw01.musicplayer20126376.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;


import androidx.core.app.NotificationCompat;

import mdp20126376.mdpcw01.musicplayer20126376.R;
import mdp20126376.mdpcw01.musicplayer20126376.activity.MainActivity;


/**
 * notification class
 */
public class MusicNotification {
    private static final int NOTIFY_ID = 20126376;
    private static NotificationManager notificationManager;
    private static final String channelId = "MusicPLayer20126376";
    private static final String channelName = "MusicPLayer20126376";


    //constructor for notification
    public MusicNotification(MusicService context) {
        notificationManager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
    }

    //display the notification
    public void startDisplayNotification(MusicService context) {
        android.app.Notification myNotification = buildNotification(context);
        context.startForeground(NOTIFY_ID, myNotification);
    }


    public void pausePlayerNotification(MusicService context) {
        context.stopForeground(false);
        android.app.Notification myNotification = buildNotification(context);
        notificationManager.notify(NOTIFY_ID, myNotification);
    }

   //After destroy the service, all the notification will be destroy
    public void stopPlayerNotification(MusicService context) {
        context.stopForeground(true);
        notificationManager.cancelAll();
    }


    private Notification buildNotification(MusicService context) {


        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("MusicPlayer20126376");
            notificationChannel.setSound(null, null);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,channelId);
        builder .setSmallIcon(R.drawable.logo)
                .setContentTitle("The Music player is open. Click here to come back to the App.")
                .setContentIntent(contentIntent)
                .setPriority(android.app.Notification.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                .setChannelId(channelId);

        return builder.build();
    }

}
