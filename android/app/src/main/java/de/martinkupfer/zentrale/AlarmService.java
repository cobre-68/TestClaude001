package de.martinkupfer.zentrale;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import androidx.core.app.NotificationCompat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** Klingelt: Dauerton (wird lauter), Vibration, Vollbild über dem Sperrbildschirm. */
public class AlarmService extends Service {
    static final String ACTION_RING = "de.martinkupfer.zentrale.RING";
    static final String ACTION_STOP = "de.martinkupfer.zentrale.STOP";
    static final String ACTION_SNOOZE = "de.martinkupfer.zentrale.SNOOZE";
    static final String CHANNEL = "vollbild_wecker";
    static final int NOTIFICATION_ID = 7471;

    static volatile boolean isRinging = false;
    static String currentLabel = "Wecker";

    private MediaPlayer player;
    private Vibrator vibrator;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private float volume = 0.15f;

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent != null ? intent.getAction() : null;
        if (ACTION_STOP.equals(action)) { finishRing(true); return START_NOT_STICKY; }
        if (ACTION_SNOOZE.equals(action)) {
            AlarmScheduler.scheduleRaw(this, 4990, System.currentTimeMillis() + 5 * 60 * 1000L, currentLabel);
            finishRing(false);
            return START_NOT_STICKY;
        }
        String label = intent != null ? intent.getStringExtra("label") : null;
        currentLabel = label != null ? label : "Wecker";
        Notification n = buildNotification();
        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(NOTIFICATION_ID, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED);
        } else {
            startForeground(NOTIFICATION_ID, n);
        }
        if (!isRinging) {
            isRinging = true;
            startSound();
            startVibration();
            handler.postDelayed(() -> finishRing(false), 10 * 60 * 1000L); // nach 10 Min automatisch aus
        }
        try {
            Intent act = new Intent(this, AlarmActivity.class);
            act.putExtra("label", currentLabel);
            act.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
            startActivity(act);
        } catch (Exception ignored) {}
        return START_NOT_STICKY;
    }

    private Notification buildNotification() {
        NotificationManager nm = getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= 26 && nm != null) {
            NotificationChannel ch = new NotificationChannel(CHANNEL, "Vollbild-Wecker", NotificationManager.IMPORTANCE_HIGH);
            ch.setSound(null, null);
            ch.enableVibration(false);
            ch.setBypassDnd(true);
            ch.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            nm.createNotificationChannel(ch);
        }
        int fl = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        Intent full = new Intent(this, AlarmActivity.class);
        full.putExtra("label", currentLabel);
        full.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        PendingIntent fullPi = PendingIntent.getActivity(this, 1, full, fl);
        PendingIntent stopPi = PendingIntent.getService(this, 2, new Intent(this, AlarmService.class).setAction(ACTION_STOP), fl);
        PendingIntent snoozePi = PendingIntent.getService(this, 3, new Intent(this, AlarmService.class).setAction(ACTION_SNOOZE), fl);
        String time = new SimpleDateFormat("HH:mm", Locale.GERMANY).format(new Date());
        return new NotificationCompat.Builder(this, CHANNEL)
            .setSmallIcon(R.drawable.ic_stat_zentrale)
            .setContentTitle("⏰ " + currentLabel)
            .setContentText(time + " Uhr")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setFullScreenIntent(fullPi, true)
            .setContentIntent(fullPi)
            .addAction(0, "Schlummern", snoozePi)
            .addAction(0, "Aus", stopPi)
            .build();
    }

    private void startSound() {
        AudioAttributes attrs = new AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build();
        try {
            player = new MediaPlayer();
            player.setAudioAttributes(attrs);
            player.setDataSource(this, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.wecker));
            player.setLooping(true);
            player.prepare();
        } catch (Exception e) {
            try {
                player = new MediaPlayer();
                player.setAudioAttributes(attrs);
                player.setDataSource(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
                player.setLooping(true);
                player.prepare();
            } catch (Exception e2) { player = null; return; }
        }
        player.setVolume(volume, volume);
        player.start();
        handler.postDelayed(ramp, 3000);
    }

    private final Runnable ramp = new Runnable() {
        @Override public void run() {
            if (player == null) return;
            volume = Math.min(1f, volume + 0.05f);
            player.setVolume(volume, volume);
            if (volume < 1f) handler.postDelayed(this, 3000);
        }
    };

    private void startVibration() {
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator == null) return;
        long[] pattern = {0, 600, 400, 600, 1200};
        if (Build.VERSION.SDK_INT >= 26) {
            AudioAttributes a = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build();
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0), a);
        } else {
            vibrator.vibrate(pattern, 0);
        }
    }

    private void finishRing(boolean stoppedByUser) {
        isRinging = false;
        handler.removeCallbacksAndMessages(null);
        if (player != null) { try { player.stop(); player.release(); } catch (Exception ignored) {} player = null; }
        if (vibrator != null) vibrator.cancel();
        if (stoppedByUser) AlarmScheduler.prefs(this).edit().putLong("lastStop", System.currentTimeMillis()).apply();
        AlarmActivity.closeIfOpen();
        if (Build.VERSION.SDK_INT >= 24) stopForeground(STOP_FOREGROUND_REMOVE);
        else stopForeground(true);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        if (player != null) { try { player.release(); } catch (Exception ignored) {} player = null; }
        if (vibrator != null) vibrator.cancel();
        isRinging = false;
        super.onDestroy();
    }
}
