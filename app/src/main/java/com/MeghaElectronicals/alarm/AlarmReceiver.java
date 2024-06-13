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

        String dateToBeSet = intent.getStringExtra("dateToBeSet");

        if (dateToBeSet != null && dateToBeSet.isBlank()) {

        }

        Data.Builder dataBuild = new Data.Builder();
        dataBuild.putString("task", intent.getStringExtra("task"));
        dataBuild.putString("desc", intent.getStringExtra("desc"));
        dataBuild.putString("TaskId", intent.getStringExtra("TaskId"));

        Log.d(TAG, "Starting Work Manager");
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class).setInputData(dataBuild.build()).build();
        WorkManager.getInstance(context).enqueue(workRequest);
    }
}
