package com.dhruvathaide.drnkcontrol.accessibility

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GhostService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "GhostService Connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        if (event.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            val packageName = event.packageName?.toString() ?: return
            
            // Checking for supported apps
            if (packageName == "com.whatsapp" || packageName == "com.instagram.android") {
                val nodeInfo = event.source ?: return
                
                // Identify if clicked node is a send button (simplified check, will need robust ID/Text checking)
                val description = nodeInfo.contentDescription?.toString()
                Log.d(TAG, "Clicked Node Description: $description in $packageName")
                
                // Example check for WhatsApp send button description (might need to explore View hierarchy)
                if (description != null && description.contains("Send", ignoreCase = true)) {
                    interceptMessage(nodeInfo)
                }
            }
        }
    }

    private fun interceptMessage(sendButtonNode: AccessibilityNodeInfo) {
        // Logic to find the EditText, capture text, clear it, and prevent sending
        // This is a complex step depending on the view hierarchy of each app.
        Log.d(TAG, "Intercepting message attempt...")
        Toast.makeText(this, "Sent to Buffer (Simulated)", Toast.LENGTH_SHORT).show()
    }

    override fun onInterrupt() {
        Log.e(TAG, "GhostService Interrupted")
    }

    companion object {
        private const val TAG = "GhostService"
    }
}
