package com.dhruvathaide.drnkcontrol.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.room.Room
import com.dhruvathaide.drnkcontrol.data.GhostDatabase
import com.dhruvathaide.drnkcontrol.utils.SafetyPrefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.sqlcipher.database.SupportFactory

/**
 * GhostService - App Lockdown Sentinel.
 * 
 * Monitors window state changes to detect when a protected app is opened
 * while Safety Mode is active.
 */
class GhostService : AccessibilityService() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    
    // Cached set of blocked package names for instant lookups
    private var blockedPackages = setOf<String>()

    private val db by lazy {
        val passphrase = "Secure_DRNK_Placeholder_Passphrase".toByteArray()
        val factory = SupportFactory(passphrase)
        Room.databaseBuilder(applicationContext, GhostDatabase::class.java, "ghost_database.db")
            .openHelperFactory(factory)
            .fallbackToDestructiveMigration()
            .build()
    }

    override fun onServiceConnected() {
        val info = AccessibilityServiceInfo().apply {
            packageNames = null // Monitor all
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
            notificationTimeout = 100
        }
        serviceInfo = info
        
        // Load blocked apps into memory
        scope.launch {
            db.blockedAppDao().getBlockedPackageNames().collect { list ->
                blockedPackages = list.toSet()
                Log.d(TAG, "Loaded ${blockedPackages.size} blocked packages")
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        // 1. FAST CHECK: Is Safety Mode even ON?
        if (!SafetyPrefs.isSafetyModeActive(applicationContext)) return

        val packageName = event.packageName?.toString() ?: return
        
        // Don't block our own app or common system UI
        if (packageName == applicationContext.packageName || 
            packageName == "com.android.systemui" || 
            packageName == "com.android.settings") return

        // 2. CHECK: Is this app in the blocked list?
        if (blockedPackages.contains(packageName)) {
            Log.i(TAG, "🔒 Lockdown triggered for $packageName")
            launchLockOverlay(packageName)
        }
    }

    private fun launchLockOverlay(targetPackage: String) {
        // We launch the LockOverlayActivity as a full-screen barrier
        // Using FLAG_ACTIVITY_NEW_TASK is required when launching from a Service
        val intent = Intent().apply {
            setClassName(applicationContext.packageName, "com.dhruvathaide.drnkcontrol.ui.lockdown.LockOverlayActivity")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra("TARGET_PACKAGE", targetPackage)
        }
        
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch lock overlay", e)
        }
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    companion object {
        private const val TAG = "GhostService"
        fun isEnabled(context: Context): Boolean {
            val componentName = "${context.packageName}/.accessibility.GhostService"
            val enabledServices = android.provider.Settings.Secure.getString(
                context.contentResolver,
                android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: return false
            return enabledServices.split(":").any { it.equals(componentName, ignoreCase = true) }
        }
    }
}
