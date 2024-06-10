package com.MeghaElectronicals.common;

import android.annotation.SuppressLint;
import android.app.ActivityManager;

import androidx.lifecycle.MutableLiveData;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

public class MyFunctions {

    private static boolean dialogShowing = false;
    private static final MutableLiveData<Date> pickedDate = new MutableLiveData<>();

    public static String getValueFromJson(String rawData, String key) {
        try {
            JSONObject data = new JSONObject(rawData);
            return data.getString(key);
        } catch (JSONException e) {
            e.fillInStackTrace();
            return null;
        }
    }

    /**
     * Takes Date in yyyy-MM-dd form and converts it into the format and the formats are:<br>
     * ddmmyyyy,<br>
     * ddmmmyyyy
     *
     * @param yyyyMMdd
     * @param format
     * @return String
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDate(String yyyyMMdd, String format) {
        Date date = Calendar.getInstance().getTime();
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(yyyyMMdd);
        } catch (ParseException e) {
            e.fillInStackTrace();
        }
        if (format.equalsIgnoreCase("ddmmyyyy")) {
            return new SimpleDateFormat("dd-MM-yyyy").format(date);
        }
        if (format.equalsIgnoreCase("ddmmmyyyy")) {
            return new SimpleDateFormat("dd-MMM-yyyy").format(date);
        }
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public static String nullCheck(String str) {
        return Optional.ofNullable(str).map(s -> s.trim().isEmpty()).orElse(true) ? "-" : str;
    }

    public static String convertDate(String date) {

        // Define the original and target date formats
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MM-yyyy\nhh:mm a", Locale.getDefault());
//        SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MM-yyyy, hh:mm a", Locale.getDefault());

        try {
            // Parse the original date-time string to a Date object
            Date dateForFormatting = originalFormat.parse(date);

            // Format the Date object to the desired format

            return targetFormat.format(dateForFormatting);
        } catch (ParseException e) {
            e.fillInStackTrace();
            return date;
        }
    }

    public static boolean isInForeground () {
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);
        return (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND || appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE);
    }

}
