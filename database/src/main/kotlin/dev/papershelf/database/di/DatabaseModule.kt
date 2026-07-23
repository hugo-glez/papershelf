package dev.papershelf.database.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
import dev.papershelf.database.dao.TagDao
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
        )
            .addMigrations(MIGRATION_1_2)
            .build()

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

    @Provides
    fun provideTagDao(database: PaperShelfDatabase): TagDao = database.tagDao()

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `tags` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `name` TEXT NOT NULL,
                    `normalizedName` TEXT NOT NULL,
                    `createdEpochMillis` INTEGER NOT NULL
                )
                """.trimIndent(),
            )
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_tags_normalizedName` ON `tags` (`normalizedName`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_tags_name` ON `tags` (`name`)")
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `book_tags` (
                    `bookId` INTEGER NOT NULL,
                    `tagId` INTEGER NOT NULL,
                    PRIMARY KEY(`bookId`, `tagId`),
                    FOREIGN KEY(`bookId`) REFERENCES `books`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                    FOREIGN KEY(`tagId`) REFERENCES `tags`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent(),
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_book_tags_bookId` ON `book_tags` (`bookId`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_book_tags_tagId` ON `book_tags` (`tagId`)")
        }
    }
}
