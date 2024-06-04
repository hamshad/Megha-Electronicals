package com.example.buzzertest;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NotificationWorker extends Worker {

    Context context;

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        Log.d("Work Manager", "NotificationWorker: " + context.getString(R.string.app_name));
    }

    @NonNull
    @Override
    public Result doWork() {
//        Toast.makeText(context, "Work Manager Executed", Toast.LENGTH_SHORT).show();
        Log.d("Work Manager", "doWork: Work Manager Executed");
        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), ringtoneUri);
        ringtone.play();
        return Result.success();
    }
}
