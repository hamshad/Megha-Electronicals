package com.MeghaElectronicals.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.MeghaElectronicals.R;
import com.MeghaElectronicals.alarm.MyMediaPlayer;
import com.MeghaElectronicals.alarm.SetAlarm;
import com.MeghaElectronicals.common.MyFunctions;
import com.MeghaElectronicals.common.MySharedPreference;
import com.MeghaElectronicals.views.StopAlarmActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.ParseException;
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
        Log.d(TAG, "StartDate: " + data.get("StartDate"));
        Log.d(TAG, "EndDate: " + data.get("EndDate"));
        Log.d(TAG, "TaskId: " + data.get("TaskId"));
        Log.d(TAG, "Time: " + Calendar.getInstance().getTime());

        new MySharedPreference(this).saveNotificationData(data.get("title"), data.get("body"));

        try {
            new SetAlarm().setAlarm(this, data.get("title"), data.get("body"), data.get("StartDate"));
        } catch (ParseException e) {
            e.fillInStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

//            [WORK MANAGER]
//            Data.Builder dataBuild = new Data.Builder();
//            dataBuild.putString("title", data.get("title"));
//            dataBuild.putString("body", data.get("body"));

            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class).build();
            WorkManager.getInstance(this).enqueue(workRequest);
        } else {
            MyMediaPlayer.startPlayer(this);
            if (MyFunctions.isInForeground()) {
                startActivity(new Intent(this, StopAlarmActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
            sendNotification(data.get("title"), data.get("body"));
        }
    }

    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, StopAlarmActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE);

        String channelId = getString(R.string.channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

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
        channel.setImportance(NotificationManager.IMPORTANCE_HIGH);

        notificationManager.createNotificationChannel(channel);

        notificationManager.notify(0, notificationBuilder.build());
    }

    @Override
    public boolean handleIntentOnMainThread(Intent intent) {
        Log.d(TAG, "handleIntentOnMainThread: ");
        return super.handleIntentOnMainThread(intent);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "onNewToken: " + token);
    }
}
