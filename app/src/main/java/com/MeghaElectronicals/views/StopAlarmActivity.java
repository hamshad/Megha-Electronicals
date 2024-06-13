package com.MeghaElectronicals.views;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.MeghaElectronicals.alarm.MyMediaPlayer;
import com.MeghaElectronicals.databinding.ActivityStopAlarmBinding;

public class StopAlarmActivity extends AppCompatActivity {

    private static final String TAG = "StopAlarmActivity";
    ActivityStopAlarmBinding ui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = ActivityStopAlarmBinding.inflate(getLayoutInflater());
        setContentView(ui.getRoot());

        MyMediaPlayer.runWakeLock(this);

        String task = getIntent().getStringExtra("task");
        String desc = getIntent().getStringExtra("desc");

        setShowWhenLocked(true);
        setTurnScreenOn(true);
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        keyguardManager.requestDismissKeyguard(this, null);

        ui.taskStopAlarm.setText(task);
        ui.descStopAlarm.setText(desc);

        ui.stopAlarmBtn.setOnClickListener(v -> MyMediaPlayer.stopPlayer(getApplicationContext()));
    }
}