package com.MeghaElectronicals.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.MeghaElectronicals.R;
import com.MeghaElectronicals.modal.LoginModal;
import com.google.gson.Gson;

public class MySharedPreference {

    private final String appName;
    private final Gson gson;
    private final SharedPreferences pref;
    private final SharedPreferences.Editor edit;

    // Constructor
    public MySharedPreference(Context context) {
        appName = context.getResources().getString(R.string.app_name);
        gson = new Gson();
        pref = context.getSharedPreferences(appName, Context.MODE_PRIVATE);
        edit = pref.edit();
    }

    // FCM Token
    public void saveToken(String token) {
        edit.putString("Token", token);
        edit.apply();
    }

    public String fetchToken() {
        return pref.getString("Token", "");
    }

    public void deleteToken() {
        edit.remove("Token");
        edit.apply();
    }


    // Boarding Screen
    public void setShowBoardingScreen(boolean show) {
        edit.putBoolean("BoardingScreen", show);
        edit.apply();
    }

    public boolean showBoardingScreen() {
        return pref.getBoolean("BoardingScreen", true);
    }


    // Login Data
    public void saveLogin(LoginModal loginModal) {
        String login = gson.toJson(loginModal);
        edit.putString("Login", login);
        edit.apply();
    }

    public LoginModal fetchLogin() {
        String login = pref.getString("Login", "");
        return gson.fromJson(login, LoginModal.class);
    }

    public void deleteLogin() {
        edit.remove("Login");
        edit.apply();
    }

    // Notification Data
//    public void saveNotificationData(String title, String body) {
//        edit.putString("NotificationTitle", title);
//        edit.putString("NotificationBody", body);
//        edit.apply();
//    }
//
//    public String fetchNotificationTitle() {
//        return pref.getString("NotificationTitle", "Task Name");
//    }
//
//    public String fetchNotificationBody() {
//        return pref.getString("NotificationBody", "Task Description");
//    }
//
//    public void deleteNotificationData() {
//        edit.remove("NotificationTitle");
//        edit.remove("NotificationBody");
//        edit.apply();
//    }
}
