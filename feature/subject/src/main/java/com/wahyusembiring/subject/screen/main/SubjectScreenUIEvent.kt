package com.wahyusembiring.subject.screen.main

import com.wahyusembiring.data.model.SubjectWithExamAndHomework
import com.wahyusembiring.data.model.entity.Exam
import com.wahyusembiring.data.model.entity.Homework
import com.wahyusembiring.data.model.entity.Subject

sealed class SubjectScreenUIEvent {
    data object OnHamburgerMenuClick : SubjectScreenUIEvent()
    data object OnFloatingActionButtonClick : SubjectScreenUIEvent()
    data class OnSubjectClick(val subject: Subject) : SubjectScreenUIEvent()
    data class OnDeleteSubjectClick(val subject: Subject) : SubjectScreenUIEvent()
    data class OnSubjectDeleteConfirmed(val subject: Subject): SubjectScreenUIEvent()
    data class OnDismissPopUp(val popUp: SubjectScreenPopUp): SubjectScreenUIEvent()
}

sealed class SubjectScreenNavigationEvent {
    data class NavigateToSubjectDetail(val subject: Subject) : SubjectScreenNavigationEvent()
    data object NavigateToCreateSubject : SubjectScreenNavigationEvent()
}