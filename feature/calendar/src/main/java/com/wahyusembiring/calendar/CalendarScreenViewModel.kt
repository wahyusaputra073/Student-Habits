package com.wahyusembiring.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wahyusembiring.data.model.ExamWithSubject
import com.wahyusembiring.data.model.HomeworkWithSubject
import com.wahyusembiring.data.model.entity.Reminder
import com.wahyusembiring.data.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalendarScreenViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CalendarScreenUIState())
    val state = _state.asStateFlow()

    private val _navigationEvent = Channel<CalendarScreenNavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    private val events = eventRepository.getAllEvent()

    init {
        viewModelScope.launch {
            events.collect {
                _state.update { uIstate ->
                    uIstate.copy(events = it)
                }
            }
        }
    }

    fun onUIEvent(event: CalendarScreenUIEvent) {
        when (event) {
            is CalendarScreenUIEvent.OnDeleteEvent -> onDeleteEvent(event.event)
            is CalendarScreenUIEvent.OnEventClick -> onEventClick(event.event)
            is CalendarScreenUIEvent.OnEventCompletedStateChange -> onEventCompletedStateChange(event.event, event.isChecked)
        }
    }

    private fun onDeleteEvent(event: Any) {
        viewModelScope.launch {
            when (event) {
                is HomeworkWithSubject -> {
                    eventRepository.deleteHomework(event.homework)
                }
                is ExamWithSubject -> {
                    eventRepository.deleteExam(event.exam)
                }
                is Reminder -> {
                    eventRepository.deleteReminder(event)
                }
            }
        }
    }

    private fun onEventCompletedStateChange(event: Any, checked: Boolean) {
        viewModelScope.launch {
            when (event) {
                is HomeworkWithSubject -> {
                    eventRepository.updateHomework(event.homework.copy(completed = checked))
                }
                is ExamWithSubject -> {
                    eventRepository.updateExam(event.exam.copy())
                }
                is Reminder -> {
                    eventRepository.updateReminder(event.copy(completed = checked))
                }
            }
        }
    }

    private fun onEventClick(event: Any) {
        when (event) {
            is HomeworkWithSubject -> {
                _navigationEvent.trySend(CalendarScreenNavigationEvent.NavigateToHomeworkDetail(event.homework.id))
            }
            is ExamWithSubject -> {
                _navigationEvent.trySend(CalendarScreenNavigationEvent.NavigateToExamDetail(event.exam.id))
            }
            is Reminder -> {
                _navigationEvent.trySend(CalendarScreenNavigationEvent.NavigateToReminderDetail(event.id))
            }
        }
    }

}