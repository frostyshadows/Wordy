package com.sherryyuan.wordy.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "wordy_database"
        ).build()
    }

    @Singleton
    @Provides
    fun provideDailyEntryDao(database: AppDatabase): DailyEntryDao {
        return database.dailyEntryDao()
    }

    @Singleton
    @Provides
    fun provideProjectDao(database: AppDatabase): ProjectDao {
        return database.projectDao()
    }

    @Singleton
    @Provides
    fun provideSelectedProjectDao(database: AppDatabase): SelectedProjectDao {
        return database.selectedProjectDao()
    }
}
