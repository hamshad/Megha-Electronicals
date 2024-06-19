package com.MeghaElectronicals.alarm;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.widget.Toast;

import com.MeghaElectronicals.R;
import com.MeghaElectronicals.common.MySharedPreference;

public class MyMediaPlayer {

    private static MediaPlayer player = null;

    private static Ringtone ringtone = null;

    private static PowerManager powerManager = null;
    private static PowerManager.WakeLock wakeLock = null;

    public static void runWakeLock(Context context) {
        if (powerManager == null || wakeLock == null) {
            powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MeghaElectronicals::WakeLock");
        }
        wakeLock.acquire(30 * 1000L /*30 Seconds*/);
    }

    public static void startPlayer(Context context) {
        if (player == null) {
//            player = MediaPlayer.create(context, R.raw.alarm_clock_old);
            player = new MediaPlayer();
        }
        boolean isDirector = new MySharedPreference(context).fetchLogin().Role().equalsIgnoreCase("Director");
        player.setAudioAttributes(new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build());
        player.setVolume(1.0f, 1.0f);
        player.setLooping(!isDirector);
        player.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);

        Uri uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + (isDirector ? R.raw.director_alarm : R.raw.alarm_clock_old));
        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                player.setDataSource(context, uri);
                player.setOnPreparedListener(mp -> player.start());
                player.prepareAsync();  // Use asynchronous prepare
            } catch (Exception e) {
                e.fillInStackTrace();
                Toast.makeText(context, "Couldn't play the audio!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public static void stopPlayer(Context context) {
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
