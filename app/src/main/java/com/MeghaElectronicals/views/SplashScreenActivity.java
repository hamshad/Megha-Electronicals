package com.example.buzzertest.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.buzzertest.MySharedPreference;
import com.example.buzzertest.R;
import com.example.buzzertest.databinding.ActivitySplashScreenBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.Executors;

public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = "SplashScreenActivity";
    ActivitySplashScreenBinding ui;
    private MySharedPreference pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(ui.getRoot());
        FirebaseApp.initializeApp(this);

        pref = new MySharedPreference(this);

        Executors.newSingleThreadExecutor().execute(() -> FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                Log.d(TAG, "onComplete: "+task.getResult());
                pref.saveToken(task.getResult());
            }
        }));

        char[] app_name = getResources().getString(R.string.app_name).toCharArray();
        int DELAY_ANIMATION = 300;
        Handler handler = new Handler(Looper.getMainLooper());

        for (char c : app_name) {
            DELAY_ANIMATION += 100;
            handler.postDelayed(() -> ui.appNameSplash.append(String.valueOf(c)), DELAY_ANIMATION);
        }
        DELAY_ANIMATION += 300;
        handler.postDelayed(() -> {
            Intent intent;
            if (!pref.fetchLogin().isEmpty()) intent = new Intent(this, MainActivity.class);
            else intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, DELAY_ANIMATION);
    }
}