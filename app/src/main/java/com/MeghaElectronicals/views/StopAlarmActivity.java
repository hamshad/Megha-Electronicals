package com.MeghaElectronicals.views;

import android.app.KeyguardManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.MeghaElectronicals.R;
import com.MeghaElectronicals.databinding.ActivityStopAlarmBinding;

public class StopAlarmActivity extends AppCompatActivity {


    private static final String TAG = "StopAlarmActivity";
    ActivityStopAlarmBinding ui;
    MediaPlayer player = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = ActivityStopAlarmBinding.inflate(getLayoutInflater());
        setContentView(ui.getRoot());

        Log.d(TAG, "onCreate");

        setShowWhenLocked(true);
        setTurnScreenOn(true);
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        keyguardManager.requestDismissKeyguard(this, null);

        player = MediaPlayer.create(this, R.raw.alarm_clock_old);

        player.setVolume(1.0f, 1.0f);
        player.setLooping(true);
        player.start();

        ui.stopAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player != null) {
                    player.stop();
                    player.release();
                    player = null;
                }
            }
        });
    }
}