package com.MeghaElectronicals.alarm;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.PowerManager;

import com.MeghaElectronicals.R;

public class MyMediaPlayer {

    private static MediaPlayer player = null;

    private static PowerManager powerManager = null;
    private static PowerManager.WakeLock wakeLock = null;

    public static void runWakeLock(Context context) {
        if (powerManager == null || wakeLock == null) {
            powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MeghaElectronicals:WakeLock");
        }
        wakeLock.acquire(5*60*1000L /*5 minutes*/);
    }

    public static void startPlayer(Context context) {
        if (player == null) {
            player = MediaPlayer.create(context, R.raw.alarm_clock_old);
        }
        player.setAudioAttributes(new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                .build());
        player.setVolume(1.0f, 1.0f);
        player.setLooping(true);
        player.start();

        runWakeLock(context);
    }

    public static void stopPlayer(Context context) {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }
}
