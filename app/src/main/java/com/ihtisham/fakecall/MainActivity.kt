package com.ihtisham.fakecall

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var delayGroup: RadioGroup
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        NotificationHelper.createChannel(this)

        delayGroup = findViewById(R.id.delayGroup)
        statusText = findViewById(R.id.statusText)
        val scheduleButton = findViewById<Button>(R.id.scheduleButton)

        requestNotificationPermissionIfNeeded()

        scheduleButton.setOnClickListener {
            val delaySeconds = when (delayGroup.checkedRadioButtonId) {
                R.id.delay10s -> 10L
                R.id.delay1m -> 60L
                R.id.delay5m -> 5 * 60L
                R.id.delay15m -> 15 * 60L
                else -> 60L
            }
            scheduleFakeCall(delaySeconds)
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }
    }

    private fun scheduleFakeCall(delaySeconds: Long) {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or
            (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, flags)

        val triggerAt = System.currentTimeMillis() + delaySeconds * 1000

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                // Fall back to inexact alarm; still fires close to on time.
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
                Toast.makeText(
                    this,
                    "Exact alarms not permitted; using approximate timing. Enable 'Alarms & reminders' for precision.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
            }
            statusText.text = "Notification will arrive in $delaySeconds seconds."
        } catch (e: SecurityException) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
            statusText.text = "Scheduled (approximate timing)."
        }
    }
}
