package com.MeghaElectronicals.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.MeghaElectronicals.notification.NotificationWorker;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        boolean keepSettingAlarm = intent.getBooleanExtra("keepSettingAlarm", true);

        if (keepSettingAlarm) {
            Log.d("SetAlarm", "keepSettingAlarm: "+keepSettingAlarm);
        }

        Data.Builder dataBuild = new Data.Builder();
        dataBuild.putString("task", intent.getStringExtra("task"));
        dataBuild.putString("desc", intent.getStringExtra("desc"));
        dataBuild.putString("TaskId", String.valueOf(intent.getIntExtra("TaskId", 0)));

        Log.d(TAG, "Starting Work Manager");
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class).setInputData(dataBuild.build()).build();
        WorkManager.getInstance(context).enqueue(workRequest);
    }
}
