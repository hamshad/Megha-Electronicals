package com.MeghaElectronicals.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.MeghaElectronicals.R;
import com.MeghaElectronicals.alarm.AlarmReceiver;
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

        Intent intent = new Intent(context, StopAlarmActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        return Result.success();
    }


    private void setAlarm() {
        Intent intentAlarmReceiver = new Intent(context, AlarmReceiver.class);
        intentAlarmReceiver.putExtra("task", pref.fetchNotificationTitle());
        intentAlarmReceiver.putExtra("desc", pref.fetchNotificationBody());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intentAlarmReceiver, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // seconds: 1000 * [desired second]
        // minutes: 1000 * 60 * [desired min]
        // hours: 1000 * 60 * 60 * [desired hours]
        // days: 1000 * 60 * 60 * 24 * [desired days]
        manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000 * 60 * 20, pendingIntent);
        Toast.makeText(context, "ALARM SET", Toast.LENGTH_SHORT).show();
    }
}
