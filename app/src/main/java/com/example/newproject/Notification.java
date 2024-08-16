package com.example.newproject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class Notification {
    Context context;
    public Notification(Context context){
        this.context = context;
    }
    public void showNotification(String title, String message){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

//        Create a notification channel for Android Oreo and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

//          set channel id, title and importance level
            NotificationChannel channel = new NotificationChannel("1", "Test Notification Channel", NotificationManager.IMPORTANCE_HIGH);

//          set description, explain what the notification is all about
            channel.setDescription("This Notification is to do this and that");
            notificationManager.createNotificationChannel(channel);
        }

//      creating a pending intent, this activity would be launched if the user taps on the notification
        Intent intent = new Intent(context, HeroActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

//      reference the channel id this notification belongs to
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "1");
        builder.setContentTitle(title) // this sets the title of the notification
                .setContentText(message);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH) // this sets the priority of the notification
                .setContentIntent(pendingIntent) // set the pending intent here
                .setSmallIcon(R.drawable.baseline_person_24) // set logo here
                .setAutoCancel(true) // Automatically dismiss the notification when clicked if set to true
                .setOngoing(false);

//        show the notification
        notificationManager.notify(1, builder.build());
    }
}
