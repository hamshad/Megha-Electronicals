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
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.MeghaElectronicals.R;
import com.MeghaElectronicals.alarm.MyMediaPlayer;
import com.MeghaElectronicals.common.MySharedPreference;
import com.MeghaElectronicals.views.StopAlarmActivity;

public class NotificationWorker extends Worker {

    Context context;
    MySharedPreference pref;
//    MediaPlayer player;

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        pref = new MySharedPreference(context);
//        player = MediaPlayer.create(context, R.raw.alarm_clock_old);
        Log.d("Work Manager", "NotificationWorker: " + context.getString(R.string.app_name));
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("Work Manager", "doWork: Work Manager Executed");

//        player.setVolume(1.0f, 1.0f);
//        player.setLooping(true);
//        player.start();

        MyMediaPlayer.startPlayer(context);

        sendNotification(pref.fetchNotificationTitle(), pref.fetchNotificationBody());

        Intent intent = new Intent(context, StopAlarmActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setAction(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        context.startActivity(intent);

        return Result.success();
    }

    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(context, StopAlarmActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE);

        String channelId = context.getString(R.string.channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId)
                .setColor(ContextCompat.getColor(context, R.color.primary))
                .setSmallIcon(R.drawable.ic_bell)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVibrate(new long[]{0, 500, 1000})
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel
        NotificationChannel channel = new NotificationChannel(
                channelId,
                context.getString(R.string.app_name),
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
}
