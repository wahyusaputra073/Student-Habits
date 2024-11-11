package com.wahyusembiring.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wahyusembiring.data.Result
import com.wahyusembiring.data.model.ExamWithSubject
import com.wahyusembiring.data.model.HomeworkWithSubject
import com.wahyusembiring.data.model.entity.Reminder
import com.wahyusembiring.data.repository.EventRepository
import com.wahyusembiring.ui.util.UIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
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

    init {
        viewModelScope.launch {
            eventRepository.getAllEvent().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _state.update {
                            it.copy(
                                popUps = it.popUps + CalendarScreenPopUp.Loading
                            )
                        }
                    }
                    is Result.Error -> {
                        _state.update {
                            it.copy(
                                popUps = it.popUps
                                    .minus(CalendarScreenPopUp.Loading)
                                    .plus(CalendarScreenPopUp.Error(UIText.DynamicString(result.throwable.message ?: "Unknown Error")))
                            )
                        }
                    }
                    is Result.Success -> {
                        _state.update { it.copy(popUps = it.popUps - CalendarScreenPopUp.Loading) }
                        result.data.collect { events ->
                            _state.update { it.copy(events = events) }
                        }
                    }
                }
            }
        }
    }

    fun onUIEvent(event: CalendarScreenUIEvent) {
        when (event) {
            is CalendarScreenUIEvent.OnDeleteEvent -> onDeleteEvent(event.event)
            is CalendarScreenUIEvent.OnEventClick -> onEventClick(event.event)
            is CalendarScreenUIEvent.OnEventCompletedStateChange -> onEventCompletedStateChange(event.event, event.isChecked)
            is CalendarScreenUIEvent.OnDismissPopUp -> onDismissPopUp(event.popUp)
        }
    }

    private fun onDismissPopUp(popUp: CalendarScreenPopUp) {
        _state.update { it.copy(popUps = it.popUps.minus(popUp)) }
    }

    private fun onDeleteEvent(event: Any) {
        viewModelScope.launch {
            when (event) {
                is HomeworkWithSubject -> {
                    eventRepository.deleteHomework(event.homework).collect { result ->
                        when (result) {
                            is Result.Loading -> {}
                            is Result.Error -> { throw result.throwable }
                            is Result.Success -> {}
                        }
                    }
                }
                is ExamWithSubject -> {
                    eventRepository.deleteExam(event.exam).collect { result ->
                        when (result) {
                            is Result.Loading -> {}
                            is Result.Error -> { throw result.throwable }
                            is Result.Success -> {}
                        }
                    }
                }
                is Reminder -> {
                    eventRepository.deleteReminder(event).collect { result ->
                        when (result) {
                            is Result.Loading -> {}
                            is Result.Error -> { throw result.throwable }
                            is Result.Success -> {}
                        }
                    }
                }
            }
        }
    }

    private fun onEventCompletedStateChange(event: Any, checked: Boolean) {
        viewModelScope.launch {
            when (event) {
                is HomeworkWithSubject -> {
                    eventRepository.updateHomework(event.homework.copy(completed = checked)).collect { result ->
                        when (result) {
                            is Result.Loading -> {}
                            is Result.Error -> { throw result.throwable }
                            is Result.Success -> {}
                        }
                    }
                }
                is ExamWithSubject -> {
                    eventRepository.updateExam(event.exam.copy()).collect { result ->
                        when (result) {
                            is Result.Loading -> {}
                            is Result.Error -> { throw result.throwable }
                            is Result.Success -> {}
                        }
                    }
                }
                is Reminder -> {
                    eventRepository.updateReminder(event.copy(completed = checked)).collect { result ->
                        when (result) {
                            is Result.Loading -> {}
                            is Result.Error -> { throw result.throwable }
                            is Result.Success -> {}
                        }
                    }
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