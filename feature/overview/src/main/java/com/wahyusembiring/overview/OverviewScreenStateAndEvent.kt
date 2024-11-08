package com.wahyusembiring.overview

import com.wahyusembiring.data.model.ExamWithSubject
import com.wahyusembiring.ui.component.eventcard.EventCard
import com.wahyusembiring.ui.component.scoredialog.ScoreDialog

data class OverviewScreenUIState(
    val eventCards: List<EventCard> = emptyList(),
    val popUps: List<OverviewScreenPopUp> = emptyList(),
)

sealed class OverviewScreenUIEvent {
    data class OnEventCompletedStateChange(val event: Any, val isCompleted: Boolean) :
        OverviewScreenUIEvent()

    data class OnDeleteEvent(val event: Any) : OverviewScreenUIEvent()
    data class OnExamScorePicked(val exam: ExamWithSubject, val score: Int) :
        OverviewScreenUIEvent()

    data class OnMarkExamAsUndone(val exam: ExamWithSubject) : OverviewScreenUIEvent()
    data class OnDismissPopUp(val popUp: OverviewScreenPopUp) : OverviewScreenUIEvent()
}

sealed class OverviewScreenPopUp {
    data object Loading : OverviewScreenPopUp()
    data object ScoreInputDialog : OverviewScreenPopUp()
}