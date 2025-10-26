package com.sumeyyaterzi.babymoments.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BabyProfileRepository @Inject constructor(
    private val dao: BabyProfileDao
) {

    fun getProfile(): Flow<BabyProfileEntity?> = dao.getProfile()

    suspend fun getProfileOnce(): BabyProfileEntity? = dao.getProfileOnce()

    suspend fun insertProfile(profile: BabyProfileEntity) {
        dao.insert(profile)
    }

    suspend fun updateProfile(profile: BabyProfileEntity) {
        dao.update(profile)
    }

    suspend fun deleteProfile(profile: BabyProfileEntity) {
        dao.delete(profile)
    }
}