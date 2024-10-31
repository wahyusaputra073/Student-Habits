package com.wahyusembiring.overview.util

import com.wahyusembiring.data.model.ExamWithSubject
import com.wahyusembiring.data.model.HomeworkWithSubject
import com.wahyusembiring.data.model.entity.Reminder
import com.wahyusembiring.datetime.Moment
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

infix fun List<Any>.inside(range: ClosedRange<Duration>): List<Any> {
    val today = Moment.now()
    val startDay = (today - 1.days + range.start).epochMilliseconds
    val endDay = (today - 1.days + range.endInclusive).epochMilliseconds

    return filter { event ->
        when (event) {
            is ExamWithSubject -> event.exam.date.time in startDay..endDay
            is HomeworkWithSubject -> event.homework.dueDate.time in startDay..endDay
            is Reminder -> event.date.time in startDay..endDay
            else -> throw IllegalArgumentException("Invalid event type")
        }
    }
}

infix fun Duration.until(duration: Duration): ClosedRange<Duration> = (this..duration)