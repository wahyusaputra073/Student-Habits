package com.wahyusembiring.subject.screen.main

import com.wahyusembiring.data.model.entity.Exam
import com.wahyusembiring.data.model.entity.Homework
import com.wahyusembiring.data.model.entity.Subject

sealed class SubjectScreenUIEvent {
    data object OnHamburgerMenuClick : SubjectScreenUIEvent()
    data class OnExamClick(val exam: Exam) : SubjectScreenUIEvent()
    data class OnHomeworkClick(val homework: Homework) : SubjectScreenUIEvent()
    data object OnFloatingActionButtonClick : SubjectScreenUIEvent()
    data class OnSubjectClick(val subject: Subject) : SubjectScreenUIEvent()
}