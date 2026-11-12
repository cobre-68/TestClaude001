package de.martinkupfer.zentrale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** Vollbild über dem Sperrbildschirm: Uhrzeit, Schlummern, Aus – oder Handy umdrehen. */
public class AlarmActivity extends Activity implements SensorEventListener {
    private static WeakReference<AlarmActivity> current = new WeakReference<>(null);
    private final Handler handler = new Handler(Looper.getMainLooper());
    private TextView clock;
    private SensorManager sensors;
    private boolean upSeen = false;
    private long downSince = 0;
    private boolean done = false;

    static void closeIfOpen() {
        AlarmActivity a = current.get();
        if (a != null) a.runOnUiThread(a::finish);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 27) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        current = new WeakReference<>(this);

        String label = getIntent().getStringExtra("label");
        if (label == null) label = AlarmService.currentLabel;

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setBackgroundColor(0xFF140F08);
        int pad = dp(32);
        root.setPadding(pad, pad, pad, pad);

        clock = new TextView(this);
        clock.setTextSize(TypedValue.COMPLEX_UNIT_SP, 88);
        clock.setTextColor(0xFFE8EAF0);
        clock.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        clock.setGravity(Gravity.CENTER);
        root.addView(clock);

        TextView lbl = new TextView(this);
        lbl.setText(label);
        lbl.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        lbl.setTextColor(0xFFFFB057);
        lbl.setGravity(Gravity.CENTER);
        lbl.setPadding(0, dp(4), 0, dp(56));
        root.addView(lbl);

        Button snooze = button("Schlummern · 5 Min", 0xFF20242D, 0xFFE8EAF0);
        snooze.setOnClickListener(v -> {
            done = true;
            startService(new Intent(this, AlarmService.class).setAction(AlarmService.ACTION_SNOOZE));
            finish();
        });
        root.addView(snooze);

        Button stop = button("Aus", 0xFFFFB057, 0xFF000000);
        stop.setOnClickListener(v -> stopAndOpenApp());
        root.addView(stop);

        TextView hint = new TextView(this);
        hint.setText("oder Handy umdrehen");
        hint.setTextColor(0xFF8A91A0);
        hint.setGravity(Gravity.CENTER);
        hint.setPadding(0, dp(18), 0, 0);
        root.addView(hint);

        setContentView(root);
        tick();
        sensors = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    private int dp(int v) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v, getResources().getDisplayMetrics());
    }

    private Button button(String text, int bg, int fg) {
        Button b = new Button(this);
        b.setText(text);
        b.setAllCaps(false);
        b.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        b.setTextColor(fg);
        GradientDrawable d = new GradientDrawable();
        d.setColor(bg);
        d.setCornerRadius(dp(40));
        b.setBackground(d);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(300), dp(60));
        lp.setMargins(0, dp(8), 0, dp(8));
        b.setLayoutParams(lp);
        return b;
    }

    private void tick() {
        clock.setText(new SimpleDateFormat("HH:mm", Locale.GERMANY).format(new Date()));
        handler.postDelayed(this::tick, 10000);
    }

    private void stopAndOpenApp() {
        if (done) return;
        done = true;
        startService(new Intent(this, AlarmService.class).setAction(AlarmService.ACTION_STOP));
        Intent app = new Intent(this, MainActivity.class);
        app.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(app);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensors != null) {
            Sensor acc = sensors.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (acc != null) sensors.registerListener(this, acc, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensors != null) sensors.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent e) {
        float z = e.values[2];
        if (z > 6) upSeen = true;                     // Display nach oben
        if (upSeen && z < -7) {                       // Display nach unten
            long now = System.currentTimeMillis();
            if (downSince == 0) downSince = now;
            else if (now - downSince > 500) stopAndOpenApp();
        } else {
            downSince = 0;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onBackPressed() {
        // Zurück-Taste beendet den Wecker nicht
    }
}
