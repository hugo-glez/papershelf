package dev.papershelf.database.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.papershelf.database.PaperShelfDatabase
import dev.papershelf.database.dao.BookDao
import dev.papershelf.database.dao.BookmarkDao
import dev.papershelf.database.dao.ReadingProgressDao
import dev.papershelf.database.dao.ReadingSessionDao
import dev.papershelf.database.dao.SettingsDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): PaperShelfDatabase =
        Room.databaseBuilder(
            context,
            PaperShelfDatabase::class.java,
            "papershelf.db",
        ).build()

    @Provides
    fun provideBookDao(database: PaperShelfDatabase): BookDao = database.bookDao()

    @Provides
    fun provideBookmarkDao(database: PaperShelfDatabase): BookmarkDao = database.bookmarkDao()

    @Provides
    fun provideReadingProgressDao(database: PaperShelfDatabase): ReadingProgressDao =
        database.readingProgressDao()

    @Provides
    fun provideReadingSessionDao(database: PaperShelfDatabase): ReadingSessionDao =
        database.readingSessionDao()

    @Provides
    fun provideSettingsDao(database: PaperShelfDatabase): SettingsDao = database.settingsDao()
}
