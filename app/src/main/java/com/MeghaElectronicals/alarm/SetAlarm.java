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



    // seconds: 1000 * [desired second]
    // minutes: 1000 * 60 * [desired minute]
    // hours: 1000 * 60 * 60 * [desired hours]
    // days: 1000 * 60 * 60 * 24 * [desired days]
    // System.currentTimeMillis() + [time]

    /**
     *
     * @param context - Context
     * @param task - Task Name
     * @param desc - Task Description
     * @param dateToSet - Date to Set [ yyyy-MM-dd HH:mm:ss ]
     */
    public void setAlarm(Context context, String task, String desc, String dateToSet) throws ParseException {
        Intent intentAlarmReceiver = new Intent(context, AlarmReceiver.class);
        intentAlarmReceiver.putExtra("task", task);
        intentAlarmReceiver.putExtra("desc", desc);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intentAlarmReceiver, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);


        // Calculate the difference between the specified time and the current time
        long timeDifferenceMillis = getSpecifiedTimeMillis(dateToSet) - System.currentTimeMillis();

        manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeDifferenceMillis, pendingIntent);
        Log.d("ALARM SET" ,"timeDifferenceMillis: " + timeDifferenceMillis + " specifiedTimeMillis: " + getSpecifiedTimeMillis(dateToSet) + " currentTimeMillis: "+System.currentTimeMillis());
    }

    private long getSpecifiedTimeMillis(String dateToSet) throws ParseException {
        // Convert the specified date and time to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        Date date = sdf.parse(dateToSet);

        return date.getTime();
    }
}
