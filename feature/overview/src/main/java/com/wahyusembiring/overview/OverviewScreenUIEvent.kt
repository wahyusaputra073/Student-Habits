package com.wahyusembiring.overview

import com.wahyusembiring.common.navigation.Screen
import com.wahyusembiring.data.model.ExamWithSubject
import com.wahyusembiring.ui.component.scoredialog.ScoreDialog


sealed class OverviewScreenUIEvent {
    data class OnEventCompletedStateChange(val event: Any, val isCompleted: Boolean) :
        OverviewScreenUIEvent()

    data class OnDeleteEvent(val event: Any) : OverviewScreenUIEvent()
    data class OnExamScorePicked(val exam: ExamWithSubject, val score: Int) :
        OverviewScreenUIEvent()

    data class OnMarkExamAsUndone(val exam: ExamWithSubject) : OverviewScreenUIEvent()
    data class OnExamScoreDialogStateChange(val scoreDialog: ScoreDialog?) : OverviewScreenUIEvent()
}