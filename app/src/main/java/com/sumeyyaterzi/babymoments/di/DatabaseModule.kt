package com.sumeyyaterzi.babymoments.di

import android.content.Context
import androidx.room.Room
import com.sumeyyaterzi.babymoments.data.AppDatabase
import com.sumeyyaterzi.babymoments.data.BabyProfileDao
import com.sumeyyaterzi.babymoments.data.MomentDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "baby_moments_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideMomentDao(db: AppDatabase): MomentDao = db.momentDao()

    @Provides
    @Singleton
    fun provideBabyProfileDao(db: AppDatabase): BabyProfileDao = db.babyProfileDao()
}