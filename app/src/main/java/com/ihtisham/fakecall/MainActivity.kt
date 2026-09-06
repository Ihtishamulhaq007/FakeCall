package com.ihtisham.fakecall

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var delayGroup: RadioGroup
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        NotificationHelper.createChannels(this)

        delayGroup = findViewById(R.id.delayGroup)
        statusText = findViewById(R.id.statusText)
        val enableButton = findViewById<Button>(R.id.enableButton)
        val disableButton = findViewById<Button>(R.id.disableButton)

        requestNotificationPermissionIfNeeded()
        restoreSavedDelay()
        refreshStatus()

        delayGroup.setOnCheckedChangeListener { _, _ ->
            Prefs.setDelaySeconds(this, selectedDelaySeconds())
            // If the standing notification is already up, refresh its text to match.
            if (Prefs.isEnabled(this)) {
                NotificationHelper.showPersistentNotification(this)
            }
        }

        enableButton.setOnClickListener {
            Prefs.setEnabled(this, true)
            NotificationHelper.showPersistentNotification(this)
            refreshStatus()
        }

        disableButton.setOnClickListener {
            Prefs.setEnabled(this, false)
            NotificationHelper.cancelPersistentNotification(this)
            refreshStatus()
        }
    }

    private fun restoreSavedDelay() {
        val saved = Prefs.getDelaySeconds(this)
        val id = when (saved) {
            10L -> R.id.delay10s
            5 * 60L -> R.id.delay5m
            15 * 60L -> R.id.delay15m
            else -> R.id.delay1m
        }
        delayGroup.check(id)
    }

    private fun selectedDelaySeconds(): Long = when (delayGroup.checkedRadioButtonId) {
        R.id.delay10s -> 10L
        R.id.delay1m -> 60L
        R.id.delay5m -> 5 * 60L
        R.id.delay15m -> 15 * 60L
        else -> 60L
    }

    private fun refreshStatus() {
        statusText.text = if (Prefs.isEnabled(this)) {
            "Standing notification is ON. Pull down the shade and tap it when you need to leave."
        } else {
            "Standing notification is OFF."
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
}
