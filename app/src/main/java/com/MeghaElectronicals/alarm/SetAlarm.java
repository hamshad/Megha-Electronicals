package com.MeghaElectronicals.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SetAlarm {

    private static final String TAG = "SetAlarm";

    // seconds: 1000 * [desired second]
    // minutes: 1000 * 60 * [desired minute]
    // hours: 1000 * 60 * 60 * [desired hours]
    // days: 1000 * 60 * 60 * 24 * [desired days]
    // System.currentTimeMillis() + [time]

    /**
     *
     * @param context Context
     * @param task Task Name
     * @param desc Task Description
     * @param TaskId Task Id
     * @param dateToSet Date to Set <i>[ yyyy-MM-dd HH:mm:ss ]</i>
     */
    public void setAlarm(Context context, String task, String desc, int TaskId, String dateToSet, String dateToBeSet) throws ParseException {

        Intent intentAlarmReceiver = new Intent(context, AlarmReceiver.class);
        intentAlarmReceiver.putExtra("task", task);
        intentAlarmReceiver.putExtra("desc", desc);
        intentAlarmReceiver.putExtra("TaskId", TaskId);
        intentAlarmReceiver.putExtra("dateToBeSet", dateToBeSet);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, TaskId, intentAlarmReceiver, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Calculate the difference between the specified time and the current time
        long timeDifferenceMillis = getSpecifiedTimeMillis(dateToSet) - System.currentTimeMillis();

        manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeDifferenceMillis, pendingIntent);
        Log.d(TAG ,"timeDifferenceMillis: " + timeDifferenceMillis + " specifiedTimeMillis: " + getSpecifiedTimeMillis(dateToSet) + " currentTimeMillis: "+System.currentTimeMillis());
    }

    public void removeAlarm(Context context, String task, String desc, int TaskId) {

        Intent intentAlarmReceiver = new Intent(context, AlarmReceiver.class);
        intentAlarmReceiver.putExtra("task", task);
        intentAlarmReceiver.putExtra("desc", desc);
        intentAlarmReceiver.putExtra("TaskId", TaskId);
        intentAlarmReceiver.putExtra("dateToBeSet", "");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, TaskId, intentAlarmReceiver, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        manager.cancel(pendingIntent);
    }

    private long getSpecifiedTimeMillis(String dateToSet) throws ParseException {

        // Convert the specified date and time to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        Date date = sdf.parse(dateToSet);

        return date.getTime();
    }
}