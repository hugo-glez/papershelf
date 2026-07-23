package dev.papershelf.data.mapper

import dev.papershelf.database.entity.TagEntity
import dev.papershelf.database.relation.BookTagProjection
import dev.papershelf.domain.model.BookTag

fun TagEntity.toDomain(): BookTag =
    BookTag(
        id = id,
        name = name,
    )

fun BookTagProjection.toDomain(): BookTag =
    BookTag(
        id = tagId,
        name = name,
    )
