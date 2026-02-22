package com.dhruvathaide.drnkcontrol.accessibility

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import com.dhruvathaide.drnkcontrol.domain.MessageRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GhostService : AccessibilityService() {

    @Inject
    lateinit var repository: MessageRepository

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "GhostService Connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        if (event.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            val packageName = event.packageName?.toString() ?: return
            
            if (packageName == "com.whatsapp" || packageName == "com.instagram.android") {
                val nodeInfo = event.source ?: return
                
                val description = nodeInfo.contentDescription?.toString()
                
                if (description != null && (description.contains("Send", ignoreCase = true) || description.contains("Post", ignoreCase = true))) {
                    checkAndIntercept(nodeInfo, packageName)
                }
            }
        }
    }

    private fun checkAndIntercept(sendButtonNode: AccessibilityNodeInfo, packageName: String) {
        serviceScope.launch {
            val settings = repository.getSettings().firstOrNull()
            if (settings?.isSafetyModeActive == true) {
                findAndInterceptMessage(rootInActiveWindow, packageName)
            }
        }
    }

    private fun findAndInterceptMessage(root: AccessibilityNodeInfo?, packageName: String) {
        if (root == null) return
        val deque = ArrayDeque<AccessibilityNodeInfo>()
        deque.add(root)

        while (deque.isNotEmpty()) {
            val node = deque.removeFirst()
            if (node.className?.contains("EditText", ignoreCase = true) == true) {
                val capturedText = node.text?.toString()
                if (!capturedText.isNullOrBlank()) {
                    saveAndClear(node, capturedText, packageName)
                    return 
                }
            }
            for (i in 0 until node.childCount) {
                node.getChild(i)?.let { deque.add(it) }
            }
        }
    }

    private fun saveAndClear(editTextNode: AccessibilityNodeInfo, text: String, packageName: String) {
        serviceScope.launch {
            repository.addMessageToQueue(packageName, text)
            val arguments = android.os.Bundle()
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "")
            editTextNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
            launch(Dispatchers.Main) {
                Toast.makeText(this@GhostService, "DRNK Control: Message Safely Buffered", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onInterrupt() {
        Log.e(TAG, "GhostService Interrupted")
    }

    companion object {
        private const val TAG = "GhostService"
    }
}
