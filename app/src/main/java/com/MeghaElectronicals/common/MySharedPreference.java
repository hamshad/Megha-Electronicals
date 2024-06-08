package com.MeghaElectronicals.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.MeghaElectronicals.R;

public class MySharedPreference {

    private Context context;
    private final String appName;
    private final SharedPreferences pref;
    private final SharedPreferences.Editor edit;

    // Constructor
    public MySharedPreference(Context context) {
        this.context = context;
        appName = context.getResources().getString(R.string.app_name);
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

    // Login Data
    public void saveLogin(String login) {
        edit.putString("Login", login);
        edit.apply();
    }

    public String fetchLogin() {
        return pref.getString("Login", "");
    }

    public void deleteLogin() {
        edit.remove("Login");
        edit.apply();
    }
}
