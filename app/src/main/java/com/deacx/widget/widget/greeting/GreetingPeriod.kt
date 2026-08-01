package com.deacx.widget.widget.greeting

import java.time.LocalDateTime
import java.time.LocalTime

/**
 * The five greeting buckets from the approved design. Boundaries are plain
 * constants for now — Phase 3 customization can expose them as settings
 * without touching the resolution logic below.
 */
enum class GreetingPeriod(val startHour: Int) {
    LATE_NIGHT(0),
    MORNING(5),
    AFTERNOON(12),
    EVENING(17),
    NIGHT(21);

    companion object {
        private val byStartHour = entries.sortedBy { it.startHour }

        /** Which bucket [now] falls into. */
        fun current(now: LocalTime = LocalTime.now()): GreetingPeriod =
            byStartHour.lastOrNull { now.hour >= it.startHour } ?: byStartHour.first()

        /** The next moment a bucket boundary is crossed after [from]. */
        fun nextBoundary(from: LocalDateTime): LocalDateTime {
            val todaysBoundaries = byStartHour.map { from.toLocalDate().atTime(it.startHour, 0) }
            return todaysBoundaries.firstOrNull { it.isAfter(from) }
                ?: from.toLocalDate().plusDays(1).atTime(byStartHour.first().startHour, 0)
        }
    }
}
