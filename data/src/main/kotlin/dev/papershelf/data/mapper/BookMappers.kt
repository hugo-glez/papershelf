package dev.papershelf.data.mapper

import dev.papershelf.database.entity.BookEntity
import dev.papershelf.database.entity.BookFormatEntity
import dev.papershelf.domain.model.Book
import dev.papershelf.domain.model.BookFormat

fun BookEntity.toDomain(): Book =
    Book(
        id = id,
        title = title,
        author = author,
        format = format.toDomain(),
        path = path,
        fileName = fileName,
        fileSizeBytes = fileSizeBytes,
        pageCount = pageCount,
        thumbnailPath = thumbnailPath,
        dateAddedEpochMillis = dateAddedEpochMillis,
        lastReadEpochMillis = lastReadEpochMillis,
        progressPercent = progressPercent,
        isAvailable = isAvailable,
        isFavorite = isFavorite,
    )

fun BookFormatEntity.toDomain(): BookFormat =
    when (this) {
        BookFormatEntity.Pdf -> BookFormat.Pdf
        BookFormatEntity.Epub -> BookFormat.Epub
    }
