package com.sumeyyaterzi.babymoments.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MomentDao {
    @Query("SELECT * FROM moments ORDER BY date DESC")
    fun getAll(): Flow<List<MomentEntity>>

    @Insert
    suspend fun insert(moment: MomentEntity): Long

    @Update
    suspend fun update(moment: MomentEntity)

    @Delete
    suspend fun delete(moment: MomentEntity)
}
