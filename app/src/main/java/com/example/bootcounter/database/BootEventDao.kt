package com.example.bootcounter.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BootEventDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(bootEvent: BootEvent)

    @Query("SELECT * FROM boot_events")
    fun getAllBootEvents(): Flow<List<BootEvent>>
}