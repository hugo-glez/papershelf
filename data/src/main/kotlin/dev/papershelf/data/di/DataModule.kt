package dev.papershelf.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.papershelf.data.repository.BookRepositoryImpl
import dev.papershelf.data.repository.BookmarkRepositoryImpl
import dev.papershelf.data.repository.LibraryMaintenanceRepositoryImpl
import dev.papershelf.data.repository.ReadingProgressRepositoryImpl
import dev.papershelf.data.repository.SettingsRepositoryImpl
import dev.papershelf.data.repository.TagRepositoryImpl
import dev.papershelf.data.scanner.FileSystemLibraryScanner
import dev.papershelf.data.scanner.BookThumbnailGenerator
import dev.papershelf.data.scanner.ThumbnailGenerator
import dev.papershelf.domain.repository.BookRepository
import dev.papershelf.domain.repository.BookmarkRepository
import dev.papershelf.domain.repository.LibraryMaintenanceRepository
import dev.papershelf.domain.repository.ReadingProgressRepository
import dev.papershelf.domain.repository.SettingsRepository
import dev.papershelf.domain.repository.TagRepository
import dev.papershelf.domain.scanner.LibraryScanner
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindBookRepository(implementation: BookRepositoryImpl): BookRepository

    @Binds
    @Singleton
    abstract fun bindBookmarkRepository(implementation: BookmarkRepositoryImpl): BookmarkRepository

    @Binds
    @Singleton
    abstract fun bindReadingProgressRepository(
        implementation: ReadingProgressRepositoryImpl,
    ): ReadingProgressRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(implementation: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindTagRepository(implementation: TagRepositoryImpl): TagRepository

    @Binds
    @Singleton
    abstract fun bindLibraryMaintenanceRepository(
        implementation: LibraryMaintenanceRepositoryImpl,
    ): LibraryMaintenanceRepository

    @Binds
    @Singleton
    abstract fun bindLibraryScanner(implementation: FileSystemLibraryScanner): LibraryScanner

    @Binds
    @Singleton
    abstract fun bindThumbnailGenerator(implementation: BookThumbnailGenerator): ThumbnailGenerator
}
