package dev.papershelf.database.relation

data class BookTagProjection(
    val bookId: Long,
    val tagId: Long,
    val name: String,
)
