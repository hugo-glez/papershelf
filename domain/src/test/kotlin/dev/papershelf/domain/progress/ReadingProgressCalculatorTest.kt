package dev.papershelf.domain.progress

import org.junit.Assert.assertEquals
import org.junit.Test

class ReadingProgressCalculatorTest {
    @Test
    fun `bound limits percent to reading range`() {
        assertEquals(0f, ReadingProgressCalculator.bound(-5f), 0.001f)
        assertEquals(42.5f, ReadingProgressCalculator.bound(42.5f), 0.001f)
        assertEquals(100f, ReadingProgressCalculator.bound(125f), 0.001f)
    }

    @Test
    fun `fromPage converts zero based page to percent`() {
        assertEquals(10f, ReadingProgressCalculator.fromPage(0, 10), 0.001f)
        assertEquals(50f, ReadingProgressCalculator.fromPage(4, 10), 0.001f)
        assertEquals(100f, ReadingProgressCalculator.fromPage(99, 100), 0.001f)
    }

    @Test
    fun `fromPage returns zero for unknown page or page count`() {
        assertEquals(0f, ReadingProgressCalculator.fromPage(null, 10), 0.001f)
        assertEquals(0f, ReadingProgressCalculator.fromPage(3, 0), 0.001f)
    }
}
