package com.ihtisham.fakecall

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        NotificationHelper.createChannel(this)

        statusText = findViewById(R.id.statusText)
        val enableButton = findViewById<Button>(R.id.enableButton)
        val disableButton = findViewById<Button>(R.id.disableButton)

        requestNotificationPermissionIfNeeded()
        refreshStatus()

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

    private fun refreshStatus() {
        statusText.text = if (Prefs.isEnabled(this)) {
            "Standing notification is ON. Pull down the shade and tap it to start a fake call."
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
