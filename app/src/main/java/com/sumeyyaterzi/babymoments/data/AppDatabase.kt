package com.sumeyyaterzi.babymoments.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        MomentEntity::class,
        BabyProfileEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun momentDao(): MomentDao
    abstract fun babyProfileDao(): BabyProfileDao
}