package com.MeghaElectronicals;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.MeghaElectronicals.views.MainActivity;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        RxJavaPlugins.setErrorHandler(e -> {
            if (e!=null) Log.e("MyApplication", "[RX JAVA ERROR]: "+ e);
        });

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                handleUncaughtException(t, e);
            }
        });
    }

    private void handleUncaughtException(Thread t, Throwable e) {
        new Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show());

        Intent i = new Intent(MyApplication.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        Runtime.getRuntime().exit(0);

    }
}
