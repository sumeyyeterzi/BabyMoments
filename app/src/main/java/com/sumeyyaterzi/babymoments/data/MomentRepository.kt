package com.sumeyyaterzi.babymoments.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MomentRepository @Inject constructor(
    private val dao: MomentDao
) {
    fun getAll() = dao.getAll()
    suspend fun insert(moment: MomentEntity) = dao.insert(moment)
    suspend fun update(moment: MomentEntity) = dao.update(moment)
    suspend fun delete(moment: MomentEntity) = dao.delete(moment)
}