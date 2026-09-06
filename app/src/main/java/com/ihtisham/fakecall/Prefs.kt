package com.ihtisham.fakecall

import android.content.Context

object Prefs {
    private const val NAME = "fake_call_prefs"
    private const val KEY_DELAY = "delay_seconds"
    private const val KEY_ENABLED = "persistent_enabled"

    fun getDelaySeconds(context: Context): Long =
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE).getLong(KEY_DELAY, 60L)

    fun setDelaySeconds(context: Context, seconds: Long) {
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
            .edit().putLong(KEY_DELAY, seconds).apply()
    }

    fun isEnabled(context: Context): Boolean =
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE).getBoolean(KEY_ENABLED, false)

    fun setEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ENABLED, enabled).apply()
    }
}
