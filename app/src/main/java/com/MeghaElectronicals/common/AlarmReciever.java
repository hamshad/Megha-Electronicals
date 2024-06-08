package com.MeghaElectronicals;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

public class AlarmReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            // Play vibration
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(VibrationEffect.createOneShot(5000, VibrationEffect.DEFAULT_AMPLITUDE));

            // Play ringtone
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            Ringtone ringtone = RingtoneManager.getRingtone(context, notification);
            ringtone.play();

            // Stop ringtone after 10 seconds
            new Thread(() -> {
                try {
                    Thread.sleep(10000);
                    if (ringtone.isPlaying())
                        ringtone.stop();
                } catch (InterruptedException e) {
                    e.fillInStackTrace();
                }
            }).start();
        } catch (Exception e) {
            Log.d("AlarmReceiver", "onMessageReceived: " + e);
        }
    }
}
