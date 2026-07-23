package dev.papershelf.reader.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.papershelf.domain.repository.BookmarkRepository
import dev.papershelf.domain.repository.ReadingProgressRepository

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ReaderEntryPoint {
    fun bookmarkRepository(): BookmarkRepository
    fun readingProgressRepository(): ReadingProgressRepository
}
