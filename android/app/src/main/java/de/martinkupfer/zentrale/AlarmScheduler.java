package de.martinkupfer.zentrale;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

/** Plant Wecker über AlarmManager.setAlarmClock (zuverlässigste Variante, Wecker-Symbol in der Statusleiste). */
public class AlarmScheduler {
    static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences("zalarm", Context.MODE_PRIVATE);
    }

    static PendingIntent operation(Context c, int id, String label) {
        Intent i = new Intent(c, AlarmReceiver.class);
        i.setAction("de.martinkupfer.zentrale.ALARM_" + id);
        i.putExtra("label", label);
        return PendingIntent.getBroadcast(c, id, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    static boolean canExact(Context c) {
        if (Build.VERSION.SDK_INT >= 31) {
            AlarmManager am = c.getSystemService(AlarmManager.class);
            return am != null && am.canScheduleExactAlarms();
        }
        return true;
    }

    /** Ohne Speichern (z. B. Schlummern). */
    static void scheduleRaw(Context c, int id, long at, String label) {
        AlarmManager am = c.getSystemService(AlarmManager.class);
        if (am == null) return;
        PendingIntent op = operation(c, id, label);
        Intent show = new Intent(c, MainActivity.class);
        PendingIntent showPi = PendingIntent.getActivity(c, id, show, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        if (canExact(c)) {
            am.setAlarmClock(new AlarmManager.AlarmClockInfo(at, showPi), op);
        } else {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, op);
        }
    }

    static void schedule(Context c, int id, long at, String label) {
        scheduleRaw(c, id, at, label);
        SharedPreferences p = prefs(c);
        String ids = p.getString("ids", "");
        p.edit()
            .putString("ids", ids.isEmpty() ? String.valueOf(id) : ids + "," + id)
            .putLong("at_" + id, at)
            .putString("label_" + id, label)
            .apply();
    }

    static void cancelAll(Context c) {
        AlarmManager am = c.getSystemService(AlarmManager.class);
        SharedPreferences p = prefs(c);
        String ids = p.getString("ids", "");
        SharedPreferences.Editor e = p.edit();
        if (!ids.isEmpty() && am != null) {
            for (String s : ids.split(",")) {
                try {
                    int id = Integer.parseInt(s);
                    am.cancel(operation(c, id, p.getString("label_" + id, "Wecker")));
                    e.remove("at_" + id).remove("label_" + id);
                } catch (NumberFormatException ignored) {}
            }
        }
        e.putString("ids", "").apply();
    }

    /** Nach einem Neustart des Handys: gespeicherte Wecker neu planen. */
    static void rescheduleStored(Context c) {
        SharedPreferences p = prefs(c);
        String ids = p.getString("ids", "");
        if (ids.isEmpty()) return;
        long now = System.currentTimeMillis();
        for (String s : ids.split(",")) {
            try {
                int id = Integer.parseInt(s);
                long at = p.getLong("at_" + id, 0);
                if (at > now) scheduleRaw(c, id, at, p.getString("label_" + id, "Wecker"));
            } catch (NumberFormatException ignored) {}
        }
    }
}
