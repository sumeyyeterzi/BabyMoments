package com.sumeyyaterzi.babymoments.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MomentEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun momentDao(): MomentDao


}