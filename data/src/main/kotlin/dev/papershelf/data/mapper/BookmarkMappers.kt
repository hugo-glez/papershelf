package dev.papershelf.data.mapper

import dev.papershelf.database.entity.BookmarkEntity
import dev.papershelf.domain.model.Bookmark

fun BookmarkEntity.toDomain(): Bookmark =
    Bookmark(
        id = id,
        bookId = bookId,
        page = page,
        chapter = chapter,
        note = note,
        createdEpochMillis = createdEpochMillis,
    )
