package dev.papershelf.domain.model

data class Book(
    val id: Long,
    val title: String,
    val author: String?,
    val format: BookFormat,
    val path: String,
    val isAvailable: Boolean,
    val isFavorite: Boolean,
)
