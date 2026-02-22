package com.dhruvathaide.drnkcontrol.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class SettingsEntity(
    @PrimaryKey val id: Int = 1,
    val isSafetyModeActive: Boolean = false
)
