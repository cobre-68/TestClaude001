package de.martinkupfer.zentrale;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import org.json.JSONException;
import org.json.JSONObject;

/** Brücke zur Web-App: Wecker planen, Status, Vollbild-Berechtigung. */
@CapacitorPlugin(name = "ZAlarm")
public class AlarmPlugin extends Plugin {

    @PluginMethod
    public void setAlarms(PluginCall call) {
        JSArray list = call.getArray("alarms", new JSArray());
        Context ctx = getContext();
        AlarmScheduler.cancelAll(ctx);
        int n = 0;
        try {
            for (int i = 0; i < list.length() && i < 30; i++) {
                JSONObject o = list.getJSONObject(i);
                AlarmScheduler.schedule(ctx, 5000 + i, o.getLong("at"), o.optString("label", "Wecker"));
                n++;
            }
        } catch (JSONException e) {
            call.reject("Ungültige Wecker-Daten");
            return;
        }
        JSObject r = status(ctx);
        r.put("scheduled", n);
        call.resolve(r);
    }

    @PluginMethod
    public void getStatus(PluginCall call) {
        call.resolve(status(getContext()));
    }

    @PluginMethod
    public void consumeStop(PluginCall call) {
        long t = AlarmScheduler.prefs(getContext()).getLong("lastStop", 0);
        AlarmScheduler.prefs(getContext()).edit().remove("lastStop").apply();
        JSObject r = new JSObject();
        r.put("stoppedAt", t);
        call.resolve(r);
    }

    @PluginMethod
    public void testRing(PluginCall call) {
        AlarmScheduler.scheduleRaw(getContext(), 4999, System.currentTimeMillis() + 10000, call.getString("label", "Test-Wecker"));
        call.resolve();
    }

    @PluginMethod
    public void openFullScreenSettings(PluginCall call) {
        try {
            Intent i;
            if (Build.VERSION.SDK_INT >= 34) {
                i = new Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT, Uri.parse("package:" + getContext().getPackageName()));
            } else if (Build.VERSION.SDK_INT >= 31) {
                i = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:" + getContext().getPackageName()));
            } else {
                call.resolve();
                return;
            }
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(i);
        } catch (Exception ignored) {}
        call.resolve();
    }

    private JSObject status(Context ctx) {
        JSObject r = new JSObject();
        r.put("canExact", AlarmScheduler.canExact(ctx));
        boolean full = true;
        if (Build.VERSION.SDK_INT >= 34) {
            NotificationManager nm = ctx.getSystemService(NotificationManager.class);
            full = nm != null && nm.canUseFullScreenIntent();
        }
        r.put("canFullScreen", full);
        r.put("ringing", AlarmService.isRinging);
        return r;
    }
}
