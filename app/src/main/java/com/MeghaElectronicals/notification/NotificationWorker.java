package com.MeghaElectronicals.notification;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.MeghaElectronicals.R;

public class NotificationWorker extends Worker {

    Context context;
    MediaPlayer player;

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        player = MediaPlayer.create(context, R.raw.alarm_clock_old);
        Log.d("Work Manager", "NotificationWorker: " + context.getString(R.string.app_name));
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("Work Manager", "doWork: Work Manager Executed");

        player.setVolume(1.0f, 1.0f);
        player.setLooping(true);
        player.start();

        return Result.success();
    }
}
