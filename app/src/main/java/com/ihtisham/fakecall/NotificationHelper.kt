package com.ihtisham.fakecall

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

object NotificationHelper {

    const val CALL_CHANNEL_ID = "fake_call_channel"
    const val PERSISTENT_CHANNEL_ID = "persistent_channel"

    const val CALL_NOTIFICATION_ID = 1001
    const val PERSISTENT_NOTIFICATION_ID = 2001

    private const val DEFAULT_STATUS_PREFIX = "Tap to start a call in "

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java)

            if (manager.getNotificationChannel(CALL_CHANNEL_ID) == null) {
                val callChannel = NotificationChannel(
                    CALL_CHANNEL_ID,
                    "Fake Call Alerts",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "The incoming fake call itself"
                    setSound(null, null) // ringtone is played by FakeCallActivity, not the notification
                }
                manager.createNotificationChannel(callChannel)
            }

            if (manager.getNotificationChannel(PERSISTENT_CHANNEL_ID) == null) {
                val persistentChannel = NotificationChannel(
                    PERSISTENT_CHANNEL_ID,
                    "Escape Call (always on)",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Standing notification you tap to start the countdown"
                    setSound(null, null)
                }
                manager.createNotificationChannel(persistentChannel)
            }
        }
    }

    /** The always-on notification. Tapping it (via ArmReceiver) starts the N-second countdown. */
    fun showPersistentNotification(context: Context, statusText: String? = null) {
        createChannels(context)

        val delaySeconds = Prefs.getDelaySeconds(context)
        val text = statusText ?: "$DEFAULT_STATUS_PREFIX${delaySeconds}s"

        val tapIntent = Intent(context, ArmReceiver::class.java)
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or
            (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        val pendingIntent = PendingIntent.getBroadcast(context, 1, tapIntent, flags)

        val notification = NotificationCompat.Builder(context, PERSISTENT_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.sym_call_incoming)
            .setContentTitle("Escape Call armed")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setAutoCancel(false)
            .setContentIntent(pendingIntent)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(PERSISTENT_NOTIFICATION_ID, notification)
    }

    fun cancelPersistentNotification(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(PERSISTENT_NOTIFICATION_ID)
    }

    /** Fired by AlarmReceiver once the countdown elapses. This is the actual "incoming call". */
    fun showCallNotification(context: Context) {
        createChannels(context)

        val tapIntent = Intent(context, FakeCallActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or
            (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        val pendingIntent = PendingIntent.getActivity(context, 0, tapIntent, flags)

        val notification = NotificationCompat.Builder(context, CALL_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.sym_call_incoming)
            .setContentTitle("Incoming call")
            .setContentText("Tap to answer")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setContentIntent(pendingIntent)
            .setFullScreenIntent(pendingIntent, true)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(CALL_NOTIFICATION_ID, notification)

        // Reset the standing notification back to its default, ready state.
        if (Prefs.isEnabled(context)) {
            showPersistentNotification(context)
        }
    }
}
