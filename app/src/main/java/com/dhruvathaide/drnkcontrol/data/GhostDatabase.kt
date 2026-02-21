package com.dhruvathaide.drnkcontrol.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [GhostMessageEntity::class], version = 1, exportSchema = false)
abstract class GhostDatabase : RoomDatabase() {
    abstract fun ghostMessageDao(): GhostMessageDao
}
