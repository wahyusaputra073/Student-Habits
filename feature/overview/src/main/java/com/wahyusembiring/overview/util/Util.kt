package com.wahyusembiring.overview.util

import com.wahyusembiring.data.model.ExamWithSubject
import com.wahyusembiring.data.model.HomeworkWithSubject
import com.wahyusembiring.data.model.entity.Reminder
import com.wahyusembiring.datetime.Moment
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.DurationUnit

internal fun List<Any>.filterByDate(range: OpenEndRange<LocalDateTime>): List<Any> {
    return filter { event ->
        when (event) {
            is HomeworkWithSubject -> {
                range.start in event.homework.dueDate..<event.homework.deadline
            }
            is ExamWithSubject -> {
                range.start in event.exam.dueDate..<event.exam.deadline
            }
            is Reminder -> {
                event.reminderDates.any { it in range }
            }
            else -> throw IllegalArgumentException("Invalid event type")
        }
    }.flatMap { event ->
        if (event is Reminder) {
            val filteredReminder = event.copy(reminderDates = event.reminderDates.filter { it in range })
            List(filteredReminder.reminderDates.size) {
                filteredReminder.copy(reminderDates = listOf(filteredReminder.reminderDates[it]))
            }
        } else {
            listOf(event)
        }
    }
}

internal fun List<Any>.sortByDate(isAscending: Boolean = true): List<Any> {
    return if (isAscending) {
        sortedBy { event ->
            when (event) {
                is HomeworkWithSubject -> event.homework.dueDate
                is ExamWithSubject -> event.exam.dueDate
                is Reminder -> event.reminderDates.first()
                else -> null
            }
        }
    } else {
        sortedByDescending {
            when (it) {
                is HomeworkWithSubject -> it.homework.dueDate
                is ExamWithSubject -> it.exam.dueDate
                is Reminder -> it.reminderDates.first()
                else -> null
            }
        }
    }
}

internal fun List<Any>.getEventInRange(range: OpenEndRange<LocalDateTime>): List<Any> {
    return filterByDate(range).sortByDate()
}

internal infix fun LocalDateTime.until(other: LocalDateTime): OpenEndRange<LocalDateTime> {
    val startDate = this.truncatedTo(ChronoUnit.DAYS)
    val endDate = other.truncatedTo(ChronoUnit.DAYS)
    return startDate..<endDate
}