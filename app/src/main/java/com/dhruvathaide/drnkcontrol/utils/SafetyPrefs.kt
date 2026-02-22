package com.dhruvathaide.drnkcontrol.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * A lightweight SharedPreferences wrapper for fast, synchronous Safety Mode state reads.
 * Used by GhostService to avoid async DB reads which can fail or be stale.
 */
object SafetyPrefs {
    private const val PREFS_NAME = "drnk_safety_prefs"
    private const val KEY_SAFETY_MODE = "is_safety_mode_active"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isSafetyModeActive(context: Context): Boolean =
        prefs(context).getBoolean(KEY_SAFETY_MODE, false)

    fun setSafetyMode(context: Context, active: Boolean) {
        prefs(context).edit().putBoolean(KEY_SAFETY_MODE, active).apply()
    }
}
