package com.MeghaElectronicals.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.MeghaElectronicals.notification.NotificationWorker;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "[onReceive] Task: "+intent.getStringExtra("task") + " Description: "+intent.getStringExtra("desc"));

//        Intent stopAlarmIntent = new Intent(context, StopAlarmActivity.class);
//        stopAlarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        context.startActivity(stopAlarmIntent);


        if (!Settings.canDrawOverlays(context)) Log.d(TAG, "onReceive: No Overlays Option");

        Data.Builder dataBuild = new Data.Builder();
        dataBuild.putString("title", intent.getStringExtra("task"));

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class).setInputData(dataBuild.build()).build();
        WorkManager.getInstance(context).enqueue(workRequest);

        Log.d(TAG, "[onReceive] - COMPLETED");
    }
}
