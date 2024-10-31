package com.wahyusembiring.calendar

sealed class CalendarScreenUIEvent {
    data class OnEventCompletedStateChange(
        val event: Any,
        val isChecked: Boolean
    ) : CalendarScreenUIEvent()
    data class OnDeleteEvent(val event: Any) : CalendarScreenUIEvent()
    data class OnEventClick(val event: Any) : CalendarScreenUIEvent()
}

sealed class CalendarScreenNavigationEvent {
    data class NavigateToHomeworkDetail(val homeworkId: Int) : CalendarScreenNavigationEvent()
    data class NavigateToExamDetail(val examkId: Int) : CalendarScreenNavigationEvent()
    data class NavigateToReminderDetail(val reminderId: Int) : CalendarScreenNavigationEvent()
}