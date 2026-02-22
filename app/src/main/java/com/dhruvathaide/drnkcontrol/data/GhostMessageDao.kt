package com.dhruvathaide.drnkcontrol.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GhostMessageDao {
    @Insert
    suspend fun insertMessage(message: GhostMessageEntity)

    @Query("SELECT * FROM ghost_messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<GhostMessageEntity>>

    @Query("SELECT * FROM ghost_messages WHERE status = 'PENDING' ORDER BY timestamp DESC")
    fun getPendingMessages(): Flow<List<GhostMessageEntity>>

    @Query("UPDATE ghost_messages SET status = :status WHERE id = :id")
    suspend fun updateMessageStatus(id: Long, status: String)

    @Query("DELETE FROM ghost_messages")
    suspend fun deleteAllMessages()

    @Query("SELECT * FROM app_settings WHERE id = 1")
    fun getSettings(): Flow<SettingsEntity?>

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun updateSettings(settings: SettingsEntity)
}
