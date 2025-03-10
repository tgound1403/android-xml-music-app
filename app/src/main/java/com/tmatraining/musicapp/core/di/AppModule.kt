package com.tmatraining.musicapp.core.di

import android.content.Context
import androidx.room.Room
import com.tmatraining.musicapp.core.db.AppDatabase
import com.tmatraining.musicapp.core.db.SongDao
import com.tmatraining.musicapp.data.datasource.local.SongDataSource
import com.tmatraining.musicapp.data.datasource.local.SongLocalDataSource
import com.tmatraining.musicapp.data.repository.SongRepositoryImpl
import com.tmatraining.musicapp.domain.SongRepository
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
        return Room.databaseBuilder(context, AppDatabase::class.java, "app_database").fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideSongDao(database: AppDatabase): SongDao {
        return database.songDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideSongRepository(@ApplicationContext context: Context): SongRepository {
        return SongRepositoryImpl(
            SongDataSource(context), SongLocalDataSource(
                DatabaseModule.provideSongDao(
                    DatabaseModule.provideAppDatabase(context)
                )
            )
        )
    }
}