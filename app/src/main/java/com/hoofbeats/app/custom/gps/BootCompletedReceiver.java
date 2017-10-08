package com.hoofbeats.app.custom.gps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Receiver for boot completed broadcast
 *
 */

public class BootCompletedReceiver extends BroadcastReceiver {

    /**
     * Broadcast received on system boot completed.
     * Starts background logging service
     *
     * @param context Context
     * @param intent Intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean autoStart = prefs.getBoolean("prefAutoStart", false);
        if (autoStart && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent i = new Intent(context, LoggerService.class);
            context.startService(i);
        }
    }
}
