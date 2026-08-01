package com.deacx.widget.widget.greeting

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime
import java.time.LocalTime

class GreetingPeriodTest {

    @Test
    fun `current returns LATE_NIGHT at midnight`() {
        assertEquals(GreetingPeriod.LATE_NIGHT, GreetingPeriod.current(LocalTime.of(0, 0)))
    }

    @Test
    fun `current returns MORNING right at the 5am boundary`() {
        assertEquals(GreetingPeriod.MORNING, GreetingPeriod.current(LocalTime.of(5, 0)))
    }

    @Test
    fun `current returns AFTERNOON just before the evening boundary`() {
        assertEquals(GreetingPeriod.AFTERNOON, GreetingPeriod.current(LocalTime.of(16, 59)))
    }

    @Test
    fun `current returns NIGHT late in the evening`() {
        assertEquals(GreetingPeriod.NIGHT, GreetingPeriod.current(LocalTime.of(23, 0)))
    }

    @Test
    fun `nextBoundary from mid-afternoon lands on evening today`() {
        val from = LocalDateTime.of(2026, 7, 31, 14, 30)
        val expected = LocalDateTime.of(2026, 7, 31, 17, 0)
        assertEquals(expected, GreetingPeriod.nextBoundary(from))
    }

    @Test
    fun `nextBoundary after the last boundary rolls to late night tomorrow`() {
        val from = LocalDateTime.of(2026, 7, 31, 22, 0)
        val expected = LocalDateTime.of(2026, 8, 1, 0, 0)
        assertEquals(expected, GreetingPeriod.nextBoundary(from))
    }

    @Test
    fun `nextBoundary exactly at a boundary skips to the following one`() {
        val from = LocalDateTime.of(2026, 7, 31, 17, 0)
        val expected = LocalDateTime.of(2026, 7, 31, 21, 0)
        assertEquals(expected, GreetingPeriod.nextBoundary(from))
    }
}
