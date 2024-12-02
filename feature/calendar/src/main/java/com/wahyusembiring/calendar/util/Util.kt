package com.wahyusembiring.calendar.util

import com.wahyusembiring.data.model.ExamWithSubject
import com.wahyusembiring.data.model.HomeworkWithSubject
import com.wahyusembiring.data.model.entity.Exam
import com.wahyusembiring.data.model.entity.Homework
import com.wahyusembiring.data.model.entity.Reminder
import com.wahyusembiring.data.model.entity.Subject
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

fun List<Any>.getEventsByDate(date: LocalDate): List<Any> {
    return this.filter {
        val eventDate = when (it) {
            is ExamWithSubject -> it.exam.dueDate
            is HomeworkWithSubject -> it.homework.dueDate
            is Reminder -> LocalDateTime.MIN
            else -> throw IllegalArgumentException("Invalid event type")
        }
//        val eventLocalDate =
//            Instant.ofEpochMilli(eventDate.time).atZone(ZoneId.systemDefault()).toLocalDate()
        val eventLocalDate = eventDate
        eventLocalDate.toLocalDate() == date
    }
}