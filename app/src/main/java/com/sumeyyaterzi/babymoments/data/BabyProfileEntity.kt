package com.sumeyyaterzi.babymoments.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "baby_profile")
data class BabyProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,

    val name: String,
    val birthDate: Long,  // Milliseconds
    val gender: String,   // "MALE" veya "FEMALE"
    val photoUri: String? = null,

    val bloodType: String? = null,
    val notes: String? = null,

    val createdAt: Long = System.currentTimeMillis()
)