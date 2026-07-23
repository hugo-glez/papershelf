package dev.papershelf.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.papershelf.database.dao.BookDao
import dev.papershelf.database.dao.BookmarkDao
import dev.papershelf.database.dao.ReadingProgressDao
import dev.papershelf.database.dao.ReadingSessionDao
import dev.papershelf.database.dao.SettingsDao
import dev.papershelf.database.dao.TagDao
import dev.papershelf.database.entity.BookTagEntity
import dev.papershelf.database.entity.BookEntity
import dev.papershelf.database.entity.BookmarkEntity
import dev.papershelf.database.entity.ReadingProgressEntity
import dev.papershelf.database.entity.ReadingSessionEntity
import dev.papershelf.database.entity.SettingsEntity
import dev.papershelf.database.entity.TagEntity

@Database(
    entities = [
        BookEntity::class,
        BookmarkEntity::class,
        ReadingProgressEntity::class,
        ReadingSessionEntity::class,
        SettingsEntity::class,
        TagEntity::class,
        BookTagEntity::class,
    ],
    version = 2,
    exportSchema = true,
)
@TypeConverters(PaperShelfTypeConverters::class)
abstract class PaperShelfDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun readingProgressDao(): ReadingProgressDao
    abstract fun readingSessionDao(): ReadingSessionDao
    abstract fun settingsDao(): SettingsDao
    abstract fun tagDao(): TagDao
}
