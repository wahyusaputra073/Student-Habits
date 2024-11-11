package com.wahyusembiring.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wahyusembiring.common.util.launch
import com.wahyusembiring.data.Result
import com.wahyusembiring.data.model.ExamWithSubject
import com.wahyusembiring.data.model.HomeworkWithSubject
import com.wahyusembiring.data.model.entity.Reminder
import com.wahyusembiring.data.repository.EventRepository
import com.wahyusembiring.datetime.Moment
import com.wahyusembiring.datetime.formatter.FormattingStyle
import com.wahyusembiring.ui.component.eventcard.EventCard
import com.wahyusembiring.ui.component.scoredialog.ScoreDialog
import com.wahyusembiring.overview.util.inside
import com.wahyusembiring.overview.util.until
import com.wahyusembiring.ui.util.UIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.days

@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val eventRepository: EventRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(OverviewScreenUIState())
    val state: StateFlow<OverviewScreenUIState> = _state

    private val _navigationEvent = Channel<OverviewScreenNavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    private val _oneTimeEvent = Channel<OverviewScreenUIEvent>()
    val oneTimeEvent = _oneTimeEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            eventRepository.getAllEvent().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _state.update { it.copy(popUps = it.popUps + OverviewScreenPopUp.Loading) }
                    }
                    is Result.Error -> { throw result.throwable }
                    is Result.Success -> {
                        _state.update { it.copy(popUps = it.popUps - OverviewScreenPopUp.Loading) }
                        handleAllEventQuery(result.data)
                    }
                }
            }
        }
    }

    private suspend fun handleAllEventQuery(data: Flow<List<Any>>) {
        data.collect { events ->
            _state.update { state ->
                state.copy(
                    eventCards = List(6) {
                        val currentMoment = Moment.now() + it.days
                        EventCard(
                            title = when (it) {
                                0 -> UIText.StringResource(R.string.today)
                                1 -> UIText.StringResource(R.string.tomorrow)
                                else -> UIText.DynamicString(currentMoment.day.dayOfWeek)
                            },
                            date = when (it) {
                                0, 1 -> UIText.DynamicString(
                                    currentMoment.toString(
                                        FormattingStyle.INDO_FULL
                                    )
                                )

                                else -> UIText.DynamicString(
                                    currentMoment.toString(
                                        FormattingStyle.INDO_MEDIUM
                                    )
                                )
                            },
                            events = events inside (it.days until (it + 1).days)
                        )
                    }
                )
            }
        }
    }

    fun onUIEvent(event: OverviewScreenUIEvent) {
        when (event) {
            is OverviewScreenUIEvent.OnEventCompletedStateChange -> launch { ->
                onEventCompletedStateChange(event.event, event.isCompleted)
            }

            is OverviewScreenUIEvent.OnDeleteEvent -> launch {
                onDeleteEvent(event.event)
            }

            is OverviewScreenUIEvent.OnExamScorePicked -> launch {
                onExamScorePicked(event.exam, event.score)
            }

            is OverviewScreenUIEvent.OnMarkExamAsUndone -> launch {
                onMarkExamAsUndone(event.exam)
            }

            is OverviewScreenUIEvent.OnDismissPopUp -> onDismissPopUp(event.popUp)

            is OverviewScreenUIEvent.OnHamburgerMenuClick -> onHamburgerMenuClick()
            is OverviewScreenUIEvent.OnEventClick -> onEventClick(event.event)
            is OverviewScreenUIEvent.OnExamFABClick -> onExamFABClick()
            is OverviewScreenUIEvent.OnHomeworkFABClick -> onHomeworkFABClick()
            is OverviewScreenUIEvent.OnReminderFABClick -> onReminderFABClick()
        }
    }

    private fun onReminderFABClick() {
        _navigationEvent.trySend(OverviewScreenNavigationEvent.NavigateToReminder())
    }

    private fun onHomeworkFABClick() {
        _navigationEvent.trySend(OverviewScreenNavigationEvent.NavigateToHomework())
    }

    private fun onExamFABClick() {
        _navigationEvent.trySend(OverviewScreenNavigationEvent.NavigateToExam())
    }

    private fun onEventClick(event: Any) {
        when (event) {
            is HomeworkWithSubject -> {
                _navigationEvent.trySend(OverviewScreenNavigationEvent.NavigateToHomework(event.homework.id))
            }
            is ExamWithSubject -> {
                _navigationEvent.trySend(OverviewScreenNavigationEvent.NavigateToExam(event.exam.id))
            }
            is Reminder -> {
                _navigationEvent.trySend(OverviewScreenNavigationEvent.NavigateToReminder(event.id))
            }
        }
    }

    private fun onHamburgerMenuClick() {
        _oneTimeEvent.trySend(OverviewScreenUIEvent.OnHamburgerMenuClick)
    }

    private fun onDismissPopUp(popUp: OverviewScreenPopUp) {
        _state.update {
            it.copy(popUps = it.popUps - popUp)
        }
    }

    private suspend fun onMarkExamAsUndone(exam: ExamWithSubject) {
        eventRepository.updateExam(exam.exam.copy(score = null))
            .collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _state.update { it.copy(popUps = it.popUps + OverviewScreenPopUp.Loading) }
                    }
                    is Result.Error -> {
                        _state.update {
                            it.copy(
                                popUps = it.popUps
                                    .minus(OverviewScreenPopUp.Loading)
                                    .plus(
                                        OverviewScreenPopUp.Error(
                                            result.throwable.message ?: "Unknown error"
                                        )
                                    )
                            )
                        }
                    }
                    is Result.Success -> {
                        _state.update { it.copy(popUps = it.popUps - OverviewScreenPopUp.Loading) }
                    }
                }
            }
    }

    private suspend fun onEventCompletedStateChange(event: Any, completed: Boolean) {
        when (event) {
            is HomeworkWithSubject -> {
                eventRepository.updateHomework(event.homework.copy(completed = completed))
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> {
                                _state.update { it.copy(popUps = it.popUps + OverviewScreenPopUp.Loading) }
                            }
                            is Result.Error -> {
                                _state.update {
                                    it.copy(
                                        popUps = it.popUps
                                            .minus(OverviewScreenPopUp.Loading)
                                            .plus(OverviewScreenPopUp.Error(result.throwable.message ?: "Unknown error"))
                                    )
                                }
                            }
                            is Result.Success -> {
                                _state.update { it.copy(popUps = it.popUps - OverviewScreenPopUp.Loading) }
                            }
                        }
                    }
            }

            is ExamWithSubject -> {
                _state.update {
                    it.copy(popUps = it.popUps + OverviewScreenPopUp.ExamScoreInputDialog(event))
                }
            }

            is Reminder -> {
                eventRepository.updateReminder(event.copy(completed = completed))
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> {
                                _state.update { it.copy(popUps = it.popUps + OverviewScreenPopUp.Loading) }
                            }
                            is Result.Error -> {
                                _state.update {
                                    it.copy(
                                        popUps = it.popUps
                                            .minus(OverviewScreenPopUp.Loading)
                                            .plus(OverviewScreenPopUp.Error(result.throwable.message ?: "Unknown error"))
                                    )
                                }
                            }
                            is Result.Success -> {
                                _state.update { it.copy(popUps = it.popUps - OverviewScreenPopUp.Loading) }
                            }
                        }
                    }
            }
        }
    }

    private suspend fun onExamScorePicked(exam: ExamWithSubject, score: Int) {
        eventRepository.updateExam(exam.exam.copy(score = score))
            .collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _state.update { it.copy(popUps = it.popUps + OverviewScreenPopUp.Loading) }
                    }
                    is Result.Error -> {
                        _state.update {
                            it.copy(
                                popUps = it.popUps
                                    .minus(OverviewScreenPopUp.Loading)
                                    .plus(
                                        OverviewScreenPopUp.Error(
                                            result.throwable.message ?: "Unknown error"
                                        )
                                    )
                            )
                        }
                    }
                    is Result.Success -> {
                        _state.update { it.copy(popUps = it.popUps - OverviewScreenPopUp.Loading) }
                    }
                }
            }
    }

    private suspend fun onDeleteEvent(event: Any) {
        when (event) {
            is HomeworkWithSubject -> {
                eventRepository.deleteHomework(event.homework)
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> {
                                _state.update { it.copy(popUps = it.popUps + OverviewScreenPopUp.Loading) }
                            }
                            is Result.Error -> {
                                _state.update {
                                    it.copy(
                                        popUps = it.popUps
                                            .minus(OverviewScreenPopUp.Loading)
                                            .plus(
                                                OverviewScreenPopUp.Error(
                                                    result.throwable.message ?: "Unknown error"
                                                )
                                            )
                                    )
                                }
                            }
                            is Result.Success -> {
                                _state.update { it.copy(popUps = it.popUps - OverviewScreenPopUp.Loading) }
                            }
                        }
                    }
            }

            is ExamWithSubject -> {
                eventRepository.deleteExam(event.exam)
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> {
                                _state.update { it.copy(popUps = it.popUps + OverviewScreenPopUp.Loading) }
                            }
                            is Result.Error -> {
                                _state.update {
                                    it.copy(
                                        popUps = it.popUps
                                            .minus(OverviewScreenPopUp.Loading)
                                            .plus(
                                                OverviewScreenPopUp.Error(
                                                    result.throwable.message ?: "Unknown error"
                                                )
                                            )
                                    )
                                }
                            }
                            is Result.Success -> {
                                _state.update { it.copy(popUps = it.popUps - OverviewScreenPopUp.Loading) }
                            }
                        }
                    }
            }

            is Reminder -> {
                eventRepository.deleteReminder(event)
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> {
                                _state.update { it.copy(popUps = it.popUps + OverviewScreenPopUp.Loading) }
                            }
                            is Result.Error -> {
                                _state.update {
                                    it.copy(
                                        popUps = it.popUps
                                            .minus(OverviewScreenPopUp.Loading)
                                            .plus(
                                                OverviewScreenPopUp.Error(
                                                    result.throwable.message ?: "Unknown error"
                                                )
                                            )
                                    )
                                }
                            }
                            is Result.Success -> {
                                _state.update { it.copy(popUps = it.popUps - OverviewScreenPopUp.Loading) }
                            }
                        }
                    }
            }
        }
    }


}