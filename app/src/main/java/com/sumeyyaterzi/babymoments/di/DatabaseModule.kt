package com.sumeyyaterzi.babymoments.data.di

import android.content.Context
import androidx.room.Room
import com.sumeyyaterzi.babymoments.data.AppDatabase
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
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "baby_moments.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideMomentDao(database: AppDatabase): MomentDao {
        return database.momentDao()
    }
}