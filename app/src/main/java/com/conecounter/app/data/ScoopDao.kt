package com.conecounter.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoopDao {
    @Query("SELECT * FROM scoops ORDER BY timestamp DESC")
    fun getAllScoops(): Flow<List<Scoop>>

    @Query("SELECT * FROM scoops WHERE kidId = :kidId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastScoopForKid(kidId: Long): Scoop?

    @Insert
    suspend fun insert(scoop: Scoop): Long

    @Delete
    suspend fun delete(scoop: Scoop)
}
