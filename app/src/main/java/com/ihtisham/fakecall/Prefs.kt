package com.ihtisham.fakecall

import android.content.Context

object Prefs {
    const val NAME = "fake_call_prefs"
    const val KEY_ENABLED = "persistent_enabled"

    fun isEnabled(context: Context): Boolean =
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE).getBoolean(KEY_ENABLED, false)

    fun setEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ENABLED, enabled).apply()
    }
}
