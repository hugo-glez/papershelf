package dev.papershelf.domain.progress

object ReadingProgressCalculator {
    fun bound(percentRead: Float): Float = percentRead.coerceIn(0f, 100f)

    fun fromPage(
        currentPageZeroBased: Int?,
        pageCount: Int,
    ): Float {
        if (currentPageZeroBased == null || pageCount <= 0) return 0f
        return bound(((currentPageZeroBased + 1).toFloat() / pageCount.toFloat()) * 100f)
    }
}
