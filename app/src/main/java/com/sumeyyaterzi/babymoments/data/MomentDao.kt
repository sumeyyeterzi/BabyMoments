package com.sumeyyaterzi.babymoments.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MomentDao {

    @Insert
    suspend fun insert(moment: MomentEntity)

    @Query("SELECT * FROM moments ORDER BY date DESC")
    fun getAll(): Flow<List<MomentEntity>>

    // YENİ: Update
    @Update
    suspend fun update(moment: MomentEntity)

    // YENİ: Delete
    @Delete
    suspend fun delete(moment: MomentEntity)

    // YENİ: ID'ye göre getir (opsiyonel, şu an gerekmiyor ama ileride lazım olabilir)
    @Query("SELECT * FROM moments WHERE id = :momentId LIMIT 1")
    suspend fun getById(momentId: Int): MomentEntity?
}