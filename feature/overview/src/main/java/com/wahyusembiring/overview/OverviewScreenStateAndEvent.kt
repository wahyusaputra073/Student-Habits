package com.wahyusembiring.overview

import com.wahyusembiring.data.model.ExamWithSubject
import com.wahyusembiring.data.model.entity.Exam
import com.wahyusembiring.ui.component.eventcard.EventCard
import com.wahyusembiring.ui.component.scoredialog.ScoreDialog

data class OverviewScreenUIState(
    val eventCards: List<EventCard> = emptyList(),
    val popUps: List<OverviewScreenPopUp> = emptyList(),
)

sealed class OverviewScreenUIEvent {
    data object OnHomeworkFABClick : OverviewScreenUIEvent()
    data object OnExamFABClick : OverviewScreenUIEvent()
    data object OnReminderFABClick : OverviewScreenUIEvent()

    data class OnEventCompletedStateChange(val event: Any, val isCompleted: Boolean) :
        OverviewScreenUIEvent()

    data class OnDeleteEvent(val event: Any) : OverviewScreenUIEvent()
    data class OnExamScorePicked(val exam: ExamWithSubject, val score: Int) :
        OverviewScreenUIEvent()

    data class OnMarkExamAsUndone(val exam: ExamWithSubject) : OverviewScreenUIEvent()
    data class OnDismissPopUp(val popUp: OverviewScreenPopUp) : OverviewScreenUIEvent()
    data object OnHamburgerMenuClick : OverviewScreenUIEvent()
    data class OnEventClick(val event: Any) : OverviewScreenUIEvent()
}

sealed class OverviewScreenNavigationEvent {
    data class NavigateToHomework(val homeworkId: String? = null) : OverviewScreenNavigationEvent()
    data class NavigateToExam(val examId: String? = null) : OverviewScreenNavigationEvent()
    data class NavigateToReminder(val reminderId: String? = null) : OverviewScreenNavigationEvent()
}

sealed class OverviewScreenPopUp {
    data object Loading : OverviewScreenPopUp()
    data class Error(val errorMessage: String) : OverviewScreenPopUp()
    data class ExamScoreInputDialog(val exam: ExamWithSubject) : OverviewScreenPopUp()
}