package dev.papershelf.data.mapper

import dev.papershelf.database.entity.ReadingProgressEntity
import dev.papershelf.domain.model.ReadingProgress

fun ReadingProgressEntity.toDomain(): ReadingProgress =
    ReadingProgress(
        bookId = bookId,
        lastPage = lastPage,
        percentRead = percentRead,
        readingTimeMillis = readingTimeMillis,
    )
