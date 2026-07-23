package dev.papershelf.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.papershelf.data.repository.BookRepositoryImpl
import dev.papershelf.data.repository.BookmarkRepositoryImpl
import dev.papershelf.data.scanner.FileSystemLibraryScanner
import dev.papershelf.domain.repository.BookRepository
import dev.papershelf.domain.repository.BookmarkRepository
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
    abstract fun bindLibraryScanner(implementation: FileSystemLibraryScanner): LibraryScanner
}
