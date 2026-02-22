package com.dhruvathaide.drnkcontrol.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [GhostMessageEntity::class, SettingsEntity::class, BlockedAppEntity::class], version = 3, exportSchema = false)
abstract class GhostDatabase : RoomDatabase() {
    abstract fun ghostMessageDao(): GhostMessageDao
    abstract fun blockedAppDao(): BlockedAppDao
}
