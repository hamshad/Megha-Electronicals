package com.MeghaElectronicals.notification;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.MeghaElectronicals.R;
import com.MeghaElectronicals.views.StopAlarmActivity;

public class NotificationWorker extends Worker {

    Context context;
//    MediaPlayer player;

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
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

        Intent intent = new Intent(context, StopAlarmActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        return Result.success();
    }
}
