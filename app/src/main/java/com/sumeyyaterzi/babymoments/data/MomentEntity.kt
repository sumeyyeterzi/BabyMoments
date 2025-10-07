package com.sumeyyaterzi.babymoments.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "moments")
data class MomentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val note: String?,
    val date: Long,
    val weight: Float?,
    val height: Float?,
    val photoUri: String?,
    val category: String?
)
