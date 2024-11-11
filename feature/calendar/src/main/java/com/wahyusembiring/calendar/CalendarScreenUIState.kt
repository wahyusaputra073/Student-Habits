package com.wahyusembiring.calendar

import com.wahyusembiring.ui.util.UIText

data class CalendarScreenUIState(
    val events: List<Any> = emptyList(),
    val popUps: List<CalendarScreenPopUp> = emptyList()
)

sealed class CalendarScreenUIEvent {
    data class OnEventCompletedStateChange(
        val event: Any,
        val isChecked: Boolean
    ) : CalendarScreenUIEvent()
    data class OnDeleteEvent(val event: Any) : CalendarScreenUIEvent()
    data class OnEventClick(val event: Any) : CalendarScreenUIEvent()
    data class OnDismissPopUp(val popUp: CalendarScreenPopUp) : CalendarScreenUIEvent()
}

sealed class CalendarScreenPopUp {
    data object Loading : CalendarScreenPopUp()
    data class Error(val message: UIText) : CalendarScreenPopUp()
}

sealed class CalendarScreenNavigationEvent {
    data class NavigateToHomeworkDetail(val homeworkId: String) : CalendarScreenNavigationEvent()
    data class NavigateToExamDetail(val examId: String) : CalendarScreenNavigationEvent()
    data class NavigateToReminderDetail(val reminderId: String) : CalendarScreenNavigationEvent()
}