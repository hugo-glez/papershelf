package dev.papershelf.domain.model

data class Bookmark(
    val id: Long,
    val bookId: Long,
    val page: Int?,
    val chapter: String?,
    val note: String?,
)
