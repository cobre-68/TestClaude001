package de.martinkupfer.zentrale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context c, Intent intent) {
        Intent s = new Intent(c, AlarmService.class);
        s.setAction(AlarmService.ACTION_RING);
        s.putExtra("label", intent.getStringExtra("label"));
        if (Build.VERSION.SDK_INT >= 26) c.startForegroundService(s);
        else c.startService(s);
    }
}
