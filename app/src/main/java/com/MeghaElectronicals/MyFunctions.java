package com.example.buzzertest;

import android.annotation.SuppressLint;

import androidx.lifecycle.MutableLiveData;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
}
