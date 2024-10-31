package com.wahyusembiring.subject.screen.main

import com.wahyusembiring.data.model.entity.Exam
import com.wahyusembiring.data.model.entity.Homework

sealed class SubjectScreenUIEvent {
    data object OnHamburgerMenuClick : SubjectScreenUIEvent()
    data class OnExamClick(val exam: Exam) : SubjectScreenUIEvent()
    data class OnHomeworkClick(val homework: Homework) : SubjectScreenUIEvent()
    data object OnFloatingActionButtonClick : SubjectScreenUIEvent()
}