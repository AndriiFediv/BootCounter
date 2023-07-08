package com.example.bootcounter.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "boot_events")
data class BootEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timestamp: Long
)