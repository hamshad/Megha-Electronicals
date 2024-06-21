package com.MeghaElectronicals.alarm;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.widget.Toast;

import com.MeghaElectronicals.R;

public class MyMediaPlayer {

    private static MediaPlayer player = null;

    private static Ringtone ringtone = null;


    public static Runnable runWakeLock(Context context) {
        //WIFI LOCK
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiManager.WifiLock wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF , "MeghaElectronicals::WakeLock");
        wifiLock.acquire();

        //WAKE LOCK
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MeghaElectronicals::WakeLock");
        wakeLock.acquire(60*1000L /*1 minutes*/);

        return () -> {
            wifiLock.release();
            wakeLock.release();
        };
    }

    public static void startPlayer(Context context, Uri uri, boolean setOnLoop) {
        if (player == null) {
//            player = MediaPlayer.create(context, R.raw.alarm_clock_old);
            player = new MediaPlayer();
        }
        player.setAudioAttributes(new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build());
        player.setVolume(1.0f, 1.0f);
        player.setLooping(setOnLoop);
        player.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);

        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                player.reset();
                player.setDataSource(context, uri);
                player.setOnPreparedListener(mp -> player.start());
                player.prepareAsync();  // Use asynchronous prepare
                new Thread(() -> {
                    try {
                        Thread.sleep(60 * 1000);
                    } catch (InterruptedException e) {
                        e.fillInStackTrace();
                    }
                    stopPlayer();
                }).start();
            } catch (Exception e) {
                e.fillInStackTrace();
                Toast.makeText(context, "Couldn't play the audio!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public static void stopPlayer() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }

    public static void startRingtone(Context context) {
        if (ringtone == null) {
            Uri uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.alarm_clock_old);
            ringtone = RingtoneManager.getRingtone(context, uri);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) ringtone.setLooping(true);
        ringtone.play();
    }

    public static void stopRingtone(Context context) {
        if (ringtone != null) {
            ringtone.stop();
            ringtone = null;
        }
    }
}
