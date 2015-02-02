package org.onebrick.android.activities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import org.onebrick.android.R;

public class DisplayNotificationReceiver extends BroadcastReceiver {
    final static String GROUP_RSVP_REMAINDERS = "group_key_emails";
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle args = intent.getExtras();
        String eventName = args.getString("EventName");
        String message = args.getString("DisplayMessage");
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(context.NOTIFICATION_SERVICE);
        Intent i = new Intent(context.getApplicationContext(),SplashScreenActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, i, 0);

        // build notification
        // the addAction re-use the same intent to keep the example short
        Notification mBuilder  = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(""+eventName)
                .setContentText(""+message)
                .setContentIntent(pIntent)
                .setGroup(GROUP_RSVP_REMAINDERS)
                .setAutoCancel(true)
                .build();
        notificationManager.notify((int)(System.currentTimeMillis() % Integer.MAX_VALUE), mBuilder);
    }
}
