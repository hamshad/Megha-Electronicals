package com.example.buzzertest;

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
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.buzzertest.views.MainActivity;
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
        Log.d(TAG, "Title Foreground: " + message.getNotification().getTitle());
        Log.d(TAG, "Body Foreground: " + message.getNotification().getBody());
        Log.d(TAG, "Time: " + Calendar.getInstance().getTime());

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class).build();
        WorkManager.getInstance(this).enqueue(workRequest);

//        try {
//            // Play vibration
//            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//            vibrator.vibrate(VibrationEffect.createOneShot(5000, VibrationEffect.DEFAULT_AMPLITUDE));
//
//            // Play ringtone
//            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
//            Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
//            ringtone.play();
//
//            // Stop ringtone after 10 seconds
//            new Thread(() -> {
//                try {
//                    Thread.sleep(10000);
//                    if (ringtone.isPlaying())
//                        ringtone.stop();
//                } catch (InterruptedException e) {
//                    e.fillInStackTrace();
//                }
//            }).start();
//        } catch (Exception e) {
//            Log.d(TAG, "onMessageReceived: " + e);
//        }

//        if (message.getNotification() == null)
            sendNotification(data.get("title"), data.get("body"));
//        else
//            sendNotification(message.getNotification().getTitle(), message.getNotification().getBody());
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
        channel.setSound(customSoundUri, new AudioAttributes.Builder()
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

    public NotificationService() {
        super();
        Log.d(TAG, "NotificationService: function");
    }
}
