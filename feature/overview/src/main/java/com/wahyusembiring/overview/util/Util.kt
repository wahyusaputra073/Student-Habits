package com.wahyusembiring.overview.util

import com.wahyusembiring.data.model.ExamWithSubject
import com.wahyusembiring.data.model.HomeworkWithSubject
import com.wahyusembiring.data.model.entity.Reminder
import com.wahyusembiring.datetime.Moment
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.DurationUnit

infix fun List<Any>.inside(range: ClosedRange<Duration>): List<Any> {
    val today = LocalDate.now().atStartOfDay()
    val startDay: LocalDateTime = today.plusDays(range.start.toLong(DurationUnit.DAYS))
    val endDay: LocalDateTime = today.plusDays(range.endInclusive.toLong(DurationUnit.DAYS))

    return filter { event ->
        when (event) {
            is ExamWithSubject -> event.exam.date.atStartOfDay() in (startDay..< endDay)
            is HomeworkWithSubject -> event.homework.dueDate.atStartOfDay() in startDay..< endDay
            is Reminder -> event.date.atStartOfDay() in startDay..< endDay
            else -> throw IllegalArgumentException("Invalid event type")
        }
    }
}

infix fun Duration.until(duration: Duration): ClosedRange<Duration> = (this..duration)