package com.conecounter.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface KidDao {
    @Query("SELECT * FROM kids ORDER BY sortOrder ASC")
    fun getAllKids(): Flow<List<Kid>>

    @Query("SELECT * FROM kids WHERE id = :kidId")
    suspend fun getKidById(kidId: Long): Kid?

    @Insert
    suspend fun insert(kid: Kid): Long

    @Update
    suspend fun update(kid: Kid)

    @Delete
    suspend fun delete(kid: Kid)
}
