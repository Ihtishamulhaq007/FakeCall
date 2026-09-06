package com.ihtisham.fakecall

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Triggered by tapping the ALWAYS-ON persistent notification.
 * Starts the N-second countdown; does NOT ring anything itself.
 */
class ArmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val delaySeconds = Prefs.getDelaySeconds(context)
        AlarmScheduler.scheduleCall(context, delaySeconds)
        NotificationHelper.showPersistentNotification(
            context,
            statusText = "Call incoming in ${delaySeconds}s\u2026"
        )
    }
}
