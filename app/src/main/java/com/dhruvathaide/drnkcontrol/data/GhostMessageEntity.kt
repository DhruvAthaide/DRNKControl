package com.dhruvathaide.drnkcontrol.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ghost_messages")
data class GhostMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val appSource: String,
    val messageContent: String,
    val status: String // "PENDING", "SENT", "BURNED"
)
