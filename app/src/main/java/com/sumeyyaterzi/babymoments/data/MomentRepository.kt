package com.sumeyyaterzi.babymoments.data

class MomentRepository(private val dao: MomentDao) {
    fun getAll() = dao.getAll()
    suspend fun insert(moment: MomentEntity) = dao.insert(moment)
    suspend fun update(moment: MomentEntity) = dao.update(moment)
    suspend fun delete(moment: MomentEntity) = dao.delete(moment)
}
