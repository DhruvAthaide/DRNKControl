package com.dhruvathaide.drnkcontrol.domain

import com.dhruvathaide.drnkcontrol.data.GhostMessageDao
import com.dhruvathaide.drnkcontrol.data.GhostMessageEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MessageRepository @Inject constructor(
    private val messageDao: GhostMessageDao
) {
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

    suspend fun updateMessageStatus(id: Long, status: String) {
        messageDao.updateMessageStatus(id, status)
    }

    suspend fun clearQueue() {
        messageDao.deleteAllMessages()
    }
}
