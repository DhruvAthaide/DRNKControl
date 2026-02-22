package com.dhruvathaide.drnkcontrol.domain

import android.content.Context
import com.dhruvathaide.drnkcontrol.data.BlockedAppDao
import com.dhruvathaide.drnkcontrol.data.BlockedAppEntity
import com.dhruvathaide.drnkcontrol.data.GhostMessageDao
import com.dhruvathaide.drnkcontrol.data.GhostMessageEntity
import com.dhruvathaide.drnkcontrol.data.SettingsEntity
import com.dhruvathaide.drnkcontrol.utils.SafetyPrefs
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SafetyRepository - Formerly MessageRepository.
 * Manages safety settings, the blocked app list, and the legacy message queue.
 */
@Singleton
class SafetyRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val messageDao: GhostMessageDao,
    private val blockedAppDao: BlockedAppDao
) {
    // Legacy Message Queue Logic (Keeping for now in case user wants to review old logs)
    suspend fun addMessageToQueue(appSource: String, content: String) {
        val entity = GhostMessageEntity(
            timestamp = System.currentTimeMillis(),
            appSource = appSource,
            messageContent = content,
            status = "PENDING"
        )
        messageDao.insertMessage(entity)
    }

    fun getPendingMessages(): Flow<List<GhostMessageEntity>> = messageDao.getPendingMessages()
    fun getAllMessages(): Flow<List<GhostMessageEntity>> = messageDao.getAllMessages()
    suspend fun updateMessageStatus(id: Long, status: String) = messageDao.updateMessageStatus(id, status)
    suspend fun clearQueue() = messageDao.deleteAllMessages()

    // Safety Settings Logic
    fun getSettings(): Flow<SettingsEntity?> = messageDao.getSettings()

    suspend fun updateSafetyMode(active: Boolean) {
        messageDao.updateSettings(SettingsEntity(isSafetyModeActive = active))
        SafetyPrefs.setSafetyMode(context, active)
    }

    // App Lockdown Logic
    fun getAllBlockedApps(): Flow<List<BlockedAppEntity>> = blockedAppDao.getAllBlockedApps()

    fun getBlockedPackageNames(): Flow<List<String>> = blockedAppDao.getBlockedPackageNames()

    suspend fun toggleAppBlock(packageName: String, appName: String, isBlocked: Boolean) {
        if (isBlocked) {
            blockedAppDao.insertOrUpdate(BlockedAppEntity(packageName, appName, true))
        } else {
            blockedAppDao.removeApp(packageName)
        }
    }
}
