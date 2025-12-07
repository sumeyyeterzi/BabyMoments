package com.sumeyyaterzi.babymoments.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BabyProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: BabyProfileEntity): Long

    @Update
    suspend fun update(profile: BabyProfileEntity)

    @Query("SELECT * FROM baby_profile LIMIT 1")
    fun getProfile(): Flow<BabyProfileEntity?>

    @Query("SELECT * FROM baby_profile LIMIT 1")
    suspend fun getProfileOnce(): BabyProfileEntity?

    @Delete
    suspend fun delete(profile: BabyProfileEntity)
}