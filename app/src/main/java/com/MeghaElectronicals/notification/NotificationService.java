package com.MeghaElectronicals.notification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.MeghaElectronicals.R;
import com.MeghaElectronicals.alarm.AlarmReceiver;
import com.MeghaElectronicals.views.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;
import java.util.Map;

public class NotificationService extends FirebaseMessagingService {

    public static final String TAG = "Notification";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Map<String, String> data = message.getData();
        Log.d(TAG, "Title Background: " + data.get("title"));
        Log.d(TAG, "Body Background: " + data.get("body"));
        Log.d(TAG, "Time: " + Calendar.getInstance().getTime());

//        [WORK MANAGER]
//        Data.Builder dataBuild = new Data.Builder();
//        dataBuild.putString("title", data.get("title"));
//
//        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class).setInputData(dataBuild.build()).build();
//        WorkManager.getInstance(this).enqueue(workRequest);

//        [ALARM MANGER]
        Intent intentAlarmReceiver = new Intent(this, AlarmReceiver.class);
        intentAlarmReceiver.putExtra("task", data.get("title"));
        intentAlarmReceiver.putExtra("desc", data.get("body"));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intentAlarmReceiver, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager manager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, pendingIntent);

        sendNotification(data.get("title"), data.get("body"));
    }

    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE);

        String channelId = getString(R.string.channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri customSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setColor(ContextCompat.getColor(this, R.color.blue))
                .setSmallIcon(R.drawable.ic_bell)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVibrate(new long[]{0, 500, 1000})
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel
        NotificationChannel channel = new NotificationChannel(
                channelId,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        channel.setSound(defaultSoundUri, new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .build());
        channel.enableVibration(true);
        channel.setVibrationPattern(new long[]{0, 500, 1000});

        notificationManager.createNotificationChannel(channel);

        notificationManager.notify(0, notificationBuilder.build());
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "onNewToken: " + token);
    }
}
