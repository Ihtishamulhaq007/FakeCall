package com.ihtisham.fakecall

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Re-shows the standing notification after a reboot, if it was left enabled. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED && Prefs.isEnabled(context)) {
            NotificationHelper.showPersistentNotification(context)
        }
    }
}
